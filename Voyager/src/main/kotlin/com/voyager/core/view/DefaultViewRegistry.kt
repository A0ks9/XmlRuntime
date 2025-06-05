package com.voyager.core.view

import android.annotation.SuppressLint
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry for managing default Android view creators in the Voyager framework.
 *
 * Key features:
 * - Default view registration
 * - Thread-safe view creation
 * - Efficient view lookup
 * - Comprehensive error handling
 * - Support for all major Android view types
 *
 * Performance optimizations:
 * - ConcurrentHashMap for thread-safe operations
 * - Efficient view creation
 * - Minimal object creation
 * - Fast view lookup
 * - Cached view creators
 *
 * Best practices:
 * - Use appropriate view types
 * - Handle view creation errors gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 *
 * Example usage:
 * ```kotlin
 * // Create a default view
 * val view = DefaultViewRegistry.createView(context, "android.widget.TextView")
 * ```
 */
internal object DefaultViewRegistry {
    private val logger = LoggerFactory.getLogger(DefaultViewRegistry::class.java.simpleName)
    val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

    init {
        try {
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
            logger.info("init", "Successfully registered all default view creators")
        } catch (e: Exception) {
            logger.error(
                "init", "Failed to register default view creators: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerBasicViews() {
        try {
            viewCreators["android.widget.View"] = { View(it) }
            viewCreators["android.widget.Space"] = { Space(it) }
        } catch (e: Exception) {
            logger.error(
                "registerBasicViews", "Failed to register basic views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerTextViews() {
        try {
            viewCreators["android.widget.TextView"] = { AppCompatTextView(it) }
            viewCreators["android.widget.EditText"] = { AppCompatEditText(it) }
            viewCreators["androidx.appcompat.widget.AppCompatTextView"] = { AppCompatTextView(it) }
            viewCreators["androidx.appcompat.widget.AppCompatEditText"] = { AppCompatEditText(it) }
        } catch (e: Exception) {
            logger.error(
                "registerTextViews", "Failed to register text views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerButtonViews() {
        try {
            viewCreators["android.widget.Button"] = { MaterialButton(it) }
            viewCreators["android.widget.ImageButton"] = { AppCompatImageButton(it) }
            viewCreators["androidx.appcompat.widget.AppCompatButton"] = { AppCompatButton(it) }
            viewCreators["androidx.appcompat.widget.AppCompatImageButton"] =
                { AppCompatImageButton(it) }
            viewCreators["com.google.android.material.button.MaterialButton"] =
                { MaterialButton(it) }
            viewCreators["com.google.android.material.floatingactionbutton.FloatingActionButton"] =
                { FloatingActionButton(it) }
            viewCreators["com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton"] =
                { ExtendedFloatingActionButton(it) }
        } catch (e: Exception) {
            logger.error(
                "registerButtonViews", "Failed to register button views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerLayoutViews() {
        try {
            viewCreators["android.widget.LinearLayout"] = { LinearLayout(it) }
            viewCreators["android.widget.FrameLayout"] = { FrameLayout(it) }
            viewCreators["android.widget.RelativeLayout"] = { RelativeLayout(it) }
            viewCreators["android.widget.TableLayout"] = { TableLayout(it) }
            viewCreators["android.widget.TableRow"] = { TableRow(it) }
            viewCreators["android.widget.GridLayout"] = { GridLayout(it) }
            viewCreators["androidx.constraintlayout.widget.ConstraintLayout"] =
                { ConstraintLayout(it) }
            viewCreators["androidx.coordinatorlayout.widget.CoordinatorLayout"] =
                { CoordinatorLayout(it) }
        } catch (e: Exception) {
            logger.error(
                "registerLayoutViews", "Failed to register layout views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerListViews() {
        try {
            viewCreators["androidx.recyclerview.widget.RecyclerView"] = { RecyclerView(it) }
            viewCreators["android.widget.ListView"] = { ListView(it) }
            viewCreators["android.widget.GridView"] = { GridView(it) }
            viewCreators["android.widget.ExpandableListView"] = { ExpandableListView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerListViews", "Failed to register list views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerScrollViews() {
        try {
            viewCreators["android.widget.ScrollView"] = { ScrollView(it) }
            viewCreators["android.widget.HorizontalScrollView"] = { HorizontalScrollView(it) }
            viewCreators["androidx.core.widget.NestedScrollView"] = { NestedScrollView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerScrollViews", "Failed to register scroll views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerImageViews() {
        try {
            viewCreators["android.widget.ImageView"] = { AppCompatImageView(it) }
            viewCreators["androidx.appcompat.widget.AppCompatImageView"] =
                { AppCompatImageView(it) }
            viewCreators["com.google.android.material.imageview.ShapeableImageView"] =
                { ShapeableImageView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerImageViews", "Failed to register image views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerMediaViews() {
        try {
            viewCreators["android.widget.VideoView"] = { VideoView(it) }
            viewCreators["android.view.SurfaceView"] = { SurfaceView(it) }
            viewCreators["android.view.TextureView"] = { TextureView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerMediaViews", "Failed to register media views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerCardViews() {
        try {
            viewCreators["androidx.cardview.widget.CardView"] = { CardView(it) }
            viewCreators["com.google.android.material.card.MaterialCardView"] =
                { MaterialCardView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerCardViews", "Failed to register card views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerProgressViews() {
        try {
            viewCreators["android.widget.ProgressBar"] = { ProgressBar(it) }
            viewCreators["com.google.android.material.progressindicator.CircularProgressIndicator"] =
                { CircularProgressIndicator(it) }
            viewCreators["com.google.android.material.progressindicator.LinearProgressIndicator"] =
                { LinearProgressIndicator(it) }
        } catch (e: Exception) {
            logger.error(
                "registerProgressViews", "Failed to register progress views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerInputViews() {
        try {
            viewCreators["android.widget.Switch"] = { SwitchCompat(it) }
            viewCreators["androidx.appcompat.widget.SwitchCompat"] = { SwitchCompat(it) }
            viewCreators["android.widget.CheckBox"] = { AppCompatCheckBox(it) }
            viewCreators["androidx.appcompat.widget.AppCompatCheckBox"] = { AppCompatCheckBox(it) }
            viewCreators["android.widget.RadioButton"] = { AppCompatRadioButton(it) }
            viewCreators["androidx.appcompat.widget.AppCompatRadioButton"] =
                { AppCompatRadioButton(it) }
        } catch (e: Exception) {
            logger.error(
                "registerInputViews", "Failed to register input views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerMaterialInputViews() {
        try {
            viewCreators["com.google.android.material.switchmaterial.SwitchMaterial"] =
                { SwitchMaterial(it) }
            viewCreators["com.google.android.material.checkbox.MaterialCheckBox"] =
                { MaterialCheckBox(it) }
            viewCreators["com.google.android.material.radiobutton.MaterialRadioButton"] =
                { MaterialRadioButton(it) }
        } catch (e: Exception) {
            logger.error(
                "registerMaterialInputViews",
                "Failed to register material input views: ${e.message}",
                e
            )
            throw e
        }
    }

    private fun registerSelectionViews() {
        try {
            viewCreators["android.widget.Spinner"] = { AppCompatSpinner(it) }
            viewCreators["androidx.appcompat.widget.AppCompatSpinner"] = { AppCompatSpinner(it) }
            viewCreators["android.widget.AutoCompleteTextView"] =
                { AppCompatAutoCompleteTextView(it) }
            viewCreators["android.widget.MultiAutoCompleteTextView"] =
                { MultiAutoCompleteTextView(it) }
            viewCreators["androidx.appcompat.widget.AppCompatAutoCompleteTextView"] =
                { AppCompatAutoCompleteTextView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerSelectionViews", "Failed to register selection views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerSliderViews() {
        try {
            viewCreators["android.widget.SeekBar"] = { AppCompatSeekBar(it) }
            viewCreators["androidx.appcompat.widget.AppCompatSeekBar"] = { AppCompatSeekBar(it) }
            viewCreators["com.google.android.material.slider.Slider"] = { Slider(it) }
            viewCreators["android.widget.RatingBar"] = { RatingBar(it) }
        } catch (e: Exception) {
            logger.error(
                "registerSliderViews", "Failed to register slider views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerMaterialComponents() {
        try {
            viewCreators["com.google.android.material.chip.Chip"] = { Chip(it) }
            viewCreators["com.google.android.material.chip.ChipGroup"] = { ChipGroup(it) }
            viewCreators["com.google.android.material.tabs.TabLayout"] = { TabLayout(it) }
        } catch (e: Exception) {
            logger.error(
                "registerMaterialComponents",
                "Failed to register material components: ${e.message}",
                e
            )
            throw e
        }
    }

    private fun registerNavigationViews() {
        try {
            viewCreators["android.widget.Toolbar"] = { Toolbar(it) }
            viewCreators["androidx.appcompat.widget.Toolbar"] = { Toolbar(it) }
            viewCreators["com.google.android.material.appbar.MaterialToolbar"] =
                { MaterialToolbar(it) }
            viewCreators["com.google.android.material.bottomnavigation.BottomNavigationView"] =
                { BottomNavigationView(it) }
            viewCreators["com.google.android.material.navigation.NavigationView"] =
                { NavigationView(it) }
            viewCreators["com.google.android.material.navigationrail.NavigationRailView"] =
                { NavigationRailView(it) }
        } catch (e: Exception) {
            logger.error(
                "registerNavigationViews", "Failed to register navigation views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerAppBarViews() {
        try {
            viewCreators["com.google.android.material.appbar.AppBarLayout"] = { AppBarLayout(it) }
            viewCreators["com.google.android.material.appbar.CollapsingToolbarLayout"] =
                { CollapsingToolbarLayout(it) }
        } catch (e: Exception) {
            logger.error(
                "registerAppBarViews", "Failed to register app bar views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerPagerViews() {
        try {
            viewCreators["androidx.viewpager.widget.ViewPager"] = { ViewPager(it) }
            viewCreators["androidx.viewpager2.widget.ViewPager2"] = { ViewPager2(it) }
        } catch (e: Exception) {
            logger.error(
                "registerPagerViews", "Failed to register pager views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerTextFieldViews() {
        try {
            viewCreators["com.google.android.material.textfield.TextInputLayout"] =
                { TextInputLayout(it) }
            viewCreators["com.google.android.material.textfield.TextInputEditText"] =
                { TextInputEditText(it) }
        } catch (e: Exception) {
            logger.error(
                "registerTextFieldViews", "Failed to register text field views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerLayoutContainerViews() {
        try {
            viewCreators["androidx.drawerlayout.widget.DrawerLayout"] = { DrawerLayout(it) }
            viewCreators["androidx.slidingpanelayout.widget.SlidingPaneLayout"] =
                { SlidingPaneLayout(it) }
        } catch (e: Exception) {
            logger.error(
                "registerLayoutContainerViews",
                "Failed to register layout container views: ${e.message}",
                e
            )
            throw e
        }
    }

    @SuppressLint("ShowToast")
    private fun registerSpecialViews() {
        try {
            viewCreators["com.google.android.material.timepicker.MaterialTimePicker"] =
                { MaterialTimePicker.Builder().build().requireView() }
            viewCreators["com.google.android.material.snackbar.Snackbar"] =
                { Snackbar.make(View(it.applicationContext), "", Snackbar.LENGTH_SHORT).view }
        } catch (e: Exception) {
            logger.error(
                "registerSpecialViews", "Failed to register special views: ${e.message}", e
            )
            throw e
        }
    }

    /**
     * Creates a view instance using a registered default view creator.
     *
     * Performance considerations:
     * - Efficient view lookup
     * - Thread-safe operation
     * - Minimal object creation
     *
     * Error handling:
     * - Safe view creation
     * - Graceful fallback for missing creators
     * - Proper logging
     *
     * @param context The context to create the view with
     * @param type The fully qualified class name of the view
     * @return The created view, or null if no creator is registered for the type
     */
    fun createView(context: ContextThemeWrapper, type: String): View? {
        try {
            return viewCreators[type]?.invoke(context)?.also {
                logger.info("createView", "Created default view of type: $type")
            }
        } catch (e: Exception) {
            logger.error(
                "createView", "Failed to create view of type $type: ${e.message}", e
            )
            return null
        }
    }
} 