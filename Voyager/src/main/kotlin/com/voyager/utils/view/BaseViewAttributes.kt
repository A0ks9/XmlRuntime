package com.voyager.utils.view

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.voyager.data.models.ConfigManager
import com.voyager.utils.ParseHelper.getColor
import com.voyager.utils.ParseHelper.parseBoolean
import com.voyager.utils.ParseHelper.parseEllipsize
import com.voyager.utils.ParseHelper.parseFloat
import com.voyager.utils.ParseHelper.parseInputType
import com.voyager.utils.ParseHelper.parseScaleType
import com.voyager.utils.ParseHelper.parseTextAlignment
import com.voyager.utils.ParseHelper.parseTextStyle
import com.voyager.utils.Utils.getClickListener
import com.voyager.utils.getParentView
import com.voyager.utils.processors.AttributeProcessor.registerAttribute
import com.voyager.utils.toPixels
import com.voyager.utils.view.AttributesHandler.addConstraintRule
import com.voyager.utils.view.AttributesHandler.addRelativeLayoutRule
import com.voyager.utils.view.AttributesHandler.setChainStyle
import com.voyager.utils.view.AttributesHandler.setConstraintLayoutBias
import com.voyager.utils.view.AttributesHandler.setDimensionRatio
import com.voyager.utils.view.AttributesHandler.setImageSource

internal object BaseViewAttributes {

    private var isAttributesInitialized = false
    private val isLoggingEnabled = ConfigManager.config.isLoggingEnabled

    fun initializeAttributes() {
        if (isAttributesInitialized) return

        commonAttributes(isLoggingEnabled)
        linearLayoutAttributes()
        relativeLayoutAttributes()
        constraintLayoutAttributes()
        imageViewAttributes()
        textViewAttributes()
        viewAttributes()

        isAttributesInitialized = true
    }

    private fun linearLayoutAttributes() {
        registerAttribute<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION.name) { targetView, attributeValue ->
            targetView.orientation = if (attributeValue.equals(
                    "horizontal", true
                )
            ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        }

        registerAttribute<LinearLayout, String>(Attributes.Common.WEIGHT.name) { targetView, attributeValue ->
            (targetView.layoutParams as? LinearLayout.LayoutParams)?.let {
                it.weight = parseFloat(attributeValue)
            }
        }
    }

    private fun relativeLayoutAttributes() {
        mapOf(
            Attributes.View.VIEW_ABOVE to RelativeLayout.ABOVE,
            Attributes.View.VIEW_BELOW to RelativeLayout.BELOW,
            Attributes.View.VIEW_TO_LEFT_OF to RelativeLayout.LEFT_OF,
            Attributes.View.VIEW_TO_RIGHT_OF to RelativeLayout.RIGHT_OF,
            Attributes.View.VIEW_ALIGN_TOP to RelativeLayout.ALIGN_TOP,
            Attributes.View.VIEW_ALIGN_BOTTOM to RelativeLayout.ALIGN_BOTTOM,
            Attributes.View.VIEW_ALIGN_PARENT_TOP to RelativeLayout.ALIGN_PARENT_TOP,
            Attributes.View.VIEW_ALIGN_PARENT_BOTTOM to RelativeLayout.ALIGN_PARENT_BOTTOM,
            Attributes.View.VIEW_ALIGN_START to RelativeLayout.ALIGN_START,
            Attributes.View.VIEW_ALIGN_END to RelativeLayout.ALIGN_END,
            Attributes.View.VIEW_ALIGN_PARENT_START to RelativeLayout.ALIGN_PARENT_START,
            Attributes.View.VIEW_ALIGN_PARENT_END to RelativeLayout.ALIGN_PARENT_END
        ).forEach { (attr, rule) ->
            registerAttribute<View, String>(attr.name) { targetView, attributeValue ->
                addRelativeLayoutRule(
                    targetView, attributeValue, rule
                )
            }
        }
    }

    private fun constraintLayoutAttributes() {
        mapOf(
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF to (ConstraintSet.LEFT to ConstraintSet.LEFT),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF to (ConstraintSet.LEFT to ConstraintSet.RIGHT),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF to (ConstraintSet.RIGHT to ConstraintSet.LEFT),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF to (ConstraintSet.RIGHT to ConstraintSet.RIGHT),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_TOP_OF to (ConstraintSet.TOP to ConstraintSet.TOP),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF to (ConstraintSet.TOP to ConstraintSet.BOTTOM),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF to (ConstraintSet.BOTTOM to ConstraintSet.TOP),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF to (ConstraintSet.BOTTOM to ConstraintSet.BOTTOM),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_START_OF to (ConstraintSet.START to ConstraintSet.START),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_END_OF to (ConstraintSet.START to ConstraintSet.END),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_START_OF to (ConstraintSet.END to ConstraintSet.START),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_END_OF to (ConstraintSet.END to ConstraintSet.END),
            Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF to (ConstraintSet.BASELINE to ConstraintSet.BASELINE)
        ).forEach { (attr, side) ->
            registerAttribute<View, String>(attr.name) { targetView, attributeValue ->
                if (isLoggingEnabled) Log.d(
                    "AttributeRegistry", "Attr: $attr, Val: $attributeValue, Side: $side"
                )
                addConstraintRule(
                    targetView.getParentView() as? ConstraintLayout,
                    targetView,
                    attributeValue,
                    side
                )
            }
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE.name) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.HORIZONTAL,
                attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE.name) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.VERTICAL,
                attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO.name) { targetView, attributeValue ->
            setDimensionRatio(
                targetView.getParentView() as? ConstraintLayout, targetView, attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS.name) { targetView, attributeValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                true,
                parseFloat(attributeValue)
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS.name) { targetView, biasValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                false,
                parseFloat(biasValue)
            )
        }
    }

    private fun imageViewAttributes() {
        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE.name) { targetView, attributeValue ->
            parseScaleType(attributeValue)?.let { targetView.scaleType = it }
        }

        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC.name) { targetView, attributeValue ->
            setImageSource(targetView, attributeValue)
        }
    }

    private fun textViewAttributes() {
        registerAttribute<TextView, String>(Attributes.Common.TEXT.name) { targetView, attributeValue ->
            targetView.text = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_SIZE.name) { targetView, attributeValue ->
            targetView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                attributeValue.toPixels(targetView.resources.displayMetrics) as Float
            )
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_COLOR.name) { targetView, attributeValue ->
            targetView.setTextColor(getColor(attributeValue, targetView.context))
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_STYLE.name) { targetView, attributeValue ->
            targetView.setTypeface(null, parseTextStyle(attributeValue.lowercase()))
        }

        registerAttribute<TextView, String>(Attributes.View.VIEW_TEXT_ALIGNMENT.name) { targetView, attributeValue ->
            parseTextAlignment(attributeValue)?.let { targetView.textAlignment = it }
        }

        registerAttribute<TextView, String>(Attributes.Common.ELLIPSIZE.name) { targetView, attributeValue ->
            targetView.ellipsize = parseEllipsize(attributeValue.lowercase())
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_SINGLE_LINE.name) { targetView, attributeValue ->
            targetView.isSingleLine = parseBoolean(attributeValue)
        }

        registerAttribute<TextView, String>(Attributes.Common.HINT.name) { targetView, attributeValue ->
            targetView.hint = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_INPUT_TYPE.name) { targetView, attributeValue ->
            parseInputType(attributeValue).let { if (it > 0) targetView.inputType = it }
        }
    }

    private fun viewAttributes() {
        registerAttribute<View, String>(Attributes.View.VIEW_ON_CLICK.name) { targetView, attributeValue ->
            targetView.setOnClickListener(
                getClickListener(
                    targetView.getParentView(), attributeValue
                )
            )
        }
    }
}