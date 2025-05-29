# Voyager Core Module

## Overview

Voyager is a powerful Android library designed to dynamically generate and manage UI hierarchies from various data sources, primarily JSON or XML definitions. It allows for flexible, data-driven UI creation, making it easier to build apps with dynamic content, A/B test different UI layouts, or render UIs received from a server.

The core module of Voyager provides the fundamental building blocks for:
- Parsing layout definitions ([ViewNode](./src/main/kotlin/com/voyager/data/models/ViewNode.kt)).
- Inflating these definitions into actual Android [View](https://developer.android.com/reference/android/view/View) hierarchies ([DynamicLayoutInflation](./src/main/kotlin/com/voyager/utils/DynamicLayoutInflation.kt)).
- Processing and applying view attributes efficiently ([AttributeProcessor](./src/main/kotlin/com/voyager/utils/processors/AttributeProcessor.kt)).
- Managing and creating view instances, including support for custom views ([ViewProcessor](./src/main/kotlin/com/voyager/utils/processors/ViewProcessor.kt)).
- Handling file and URI operations for layout loading ([FileHelper](./src/main/kotlin/com/voyager/utils/FileHelper.kt)).

## Integration

To integrate the Voyager core module into your Android project, add the following dependency to your app's `build.gradle.kts` or `build.gradle` file:

```gradle
// build.gradle.kts (Kotlin DSL)
implementation("com.example.voyager:voyager-core:1.0.0") // Replace with actual artifact and version

// build.gradle (Groovy DSL)
// implementation 'com.example.voyager:voyager-core:1.0.0' // Replace with actual artifact and version
```

**Initial Setup (Recommended):**

For optimal performance, especially if you use custom views or attributes, consider performing registrations during your application's startup phase (e.g., in your `Application` class):

```kotlin
import com.voyager.utils.processors.ViewProcessor
import com.voyager.utils.processors.AttributeProcessor
// ... other imports for your custom views/attribute handlers

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Pre-register custom views (see "Custom View Registration" section)
        // ViewProcessor.registerView("com.example.customviews", "MyCustomView") { context -> MyCustomView(context) }

        // Pre-register custom attributes (see "Custom Attributes" section)
        // AttributeProcessor.registerAttribute<MyCustomView, String>("customProperty") { view, value -> view.setCustomProperty(value) }
    }
}
```

## Key Features & Functionalities

### 1. Dynamic Layout Inflation

Voyager can inflate layouts from JSON or XML resource URIs. The layout is defined using a structure represented by `ViewNode` objects, which specify the view type, attributes, and any child views.

-   **Inflating from URI:**
    Use `DynamicLayoutInflation.inflate()`:
    ```kotlin
    val layoutUri: Uri = ... // Uri pointing to your JSON or XML layout file
    val parentView: ViewGroup = ... // The ViewGroup to inflate into
    val themeResId: Int = R.style.AppTheme // Your app's theme

    DynamicLayoutInflation.inflate(context, themeResId, layoutUri, parentView) { inflatedView ->
        if (inflatedView != null) {
            // Layout inflated successfully
        } else {
            // Inflation failed
        }
    }
    ```
    Voyager automatically detects if the URI points to a JSON or XML file.

-   **ViewNode Structure:**
    A `ViewNode` typically contains:
    *   `id`: An optional string identifier.
    *   `type`: The view type (e.g., "TextView", "LinearLayout", or a fully qualified custom view class name).
    *   `activityName`: Context identifier, used as a primary key for caching/persistence.
    *   `attributes`: An `ArrayMap<String, String>` of view attributes.
    *   `children`: A `List<ViewNode>` for nested views.

### 2. Attribute Processing

Attributes defined in the `ViewNode` are applied to the Android `View` instances by the `AttributeProcessor`. This processor handles standard Android attributes (which are mapped internally) and can be extended for custom attributes.

-   It uses an optimized application order, especially for `ConstraintLayout` attributes.
-   It prevents redundant attribute applications for efficiency.

### 3. Custom View Registration

Voyager supports custom views in your dynamic layouts. For best performance, pre-register your custom views.

-   **How to Register:**
    Use `ViewProcessor.registerView()`:
    ```kotlin
    // In your Application class or an initialization block
    ViewProcessor.registerView("com.example.myapp.ui", "MyCustomButton") { themedContext ->
        MyCustomButton(themedContext)
    }
    ```
    The first argument is the package name, the second is the simple class name. The lambda should return an instance of your custom view.

-   **Why Pre-register?**
    If a view type string encountered during inflation is not pre-registered, `ViewProcessor` falls back to using reflection to create an instance. Reflection is significantly slower. Pre-registration bypasses this, leading to faster layout inflation.

### 4. Event Handling (Delegates)

Voyager allows you to set a "delegate" object on an inflated view (or hierarchy). Click events (and potentially other events if custom attributes are defined) can then be routed to methods on this delegate object using string method names.

-   **Setting a Delegate:**
    ```kotlin
    val inflatedLayout: View = ... // Assume this is your inflated layout root
    val myEventHandler = MyActivityOrViewModel() // Must be an instance of a class with handler methods
    DynamicLayoutInflation.setDelegate(inflatedLayout, myEventHandler)
    ```

-   **Defining Click Handlers in Layout JSON:**
    ```json
    {
      "type": "Button",
      "attributes": {
        "id": "myButton",
        "text": "Click Me",
        "onClick": "handleMyButtonClick" // 'onClick' is a special attribute name
      }
    }
    ```
    When this button is clicked, Voyager will attempt to call `myEventHandler.handleMyButtonClick(view: View)` or `myEventHandler.handleMyButtonClick()` via reflection.

    You can also pass arguments:
    `"onClick": "onItemClicked('item1', 10)"`

-   **Performance Note:** This event handling mechanism uses reflection. For highly performance-sensitive interactions, consider alternative approaches or more direct event binding if possible after inflation.

### 5. View State Persistence (Basic)

The module includes components like `ViewNodeRepository` and `RoomViewNodeDataSource` which suggest capabilities for persisting and caching `ViewNode` definitions, potentially for faster subsequent inflations or offline support. `ViewHelper.kt` also contains logic for saving and restoring view state, though its full integration depends on how it's used within the broader Voyager ecosystem.

## Usage Examples

**1. Inflate a Simple JSON Layout:**

*   **`my_layout.json` (in `res/raw` or accessible via URI):**
    ```json
    {
      "type": "LinearLayout",
      "activityName": "MainActivity",
      "attributes": {
        "orientation": "vertical",
        "layout_width": "match_parent",
        "layout_height": "match_parent",
        "padding": "16dp"
      },
      "children": [
        {
          "type": "TextView",
          "attributes": {
            "text": "Hello, Voyager!",
            "layout_width": "wrap_content",
            "layout_height": "wrap_content"
          }
        },
        {
          "type": "Button",
          "attributes": {
            "id": "exampleButton",
            "text": "Click Me",
            "onClick": "onExampleButtonClicked",
            "layout_width": "wrap_content",
            "layout_height": "wrap_content"
          }
        }
      ]
    }
    ```

*   **In your Activity/Fragment:**
    ```kotlin
    class MyActivity : AppCompatActivity() {
        // ...
        fun loadLayout() {
            val layoutContainer: ViewGroup = findViewById(R.id.my_placeholder_container)
            val layoutUri = Uri.parse("android.resource://${packageName}/${R.raw.my_layout}")

            DynamicLayoutInflation.inflate(this, R.style.AppTheme, layoutUri, layoutContainer) { inflatedView ->
                if (inflatedView != null) {
                    // Optional: Set a delegate if you have onClick handlers
                    DynamicLayoutInflation.setDelegate(inflatedView, this)
                    // Add to view hierarchy if not already added by providing a parent to inflate call
                    // if (layoutContainer.childCount == 0) { // Example check
                    //     layoutContainer.addView(inflatedView)
                    // }
                }
            }
        }

        fun onExampleButtonClicked(view: View) {
            Toast.makeText(this, "Example Button Clicked!", Toast.LENGTH_SHORT).show()
        }
        // ...
    }
    ```

**2. Registering and Using a Custom View:**

*   **`MyCustomTextView.kt`:**
    ```kotlin
    package com.example.myapp.customviews

    import android.content.Context
    import android.util.AttributeSet
    import androidx.appcompat.widget.AppCompatTextView

    class MyCustomTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        fun setCustomText(text: String) {
            this.text = "Custom: $text"
        }
    }
    ```

*   **`MyApplication.kt`:**
    ```kotlin
    import com.voyager.utils.processors.ViewProcessor
    // ...
    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            ViewProcessor.registerView("com.example.myapp.customviews", "MyCustomTextView") { themedContext ->
                MyCustomTextView(themedContext)
            }
        }
    }
    ```

*   **Layout JSON:**
    ```json
    {
      "type": "com.example.myapp.customviews.MyCustomTextView", // Fully qualified name
      "activityName": "MainActivity",
      "attributes": {
        "customText": "Voyager Rocks!", // Assuming you registered a custom attribute handler for 'customText'
        "textSize": "20sp" // Standard attributes still work
      }
    }
    ```

## Customization & Extensibility

### Custom Attributes

You can define how Voyager handles custom attributes in your layout files.

1.  **Define an Attribute Handler Function:**
    This function takes the view instance and the attribute value.

2.  **Register the Handler with `AttributeProcessor`:**
    ```kotlin
    // In your Application class or an initialization block
    AttributeProcessor.registerAttribute<MyCustomTextView, String>("customText") { viewInstance, value ->
        viewInstance.setCustomText(value) // Call a method on your custom view
    }

    AttributeProcessor.registerAttribute<TextView, Boolean>("isErrorState") { textView, isError ->
        if (isError) {
            textView.setTextColor(Color.RED)
        } else {
            textView.setTextColor(Color.BLACK)
        }
    }
    ```
    - The generic types `<MyCustomTextView, String>` specify the expected View type and attribute value type.
    - `"customText"` is the attribute name as it will appear in your JSON/XML.

### Custom View Types

As shown previously, use `ViewProcessor.registerView()` to make Voyager aware of your custom view classes. This is crucial for performance and proper instantiation.

## Performance Considerations

-   **Pre-register Custom Views:** Always pre-register your custom view types using `ViewProcessor.registerView()` during application startup. This avoids the significant performance overhead of reflection during layout inflation.
-   **Reflection for Event Handling:** The `onClick` (and similar event) handling via string method names on a delegate uses reflection. While convenient, this can be slower than direct listener implementations. For performance-critical UI elements or frequently triggered events, consider setting listeners manually after inflation or using alternative event bus patterns.
-   **XML Parsing:** When using XML layouts, Voyager's `FileHelper.parseXML()` uses a native (JNI) parser to convert XML to an intermediate JSON string, which is then parsed into `ViewNode`s. While the native parsing is efficient, this is still an extra step compared to direct JSON parsing. For very large and complex layouts, direct JSON might offer a slight edge if this step becomes a bottleneck.
-   **Minimize Complex Logic in Attribute Handlers:** Keep custom attribute handlers efficient. Complex operations within a handler will slow down the inflation of every view that uses that attribute.

## Permissions

If you use Voyager's `FileHelper` (which is invoked by `DynamicLayoutInflation` when you provide file URIs) to load layouts from external storage, your application will need appropriate permissions:

-   **`android.Manifest.permission.READ_EXTERNAL_STORAGE`**: Required for reading layout files from shared external storage on Android versions prior to Scoped Storage (Android Q, API 29) or when accessing files outside your app's specific directories even with Scoped Storage (depending on the URI).
-   While Voyager itself doesn't request these permissions, it's the responsibility of the hosting application to ensure they are granted if such URIs are used.
-   For Android Q (API 29) and above, `FileHelper` often falls back to copying files to internal app storage if direct path access is restricted, which doesn't require these permissions for the app's own directories but does for the initial URI access if it's from shared storage.

Always follow best practices for runtime permission requests as per the Android documentation.
```
