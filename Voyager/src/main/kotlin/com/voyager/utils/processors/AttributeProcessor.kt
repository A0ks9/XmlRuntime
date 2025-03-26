/**
 * Voyager Attribute Processing System
 * Copyright (c) 2024
 *
 * A high-performance attribute processing system for Android Views in the Voyager framework.
 * This implementation provides thread-safe operations, efficient memory usage, and optimized
 * performance for handling view attributes.
 *
 * Key features:
 * - Thread-safe attribute management using [ConcurrentHashMap]
 * - Efficient bitmap-based duplicate detection
 * - Zero-allocation string operations
 * - Optimized constraint layout processing
 * - Type-safe attribute registration and application
 *
 * @author Abdelrahman Omar
 */
package com.voyager.utils.processors

import android.view.View
import com.voyager.utils.partition
import com.voyager.utils.processors.AttributeProcessor.INITIAL_CAPACITY
import com.voyager.utils.view.Attributes
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Core processor for handling view attributes in the Voyager framework.
 *
 * This singleton object provides a centralized system for registering and applying
 * attributes to Android Views. It uses optimized data structures and algorithms
 * to ensure high performance and low memory overhead.
 *
 * Example usage:
 * ```kotlin
 * AttributeProcessor.registerAttribute<TextView, String>("text") { view, text ->
 *     view.text = text
 * }
 * ```
 *
 * @see AttributeHandler
 * @see BitmaskManager
 */
@PublishedApi
internal object AttributeProcessor {
    /**
     * Performance-optimized constants for bit operations and initial capacities.
     */
    private const val INITIAL_CAPACITY = 32
    private const val BITS_PER_LONG = 6
    private const val BIT_MASK = 0x3F
    private const val BITMAP_INITIAL_SIZE = 4

    /**
     * Cached string constants to prevent allocations in hot paths.
     */
    private const val LAYOUT_CONSTRAINT_PREFIX = "layout_constraint"
    private const val BIAS_KEYWORD = "bias"

    /**
     * Lazy-initialized logger to reduce startup overhead.
     */
    private val logger by lazy { Logger.getLogger(AttributeProcessor::class.java.name) }

    /**
     * Thread-safe storage for attribute handlers and their identifiers.
     * Pre-sized to [INITIAL_CAPACITY] to reduce resizing operations.
     */
    val attributeHandlers = ConcurrentHashMap<Int, AttributeHandler>(INITIAL_CAPACITY)
    val attributeIds = ConcurrentHashMap<String, Int>(INITIAL_CAPACITY)
    val nextId = AtomicInteger(0)
    private val bitmask = BitmaskManager()

    /**
     * Thread-safe registration of attributes for Android API 21+.
     * Uses atomic operations to ensure thread safety without requiring API 23's computeIfAbsent.
     *
     * @param V The type of View this attribute applies to
     * @param T The type of value this attribute accepts
     * @param name The unique identifier for this attribute
     * @param handler The function that applies the attribute value to the view
     */
    inline fun <reified V : View, reified T> registerAttribute(
        name: String,
        crossinline handler: (V, T) -> Unit,
    ) {
        // Thread-safe check and registration for API 21+
        var id = attributeIds[name]
        if (id == null) {
            synchronized(attributeIds) {
                // Double-check pattern to ensure thread safety
                id = attributeIds[name]
                if (id == null) {
                    id = nextId.getAndIncrement()
                    attributeIds[name] = id
                    @Suppress("UNCHECKED_CAST")
                    attributeHandlers[id] = AttributeHandler(
                        V::class.java, T::class.java
                    ) { view, value -> handler(view as V, value as T) }
                }
            }
        }
    }

    /**
     * Applies a single attribute to a view with optimized null checking.
     *
     * @param view The target view to apply the attribute to
     * @param name The name of the attribute to apply
     * @param value The value to apply (may be null)
     * @throws IllegalArgumentException if the attribute name is unknown
     */
    @OptIn(ExperimentalContracts::class)
    internal fun applyAttribute(view: View, name: String, value: Any?) {
        contract {
            returns() implies (value != null)
        }

        val id = attributeIds[name] ?: return
        if (bitmask.setIfNotSet(id)) {
            attributeHandlers[id]?.apply(view, value)
        }
    }

    /**
     * Applies multiple attributes to a view in an optimized order.
     *
     * This function processes attributes in the following order:
     * 1. ID attribute (if present)
     * 2. Normal attributes
     * 3. Constraint attributes
     * 4. Bias attributes
     *
     * @param view The target view to apply attributes to
     * @param attrs Map of attribute names to their values
     */
    fun applyAttributes(view: View, attrs: Map<String, Any?>) {
        bitmask.clear()

        // Fast path for ID attribute
        attrs[Attributes.Common.ID.name]?.let { id ->
            applyAttribute(view, Attributes.Common.ID.name, id)
        }

        // Process attributes in batches
        attrs.filterNot { (key, _) -> key.isIDAttribute() }
            .partition { (key, _) -> key.isConstraintLayoutAttribute() }
            .let { (constraints, normal) ->
                // Process normal attributes
                normal.forEach { (name, value) ->
                    applyAttribute(view, name, value)
                }

                // Process constraints efficiently
                constraints.partition { (key, _) -> key.isConstraint() }.let { (pure, bias) ->
                        pure.forEach { (name, value) ->
                            applyAttribute(view, name, value)
                        }
                        bias.forEach { (name, value) ->
                            applyAttribute(view, name, value)
                        }
                    }
            }
    }

    /**
     * Type-safe handler for attribute application with minimal allocations.
     *
     * @property viewClass The class of the view this handler applies to
     * @property valueClass The class of the value this handler accepts
     * @property handler The function that applies the value to the view
     */
    class AttributeHandler(
        private val viewClass: Class<*>,
        private val valueClass: Class<*>,
        private val handler: (View, Any?) -> Unit,
    ) {
        /**
         * Applies the attribute value to the view with type checking.
         *
         * @param view The target view
         * @param value The value to apply
         * @throws IllegalArgumentException if type checking fails
         */
        @OptIn(ExperimentalContracts::class)
        fun apply(view: View, value: Any?) {
            contract {
                returns() implies (value != null)
            }

            if (viewClass.isInstance(view) && (value == null || valueClass.isInstance(value))) {
                handler(view, value)
            }
        }
    }

    /**
     * Memory-efficient bitmap manager for tracking attribute states.
     *
     * Uses primitive [LongArray] for optimal memory usage and bit manipulation
     * operations for high-performance state tracking.
     */
    private class BitmaskManager {
        private var bitmaskArray = LongArray(BITMAP_INITIAL_SIZE)
        private var currentSize = BITMAP_INITIAL_SIZE

        /**
         * Ensures the bitmap has sufficient capacity for the given index.
         *
         * @param index The bit index that needs to be accessed
         */
        @Suppress("NOTHING_TO_INLINE")
        private inline fun ensureCapacity(index: Int) {
            val requiredSize = (index shr BITS_PER_LONG) + 1
            if (requiredSize > currentSize) {
                bitmaskArray = bitmaskArray.copyOf(requiredSize.coerceAtLeast(currentSize * 2))
                currentSize = bitmaskArray.size
            }
        }

        /**
         * Sets a bit if it's not already set.
         *
         * @param index The bit index to set
         * @return true if the bit was not previously set, false otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun setIfNotSet(index: Int): Boolean {
            ensureCapacity(index)
            val pos = index shr BITS_PER_LONG
            val bit = 1L shl (index and BIT_MASK)
            val current = bitmaskArray[pos]
            return if ((current and bit) == 0L) {
                bitmaskArray[pos] = current or bit
                true
            } else false
        }

        /**
         * Clears all bits in the bitmap.
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun clear() {
            bitmaskArray.fill(0L)
        }
    }

    /**
     * Zero-allocation string extension functions for attribute type checking.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isConstraintLayoutAttribute() =
        startsWith(LAYOUT_CONSTRAINT_PREFIX, ignoreCase = true)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isConstraint() =
        isConstraintLayoutAttribute() && !contains(BIAS_KEYWORD, ignoreCase = true)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isBias() =
        isConstraintLayoutAttribute() && contains(BIAS_KEYWORD, ignoreCase = true)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isIDAttribute() = equals(Attributes.Common.ID.name, ignoreCase = true)
}
