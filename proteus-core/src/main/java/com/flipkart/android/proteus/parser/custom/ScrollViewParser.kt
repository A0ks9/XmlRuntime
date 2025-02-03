package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.ScrollView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * Kotlin implementation of ScrollViewParser, responsible for creating and configuring ScrollView views.
 *
 * This class extends ViewTypeParser and specializes in handling "ScrollView" view types within the Proteus framework.
 * It defines how ScrollView views are created, their type, parent type, and handles specific attributes,
 * currently including `scrollbars`.
 *
 * @param T The type of ScrollView view this parser handles, must be a subclass of ScrollView.
 *           In the context of Proteus, this is likely `ProteusScrollView`.
 */
class ScrollViewParser<T : ScrollView> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "ScrollView".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "ScrollView", representing the view type.
     */
    override fun getType(): String =
        "ScrollView" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the ScrollView view, which is "FrameLayout".
     * This indicates that ScrollView inherits properties and behaviors from FrameLayout in the Proteus framework.
     *
     * @return The string "FrameLayout", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "FrameLayout".
     */
    override fun getParentType(): String? =
        "FrameLayout" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the ScrollView view (`ProteusScrollView`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual ScrollView view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the ScrollView view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusScrollView in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusScrollView(context) // Creates and returns a new ProteusScrollView instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to ScrollView.
     * This method registers processors for handling attributes, currently including `scrollbars`.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'scrollbars' attribute (String "none", "horizontal", "vertical", or default) - using lambda
        addAttributeProcessor(Attributes.ScrollView.Scrollbars,
            StringAttributeProcessor { view, value -> // Lambda using StringAttributeProcessor to handle scrollbars attribute
                when (value) { // Use when expression for cleaner conditional logic
                    "none" -> { // If value is "none", disable both horizontal and vertical scrollbars
                        view.isHorizontalScrollBarEnabled = false
                        view.isVerticalScrollBarEnabled = false
                    }

                    "horizontal" -> { // If value is "horizontal", enable horizontal scrollbar and disable vertical
                        view.isHorizontalScrollBarEnabled = true
                        view.isVerticalScrollBarEnabled = false
                    }

                    "vertical" -> { // If value is "vertical", disable horizontal scrollbar and enable vertical
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