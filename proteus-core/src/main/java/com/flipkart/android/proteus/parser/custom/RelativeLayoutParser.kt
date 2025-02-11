package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.RelativeLayout
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.processor.GravityAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusRelativeLayout

/**
 * Kotlin implementation of RelativeLayoutParser, responsible for creating and configuring RelativeLayout views.
 *
 * This class extends ViewTypeParser and specializes in handling "RelativeLayout" view types within the Proteus framework.
 * It defines how RelativeLayout views are created, their type, parent type, and handles specific attributes,
 * currently including `gravity`.
 *
 * @param T The type of RelativeLayout view this parser handles, must be a subclass of RelativeLayout.
 *           In the context of Proteus, this is likely `ProteusRelativeLayout`.
 */
class RelativeLayoutParser<T : RelativeLayout> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "RelativeLayout".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "RelativeLayout", representing the view type.
     */
    override fun getType(): String =
        "RelativeLayout" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the RelativeLayout view, which is "ViewGroup".
     * This indicates that RelativeLayout inherits properties and behaviors from ViewGroup in the Proteus framework.
     *
     * @return The string "ViewGroup", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "ViewGroup".
     */
    override fun getParentType(): String? =
        "ViewGroup" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the RelativeLayout view (`ProteusRelativeLayout`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual RelativeLayout view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the RelativeLayout view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusRelativeLayout in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusRelativeLayout(context) // Creates and returns a new ProteusRelativeLayout instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to RelativeLayout.
     * This method registers processors for handling attributes, currently including `gravity`.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'gravity' attribute (using GravityAttributeProcessor) - using lambda
        addAttributeProcessor(Attributes.View.Gravity, object : GravityAttributeProcessor<T>() {
            override fun setGravity(view: T, gravity: Int) {
                view.gravity = gravity
            }
        })
    }
}