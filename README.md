# XmlRuntime

**XmlRuntime** is an Android library that enables dynamic UI rendering from XML definitions at runtime. The library is designed with a lean architecture—using fewer classes and concise, maintainable Kotlin code—to actively handle custom views and custom attributes while ensuring efficient performance and a responsive UI.

> **Note:**  
> XmlRuntime leverages core functionality from [Proteus](https://github.com/Flipkart/Proteus), which is licensed under the **Apache License 2.0**. This README does not include details about Proteus internals.

---

## Features

- **Dynamic XML UI Rendering:**  
  Render Android views from XML definitions at runtime, enabling dynamic UI updates without requiring app recompilation.
  
- **Active Custom View & Attribute Handling:**  
  Automatically processes and applies custom views and attributes with minimal configuration.
  
- **Lean Architecture:**  
  Designed with fewer classes for a cleaner, more maintainable codebase.
  
- **Concise & Clean Code:**  
  Utilizes Kotlin’s modern features to keep the code simple, expressive, and efficient.
  
- **Efficient Performance:**  
  Optimized parsing and view creation ensure a responsive UI even with dynamic rendering.

---

## Feature Checklist

| Feature                                                                                                          | Implemented |
|------------------------------------------------------------------------------------------------------------------|:-----------:|
| Dynamic XML UI Rendering                                                                                         |    False    |
| Active Custom View Handling                                                                                      |    False    |
| Active Custom Attribute Handling                                                                                 |    False    |
| Lean Architecture (fewer classes)                                                                                |    False    |
| Concise & Clean Kotlin Code                                                                                      |    False    |
| Efficient Performance                                                                                            |    False    |
| **Reduce Class Count:** Further merge similar functionalities to simplify the codebase.                          |    False    |
| **Enhanced Customization:** Streamline registration & processing of custom views/attributes.                     |    False    |
| **Improve Error Handling:** Enhance parsing and runtime error messages for layout inflation.                     |    False    |
| **Add Unit Tests:** Implement comprehensive tests (unit and integration) for stability.                          |    False    |
| **Optimize Performance:** Further optimize layout parsing and view creation pipelines.                           |    False    |
| **Expanded Documentation:** Continue updating documentation to help developers integrate and extend the library. |    False    |

---

## How It Works

1. **XML Layout Parsing:**  
   The library reads XML layout definitions provided at runtime and converts them into an internal structured format.

2. **Dynamic View Creation:**  
   Based on the XML, corresponding Android views (e.g., `TextView`, `Button`) are instantiated. Standard views are handled automatically, and you can easily register custom view types.

3. **Custom Attributes Application:**  
   Custom attribute processors actively apply attribute values to views, ensuring that any customization defined in the XML is automatically rendered.

4. **Lean & Maintainable Code:**  
   With fewer classes and concise Kotlin implementations, the library is both easy to understand and extend, while maintaining excellent performance.

---

# XmlRuntime - Dynamic Android Layout Inflation from XML/JSON

This project demonstrates dynamic Android layout inflation from XML or JSON resources at runtime, using a Kotlin-based library with KSP (Kotlin Symbol Processing) for automated attribute handling. It utilizes the MVVM (Model-View-ViewModel) architecture for a clean and maintainable codebase.

## Project Structure

com.runtimexml (root package)
├── data (Data Layer - Repositories, Data Sources, Models)
│ ├── repositories (Handles data retrieval and logic)
│ │ ├── XmlRepository.kt - Repository for XML file operations.
│ │ └── ViewStateRepository.kt - Repository for managing ViewState data.
│ ├── sources (Data sources - Local and Remote)
│ │ ├── local (Local data sources - Room)
│ │ │ ├── RoomViewStateDataSource.kt - Data source for Room database operations on ViewState.
│ │ │ ├── db
│ │ │ │ ├── AppDatabase.kt - Room database definition.
│ │ │ │ └── ViewStateDao.kt - DAO (Data Access Object) for ViewState entity.
│ │ └── remote (For "remote" data - in this case, XML file parsing)
│ │ └── XmlFileDataSource.kt - Data source for XML file parsing operations.
│ └── models (Data models/entities)
│ │ └── ViewState.kt - Data class representing the state of a dynamically created view.
├── di (Dependency Injection - Koin)
│ └── AppModule.kt - Koin module for dependency injection setup.
├── ui (UI Layer - Activities, Fragments, ViewModels, Adapters)
│ ├── activities (Activities - Views in MVVM)
│ │ └── MainActivity.kt - Main activity, responsible for UI interactions and observing ViewModel.
│ ├── components (Custom UI components)
│ │ └── MyCustomTextView.kt - Example of a custom TextView component with automated attribute handling.
│ ├── viewModels (ViewModels - Business logic and data for Views)
│ │ └── MainViewModel.kt - ViewModel for MainActivity, handles UI logic and data preparation.
├── utils (General Utility - Platform/App level utilities)
│ ├── annotations (Annotations for KSP processing)
│ │ ├── AutoAttribute.kt - Annotation to mark attributes for automatic handling.
│ │ └── AutoViewAttributes.kt - Annotation to mark custom views for automatic attribute parsing.
│ ├── processors (Attribute processing and view creation logic)
│ │ ├── AttributeProcessorProvider.kt - KSP Symbol Processor Provider.
│ │ ├── AttributeRegistry.kt - KSP Symbol Processor for generating attribute mappers and parsers.
│ │ ├── ViewAttributeParser.kt - Abstract class for defining attribute parsers for custom views.
│ │ └── ViewProcessor.kt - Handles view creation and registration of ViewAttributeParsers.
│ ├── DimensionConvertor.kt - Utility for converting dimension strings to pixels.
│ ├── Extensions.kt - Kotlin extensions functions.
│ ├── FileHelper.kt - Utility for file operations, including XML to JSON conversion.
│ ├── ParseHelper.kt - Utility for parsing various attribute values.
│ ├── ParentViewReference.kt - Utility for managing parent view references.
│ └── Utils.kt - General utility functions.
├── generated (Generated code - KSP output)
│ └── com
│ └── runtimexml
│ └── utils
│ └── processors
│ └── generated
│ └── MyCustomTextViewViewAttributeParser.kt (Example - Name will vary) - Generated ViewAttributeParser for MyCustomTextView.
└── res (Resources - layouts, drawables, etc.)
└── ... (resources folder)

---

## Getting Started

### Prerequisites

- **Android Studio** 4.x or later
- **Gradle** 6.x or later
- **Android SDK** 21 or above

### Installation

1. **Clone the Repository:**

    ```bash
    git clone https://github.com/A0ks9/XmlRuntime.git
    ```

2. **Open in Android Studio:**  
   Open the cloned repository in Android Studio.

3. **Build the Project:**  
   Use Gradle to build and run the project.

4. **Integrate into Your App:**  
   Include the library module as a dependency in your project and import the necessary classes.

---

## Usage

- **Defining XML Layouts:**  
  Create XML layout files to describe your UI. Standard view types (e.g., `TextView`, `Button`) are supported, and you can specify custom attributes as needed.

- **Registering Custom Views & Attributes:**  
  Extend the provided extension points to register your custom view types and attribute processors. For example:

  ```kotlin
  proteus.registerViewType("MyCustomView", object : ViewTypeParser<MyCustomView>() {
      override fun createView(context: Context): MyCustomView = MyCustomView(context)
      
      override fun addAttributeProcessors() {
          addProcessor("customAttr", object : AttributeProcessor<MyCustomView>() {
              override fun handle(view: MyCustomView, value: Any?) {
                  view.setCustomValue(value.toString())
              }
          })
      }
  })
  ```

- **Inflating Layouts at Runtime:**  
  Use the library’s API to load and inflate your XML layouts dynamically:

  ```kotlin
  // Load your XML layout (possibly converted to JSON or another internal format)
  val xmlLayoutDefinition = /* your XML layout data */
  
  // Initialize Proteus with your context
  val proteus = Proteus.Builder()
      .setContext(this)
      .build()
  
  // Inflate the view using the layout definition
  val view = proteus.createView(xmlLayoutDefinition)
  
  // Set the inflated view as your activity's content
  setContentView(view)
  ```

---

## Contributing

Contributions are welcome! To contribute:

1. **Fork the Repository:**  
   Create your own branch for your feature or bug fix.

2. **Write Tests:**  
   Ensure your changes are covered by appropriate tests.

3. **Update Documentation:**  
   Update the README or inline documentation if necessary.

4. **Submit a Pull Request:**  
   Open a pull request with a detailed description of your changes.

For any questions or discussions, please open an issue on the [GitHub repository](https://github.com/A0ks9/XmlRuntime).

---

## License

XmlRuntime uses core functionality from Proteus, which is licensed under the **Apache License 2.0**.  
See [Proteus LICENSE](https://github.com/Flipkart/Proteus/blob/master/LICENSE) for details.

XmlRuntime itself is released under the **[Your License Here]**. (You may choose to use the same license as Proteus or another compatible open-source license.)

---

## Contact

For more information, please visit the [XmlRuntime GitHub repository](https://github.com/A0ks9/XmlRuntime) or contact the project maintainer.
