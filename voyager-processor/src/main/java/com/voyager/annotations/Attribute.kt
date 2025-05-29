/**
 * Marks a property or function within a `@ViewRegister`-annotated class to be settable
 * via an XML attribute in Voyager's dynamic layout definitions.
 *
 * The KSP `AttributeProcessor` will find members annotated with `@Attribute` and generate
 * the necessary boilerplate to map an XML attribute name to the corresponding property assignment
 * or function call in the generated `ViewAttributeParser`.
 *
 * **Usage on Properties:**
 * When used on a `var` property, the KSP processor generates a lambda that assigns the
 * attribute's value to this property. The property must be mutable.
 * ```kotlin
 * package com.example.views // Assuming your views are here
 *
 * import com.voyager.annotations.Attribute
 * import com.voyager.annotations.ViewRegister
 * import android.content.Context
 * import android.view.View
 *
 * @ViewRegister(name = "MyCustomWidget")
 * class MyCustomWidget(context: Context) : View(context) {
 *     @Attribute(xmlName = "customText") // Will be settable via app:customText="value" or customText="value"
 *     var currentText: String? = null
 *         set(value) {
 *             field = value
 *             // Update view, e.g., invalidate()
 *         }
 * }
 * ```
 *
 * **Usage on Functions (Setters):**
 * When used on a function, the function should ideally take a single parameter representing
 * the attribute's value. The KSP processor generates a lambda that calls this function.
 * ```kotlin
 * package com.example.views // Assuming your views are here
 *
 * import com.voyager.annotations.Attribute
 * import com.voyager.annotations.ViewRegister
 * import android.content.Context
 * import android.widget.ImageView
 * import android.graphics.drawable.Drawable // Ensure Drawable is imported if used
 *
 * @ViewRegister(name = "MyImageView")
 * class MyImageView(context: Context) : ImageView(context) {
 *     @Attribute(xmlName = "imageUrl") // app:imageUrl="http://..."
 *     fun loadImage(url: String?) {
 *         // Load image using Glide, Picasso, etc.
 *     }
 *
 *     @Attribute(xmlName = "android:tint") // For framework attributes
 *     fun setTint(color: Int) {
 *         // setColorFilter(color, PorterDuff.Mode.SRC_IN) // Example implementation
 *     }
 * }
 * ```
 *
 * **Type Conversion Note:**
 * The value provided from Voyager's layout definition (JSON/XML) is typically a string.
 * The generated code will attempt a direct cast (e.g., `value as ExpectedType`).
 * It is the responsibility of the runtime `AttributeProcessor` (in the core Voyager library)
 * to handle necessary type conversions from string to the actual type required by the
 * property or function parameter (e.g., String to Int, String to ColorInt, String to Boolean).
 *
 * @property xmlName The name of the XML attribute as it would appear in a Voyager layout
 *                   definition (e.g., "customTitle", "app:layout_constraintTop_toTopOf", "android:text").
 *                   This name is used by the KSP processor to create a mapping.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.annotations

@Retention(AnnotationRetention.SOURCE) // KSP processes annotations at compile time
@Target(
    AnnotationTarget.PROPERTY,          // For direct property access (must be var)
    AnnotationTarget.PROPERTY_SETTER,   // For custom logic in property setters
    AnnotationTarget.FUNCTION           // For setter-like functions
)
annotation class Attribute(val xmlName: String)
