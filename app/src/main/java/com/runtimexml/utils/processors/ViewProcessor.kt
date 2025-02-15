package com.runtimexml.utils.processors

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

    companion object {
        private val viewCreators: LinkedHashSet<Pair<String, (Context) -> View>> =
            linkedSetOf("View" to { View(it) },
                "Space" to { Space(it) },

                "TextView" to { TextView(it) },
                "EditText" to { EditText(it) },
                "androidx.appcompat.widget.AppCompatTextView" to { AppCompatTextView(it) },
                "androidx.appcompat.widget.AppCompatEditText" to { AppCompatEditText(it) },

                "Button" to { Button(it) },
                "ImageButton" to { ImageButton(it) },
                "androidx.appcompat.widget.AppCompatButton" to { AppCompatButton(it) },

                "com.google.android.material.button.MaterialButton" to { MaterialButton(it) },
                "com.google.android.material.floatingactionbutton.FloatingActionButton" to {
                    FloatingActionButton(it)
                },

                "LinearLayout" to { LinearLayout(it) },
                "FrameLayout" to { FrameLayout(it) },
                "RelativeLayout" to { RelativeLayout(it) },
                "TableLayout" to { TableLayout(it) },
                "TableRow" to { TableRow(it) },
                "GridLayout" to { GridLayout(it) },
                "androidx.constraintlayout.widget.ConstraintLayout" to { ConstraintLayout(it) },
                "androidx.coordinatorlayout.widget.CoordinatorLayout" to { CoordinatorLayout(it) },

                "RecyclerView" to { RecyclerView(it) },
                "ListView" to { ListView(it) },
                "GridView" to { GridView(it) },
                "ExpandableListView" to { ExpandableListView(it) },
                "ScrollView" to { ScrollView(it) },
                "HorizontalScrollView" to { HorizontalScrollView(it) },
                "NestedScrollView" to { NestedScrollView(it) },

                "ImageView" to { ImageView(it) },
                "androidx.appcompat.widget.AppCompatImageView" to { AppCompatImageView(it) },
                "com.google.android.material.imageview.ShapeableImageView" to {
                    ShapeableImageView(it)
                },
                "VideoView" to { VideoView(it) },
                "SurfaceView" to { SurfaceView(it) },
                "TextureView" to { TextureView(it) },

                "androidx.cardview.widget.CardView" to { CardView(it) },
                "com.google.android.material.card.MaterialCardView" to { MaterialCardView(it) },

                "ProgressBar" to { ProgressBar(it) },
                "com.google.android.material.progressindicator.CircularProgressIndicator" to {
                    CircularProgressIndicator(it)
                },
                "com.google.android.material.progressindicator.LinearProgressIndicator" to {
                    LinearProgressIndicator(it)
                },

                "Switch" to { Switch(it) },
                "androidx.appcompat.widget.SwitchCompat" to { SwitchCompat(it) },
                "CheckBox" to { CheckBox(it) },
                "androidx.appcompat.widget.AppCompatCheckBox" to { AppCompatCheckBox(it) },
                "RadioButton" to { RadioButton(it) },
                "androidx.appcompat.widget.AppCompatRadioButton" to { AppCompatRadioButton(it) },

                "com.google.android.material.switchmaterial.SwitchMaterial" to { SwitchMaterial(it) },
                "com.google.android.material.checkbox.MaterialCheckBox" to { MaterialCheckBox(it) },
                "com.google.android.material.radiobutton.MaterialRadioButton" to {
                    MaterialRadioButton(it)
                },

                "Spinner" to { Spinner(it) },
                "AutoCompleteTextView" to { AutoCompleteTextView(it) },
                "MultiAutoCompleteTextView" to { MultiAutoCompleteTextView(it) },
                "androidx.appcompat.widget.AppCompatAutoCompleteTextView" to {
                    AppCompatAutoCompleteTextView(it)
                },

                "SeekBar" to { SeekBar(it) },
                "androidx.appcompat.widget.AppCompatSeekBar" to { AppCompatSeekBar(it) },
                "com.google.android.material.slider.Slider" to { Slider(it) },
                "RatingBar" to { RatingBar(it) },

                "com.google.android.material.chip.Chip" to { Chip(it) },
                "com.google.android.material.chip.ChipGroup" to { ChipGroup(it) },
                "com.google.android.material.tabs.TabLayout" to { TabLayout(it) },

                "Toolbar" to { toolbar(it) },
                "androidx.appcompat.widget.Toolbar" to { Toolbar(it) },
                "com.google.android.material.appbar.MaterialToolbar" to { MaterialToolbar(it) },
                "com.google.android.material.bottomnavigation.BottomNavigationView" to {
                    BottomNavigationView(it)
                },
                "com.google.android.material.navigation.NavigationView" to { NavigationView(it) },

                "com.google.android.material.navigationrail.NavigationRailView" to {
                    NavigationRailView(
                        it
                    )
                },

                "com.google.android.material.appbar.AppBarLayout" to { AppBarLayout(it) },
                "com.google.android.material.appbar.CollapsingToolbarLayout" to {
                    CollapsingToolbarLayout(it)
                },

                "androidx.viewpager.widget.ViewPager" to { ViewPager(it) },
                "androidx.viewpager2.widget.ViewPager2" to { ViewPager2(it) },

                "com.google.android.material.textfield.TextInputLayout" to { TextInputLayout(it) },
                "com.google.android.material.textfield.TextInputEditText" to { TextInputEditText(it) },

                "androidx.drawerlayout.widget.DrawerLayout" to { DrawerLayout(it) },
                "androidx.slidingpanelayout.widget.SlidingPaneLayout" to { SlidingPaneLayout(it) },

                "com.google.android.material.timepicker.MaterialTimePicker" to {
                    MaterialTimePicker.Builder().build().requireView()
                },

                "com.google.android.material.snackbar.Snackbar" to {
                    Snackbar.make(View(it), "", Snackbar.LENGTH_SHORT).view
                })


        @JvmStatic
        fun <T> register(clazz: Class<T>, creator: (Context) -> View) {
            val className = clazz.simpleName
            viewCreators.add(className to creator)
        }

        fun isRegistered(className: String): Boolean {
            return viewCreators.any { it.first == className }
        }

        fun createViewIfExists(className: String, context: Context): View? {
            if (isRegistered(className)) return viewCreators.find { it.first == className }?.second?.invoke(context)
            return null
        }

        @JvmStatic
        fun createView(className: String, context: Context): View? =
            viewCreators.find{ it.first == className }?.second?.invoke(context)
    }
}