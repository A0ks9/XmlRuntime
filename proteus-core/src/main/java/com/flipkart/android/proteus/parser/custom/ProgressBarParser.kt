package com.flipkart.android.proteus.parser.custom

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ProgressBar
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.processor.ColorResourceProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * A [ViewTypeParser] for parsing and creating [ProgressBar] views in a Proteus layout.
 *
 * @param T The type of ProgressBar this parser handles (e.g., [ProgressBar], [ProteusProgressBar]).
 */
class ProgressBarParser<T : ProgressBar> : ViewTypeParser<T>() {

    /**
     * Returns the type of view this parser handles.
     *
     * @return The view type as a string (e.g., "ProgressBar").
     */
    override fun getType(): String = "ProgressBar"

    /**
     * Returns the parent type of the view this parser handles.
     *
     * @return The parent view type as a string (e.g., "View").
     */
    override fun getParentType(): String? = "View"

    /**
     * Creates a new [ProteusProgressBar] instance for the given context, layout, and data.
     *
     * @param context The [ProteusContext] used to create the view.
     * @param layout The layout definition for the view.
     * @param data The data to bind to the view.
     * @param parent The parent view group, if any.
     * @param dataIndex The index of the data item being bound.
     * @return A new [ProteusProgressBar] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusProgressBar(context)

    /**
     * Adds custom attribute processors for the ProgressBar view.
     * This includes processors for max, progress, progress tint, and more.
     */
    override fun addAttributeProcessors() {
        // Processor for setting the max value
        addAttributeProcessor(Attributes.ProgressBar.Max, StringAttributeProcessor { view, value ->
            view.max = ParseHelper.parseDouble(value).toInt()
        })

        // Processor for setting the progress value
        addAttributeProcessor(Attributes.ProgressBar.Progress,
            StringAttributeProcessor { view, value ->
                view.progress = ParseHelper.parseDouble(value).toInt()
            })

        // Processor for setting the progress tint
        addAttributeProcessor(
            Attributes.ProgressBar.ProgressTint,
            object : AttributeProcessor<T>() {
                override fun handleValue(view: T?, value: Value) {
                    if (!value.isObject) return
                    val background =
                        value.asObject.asString("background")?.let { ParseHelper.parseColor(it) }
                            ?: Color.TRANSPARENT
                    val progress =
                        value.asObject.asString("progress")?.let { ParseHelper.parseColor(it) }
                            ?: Color.TRANSPARENT
                    view?.progressDrawable = getLayerDrawable(progress, background)
                }

                override fun handleResource(view: T?, resource: Resource) {
                    view?.progressDrawable = resource.getDrawable(view.context)
                }

                override fun handleAttributeResource(view: T?, attribute: AttributeResource) {
                    view?.progressDrawable = attribute.apply(view.context).getDrawable(0)
                }

                override fun handleStyleResource(view: T?, style: StyleResource) {
                    view?.progressDrawable = style.apply(view.context).getDrawable(0)
                }
            })

        // Processor for setting the secondary progress tint (API level 21+)
        addAttributeProcessor(Attributes.ProgressBar.SecondaryProgressTint,
            object : ColorResourceProcessor<T>() {
                override fun setColor(view: T, color: Int) {} // Not used

                override fun setColor(view: T, colors: ColorStateList) {
                    view.secondaryProgressTintList = colors
                }
            })

        // Processor for setting the indeterminate tint (API level 21+)
        addAttributeProcessor(Attributes.ProgressBar.IndeterminateTint,
            object : ColorResourceProcessor<T>() {
                override fun setColor(view: T, color: Int) {} // Not used

                override fun setColor(view: T, colors: ColorStateList) {
                    view.indeterminateTintList = colors
                }
            })
    }

    /**
     * Creates a [LayerDrawable] with the given progress and background colors.
     *
     * @param progress The color for the progress bar.
     * @param background The color for the background.
     * @return A [LayerDrawable] instance.
     */
    private fun getLayerDrawable(progress: Int, background: Int): Drawable {
        val backgroundShape = ShapeDrawable().apply {
            paint.style = Paint.Style.FILL
            paint.color = background
        }

        val progressShape = ShapeDrawable().apply {
            paint.style = Paint.Style.FILL
            paint.color = progress
        }

        val clipDrawable = ClipDrawable(progressShape, Gravity.START, ClipDrawable.HORIZONTAL)
        return LayerDrawable(arrayOf(backgroundShape, clipDrawable))
    }
}