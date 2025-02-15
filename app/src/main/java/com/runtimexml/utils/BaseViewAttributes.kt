package com.runtimexml.utils

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.runtimexml.utils.DynamicLayoutInflation.getGeneratedViewInfo
import com.runtimexml.utils.ParseHelper.parseFloat
import com.runtimexml.utils.ParseHelper.parseGravity
import com.runtimexml.utils.ParseHelper.parseRelativeLayoutBoolean
import com.runtimexml.utils.interfaces.AttributeProcessorRegistry
import com.runtimexml.utils.processors.AttributeProcessor.Companion.addAttribute
import com.runtimexml.utils.processors.AttributeProcessor.Companion.attributeRegistry

internal object BaseViewAttributes {
    internal val processors = mutableMapOf<String, AttributeProcessorRegistry<Any>>()
    internal const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

    fun initializeAttributes() {
        attributeRegistry {
            addAttribute<String>(Attributes.Common.ID) { view, value ->
                view.getParentView()?.getGeneratedViewInfo()?.let { info ->
                    view.id =
                        View.generateViewId().also { info.viewID[value!!.extractViewId()] = it }
                }
            }

            // Handles width, height, margins, and paddings dynamically
            listOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attr ->
                addAttribute<String>(attr) { view, value ->
                    setSize(
                        view, value.toString(), isWidth = true
                    )
                }
            }
            listOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attr ->
                addAttribute<String>(attr) { view, value ->
                    setSize(
                        view, value.toString(), isWidth = false
                    )
                }
            }

            addAttribute<String>(Attributes.Common.GRAVITY) { view, value ->
                when (view.getParentView()) {
                    is LinearLayout -> (view.layoutParams as LinearLayout.LayoutParams).gravity =
                        parseGravity(value)

                    is FrameLayout -> (view.layoutParams as FrameLayout.LayoutParams).gravity =
                        parseGravity(value)
                }
            }

            addAttribute<String>(Attributes.Common.WEIGHT) { view, value ->
                (view.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    weight = parseFloat(value)
                }
            }

            // Handle RelativeLayout alignment rules
            val relativeRules = mapOf(
                Attributes.View.VIEW_ABOVE to RelativeLayout.ABOVE,
                Attributes.View.VIEW_BELOW to RelativeLayout.BELOW,
                Attributes.View.VIEW_TO_LEFT_OF to RelativeLayout.LEFT_OF,
                Attributes.View.VIEW_TO_RIGHT_OF to RelativeLayout.RIGHT_OF,
                Attributes.View.VIEW_ALIGN_TOP to RelativeLayout.ALIGN_TOP,
                Attributes.View.VIEW_ALIGN_BOTTOM to RelativeLayout.ALIGN_BOTTOM,
                Attributes.View.VIEW_ALIGN_PARENT_TOP to RelativeLayout.ALIGN_PARENT_TOP,
                Attributes.View.VIEW_ALIGN_PARENT_BOTTOM to RelativeLayout.ALIGN_PARENT_BOTTOM
            )
            relativeRules.forEach { (attr, rule) ->
                addAttribute<String>(attr) { view, value ->
                    addRelativeLayoutRule(view, value.toString(), rule)
                }
            }

            // Handle margins and paddings
            listOf(
                Attributes.Common.LAYOUT_MARGIN to ::setMargin,
                Attributes.Common.LAYOUT_MARGIN_LEFT to ::setMarginLeft,
                Attributes.Common.LAYOUT_MARGIN_RIGHT to ::setMarginRight,
                Attributes.Common.LAYOUT_MARGIN_TOP to ::setMarginTop,
                Attributes.Common.LAYOUT_MARGIN_BOTTOM to ::setMarginBottom,
                Attributes.Common.PADDING to ::setPadding,
                Attributes.Common.PADDING_LEFT to ::setPaddingLeft,
                Attributes.Common.PADDING_RIGHT to ::setPaddingRight,
                Attributes.Common.PADDING_TOP to ::setPaddingTop,
                Attributes.Common.PADDING_BOTTOM to ::setPaddingBottom
            ).forEach { (attr, func) ->
                addAttribute<String>(attr) { view, value ->
                    //handle if for horizontal
                    func(view, value?.toPixels(view.resources.displayMetrics, asInt = true) as Int)
                }
            }
        }
    }

    /** Sets width or height dynamically */
    private fun setSize(view: BaseView, value: String, isWidth: Boolean) {
        val params = (view.layoutParams ?: ViewGroup.LayoutParams(WRAP, WRAP))
        val size = value.toLayoutParam(view.resources.displayMetrics, view.getParentView(), isWidth)
        if (isWidth) params.width = size else params.height = size
        view.layoutParams = params
    }

    /** Converts layout string values to proper size */
    private fun String.toLayoutParam(
        metrics: DisplayMetrics, parent: ViewGroup?, horizontal: Boolean
    ) = when (this) {
        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
        else -> this.toPixels(metrics, parent, horizontal, true) as Int
    }

    /** Helper functions for margins & paddings */
    private fun setMargin(view: View, value: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(value, value, value, value)

    private fun setMarginLeft(view: View, value: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(value, 0, 0, 0)

    private fun setMarginRight(view: View, value: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0, value, 0)

    private fun setMarginTop(view: View, value: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, value, 0, 0)

    private fun setMarginBottom(view: View, value: Int) =
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0, 0, value)

    private fun setPadding(view: View, value: Int) = view.setPadding(value, value, value, value)
    private fun setPaddingLeft(view: View, value: Int) = view.setPadding(value, 0, 0, 0)
    private fun setPaddingRight(view: View, value: Int) = view.setPadding(0, 0, value, 0)
    private fun setPaddingTop(view: View, value: Int) = view.setPadding(0, value, 0, 0)
    private fun setPaddingBottom(view: View, value: Int) = view.setPadding(0, 0, 0, value)

    /** Adds relative layout rule dynamically */
    private fun addRelativeLayoutRule(view: BaseView, value: String, rule: Int) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            val anchor = when {
                value.isBoolean() -> parseRelativeLayoutBoolean(value.asBoolean()!!)
                else -> view.getParentView()?.getViewID(value.extractViewId())!!
            }
            ParseHelper.addRelativeLayoutRule(view, rule, anchor)
        }
    }
}
