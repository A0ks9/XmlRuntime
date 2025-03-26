package com.voyager.annotations

/**
 * Annotation for registering custom views in the Voyager framework.
 *
 * This annotation is used to mark a class as a custom view that should be processed
 * by the Voyager framework. It enables automatic view registration and attribute binding.
 *
 * Key features:
 * - Automatic view registration
 * - Runtime view instantiation
 * - Attribute binding support
 * - Type-safe view handling
 *
 * Usage example:
 * ```kotlin
 * @ViewRegister("com.example.app.CustomView")
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
 * }
 * ```
 *
 * Performance considerations:
 * - Annotation processing is done at compile time
 * - Efficient view registration
 * - Optimized attribute binding
 * - Memory-efficient implementation
 *
 * @property className The fully qualified class name of the view to register
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewRegister(val className: String)