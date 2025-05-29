# Voyager Resources Gradle Plugin

## 1. Overview

The Voyager Resources Gradle Plugin is a companion plugin for the [Voyager framework](../Voyager/README.md). Its primary purpose is to automatically generate a Kotlin source file named `ResourcesBridge.kt`.

This generated `ResourcesBridge.kt` file provides a programmatic way to resolve resource names (as strings) to their corresponding Android integer resource IDs (e.g., `R.string.app_name`). This capability is essential for the Voyager core module, particularly for its dynamic layout inflation system, which often defines UI elements and their resources using string identifiers in JSON or XML layout definitions.

By using this plugin, you enable the Voyager framework to dynamically look up resource IDs at runtime based on string names extracted from your layout definitions.

## 2. Plugin Integration

To use the Voyager Resources Plugin, you need to apply it in your Android application or library module's `build.gradle` (Groovy DSL) or `build.gradle.kts` (Kotlin DSL) file.

**Kotlin DSL (`build.gradle.kts`):**
```kotlin
plugins {
    // Apply other plugins like com.android.application or com.android.library
    id("com.android.application") // or com.android.library
    kotlin("android")
    id("com.voyager.plugin.resources") // Placeholder: Replace with the actual plugin ID
}

// Configure the plugin (see section 3)
resources {
    resFiles.from(fileTree("src/main/res/values").include("**/*.xml"))
}
```

**Groovy DSL (`build.gradle`):**
```groovy
plugins {
    // Apply other plugins like com.android.application or com.android.library
    id 'com.android.application' // or 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'com.voyager.plugin.resources' // Placeholder: Replace with the actual plugin ID
}

// Configure the plugin (see section 3)
resources {
    resFiles.from(fileTree('src/main/res/values').include('**/*.xml'))
}
```
**Note:** Replace `"com.voyager.plugin.resources"` with the actual plugin ID when it's published.

## 3. Configuration

The plugin registers an extension named `resources` that you can use to configure its behavior.

```kotlin
// build.gradle.kts
resources {
    // Configure properties here
    resFiles.from(
        fileTree("src/main/res/values").include("**/*.xml"),
        fileTree("src/main/res/values-en").include("**/*.xml") // Example: include specific locale values
    )
}
```

```groovy
// build.gradle
resources {
    // Configure properties here
    resFiles.from(
        fileTree('src/main/res/values').include('**/*.xml'),
        fileTree('src/main/res/values-en').include('**/*.xml') // Example: include specific locale values
    )
}
```

### Available Configuration Options:

*   **`resFiles`**:
    *   **Type:** `org.gradle.api.file.ConfigurableFileCollection`
    *   **Description:** This is the primary configuration property. It specifies the collection of Android resource XML files that the plugin should parse. Typically, these are the XML files located in your module's `src/main/res/values/` directory and any locale-specific `values-*` directories.
    *   **Usage:** You should configure this to point to all relevant `values/*.xml` files.
        ```kotlin
        // Include all XML files from the default 'values' directory
        resFiles.from(fileTree("src/main/res/values").include("**/*.xml"))

        // Include files from multiple 'values' directories (e.g., for different locales or build types)
        resFiles.from(
            fileTree("src/main/res/values").include("**/*.xml"),
            fileTree("src/main/res/values-en").include("**/*.xml"),
            // project.fileTree("src/demo/res/values").include("**/*.xml") // For build types/flavors
        )
        ```

## 4. Generated Code: `ResourcesBridge.kt`

The plugin generates a Kotlin file named `ResourcesBridge.kt`.

*   **Location:** The file is typically generated in your module's build directory, under a path like:
    `build/generated/voyager-kt-resources/com/voyager/resources/ResourcesBridge.kt`
    This path is automatically added to your variant's source set, so you don't need to configure it manually.

*   **Package:** The generated class is placed in the package `com.voyager.resources`.

*   **Structure & Purpose:**
    The `ResourcesBridge.kt` file contains a class named `ResourcesBridge` that implements the `com.voyager.utils.interfaces.ResourcesProvider` interface (defined in the Voyager core module).
    ```kotlin
    package com.voyager.resources

    import com.voyager.utils.interfaces.ResourcesProvider
    import your.application.pkg.R // Imports your app's R class

    internal class ResourcesBridge : ResourcesProvider {
        override fun getResId(type: String, name: String): Int {
            return when (type) {
                "string" -> when (name) {
                    "app_name" -> R.string.app_name
                    "another_string" -> R.string.another_string
                    // ... other strings
                    else -> -1
                }
                "color" -> when (name) {
                    "primary_color" -> R.color.primary_color
                    // ... other colors
                    else -> -1
                }
                // ... other resource types
                else -> -1
            }
        }
    }
    ```

*   **Benefits & Usage by Voyager Core:**
    *   **String-Based Resource Lookup:** The primary benefit is to allow the Voyager core framework to look up Android resource IDs using strings for the resource type (e.g., "string", "color") and resource name (e.g., "app_name"). This is crucial for dynamic layout systems where resource identifiers are often specified as strings in JSON or XML definitions.
    *   **Decoupling:** It decouples the core Voyager framework from direct dependencies on your app's specific `R` class structure at compile time (for the framework itself), allowing it to operate on names.
    *   **Centralized Mapping:** Provides a single, auto-generated point for this string-to-ID mapping.

## 5. Build Process Integration

*   **Automatic Task Registration:** The plugin automatically registers a `GenerateResourcesTask` (e.g., `generateVoyagerResourcesDebug`) for each Android build variant (debug, release, custom flavors/types).
*   **Task Dependency:** It ensures that this generation task runs before the Kotlin compilation task for that variant. This means `ResourcesBridge.kt` is always generated before your project code (including any Voyager core library usage) is compiled.
*   **Incremental Builds & Caching:**
    *   The `GenerateResourcesTask` is designed to be incremental. It only re-runs if its inputs (your `resFiles`, plugin version, or package name) change, or if its outputs are missing.
    *   It's also a `@CacheableTask`, meaning Gradle can cache its output and reuse it across builds (even clean builds, if the cache is populated from a previous build or a shared build cache) if inputs are identical. This significantly speeds up subsequent builds.

## 6. Important Considerations & Limitations

*   **Supported Resource Types:** The plugin currently parses and generates mappings for resource types typically declared in `res/values/*.xml` files. These include:
    *   `color`
    *   `string`
    *   `style`
    *   `dimen`
    *   `bool`
    *   `integer`
    *   `array` (e.g., `string-array`, `integer-array`)
    *   `plurals`
    *   `attr` (custom attributes defined in `attrs.xml`)
    It does **not** automatically discover file-based resources like individual drawables (PNGs, JPGs in `res/drawable/`) or layouts by filename unless those filenames are also referenced by a resource name declaration in a values XML file (which is typical for XML drawables, selectors, etc., but not for raw image assets).

*   **Build Times:** For projects with an extremely large number of resource entries in `values` files, the initial generation of `ResourcesBridge.kt` might contribute a noticeable amount to the build time. However, subsequent builds should be fast due to Gradle's incrementality and caching. The generated `when` statement, if very large, might also have a minor impact on Kotlin compilation times for that specific file.

*   **Focus on `res/values`:** The plugin is primarily designed to process XML files located within `res/values/` (and its locale/configuration-specific variants like `res/values-en/`). It does not scan other `res/` subdirectories like `res/drawable/` or `res/layout/` directly for file-based resources.

*   **Resource Name Mangling:** Resource names containing `.` (dots) will have the dots replaced with `_` (underscores) in the generated `ResourcesBridge.kt` to form valid Kotlin identifiers (e.g., `name="user.profile.title"` becomes `R.string.user_profile_title`).

This README provides a guide to integrating and using the Voyager Resources Plugin. Ensure you replace placeholder values like the plugin ID with the actual published values.
```
