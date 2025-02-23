# Dynamic UI Builder (codename: Voyager)

## Overview

Voyager is an Android project designed to facilitate the creation of dynamic UIs that can be built and modified at runtime. It empowers developers to define UI layouts using both JSON and XML formats, providing unparalleled flexibility and enabling UI changes without requiring app updates. Leveraging cutting-edge technologies like Kotlin, Room persistence, Koin dependency injection, and Kotlin Symbol Processing (KSP), Voyager enables efficient, scalable, and persistent UI management.

## Intentions

The core objectives of this project are:

*   **Simplify Dynamic UI Development:**  To provide a straightforward and efficient framework for creating and managing dynamic UIs in Android applications, reducing the complexity of building adaptable interfaces.
*   **Enable UI Customization Without App Updates:**  To empower developers to modify and update UI layouts remotely via JSON or XML configurations, allowing for instant UI adjustments without requiring users to download new app versions.
*   **Promote Code Reusability and Modularity:** To create a modular, well-structured, and easily extensible codebase that can be readily adapted for diverse UI scenarios and project requirements.
*   **Accelerate Development Velocity:**  To minimize the dependence on traditional Android layouts by enabling automated generation of UI components through code, significantly boosting development productivity.

## Key Features

*   **Dynamic Layout Inflation:**  Creates Android layouts programmatically by parsing JSON or XML resource files and data structures at runtime. This offers unprecedented flexibility in UI design.
*   **Kotlin Symbol Processing (KSP) Code Generation:** Employs KSP to automatically generate `ViewAttributeParser` classes for custom views. This minimizes boilerplate code, accelerates development, and ensures type safety.
    *   **Java Compatibility:** The KSP-based annotation processing library now supports both Kotlin and Java, making it easier to integrate into existing projects or develop new features using either language.
*   **Room Data Persistence:** Leverages the Room persistence library to persist UI state and user data locally, enabling seamless restoration of previous UI states across app sessions and enhancing the user experience.
*   **Koin Dependency Injection:** Integrates Koin, a lightweight dependency injection framework, to simplify the management of application dependencies, promote testability, and improve code organization.
*   **XML and JSON Support:** Supports layout definitions in both XML and JSON formats, providing versatility and interoperability with various data sources.
*   **Modular Architecture:** Follows a layered architecture with clear separation of concerns: UI, ViewModel, Repository, and Data Sources, resulting in a highly maintainable and testable codebase.
*   **Custom View Creation:** Streamlines the creation of custom UI components using code generation, promoting flexibility and facilitating the reuse of custom components across different layouts and screens.
*   **Data Binding:** Enables seamless binding of UI elements to data sources, simplifying UI updates and enhancing the responsiveness and interactivity of dynamic interfaces.

## Roadmap: Implemented Improvements and Future Enhancements

This section tracks the progress and future plans for the Dynamic UI Builder project:

| Feature / Improvement                           | Status          | Description                                                                                                                                                                                                                                              |
|:------------------------------------------------|:----------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Core Functionality**                          |                 |                                                                                                                                                                                                                                                          |
| Dynamic Layout Inflation (JSON/XML)             | **Done**        | Implemented core functionality to dynamically create Android layouts by parsing JSON or XML definitions. This provides the foundation for building dynamic and adaptable UIs.                                                                            |
| Room Data Persistence                           | **Done**        | Integrated Room data persistence to store and restore UI state, ensuring a seamless user experience across app sessions.                                                                                                                                 |
| Koin Dependency Injection                       | **Done**        | Adopted Koin for dependency injection to streamline code organization, promote testability, and manage dependencies effectively.                                                                                                                         |
| Separation of Concerns (Layered Architecture)   | **Done**        | Organized the codebase into distinct layers (UI, ViewModel, Repository, Data Sources) to improve maintainability, testability, and scalability.                                                                                                          |
| **Code Generation**                             |                 |                                                                                                                                                                                                                                                          |
| KSP-Based Attribute Parser Generation           | **Done**        | Utilized KSP to automatically generate `ViewAttributeParser` classes for custom views, minimizing boilerplate code and accelerating development.                                                                                                         |
| Java Compatibility for Code Generation          | **Done**        | Enhanced the KSP-based code generation to support custom views defined in either Kotlin or Java, making it more versatile for different project scenarios.                                                                                               |
| **Testing & Quality Assurance**                 |                 |                                                                                                                                                                                                                                                          |
| Automated Unit and UI Testing (Core Components) | **In Progress** | Developing comprehensive automated tests for core UI components, such as text views, buttons, and images, to ensure they behave as expected in various scenarios.                                                                                        |
| **Enhancements**                                |                 |                                                                                                                                                                                                                                                          |
| Data Binding Support                            | **Planned**     | Integrating support for data binding expressions to dynamically update UI elements based on data changes, reducing boilerplate code and improving responsiveness.                                                                                        |
| Remote Configuration Loading                    | **Planned**     | Implementing the ability to load layout definitions from remote sources (e.g., cloud storage), allowing for dynamic UI updates without requiring app updates and facilitating A/B testing.                                                               |
| Visual Layout Editor Integration                | **Planned**     | Developing a visual layout editor that allows users to design dynamic UIs graphically and automatically generate JSON/XML definitions, making it more accessible to designers and non-technical team members.                                            |
| Theming and Style Support                       | **Planned**     | Adding robust support for theming and styling, allowing developers to customize the appearance of dynamic layouts with themes, styles, and customizable attributes.                                                                                      |
| **Advanced Features**                           |                 |                                                                                                                                                                                                                                                          |
| Remote Image Loading Caching                    | **Planned**     | Implementing caching mechanisms for images loaded from remote sources to minimize network traffic, improve app performance, and handle image loading failures gracefully.                                                                                |
| Complex Layout Support                          | **Planned**     | Extending support to handle more advanced layout structures, such as custom compound views, RecyclerView item decorations, and complex animations.                                                                                                       |
| Event Binding                                   | **Planned**     | Supporting event binding, allowing developers to bind UI events (e.g., clicks, text changes) directly to methods in ViewModels or other data sources, enabling more dynamic and responsive UI interactions.                                              |
| Accessibility Support                           | **Planned**     | Enhancing the accessibility of dynamically generated layouts by automatically configuring UI elements with appropriate ARIA attributes and accessibility properties.                                                                                     |
| Error Handling and Fallbacks                    | **Planned**     | Providing robust error handling mechanisms for layout inflation and loading, allowing developers to define fallback layouts to be displayed when primary layouts cannot be loaded due to network issues, data errors, or other unforeseen circumstances. |
| Performance Optimization                        | **Planned**     | Implementing various performance optimization strategies, such as caching layout definitions, pre-loading assets, and using background threads for UI updates, to ensure smooth and responsive user experiences.                                         |
| Layout Validation                               | **Planned**     | Providing a validation tool that allows developers to validate JSON/XML layout definitions before they are inflated, identifying potential errors and ensuring the structural integrity of the UI.                                                       |
| Local Data Binding                              | **Planned**     | Automatically updating UI elements when local data sources or repositories change, ensuring that the UI always reflects the latest data and improving the responsiveness of the application.                                                             |

## Technologies Used

*   Kotlin
*   Android SDK
*   Room Persistence Library
*   Koin Dependency Injection
*   Kotlin Symbol Processing (KSP)
*   JSON
*   XML
*   Robolectric (for unit testing)
*   Truth (for assertions)

## Architecture

The project follows a layered architecture:

*   **UI Layer:** Activities and Fragments responsible for displaying the UI and handling user interactions.
*   **ViewModel Layer:** ViewModels manage UI state and interact with the Repository layer.
*   **Repository Layer:** Provides a clean API for data access, abstracting the underlying data sources.
*   **Data Source Layer:** Handles data retrieval and persistence from local (Room) and remote (XML/JSON files) sources.

## Setup Instructions

1.  **Clone the Repository:**

    ```bash
    git clone <your-repository-url>
    ```

2.  **Open in Android Studio:**

    Open the project in Android Studio.

3.  **Configure Dependencies:**

    Ensure that you have the necessary dependencies declared in your `build.gradle.kts` files. The project uses:

    *   Kotlin Coroutines
    *   AndroidX Libraries (AppCompat, Material, ConstraintLayout, etc.)
    *   Room Persistence Library
    *   Koin DI
    *   KSP

4.  **Build the Project:**

    Build the project in Android Studio (Build -> Make Project).

## Usage

1.  **XML/JSON Layout Definitions:** Place your XML or JSON layout files in the `res/raw` directory or provide URIs to files stored elsewhere.
2.  **Dynamic Inflation:** Use the `DynamicLayoutInflation.inflateJson()` method to inflate the layout at runtime.
3.  **View Handlers:** Implement the `ViewHandler` interface to handle view-related events and lifecycle management.
    *   **Pre-Loading:** To preload views for dynamic loading first, please use LoadingActivity or SplashActivity to avoid future errors

## KSP Symbol Processing

This project uses KSP to generate `ViewAttributeParser` classes automatically. To trigger the code generation:

1.  Ensure that KSP is properly configured in your `build.gradle.kts` file (see the KSP documentation for details).
2.  Rebuild the project.

## Contributing

Contributions are welcome!  Please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Implement your changes.
4.  Submit a pull request.

## License

(Choose a license, e.g., Apache 2.0, MIT)