package com.flipkart.android.proteus.parser.custom

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.processor.*
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusTextView

/**
 * Kotlin implementation of TextViewParser, responsible for creating and configuring TextView views.
 *
 * This class extends ViewTypeParser and specializes in handling "TextView" view types within the Proteus framework.
 * It defines how TextView views are created, their type, parent type, and handles a wide range of text-related attributes
 * such as text content, styling, drawables, and more.
 *
 * @param T The type of TextView view this parser handles, must be a subclass of TextView.
 *           In the context of Proteus, this is likely `ProteusTextView`.
 */
class TextViewParser<T : TextView> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "TextView".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "TextView", representing the view type.
     */
    override fun getType(): String =
        "TextView" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the TextView view, which is "View".
     * This indicates that TextView inherits properties and behaviors from View in the Proteus framework.
     *
     * @return The string "View", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "View".
     */
    override fun getParentType(): String? =
        "View" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the TextView view (`ProteusTextView`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual TextView view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the TextView view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusTextView in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusTextView(context) // Creates and returns a new ProteusTextView instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to TextView.
     * This method registers processors for handling a wide variety of text styling and content related attributes.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'html' attribute (String - HTML formatted text) - using lambda and API level check
        addAttributeProcessor(Attributes.TextView.HTML,
            StringAttributeProcessor { view, value -> // Lambda for setting HTML text
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // API level check for Html.fromHtml(int, int)
                    view.text = Html.fromHtml(
                        value, Html.FROM_HTML_MODE_LEGACY
                    ) // Set HTML text using modern API
                } else {
                    @Suppress("DEPRECATION") // Suppress deprecation warning for older API usage
                    view.text =
                        Html.fromHtml(value) // Set HTML text using deprecated API for older versions
                }
            })

        // Attribute processor for 'text' attribute (String - plain text content) - using lambda
        addAttributeProcessor(Attributes.TextView.Text, StringAttributeProcessor { view, value ->
            view.text = value
        }) // Lambda for setting plain text

        // Attribute processor for 'drawablePadding' attribute (Dimension - padding between drawables and text) - using lambda
        addAttributeProcessor(Attributes.TextView.DrawablePadding,
            DimensionAttributeProcessor { view, dimension -> // Lambda for setting drawable padding
                view?.compoundDrawablePadding =
                    dimension.toInt() // Set compound drawable padding (int value of dimension)
            })

        // Attribute processor for 'textSize' attribute (Dimension - text size) - using lambda
        addAttributeProcessor(Attributes.TextView.TextSize,
            DimensionAttributeProcessor { view, dimension -> // Lambda for setting text size
                view?.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension) // Set text size in pixels
            })

        // Attribute processor for 'gravity' attribute (Gravity - text alignment and positioning) - using lambda
        addAttributeProcessor(Attributes.TextView.Gravity, object : GravityAttributeProcessor<T>() {
            override fun setGravity(view: T, @ProteusGravity gravity: Int) {
                view.gravity = gravity
            }
        })

        // Attribute processor for 'textColor' attribute (ColorResource - text color) - using lambda, handles both color int and ColorStateList
        addAttributeProcessor(Attributes.TextView.TextColor,
            object :
                ColorResourceProcessor<T>() { // Anonymous ColorResourceProcessor to handle both setColor methods
                override fun setColor(view: T, color: Int) { // Override setColor for int color
                    view.setTextColor(color) // Set text color using integer color value
                }

                override fun setColor(
                    view: T, colors: ColorStateList
                ) { // Override setColor for ColorStateList
                    view.setTextColor(colors) // Set text color using ColorStateList (for different states)
                }
            })

        // Attribute processor for 'textColorHint' attribute (ColorResource - hint text color) - using lambda, handles both color int and ColorStateList
        addAttributeProcessor(Attributes.TextView.TextColorHint,
            object :
                ColorResourceProcessor<T>() { // Anonymous ColorResourceProcessor for hint text color
                override fun setColor(view: T, color: Int) { // Override setColor for int color
                    view.setHintTextColor(color) // Set hint text color using integer color value
                }

                override fun setColor(
                    view: T, colors: ColorStateList
                ) { // Override setColor for ColorStateList
                    view.setHintTextColor(colors) // Set hint text color using ColorStateList
                }
            })

        // Attribute processor for 'textColorLink' attribute (ColorResource - link text color) - using lambda, handles both color int and ColorStateList
        addAttributeProcessor(Attributes.TextView.TextColorLink,
            object :
                ColorResourceProcessor<T>() { // Anonymous ColorResourceProcessor for link text color
                override fun setColor(view: T, color: Int) { // Override setColor for int color
                    view.setLinkTextColor(color) // Set link text color using integer color value
                }

                override fun setColor(
                    view: T, colors: ColorStateList
                ) { // Override setColor for ColorStateList
                    view.setLinkTextColor(colors) // Set link text color using ColorStateList
                }
            })

        // Attribute processor for 'textColorHighLight' attribute (ColorResource - highlight color) - using lambda, handles only color int
        addAttributeProcessor(Attributes.TextView.TextColorHighLight,
            object :
                ColorResourceProcessor<T>() { // Anonymous ColorResourceProcessor for highlight color
                override fun setColor(view: T, color: Int) { // Override setColor for int color
                    view.highlightColor = color // Set highlight color using integer color value
                }

                override fun setColor(
                    view: T, colors: ColorStateList
                ) { // Override setColor for ColorStateList - intentionally empty as per original code
                    // No action for ColorStateList in original Java code
                }
            })

        // Attribute processor for 'drawableLeft' attribute (DrawableResource - drawable on the left) - using lambda
        addAttributeProcessor(Attributes.TextView.DrawableLeft,
            DrawableResourceProcessor { view, drawable -> // Lambda for setting drawableLeft
                val compoundDrawables = view.compoundDrawables // Get existing compound drawables
                view.setCompoundDrawablesWithIntrinsicBounds(
                    drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]
                ) // Set left drawable, keeping others
            })

        // Attribute processor for 'drawableTop' attribute (DrawableResource - drawable on the top) - using lambda
        addAttributeProcessor(Attributes.TextView.DrawableTop,
            DrawableResourceProcessor { view, drawable -> // Lambda for setting drawableTop
                val compoundDrawables = view.compoundDrawables // Get existing compound drawables
                view.setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]
                ) // Set top drawable, keeping others
            })

        // Attribute processor for 'drawableRight' attribute (DrawableResource - drawable on the right) - using lambda
        addAttributeProcessor(Attributes.TextView.DrawableRight,
            DrawableResourceProcessor { view, drawable -> // Lambda for setting drawableRight
                val compoundDrawables = view.compoundDrawables // Get existing compound drawables
                view.setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]
                ) // Set right drawable, keeping others
            })

        // Attribute processor for 'drawableBottom' attribute (DrawableResource - drawable on the bottom) - using lambda
        addAttributeProcessor(Attributes.TextView.DrawableBottom,
            DrawableResourceProcessor { view, drawable -> // Lambda for setting drawableBottom
                val compoundDrawables = view.compoundDrawables // Get existing compound drawables
                view.setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], drawable
                ) // Set bottom drawable, keeping others
            })

        // Attribute processor for 'maxLines' attribute (String - integer for maximum lines) - using lambda
        addAttributeProcessor(Attributes.TextView.MaxLines,
            StringAttributeProcessor { view, value -> // Lambda for setting maxLines
                view.maxLines =
                    ParseHelper.parseInt(value) // Parse integer value for maxLines and set it
            })

        // Attribute processor for 'ellipsize' attribute (String - ellipsize mode) - using lambda
        addAttributeProcessor(Attributes.TextView.Ellipsize,
            StringAttributeProcessor { view, value -> // Lambda for setting ellipsize mode
                ParseHelper.parseEllipsize(value)
                    .let { ellipsize -> // Parse ellipsize string using ParseHelper, use let for null safety
                        view.ellipsize = ellipsize // Set ellipsize mode after casting
                    }
            })

        // Attribute processor for 'paintFlags' attribute (String "strike" for strike-through text) - using lambda
        addAttributeProcessor(Attributes.TextView.PaintFlags,
            StringAttributeProcessor { view, value -> // Lambda for setting paintFlags
                if (value == "strike") { // Check if value is "strike"
                    view.paintFlags =
                        view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Set strike-through flag
                }
            })

        // Attribute processor for 'prefix' attribute (String - text prefix) - using lambda
        addAttributeProcessor(Attributes.TextView.Prefix,
            StringAttributeProcessor { view, value -> // Lambda for setting prefix
                prependTextStringBuilder(view, value) // Prepend value to existing text
            })

        // Attribute processor for 'suffix' attribute (String - text suffix) - using lambda
        addAttributeProcessor(Attributes.TextView.Suffix,
            StringAttributeProcessor { view, value -> // Lambda for setting suffix
                appendTextStringBuilder(view, value) // Append value to existing text
            })

        // Attribute processor for 'textStyle' attribute (String - text style like "bold", "italic", etc.) - using lambda
        addAttributeProcessor(Attributes.TextView.TextStyle,
            StringAttributeProcessor { view, value -> // Lambda for setting textStyle
                val typeface =
                    ParseHelper.parseTextStyle(value) // Parse text style string using ParseHelper
                view.typeface =
                    Typeface.defaultFromStyle(typeface) // Set typeface based on parsed style
            })

        // Attribute processor for 'singleLine' attribute (Boolean - single line text) - using lambda
        addAttributeProcessor(
            Attributes.TextView.SingleLine,
            BooleanAttributeProcessor { view, value ->
                view.isSingleLine = value
            }) // Lambda for setting singleLine

        // Attribute processor for 'textAllCaps' attribute (Boolean - all caps text) - using lambda
        addAttributeProcessor(
            Attributes.TextView.TextAllCaps,
            BooleanAttributeProcessor { view, value ->
                view.isAllCaps = value
            }) // Lambda for setting textAllCaps

        // Attribute processor for 'hint' attribute (String - hint text) - using lambda
        addAttributeProcessor(Attributes.TextView.Hint, StringAttributeProcessor { view, value ->
            view.hint = value
        }) // Lambda for setting hint text
    }

    // Prepending with StringBuilder
    fun prependTextStringBuilder(view: TextView, value: Any?) {
        val currentText = view.text?.toString() ?: "" // Get current text safely
        val builder =
            StringBuilder(value.toString()) // Initialize StringBuilder with the value to prepend
        builder.append(currentText)                 // Append the current text
        view.text = builder.toString()             // Set the modified string back to the TextView
    }

    // Appending with StringBuilder
    fun appendTextStringBuilder(view: TextView, value: Any?) {
        val currentText = view.text?.toString() ?: "" // Get current text safely
        val builder =
            StringBuilder(currentText)     // Initialize StringBuilder with the current text
        builder.append(value.toString())             // Append the new value
        view.text = builder.toString()             // Set the modified string back to the TextView
    }
}