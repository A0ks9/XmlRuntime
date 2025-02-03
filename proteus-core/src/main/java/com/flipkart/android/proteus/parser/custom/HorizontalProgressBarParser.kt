package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue


/**
 * Kotlin implementation of HorizontalProgressBarParser, responsible for creating and configuring HorizontalProgressBar views.
 *
 * This class extends ViewTypeParser and specializes in handling "HorizontalProgressBar" view types within the Proteus framework.
 * It defines how HorizontalProgressBar views are created, their type, and their parent type in the view hierarchy.
 *
 * @param T The type of HorizontalProgressBar view this parser handles, must be a subclass of HorizontalProgressBar.
 *           In the context of Proteus, this is likely `ProteusHorizontalProgressBar`.
 */
class HorizontalProgressBarParser<T : HorizontalProgressBar> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "HorizontalProgressBar".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "HorizontalProgressBar", representing the view type.
     */
    override fun getType(): String { // Override getType() function, removed @NonNull annotation
        return "HorizontalProgressBar" // Returns the view type name
    }

    /**
     * Returns the parent type of the HorizontalProgressBar view, which is "ProgressBar".
     * This indicates that HorizontalProgressBar inherits properties and behaviors from ProgressBar in the Proteus framework.
     *
     * @return The string "ProgressBar", representing the parent view type.
     *         Returns null if there's no explicit parent type (though in this case, ProgressBar is the parent).
     */
    override fun getParentType(): String? { // Override getParentType(), using Kotlin's nullable String?
        return "ProgressBar" // Returns the parent view type name
    }

    /**
     * Creates a new instance of the HorizontalProgressBar view (`ProteusHorizontalProgressBar`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual HorizontalProgressBar view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the HorizontalProgressBar view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusHorizontalProgressBar in this case.
     */
    override fun createView( // Override createView(), removed @NonNull annotation
        context: ProteusContext, // Removed @NonNull annotation from parameters
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup? , removed @Nullable annotation
        dataIndex: Int
    ): ProteusView {
        return ProteusHorizontalProgressBar(context) // Creates and returns a new ProteusHorizontalProgressBar instance
    }

    /**
     * This method is intended to be overridden to add attribute processors specific to the HorizontalProgressBar view.
     * Attribute processors handle the parsing and application of XML attributes to the created view.
     *
     * Currently, this method is empty, indicating that there are no specific attribute processors defined for HorizontalProgressBar in this implementation.
     * Subclasses of HorizontalProgressBarParser can override this method to add custom attribute handling logic.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() - remains empty as in Java
        // No specific attribute processors for HorizontalProgressBar in this base implementation.
        // Custom attribute processing can be added by overriding this method in subclasses.
    }
}