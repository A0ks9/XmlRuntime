package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.ImageButton
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusImageButton


/**
 * Kotlin implementation of ImageButtonParser, responsible for creating and configuring ImageButton views.
 *
 * This class extends ViewTypeParser and specializes in handling "ImageButton" view types within the Proteus framework.
 * It defines how ImageButton views are created, their type, and their parent type in the view hierarchy.
 *
 * @param T The type of ImageButton view this parser handles, must be a subclass of ImageButton.
 *           In the context of Proteus, this is likely `ProteusImageButton`.
 */
class ImageButtonParser<T : ImageButton> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "ImageButton".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "ImageButton", representing the view type.
     */
    override fun getType(): String =
        "ImageButton" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the ImageButton view, which is "ImageView".
     * This indicates that ImageButton inherits properties and behaviors from ImageView in the Proteus framework.
     *
     * @return The string "ImageView", representing the parent view type.
     *         Returns null if there's no explicit parent type (though in this case, ImageView is the parent).
     */
    override fun getParentType(): String? =
        "ImageView" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the ImageButton view (`ProteusImageButton`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual ImageButton view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the ImageButton view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusImageButton in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusImageButton(context) // Creates and returns a new ProteusImageButton instance using expression body

    /**
     * This method is intended to be overridden to add attribute processors specific to the ImageButton view.
     * Attribute processors handle the parsing and application of XML attributes to the created view.
     *
     * Currently, this method is empty, indicating that there are no specific attribute processors defined for ImageButton in this implementation.
     * Subclasses of ImageButtonParser can override this method to add custom attribute handling logic.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() - remains empty as in Java
        // No specific attribute processors for ImageButton in this base implementation.
        // Custom attribute processing can be added by overriding this method in subclasses.
    }
}