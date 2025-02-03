package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.CheckBox
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * A [ViewTypeParser] for parsing and creating [CheckBox] views in a Proteus layout.
 *
 * @param T The type of CheckBox this parser handles (e.g., [CheckBox], [AppCompatCheckBox]).
 */
class CheckBoxParser<T : CheckBox> : ViewTypeParser<T>() {

    /**
     * Returns the type of view this parser handles.
     *
     * @return The view type as a string (e.g., "CheckBox").
     */
    override fun getType(): String = "CheckBox"

    /**
     * Returns the parent type of the view this parser handles.
     *
     * @return The parent view type as a string (e.g., "Button").
     */
    override fun getParentType(): String? = "Button"

    /**
     * Creates a new [ProteusCheckBox] instance for the given context, layout, and data.
     *
     * @param context The [ProteusContext] used to create the view.
     * @param layout The layout definition for the view.
     * @param data The data to bind to the view.
     * @param parent The parent view group, if any.
     * @param dataIndex The index of the data item being bound.
     * @return A new [ProteusCheckBox] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusCheckBox(context)

    /**
     * Adds custom attribute processors for the CheckBox view.
     * This includes processors for the button drawable and checked state.
     */
    override fun addAttributeProcessors() {
        // Processor for setting the button drawable
        addAttributeProcessor(Attributes.CheckBox.Button,
            DrawableResourceProcessor { view, drawable ->
                view.buttonDrawable = drawable
            })

        // Processor for setting the checked state
        addAttributeProcessor(Attributes.CheckBox.Checked, StringAttributeProcessor { view, value ->
            view.isChecked = value.toBoolean()
        })
    }
}