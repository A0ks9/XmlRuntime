# Voyager - Dynamic XML Runtime Layout Engine

[![Android CI Build](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml/badge.svg)](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.voyager/xml-runtime)](https://search.maven.org/artifact/com.voyager/xml-runtime)

Voyager is a high-performance, dynamic XML runtime layout engine for Android that enables you to load and render XML layouts at runtime without requiring app recompilation. It provides a flexible and efficient way to create dynamic user interfaces.

# ✨ Actively Maintained ✨

This project was originally archived but is now under active maintenance and development. We are working to improve its features, performance, and stability. Contributions are welcome!

## 🌟 Features

- **Dynamic XML Loading**: Load and render XML layouts at runtime
- **High Performance**: Optimized parsing and rendering engine
- **Type Safety**: Full Kotlin support with type-safe attribute handling
- **Custom View Support**: Easy integration of custom views
- **Resource Management**: Efficient resource handling and caching
- **Incremental Processing**: Smart incremental build support
- **Annotation Processing**: KSP-based annotation processing
- **Memory Efficient**: Optimized memory usage and garbage collection
- **Thread Safe**: Safe concurrent operations
- **Comprehensive Testing**: Extensive test coverage

## 🛠️ For Developers & Contributors

This section provides a deeper dive into Voyager's internal architecture and guidelines for contributing or building upon the library.

### Core Architecture

Voyager's power comes from a few key components working together:

*   **`com.voyager.utils.DynamicLayoutInflation`**: This is the main engine for layout inflation. It takes a `Uri` pointing to an XML or JSON layout definition, parses it, and orchestrates the creation of the Android `View` hierarchy. It uses a `ViewNode` tree as an intermediate representation.
*   **`com.voyager.data.models.ViewNode`**: A data class that represents a single view element within the layout hierarchy. It holds the view's type (e.g., "Button"), its attributes (like "width", "text"), and its children `ViewNode`s. `ViewNode`s can be cached using Room persistence for faster layout loading in subsequent sessions.
*   **`com.voyager.utils.processors.ViewProcessor`**: Responsible for creating actual Android `View` instances (e.g., `TextView`, `Button`). It maintains a registry of known view types (mapping string identifiers like "Button" to their constructors). If a view type isn't pre-registered, it can attempt to create it using reflection (though pre-registering custom views is recommended for performance).
*   **`com.voyager.utils.processors.AttributeProcessor`**: This component applies the attributes defined in the `ViewNode` to the created `View` instances. It's optimized for performance, using techniques like type-safe handlers, efficient ordering of attribute application (e.g., ID, layout params, then other attributes), and bitmasks to avoid redundant work.
*   **`voyager-plugin` & `voyager-processor`**: 
    *   `voyager-plugin` (Gradle plugin ID: `com.voyager.plugin`): This plugin is applied in `app/build.gradle.kts`. Its specific role (e.g., build-time code generation, resource processing, or integration tasks for Voyager) requires further investigation for detailed documentation.
    *   `voyager-processor` (KSP Processor): This is a Kotlin Symbol Processing (KSP) annotation processor, declared as a dependency. It processes annotations like `@ViewRegister` and `@Attribute` at compile time. The generated code is then likely used by `ViewProcessor` and `AttributeProcessor` to enable type-safe and efficient registration and handling of custom views and attributes.

### Building from Source

1.  Clone the repository from GitHub.
2.  Import the project into Android Studio (latest stable version recommended).
3.  The project uses Gradle as its build system. Build the project using the standard Android Studio build commands (e.g., "Build > Make Project" or running `./gradlew build` from the terminal in the project root).
4.  The `app` module serves as a sample application demonstrating Voyager's usage. Explore it to see the library in action.

### Initialization Explained

Voyager's initialization is managed through a combination of dependency injection and component-specific setups:

1.  **Dependency Injection (Koin):** Core services and configurations are managed by Koin. You'll need to initialize Koin in your `Application` class, providing Voyager's `appModule` from `com.voyager.di.appModule`. This module sets up data sources, repositories, and the `VoyagerConfig`.
    ```kotlin
    // In your Application class:
    // import android.app.Application
    // import com.voyager.di.appModule // Main Koin module for Voyager
    // import com.voyager.utils.interfaces.ResourcesProvider // Interface for resource access
    // import com.example.MyResourcesProvider // Your concrete implementation of ResourcesProvider
    // import org.koin.android.ext.koin.androidContext
    // import org.koin.core.context.startKoin // Or GlobalContext.startKoin

    // class MyApplication : Application() {
    //     override fun onCreate() {
    //         super.onCreate()
    //         startKoin {
    //             androidContext(this@MyApplication)
    //             // MyResourcesProvider should implement com.voyager.utils.interfaces.ResourcesProvider
    //             // isLoggingEnabled can be linked to your app's BuildConfig.DEBUG or a similar flag
    //             modules(appModule(MyResourcesProvider(), isLoggingEnabled = true))
    //         }
    //     }
    // }
    ```
    *Your `ResourcesProvider` implementation is crucial for Voyager to dynamically access application resources like drawables, strings, dimensions, and styles by name.*
2.  **ViewHandler (Optional but often necessary):** The `com.voyager.utils.interfaces.ViewHandler` interface provides callbacks and a connection point between the dynamically inflated views and your Activity/Fragment (e.g., for event handling or ViewModel interaction). The sample app's `MainActivity` implements this.
3.  **Internal Initializers:** Key components like `com.voyager.utils.view.BaseViewAttributes` (for default attributes) and `com.voyager.utils.processors.ViewProcessor` (for default view types) have their own internal static `init` blocks or companion object initializers. These are typically invoked automatically when these classes are first loaded by the JVM.

### Registering Custom Views and Attributes

Voyager is designed to be extensible. You can add support for your custom Android Views and define how custom attributes are handled. This is typically achieved using annotations processed by `voyager-processor`.
*(Note: The exact annotation class paths like `com.voyager.annotations.ViewRegister` are based on common patterns. Verify these against the `voyager-processor` module's actual annotation definitions if possible.)*

*   **Custom Views (`@com.voyager.annotations.ViewRegister`):**
    To make your custom view available for dynamic inflation, annotate its class with `@ViewRegister("YourCustomViewTag")`. The tag is the string you'll use in your XML/JSON layout files to refer to this custom view.
    ```kotlin
    // import com.voyager.annotations.ViewRegister // Replace with actual package if different
    // import com.voyager.annotations.Attribute    // Replace with actual package if different
    // import android.content.Context
    // import android.util.AttributeSet
    // import com.google.android.material.textview.MaterialTextView // Or any other base view

    // @ViewRegister("MySpecialTextView") // This tag is used in your dynamic layouts
    // class MySpecialTextView @JvmOverloads constructor(
    //     context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    // ) : MaterialTextView(context, attrs, defStyleAttr) {
    //
    //     // Example of a custom attribute for this view
    //     @Attribute("app:specialTitle") // Use your app's namespace or a custom one
    //     var specialTitle: String? = null
    //         set(value) {
    //             field = value
    //             // Update the view's appearance or behavior based on this attribute
    //             text = "Title: $value | Original: $text" 
    //         }
    // }
    ```
*   **Custom Attributes (`@com.voyager.annotations.Attribute`):**
    Properties within your custom view (or even if you're creating a custom handler for attributes on existing views) can be annotated with `@Attribute("namespace:attributeName")`. The `voyager-processor` uses this to generate code that `AttributeProcessor` can use to apply the attribute value from the layout file to this property.

The `voyager-processor` (KSP) handles these annotations at compile time, generating the necessary mappings. Ensure that KSP is correctly configured in your build and that the `voyager-processor` dependency is added.

## 🚀 Getting Started

### Installation

Add the following to your project's `build.gradle.kts`:

```kotlin
plugins {
    id("com.voyager.plugin")
}

dependencies {
    implementation("com.github.A0ks9:Voyager:1.0.0-Beta01")
    ksp("com.github.A0ks9:voyager-processor:1.0.0-Beta01")
}
```

### Basic Usage

```kotlin
// 1. Initialize Koin in your Application class (example)
// import com.voyager.di.appModule // Assuming appModule is the Koin module
// import org.koin.android.ext.koin.androidContext
// import org.koin.core.context.startKoin // Or GlobalContext.startKoin depending on Koin version
// import com.example.MyResourcesProvider // Replace with your actual ResourcesProvider
// import android.app.Application
// import com.voyager.utils.interfaces.ResourcesProvider // Path to ResourcesProvider

// class MyApplication : Application() {
//     override fun onCreate() {
//         super.onCreate()
//         startKoin {
//             androidContext(this@MyApplication)
//             // Provide your implementation of ResourcesProvider
//             // isLoggingEnabled can be tied to your BuildConfig.DEBUG or similar
//             modules(appModule(MyResourcesProvider(), isLoggingEnabled = true))
//         }
//     }
// }

// 2. In your Activity or Fragment:
// (Assuming you have a layoutUri: Uri, a parent: ViewGroup, and a themeResId: Int)
// import android.net.Uri
// import android.os.Bundle
// import android.util.Log
// import android.view.ViewGroup
// import androidx.appcompat.app.AppCompatActivity // Or your base Activity
// import com.voyager.utils.DynamicLayoutInflation
// import com.example.R // Your app's R class (ensure R.raw.your_layout_file and R.id.your_container_viewgroup exist)

// class YourActivity : AppCompatActivity() {
//   override fun onCreate(savedInstanceState: Bundle?) {
//     super.onCreate(savedInstanceState)
//     // ... your existing setup, ensure setContentView is called with a layout that contains your_container_viewgroup
//     // setContentView(R.layout.activity_your) 

//     // Example: loading a layout from res/raw
//     val layoutUri: Uri = Uri.parse("android.resource://${packageName}/${R.raw.your_layout_file}") 
//     val parentView: ViewGroup = findViewById(R.id.your_container_viewgroup) 
//     // It's good practice to define a specific theme for Voyager-inflated views
//     // if they need to be styled differently or to ensure consistency.
//     val themeResId: Int = R.style.YourAppTheme_VoyagerCompatible // An example theme

//     DynamicLayoutInflation.inflate(this, themeResId, layoutUri, parentView) { inflatedView ->
//         if (inflatedView != null) {
//             // Successfully inflated the view.
//             // DynamicLayoutInflation adds the view to parentView if parentView is not null.
//             Log.d("VoyagerDemo", "View inflated: ${inflatedView::class.java.simpleName}")
//         } else {
//             // Handle inflation error
//             Log.e("VoyagerDemo", "Failed to inflate layout from $layoutUri")
//         }
//     }
//   }
// }
```
Mention that the `layoutUri` can point to an XML or JSON file.

## 🛠️ Supported Views and Attributes

### Optimized Views

The library supports a wide range of Android views with optimized performance:

#### Basic Views
- `View`
- `Space`

#### Text Views
- `TextView` (AppCompat)
- `EditText` (AppCompat)

#### Button Views
- `Button` (Material)
- `ImageButton` (AppCompat)
- `MaterialButton`

#### Layout Views
- `LinearLayout`
- `FrameLayout`
- `RelativeLayout`
- `TableLayout`
- `TableRow`
- `GridLayout`
- `ConstraintLayout`
- `CoordinatorLayout`

#### List Views
- `RecyclerView`
- `ListView`
- `GridView`
- `ExpandableListView`

#### Scroll Views
- `ScrollView`
- `HorizontalScrollView`
- `NestedScrollView`

#### Image Views
- `ImageView` (AppCompat)
- `ShapeableImageView`

#### Media Views
- `VideoView`
- `SurfaceView`
- `TextureView`

#### Card Views
- `CardView`
- `MaterialCardView`

#### Progress Views
- `ProgressBar`
- `CircularProgressIndicator`
- `LinearProgressIndicator`

#### Input Views
- `Switch` (AppCompat)
- `CheckBox` (AppCompat)
- `RadioButton` (AppCompat)
- `SwitchMaterial`
- `MaterialCheckBox`

### Supported Attributes

#### Common Attributes

##### Layout Parameters
- `width`, `height`
- `layout_width`, `layout_height`
- `min_width`, `min_height`
- `max_width`, `max_height`
- `margin`, `padding`
- `gravity`, `layout_gravity`
- `clip_to_padding`

##### Visual Attributes
- `background`, `foreground`
- `backgroundTint`, `foregroundTint`
- `backgroundTintMode`, `foregroundTintMode`
- `elevation`, `alpha`
- `rotation`, `rotationX`, `rotationY`
- `scaleX`, `scaleY`
- `translationX`, `translationY`, `translationZ`
- `drawing_cache_quality`

##### State Attributes
- `visibility`
- `clickable`, `longClickable`
- `enabled`
- `focusable`, `focusableInTouchMode`
- `scrollbars`, `overScrollMode`
- `scrollIndicators`
- `tag`
- `tooltip_text`

##### Accessibility
- `contentDescription`
- `importantForAccessibility`
- `screenReaderFocusable`

##### Drawable Attributes
- `drawableStart`, `drawableEnd`
- `drawableTop`, `drawableBottom`
- `drawablePadding`
- `tint`, `tintMode`
- `drawableTint`, `drawableTintMode`

#### Layout-Specific Attributes

##### LinearLayout
- `orientation`
- `weight`

##### RelativeLayout
- `layout_above`
- `layout_below`
- `layout_toLeftOf`
- `layout_toRightOf`
- `layout_alignTop`
- `layout_alignBottom`
- `layout_alignParentTop`
- `layout_alignParentBottom`
- `layout_alignStart`
- `layout_alignEnd`
- `layout_alignParentStart`
- `layout_alignParentEnd`

##### ConstraintLayout
- `layout_constraintTop_toTopOf`
- `layout_constraintTop_toBottomOf`
- `layout_constraintBottom_toTopOf`
- `layout_constraintBottom_toBottomOf`
- `layout_constraintStart_toStartOf`
- `layout_constraintStart_toEndOf`
- `layout_constraintEnd_toStartOf`
- `layout_constraintEnd_toEndOf`
- `layout_constraintHorizontal_bias`
- `layout_constraintVertical_bias`
- `layout_constraintDimensionRatio`
- `layout_constraintHorizontal_chainStyle`
- `layout_constraintVertical_chainStyle`

#### View-Specific Attributes

##### TextView
- `text`, `textSize`, `textColor`
- `textStyle`, `textAlignment`
- `ellipsize`, `singleLine`
- `hint`, `textColorHint`
- `letterSpacing`, `lineSpacingExtra`
- `lineSpacingMultiplier`
- `maxLines`, `minLines`
- `inputType`, `imeOptions`
- `fontFamily`
- `drawableStart`, `drawableEnd`
- `drawableTop`, `drawableBottom`
- `drawablePadding`
- `textScaleX`
- `transformationMethod`
- `isAllCaps`
- `shadowColor`
- `shadowRadius`
- `shadowDx`
- `shadowDy`

##### ImageView
- `src`
- `scaleType`
- `tint`, `tintMode`

## 🏗️ Original High-Level Architecture

Voyager is built with a modular architecture:

- **Core Engine**: High-performance XML parsing and rendering
- **Annotation Processor**: KSP-based code generation
- **Resource Manager**: Efficient resource handling
- **View Registry**: Dynamic view registration system
- **Attribute Processor**: Type-safe attribute handling

## 📈 Performance Optimizations

The library implements several performance optimizations to ensure smooth runtime performance:

1. Lazy initialization of attributes
2. Caching of frequently used values
3. Efficient string operations
4. Optimized view traversal
5. Reduced object creation
6. Thread-safe operations
7. Memory-efficient attribute processing
8. Smart caching of parsed layouts
9. Optimized view creation and recycling
10. Parallel processing support
11. Incremental build optimization

## 🔧 Custom View Support

You can register custom views using the `@ViewRegister` annotation:

```kotlin
@ViewRegister("CustomView")
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Attribute("app:customText")
    var customText: String? = null
        set(value) {
            field = value
            // Update view
        }
}
```

## 🔄 Roadmap

Our vision is to evolve Voyager from a powerful layout engine into a more comprehensive UI framework for dynamic Android applications. The following roadmap outlines our steps towards this goal, alongside ongoing improvements to the core library.

### Short-term Goals (Next 3 months)

1. **Performance Improvements**
   - [ ] Implement layout caching system
   - [ ] Optimize memory usage
   - [ ] Add parallel processing for large layouts
   - [ ] Implement view recycling

2. **Feature Additions**
   - [ ] Support for dynamic theme changes
   - [ ] Add layout preview support
   - [ ] Implement layout validation
   - [ ] Add support for custom attribute types
   - [ ] *Proactive Bug Fixing:* Continuously audit and fix potential bugs in parsing and view handling logic, and promptly address community-reported issues.
   - [ ] *Accessibility Enhancements:* Introduce tools or automated checks to help ensure dynamically loaded layouts meet accessibility standards.

3. **Developer Experience**
   - [ ] Improve error messages
   - [ ] Add detailed logging
   - [ ] Create debugging tools
   - [ ] Enhance documentation

### Long-term Goals (6-12 months)

1. **Advanced Features & Framework Evolution**
   - [ ] Support for complex animations
   - [ ] *Enhanced Styling/Theming:* Support for runtime style application from JSON or other structured formats, beyond current dynamic theme changes.
   - [ ] *Advanced Data Binding:* Explore more advanced data binding capabilities within dynamic XMLs (e.g., simple expressions, ViewModel binding).
   - [ ] *Security for Remote Layouts:* If loading XML from a network, implement measures like signature verification or sandboxing for enhanced security.
   - [ ] *Lifecycle Management:* Define and manage lifecycle for dynamically created views/components, potentially integrating with Android Jetpack lifecycle components.
   - [ ] *State Management Guidance:* Provide clear guidance and utilities for managing state within Voyager-driven UIs, aligning with established Android patterns (e.g., ViewModel, LiveData/Flow).
   - [ ] Layout templates
   - [ ] Remote layout updates

2. **Tooling & Integration**
   - [ ] Android Studio plugin
   - [ ] Layout preview tool
   - [ ] Performance profiler
   - [ ] Debugging tools
   - [ ] *Jetpack Compose Integration:* Allow Voyager-inflated views to be embedded within Compose UI and vice-versa.

3. **Ecosystem & Future Development**
   - [ ] Community guidelines
   - [ ] Sample apps
   - [ ] Integration guides
   - [ ] Performance benchmarks
   - [ ] *Navigation System (Exploratory):* Investigate a lightweight navigation system for applications heavily reliant on Voyager for screen generation.
   - [ ] *Extensibility & Plugin Architecture:* Develop a more robust plugin system to allow developers to easily extend Voyager's core functionality or integrate third-party services.
   - [ ] *CLI Tooling (Future Consideration):* Explore the potential for CLI tools for validating Voyager XMLs, generating boilerplate code, or managing layout templates.

## 📊 Project Status

- **Version**: 1.0.0-Beta01
- **Status**: Actively Maintained & Under Development
- **Target Android**: API 21+
- **Kotlin Version**: 1.9.0+
- **Gradle Version**: 8.0+

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 📞 Support

- [GitHub Issues](https://github.com/A0ks9/XmlRuntime/issues)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/voyager)

## 🔄 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.

## 🙏 Acknowledgments

- Android Jetpack Compose for inspiration
- Kotlin Symbol Processing (KSP)
- Android Gradle Plugin
- All contributors and maintainers

---
