package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.HorizontalScrollView
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor
import com.flipkart.android.proteus.processor.StringAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusHorizontalScrollView

/**
 * Kotlin implementation of HorizontalScrollViewParser, responsible for creating and configuring HorizontalScrollView views.
 *
 * This class extends ViewTypeParser and specializes in handling "HorizontalScrollView" view types within the Proteus framework.
 * It defines how HorizontalScrollView views are created, their type, parent type, and handles specific attributes like
 * `fillViewPort` and `scrollbars`.
 *
 * @param T The type of HorizontalScrollView view this parser handles, must be a subclass of HorizontalScrollView.
 *           In the context of Proteus, this is likely `ProteusHorizontalScrollView`.
 */
class HorizontalScrollViewParser<T : HorizontalScrollView> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "HorizontalScrollView".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "HorizontalScrollView", representing the view type.
     */
    override fun getType(): String { // Override getType() function, removed @NonNull annotation
        return "HorizontalScrollView" // Returns the view type name
    }

    /**
     * Returns the parent type of the HorizontalScrollView view, which is "FrameLayout".
     * This indicates that HorizontalScrollView inherits properties and behaviors from FrameLayout in the Proteus framework.
     *
     * @return The string "FrameLayout", representing the parent view type.
     *         Returns null if there's no explicit parent type (though in this case, FrameLayout is the parent).
     */
    override fun getParentType(): String? { // Override getParentType(), using Kotlin's nullable String?
        return "FrameLayout" // Returns the parent view type name
    }

    /**
     * Creates a new instance of the HorizontalScrollView view (`ProteusHorizontalScrollView`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual HorizontalScrollView view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the HorizontalScrollView view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusHorizontalScrollView in this case.
     */
    override fun createView( // Override createView(), removed @NonNull annotation
        context: ProteusContext, // Removed @NonNull annotation from parameters
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup? , removed @Nullable annotation
        dataIndex: Int
    ): ProteusView {
        return ProteusHorizontalScrollView(context) // Creates and returns a new ProteusHorizontalScrollView instance
    }

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to HorizontalScrollView.
     * This method registers processors for handling attributes like `fillViewPort` and `scrollbars`.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for handling the 'fillViewPort' attribute (boolean)
        addAttributeProcessor(Attributes.HorizontalScrollView.FillViewPort,
            BooleanAttributeProcessor<T> { view, value ->
                view.isFillViewport = value // Set fillViewport property of HorizontalScrollView
            })

        // Attribute processor for handling the 'scrollbars' attribute (string)
        addAttributeProcessor(Attributes.ScrollView.Scrollbars,
            StringAttributeProcessor<T> { view, value ->
                when (value) { // Use when for string matching scrollbar values
                    "none" -> { // If value is "none", disable both horizontal and vertical scrollbars
                        view.isHorizontalScrollBarEnabled = false
                        view.isVerticalScrollBarEnabled = false
                    }

                    "horizontal" -> { // If value is "horizontal", enable horizontal and disable vertical scrollbar
                        view.isHorizontalScrollBarEnabled = true
                        view.isVerticalScrollBarEnabled = false
                    }

                    "vertical" -> { // If value is "vertical", disable horizontal and enable vertical scrollbar (Note: Vertical scrollbar makes less sense for HorizontalScrollView)
                        view.isHorizontalScrollBarEnabled = false
                        view.isVerticalScrollBarEnabled = true
                    }

                    else -> { // Default case (or any other value), disable both scrollbars
                        view.isHorizontalScrollBarEnabled = false
                        view.isVerticalScrollBarEnabled = false
                    }
                }
            })
    }
}