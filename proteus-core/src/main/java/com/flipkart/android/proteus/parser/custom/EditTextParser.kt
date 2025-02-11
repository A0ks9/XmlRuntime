package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.EditText
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusEditText

/**
 * A [ViewTypeParser] for parsing and creating [EditText] views in a Proteus layout.
 *
 * @param T The type of EditText this parser handles (e.g., [EditText], [AppCompatEditText]).
 */
class EditTextParser<T : EditText> : ViewTypeParser<T>() {

    /**
     * Returns the type of view this parser handles.
     *
     * @return The view type as a string (e.g., "EditText").
     */
    override fun getType(): String = "EditText"

    /**
     * Returns the parent type of the view this parser handles.
     *
     * @return The parent view type as a string (e.g., "TextView").
     */
    override fun getParentType(): String? = "TextView"

    /**
     * Creates a new [ProteusEditText] instance for the given context, layout, and data.
     *
     * @param context The [ProteusContext] used to create the view.
     * @param layout The layout definition for the view.
     * @param data The data to bind to the view.
     * @param parent The parent view group, if any.
     * @param dataIndex The index of the data item being bound.
     * @return A new [ProteusEditText] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusEditText(context)

    /**
     * Adds custom attribute processors for the EditText view.
     * Override this method to add processors for custom attributes.
     */
    override fun addAttributeProcessors() {
        // No custom attribute processors by default.
    }
}