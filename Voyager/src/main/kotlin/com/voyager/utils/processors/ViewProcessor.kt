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
 * A high-performance view processor for the Voyager framework that handles dynamic view creation and attribute processing.
 *
 * This class provides a thread-safe, memory-efficient system for creating and configuring Android views
 * at runtime. It uses a registry pattern to manage view creators and optimizes view creation through
 * caching and lazy initialization.
 *
 * Key features:
 * - Thread-safe view creation and registration
 * - Memory-efficient view caching
 * - Optimized attribute processing
 * - Comprehensive theme resource handling
 * - Type-safe view creation
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class ViewProcessor {

    @SuppressLint("ShowToast")
    companion object {
        /**
         * Thread-safe map of view creators indexed by fully qualified class names.
         * Uses ConcurrentHashMap for thread safety and optimal performance.
         */
        private val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

        /**
         * Lazy-initialized logger to reduce startup overhead.
         */
        private val logger by lazy { Logger.getLogger(ViewProcessor::class.java.name) }

        private const val DEFAULT_ANDROID_WIDGET_PACKAGE = "android.widget."

        init {
            registerDefaultViews()
        }

        /**
         * Registers all default Android views with their corresponding creators.
         * Views are registered with their Material Design and AppCompat variants.
         */
        private fun registerDefaultViews() {
            // Basic views
            registerView("android.widget.View") { View(it) }
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

            // Special views
            registerView("com.google.android.material.timepicker.MaterialTimePicker") {
                MaterialTimePicker.Builder().build().requireView()
            }
            registerView("com.google.android.material.snackbar.Snackbar") {
                Snackbar.make(View(it), "", Snackbar.LENGTH_SHORT).view
            }
        }

        /**
         * Registers a view creator for a specific class name.
         *
         * @param className The fully qualified class name of the view
         * @param creator The function that creates the view instance
         */
        @JvmStatic
        private fun registerView(className: String, creator: (ContextThemeWrapper) -> View) {
            viewCreators.put(className, creator)
        }

        /**
         * Registers a view creator for a specific package and class name.
         *
         * @param packageName The package name
         * @param className The class name
         * @param creator The function that creates the view instance
         */
        @JvmStatic
        fun registerView(
            packageName: String,
            className: String,
            creator: (ContextThemeWrapper) -> View,
        ) {
            viewCreators.put("$packageName.$className", creator)
        }

        /**
         * Checks if a view is registered for the given package and class name.
         *
         * @param packageName The package name
         * @param className The class name
         * @return true if the view is registered, false otherwise
         */
        @JvmStatic
        fun isRegistered(packageName: String, className: String): Boolean =
            viewCreators.containsKey("$packageName.$className")

        /**
         * Checks if a view is registered for the given class path.
         *
         * @param classPath The class path to check
         * @return true if the view is registered, false otherwise
         */
        internal fun isRegistered(classPath: String): Boolean =
            viewCreators.containsKey(getFullQualifiedType(classPath))

        /**
         * Creates a view instance for the given package and class name.
         *
         * @param packageName The package name
         * @param className The class name
         * @param context The themed context wrapper
         * @return The created view instance, or null if creation fails
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
         * Creates a view instance for the given class path.
         *
         * @param classPath The class path
         * @param context The themed context wrapper
         * @return The created view instance, or null if creation fails
         */
        internal fun createView(classPath: String, context: ContextThemeWrapper): View? {
            val fullyQualifiedName = getFullQualifiedType(classPath)
            return viewCreators[fullyQualifiedName]?.invoke(context)
        }

        /**
         * Creates a view instance by type, with fallback to reflection if not registered.
         *
         * @param context The themed context wrapper
         * @param type The type of view to create
         * @return The created view instance
         * @throws IllegalArgumentException if view creation fails
         */
        internal fun createViewByType(context: ContextThemeWrapper, type: String): View {
            val fullyQualifiedName = getFullQualifiedType(type)

            return viewCreators[fullyQualifiedName]?.invoke(context) ?: try {
                val kClass = Class.forName(fullyQualifiedName).kotlin
                val ktor = kClass.constructors.firstOrNull { constructor ->
                    constructor.parameters.size == 1 && constructor.parameters[0].type.classifier == Context::class
                }
                    ?: throw IllegalArgumentException("No constructor with Context found for $fullyQualifiedName")

                val viewConstructor: (ContextThemeWrapper) -> View = { ctx ->
                    requireNotNull(ktor.call(ctx) as? View) { "View creation failed for type $fullyQualifiedName" }
                }

                registerView(fullyQualifiedName, viewConstructor)
                viewConstructor(context)
            } catch (e: Exception) {
                logger.severe("Error creating view for type: $fullyQualifiedName")
                throw IllegalArgumentException(
                    "Error creating view for type: $fullyQualifiedName", e
                )
            }
        }

        /**
         * Gets the fully qualified type name for a given type string.
         *
         * @param type The type string to process
         * @return The fully qualified type name
         */
        private fun getFullQualifiedType(type: String): String =
            if (type.contains(".")) type else "$DEFAULT_ANDROID_WIDGET_PACKAGE$type"
    }
}