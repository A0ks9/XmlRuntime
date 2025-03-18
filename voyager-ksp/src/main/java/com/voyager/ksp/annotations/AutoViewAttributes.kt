package com.voyager.ksp.annotations

/**
 * Specifies that a class should automatically generate view attributes based on its properties.
 *
 * This annotation is used in conjunction with a code generator (e.g., an annotation processors)
 * that automatically creates a set of attributes for a given view class based on the properties
 * defined within the annotated class. This can be useful for simplifying the process of creating
 * custom view attributes in Android.
 *
 * @property viewClassName The fully qualified class name of the View for which attributes should be generated.
 *                         This should be a class that extends android.view.View or one of its subclasses
 *                         (e.g., android.widget.TextView, android.widget.ImageView).
 *
 * Example:
 * ```kotlin
 * @AutoViewAttributes("android.widget.TextView")
 * data class MyTextViewAttributes(
 *     val text: String,
 *     val textColor: Int,
 *     val textSize: Float
 * )
 * ```
 * In this example, the annotation processors would generate code that allows the "text", "textColor", and "textSize" attributes
 * to be set on a TextView in XML or programmatically, mapping them to the properties in `MyTextViewAttributes`.
 *
 * Notes:
 * - The code generator that handles this annotation is not provided within this class definition and must exist separately.
 * - The generated attributes will likely follow a naming convention based on the property names (e.g., camelCase to snake_case).
 * - Proper configuration of the annotation processors is crucial for this functionality to work correctly.
 * - The properties in the annotated class should be data types that can be represented as Android View Attributes (e.g. String, Int, Color, Dimension).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoViewAttributes(val viewClassName: String)