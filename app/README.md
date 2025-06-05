# Sample Voyager Application (`app` module)

## 1. Module Purpose

This `app` module serves as a sample application to demonstrate the features and usage of the **Voyager** library. It showcases how Voyager can dynamically inflate XML layouts at runtime, providing a flexible way to build and update user interfaces without needing to recompile the application.

This sample provides practical examples of:
- Loading XML layout definitions from various sources
- Rendering these layouts into native Android views
- Integrating custom views with Voyager
- Managing UI state and logic with ViewModels
- Utilizing dependency injection with Koin
- Handling runtime permissions and file access
- Implementing edge-to-edge display
- Using RxJava for asynchronous operations

## 2. Module Structure

The `app` module is structured as follows:

### Core Components

*   **`com.example.MyApplication`**:
    *   Main `Application` class that initializes core components
    *   Sets up logging system with `AndroidLogger`
    *   Initializes Koin for dependency injection
    *   Registers custom attributes for XML parsing

*   **`com.example.di`**:
    *   Contains Koin modules for dependency injection
    *   `AppModule`: Defines ViewModel and other dependencies
    *   Uses Koin's DSL for clean dependency configuration

*   **`com.example.logging`**:
    *   `AndroidLogger`: Custom implementation of Voyager's logging interface
    *   Provides Android-specific logging using `android.util.Log`

### UI Components

*   **`com.example.ui.activities`**:
    *   `MainActivity`: Main entry point demonstrating Voyager's capabilities
    *   Implements file selection and XML rendering
    *   Handles runtime permissions
    *   Uses edge-to-edge display
    *   Manages RxJava subscriptions

*   **`com.example.ui.components`**:
    *   `CustomButton`: Example custom view implementation
    *   Demonstrates `@ViewRegister` and `@Attribute` usage
    *   Shows how to create custom XML attributes
    *   Extends `AppCompatButton` for Material Design support

*   **`com.example.ui.viewModels`**:
    *   `MainViewModel`: Manages UI state and business logic
    *   Uses LiveData for reactive UI updates
    *   Handles file selection state
    *   Manages button text and enabled states

### Resources

*   **`res/layout`**:
    *   `activity_main.xml`: Main layout using ViewBinding
    *   Supports dynamic XML loading and rendering
    *   Implements Material Design components

*   **`res/values`**:
    *   Standard Android resources
    *   Custom theme implementation
    *   String and color resources

## 3. Key Features

### Dynamic XML Loading
- Uses Storage Access Framework (SAF) for file selection
- Supports loading XML from various sources
- Handles runtime permissions for file access

### Custom View Integration
- Demonstrates custom view creation with `@ViewRegister`
- Shows custom attribute implementation with `@Attribute`
- Provides example of extending Android components

### Modern Android Architecture
- Uses ViewModel for state management
- Implements LiveData for reactive UI updates
- Follows MVVM architecture pattern
- Uses Koin for dependency injection

### UI/UX Features
- Implements edge-to-edge display
- Uses Material Design components
- Supports dynamic theme changes
- Provides responsive layout

### Asynchronous Operations
- Uses RxJava for background processing
- Handles XML rendering asynchronously
- Manages subscriptions properly

## 4. How to Build and Run

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Kotlin 1.5.0 or higher

### Building and Running
1. Open the project in Android Studio
2. Sync Gradle files
3. Select the `app` run configuration
4. Click 'Run' or press `Shift + F10`

### Testing the App
1. Launch the app
2. Grant storage permissions if prompted
3. Click "Choose File" to select an XML file
4. Click "Render XML" to see the dynamic layout

## 5. Dependencies

### Core Dependencies
- **Voyager Library**:
  - `:xml-runtime`: Core runtime library
  - `:voyager-processor`: KSP annotation processor

### AndroidX Libraries
- AppCompat
- ConstraintLayout
- Lifecycle (ViewModel, LiveData)
- Core KTX

### Other Dependencies
- Koin for dependency injection
- RxJava for asynchronous operations
- Material Components for Android

## 6. Contributing

Feel free to contribute to this sample app by:
1. Forking the repository
2. Creating a feature branch
3. Submitting a pull request

## 7. License

This sample app is provided under the same license as the Voyager library.
