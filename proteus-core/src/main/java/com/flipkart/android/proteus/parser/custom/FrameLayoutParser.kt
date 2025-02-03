package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * A [ViewTypeParser] for parsing and creating [AspectRatioFrameLayout] views in a Proteus layout.
 *
 * @param T The type of FrameLayout this parser handles (e.g., [AspectRatioFrameLayout]).
 */
class FrameLayoutParser<T : AspectRatioFrameLayout> : ViewTypeParser<T>() {

    /**
     * Returns the type of view this parser handles.
     *
     * @return The view type as a string (e.g., "FrameLayout").
     */
    override fun getType(): String = "FrameLayout"

    /**
     * Returns the parent type of the view this parser handles.
     *
     * @return The parent view type as a string (e.g., "ViewGroup").
     */
    override fun getParentType(): String? = "ViewGroup"

    /**
     * Creates a new [ProteusAspectRatioFrameLayout] instance for the given context, layout, and data.
     *
     * @param context The [ProteusContext] used to create the view.
     * @param layout The layout definition for the view.
     * @param data The data to bind to the view.
     * @param parent The parent view group, if any.
     * @param dataIndex The index of the data item being bound.
     * @return A new [ProteusAspectRatioFrameLayout] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusAspectRatioFrameLayout(context)

    /**
     * Adds custom attribute processors for the FrameLayout view.
     * This includes processors for height ratio and width ratio.
     */
    override fun addAttributeProcessors() {
        // Processor for setting the height ratio
        addAttributeProcessor(
            Attributes.FrameLayout.HeightRatio,
            StringAttributeProcessor { view, value ->
                view.setAspectRatioHeight(ParseHelper.parseInt(value))
            })

        // Processor for setting the width ratio
        addAttributeProcessor(
            Attributes.FrameLayout.WidthRatio,
            StringAttributeProcessor { view, value ->
                view.setAspectRatioWidth(ParseHelper.parseInt(value))
            })
    }
}