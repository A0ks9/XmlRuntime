package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.ImageView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.processor.DrawableResourceProcessor
import com.flipkart.android.proteus.processor.StringAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

package com.flipkart.android.proteus

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.flipkart.android.proteus.processor.DrawableResourceProcessor
import com.flipkart.android.proteus.processor.StringAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.toolbox.ParseHelper
import com.flipkart.android.proteus.value.ObjectValue

/**
 * Kotlin implementation of ImageViewParser, responsible for creating and configuring ImageView views.
 *
 * This class extends ViewTypeParser and specializes in handling "ImageView" view types within the Proteus framework.
 * It defines how ImageView views are created, their type, parent type, and handles specific attributes like
 * `src` (source drawable), `scaleType`, and `adjustViewBounds`.
 *
 * @param T The type of ImageView view this parser handles, must be a subclass of ImageView.
 *           In the context of Proteus, this is likely `ProteusImageView`.
 */
class ImageViewParser<T : ImageView> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "ImageView".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "ImageView", representing the view type.
     */
    override fun getType(): String =
        "ImageView" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the ImageView view, which is "View".
     * This indicates that ImageView inherits properties and behaviors from View in the Proteus framework.
     *
     * @return The string "View", representing the parent view type.
     *         Returns null if there's no explicit parent type (though in this case, View is the parent).
     */
    override fun getParentType(): String? =
        "View" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the ImageView view (`ProteusImageView`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual ImageView view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the ImageView view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusImageView in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusImageView(context) // Creates and returns a new ProteusImageView instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to ImageView.
     * This method registers processors for handling attributes like `src`, `scaleType`, and `adjustViewBounds`.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'src' attribute (Drawable resource) - using lambda
        addAttributeProcessor(Attributes.ImageView.Src,
            DrawableResourceProcessor { view, drawable -> view.setImageDrawable(drawable) }) // Lambda for setting image drawable

        // Attribute processor for 'scaleType' attribute (String) - using lambda
        addAttributeProcessor(Attributes.ImageView.ScaleType,
            StringAttributeProcessor { view, value -> // Lambda for setting scale type
                ParseHelper.parseScaleType(value)
                    ?.let { scaleType -> // Parse scale type string using ParseHelper
                        view.scaleType =
                            scaleType // Set scale type on ImageView if parsing is successful
                    }
            })

        // Attribute processor for 'adjustViewBounds' attribute (String "true" or "false") - using lambda
        addAttributeProcessor(Attributes.ImageView.AdjustViewBounds,
            StringAttributeProcessor { view, value -> // Lambda for setting adjustViewBounds
                view.adjustViewBounds =
                    value == "true" // Set adjustViewBounds based on string value ("true" or anything else)
            })
    }
}