package com.voyager.utils.processors

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
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
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.ColorInt
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

class ViewProcessor {

    @SuppressLint("ShowToast")
    companion object {
        private val viewCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

        init {
            registerDefaultViews() // Register default Android views
        }

        private fun registerDefaultViews() {
            registerView("android.widget.View") { View(it) }
            registerView("android.widget.Space") { Space(it) }

            //handle for creating the views that has a class in the Material and the appCompat and android widgets
            registerView("android.widget.TextView") { AppCompatTextView(it) }
            registerView("android.widget.EditText") { AppCompatEditText(it) }
            registerView("androidx.appcompat.widget.AppCompatTextView") { AppCompatTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatEditText") { AppCompatEditText(it) }
            registerView("android.widget.Button") { MaterialButton(it) }
            registerView("android.widget.ImageButton") { AppCompatImageButton(it) }
            registerView("androidx.appcompat.widget.AppCompatButton") { AppCompatButton(it) }
            registerView("androidx.appcompat.widget.AppCompatImageButton") { AppCompatImageButton(it) }

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

            registerView("android.widget.ImageView") { AppCompatImageView(it) }
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

            registerView("android.widget.Switch") { SwitchCompat(it) }
            registerView("androidx.appcompat.widget.SwitchCompat") { SwitchCompat(it) }
            registerView("android.widget.CheckBox") { AppCompatCheckBox(it) }
            registerView("androidx.appcompat.widget.AppCompatCheckBox") { AppCompatCheckBox(it) }
            registerView("android.widget.RadioButton") { AppCompatRadioButton(it) }
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

            registerView("android.widget.Spinner") { AppCompatSpinner(it) }
            registerView("androidx.appcompat.widget.AppCompatSpinner") { AppCompatSpinner(it) }
            registerView("android.widget.AutoCompleteTextView") { AutoCompleteTextView(it) }
            registerView("android.widget.MultiAutoCompleteTextView") { MultiAutoCompleteTextView(it) }
            registerView("androidx.appcompat.widget.AppCompatAutoCompleteTextView") {
                AppCompatAutoCompleteTextView(
                    it
                )
            }

            registerView("android.widget.SeekBar") { AppCompatSeekBar(it) }
            registerView("androidx.appcompat.widget.AppCompatSeekBar") { AppCompatSeekBar(it) }
            registerView("com.google.android.material.slider.Slider") { Slider(it) }
            registerView("android.widget.RatingBar") { RatingBar(it) }

            registerView("com.google.android.material.chip.Chip") { Chip(it) }
            registerView("com.google.android.material.chip.ChipGroup") { ChipGroup(it) }
            registerView("com.google.android.material.tabs.TabLayout") { TabLayout(it) }

            registerView("android.widget.Toolbar") { Toolbar(it) }
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
        private fun registerView(className: String, creator: (ContextThemeWrapper) -> View) {
            viewCreators.put(className, creator)
        }

        @JvmStatic
        fun registerView(
            packageName: String, className: String, creator: (ContextThemeWrapper) -> View,
        ) {
            viewCreators.put("$packageName.$className", creator)
        }

        @JvmStatic
        fun isRegistered(packageName: String, className: String): Boolean =
            viewCreators.containsKey("$packageName.$className")

        internal fun isRegistered(classPath: String): Boolean =
            viewCreators.containsKey(getFullQualifiedType(classPath))

        @JvmStatic
        fun createView(
            packageName: String, className: String, context: ContextThemeWrapper,
        ): View? {
            val v = viewCreators["$packageName.$className"]?.let { it(context) }
            Log.d("ThemeCheck", v?.context?.theme.toString())
            return v
        }


        //handle if the classPath is include and fragment and data and requestFocus and binding and databinding and viewModel and tag and checkable and gesture and keyFrame and Preference and transition and view and include-marco
        internal fun createView(classPath: String, context: ContextThemeWrapper): View? {
            val v = viewCreators[getFullQualifiedType(classPath)]?.let { it(context) }
            Log.d("ThemeCheck", v?.context?.theme.toString())
            return v
        }

        internal fun createViewByType(context: ContextThemeWrapper, type: String): View {
            val fullyQualifiedName = getFullQualifiedType(type)

            val vv = viewCreators[fullyQualifiedName]?.let { constructor ->
                constructor(context)
            }
            logViewThemeResources(vv)
            if (vv != null) return vv


            return try {
                val kClass = Class.forName(fullyQualifiedName).kotlin

                val ktor = kClass.constructors.firstOrNull { constructor ->
                    constructor.parameters.size == 1 && constructor.parameters[0].type.classifier == Context::class
                }
                    ?: throw IllegalArgumentException("No constructor with Context found for $fullyQualifiedName")

                val viewConstructor: (ContextThemeWrapper) -> View = { ctx ->
                    requireNotNull(ktor.call(ctx) as? View) { "View creation failed for type $fullyQualifiedName" }
                }

                registerView(fullyQualifiedName, viewConstructor)
                val v = viewConstructor(context)
                Log.d("ThemeCheck", v.context.theme.toString())
                v
            } catch (e: Exception) {
                Log.e("ViewFactory", "Error creating view for type: $fullyQualifiedName", e)
                throw IllegalArgumentException(
                    "Error creating view for type: $fullyQualifiedName", e
                )
            }
        }

        private fun getFullQualifiedType(type: String): String =
            if (type.contains(".")) type else "android.widget.$type"

        @ColorInt
        fun Context.getColorFromAttr(attr: Int): Int {
            val typedValue = TypedValue()
            theme.resolveAttribute(attr, typedValue, true)
            return typedValue.data
        }

        // Function to get and log the theme resources of a view
        fun logViewThemeResources(view: View?) {
            if (view == null) return
            val context = view.context

            // Get the theme resource ID
            val themeResourceId = context.resources.configuration.uiMode

            // Get text color (if the view supports it)
            val textColor = when (view) {
                is TextView -> view.currentTextColor
                else -> null
            }

            // Get primary color
            val primaryColor = context.getColorFromAttr(android.R.attr.colorPrimary)

            // Get background color
            val backgroundColor = (view.background as? ColorDrawable)?.color

            // Create a string to hold the list of all attributes
            val styleAttributes = StringBuilder()

            // Attributes to check for
            val attributes = intArrayOf(
                android.R.attr.textColor,
                android.R.attr.colorPrimary,
                android.R.attr.background,
                android.R.attr.textSize,
                android.R.attr.fontFamily,
                android.R.attr.colorAccent,
                android.R.attr.colorControlNormal,
                android.R.attr.colorControlActivated,
                android.R.attr.colorButtonNormal,
                android.R.attr.buttonStyle,
                android.R.attr.editTextStyle,
                android.R.attr.spinnerStyle,
                android.R.attr.buttonStyleSmall,
                android.R.attr.colorPrimaryDark,
                android.R.attr.actionModeBackground,
                android.R.attr.actionModeCloseDrawable,
                android.R.attr.alertDialogTheme,
                android.R.attr.windowBackground
            )

            // Loop through the attributes and resolve their values
            for (attr in attributes) {
                val typedValue = TypedValue()
                val resolved = context.theme.resolveAttribute(attr, typedValue, true)

                if (resolved) {
                    // Check the type of the attribute and handle accordingly
                    when (typedValue.type) {
                        TypedValue.TYPE_STRING -> styleAttributes.append("Attribute(${attr}): ${typedValue.string}, ")
                        TypedValue.TYPE_DIMENSION -> styleAttributes.append("Attribute(${attr}): ${typedValue.getDimension(context.resources.displayMetrics)}, ")
                        TypedValue.TYPE_FLOAT -> styleAttributes.append("Attribute(${attr}): ${typedValue.float}, ")
                        TypedValue.TYPE_INT_COLOR_ARGB8, TypedValue.TYPE_INT_COLOR_RGB8, TypedValue.TYPE_INT_COLOR_ARGB4, TypedValue.TYPE_INT_COLOR_RGB4 ->
                            styleAttributes.append("Attribute(${attr}): ${typedValue.data}, ")
                        else -> styleAttributes.append("Attribute(${attr}): Unknown type, ")
                    }
                }
            }

            // Log all the attributes and the view information
            Log.d("Theme", "View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes")
            println("View: ${view::class.java.simpleName}, Text Color: $textColor, Primary Color: $primaryColor, Background Color: $backgroundColor, Theme Resource ID: $themeResourceId, Style Attributes: $styleAttributes")
        }
    }
}