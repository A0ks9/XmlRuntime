package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor
import com.flipkart.android.proteus.processor.DrawableResourceProcessor
import com.flipkart.android.proteus.processor.StringAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusFixedRatingBar
import com.flipkart.android.proteus.view.custom.FixedRatingBar

/**
 * Kotlin implementation of RatingBarParser, responsible for creating and configuring RatingBar views (specifically `FixedRatingBar`).
 *
 * This class extends ViewTypeParser and specializes in handling "RatingBar" view types within the Proteus framework.
 * It defines how RatingBar views are created, their type, parent type, and handles specific attributes like
 * `numStars`, `rating`, `isIndicator`, `stepSize`, `minHeight`, and `progressDrawable`.
 *
 * @param T The type of RatingBar view this parser handles, must be a subclass of `FixedRatingBar`.
 *           In the context of Proteus, this is likely `ProteusFixedRatingBar`.
 */
class RatingBarParser<T : FixedRatingBar> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "RatingBar".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "RatingBar", representing the view type.
     */
    override fun getType(): String =
        "RatingBar" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the RatingBar view, which is "View".
     * This indicates that RatingBar inherits properties and behaviors from View in the Proteus framework.
     *
     * @return The string "View", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "View".
     */
    override fun getParentType(): String? =
        "View" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the RatingBar view (`ProteusFixedRatingBar`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual RatingBar view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the RatingBar view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusFixedRatingBar in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusFixedRatingBar(context) // Creates and returns a new ProteusFixedRatingBar instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to RatingBar.
     * This method registers processors for handling attributes like `numStars`, `rating`, `isIndicator`, etc.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'numStars' attribute (String - parseInt) - using lambda
        addAttributeProcessor(Attributes.RatingBar.NumStars,
            StringAttributeProcessor { view, value -> // Lambda for setting numStars
                view.numStars =
                    ParseHelper.parseInt(value) // Parse integer value for numStars and set it
            })

        // Attribute processor for 'rating' attribute (String - parseFloat) - using lambda
        addAttributeProcessor(Attributes.RatingBar.Rating,
            StringAttributeProcessor { view, value -> // Lambda for setting rating
                view.rating =
                    ParseHelper.parseFloat(value) // Parse float value for rating and set it
            })

        // Attribute processor for 'isIndicator' attribute (Boolean) - using lambda
        addAttributeProcessor(Attributes.RatingBar.IsIndicator,
            BooleanAttributeProcessor { view, value -> // Lambda for setting isIndicator
                view.setIsIndicator(value) // Set isIndicator boolean value
            })

        // Attribute processor for 'stepSize' attribute (String - parseFloat) - using lambda
        addAttributeProcessor(Attributes.RatingBar.StepSize,
            StringAttributeProcessor { view, value -> // Lambda for setting stepSize
                view.stepSize =
                    ParseHelper.parseFloat(value) // Parse float value for stepSize and set it
            })

        // Attribute processor for 'minHeight' attribute (Dimension) - using lambda
        addAttributeProcessor(Attributes.RatingBar.MinHeight,
            DimensionAttributeProcessor { view, dimension -> // Lambda for setting minHeight
                view?.minimumHeight =
                    dimension.toInt() // Set minimumHeight (int value of dimension)
            })

        // Attribute processor for 'progressDrawable' attribute (Drawable resource) - using lambda and tiled drawable logic
        addAttributeProcessor(Attributes.RatingBar.ProgressDrawable,
            DrawableResourceProcessor { view, drawable -> // Lambda for setting progressDrawable
                var tiledDrawable =
                    view.getTiledDrawable(drawable, false) // Get tiled drawable using helper method
                view.progressDrawable = tiledDrawable // Set the tiled progress drawable
            })
    }
}