package com.runtimexml.utils.processors

import android.annotation.SuppressLint
import android.content.Context
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.MultiAutoCompleteTextView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Space
import android.widget.Spinner
import android.widget.Switch
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.collection.SparseArrayCompat
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
import android.widget.Toolbar as toolbar

class ViewProcessor {

    @SuppressLint("ShowToast")
    companion object {
        private val viewCreators = SparseArrayCompat<(Context) -> View>()

        init {
            registerView("android.widget.View") { View(it) }
            registerView("android.widget.Space") { Space(it) }

            registerView("android.widget.TextView") { TextView(it) }
            registerView("android.widget.EditText") { EditText(it) }
            registerView("androidx.appcompat.widget.AppCompatTextView") { AppCompatTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatEditText") { AppCompatEditText(it) }

            registerView("android.widget.Button") { Button(it) }
            registerView("android.widget.ImageButton") { ImageButton(it) }
            registerView("androidx.appcompat.widget.AppCompatButton") { AppCompatButton(it) }

            registerView("com.google.android.material.button.MaterialButton") { MaterialButton(it) }
            registerView("com.google.android.material.floatingactionbutton.FloatingActionButton") {
                FloatingActionButton(it)
            }

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

            registerView("androidx.recyclerview.widget.RecyclerView") { RecyclerView(it) }
            registerView("android.widget.ListView") { ListView(it) }
            registerView("android.widget.GridView") { GridView(it) }
            registerView("android.widget.ExpandableListView") { ExpandableListView(it) }
            registerView("android.widget.ScrollView") { ScrollView(it) }
            registerView("android.widget.HorizontalScrollView") { HorizontalScrollView(it) }
            registerView("androidx.core.widget.NestedScrollView") { NestedScrollView(it) }

            registerView("android.widget.ImageView") { ImageView(it) }
            registerView("androidx.appcompat.widget.AppCompatImageView") { AppCompatImageView(it) }
            registerView("com.google.android.material.imageview.ShapeableImageView") {
                ShapeableImageView(
                    it
                )
            }

            registerView("android.widget.VideoView") { VideoView(it) }
            registerView("android.view.SurfaceView") { SurfaceView(it) }
            registerView("android.view.TextureView") { TextureView(it) }

            registerView("androidx.cardview.widget.CardView") { CardView(it) }
            registerView("com.google.android.material.card.MaterialCardView") { MaterialCardView(it) }

            registerView("android.widget.ProgressBar") { ProgressBar(it) }
            registerView("com.google.android.material.progressindicator.CircularProgressIndicator") {
                CircularProgressIndicator(it)
            }
            registerView("com.google.android.material.progressindicator.LinearProgressIndicator") {
                LinearProgressIndicator(it)
            }

            registerView("android.widget.Switch") { Switch(it) }
            registerView("androidx.appcompat.widget.SwitchCompat") { SwitchCompat(it) }
            registerView("android.widget.CheckBox") { CheckBox(it) }
            registerView("androidx.appcompat.widget.AppCompatCheckBox") { AppCompatCheckBox(it) }
            registerView("android.widget.RadioButton") { RadioButton(it) }
            registerView("androidx.appcompat.widget.AppCompatRadioButton") { AppCompatRadioButton(it) }

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

            registerView("android.widget.Spinner") { Spinner(it) }
            registerView("android.widget.AutoCompleteTextView") { AutoCompleteTextView(it) }
            registerView("android.widget.MultiAutoCompleteTextView") { MultiAutoCompleteTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatAutoCompleteTextView") {
                AppCompatAutoCompleteTextView(
                    it
                )
            }

            registerView("android.widget.SeekBar") { SeekBar(it) }
            registerView("androidx.appcompat.widget.AppCompatSeekBar") { AppCompatSeekBar(it) }
            registerView("com.google.android.material.slider.Slider") { Slider(it) }
            registerView("android.widget.RatingBar") { RatingBar(it) }

            registerView("com.google.android.material.chip.Chip") { Chip(it) }
            registerView("com.google.android.material.chip.ChipGroup") { ChipGroup(it) }
            registerView("com.google.android.material.tabs.TabLayout") { TabLayout(it) }

            registerView("android.widget.Toolbar") { toolbar(it) }
            registerView("androidx.appcompat.widget.Toolbar") { Toolbar(it) }
            registerView("com.google.android.material.appbar.MaterialToolbar") { MaterialToolbar(it) }
            registerView("com.google.android.material.bottomnavigation.BottomNavigationView") {
                BottomNavigationView(
                    it
                )
            }
            registerView("com.google.android.material.navigation.NavigationView") {
                NavigationView(
                    it
                )
            }

            registerView("com.google.android.material.navigationrail.NavigationRailView") {
                NavigationRailView(
                    it
                )
            }

            registerView("com.google.android.material.appbar.AppBarLayout") { AppBarLayout(it) }
            registerView("com.google.android.material.appbar.CollapsingToolbarLayout") {
                CollapsingToolbarLayout(
                    it
                )
            }

            registerView("androidx.viewpager.widget.ViewPager") { ViewPager(it) }
            registerView("androidx.viewpager2.widget.ViewPager2") { ViewPager2(it) }

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

            registerView("androidx.drawerlayout.widget.DrawerLayout") { DrawerLayout(it) }
            registerView("androidx.slidingpanelayout.widget.SlidingPaneLayout") {
                SlidingPaneLayout(
                    it
                )
            }

            registerView("com.google.android.material.timepicker.MaterialTimePicker") {
                MaterialTimePicker.Builder().build().requireView()
            }

            registerView("com.google.android.material.snackbar.Snackbar") {
                Snackbar.make(View(it), "", Snackbar.LENGTH_SHORT).view
            }
        }

        @JvmStatic
        private fun registerView(className: String, creator: (Context) -> View) {
            viewCreators.put(className.hashCode(), creator)
        }

        @JvmStatic
        fun registerView(packageName: String, className: String, creator: (Context) -> View) {
            viewCreators.put("$packageName.$className".hashCode(), creator)
        }

        @JvmStatic
        fun isRegistered(packageName: String, className: String): Boolean =
            viewCreators.indexOfKey("$packageName.$className".hashCode()) >= 0

        internal fun isRegistered(classPath: String): Boolean =
            viewCreators.indexOfKey(classPath.hashCode()) >= 0

        @JvmStatic
        fun createView(packageName: String, className: String, context: Context): View? =
            viewCreators["$packageName.$className".hashCode()]?.invoke(context)

        internal fun createView(classPath: String, context: Context): View? =
            viewCreators[classPath.hashCode()]?.invoke(context)
    }
}