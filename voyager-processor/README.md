# Voyager KSP Annotation Processor (`voyager-processor`)

## 1. Overview

The `voyager-processor` module is a Kotlin Symbol Processing (KSP) tool designed to work with the Voyager framework. Its primary purpose is to automate the creation of boilerplate code required for integrating your custom Android views and their attributes with Voyager's dynamic layout inflation system.

It processes custom annotations you add to your view classes:
-   `@com.voyager.annotations.ViewRegister`: Marks a custom view class to be recognized by Voyager.
-   `@com.voyager.annotations.Attribute`: Marks properties or functions within a `@ViewRegister`-annotated class to be settable from attributes defined in Voyager's JSON or XML layout definitions.

Based on these annotations, the processor generates a specific `[YourClassName]ViewAttributeParser.kt` file for each registered view. This generated parser handles:
-   Registering your custom view with Voyager's runtime `ViewProcessor`.
-   Providing the logic for Voyager's runtime `AttributeProcessor` to apply XML/JSON attributes to your custom view's properties or setter-like functions.

## 2. Why KSP?

Using KSP (Kotlin Symbol Processing) offers several advantages:
-   **Compile-time Code Generation:** All the boilerplate is generated during compilation, meaning there's no runtime reflection involved for discovering or invoking these view registrations and attribute setters.
-   **Performance:** Avoiding runtime reflection leads to faster view inflation and attribute application.
-   **Type Safety:** While attribute values from layouts are often strings, KSP helps in generating code that interacts with your typed Kotlin properties and functions. The generated code includes casts, and errors due to fundamentally incompatible types can often be caught earlier.
-   **Reduced Boilerplate:** You only need to declare annotations, and the processor handles the rest.

## 3. Setup and Integration

To use `voyager-processor` in your Android module (where your custom views are defined):

1.  **Apply the KSP Plugin:**
    Ensure the KSP plugin is applied in your module's `build.gradle.kts` (or `build.gradle`) file.
    ```kotlin
    // build.gradle.kts
    plugins {
        id("com.google.devtools.ksp") version "1.9.20-1.0.13" // Use the latest compatible KSP version
    }
    ```
    ```groovy
    // build.gradle
    plugins {
        id 'com.google.devtools.ksp' version '1.9.20-1.0.13' // Use the latest compatible KSP version
    }
    ```

2.  **Add Dependencies:**
    You'll need to add dependencies for the annotations and the processor itself. Typically, annotations are in a separate, lightweight library that your main code depends on (`implementation` or `api`), while the processor itself is added to the `ksp` configuration.

    ```kotlin
    // build.gradle.kts
    dependencies {
        // Assuming annotations are in 'voyager-annotations' (replace with actual artifact)
        implementation("com.voyager.libs:voyager-annotations:1.0.0") // Placeholder

        // Add the KSP processor
        ksp("com.voyager.libs:voyager-processor:1.0.0") // Placeholder
    }
    ```
    ```groovy
    // build.gradle
    dependencies {
        // Assuming annotations are in 'voyager-annotations' (replace with actual artifact)
        implementation 'com.voyager.libs:voyager-annotations:1.0.0' // Placeholder

        // Add the KSP processor
        ksp 'com.voyager.libs:voyager-processor:1.0.0' // Placeholder
    }
    ```
    **Note:** Replace artifact IDs and versions with the actual published coordinates for Voyager.

## 4. Annotations Explained

### 4.1. `@ViewRegister`

-   **Target:** `AnnotationTarget.CLASS`
-   **Retention:** `AnnotationRetention.SOURCE`
-   **Purpose:** Marks a class as a custom view that should be processed by Voyager and made available for dynamic inflation.
-   **Parameters:**
    -   `name: String = ""` (Optional):
        -   This string identifier is used to register the view with Voyager's runtime `ViewProcessor`. It's the "type" string you would use in your JSON or XML layout definitions to refer to this custom view.
        -   If left empty (the default), the KSP processor will use the **fully qualified class name** of the annotated view as its registration name.
        -   **Recommendation:** Provide a shorter, unique name (e.g., "MyCustomButton") for convenience in layout definitions, especially if your class names are long or reside in deep packages.

-   **Usage Example:**
    ```kotlin
    package com.example.myapp.views

    import android.content.Context
    import android.view.View
    import com.voyager.annotations.ViewRegister

    // Example 1: Using a custom registration name
    @ViewRegister(name = "SimpleWidget")
    class MySimpleWidget(context: Context) : View(context) {
        // ... view logic ...
    }

    // Example 2: Defaulting to the fully qualified class name
    // (will be registered as "com.example.myapp.views.AnotherCustomView")
    @ViewRegister
    class AnotherCustomView(context: Context) : View(context) {
        // ... view logic ...
    }
    ```

### 4.2. `@Attribute`

-   **Target:** `AnnotationTarget.PROPERTY`, `AnnotationTarget.PROPERTY_SETTER`, `AnnotationTarget.FUNCTION`
-   **Retention:** `AnnotationRetention.SOURCE`
-   **Purpose:** Marks a property or function within a `@ViewRegister`-annotated class as being settable from an attribute in a Voyager layout definition.
-   **Parameters:**
    -   `xmlName: String`:
        -   **Required.** This string specifies the name of the attribute as it will appear in your JSON or XML layout files (e.g., `"customTitle"`, `"app:iconTint"`, `"android:text"`).
        -   The KSP processor uses this name to create a mapping in the generated `ViewAttributeParser`.

-   **Usage Examples:**

    *   **On a mutable property (`var`):**
        ```kotlin
        package com.example.myapp.views

        import android.content.Context
        import android.view.View
        import com.voyager.annotations.ViewRegister
        import com.voyager.annotations.Attribute

        @ViewRegister(name = "UserProfileView")
        class UserProfileView(context: Context) : View(context) {
            @Attribute(xmlName = "userName")
            var displayedUserName: String? = null
                set(value) {
                    field = value
                    // Invalidate or update internal TextView, etc.
                }

            @Attribute(xmlName = "showAge")
            var isAgeVisible: Boolean = true
                // Custom setter logic can be here too
        }
        ```
        *Layout Usage:* `{"type": "UserProfileView", "attributes": {"userName": "Voyager User", "showAge": "true"}}`

    *   **On a function (setter method):**
        The function should ideally take a single parameter whose type corresponds to the attribute's value.
        ```kotlin
        package com.example.myapp.views

        import android.content.Context
        import android.graphics.Color
        import android.widget.TextView
        import com.voyager.annotations.ViewRegister
        import com.voyager.annotations.Attribute

        @ViewRegister(name = "TintableTextView")
        class TintableTextView(context: Context) : TextView(context) {

            @Attribute(xmlName = "myTintColor") // e.g., app:myTintColor="#FF0000"
            fun setCustomTintColor(colorString: String) {
                // Runtime AttributeProcessor would need to handle String to ColorInt conversion
                // Or, if the value from JSON/XML is already an Int, it could be `color: Int`
                try {
                    setTextColor(Color.parseColor(colorString))
                } catch (e: IllegalArgumentException) {
                    // Handle invalid color string
                }
            }

            @Attribute(xmlName = "android:textAllCaps") // Example for a framework attribute
            fun setAllCaps(allCaps: Boolean) {
                isAllCaps = allCaps
            }
        }
        ```
        *Layout Usage:* `{"type": "TintableTextView", "attributes": {"myTintColor": "#FF00FF", "android:textAllCaps": "true"}}`

## 5. Code Generation Details

For each class `YourViewClass` annotated with `@ViewRegister`, the processor generates a new Kotlin file named `YourViewClassViewAttributeParser.kt`.

*   **Location:** This file is placed in a `.generated` subpackage of your view's original package (e.g., if your view is in `com.example.views`, the parser will be in `com.example.views.generated`). This generated source directory is automatically added to your project's compile path.
*   **Structure of the Generated Parser:**
    ```kotlin
    package com.example.views.generated // Example

    import android.content.Context
    import android.view.View
    import androidx.appcompat.view.ContextThemeWrapper
    import com.example.views.YourViewClass // Your original view class
    import com.voyager.utils.processors.ViewAttributeParser // Base class from Voyager core
    import com.voyager.utils.processors.ViewProcessor     // From Voyager core

    internal class YourViewClassViewAttributeParser : ViewAttributeParser() {

        override fun getViewType(): String = "YourViewRegistrationName" // From @ViewRegister(name=...) or FQN

        private val attributeMap: Map<String, (View, Any?) -> Unit> = mapOf(
            "xmlAttributeName1" to { view, value -> (view as YourViewClass).propertyName = value as PropertyType },
            "xmlAttributeName2" to { view, value -> (view as YourViewClass).functionName(value as ParamType) }
            // ... other mappings ...
        )

        override fun addAttributes() {
            registerAttributes(attributeMap) // Method from base ViewAttributeParser
        }

        override fun createView(context: ContextThemeWrapper): View = YourViewClass(context)

        init {
            // Register this view type with Voyager's runtime ViewProcessor
            ViewProcessor.registerView("com.example.views", "YourViewClass") { themedContext ->
                createView(themedContext)
            }
            // Register its attributes with the runtime AttributeProcessor (via base class)
            addAttributes()
        }
    }
    ```
*   **Integration with Voyager Runtime:**
    *   The `init` block in the generated parser automatically registers the custom view with `ViewProcessor` using the specified (or defaulted) name.
    *   It also calls `addAttributes()`, which (through the base `ViewAttributeParser` class) registers the `attributeMap` with Voyager's core `AttributeProcessor`.
    *   When Voyager's `DynamicLayoutInflation` inflates a layout and encounters your custom view's type string, `ViewProcessor` can now instantiate it.
    *   Then, `AttributeProcessor` uses the registered handlers from `attributeMap` to apply the attributes from your JSON/XML layout definition to the view instance.

## 6. Full Example

**`com/example/myapp/ui/MyCustomCard.kt`:**
```kotlin
package com.example.myapp.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout // Or any base view
import android.widget.TextView
import com.voyager.annotations.Attribute
import com.voyager.annotations.ViewRegister

@ViewRegister(name = "MyCustomCard") // Registered as "MyCustomCard"
class MyCustomCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val titleView: TextView

    init {
        titleView = TextView(context).apply {
            textSize = 20f
            setTextColor(Color.BLACK)
        }
        addView(titleView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        // Default background
        setCardBackgroundColor("#CCCCCC")
    }

    @Attribute(xmlName = "cardTitle")
    var title: String? = null
        set(value) {
            field = value
            titleView.text = value
        }

    @Attribute(xmlName = "cardBackgroundColor")
    fun setCardBackgroundColor(colorString: String?) {
        try {
            val color = Color.parseColor(colorString ?: "#FFFFFF")
            val gd = GradientDrawable()
            gd.setColor(color)
            gd.cornerRadius = 16f
            background = gd
        } catch (e: IllegalArgumentException) {
            // Log error or set default color
        }
    }

    @Attribute(xmlName = "elevationDp")
    fun setCardElevation(elevationDp: Float) {
        elevation = elevationDp * resources.displayMetrics.density // Basic dp to px
    }
}
```

**Generated `com/example/myapp/ui/generated/MyCustomCardViewAttributeParser.kt` (Simplified):**
```kotlin
package com.example.myapp.ui.generated

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.example.myapp.ui.MyCustomCard
import com.voyager.utils.processors.ViewAttributeParser
import com.voyager.utils.processors.ViewProcessor
import java.lang.String // Example import, KotlinPoet handles actual imports

internal class MyCustomCardViewAttributeParser : ViewAttributeParser() {
    override fun getViewType(): String = "MyCustomCard"

    private val attributeMap: Map<String, (View, Any?) -> Unit> = mapOf(
        "cardTitle" to { view, value -> (view as MyCustomCard).title = value as String? },
        "cardBackgroundColor" to { view, value -> (view as MyCustomCard).setCardBackgroundColor(value as String?) },
        "elevationDp" to { view, value -> (view as MyCustomCard).setCardElevation(value as Float) }
    )

    override fun addAttributes() {
        registerAttributes(attributeMap)
    }

    override fun createView(context: ContextThemeWrapper): View = MyCustomCard(context)

    init {
        ViewProcessor.registerView("com.example.myapp.ui", "MyCustomCard") { themedContext ->
            createView(themedContext)
        }
        addAttributes()
    }
}
```

## 7. Important Considerations & Limitations

*   **Supported Members for `@Attribute`:**
    *   **Properties:** Must be mutable (`var`). The KSP processor will generate code to assign to them.
    *   **Functions:** Should ideally be setter-like, taking one argument. The KSP processor will generate code to call them with the attribute value.
    *   The KSP processor will log errors if these conditions are not met (e.g., annotating a `val` or a function with an incorrect number of parameters).

*   **Type Conversion at Runtime:**
    *   Voyager's layout definitions (JSON/XML) typically provide attribute values as strings.
    *   The KSP-generated parser creates lambdas that perform a direct cast (e.g., `value as String`, `value as Int`).
    *   **Crucial:** The core Voyager runtime `AttributeProcessor` is responsible for converting the input string value from the layout definition to the actual type expected by your property or function *before* this generated lambda is called.
    *   For common types like `Int`, `Float`, `Boolean`, `ColorInt` (from color strings), standard handlers are usually available in the runtime `AttributeProcessor`.
    *   If your `@Attribute`-annotated member expects a custom type, you may need to ensure a corresponding string-to-custom-type conversion logic is registered with the runtime `AttributeProcessor`.

*   **Inheritance:** The KSP processor currently uses `getAllFunctions()` and `getAllProperties()`, meaning it will find and process `@Attribute`-annotated members from superclasses as well. Ensure this is the desired behavior. If only directly declared members should be processed, the processor logic would need to change to `getDeclaredFunctions/Properties`.

*   **Error Reporting:** KSP provides errors and warnings at compile time. Check your build output for messages from `voyager-processor` if you encounter issues with annotation usage (e.g., incorrect targets, unresolvable types, non-mutable properties). The processor attempts to link errors to the specific source code elements.

By using `@ViewRegister` and `@Attribute`, you significantly reduce the manual boilerplate needed to make your custom views fully compatible with the Voyager framework's dynamic UI capabilities.
```
