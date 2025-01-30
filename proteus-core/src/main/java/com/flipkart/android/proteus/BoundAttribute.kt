package com.flipkart.android.proteus

import com.flipkart.android.proteus.value.Binding

/**
 * Kotlin data class representing a bound attribute in Proteus.
 *
 * A `BoundAttribute` associates an attribute ID with its corresponding data binding.
 * It is used to link XML attributes of a View with data binding expressions that
 * will dynamically provide values for these attributes.
 *
 * @property attributeId The integer ID of the attribute. This typically corresponds to
 *                       an attribute defined in the `Attributes.kt` file.
 * @property binding The [Binding] object responsible for providing the value for this attribute.
 *                   It contains information about the data path and function (if any)
 *                   used to resolve the attribute's value at runtime. Must not be null.
 */
data class BoundAttribute( // Converted to Kotlin data class for simplicity and conciseness
    /**
     * The integer attribute id of the pair.
     */
    val attributeId: Int, // Converted to val (immutable in Kotlin, like final in Java)

    /**
     * The [Binding] for the layout attribute's value.
     */
    // @NonNull annotation is retained for null safety, indicating non-nullable Binding
    val binding: Binding // Converted to val and Kotlin's non-nullable type due to @NonNull annotation
)