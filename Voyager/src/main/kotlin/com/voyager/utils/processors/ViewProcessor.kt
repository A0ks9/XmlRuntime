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
import java.util.logging.Level
import java.util.logging.Logger

/**
 * `ViewProcessor` is responsible for creating instances of Android [View] objects dynamically
 * based on their type string (e.g., "TextView", "com.example.CustomView").
 * It maintains a registry of view creators and supports fetching views using a themed context.
 *
 * This class is designed for high performance and thread safety in dynamic layout inflation scenarios.
 *
 * Key functionalities:
 *  - **Registration of View Creators:** Allows registering custom view types along with a lambda function
 *    that knows how to create an instance of that view. This is crucial for performance as it
 *    avoids reflection for registered views.
 *  - **Default View Support:** Pre-registers a comprehensive list of standard Android framework,
 *    AppCompat, and Material Design views.
 *  - **View Creation by Type:** Provides a method ([createViewByType]) to get a [View] instance.
 *    It first checks the registry and, if not found, attempts to create the view using reflection.
 *    Constructors created via reflection are then cached for subsequent calls to improve performance.
 *  - **Context Handling:** All view creation methods require a [ContextThemeWrapper] to ensure views
 *    are inflated with the correct theme.
 *
 * @see [DynamicLayoutInflation] which uses this processor.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class ViewProcessor { // KDoc for the class itself

    /**
     * Companion object for [ViewProcessor], providing static access to its methods.
     * This object manages the registry of view creators and the logic for view instantiation.
     */
    @SuppressLint("ShowToast") // Retained as it was in the original code, consider if still needed.
    companion object {
        /**
         * A thread-safe [ConcurrentHashMap] that stores registered view creators.
         * The key is the fully qualified class name of the [View] (e.g., "android.widget.TextView").
         * The value is a lambda function `(ContextThemeWrapper) -> View` that instantiates the view.
         */
        private val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

        /**
         * Lazy-initialized [Logger] for logging messages related to [ViewProcessor] operations.
         * Using [Logger.getLogger] with the class name.
         */
        private val logger by lazy { Logger.getLogger(ViewProcessor::class.java.name) }

        /**
         * Default package prefix for Android widgets when a simple class name is provided
         * (e.g., "TextView" implies "android.widget.TextView").
         */
        private const val DEFAULT_ANDROID_WIDGET_PACKAGE = "android.widget."

        /**
         * Initializes the [ViewProcessor] by pre-registering a comprehensive set of default
         * Android framework, AppCompat, and Material Design views.
         * This pre-registration significantly improves performance by avoiding reflection
         * for commonly used views during layout inflation.
         */
        init {
            registerDefaultViews()
        }

        /**
         * Registers all default Android views with their corresponding creators.
         * This method is called during [ViewProcessor] initialization.
         * It includes a wide array of views from the Android framework, AppCompat library,
         * and Material Components library to ensure most common views are created efficiently
         * without reflection.
         *
         * The registration is broken down into categories for better organization.
         */
        private fun registerDefaultViews() {
            registerBasicViews()
            registerTextViews()
            registerButtonViews()
            registerLayoutViews()
            registerListViews()
            registerScrollViews()
            registerImageViews()
            registerMediaViews()
            registerCardViews()
            registerProgressViews()
            registerInputViews()
            registerMaterialInputViews()
            registerSelectionViews()
            registerSliderViews()
            registerMaterialComponents()
            registerNavigationViews()
            registerAppBarViews()
            registerPagerViews()
            registerTextFieldViews()
            registerLayoutContainerViews()
            registerSpecialViews()
        }

        // --- Start of categorized default view registration methods ---
        private fun registerBasicViews() {
            registerViewInternal("android.widget.View") { View(it) }
            registerViewInternal("android.widget.Space") { Space(it) }
        }

        private fun registerTextViews() {
            registerViewInternal("android.widget.TextView") { AppCompatTextView(it) }
            registerViewInternal("android.widget.EditText") { AppCompatEditText(it) }
            registerViewInternal("androidx.appcompat.widget.AppCompatTextView") { AppCompatTextView(it) }
            registerViewInternal("androidx.appcompat.widget.AppCompatEditText") { AppCompatEditText(it) }
        }

        private fun registerButtonViews() {
            registerViewInternal("android.widget.Button") { MaterialButton(it) } // Prefer MaterialButton
            registerViewInternal("android.widget.ImageButton") { AppCompatImageButton(it) }
            registerViewInternal("androidx.appcompat.widget.AppCompatButton") { AppCompatButton(it) }
            registerViewInternal("androidx.appcompat.widget.AppCompatImageButton") { AppCompatImageButton(it) }
            registerViewInternal("com.google.android.material.button.MaterialButton") { MaterialButton(it) }
            registerViewInternal("com.google.android.material.floatingactionbutton.FloatingActionButton") { FloatingActionButton(it) }
        }

        private fun registerLayoutViews() {
            registerViewInternal("android.widget.LinearLayout") { LinearLayout(it) }
            registerViewInternal("android.widget.FrameLayout") { FrameLayout(it) }
            registerViewInternal("android.widget.RelativeLayout") { RelativeLayout(it) }
            registerViewInternal("android.widget.TableLayout") { TableLayout(it) }
            registerViewInternal("android.widget.TableRow") { TableRow(it) }
            registerViewInternal("android.widget.GridLayout") { GridLayout(it) }
            registerViewInternal("androidx.constraintlayout.widget.ConstraintLayout") { ConstraintLayout(it) }
            registerViewInternal("androidx.coordinatorlayout.widget.CoordinatorLayout") { CoordinatorLayout(it) }
        }

        private fun registerListViews() {
            registerViewInternal("androidx.recyclerview.widget.RecyclerView") { RecyclerView(it) }
            registerViewInternal("android.widget.ListView") { ListView(it) }
            registerViewInternal("android.widget.GridView") { GridView(it) }
            registerViewInternal("android.widget.ExpandableListView") { ExpandableListView(it) }
        }

        private fun registerScrollViews() {
            registerViewInternal("android.widget.ScrollView") { ScrollView(it) }
            registerViewInternal("android.widget.HorizontalScrollView") { HorizontalScrollView(it) }
            registerViewInternal("androidx.core.widget.NestedScrollView") { NestedScrollView(it) }
        }

        private fun registerImageViews() {
            registerViewInternal("android.widget.ImageView") { AppCompatImageView(it) }
            registerViewInternal("androidx.appcompat.widget.AppCompatImageView") { AppCompatImageView(it) }
            registerViewInternal("com.google.android.material.imageview.ShapeableImageView") { ShapeableImageView(it) }
        }

        private fun registerMediaViews() {
            registerViewInternal("android.widget.VideoView") { VideoView(it) }
            registerViewInternal("android.view.SurfaceView") { SurfaceView(it) }
            registerViewInternal("android.view.TextureView") { TextureView(it) }
        }

        private fun registerCardViews() {
            registerViewInternal("androidx.cardview.widget.CardView") { CardView(it) }
            registerViewInternal("com.google.android.material.card.MaterialCardView") { MaterialCardView(it) }
        }

        private fun registerProgressViews() {
            registerViewInternal("android.widget.ProgressBar") { ProgressBar(it) }
            registerViewInternal("com.google.android.material.progressindicator.CircularProgressIndicator") { CircularProgressIndicator(it) }
            registerViewInternal("com.google.android.material.progressindicator.LinearProgressIndicator") { LinearProgressIndicator(it) }
        }

        private fun registerInputViews() {
            registerViewInternal("android.widget.Switch") { SwitchCompat(it) } // Prefer SwitchCompat
            registerViewInternal("androidx.appcompat.widget.SwitchCompat") { SwitchCompat(it) }
            registerViewInternal("android.widget.CheckBox") { AppCompatCheckBox(it) } // Prefer AppCompatCheckBox
            registerViewInternal("androidx.appcompat.widget.AppCompatCheckBox") { AppCompatCheckBox(it) }
            registerViewInternal("android.widget.RadioButton") { AppCompatRadioButton(it) } // Prefer AppCompatRadioButton
            registerViewInternal("androidx.appcompat.widget.AppCompatRadioButton") { AppCompatRadioButton(it) }
        }

        private fun registerMaterialInputViews() {
            registerViewInternal("com.google.android.material.switchmaterial.SwitchMaterial") { SwitchMaterial(it) }
            registerViewInternal("com.google.android.material.checkbox.MaterialCheckBox") { MaterialCheckBox(it) }
            registerViewInternal("com.google.android.material.radiobutton.MaterialRadioButton") { MaterialRadioButton(it) }
        }

        private fun registerSelectionViews() {
            registerViewInternal("android.widget.Spinner") { AppCompatSpinner(it) } // Prefer AppCompatSpinner
            registerViewInternal("androidx.appcompat.widget.AppCompatSpinner") { AppCompatSpinner(it) }
            registerViewInternal("android.widget.AutoCompleteTextView") { AppCompatAutoCompleteTextView(it) } // Prefer AppCompat
            registerViewInternal("android.widget.MultiAutoCompleteTextView") { MultiAutoCompleteTextView(it) } // Base class, consider AppCompat if available for this
            registerViewInternal("androidx.appcompat.widget.AppCompatAutoCompleteTextView") { AppCompatAutoCompleteTextView(it) }
        }

        private fun registerSliderViews() {
            registerViewInternal("android.widget.SeekBar") { AppCompatSeekBar(it) } // Prefer AppCompatSeekBar
            registerViewInternal("androidx.appcompat.widget.AppCompatSeekBar") { AppCompatSeekBar(it) }
            registerViewInternal("com.google.android.material.slider.Slider") { Slider(it) }
            registerViewInternal("android.widget.RatingBar") { RatingBar(it) } // No direct AppCompat/Material, framework is fine
        }

        private fun registerMaterialComponents() {
            registerViewInternal("com.google.android.material.chip.Chip") { Chip(it) }
            registerViewInternal("com.google.android.material.chip.ChipGroup") { ChipGroup(it) }
            registerViewInternal("com.google.android.material.tabs.TabLayout") { TabLayout(it) }
        }

        private fun registerNavigationViews() {
            registerViewInternal("android.widget.Toolbar") { Toolbar(it) } // AppCompat Toolbar is often used via XML
            registerViewInternal("androidx.appcompat.widget.Toolbar") { Toolbar(it) }
            registerViewInternal("com.google.android.material.appbar.MaterialToolbar") { MaterialToolbar(it) }
            registerViewInternal("com.google.android.material.bottomnavigation.BottomNavigationView") { BottomNavigationView(it) }
            registerViewInternal("com.google.android.material.navigation.NavigationView") { NavigationView(it) }
            registerViewInternal("com.google.android.material.navigationrail.NavigationRailView") { NavigationRailView(it) }
        }

        private fun registerAppBarViews() {
            registerViewInternal("com.google.android.material.appbar.AppBarLayout") { AppBarLayout(it) }
            registerViewInternal("com.google.android.material.appbar.CollapsingToolbarLayout") { CollapsingToolbarLayout(it) }
        }

        private fun registerPagerViews() {
            registerViewInternal("androidx.viewpager.widget.ViewPager") { ViewPager(it) }
            registerViewInternal("androidx.viewpager2.widget.ViewPager2") { ViewPager2(it) }
        }

        private fun registerTextFieldViews() {
            registerViewInternal("com.google.android.material.textfield.TextInputLayout") { TextInputLayout(it) }
            registerViewInternal("com.google.android.material.textfield.TextInputEditText") { TextInputEditText(it) }
        }

        private fun registerLayoutContainerViews() {
            registerViewInternal("androidx.drawerlayout.widget.DrawerLayout") { DrawerLayout(it) }
            registerViewInternal("androidx.slidingpanelayout.widget.SlidingPaneLayout") { SlidingPaneLayout(it) }
        }

        private fun registerSpecialViews() {
            // These might have specific construction needs or are less common in dynamic inflation.
            // Example: MaterialTimePicker usually inflated/shown via its Builder.
            registerViewInternal("com.google.android.material.timepicker.MaterialTimePicker") {
                // Note: This creates the picker dialog's view. Actual usage might differ.
                MaterialTimePicker.Builder().build().requireView()
            }
            // Example: Snackbar is typically made and shown, not inflated into a hierarchy directly.
            registerViewInternal("com.google.android.material.snackbar.Snackbar") {
                // This provides the Snackbar's view, which could be used if adding to a layout manually.
                Snackbar.make(View(it.applicationContext), "", Snackbar.LENGTH_SHORT).view
            }
        }
        // --- End of categorized default view registration methods ---

        /**
         * Registers a view creator function for a given fully qualified class name.
         * This is an internal helper primarily used by [registerDefaultViews] and
         * when caching reflected constructors in [createViewByType].
         *
         * @param className The fully qualified class name of the [View] to register.
         * @param creator A lambda function `(ContextThemeWrapper) -> View` that instantiates the view.
         */
        @JvmStatic
        private fun registerViewInternal(className: String, creator: (ContextThemeWrapper) -> View) {
            viewCreators[className] = creator
            if (logger.isLoggable(Level.CONFIG)) {
                logger.config("Registered view creator for: $className")
            }
        }

        /**
         * Registers a custom view creator function for a given package name and class name.
         *
         * This method is essential for integrating custom views with Voyager's dynamic inflation system
         * and achieving optimal performance. By pre-registering your custom views (e.g., during
         * application startup), you allow Voyager to instantiate them directly using the provided
         * [creator] lambda, bypassing the slower reflection-based instantiation that would otherwise occur.
         *
         * **Performance Note:**
         * Calling this method for all your custom views is highly recommended. When [createViewByType]
         * encounters a view type not found in its registry, it falls back to using reflection to find
         * and invoke the view's constructor. While the reflected constructor is then cached for
         * subsequent uses of that specific view type, the initial reflection is costly.
         * Pre-registration avoids this initial reflection overhead entirely.
         *
         * Example usage in your Application class or a DI module:
         * ```kotlin
         * ViewProcessor.registerView("com.example.myapp.customviews", "MyCustomButton") { context ->
         *     MyCustomButton(context)
         * }
         * ViewProcessor.registerView("com.example.myapp.customviews", "AnotherCustomLayout") { context ->
         *     AnotherCustomLayout(context, null) // If it has a (Context, AttributeSet?) constructor
         * }
         * ```
         *
         * @param packageName The package name of the custom view (e.g., "com.example.custom").
         * @param className The simple class name of the custom view (e.g., "MyCustomView").
         * @param creator A lambda function `(ContextThemeWrapper) -> View` that takes a themed context
         *                and returns an instance of the custom view.
         */
        @JvmStatic
        fun registerView(
            packageName: String,
            className: String,
            creator: (ContextThemeWrapper) -> View,
        ) {
            val fullyQualifiedName = "$packageName.$className"
            viewCreators[fullyQualifiedName] = creator // Use the internal registration method
            if (logger.isLoggable(Level.INFO)) { // Changed to INFO for custom registrations
                logger.info("Registered custom view creator: $fullyQualifiedName")
            }
        }

        /**
         * Checks if a view creator has been registered for the given package and class name.
         *
         * @param packageName The package name of the view.
         * @param className The simple class name of the view.
         * @return `true` if a creator is registered for this view, `false` otherwise.
         */
        @JvmStatic
        fun isRegistered(packageName: String, className: String): Boolean {
            val fullyQualifiedName = "$packageName.$className"
            return viewCreators.containsKey(fullyQualifiedName)
        }

        /**
         * Checks if a view creator has been registered for the given class path.
         * The class path can be a simple name (e.g., "TextView") or a fully qualified name.
         *
         * @param classPath The class path (simple or fully qualified) of the view.
         * @return `true` if a creator is registered for this view (after resolving to a fully qualified name),
         *         `false` otherwise.
         */
        internal fun isRegistered(classPath: String): Boolean {
            return viewCreators.containsKey(getFullQualifiedType(classPath))
        }

        /**
         * Creates a [View] instance for the given package and class name using a registered creator.
         *
         * This method attempts to find a pre-registered creator for the specified view.
         * It's suitable for scenarios where you know the view type should have been registered
         * (e.g., custom views you've explicitly registered via [registerViewInternal]).
         *
         * **Note:** This method does *not* fall back to reflection if the view is not found.
         * For creation that includes a reflection fallback, use [createViewByType].
         *
         * @param packageName The package name of the view (e.g., "com.example.custom").
         * @param className The simple class name of the view (e.g., "MyCustomView").
         * @param context The [ContextThemeWrapper] to be used for creating the view, ensuring it's
         *                correctly themed.
         * @return The created [View] instance if a creator was found and successfully invoked,
         *         or `null` if no creator was registered for the given view type.
         */
        @JvmStatic
        fun createView(
            packageName: String,
            className: String,
            context: ContextThemeWrapper,
        ): View? {
            val fullyQualifiedName = "$packageName.$className"
            return viewCreators[fullyQualifiedName]?.invoke(context)
        }

        /**
         * Creates a [View] instance for a given class path (simple or fully qualified) using a registered creator.
         *
         * This internal method resolves the `classPath` to a fully qualified name and then attempts
         * to find a pre-registered creator.
         *
         * **Note:** Similar to the public `createView` overload, this method does *not* fall back to reflection.
         * It's used internally when a direct lookup (after name resolution) is intended.
         *
         * @param classPath The class path of the view, which can be a simple name (e.g., "TextView")
         *                  or a fully qualified name (e.g., "android.widget.TextView").
         * @param context The [ContextThemeWrapper] for creating the view.
         * @return The created [View] instance if a creator was found, or `null` otherwise.
         */
        internal fun createView(classPath: String, context: ContextThemeWrapper): View? {
            val fullyQualifiedName = getFullQualifiedType(classPath)
            return viewCreators[fullyQualifiedName]?.invoke(context)
        }

        /**
         * Creates a [View] instance for the given view type string (e.g., "TextView", "com.example.CustomView").
         *
         * This is the primary method used by the inflation system ([DynamicLayoutInflation]) to create views.
         *
         * **Creation Logic:**
         * 1. Resolves the input `type` to a fully qualified class name.
         * 2. Checks if a creator for this fully qualified name is already registered in [viewCreators].
         *    If yes, it invokes the registered creator and returns the view.
         * 3. If not registered, it attempts to create the view using reflection via [createViewViaReflectionAndCache].
         * 4. If view creation fails, it logs the error and throws an [IllegalArgumentException].
         *
         * **Performance Implication:**
         * The reflection fallback can be slow. To optimize performance, especially for custom views,
         * it is highly recommended to pre-register view creators using [registerView].
         * Pre-registration bypasses the reflection mechanism entirely.
         *
         * @param context The [ContextThemeWrapper] to use for creating the view, ensuring correct theming.
         * @param type The type of the view to create. This can be a simple class name
         *             (e.g., "Button", which will be resolved to "android.widget.Button") or a
         *             fully qualified class name (e.g., "com.example.custom.MyCustomView").
         * @return The created [View] instance.
         * @throws IllegalArgumentException if the view cannot be created (e.g., class not found,
         *                                  no suitable constructor, or instantiation error).
         */
        internal fun createViewByType(context: ContextThemeWrapper, type: String): View {
            val fullyQualifiedName = getFullQualifiedType(type)

            // Attempt to find a pre-registered creator first
            viewCreators[fullyQualifiedName]?.let { creator ->
                return creator(context)
            }

            // If not registered, attempt to create via reflection and then cache the constructor
            return createViewViaReflectionAndCache(context, fullyQualifiedName)
        }

        /**
         * Attempts to create a [View] instance using reflection for the given fully qualified class name.
         * If successful, the reflected constructor is wrapped in a lambda and cached in [viewCreators]
         * for future use. This reduces reflection overhead for subsequent creations of the same view type.
         *
         * Logs a warning when reflection is used, encouraging pre-registration for better performance.
         *
         * @param context The [ContextThemeWrapper] for creating the view.
         * @param fullyQualifiedName The fully qualified name of the class to instantiate.
         * @return The created [View] instance.
         * @throws IllegalArgumentException if view creation via reflection fails (e.g., class not found,
         *                                  no suitable constructor, constructor invocation error).
         */
        private fun createViewViaReflectionAndCache(
            context: ContextThemeWrapper,
            fullyQualifiedName: String
        ): View {
            try {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(
                        "View type '$fullyQualifiedName' not pre-registered. " +
                                "Attempting reflection. For optimal performance, please pre-register this view type " +
                                "using ViewProcessor.registerView()."
                    )
                }
                val kClass = Class.forName(fullyQualifiedName).kotlin
                // Look for a constructor that takes a single Context argument.
                val ktor = kClass.constructors.firstOrNull { constructor ->
                    constructor.parameters.size == 1 &&
                            constructor.parameters[0].type.classifier == Context::class
                } ?: throw NoSuchMethodException(
                    "No constructor with a single Context parameter found for $fullyQualifiedName. " +
                            "Custom views must have a public constructor(Context) or constructor(Context, AttributeSet)."
                )

                // Create a lambda that calls the constructor
                val viewConstructor: (ContextThemeWrapper) -> View = { ctx ->
                    try {
                        ktor.call(ctx) as View // It's critical that the constructor returns a View
                    } catch (e: Exception) { // Catch errors during constructor invocation
                        logger.log(Level.SEVERE, "Failed to invoke constructor for $fullyQualifiedName via reflection.", e)
                        throw IllegalArgumentException("Failed to invoke constructor for $fullyQualifiedName.", e)
                    }
                }

                // Register (cache) this constructor lambda for future use
                registerViewInternal(fullyQualifiedName, viewConstructor)
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Successfully created and cached constructor for '$fullyQualifiedName' via reflection.")
                }
                // Call the newly created and cached constructor
                return viewConstructor(context)
            } catch (e: ClassNotFoundException) {
                logger.log(Level.SEVERE, "Class not found for view type: $fullyQualifiedName. Ensure the class name is correct and the class is included in the project.", e)
                throw IllegalArgumentException("Class not found for view type: $fullyQualifiedName", e)
            } catch (e: NoSuchMethodException) {
                logger.log(Level.SEVERE, "Suitable constructor not found for view type: $fullyQualifiedName. Ensure it has a public constructor that takes a Context.", e)
                throw IllegalArgumentException("Suitable constructor not found for view type: $fullyQualifiedName. Ensure it has a public constructor that takes a Context.", e)
            } catch (e: Exception) { // Catch any other reflection-related errors like security exceptions or invocation target exceptions
                logger.log(Level.SEVERE, "Error creating view via reflection for type: $fullyQualifiedName", e)
                throw IllegalArgumentException("Error creating view via reflection for type: $fullyQualifiedName. Details: ${e.message}", e)
            }
        }

        /**
         * Converts a potentially simple view type string (e.g., "TextView") into a fully qualified
         * class name (e.g., "android.widget.TextView"). If the input `type` already contains a '.',
         * it's assumed to be fully qualified and is returned as is.
         *
         * @param type The view type string to resolve.
         * @return The fully qualified class name for the view type.
         */
        private fun getFullQualifiedType(type: String): String =
            if (type.contains('.')) type else "$DEFAULT_ANDROID_WIDGET_PACKAGE$type"
    }
}