package com.voyager.core.view

import android.annotation.SuppressLint
import android.util.AttributeSet
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
    val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper, AttributeSet) -> View>()

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
            viewCreators["android.widget.View"] = { ctx, attrs -> View(ctx, attrs) }
            viewCreators["android.widget.Space"] = { ctx, attrs -> Space(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerBasicViews", "Failed to register basic views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerTextViews() {
        try {
            viewCreators["android.widget.TextView"] =
                { ctx, attrs -> AppCompatTextView(ctx, attrs) }
            viewCreators["android.widget.EditText"] =
                { ctx, attrs -> AppCompatEditText(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatTextView"] =
                { ctx, attrs -> AppCompatTextView(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatEditText"] =
                { ctx, attrs -> AppCompatEditText(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerTextViews", "Failed to register text views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerButtonViews() {
        try {
            viewCreators["android.widget.Button"] = { ctx, attrs -> MaterialButton(ctx, attrs) }
            viewCreators["android.widget.ImageButton"] =
                { ctx, attrs -> AppCompatImageButton(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatButton"] =
                { ctx, attrs -> AppCompatButton(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatImageButton"] =
                { ctx, attrs -> AppCompatImageButton(ctx, attrs) }
            viewCreators["com.google.android.material.button.MaterialButton"] =
                { ctx, attrs -> MaterialButton(ctx, attrs) }
            viewCreators["com.google.android.material.floatingactionbutton.FloatingActionButton"] =
                { ctx, attrs -> FloatingActionButton(ctx, attrs) }
            viewCreators["com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton"] =
                { ctx, attrs -> ExtendedFloatingActionButton(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerButtonViews", "Failed to register button views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerLayoutViews() {
        try {
            viewCreators["android.widget.LinearLayout"] = { ctx, attrs -> LinearLayout(ctx, attrs) }
            viewCreators["android.widget.FrameLayout"] = { ctx, attrs -> FrameLayout(ctx, attrs) }
            viewCreators["android.widget.RelativeLayout"] =
                { ctx, attrs -> RelativeLayout(ctx, attrs) }
            viewCreators["android.widget.TableLayout"] = { ctx, attrs -> TableLayout(ctx, attrs) }
            viewCreators["android.widget.TableRow"] = { ctx, attrs -> TableRow(ctx, attrs) }
            viewCreators["android.widget.GridLayout"] = { ctx, attrs -> GridLayout(ctx, attrs) }
            viewCreators["androidx.constraintlayout.widget.ConstraintLayout"] =
                { ctx, attrs -> ConstraintLayout(ctx, attrs) }
            viewCreators["androidx.coordinatorlayout.widget.CoordinatorLayout"] =
                { ctx, attrs -> CoordinatorLayout(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerLayoutViews", "Failed to register layout views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerListViews() {
        try {
            viewCreators["androidx.recyclerview.widget.RecyclerView"] =
                { ctx, attrs -> RecyclerView(ctx, attrs) }
            viewCreators["android.widget.ListView"] = { ctx, attrs -> ListView(ctx, attrs) }
            viewCreators["android.widget.GridView"] = { ctx, attrs -> GridView(ctx, attrs) }
            viewCreators["android.widget.ExpandableListView"] =
                { ctx, attrs -> ExpandableListView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerListViews", "Failed to register list views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerScrollViews() {
        try {
            viewCreators["android.widget.ScrollView"] = { ctx, attrs -> ScrollView(ctx, attrs) }
            viewCreators["android.widget.HorizontalScrollView"] =
                { ctx, attrs -> HorizontalScrollView(ctx, attrs) }
            viewCreators["androidx.core.widget.NestedScrollView"] =
                { ctx, attrs -> NestedScrollView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerScrollViews", "Failed to register scroll views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerImageViews() {
        try {
            viewCreators["android.widget.ImageView"] =
                { ctx, attrs -> AppCompatImageView(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatImageView"] =
                { ctx, attrs -> AppCompatImageView(ctx, attrs) }
            viewCreators["com.google.android.material.imageview.ShapeableImageView"] =
                { ctx, attrs -> ShapeableImageView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerImageViews", "Failed to register image views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerMediaViews() {
        try {
            viewCreators["android.widget.VideoView"] = { ctx, attrs -> VideoView(ctx, attrs) }
            viewCreators["android.view.SurfaceView"] = { ctx, attrs -> SurfaceView(ctx, attrs) }
            viewCreators["android.view.TextureView"] = { ctx, attrs -> TextureView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerMediaViews", "Failed to register media views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerCardViews() {
        try {
            viewCreators["androidx.cardview.widget.CardView"] =
                { ctx, attrs -> CardView(ctx, attrs) }
            viewCreators["com.google.android.material.card.MaterialCardView"] =
                { ctx, attrs -> MaterialCardView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerCardViews", "Failed to register card views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerProgressViews() {
        try {
            viewCreators["android.widget.ProgressBar"] = { ctx, attrs -> ProgressBar(ctx, attrs) }
            viewCreators["com.google.android.material.progressindicator.CircularProgressIndicator"] =
                { ctx, attrs -> CircularProgressIndicator(ctx, attrs) }
            viewCreators["com.google.android.material.progressindicator.LinearProgressIndicator"] =
                { ctx, attrs -> LinearProgressIndicator(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerProgressViews", "Failed to register progress views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerInputViews() {
        try {
            viewCreators["android.widget.Switch"] = { ctx, attrs -> SwitchCompat(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.SwitchCompat"] =
                { ctx, attrs -> SwitchCompat(ctx, attrs) }
            viewCreators["android.widget.CheckBox"] =
                { ctx, attrs -> AppCompatCheckBox(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatCheckBox"] =
                { ctx, attrs -> AppCompatCheckBox(ctx, attrs) }
            viewCreators["android.widget.RadioButton"] =
                { ctx, attrs -> AppCompatRadioButton(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatRadioButton"] =
                { ctx, attrs -> AppCompatRadioButton(ctx, attrs) }
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
                { ctx, attrs -> SwitchMaterial(ctx, attrs) }
            viewCreators["com.google.android.material.checkbox.MaterialCheckBox"] =
                { ctx, attrs -> MaterialCheckBox(ctx, attrs) }
            viewCreators["com.google.android.material.radiobutton.MaterialRadioButton"] =
                { ctx, attrs -> MaterialRadioButton(ctx, attrs) }
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
            viewCreators["android.widget.Spinner"] = { ctx, attrs -> AppCompatSpinner(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatSpinner"] =
                { ctx, attrs -> AppCompatSpinner(ctx, attrs) }
            viewCreators["android.widget.AutoCompleteTextView"] =
                { ctx, attrs -> AppCompatAutoCompleteTextView(ctx, attrs) }
            viewCreators["android.widget.MultiAutoCompleteTextView"] =
                { ctx, attrs -> MultiAutoCompleteTextView(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatAutoCompleteTextView"] =
                { ctx, attrs -> AppCompatAutoCompleteTextView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerSelectionViews", "Failed to register selection views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerSliderViews() {
        try {
            viewCreators["android.widget.SeekBar"] = { ctx, attrs -> AppCompatSeekBar(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.AppCompatSeekBar"] =
                { ctx, attrs -> AppCompatSeekBar(ctx, attrs) }
            viewCreators["com.google.android.material.slider.Slider"] =
                { ctx, attrs -> Slider(ctx, attrs) }
            viewCreators["android.widget.RatingBar"] = { ctx, attrs -> RatingBar(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerSliderViews", "Failed to register slider views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerMaterialComponents() {
        try {
            viewCreators["com.google.android.material.chip.Chip"] =
                { ctx, attrs -> Chip(ctx, attrs) }
            viewCreators["com.google.android.material.chip.ChipGroup"] =
                { ctx, attrs -> ChipGroup(ctx, attrs) }
            viewCreators["com.google.android.material.tabs.TabLayout"] =
                { ctx, attrs -> TabLayout(ctx, attrs) }
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
            viewCreators["android.widget.Toolbar"] = { ctx, attrs -> Toolbar(ctx, attrs) }
            viewCreators["androidx.appcompat.widget.Toolbar"] =
                { ctx, attrs -> Toolbar(ctx, attrs) }
            viewCreators["com.google.android.material.appbar.MaterialToolbar"] =
                { ctx, attrs -> MaterialToolbar(ctx, attrs) }
            viewCreators["com.google.android.material.bottomnavigation.BottomNavigationView"] =
                { ctx, attrs -> BottomNavigationView(ctx, attrs) }
            viewCreators["com.google.android.material.navigation.NavigationView"] =
                { ctx, attrs -> NavigationView(ctx, attrs) }
            viewCreators["com.google.android.material.navigationrail.NavigationRailView"] =
                { ctx, attrs -> NavigationRailView(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerNavigationViews", "Failed to register navigation views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerAppBarViews() {
        try {
            viewCreators["com.google.android.material.appbar.AppBarLayout"] =
                { ctx, attrs -> AppBarLayout(ctx, attrs) }
            viewCreators["com.google.android.material.appbar.CollapsingToolbarLayout"] =
                { ctx, attrs -> CollapsingToolbarLayout(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerAppBarViews", "Failed to register app bar views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerPagerViews() {
        try {
            viewCreators["androidx.viewpager.widget.ViewPager"] =
                { ctx, attrs -> ViewPager(ctx, attrs) }
            viewCreators["androidx.viewpager2.widget.ViewPager2"] =
                { ctx, attrs -> ViewPager2(ctx, attrs) }
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
                { ctx, attrs -> TextInputLayout(ctx, attrs) }
            viewCreators["com.google.android.material.textfield.TextInputEditText"] =
                { ctx, attrs -> TextInputEditText(ctx, attrs) }
        } catch (e: Exception) {
            logger.error(
                "registerTextFieldViews", "Failed to register text field views: ${e.message}", e
            )
            throw e
        }
    }

    private fun registerLayoutContainerViews() {
        try {
            viewCreators["androidx.drawerlayout.widget.DrawerLayout"] =
                { ctx, attrs -> DrawerLayout(ctx, attrs) }
            viewCreators["androidx.slidingpanelayout.widget.SlidingPaneLayout"] =
                { ctx, attrs -> SlidingPaneLayout(ctx, attrs) }
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
                { _, _ -> MaterialTimePicker.Builder().build().requireView() }
            viewCreators["com.google.android.material.snackbar.Snackbar"] = { ctx, _ ->
                Snackbar.make(
                    View(ctx.applicationContext),
                    "",
                    Snackbar.LENGTH_SHORT
                ).view
            }
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
    fun createView(context: ContextThemeWrapper, attrs: AttributeSet, type: String): View? {
        try {
            return viewCreators[type]?.invoke(context, attrs)?.also {
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