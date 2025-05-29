/**
 * Marks a class as a custom view that should be processed and registered
 * by the Voyager framework.
 *
 * The KSP `AttributeProcessor` finds classes annotated with `@ViewRegister` and generates
 * a corresponding `ViewAttributeParser`. This parser handles the registration of the custom view
 * with Voyager's runtime `ViewProcessor` and sets up attribute handling based on
 * any `@Attribute`-annotated members within the class.
 *
 * **Usage Example:**
 * ```kotlin
 * package com.example.myapp.views
 *
 * import com.voyager.annotations.ViewRegister
 * import com.voyager.annotations.Attribute
 * import android.content.Context
 * import android.util.AttributeSet
 * import androidx.appcompat.widget.AppCompatTextView
 *
 * // Register with a custom name for use in Voyager JSON/XML layouts
 * @ViewRegister(name = "MySpecialTextView")
 * class MyCustomTextView @JvmOverloads constructor(
 *     context: Context, attrs: AttributeSet? = null
 * ) : AppCompatTextView(context, attrs) {
 *
 *     @Attribute(xmlName = "customPrefix")
 *     var prefix: String = ""
 *         set(value) {
 *             field = value
 *             text = "$prefix$originalText" // Example usage
 *         }
 *
 *     private var originalText: String = ""
 *
 *     // Example of a method that could be an attribute target if needed
 *     // @Attribute(xmlName = "fullText")
 *     // fun setFullText(value: CharSequence?) {
 *     //     originalText = value?.toString() ?: ""
 *     //     text = "$prefix$originalText"
 *     // }
 * }
 *
 * // Register using its fully qualified class name (default if name is empty)
 * @ViewRegister
 * class AnotherCustomView(context: Context) : View(context) {
 *     // ...
 * }
 * ```
 *
 * @property name (Optional) The string identifier for this view type that will be used in Voyager's
 *               dynamic layout definitions (e.g., JSON or XML). This name is also used to
 *               register the view with Voyager's runtime `ViewProcessor`.
 *               If left empty (which is the default: `""`), the KSP `AttributeProcessor` will use the
 *               fully qualified class name of the annotated view as the registration name.
 *               Providing a shorter, simpler name (e.g., "MyCustomButton") is recommended for
 *               ease of use in layout definitions, especially if the class name is long or part of
 *               a complex package structure.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE) // Correct for KSP - only needed at compile time
annotation class ViewRegister(val name: String = "") // Parameter 'name', optional with default
