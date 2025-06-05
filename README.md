# Voyager - Dynamic XML Runtime Layout Engine

[![Android CI Build](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml/badge.svg)](https://github.com/A0ks9/XmlRuntime/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.voyager/xml-runtime)](https://search.maven.org/artifact/com.voyager/xml-runtime)

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Getting Started](#getting-started)
4. [Architecture](#architecture)
5. [Performance](#performance)
6. [Custom Views](#custom-views)
7. [Error Handling](#error-handling)
8. [Contributing](#contributing)
9. [Roadmap](#roadmap)
10. [Support](#support)

## Overview

Voyager is a high-performance, dynamic XML runtime layout engine for Android that enables you to load and render XML layouts at runtime without requiring app recompilation. It provides a flexible and efficient way to create dynamic user interfaces.

### ✨ Actively Maintained ✨

This project was originally archived but is now under active maintenance and development. We are working to improve its features, performance, and stability. Contributions are welcome!

## Features

### Core Features
- **Dynamic XML Loading**: Load and render XML layouts at runtime
- **High Performance**: Optimized parsing and rendering engine
- **Type Safety**: Full Kotlin support with type-safe attribute handling
- **Custom View Support**: Easy integration of custom views
- **Resource Management**: Efficient resource handling and caching

### Technical Features
- **Incremental Processing**: Smart incremental build support
- **Annotation Processing**: KSP-based annotation processing
- **Memory Efficient**: Optimized memory usage and garbage collection
- **Thread Safe**: Safe concurrent operations
- **Comprehensive Testing**: Extensive test coverage

## Getting Started

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
// 1. Initialize Koin in your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule(MyResourcesProvider(), isLoggingEnabled = true))
        }
    }
}

// 2. In your Activity or Fragment
class YourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layoutUri = Uri.parse("android.resource://${packageName}/${R.raw.your_layout_file}")
        val parentView = findViewById<ViewGroup>(R.id.your_container_viewgroup)
        val themeResId = R.style.YourAppTheme_VoyagerCompatible

        DynamicLayoutInflation.inflate(this, themeResId, layoutUri, parentView) { inflatedView ->
            if (inflatedView != null) {
                Log.d("VoyagerDemo", "View inflated: ${inflatedView::class.java.simpleName}")
            } else {
                Log.e("VoyagerDemo", "Failed to inflate layout from $layoutUri")
            }
        }
    }
}
```

## Architecture

### Core Components

1. **DynamicLayoutInflation**
   - Main engine for layout inflation
   - Handles XML/JSON parsing
   - Manages view hierarchy creation

2. **ViewNode**
   - Represents view elements
   - Handles attribute storage
   - Supports caching

3. **ViewProcessor**
   - Creates view instances
   - Manages view registry
   - Handles custom views

4. **AttributeProcessor**
   - Applies attributes to views
   - Type-safe attribute handling
   - Optimized attribute application

### Plugin Architecture

1. **voyager-plugin**
   - Build-time code generation
   - Resource processing
   - Integration tasks

2. **voyager-processor**
   - KSP annotation processing
   - Custom view registration
   - Attribute mapping generation

## Performance

### Optimizations

1. **Memory Management**
   - Efficient resource handling
   - Smart caching
   - Reduced object creation

2. **Processing**
   - Incremental builds
   - Parallel processing
   - Thread-safe operations

3. **View Handling**
   - Optimized view creation
   - Efficient recycling
   - Smart attribute application

### Best Practices

1. **View Registration**
   - Pre-register custom views
   - Use appropriate view types
   - Implement efficient recycling

2. **Attribute Handling**
   - Use type-safe attributes
   - Implement proper validation
   - Handle errors gracefully

3. **Resource Management**
   - Cache frequently used resources
   - Clean up unused resources
   - Monitor memory usage

## Custom Views

### Registration

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

### Attribute Handling

```kotlin
@ViewRegister("CustomView")
class CustomView : View {
    @Attribute("app:customColor")
    fun setCustomColor(color: Int) {
        try {
            setBackgroundColor(color)
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

## Error Handling

### Compile-time Errors
```kotlin
// Invalid usage examples
@ViewRegister
class InvalidView {
    @Attribute(xmlName = "text") // Error: Not a View subclass
    var text: String? = null
}
```

### Runtime Errors
```kotlin
try {
    DynamicLayoutInflation.inflate(context, themeResId, layoutUri, parentView) { inflatedView ->
        // Handle success
    }
} catch (e: Exception) {
    when (e) {
        is IllegalArgumentException -> // Handle invalid layout
        is SecurityException -> // Handle permission issues
        is IOException -> // Handle file operations
        else -> // Handle other errors
    }
}
```

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comprehensive documentation
- Include unit tests

## Roadmap

### Short-term Goals (Next 3 months)
1. **Performance Improvements**
   - [ ] Implement layout caching
   - [ ] Optimize memory usage
   - [ ] Add parallel processing
   - [ ] Implement view recycling

2. **Feature Additions**
   - [ ] Support for dynamic themes
   - [ ] Add layout preview
   - [ ] Implement validation
   - [ ] Add custom attribute types

3. **Developer Experience**
   - [ ] Improve error messages
   - [ ] Add detailed logging
   - [ ] Create debugging tools
   - [ ] Enhance documentation

### Long-term Goals (6-12 months)
1. **Advanced Features**
   - [ ] Complex animations
   - [ ] Layout templates
   - [ ] Remote updates
   - [ ] Jetpack Compose integration

2. **Tooling**
   - [ ] Android Studio plugin
   - [ ] Layout preview tool
   - [ ] Performance profiler
   - [ ] Debugging tools

## Support

- [GitHub Issues](https://github.com/A0ks9/XmlRuntime/issues)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/voyager)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Android Jetpack Compose for inspiration
- Kotlin Symbol Processing (KSP)
- Android Gradle Plugin
- All contributors and maintainers

---
