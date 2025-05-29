package com.voyager.utils.processors

import android.annotation.SuppressLint
import android.content.Context
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.MultiAutoCompleteTextView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.VideoView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

/**
 * `ViewProcessor` is responsible for the dynamic creation of Android `View` instances within the Voyager framework.
 *
 * This class acts as a central registry and factory for views. It maintains a map of known view types
 * (e.g., "TextView", "LinearLayout", "com.example.CustomView") to functions that can create instances
 * of these views.
 *
 * Key functionalities:
 * - **View Registration:** Allows programmatic registration of custom view types along with their
 *   constructor logic. Standard Android views (widgets, layouts, Material Components) are pre-registered.
 * - **View Creation:** Provides methods to create `View` instances given a type string (e.g., "Button")
 *   and a `ContextThemeWrapper`. This ensures views are created with the correct context and theme.
 * - **Reflection Fallback:** If a requested view type is not explicitly registered, `ViewProcessor`
 *   attempts to create it using reflection. This allows for flexibility but is generally less performant
 *   than registered creators. The created reflective constructor is then cached for future use.
 * - **Constructor Handling:** It primarily targets constructors that accept a single `Context` argument,
 *   which is common for Android views. The provided `ContextThemeWrapper` is used here.
 * - **Namespace Handling:** Includes logic to resolve simple view names (e.g., "TextView") to their
 *   fully qualified class names (e.g., "android.widget.TextView") using a default package.
 *
 * The `ViewProcessor` is designed for performance and thread safety, utilizing a [ConcurrentHashMap]
 * for storing view creators. It plays a crucial role in enabling Voyager to inflate layouts defined
 * in XML or JSON by dynamically instantiating the necessary view components.
 *
 * @see com.voyager.utils.DynamicLayoutInflation
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class ViewProcessor { // Class KDoc updated

    @SuppressLint("ShowToast")
    companion object {
        /**
         * A thread-safe map storing registered view creator functions.
         * The key is the fully qualified class name of the view (e.g., "android.widget.TextView"),
         * and the value is a lambda function `(ContextThemeWrapper) -> View` that creates an instance of the view.
         * [ConcurrentHashMap] is used to ensure thread safety for registrations and retrievals.
         */
        private val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

        /**
         * A logger instance for logging messages related to `ViewProcessor` operations.
         * Initialized lazily to reduce startup overhead.
         */
        private val logger by lazy { Logger.getLogger(ViewProcessor::class.java.name) }

        /**
         * The default package name for standard Android widgets (e.g., "android.widget.").
         * This is used by [getFullQualifiedType] to resolve simple view names like "TextView"
         * to "android.widget.TextView" if no package is specified.
         */
        private const val DEFAULT_ANDROID_WIDGET_PACKAGE = "android.widget."

        /**
         * Initialization block for the companion object.
         * This block is executed when the `ViewProcessor` class is first loaded.
         * It calls [registerDefaultViews] to populate the [viewCreators] map with
         * standard Android views and Material Design components.
         */
        init {
            registerDefaultViews()
        }

        /**
         * Registers a comprehensive set of default Android views, AppCompat views, and Material Design components
         * with their respective creator functions in the [viewCreators] map.
         * This pre-populates the processor with common views, making them readily available for dynamic inflation
         * without needing reflection for these standard types.
         */
        private fun registerDefaultViews() {
            // Registering common Android framework views.
            // Each call to registerView maps a string type (e.g., "android.widget.View")
            // to a lambda function that constructs an instance of the corresponding View class,
            // passing the ContextThemeWrapper `it`.

            // Basic views
            registerView("android.widget.View") { View(it) } // Base class for all views
            registerView("android.widget.Space") { Space(it) }

            // Text views
            registerView("android.widget.TextView") { AppCompatTextView(it) }
            registerView("android.widget.EditText") { AppCompatEditText(it) }
            registerView("androidx.appcompat.widget.AppCompatTextView") { AppCompatTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatEditText") { AppCompatEditText(it) }

            // Button views
            registerView("android.widget.Button") { MaterialButton(it) }
            registerView("android.widget.ImageButton") { AppCompatImageButton(it) }
            registerView("androidx.appcompat.widget.AppCompatButton") { AppCompatButton(it) }
            registerView("androidx.appcompat.widget.AppCompatImageButton") { AppCompatImageButton(it) }

            // Material Design views
            registerView("com.google.android.material.button.MaterialButton") { MaterialButton(it) }
            registerView("com.google.android.material.floatingactionbutton.FloatingActionButton") {
                FloatingActionButton(it)
            }

            // Layout views
            registerView("android.widget.LinearLayout") { LinearLayout(it) }
            registerView("android.widget.FrameLayout") { FrameLayout(it) }
            registerView("android.widget.RelativeLayout") { RelativeLayout(it) }
            registerView("android.widget.TableLayout") { TableLayout(it) }
            registerView("android.widget.TableRow") { TableRow(it) }
            registerView("android.widget.GridLayout") { GridLayout(it) }
            registerView("androidx.constraintlayout.widget.ConstraintLayout") { ConstraintLayout(it) }
            registerView("androidx.coordinatorlayout.widget.CoordinatorLayout") {
                CoordinatorLayout(
                    it
                )
            }

            // List views
            registerView("androidx.recyclerview.widget.RecyclerView") { RecyclerView(it) }
            registerView("android.widget.ListView") { ListView(it) }
            registerView("android.widget.GridView") { GridView(it) }
            registerView("android.widget.ExpandableListView") { ExpandableListView(it) }

            // Scroll views
            registerView("android.widget.ScrollView") { ScrollView(it) }
            registerView("android.widget.HorizontalScrollView") { HorizontalScrollView(it) }
            registerView("androidx.core.widget.NestedScrollView") { NestedScrollView(it) }

            // Image views
            registerView("android.widget.ImageView") { AppCompatImageView(it) }
            registerView("androidx.appcompat.widget.AppCompatImageView") { AppCompatImageView(it) }
            registerView("com.google.android.material.imageview.ShapeableImageView") {
                ShapeableImageView(
                    it
                )
            }

            // Media views
            registerView("android.widget.VideoView") { VideoView(it) }
            registerView("android.view.SurfaceView") { SurfaceView(it) }
            registerView("android.view.TextureView") { TextureView(it) }

            // Card views
            registerView("androidx.cardview.widget.CardView") { CardView(it) }
            registerView("com.google.android.material.card.MaterialCardView") { MaterialCardView(it) }

            // Progress views
            registerView("android.widget.ProgressBar") { ProgressBar(it) }
            registerView("com.google.android.material.progressindicator.CircularProgressIndicator") {
                CircularProgressIndicator(it)
            }
            registerView("com.google.android.material.progressindicator.LinearProgressIndicator") {
                LinearProgressIndicator(it)
            }

            // Input views
            registerView("android.widget.Switch") { SwitchCompat(it) }
            registerView("androidx.appcompat.widget.SwitchCompat") { SwitchCompat(it) }
            registerView("android.widget.CheckBox") { AppCompatCheckBox(it) }
            registerView("androidx.appcompat.widget.AppCompatCheckBox") { AppCompatCheckBox(it) }
            registerView("android.widget.RadioButton") { AppCompatRadioButton(it) }
            registerView("androidx.appcompat.widget.AppCompatRadioButton") { AppCompatRadioButton(it) }

            // Material Design input views
            registerView("com.google.android.material.switchmaterial.SwitchMaterial") {
                SwitchMaterial(
                    it
                )
            }
            registerView("com.google.android.material.checkbox.MaterialCheckBox") {
                MaterialCheckBox(
                    it
                )
            }
            registerView("com.google.android.material.radiobutton.MaterialRadioButton") {
                MaterialRadioButton(
                    it
                )
            }

            // Selection views
            registerView("android.widget.Spinner") { AppCompatSpinner(it) }
            registerView("androidx.appcompat.widget.AppCompatSpinner") { AppCompatSpinner(it) }
            registerView("android.widget.AutoCompleteTextView") { AutoCompleteTextView(it) }
            registerView("android.widget.MultiAutoCompleteTextView") { MultiAutoCompleteTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatAutoCompleteTextView") {
                AppCompatAutoCompleteTextView(it)
            }

            // Slider views
            registerView("android.widget.SeekBar") { AppCompatSeekBar(it) }
            registerView("androidx.appcompat.widget.AppCompatSeekBar") { AppCompatSeekBar(it) }
            registerView("com.google.android.material.slider.Slider") { Slider(it) }
            registerView("android.widget.RatingBar") { RatingBar(it) }

            // Material Design components
            registerView("com.google.android.material.chip.Chip") { Chip(it) }
            registerView("com.google.android.material.chip.ChipGroup") { ChipGroup(it) }
            registerView("com.google.android.material.tabs.TabLayout") { TabLayout(it) }

            // Navigation views
            registerView("android.widget.Toolbar") { Toolbar(it) }
            registerView("androidx.appcompat.widget.Toolbar") { Toolbar(it) }
            registerView("com.google.android.material.appbar.MaterialToolbar") { MaterialToolbar(it) }
            registerView("com.google.android.material.bottomnavigation.BottomNavigationView") {
                BottomNavigationView(it)
            }
            registerView("com.google.android.material.navigation.NavigationView") {
                NavigationView(
                    it
                )
            }
            registerView("com.google.android.material.navigationrail.NavigationRailView") {
                NavigationRailView(it)
            }

            // App bar views
            registerView("com.google.android.material.appbar.AppBarLayout") { AppBarLayout(it) }
            registerView("com.google.android.material.appbar.CollapsingToolbarLayout") {
                CollapsingToolbarLayout(it)
            }

            // Pager views
            registerView("androidx.viewpager.widget.ViewPager") { ViewPager(it) }
            registerView("androidx.viewpager2.widget.ViewPager2") { ViewPager2(it) }

            // Text field views
            registerView("com.google.android.material.textfield.TextInputLayout") {
                TextInputLayout(
                    it
                )
            }
            registerView("com.google.android.material.textfield.TextInputEditText") {
                TextInputEditText(
                    it
                )
            }

            // Layout containers
            registerView("androidx.drawerlayout.widget.DrawerLayout") { DrawerLayout(it) }
            registerView("androidx.slidingpanelayout.widget.SlidingPaneLayout") {
                SlidingPaneLayout(
                    it
                )
            }

            // Special views that might require a different setup pattern.
            // For MaterialTimePicker, it seems we need to build it and then get its view.
            // This is a specific case handled during registration.
            registerView("com.google.android.material.timepicker.MaterialTimePicker") {
                MaterialTimePicker.Builder().build().requireView() // Note: requireView() might imply it must be added to a hierarchy.
            }
            // For Snackbar, we make a dummy Snackbar to get access to its view,
            // which can then be used or styled if Voyager supports that.
            registerView("com.google.android.material.snackbar.Snackbar") {
                Snackbar.make(View(it) /* A dummy anchor view */, "", Snackbar.LENGTH_SHORT).view
            }
        }

        /**
         * Registers a view creator function for a given fully qualified class name.
         * This is a private helper primarily used by [registerDefaultViews] and the reflection fallback mechanism
         * in [createViewByType].
         *
         * @param className The fully qualified class name of the view to register (e.g., "android.widget.TextView").
         * @param creator A lambda function that takes a [ContextThemeWrapper] and returns an instance of the [View].
         */
        @JvmStatic
        private fun registerView(className: String, creator: (ContextThemeWrapper) -> View) {
            // Check if a creator for this class name is already registered.
            if (viewCreators.containsKey(className)) {
                // Log a warning if overwriting an existing view creator. This might be intentional or a configuration error.
                logger.warning("View creator for $className is already registered. Overwriting.")
            }
            // Store the creator lambda in the map, keyed by the fully qualified class name.
            viewCreators[className] = creator
        }

        /**
         * Registers a view creator function for a given package and class name.
         * This public method allows external components or users of the Voyager library
         * to register their custom views.
         *
         * @param packageName The package name of the view (e.g., "com.example.customviews").
         * @param className The simple class name of the view (e.g., "MyCustomButton").
         * @param creator A lambda function that takes a [ContextThemeWrapper] and returns an instance of the [View].
         */
        @JvmStatic
        fun registerView(
            packageName: String,
            className: String,
            creator: (ContextThemeWrapper) -> View,
        ) {
            // Construct the fully qualified name from package and class name.
            val fullyQualifiedName = "$packageName.$className"
            // Check if a creator for this fully qualified name is already registered.
            if (viewCreators.containsKey(fullyQualifiedName)) {
                // Log a warning if overwriting.
                logger.warning("View creator for $fullyQualifiedName is already registered. Overwriting.")
            }
            // Store the creator lambda.
            viewCreators[fullyQualifiedName] = creator
        }

        /**
         * Checks if a view creator is registered for the given package and class name.
         *
         * @param packageName The package name of the view.
         * @param className The simple class name of the view.
         * @return `true` if a creator is registered for the fully qualified name, `false` otherwise.
         */
        @JvmStatic
        fun isRegistered(packageName: String, className: String): Boolean =
            viewCreators.containsKey("$packageName.$className")

        /**
         * Checks if a view creator is registered for the given class path.
         * The class path can be a simple name (e.g., "TextView") or a fully qualified name.
         *
         * @param classPath The class path (simple or fully qualified) of the view.
         * @return `true` if a creator is registered for the resolved fully qualified name, `false` otherwise.
         */
        internal fun isRegistered(classPath: String): Boolean =
            viewCreators.containsKey(getFullQualifiedType(classPath))

        /**
         * Creates a [View] instance using a registered creator for the given package and class name.
         * This method looks up a pre-registered creator function. It does not fall back to reflection.
         *
         * @param packageName The package name of the view.
         * @param className The simple class name of the view.
         * @param context The [ContextThemeWrapper] to be used for creating the view, ensuring proper theming.
         * @return The created [View] instance if a creator was found and successfully invoked, or `null` otherwise.
         */
        @JvmStatic
        fun createView(
            packageName: String,
            className: String,
            context: ContextThemeWrapper,
        ): View? {
            val cacheKey = "$packageName.$className"
            return viewCreators[cacheKey]?.invoke(context)
        }

        /**
         * Creates a [View] instance using a registered creator for the given class path.
         * The class path can be a simple name (which will be resolved using [DEFAULT_ANDROID_WIDGET_PACKAGE])
         * or a fully qualified name. This method looks up a pre-registered creator.
         * It does not fall back to reflection.
         *
         * @param classPath The class path (simple or fully qualified) of the view.
         * @param context The [ContextThemeWrapper] to be used for creating the view.
         * @return The created [View] instance if a creator was found and successfully invoked, or `null` otherwise.
         */
        internal fun createView(classPath: String, context: ContextThemeWrapper): View? {
            val fullyQualifiedName = getFullQualifiedType(classPath)
            return viewCreators[fullyQualifiedName]?.invoke(context)
        }

        /**
         * Creates a [View] instance for a given type string (simple or fully qualified name).
         *
         * This is the primary internal method used by [com.voyager.utils.DynamicLayoutInflation] to instantiate views.
         * It first attempts to find a pre-registered creator for the resolved fully qualified name.
         * If no creator is found, it falls back to using reflection to find a constructor that accepts
         * a single `Context` argument. If successful via reflection, the dynamically created constructor
         * logic is then registered (cached) in [viewCreators] for subsequent calls for the same type,
         * optimizing future instantiations.
         *
         * @param context The [ContextThemeWrapper] to be used for creating the view.
         * @param type The type string of the view to create (e.g., "TextView", "com.google.android.material.button.MaterialButton").
         * @return The created [View] instance.
         * @throws IllegalArgumentException if view creation fails (e.g., class not found, no suitable constructor,
         *                                  or constructor invocation fails).
         */
        internal fun createViewByType(context: ContextThemeWrapper, type: String): View {
            // Step 1: Resolve the potentially simple 'type' string (e.g., "TextView")
            // to its fully qualified class name (e.g., "android.widget.TextView").
            val fullyQualifiedName = getFullQualifiedType(type)

            // Step 2: Attempt to find and invoke a pre-registered creator for this view type.
            // The `let` scope function is used for a concise null check and invocation.
            viewCreators[fullyQualifiedName]?.invoke(context)?.let { createdView ->
                return createdView // If creator found and view created, return it immediately.
            }

            // Step 3: If no pre-registered creator was found, fallback to reflection.
            logger.info("No pre-registered creator for $fullyQualifiedName. Attempting reflection.")
            return try {
                // Load the Kotlin class (KClass) for the view type.
                val kClass = Class.forName(fullyQualifiedName).kotlin

                // Find a constructor that accepts a single Context argument.
                // This is the most common constructor signature for Android views.
                val targetConstructor = kClass.constructors.firstOrNull { constructor ->
                    val params = constructor.parameters
                    params.size == 1 && params[0].type.classifier == Context::class
                }

                // If no suitable constructor is found, throw an exception.
                targetConstructor ?: throw IllegalArgumentException(
                    "No suitable constructor (Context) found for $fullyQualifiedName. " +
                            "Consider registering this view type explicitly if it's a custom view or has a non-standard constructor."
                )

                // Create a lambda function that wraps the reflective constructor call.
                // This lambda will serve as the view creator.
                val reflectiveViewConstructor: (ContextThemeWrapper) -> View = { themedContext ->
                    // Invoke the constructor reflectively.
                    // Ensure the result is cast to View, or throw if it's not (shouldn't happen if constructor is correct).
                    requireNotNull(targetConstructor.call(themedContext) as? View) {
                        "View creation by reflection failed for type $fullyQualifiedName. Constructor returned null or wrong type."
                    }
                }

                // Step 4: Cache the reflectively found and wrapped constructor in `viewCreators`.
                // This avoids repeated reflection for the same view type in future calls, improving performance.
                registerView(fullyQualifiedName, reflectiveViewConstructor)
                logger.info("Successfully created and registered view constructor for $fullyQualifiedName via reflection.")

                // Step 5: Invoke the newly cached reflective constructor to create and return the view.
                reflectiveViewConstructor(context)

            } catch (e: ClassNotFoundException) {
                // Handle cases where the view class cannot be found.
                logger.severe("Class not found for type: $fullyQualifiedName. Ensure the class is available in the classpath and the name is correct.")
                throw IllegalArgumentException("Class not found for view type: $fullyQualifiedName. Check for typos or missing dependencies.", e)
            } catch (e: Exception) {
                // Catch other potential exceptions during reflection (e.g., security exceptions, instantiation errors).
                logger.severe("Error creating view for type '$fullyQualifiedName' using reflection: ${e.message}")
                throw IllegalArgumentException(
                    "Failed to create view for type: $fullyQualifiedName using reflection. Details: ${e.message}", e
                )
            }
        }

        /**
         * Resolves a potentially simple view type string to its fully qualified class name.
         * If the `type` string already contains a dot (`.`), it's assumed to be fully qualified.
         * Otherwise, it's prepended with [DEFAULT_ANDROID_WIDGET_PACKAGE].
         *
         * For example:
         * - "TextView" becomes "android.widget.TextView"
         * - "com.example.CustomView" remains "com.example.CustomView"
         *
         * @param type The view type string (e.g., "Button", "androidx.cardview.widget.CardView").
         * @return The fully qualified class name for the given type.
         */
        private fun getFullQualifiedType(type: String): String =
            if (type.contains(".")) type else "$DEFAULT_ANDROID_WIDGET_PACKAGE$type"
    }
}