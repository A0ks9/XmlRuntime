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
import java.util.concurrent.ConcurrentHashMap

internal object DefaultViewRegistry {
    val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

    init {
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

    private fun registerBasicViews() {
        viewCreators["android.widget.View"] = { View(it) }
        viewCreators["android.widget.Space"] = { Space(it) }
    }

    private fun registerTextViews() {
        viewCreators["android.widget.TextView"] = { AppCompatTextView(it) }
        viewCreators["android.widget.EditText"] = { AppCompatEditText(it) }
        viewCreators["androidx.appcompat.widget.AppCompatTextView"] = {
            AppCompatTextView(
                it
            )
        }
        viewCreators["androidx.appcompat.widget.AppCompatEditText"] = {
            AppCompatEditText(
                it
            )
        }
    }

    private fun registerButtonViews() {
        viewCreators["android.widget.Button"] = { MaterialButton(it) }
        viewCreators["android.widget.ImageButton"] = { AppCompatImageButton(it) }
        viewCreators["androidx.appcompat.widget.AppCompatButton"] = { AppCompatButton(it) }
        viewCreators["androidx.appcompat.widget.AppCompatImageButton"] = {
            AppCompatImageButton(
                it
            )
        }
        viewCreators["com.google.android.material.button.MaterialButton"] = {
            MaterialButton(
                it
            )
        }
        viewCreators["com.google.android.material.floatingactionbutton.FloatingActionButton"] = {
            FloatingActionButton(
                it
            )
        }
        viewCreators["com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton"] =
            {
                ExtendedFloatingActionButton(it)
            }
    }

    private fun registerLayoutViews() {
        viewCreators["android.widget.LinearLayout"] = { LinearLayout(it) }
        viewCreators["android.widget.FrameLayout"] = { FrameLayout(it) }
        viewCreators["android.widget.RelativeLayout"] = { RelativeLayout(it) }
        viewCreators["android.widget.TableLayout"] = { TableLayout(it) }
        viewCreators["android.widget.TableRow"] = { TableRow(it) }
        viewCreators["android.widget.GridLayout"] = { GridLayout(it) }
        viewCreators["androidx.constraintlayout.widget.ConstraintLayout"] = {
            ConstraintLayout(
                it
            )
        }
        viewCreators["androidx.coordinatorlayout.widget.CoordinatorLayout"] = {
            CoordinatorLayout(
                it
            )
        }
    }

    private fun registerListViews() {
        viewCreators["androidx.recyclerview.widget.RecyclerView"] = { RecyclerView(it) }
        viewCreators["android.widget.ListView"] = { ListView(it) }
        viewCreators["android.widget.GridView"] = { GridView(it) }
        viewCreators["android.widget.ExpandableListView"] = { ExpandableListView(it) }
    }

    private fun registerScrollViews() {
        viewCreators["android.widget.ScrollView"] = { ScrollView(it) }
        viewCreators["android.widget.HorizontalScrollView"] = { HorizontalScrollView(it) }
        viewCreators["androidx.core.widget.NestedScrollView"] = { NestedScrollView(it) }
    }

    private fun registerImageViews() {
        viewCreators["android.widget.ImageView"] = { AppCompatImageView(it) }
        viewCreators["androidx.appcompat.widget.AppCompatImageView"] = {
            AppCompatImageView(
                it
            )
        }
        viewCreators["com.google.android.material.imageview.ShapeableImageView"] = {
            ShapeableImageView(
                it
            )
        }
    }

    private fun registerMediaViews() {
        viewCreators["android.widget.VideoView"] = { VideoView(it) }
        viewCreators["android.view.SurfaceView"] = { SurfaceView(it) }
        viewCreators["android.view.TextureView"] = { TextureView(it) }
    }

    private fun registerCardViews() {
        viewCreators["androidx.cardview.widget.CardView"] = { CardView(it) }
        viewCreators["com.google.android.material.card.MaterialCardView"] = {
            MaterialCardView(
                it
            )
        }
    }

    private fun registerProgressViews() {
        viewCreators["android.widget.ProgressBar"] = { ProgressBar(it) }
        viewCreators["com.google.android.material.progressindicator.CircularProgressIndicator"] = {
            CircularProgressIndicator(
                it
            )
        }
        viewCreators["com.google.android.material.progressindicator.LinearProgressIndicator"] = {
            LinearProgressIndicator(
                it
            )
        }
    }

    private fun registerInputViews() {
        viewCreators["android.widget.Switch"] = { SwitchCompat(it) }
        viewCreators["androidx.appcompat.widget.SwitchCompat"] = { SwitchCompat(it) }
        viewCreators["android.widget.CheckBox"] = { AppCompatCheckBox(it) }
        viewCreators["androidx.appcompat.widget.AppCompatCheckBox"] = {
            AppCompatCheckBox(
                it
            )
        }
        viewCreators["android.widget.RadioButton"] = { AppCompatRadioButton(it) }
        viewCreators["androidx.appcompat.widget.AppCompatRadioButton"] = {
            AppCompatRadioButton(
                it
            )
        }
    }

    private fun registerMaterialInputViews() {
        viewCreators["com.google.android.material.switchmaterial.SwitchMaterial"] = {
            SwitchMaterial(
                it
            )
        }
        viewCreators["com.google.android.material.checkbox.MaterialCheckBox"] = {
            MaterialCheckBox(
                it
            )
        }
        viewCreators["com.google.android.material.radiobutton.MaterialRadioButton"] = {
            MaterialRadioButton(
                it
            )
        }
    }

    private fun registerSelectionViews() {
        viewCreators["android.widget.Spinner"] = { AppCompatSpinner(it) }
        viewCreators["androidx.appcompat.widget.AppCompatSpinner"] = { AppCompatSpinner(it) }
        viewCreators["android.widget.AutoCompleteTextView"] = {
            AppCompatAutoCompleteTextView(
                it
            )
        }
        viewCreators["android.widget.MultiAutoCompleteTextView"] = {
            MultiAutoCompleteTextView(
                it
            )
        }
        viewCreators["androidx.appcompat.widget.AppCompatAutoCompleteTextView"] = {
            AppCompatAutoCompleteTextView(
                it
            )
        }
    }

    private fun registerSliderViews() {
        viewCreators["android.widget.SeekBar"] = { AppCompatSeekBar(it) }
        viewCreators["androidx.appcompat.widget.AppCompatSeekBar"] = { AppCompatSeekBar(it) }
        viewCreators["com.google.android.material.slider.Slider"] = { Slider(it) }
        viewCreators["android.widget.RatingBar"] = { RatingBar(it) }
    }

    private fun registerMaterialComponents() {
        viewCreators["com.google.android.material.chip.Chip"] = { Chip(it) }
        viewCreators["com.google.android.material.chip.ChipGroup"] = { ChipGroup(it) }
        viewCreators["com.google.android.material.tabs.TabLayout"] = { TabLayout(it) }
    }

    private fun registerNavigationViews() {
        viewCreators["android.widget.Toolbar"] = { Toolbar(it) }
        viewCreators["androidx.appcompat.widget.Toolbar"] = { Toolbar(it) }
        viewCreators["com.google.android.material.appbar.MaterialToolbar"] = {
            MaterialToolbar(
                it
            )
        }
        viewCreators["com.google.android.material.bottomnavigation.BottomNavigationView"] = {
            BottomNavigationView(
                it
            )
        }
        viewCreators["com.google.android.material.navigation.NavigationView"] = {
            NavigationView(
                it
            )
        }
        viewCreators["com.google.android.material.navigationrail.NavigationRailView"] = {
            NavigationRailView(
                it
            )
        }
    }

    private fun registerAppBarViews() {
        viewCreators["com.google.android.material.appbar.AppBarLayout"] = {
            AppBarLayout(
                it
            )
        }
        viewCreators["com.google.android.material.appbar.CollapsingToolbarLayout"] = {
            CollapsingToolbarLayout(
                it
            )
        }
    }

    private fun registerPagerViews() {
        viewCreators["androidx.viewpager.widget.ViewPager"] = { ViewPager(it) }
        viewCreators["androidx.viewpager2.widget.ViewPager2"] = { ViewPager2(it) }
    }

    private fun registerTextFieldViews() {
        viewCreators["com.google.android.material.textfield.TextInputLayout"] = {
            TextInputLayout(
                it
            )
        }
        viewCreators["com.google.android.material.textfield.TextInputEditText"] = {
            TextInputEditText(
                it
            )
        }
    }

    private fun registerLayoutContainerViews() {
        viewCreators["androidx.drawerlayout.widget.DrawerLayout"] = { DrawerLayout(it) }
        viewCreators["androidx.slidingpanelayout.widget.SlidingPaneLayout"] = {
            SlidingPaneLayout(
                it
            )
        }
    }

    @SuppressLint("ShowToast")
    private fun registerSpecialViews() {
        viewCreators["com.google.android.material.timepicker.MaterialTimePicker"] = {
            MaterialTimePicker.Builder().build().requireView()
        }
        viewCreators["com.google.android.material.snackbar.Snackbar"] = {
            Snackbar.make(View(it.applicationContext), "", Snackbar.LENGTH_SHORT).view
        }
    }

    fun createView(context: ContextThemeWrapper, type: String): View? {
        return viewCreators[type]?.invoke(context)
    }
} 