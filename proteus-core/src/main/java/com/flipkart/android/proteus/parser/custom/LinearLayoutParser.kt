package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.widget.LinearLayout
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * A [ViewTypeParser] for parsing and creating [LinearLayout] views in a Proteus layout.
 *
 * @param T The type of LinearLayout this parser handles (e.g., [LinearLayout], [ProteusLinearLayout]).
 */
class LinearLayoutParser<T : LinearLayout> : ViewTypeParser<T>() {

    /**
     * Returns the type of view this parser handles.
     *
     * @return The view type as a string (e.g., "LinearLayout").
     */
    override fun getType(): String = "LinearLayout"

    /**
     * Returns the parent type of the view this parser handles.
     *
     * @return The parent view type as a string (e.g., "ViewGroup").
     */
    override fun getParentType(): String? = "ViewGroup"

    /**
     * Creates a new [ProteusLinearLayout] instance for the given context, layout, and data.
     *
     * @param context The [ProteusContext] used to create the view.
     * @param layout The layout definition for the view.
     * @param data The data to bind to the view.
     * @param parent The parent view group, if any.
     * @param dataIndex The index of the data item being bound.
     * @return A new [ProteusLinearLayout] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusLinearLayout(context)

    /**
     * Adds custom attribute processors for the LinearLayout view.
     * This includes processors for orientation, gravity, dividers, and weight sum.
     */
    override fun addAttributeProcessors() {
        // Processor for setting the orientation
        addAttributeProcessor(Attributes.LinearLayout.Orientation,
            StringAttributeProcessor { view, value ->
                view.orientation =
                    if ("horizontal" == value) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            })

        // Processor for setting the gravity
        addAttributeProcessor(Attributes.View.Gravity, GravityAttributeProcessor { view, gravity ->
            view.gravity = gravity
        })

        // Processor for setting the divider drawable (API level 11+)
        addAttributeProcessor(Attributes.LinearLayout.Divider,
            DrawableResourceProcessor { view, drawable ->
                view.dividerDrawable = drawable
            })

        // Processor for setting the divider padding (API level 11+)
        addAttributeProcessor(Attributes.LinearLayout.DividerPadding,
            DimensionAttributeProcessor { view, dimension ->
                view.dividerPadding = dimension.toInt()
            })

        // Processor for setting the show dividers mode (API level 11+)
        addAttributeProcessor(Attributes.LinearLayout.ShowDividers,
            StringAttributeProcessor { view, value ->
                view.showDividers = ParseHelper.parseDividerMode(value)
            })

        // Processor for setting the weight sum
        addAttributeProcessor(Attributes.LinearLayout.WeightSum,
            StringAttributeProcessor { view, value ->
                view.weightSum = ParseHelper.parseFloat(value)
            })
    }
}