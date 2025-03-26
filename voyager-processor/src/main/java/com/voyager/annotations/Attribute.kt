package com.voyager.annotations

/**
 * Annotation for marking properties and functions as XML attributes in Android views.
 *
 * This annotation is used to specify the XML attribute name that corresponds to a property
 * or function in a view class. It enables automatic binding between XML attributes and
 * Kotlin code.
 *
 * Key features:
 * - Property and function attribute mapping
 * - XML attribute binding
 * - Runtime attribute resolution
 * - Type-safe attribute handling
 *
 * Usage examples:
 * ```kotlin
 * class CustomView @JvmOverloads constructor(
 *     context: Context,
 *     attrs: AttributeSet? = null,
 *     defStyleAttr: Int = 0
 * ) : View(context, attrs, defStyleAttr) {
 *
 *     @Attribute("android:text")
 *     var text: String? = null
 *         set(value) {
 *             field = value
 *             // Update view state
 *         }
 *
 *     @Attribute("android:background")
 *     fun setBackgroundDrawable(drawable: Drawable?) {
 *         // Handle background drawable
 *     }
 * }
 * ```
 *
 * Performance considerations:
 * - Annotation processing is done at compile time
 * - No runtime overhead for attribute resolution
 * - Efficient attribute binding
 * - Memory-efficient implementation
 *
 * @property attrName The XML attribute name that this property or function maps to
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Attribute(val attrName: String)