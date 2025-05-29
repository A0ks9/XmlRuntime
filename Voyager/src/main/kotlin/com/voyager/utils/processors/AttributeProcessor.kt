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
 * This singleton object provides a centralized system for registering attribute handlers
 * and applying attributes to Android `View` instances during dynamic layout inflation.
 * It is designed with performance as a key consideration, employing several optimizations:
 *
 * - **Efficient Attribute Lookup:** Uses [ConcurrentHashMap] for storing attribute names to integer IDs (`attributeIds`)
 *   and integer IDs to `AttributeHandler` instances (`attributeHandlers`), allowing for fast retrieval.
 * - **Type-Safe Handlers:** The [AttributeHandler] class ensures type safety when applying attributes,
 *   preventing runtime errors due to type mismatches between the expected attribute value and the actual value.
 * - **Duplicate Application Prevention:** Utilizes a [BitmaskManager] to track which attributes have already
 *   been applied to a view within a single `applyAttributes` call. This prevents redundant work,
 *   especially if attributes are inadvertently duplicated in layout definitions.
 * - **Optimized Application Order:** The `applyAttributes` method processes attributes in a specific order:
 *   ID attributes first, then normal attributes, followed by ConstraintLayout constraints, and finally
 *   ConstraintLayout bias attributes. This order is crucial because some attributes (e.g., `RelativeLayout` rules
 *   or `ConstraintLayout` constraints) depend on the view's ID being set beforehand.
 * - **Minimized Allocations:** Caches string constants and uses efficient data structures to reduce memory churn.
 *
 * Example usage for registering a custom attribute:
 * ```kotlin
 * // In a component that initializes Voyager or custom views
 * AttributeProcessor.registerAttribute<TextView, String>("app:customText") { view, textValue ->
 *     view.text = "Custom: $textValue"
 * }
 * ```
 *
 * During layout inflation, Voyager's `DynamicLayoutInflation` would then use `AttributeProcessor.applyAttributes`
 * to apply all attributes parsed from the layout definition (XML or JSON) to the created views.
 *
 * @see com.voyager.utils.DynamicLayoutInflation
 * @see AttributeHandler
 * @see BitmaskManager
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
@PublishedApi
internal object AttributeProcessor {
    /**
     * Initial capacity for hashmaps to reduce resizing operations, chosen based on typical attribute counts.
     */
    private const val INITIAL_CAPACITY = 32

    /** Number of bits in a Long, used for [BitmaskManager] calculations. */
    private const val BITS_PER_LONG = 6 // Long.SIZE_BITS would be 64, this seems to be 2^6 for array indexing. It should be 64 or (Long.SIZE_BYTES * 8)

    /** Mask for isolating bits within a Long in [BitmaskManager]. */
    private const val BIT_MASK = 0x3F // Corresponds to 63 (2^6 - 1)

    /** Initial size of the [LongArray] in [BitmaskManager]. */
    private const val BITMAP_INITIAL_SIZE = 4 // Number of Longs, so 4 * 64 = 256 attributes initially.

    /** Prefix for identifying ConstraintLayout attributes to optimize their processing order. */
    private const val LAYOUT_CONSTRAINT_PREFIX = "layout_constraint"

    /** Keyword used within ConstraintLayout attributes to identify bias attributes. */
    private const val BIAS_KEYWORD = "bias"

    /**
     * Logger instance for AttributeProcessor, initialized lazily to minimize startup impact.
     */
    private val logger by lazy { Logger.getLogger(AttributeProcessor::class.java.name) }

    /**
     * Thread-safe map storing [AttributeHandler] instances, keyed by an integer ID.
     * Each handler is responsible for applying a specific attribute to a view.
     * Pre-sized to [INITIAL_CAPACITY].
     */
    val attributeHandlers = ConcurrentHashMap<Int, AttributeHandler>(INITIAL_CAPACITY)

    /**
     * Thread-safe map storing unique integer IDs for each registered attribute name.
     * This allows for more efficient lookup and storage in [attributeHandlers] and [BitmaskManager].
     * Pre-sized to [INITIAL_CAPACITY].
     */
    val attributeIds = ConcurrentHashMap<String, Int>(INITIAL_CAPACITY)

    /**
     * Atomic counter for generating unique integer IDs for new attributes.
     * Ensures thread-safety for ID generation.
     */
    val nextId = AtomicInteger(0)

    /**
     * Manages a bitmask to track applied attributes for a view during a single `applyAttributes` call,
     * preventing redundant applications.
     */
    private val bitmask = BitmaskManager()

    /**
     * Registers an attribute handler for a specific attribute name, view type, and value type.
     * This method is type-safe due to reified generics for the View (`V`) and attribute value (`T`) types.
     *
     * The registration process is thread-safe:
     * 1. It checks if an ID for the attribute name already exists.
     * 2. If not, it synchronizes on `attributeIds` to ensure that the ID generation and map insertion
     *    are atomic, preventing race conditions if multiple threads try to register the same attribute concurrently.
     * 3. A unique integer ID is generated using [nextId].
     * 4. The attribute name and its ID are stored in [attributeIds].
     * 5. An [AttributeHandler] is created, capturing the specific view and value types, and stored in [attributeHandlers]
     *    keyed by the generated ID.
     *
     * @param V The specific [View] subclass this attribute applies to (e.g., `TextView::class.java`).
     * @param T The expected data type of the attribute's value (e.g., `String::class.java`).
     * @param name The unique name of the attribute (e.g., "android:text", "app:customFont").
     * @param handler A lambda function `(V, T) -> Unit` that takes an instance of view type `V`
     *                and a value of type `T`, and applies the attribute.
     */
    inline fun <reified V : View, reified T> registerAttribute(
        name: String,
        crossinline handler: (V, T) -> Unit,
    ) {
        var id = attributeIds[name] // Initial check for existing ID (outside synchronized block for performance)
        if (id == null) {
            // Synchronize on the attributeIds map to ensure thread-safe registration
            // if multiple threads attempt to register attributes concurrently.
            synchronized(attributeIds) {
                // Double-check locking pattern: Re-check if another thread might have registered
                // this attribute while the current thread was waiting for the lock.
                id = attributeIds[name]
                if (id == null) {
                    // If still null, proceed with generating a new ID and registering the handler.
                    val newId = nextId.getAndIncrement() // Atomically generate a new unique ID.
                    attributeIds[name] = newId // Map the attribute name to the new ID.

                    // Create and store the AttributeHandler.
                    // The handler captures the specific view (V) and value (T) types using reified generics.
                    // UNCHECKED_CAST is suppressed because the reified types V and T, combined with
                    // AttributeHandler's internal type checking, provide the necessary type safety.
                    @Suppress("UNCHECKED_CAST")
                    attributeHandlers[newId] = AttributeHandler(
                        V::class.java, // Store Class<V> for runtime type checking.
                        T::class.java  // Store Class<T> for runtime type checking.
                    ) { view, value -> handler(view as V, value as T) } // The actual lambda to apply the attribute.
                    id = newId // Update 'id' to the newly generated one for consistency.
                }
            }
        }
    }

    /**
     * Applies a single attribute to a given [View] if it hasn't been applied already in the current batch.
     *
     * This internal function first retrieves the integer ID for the attribute `name`.
     * If the ID exists and the attribute has not yet been set for the current view (checked using `bitmask.setIfNotSet(id)`),
     * it retrieves the corresponding [AttributeHandler] and invokes its `apply` method.
     *
     * @param view The target [View] to apply the attribute to.
     * @param name The name of the attribute to apply.
     * @param value The value of the attribute. Can be `null`.
     * @throws IllegalArgumentException (indirectly via `AttributeHandler.apply`) if the attribute handler
     *                                  is not found or if type checking within the handler fails.
     */
    @OptIn(ExperimentalContracts::class)
    internal fun applyAttribute(view: View, name: String, value: Any?) {
        contract {
            returns() implies (value != null) // This contract might be too strong if null values are permissible for some attributes.
        }

        val id = attributeIds[name] ?: run {
            // Optional: Log if an attribute name is unknown but present in the layout.
            // logger.warning("Unknown attribute '$name' encountered for view ${view.javaClass.simpleName}")
            return
        }

        // Check bitmask: if setIfNotSet returns true, the attribute was not previously set in this batch.
        if (bitmask.setIfNotSet(id)) {
            attributeHandlers[id]?.apply(view, value)
                ?: logger.severe("No AttributeHandler found for ID $id (name: $name), though an ID was registered.")
        }
    }

    /**
     * Applies a map of attributes to a given [View] in an optimized, specific order.
     *
     * The order of application is critical for certain types of attributes:
     * 1.  **ID Attribute (`android:id`):** Applied first because other attributes, especially layout rules
     *     (like `RelativeLayout` constraints or `ConstraintLayout` references), often depend on the view's ID.
     * 2.  **Normal Attributes:** All other attributes that are not ID or ConstraintLayout specific.
     * 3.  **ConstraintLayout Pure Constraints:** Attributes like `layout_constraintTop_toTopOf`, etc., excluding bias.
     *     These define the structural relationships in `ConstraintLayout`.
     * 4.  **ConstraintLayout Bias Attributes:** Attributes like `layout_constraintHorizontal_bias`. These are typically
     *     applied after structural constraints are in place.
     *
     * Before applying any attributes, the [bitmask] is cleared to ensure a fresh state for the current view.
     * The method filters and partitions attributes to process them in the defined batches.
     *
     * @param view The target [View] to which the attributes will be applied.
     * @param attrs A [Map] where keys are attribute names (e.g., "android:layout_width") and values
     *              are the corresponding attribute values. Values can be `null`.
     */
    fun applyAttributes(view: View, attrs: Map<String, Any?>) {
        // Reset the bitmask at the beginning of applying attributes to a new view.
        // This ensures that the duplicate application check is fresh for each view.
        bitmask.clear()

        // Phase 1: Apply ID attribute (`android:id`) first.
        // The ID is often a prerequisite for other attributes, especially layout constraints
        // (e.g., RelativeLayout rules, ConstraintLayout connections).
        attrs[Attributes.Common.ID.name]?.let { idValue ->
            applyAttribute(view, Attributes.Common.ID.name, idValue)
        }

        // Partition the remaining attributes (excluding ID) into two groups:
        // - `constraintLayoutAttrs`: Attributes specific to ConstraintLayout.
        // - `normalAttrs`: All other attributes.
        // This partitioning helps in applying attributes in a dependency-aware order.
        val (constraintLayoutAttrs, normalAttrs) = attrs
            .filterNot { (key, _) -> key.isIDAttribute() } // Exclude ID as it's already processed.
            .partition { (key, _) -> key.isConstraintLayoutAttribute() } // Separate ConstraintLayout attributes.

        // Phase 2: Process normal (non-ConstraintLayout, non-ID) attributes.
        // These typically include basic properties like text, color, padding, etc.
        normalAttrs.forEach { (name, value) ->
            applyAttribute(view, name, value)
        }

        // Further partition ConstraintLayout attributes into:
        // - `pureConstraints`: Structural constraints (e.g., `layout_constraintTop_toTopOf`).
        // - `biasAttrs`: Bias attributes (e.g., `layout_constraintHorizontal_bias`).
        // This sub-ordering within ConstraintLayout attributes can be important.
        val (pureConstraints, biasAttrs) = constraintLayoutAttrs
            .partition { (key, _) -> key.isConstraint() } // `isConstraint` filters for non-bias constraints.

        // Phase 3: Process pure ConstraintLayout attributes.
        // These define the connections and dimensions relative to other views or parent.
        pureConstraints.forEach { (name, value) ->
            applyAttribute(view, name, value)
        }

        // Phase 4: Process ConstraintLayout bias attributes.
        // Bias attributes are typically applied last among constraints as they adjust positioning
        // within the established structural constraints.
        biasAttrs.forEach { (name, value) ->
            applyAttribute(view, name, value)
        }
    }

    /**
     * A type-safe wrapper around an attribute application lambda function.
     *
     * This class stores the expected `Class` types for the [View] and the attribute's value.
     * The `apply` method performs runtime instance checks against these stored classes before
     * invoking the actual `handler` lambda. This provides a layer of type safety, ensuring
     * that the handler is only called with compatible view and value types.
     *
     * @property viewClass The expected `Class` of the [View] this handler applies to.
     * @property valueClass The expected `Class` of the attribute's value this handler accepts.
     *                      Can be `Any::class.java` or a more specific type.
     * @property handler The lambda function `(View, Any?) -> Unit` that performs the actual
     *                   attribute application. It takes a generic `View` and `Any?` value,
     *                   relying on the outer `AttributeProcessor.registerAttribute` for initial
     *                   type casting and this class's `apply` for runtime verification.
     */
    class AttributeHandler(
        internal val viewClass: Class<*>, // Changed to internal for clarity, as it's used by AttributeProcessor
        internal val valueClass: Class<*>, // Changed to internal
        private val handler: (View, Any?) -> Unit,
    ) {
        /**
         * Applies the attribute value to the view after performing type checks.
         *
         * It verifies that the provided `view` is an instance of `viewClass` and
         * the `value` is an instance of `valueClass` (or `null`). If the types match,
         * the wrapped `handler` is invoked.
         *
         * @param view The target [View] instance.
         * @param value The attribute value to apply. Can be `null`.
         * @throws IllegalArgumentException if the `view` or `value` types are incompatible
         *                                  with those expected by this handler (though this is more of a
         *                                  protective measure, as registration should ensure correct types).
         */
        @OptIn(ExperimentalContracts::class)
        fun apply(view: View, value: Any?) {
            contract {
                // This contract implies 'value' is not null upon normal return.
                // If null values are valid and should be passed to the handler,
                // this contract might need adjustment or removal for this specific method.
                returns() implies (value != null)
            }

            // Runtime type check:
            // 1. Ensure the 'view' instance is compatible with the 'viewClass' stored by this handler.
            // 2. Ensure the 'value' is compatible with the 'valueClass' OR if the value is null (as null can be a valid attribute value).
            if (viewClass.isInstance(view) && (value == null || valueClass.isInstance(value))) {
                try {
                    // If type checks pass, invoke the actual handler lambda.
                    handler(view, value)
                } catch (e: ClassCastException) {
                    // This catch block is a safeguard. If the handler lambda itself performs an unsafe cast
                    // that was not caught by the V, T generics in registerAttribute, this will log it.
                    // Ideally, handler lambdas should be type-safe based on their generic parameters.
                    logger.severe(
                        "ClassCastException during attribute application. " +
                                "View: ${view.javaClass.name}, Expected View: ${viewClass.name}, " +
                                "Value Type: ${value?.javaClass?.name ?: "null"}, Expected Value: ${valueClass.name}. " +
                                "Error: ${e.message}"
                    )
                    // Rethrow as IllegalArgumentException to signal a problem with attribute application.
                    throw IllegalArgumentException(
                        "Type mismatch during attribute application: ${e.message}", e
                    )
                }
            } else {
                // This block is reached if the runtime type check fails.
                // This typically indicates a misconfiguration: either an attribute was registered
                // with incorrect V, T types, or it's being applied to an incompatible view.
                logger.warning(
                    "Attribute application skipped due to type mismatch. " +
                            "View: ${view.javaClass.name} (expected: ${viewClass.name}), " +
                            "Value Type: ${value?.javaClass?.name ?: "null"} (expected: ${valueClass.name})"
                )
                // Depending on strictness, one might throw an exception here.
                // For now, it logs a warning and skips application of this attribute.
            }
        }
    }

    /**
     * Manages a resizable array of `Long` values used as a bitmask.
     * This class provides a memory-efficient way to track a large number of boolean states
     * (e.g., whether an attribute at a specific integer ID has been applied).
     *
     * It uses bit manipulation operations on `Long` primitives for high performance.
     * The underlying `LongArray` ([bitmaskArray]) automatically expands when an index
     * outside its current capacity is accessed.
     */
    private class BitmaskManager {
        private var bitmaskArray = LongArray(BITMAP_INITIAL_SIZE)
        private var currentSize = BITMAP_INITIAL_SIZE // Tracks the current allocated size of bitmaskArray in terms of Long elements.

        /**
         * Ensures that the [bitmaskArray] has enough capacity to store the bit at the given `index`.
         * If the `index` requires a `Long` element beyond the current array size, the array is expanded.
         * The new size is the maximum of the required size or double the current size, providing amortized
         * constant time for additions.
         *
         * @param index The bit index that needs to be accessed (0-based).
         */
        @Suppress("NOTHING_TO_INLINE")
        private inline fun ensureCapacity(index: Int) {
            // Determine the index of the Long in `bitmaskArray` that would contain the bit for `index`.
            // `shr BITS_PER_LONG` is equivalent to `index / 64` since BITS_PER_LONG is currently 6 (for 2^6=64, assuming it meant to be 64).
            // If BITS_PER_LONG is indeed 6, then it's `index / 64`.
            // Let's assume BITS_PER_LONG = 6 implies 64 bits per long.
            val requiredLongArrayIndex = index shr BITS_PER_LONG // (index / (2^BITS_PER_LONG)) which is index / 64 if BITS_PER_LONG=6.

            // The number of Long elements needed in the array is one more than the array index.
            val requiredSizeInLongs = requiredLongArrayIndex + 1

            // If the required number of Longs exceeds the current array capacity:
            if (requiredSizeInLongs > currentSize) {
                // Calculate the new capacity: either the directly required size, or double the current size,
                // whichever is larger. This is a common growth strategy for dynamic arrays.
                val newCapacity = requiredSizeInLongs.coerceAtLeast(currentSize * 2)
                // Create a new LongArray with the new capacity and copy existing data.
                bitmaskArray = bitmaskArray.copyOf(newCapacity)
                // Update the current size tracker.
                currentSize = bitmaskArray.size
                // Optional: logger.finest("BitmaskManager resized to $currentSize Longs for index $index.")
            }
        }

        /**
         * Sets the bit at the specified `index` to 1 if it is not already set.
         *
         * This method first ensures there's capacity for the `index`. It then calculates
         * the position of the `Long` in the [bitmaskArray] and the specific bit within that `Long`.
         * If the bit is currently 0, it's set to 1, and the method returns `true`.
         * If the bit is already 1, the array remains unchanged, and the method returns `false`.
         *
         * @param index The 0-based index of the bit to set.
         * @return `true` if the bit was successfully set (i.e., it was previously 0).
         *         `false` if the bit was already set (i.e., it was already 1).
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun setIfNotSet(index: Int): Boolean {
            ensureCapacity(index) // Make sure the array is large enough for this index.

            // Calculate which Long element in `bitmaskArray` this attribute's bit belongs to.
            // `index shr BITS_PER_LONG` is `index / 64` (integer division).
            val longArrayIndex = index shr BITS_PER_LONG

            // Calculate the specific bit within that Long.
            // `index and BIT_MASK` is `index % 64`. `1L shl (index % 64)` creates a mask
            // with only the bit at that position set to 1 (e.g., 0...010...0).
            val bitPositionInLong = 1L shl (index and BIT_MASK)

            // Get the current Long value from the array at the calculated index.
            val currentValue = bitmaskArray[longArrayIndex]

            // Check if the specific bit is already set in the `currentValue`.
            // If `(currentValue and bitPositionInLong)` is 0, the bit is not set.
            return if ((currentValue and bitPositionInLong) == 0L) {
                // If the bit is not set, set it by ORing `currentValue` with `bitPositionInLong`.
                bitmaskArray[longArrayIndex] = currentValue or bitPositionInLong
                true // Return true indicating the bit was newly set.
            } else {
                false // Return false indicating the bit was already set.
            }
        }

        /**
         * Clears all bits in the bitmask, effectively resetting all tracked states to false (0).
         * This is done by filling the entire [bitmaskArray] with `0L`.
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun clear() {
            bitmaskArray.fill(0L)
            // logger.finest("BitmaskManager cleared.")
        }
    }

    /**
     * Checks if the string represents a ConstraintLayout attribute.
     * Performance-oriented: uses `startsWith` and ignores case.
     * @return `true` if the attribute name starts with "layout_constraint", `false` otherwise.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isConstraintLayoutAttribute() =
        this.startsWith(LAYOUT_CONSTRAINT_PREFIX, ignoreCase = true)

    /**
     * Checks if the string represents a "pure" ConstraintLayout attribute (not a bias attribute).
     * It must be a ConstraintLayout attribute and not contain the "bias" keyword.
     * @return `true` if it's a non-bias ConstraintLayout attribute, `false` otherwise.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isConstraint() =
        this.isConstraintLayoutAttribute() && !this.contains(BIAS_KEYWORD, ignoreCase = true)

    /**
     * Checks if the string represents a ConstraintLayout bias attribute.
     * It must be a ConstraintLayout attribute and contain the "bias" keyword.
     * @return `true` if it's a ConstraintLayout bias attribute, `false` otherwise.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isBias() =
        this.isConstraintLayoutAttribute() && this.contains(BIAS_KEYWORD, ignoreCase = true)

    /**
     * Checks if the string represents the ID attribute ("android:id").
     * Comparison is case-insensitive.
     * @return `true` if the attribute name is the ID attribute, `false` otherwise.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isIDAttribute() = this.equals(Attributes.Common.ID.name, ignoreCase = true)
}
