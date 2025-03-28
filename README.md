# Voyager - Dynamic XML Runtime Layout Engine

[![Android CI Build](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml/badge.svg)](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.voyager/xml-runtime)](https://search.maven.org/artifact/com.voyager/xml-runtime)

Voyager is a high-performance, dynamic XML runtime layout engine for Android that enables you to load and render XML layouts at runtime without requiring app recompilation. It provides a flexible and efficient way to create dynamic user interfaces.

# ⚠ Project Archived – No Longer Maintained

This project is no longer maintained and will not receive any further updates, bug fixes, or support.

## 🚨 Status  
- 🚫 No new features  
- 🛠 No bug fixes  
- 📭 No issue tracking or pull requests  

You are free to **fork** and modify the project as needed. However, please note that it is provided **as-is**, with no guarantees of stability or future updates.  

Thank you to everyone who contributed and supported this project!

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

3. **Developer Experience**
   - [ ] Improve error messages
   - [ ] Add detailed logging
   - [ ] Create debugging tools
   - [ ] Enhance documentation

### Long-term Goals (6-12 months)

1. **Advanced Features**
   - [ ] Support for complex animations
   - [ ] Dynamic style inheritance
   - [ ] Layout templates
   - [ ] Remote layout updates

2. **Tooling**
   - [ ] Android Studio plugin
   - [ ] Layout preview tool
   - [ ] Performance profiler
   - [ ] Debugging tools

3. **Ecosystem**
   - [ ] Community guidelines
   - [ ] Sample apps
   - [ ] Integration guides
   - [ ] Performance benchmarks

## 📊 Project Status

- **Version**: 1.0.0-Beta01
- **Status**: Active Development
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

Made with ❤️ by [Abdelrahman Omar](https://github.com/A0ks9)