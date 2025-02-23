# DynamicInflate: The Ultimate Android Library for Dynamic Layout Inflation

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Kotlin Version](https://img.shields.io/badge/Kotlin-2.1.10-blueviolet.svg)](https://kotlinlang.org/)

**Dynamically inflate and manage Android UI layouts from JSON or XML, empowering flexible and
adaptable applications.**

## Introduction

DynamicInflate is a powerful Android library designed to revolutionize how you build user
interfaces. It allows you to **inflate Android layouts at runtime from external JSON or XML files**,
enabling unprecedented flexibility and adaptability in your applications. Instead of being limited
to static layouts defined within your APK, DynamicInflate lets you:

* **Update UI without App Updates:** Modify your app's user interface dynamically by simply changing
  the JSON or XML layout definitions hosted externally or within your assets. No need to push new
  app versions for UI tweaks!
* **Simplify UI Management:** Define and manage UI layouts externally, making it easier to
  collaborate, version control, and update UI designs independently of the app's code.
* **Reduce APK Size (Potentially):**  Defer layout loading and potentially reduce your initial APK
  size by fetching layouts on demand.
* **Experiment and A/B Test UI Changes:**  Easily A/B test different UI variations by serving
  different layout files to different user segments.
* **Theme and Style Dynamically:** Apply themes and styles to dynamically inflated layouts, ensuring
  consistent branding and visual appearance.
* **Enable Server-Driven UI:** Build truly server-driven applications where the entire UI structure
  can be controlled and updated from the backend.

DynamicInflate goes beyond simple layout inflation. It's designed to be a robust framework,
incorporating features like data binding (coming soon), custom view support, and performance
optimizations to ensure smooth and efficient UI rendering.

## Features

**Current Features:**

* **Dynamic Layout Inflation:**
    * Inflate layouts from JSON and XML files.
    * Load layouts from external storage (using `Uri`), assets, or server URLs (coming soon).
* **Comprehensive View Support:**
    * Supports inflation of a wide range of standard Android views and Material Components,
      including: `View`, `Space`, `TextView`, `EditText`, `Button`, `ImageButton`, `ImageView`,
      `LinearLayout`, `FrameLayout`, `RelativeLayout`, `TableLayout`, `TableRow`, `GridLayout`,
      `ConstraintLayout`, `CoordinatorLayout`, `RecyclerView`, `ListView`, `GridView`,
      `ExpandableListView`, `ScrollView`, `HorizontalScrollView`, `NestedScrollView`, `VideoView`,
      `SurfaceView`, `TextureView`, `CardView`, `MaterialCardView`, `ProgressBar`,
      `CircularProgressIndicator`, `LinearProgressIndicator`, `Switch`, `SwitchCompat`, `CheckBox`,
      `AppCompatCheckBox`, `RadioButton`, `AppCompatRadioButton`, `SwitchMaterial`,
      `MaterialCheckBox`, `MaterialRadioButton`, `Spinner`, `AutoCompleteTextView`,
      `MultiAutoCompleteTextView`, `AppCompatAutoCompleteTextView`, `SeekBar`, `AppCompatSeekBar`,
      `Slider`, `RatingBar`, `Chip`, `ChipGroup`, `TabLayout`, `Toolbar`, `AppCompatToolbar`,
      `MaterialToolbar`, `BottomNavigationView`, `NavigationView`, `NavigationRailView`,
      `AppBarLayout`, `CollapsingToolbarLayout`, `ViewPager`, `ViewPager2`, `TextInputLayout`,
      `TextInputEditText`, `DrawerLayout`, `SlidingPaneLayout`, `MaterialTimePicker`, `Snackbar`.
* **Attribute Parsing and Application:**
    * Efficiently parses and applies common Android view attributes defined in JSON/XML to their
      corresponding View properties.
    * Supports attributes for layout parameters, visibility, text, colors, drawables, gravity,
      padding, margins, click listeners, and more.
    * Uses Kotlin Symbol Processing (KSP) for optimized attribute parsing, minimizing reflection
      overhead.
* **Custom View Attribute Handling:**
    * Annotation-based (`@AutoViewAttributes`, `@AutoAttribute`) mechanism to easily define custom
      attribute parsers for your own custom views.
* **View State Persistence:**
    * Automatically saves and restores the state of dynamically inflated views using Room
      Persistence Library.
    * Caches `ViewState` in a local database for offline access and fast app restarts.

**Coming Soon (Roadmap):**

* **Resource Handling:**
    * Supports loading colors and drawables from Android resources (`@color/`, `@drawable/`).
    * **Asset Support:** Load colors and drawables directly from your app's `assets` folder (
      `@asset/`).
* **Dependency Injection:**
    * Leverages Koin for dependency injection, promoting modularity, testability, and
      maintainability.
* **Error Handling:**
    * Robust error handling during layout parsing, view creation, and attribute application.
    * Graceful degradation in case of errors.
* **Data Binding and Two-Way Binding:**
    * Seamlessly bind data directly to views inflated from JSON/XML.
    * Enable two-way data binding for automatic UI updates based on data changes and vice versa.
* **RecyclerView and ListAdapter Support:**
    * Dynamic inflation of `RecyclerView` layouts with efficient item recycling.
    * Support for dynamically creating and configuring `RecyclerView.Adapter` instances.
* **Layout Includes/Templates:**
    * Define reusable layout snippets (like XML `<include>` or `<merge>`) in JSON/XML.
    * Reduce layout duplication and improve modularity.
* **Conditional Layouts:**
    * Implement conditional layout logic in JSON/XML to show/hide or change parts of the layout
      based on data or conditions.
* **Data-Driven Styling:**
    * Style views dynamically based on bound data values (e.g., change text color based on status).
* **Custom View Registration API:**
    * Provide a flexible API for users to register and use their own custom View classes without
      modifying the library code or relying solely on annotations.
* **Enhanced Theming and Styling:**
    * Comprehensive theme support for applying global styles to dynamic layouts.
    * Style inheritance and cascading styles.
    * Support for loading themes from external sources (JSON/XML theme files).
* **Component Library:**
    * Create a library of pre-built, reusable UI components (buttons, text fields, cards, forms,
      etc.) that can be easily used in JSON/XML layouts.
    * Significantly simplify layout creation and increase reusability.
* **Network Layout Loading:**
    * Fetch layout definitions from remote URLs, enabling server-driven UI capabilities.
* **Advanced Error Handling and Fallbacks:**
    * More detailed error reporting and logging.
    * Mechanisms for defining fallback layouts or default UI behavior in case of parsing or
      inflation errors.
* **Performance Optimizations:**
    * Layout Caching: Cache parsed layouts in memory to avoid repeated parsing.
    * View Pooling/Recycling: Implement view pooling and recycling for lists and dynamic updates to
      minimize view creation overhead.
    * Asynchronous Inflation: Perform layout parsing and inflation off the main thread to prevent UI
      blocking.
* **Improved Asset Handling:**
    * Support for asset qualifiers to load resources based on device configurations (screen density,
      language, etc.).

## How It Works (Architecture Overview)

DynamicInflate is built upon a modular and efficient architecture:

1. **Layout Source (`DynamicLayoutInflation`):**
    * Accepts layout definitions in JSON or XML format, specified by a `Uri` (local file, assets, or
      network URL - coming soon).
    * `DynamicLayoutInflation` class acts as the main entry point for layout inflation.
    * Handles the initial parsing and orchestration of the inflation process.

2. **Parsing (`FileHelper`, `ParseHelper`):**
    * **`FileHelper`**: Responsible for reading layout files (JSON or XML) from the specified `Uri`.
        * Uses SAX parser for efficient XML parsing and conversion to JSON.
        * Handles file extension detection and content resolution.
    * **`ParseHelper`**: Provides utility functions for parsing attribute values from strings to
      their corresponding Android types (integers, floats, colors, drawables, gravity, etc.).

3. **View Creation (`ViewProcessor`):**
    * **`ViewProcessor`**: Manages the creation of Android `View` instances based on the "type"
      specified in the JSON/XML layout definition.
    * Uses a factory pattern and a registry (`viewCreators`) to map view type names to their
      corresponding constructor functions.
    * Minimizes reflection by using direct view instantiation through registered creators.
    * Allows registration of custom `ViewAttributeParser` classes to handle attributes specific to
      custom views.

4. **Attribute Application (`AttributeRegistry`, `BaseViewAttributes`, `ViewAttributeParser`):**
    * **`AttributeRegistry`**:  A central registry for attribute processors.
        * Stores mappings between attribute names (e.g., "text", "layout_width") and
          `AttributeProcessorRegistry` interfaces.
        * Provides static methods for registering and applying attributes to `View` instances.
    * **`BaseViewAttributes`**:  Initializes and configures the `AttributeRegistry` with default
      attribute processors for common Android views and attributes.
    * **`ViewAttributeParser`**: Abstract class that custom attribute parsers extend.
        * Each `ViewAttributeParser` is responsible for registering attribute-specific processors
          for a particular `View` type.
        * KSP annotation processing generates `ViewAttributeParser` implementations for classes
          annotated with `@AutoViewAttributes`.

5. **Layout Inflation (`DynamicLayoutInflation`):**
    * `DynamicLayoutInflation` recursively traverses the parsed JSON/XML structure.
    * For each view definition, it uses `ViewProcessor` to create the `View` instance.
    * Retrieves the appropriate `ViewAttributeParser` (if available) or uses the default
      `AttributeRegistry` to apply attributes to the `View`.
    * Adds the created `View` to the specified `ViewGroup` parent.

6. **View State Persistence (`ViewState`, `ViewStateDao`, `RoomViewStateDataSource`):**
    * **`ViewState`**: Data class representing the state of a dynamically inflated `View`, including
      its ID, type, attributes (as JSON), and child views (as JSON). Implements `Parcelable` for
      state saving during configuration changes.
    * **`ViewStateDao`**: Room Data Access Object for interacting with the `view_states` database
      table.
    * **`RoomViewStateDataSource`**: Data source class that provides methods to access and manage
      `ViewState` data in the Room database.
    * DynamicInflate automatically saves and restores `ViewState` to persist UI configurations
      across app sessions and configuration changes.

7. **Resource Handling (`ParseHelper`, `ResourceLoader` - Planned):**
    * **`ParseHelper`**: Provides utility functions to load colors and drawables from Android
      resources.
    * **`ResourceLoader` (Planned)**:  A dedicated class (to be implemented) for handling resource
      loading, including:
        * Loading colors and drawables from assets (`assets/` URI scheme).
        * Caching loaded resources for performance.
        * Potentially handling network-based resources (future).

8. **Dependency Injection (`AppModule`, Koin):**
    * Uses Koin for dependency injection to manage dependencies between components (e.g.,
      `ViewStateRepository`, `XmlRepository`, data sources, ViewModels).
    * Improves modularity, testability, and simplifies configuration.

## Getting Started (Basic Usage)

**1. Add Dependency:**

Add the DynamicInflate library dependency to your `build.gradle.kts` (Module: app) file:

```kotlin
dependencies {
    implementation("com.github.A0ks9:voyager:$version")
    ksp("com.github.A0ks9:voyager-ksp:$version")
}
```

**2. Create a Layout File (JSON or XML):**

Create a JSON or XML file defining your layout structure. For example, a simple JSON layout named
`my_layout.json` in your `assets` folder:

```json
{
  "type": "LinearLayout",
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
        "text": "Hello from Dynamic Layout!",
        "textSize": "24sp",
        "textColor": "@color/black",
        "gravity": "center_horizontal",
        "layout_width": "wrap_content",
        "layout_height": "wrap_content"
      }
    },
    {
      "type": "Button",
      "attributes": {
        "text": "Click Me!",
        "id": "@+id/myButton",
        "layout_width": "wrap_content",
        "layout_height": "wrap_content",
        "layout_marginTop": "16dp",
        "onClick": "onButtonClick"
      }
    }
  ]
}
```

**3. Implement `ViewHandler` in your Activity or Fragment:**

Your Activity or Fragment that will host the dynamic layout needs to implement the `ViewHandler`
interface:

```kotlin
package com.example.myapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dynamicinflate.utils.DynamicLayoutInflation
import com.dynamicinflate.utils.JsonCast
import com.dynamicinflate.utils.interfaces.ViewHandler
import com.example.myapp.databinding.ActivityMainBinding // Replace with your binding class

class MainActivity : AppCompatActivity(), ViewHandler {

    private lateinit var binding: ActivityMainBinding // Replace with your binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Replace with your binding class
        setContentView(binding.root)

        // Get the parent layout from your binding (replace parentLayout with your actual ID)
        val parentLayout = binding.parentLayout // Replace parentLayout with your actual ID

        // Inflate the layout from assets
        val layoutUri =
            Uri.parse("android.resource://com.example.myapp/assets/my_layout.json") // Replace with your package name and asset file name
        DynamicLayoutInflation.inflateJson(this, layoutUri, parentLayout)
    }

    override fun getContainerLayout(): ViewGroup {
        // Return the parent ViewGroup where the dynamic layout will be inflated
        return binding.parentLayout // Replace parentLayout with your actual ID
    }

    override fun getJsonConfiguration(): JsonCast? {
        return null // Or return your JsonCast configuration if needed programmatically
    }

    override fun onViewCreated(parentView: ViewGroup?) {
        // Optional callback after view inflation is complete
        val myButton = parentView?.findViewById<View>(R.id.myButton) // Find view by ID
        myButton?.setOnClickListener {
            Toast.makeText(this, "Button Clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    // Example onClick method for the Button in the layout
    fun onButtonClick(view: View) {
        Toast.makeText(this, "Button Clicked via onClick!", Toast.LENGTH_SHORT).show()
        // You can access views by ID here if needed:
        // val myTextView = view.findViewByIdString("myTextViewId")
    }
}
```

**4. Run Your App:**

Build and run your Android application. DynamicInflate will inflate the layout defined in
`my_layout.json` and add it to the `parentLayout` ViewGroup in your Activity's layout.

## Advanced Usage

* **Data Binding Examples:** Show how to bind data to views using the data binding features (once
  implemented).
* **Custom View Registration:** Demonstrate how to register and use custom view classes.
* **Theming and Styling:**  Examples of applying themes and styles to dynamic layouts.
* **Layout Includes/Templates:** Show how to use layout includes to create reusable layout snippets.
* **Conditional Layouts:** Demonstrate how to implement conditional UI logic in JSON/XML.
* **Network Layout Loading:** (When implemented) Show how to load layouts from remote URLs.
* **Programmatic Layout Configuration:** Explain how to configure layouts and attributes
  programmatically using the DynamicInflate API.

## Improvements and Contributions

**Planned Improvements:**

* Implement Data Binding and Two-Way Binding.
* Add RecyclerView and ListAdapter support for dynamic lists.
* Introduce Layout Includes/Templates for reusable layout components.
* Enable Conditional Layouts based on data or conditions.
* Implement Data-Driven Styling for dynamic view styling.
* Provide a Custom View Registration API for easy integration of user-defined views.
* Enhance Theming and Styling capabilities with style inheritance and external theme loading.
* Develop a Component Library of pre-built UI elements.
* Enable Network Layout Loading from remote URLs.
* Improve Error Handling and provide fallback mechanisms.
* Optimize Performance with layout caching, view pooling, and asynchronous inflation.
* Enhance Asset Handling with support for resource qualifiers.
* Handle Colors and drawables from assets and save new colors

**Contributions:**

We welcome contributions to DynamicInflate! If you have ideas for improvements, bug fixes, or new
features, please feel free to contribute.

* **Bug Reports:**  If you find a bug, please create a detailed issue on
  the [GitHub repository issues page](https://github.com/A0ks9/XmlRuntime/issues/new).
* **Feature Requests:**  Suggest new features or enhancements by creating a feature request issue.
* **Pull Requests:**  Contribute code by submitting pull requests. Please follow these guidelines:
    * Fork the repository.
    * Create a branch for your feature or bug fix.
    * Write clear and well-documented code.
    * Include tests if applicable.
    * Submit a pull request to the `main` branch.

Please see [CONTRIBUTING](https://github.com/A0ks9/XmlRuntime/blob/master/CONTRIBUTING.md) (create
this file in your repository) for more detailed contribution
guidelines.

## License

DynamicInflate is released under
the [Apache 2.0 License](https://opensource.org/licenses/Apache-2.0). See the `LICENSE` file for the
full license text.