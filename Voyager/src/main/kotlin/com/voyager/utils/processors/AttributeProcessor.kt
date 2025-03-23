package com.voyager.utils.processors

import android.view.View
import com.voyager.utils.view.Attributes
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

@PublishedApi
internal object AttributeProcessor {
    val logger: Logger = Logger.getLogger(AttributeProcessor::class.java.name)

    // Using ConcurrentHashMap to handle concurrent access
    val attributeHandlers = ConcurrentHashMap<Int, AttributeHandler>()
    val attributeIds = ConcurrentHashMap<String, Int>()
    val nextId = AtomicInteger(0)
    private val bitmask = BitmaskManager()

    /**
     * Register an attribute handler with its corresponding view and value type.
     */
    inline fun <reified V : View, reified T> registerAttribute(
        name: String,
        noinline handler: (V, T) -> Unit,
    ) {
        if (!attributeIds.containsKey(name)) {
            val id = nextId.getAndIncrement()
            attributeIds[name] = id
            @Suppress("UNCHECKED_CAST")
            attributeHandlers[id] = AttributeHandler(
                V::class.java, T::class.java, handler as (View, Any?) -> Unit
            )
        } else {
            logger.warning("Attribute '$name' is already registered.")
        }
    }

    /**
     * Apply an attribute value to a view by its name.
     */
    internal fun applyAttribute(view: View, name: String, value: Any?) {
        val id = attributeIds[name] ?: throw IllegalArgumentException("Unknown attribute: $name")
        if (!bitmask.setIfNotSet(id)) {
            logger.warning("Duplicate attribute detected: $name")
            return
        }

        attributeHandlers[id]?.apply(view, value)
            ?: throw IllegalArgumentException("No handler found for attribute: $name")
    }

    /**
     * Apply multiple attributes to a view.
     */
    fun applyAttributes(view: View, attrs: Map<String, Any?>) {
        bitmask.clear()
        applyAttribute(view, Attributes.Common.ID.name, attrs[Attributes.Common.ID.name])

        attrs.filterNot { it.key.isConstraintLayoutAttribute() || it.key.isIDAttribute() }
            .forEach { (name, value) -> applyAttribute(view, name, value) }

        val constraintAttributes = attrs.filter { it.key.isConstraintLayoutAttribute() }

        val constraintOnlyAttributes = constraintAttributes.filter { it.key.isConstraint() }
        applyAttributesInternal(view, constraintOnlyAttributes)

        val biasAttributes = constraintAttributes.filter { it.key.isBias() }
        applyAttributesInternal(view, biasAttributes)
    }

    private fun applyAttributesInternal(view: View, attrs: Map<String, Any?>) {
        attrs.forEach { (name, value) ->
            if (name == "android" || name == "app") {
                applyAttribute(view, name, value)
            }
        }
    }

    // Class that handles the application of a single attribute to a view
    class AttributeHandler(
        private val viewClass: Class<*>,
        private val valueClass: Class<*>,
        private val handler: (View, Any?) -> Unit,
    ) {
        fun apply(view: View, value: Any?) {
            if (!viewClass.isInstance(view)) {
                throw IllegalArgumentException("Expected ${viewClass.simpleName}, got ${view::class.java.simpleName}")
            }
            if (value != null && !valueClass.isInstance(value)) {
                throw IllegalArgumentException("Expected ${valueClass.simpleName}, got ${value::class.java.simpleName}")
            }
            handler(view, value)
        }
    }

    // Bitmask manager for efficient attribute tracking
    private class BitmaskManager {
        @Volatile
        private var bitmaskArray = LongArray(4)

        private fun ensureCapacity(index: Int) {
            if (index shr 6 >= bitmaskArray.size) {
                bitmaskArray = bitmaskArray.copyOf(bitmaskArray.size * 2)
            }
        }

        fun setIfNotSet(index: Int): Boolean {
            ensureCapacity(index)
            val pos = index shr 6
            val bit = 1L shl (index and 0x3F)

            if ((bitmaskArray[pos] and bit) != 0L) return false
            bitmaskArray[pos] = bitmaskArray[pos] or bit
            return true
        }

        fun clear() {
            bitmaskArray.fill(0L)
        }
    }

    // Extension functions to handle specific attribute categories
    private fun String.isIDAttribute(): Boolean =
        equals(Attributes.Common.ID.name, ignoreCase = true)

    private fun String.isConstraintLayoutAttribute(): Boolean =
        startsWith("layout_constraint", ignoreCase = true)

    private fun String.isConstraint(): Boolean =
        isConstraintLayoutAttribute() && !contains("bias", ignoreCase = true)

    private fun String.isBias(): Boolean =
        isConstraintLayoutAttribute() && contains("bias", ignoreCase = true)
}
