# Voyager - Dynamic XML Runtime Layout Engine

[![Android CI Build](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml/badge.svg)](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.voyager/xml-runtime)](https://search.maven.org/artifact/com.voyager/xml-runtime)

Voyager is a high-performance, dynamic XML runtime layout engine for Android that enables you to load and render XML layouts at runtime without requiring app recompilation. It provides a flexible and efficient way to create dynamic user interfaces.

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
// Initialize the library
Voyager.init(context)

// Load XML layout
val layout = Voyager.loadLayout(context, xmlString)

// Render layout
val view = layout.render()

// Add to container
container.addView(view)
```

### Advanced Usage

- **Accessing Inflated Views:** After inflating a layout, you can find views by ID using standard Android methods (`findViewById`) on the returned parent view or by iterating through its children. Remember that IDs must be unique.
- **Dynamic Event Handling:** To attach listeners to dynamically inflated views, you can either:
    1.  Traverse the view hierarchy after inflation and attach listeners programmatically.
    2.  For custom views registered with `@ViewRegister`, you can define methods annotated with `@Attribute` that could, for example, accept a lambda or a binding reference to a ViewModel method (this would be an advanced feature to develop).
- **Interacting with ViewModel:** Ensure your ViewModel provides `LiveData` or `StateFlow` for dynamic content that views should observe. For actions, views can call ViewModel methods directly.

### Troubleshooting

- **KSP Issues:** Ensure `ksp` is correctly configured in your `build.gradle.kts` files for both the library and your app module if you're using custom view registration. Clean and rebuild the project if you encounter annotation processing errors.
- **Resource Not Found:** Voyager attempts to resolve resources dynamically. Ensure all referenced resources (`@drawable/`, `@string/`, `@color/`, etc.) are present in your project. For complex cases, you might need to use the `ResourcesBridge`.
- **View Not Rendering as Expected:** Double-check your XML syntax. Use Android Studio's Layout Inspector to debug the view hierarchy at runtime.

### Best Practices

- **Performance:** While Voyager is optimized, avoid overly complex or deeply nested XMLs for dynamic inflation if performance is critical. Use layout caching if re-inflating the same layout multiple times.
- **Memory:** Be mindful of context leaks if passing contexts to custom views or related classes. Voyager itself is designed to be memory efficient.
- **Security:** Be cautious when loading XML from untrusted sources (e.g., over the network). Sanitize or validate such XMLs before passing them to Voyager. Consider implementing signature checks for remote XMLs (see Roadmap).

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

## 🛠️ Architecture

Voyager is built with a modular architecture:

- **Core Engine**: High-performance XML parsing and rendering
- **Annotation Processor**: KSP-based code generation
- **Resource Manager**: Efficient resource handling
- **View Registry**: Dynamic view registration system
- **Attribute Processor**: Type-safe attribute handling

The **Core Engine** is the heart of Voyager, responsible for parsing XML input and constructing the view hierarchy. It leverages the **View Registry** to instantiate specific view types, including custom views registered via the **Annotation Processor** (KSP). The **Attribute Processor** works in tandem to apply XML attributes to these views in a type-safe manner. The **Resource Manager** efficiently handles access to Android resources like drawables, strings, and dimensions needed during layout inflation. The overall data flow typically starts with an XML string or resource ID, which is parsed into an internal representation, then transformed into a hierarchy of View objects, and finally rendered onto the screen.

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
- **Status**: Active Development
- **Target Android**: API 21+
- **Kotlin Version**: 1.9.0+
- **Gradle Version**: 8.0+

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

#### How the Code Works (Overview)
*   `Voyager` (library module): Contains the core runtime engine for parsing, view creation, attribute handling, and resource management.
*   `voyager-processor` (KSP module): Handles annotation processing for `@ViewRegister` and `@Attribute` to generate code that supports custom views.
*   `voyager-plugin` (Gradle plugin module): Provides the Gradle plugin for easier integration into Android projects.
*   `app` (sample module): Demonstrates usage of the Voyager library. A good place to test changes.

#### Getting Started with Development
*   **Environment:** Clone the repository and open the project in the latest stable version of Android Studio.
*   **Building:** The project can be built using standard Gradle commands (e.g., `./gradlew build`) or via Android Studio.
*   **Running Tests:** Execute `./gradlew check` to run all verifications, including unit tests and lint. Add new tests for any new functionality or bug fixes in the relevant `src/test` or `src/androidTest` directory of the modified module.

#### Coding Style & PR Process
*   Follow standard Kotlin coding conventions (see the official Kotlin Lang documentation) and Android development best practices.
*   Ensure your code is well-commented, especially for complex logic.
*   Submit Pull Requests to the `main` branch (or a designated development branch). Provide a clear description of your changes.
*   Look for issues tagged 'good first issue' or 'help wanted' if you're new to the project.

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

Made with ❤️ by [Abdelrahman Omar](https://github.com/A0ks9)