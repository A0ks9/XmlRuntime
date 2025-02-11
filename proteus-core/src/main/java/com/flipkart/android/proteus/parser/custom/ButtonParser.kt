package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.Button
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusButton

/**
 * Kotlin implementation of ButtonParser, responsible for creating and configuring Button views.
 *
 * This class extends ViewTypeParser and specializes in handling "Button" view types within the Proteus framework.
 * It defines how Button views are created, their type, and their parent type in the view hierarchy.
 *
 * @param T The type of Button view this parser handles, must be a subclass of Button.
 *           In the context of Proteus, this is likely `ProteusButton`.
 */
class ButtonParser<T : Button> :
    ViewTypeParser<T>() { // Class declaration in Kotlin, inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "Button".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "Button", representing the view type.
     */
    override fun getType(): String { // Override getType() function, @NonNull annotation remains for non-nullable guarantee
        return "Button" // Returns the view type name
    }

    /**
     * Returns the parent type of the Button view, which is "TextView".
     * This indicates that Button inherits properties and behaviors from TextView in the Proteus framework.
     *
     * @return The string "TextView", representing the parent view type.
     *         Returns null if there's no explicit parent type (though in this case, TextView is the parent).
     */
    override fun getParentType(): String? { // Override getParentType(), using Kotlin's nullable String? for @Nullable
        return "TextView" // Returns the parent view type name
    }

    /**
     * Creates a new instance of the Button view (`ProteusButton`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual Button view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the Button view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusButton in this case.
     */
    override fun createView( // Override createView(), @NonNull annotation remains
        context: ProteusContext, // @NonNull annotations remain for parameters as well, if semantically important
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup? for @Nullable
        dataIndex: Int
    ): ProteusView {
        return ProteusButton(context) // Creates and returns a new ProteusButton instance
    }

    /**
     * This method is intended to be overridden to add attribute processors specific to the Button view.
     * Attribute processors handle the parsing and application of XML attributes to the created view.
     *
     * Currently, this method is empty, indicating that there are no specific attribute processors defined for Button in this implementation.
     * Subclasses of ButtonParser can override this method to add custom attribute handling logic.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() - remains empty as in Java
        // No specific attribute processors for Button in this base implementation.
        // Custom attribute processing for Button can be added by overriding this method in subclasses.
    }
}