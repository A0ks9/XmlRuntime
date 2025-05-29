# Sample Voyager Application (`app` module)

## 1. Module Purpose

This `app` module serves as a sample application to demonstrate the features and usage of the **Voyager** library. It showcases how Voyager can dynamically inflate XML layouts at runtime, providing a flexible way to build and update user interfaces without needing to recompile the application.

This sample provides practical examples of:
- Loading XML layout definitions from various sources.
- Rendering these layouts into native Android views.
- Integrating custom views with Voyager.
- Managing UI state and logic with ViewModels.
- Utilizing dependency injection with Koin.

## 2. Module Structure

The `app` module is structured as follows:

*   **`com.example.MyApplication`**:
    *   Contains the main `Application` class.
    *   Responsible for initializing Koin for dependency injection.

*   **`com.example.di`**:
    *   Houses Koin modules, such as `originalAppModule`.
    *   These modules define how dependencies (like ViewModels, services, etc.) are provided and injected throughout the application.

*   **`com.example.ui.activities`**:
    *   Contains Android `Activity` classes.
    *   `MainActivity`: Serves as the main entry point of the application and provides an example screen demonstrating Voyager's capabilities.

*   **`com.example.ui.components`**:
    *   Includes custom view implementations.
    *   `CustomButton.kt`: An example of a custom view component that is registered with Voyager using the `@ViewRegister` annotation, allowing it to be used in dynamically inflated XML layouts.

*   **`com.example.ui.viewModels`**:
    *   Contains `ViewModel` classes.
    *   `MainViewModel.kt`: Handles the UI logic, state management, and business operations for `MainActivity`. It interacts with Voyager to load and display layouts.

*   **`res/layout`**:
    *   `activity_main.xml`: The main layout for `MainActivity`, primarily using traditional Android view binding for its static components.
    *   Other XML layout files (e.g., `*.xml` loaded at runtime) are dynamically processed and inflated by Voyager, not directly referenced in compiled code in the same way `activity_main.xml` is.

*   **`res/values`**:
    *   Contains standard Android resources like strings (`strings.xml`), colors (`colors.xml`), and themes (`themes.xml`).

## 3. How to Build and Run

To build and run this sample application:

1.  **Prerequisites:**
    *   Ensure you have Android Studio installed.
    *   Make sure the Android SDK is set up correctly.

2.  **Building and Running:**
    *   Open the root project (which includes the Voyager library and this `app` module) in Android Studio.
    *   Select the `app` run configuration from the dropdown menu in the toolbar.
    *   Click the 'Run' button (or press `Shift + F10`). Android Studio will build and deploy the application to the selected emulator or connected device.

## 4. Key Features Demonstrated

This sample application highlights the following key features of the Voyager library and modern Android development practices:

*   **Dynamic XML Loading and Rendering:** The core functionality of Voyager is showcased by loading XML layout strings (or files) at runtime and rendering them into interactive views.
*   **Custom View Integration:** Demonstrates how to create and use custom views (e.g., `CustomButton.kt`) within Voyager-inflated layouts using `@ViewRegister` for the view class and `@Attribute` for its custom XML attributes.
*   **ViewModel Usage:** `MainViewModel` is used to manage UI-related data, handle user interactions, and orchestrate calls to the Voyager library, separating logic from the `MainActivity`.
*   **Dependency Injection with Koin:** Koin is used to manage dependencies, making the codebase more modular, testable, and maintainable (e.g., injecting `MainViewModel` into `MainActivity`).
*   **Storage Access Framework (SAF):** The app demonstrates using SAF for:
    *   Opening user-selected XML layout files from device storage.
    *   Saving generated JSON representations of layouts to device storage.

## 5. Dependencies

The `app` module relies on several key dependencies:

*   **Voyager Library (Local Module):**
    *   `implementation(project(":xml-runtime"))`: The core Voyager runtime library.
    *   `ksp(project(":voyager-processor"))`: The KSP annotation processor for Voyager, used to generate code for custom views and attributes.
*   **Koin:**
    *   Used for dependency injection to manage and provide objects like ViewModels.
*   **AndroidX Libraries:**
    *   Standard libraries such as `AppCompat`, `ConstraintLayout`, `RecyclerView`, `Lifecycle (ViewModel, LiveData)`, etc.
*   **Material Components for Android:**
    *   For modern UI elements and themes.

This README provides an overview of the `app` module's purpose, structure, and how to get it running. For more details on the Voyager library itself, please refer to the main project README.md.
