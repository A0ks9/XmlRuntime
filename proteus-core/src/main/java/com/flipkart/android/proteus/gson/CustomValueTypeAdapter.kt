package com.flipkart.android.proteus.gson

import com.flipkart.android.proteus.value.Value
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter

/**
 * An abstract base class for creating custom type adapters for subclasses of {@link Value}.
 *
 * This class provides a foundation for implementing custom serialization or deserialization logic
 * for specific types of {@link Value} within a data processing or serialization framework.
 * It's designed to be extended by concrete adapter classes that handle particular subtypes of {@link Value}.
 *
 * **Key features:**
 *
 * *   **Abstract Base:**  Designed to be inherited from, not instantiated directly. Provides common structure for custom adapters.
 * *   **Generic Type Parameter `<V : Value>`:** Makes the adapter type-safe by restricting it to work with {@link Value}
 *     subclasses.  Subclasses of `CustomValueTypeAdapter` will specify a concrete subtype of {@link Value} for `V`.
 * *   **`type` Property:** Holds an integer `type` identifier. This is likely intended to be used to distinguish between
 *     different types of adapters or for type identification purposes within the framework where these adapters are used.
 * *   **Protected Constructor:** The constructor is `protected`, meaning it's accessible to subclasses within the same
 *     package or in different packages, but not directly from outside the class hierarchy. Subclasses are expected to call
 *     this constructor to initialize the `type` property.
 *
 * **Usage Scenario:**
 *
 * In a data serialization or processing framework (like Gson, Jackson, or a custom framework), you might need to handle
 * custom serialization/deserialization for specific data types that are represented by {@link Value} or its subtypes.
 * You would create concrete classes that extend `CustomValueTypeAdapter`, providing the actual implementation for
 * serialization/deserialization and associating them with a specific `type` identifier.
 *
 * **Example (Conceptual - demonstrating how a subclass might look):**
 *
 * ```kotlin
 * // Example subclass for adapting a specific Value subtype, e.g., MyCustomValue
 * class MyCustomValueAdapter : CustomValueTypeAdapter<MyCustomValue>(TYPE_MY_CUSTOM_VALUE) { // Define a type ID
 *
 *     companion object {
 *         const val TYPE_MY_CUSTOM_VALUE = 123 // Example type ID for MyCustomValue
 *     }
 *
 *     // Override methods from TypeAdapter<MyCustomValue> to handle serialization/deserialization
 *     override fun write(out: JsonWriter, value: MyCustomValue?) {
 *         // ... custom serialization logic for MyCustomValue ...
 *         out.value(value?.someProperty) // Example serialization
 *     }
 *
 *     override fun read(`in`: JsonReader): MyCustomValue? {
 *         // ... custom deserialization logic for MyCustomValue ...
 *         val propertyValue = `in`.nextString() // Example deserialization
 *         return MyCustomValue(propertyValue)   // Create MyCustomValue instance
 *     }
 * }
 *
 * // Registering the custom adapter in a framework (example concept)
 * // TypeAdapterFactory.registerAdapter(MyCustomValue::class.java, MyCustomValueAdapter())
 * ```
 *
 * @param <V> The specific subtype of {@link Value} that this adapter is designed to handle.
 * @property type An integer identifier representing the type this adapter handles.
 */
abstract class CustomValueTypeAdapter<V : Value>(
    /**
     * The type identifier for this adapter.
     *
     * This integer is used to distinguish this specific adapter from others,
     * allowing the framework to identify which adapter to use for a particular type.
     * It is initialized in the constructor of subclasses.
     */
    val type: Int // 'val' for final and immutable in Kotlin, default public visibility
) : TypeAdapter<V>() // Kotlin syntax for inheritance
{
    /**
     * Helper function to write a [Value] instance using this adapter.
     *
     * This function safely casts the given [Value] to the expected type [V] and
     * then calls the [write] method. It is useful when you have a [Value] that is not
     * statically known to be of type [V], but you are confident it is compatible.
     *
     * @param out The [JsonWriter] to write to.
     * @param value The [Value] to write. It must be of a type that is compatible with [V].
     * @throws ClassCastException if [value] is not of type [V].
     */
    fun writeValue(out: JsonWriter, value: Value) {
        @Suppress("UNCHECKED_CAST") write(out, value as V)
    }
}