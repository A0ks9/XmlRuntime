package com.voyager.utils

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.voyager.data.models.ConfigManager
import com.voyager.utils.AttributesHandler.addConstraintRule
import com.voyager.utils.AttributesHandler.addRelativeLayoutRule
import com.voyager.utils.AttributesHandler.handleBackground
import com.voyager.utils.AttributesHandler.handleForeground
import com.voyager.utils.AttributesHandler.handleForegroundTint
import com.voyager.utils.AttributesHandler.handleResString
import com.voyager.utils.AttributesHandler.setChainStyle
import com.voyager.utils.AttributesHandler.setConstraintLayoutBias
import com.voyager.utils.AttributesHandler.setDimensionRatio
import com.voyager.utils.AttributesHandler.setImageSource
import com.voyager.utils.AttributesHandler.setSize
import com.voyager.utils.ParseHelper.getColor
import com.voyager.utils.ParseHelper.parseBoolean
import com.voyager.utils.ParseHelper.parseDrawingCacheQuality
import com.voyager.utils.ParseHelper.parseEllipsize
import com.voyager.utils.ParseHelper.parseFloat
import com.voyager.utils.ParseHelper.parseGravity
import com.voyager.utils.ParseHelper.parseInputType
import com.voyager.utils.ParseHelper.parsePorterDuff
import com.voyager.utils.ParseHelper.parseScaleType
import com.voyager.utils.ParseHelper.parseTextAlignment
import com.voyager.utils.ParseHelper.parseTextStyle
import com.voyager.utils.ParseHelper.parseVisibility
import com.voyager.utils.Utils.getClickListener
import com.voyager.utils.Utils.getGeneratedViewInfo
import com.voyager.utils.interfaces.AttributeProcessorRegistry
import com.voyager.utils.processors.AttributeRegistry
import com.voyager.utils.processors.AttributeRegistry.Companion.configureProcessor
import com.voyager.utils.processors.AttributeRegistry.Companion.registerAttribute

internal object BaseViewAttributes {

    val AttributeProcessors = LinkedHashSet<Pair<String, AttributeProcessorRegistry<*, Any>>>()
    private var isAttributesInitialized = false
    private val isLoggingEnabled = ConfigManager.config.isLoggingEnabled

    fun initializeAttributes() {
        if (isAttributesInitialized) return
        configureProcessor {
            commonAttributes()
            linearLayoutAttributes()
            relativeLayoutAttributes()
            constraintLayoutAttributes()
            imageViewAttributes()
            textViewAttributes()
            viewAttributes()
        }
        isAttributesInitialized = true
    }

    private fun AttributeRegistry.commonAttributes() {
        registerAttribute<View, String>(Attributes.Common.ID) { targetView, attributeValue ->
            targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
                if (isLoggingEnabled) Log.d(
                    "ViewAttributes",
                    "ID: $attributeValue, viewID: ${attributeValue.extractViewId()}"
                )
                targetView.id =
                    View.generateViewId().also { info.viewID[attributeValue.extractViewId()] = it }
            }
        }

        widthAndHeightAttributes()

        registerAttribute<View, String>(Attributes.Common.GRAVITY) { targetView, attributeValue ->
            val alignment = parseGravity(attributeValue)
            when (val layoutParams = targetView.layoutParams) {
                is LinearLayout.LayoutParams -> layoutParams.gravity = alignment
                is FrameLayout.LayoutParams -> layoutParams.gravity = alignment
                else -> try {
                    if (targetView is TextView) targetView.gravity = alignment
                } catch (_: Exception) {
                    if (isLoggingEnabled) Log.w(
                        "ViewAttributes",
                        "Gravity not applicable: ${targetView.javaClass.simpleName}"
                    )
                }
            }
        }

        marginAndPaddingAttributes()

        registerAttribute<View, String>(Attributes.Common.VISIBILITY) { targetView, attributeValue ->
            targetView.visibility = parseVisibility(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.CLICKABLE) { targetView, attributeValue ->
            targetView.isClickable = parseBoolean(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.LONG_CLICKABLE) { targetView, attributeValue ->
            targetView.isLongClickable = parseBoolean(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.TAG) { targetView, attributeValue ->
            if (targetView.tag == null) targetView.tag = attributeValue
        }

        registerAttribute<View, String>(Attributes.Common.ENABLED) { targetView, attributeValue ->
            targetView.isEnabled = parseBoolean(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.BACKGROUND) { targetView, attributeValue ->
            handleBackground(targetView, attributeValue, targetView.context)
        }

        registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT) { targetView, attributeValue ->
            ViewCompat.setBackgroundTintList(
                targetView, ContextCompat.getColorStateList(
                    targetView.context,
                    getColor(attributeValue, targetView.context, Color.TRANSPARENT)
                )
            )
        }

        registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT_MODE) { targetView, attributeValue ->
            ViewCompat.setBackgroundTintMode(targetView, parsePorterDuff(attributeValue))
        }

        registerAttribute<View, String>(Attributes.Common.CONTENT_DESCRIPTION) { targetView, attributeValue ->
            targetView.contentDescription = handleResString(attributeValue, targetView.context)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerAttribute<View, String>(Attributes.Common.FOREGROUND) { targetView, attributeValue ->
                handleForeground(targetView, attributeValue, targetView.context)
            }

            registerAttribute<View, String>(Attributes.Common.FOREGROUND_GRAVITY) { targetView, attributeValue ->
                targetView.foregroundGravity = parseGravity(attributeValue)
            }

            registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT) { targetView, attributeValue ->
                handleForegroundTint(targetView, attributeValue, targetView.context)
            }

            registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT_MODE) { targetView, attributeValue ->
                targetView.foregroundTintMode = parsePorterDuff(attributeValue)
            }
        }

        registerAttribute<View, String>(Attributes.Common.ROTATION) { targetView, attributeValue ->
            targetView.rotation = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.ROTATION_X) { targetView, attributeValue ->
            targetView.rotationX = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.ROTATION_Y) { targetView, attributeValue ->
            targetView.rotationY = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.SCALE_X) { targetView, attributeValue ->
            targetView.scaleX = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.SCALE_Y) { targetView, attributeValue ->
            targetView.scaleY = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.TRANSLATION_X) { targetView, attributeValue ->
            targetView.translationX = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.TRANSLATION_Y) { targetView, attributeValue ->
            targetView.translationY = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.TRANSLATION_Z) { targetView, attributeValue ->
            targetView.translationZ = attributeValue.toFloat()
        }

        registerAttribute<View, String>(Attributes.Common.ELEVATION) { targetView, attributeValue ->
            targetView.elevation = attributeValue.toFloat()
        }

        @Suppress("DEPRECATION") registerAttribute<View, String>(Attributes.Common.DRAWING_CACHE_QUALITY) { targetView, attributeValue ->
            targetView.drawingCacheQuality = parseDrawingCacheQuality(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.ALPHA) { targetView, attributeValue ->
            targetView.alpha = attributeValue.toFloat()
        }

        registerAttribute<ViewGroup, String>(Attributes.Common.LAYOUT_GRAVITY) { targetView, attributeValue ->
            val gravity = parseGravity(attributeValue)
            val params = targetView.layoutParams
            if (targetView is LinearLayout) {
                (params as LinearLayout.LayoutParams).gravity = gravity
            } else if (targetView is FrameLayout) {
                (params as FrameLayout.LayoutParams).gravity = gravity
            } else if (targetView is GridLayout) {
                (params as GridLayout.LayoutParams).setGravity(gravity)
            }

            targetView.layoutParams = params
        }
    }

    private fun AttributeRegistry.widthAndHeightAttributes() {
        arrayOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attribute ->
            registerAttribute<View, String>(attribute) { targetView, attributeValue ->
                setSize(
                    targetView, attributeValue, isWidth = true
                )
            }
        }
        arrayOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attribute ->
            registerAttribute<View, String>(attribute) { targetView, attributeValue ->
                setSize(
                    targetView, attributeValue, isWidth = false
                )
            }
        }
    }

    private fun AttributeRegistry.linearLayoutAttributes() {
        registerAttribute<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION) { targetView, attributeValue ->
            targetView.orientation = if (attributeValue.equals(
                    "horizontal", true
                )
            ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        }

        registerAttribute<LinearLayout, String>(Attributes.Common.WEIGHT) { targetView, attributeValue ->
            (targetView.layoutParams as? LinearLayout.LayoutParams)?.let {
                it.weight = parseFloat(attributeValue)
            }
        }
    }

    private fun AttributeRegistry.relativeLayoutAttributes() {
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
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
                addRelativeLayoutRule(
                    targetView, attributeValue, rule
                )
            }
        }
    }

    private fun AttributeRegistry.constraintLayoutAttributes() {
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
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
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

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.HORIZONTAL,
                attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.VERTICAL,
                attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO) { targetView, attributeValue ->
            setDimensionRatio(
                targetView.getParentView() as? ConstraintLayout, targetView, attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS) { targetView, attributeValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                true,
                parseFloat(attributeValue)
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS) { targetView, biasValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                false,
                parseFloat(biasValue)
            )
        }
    }

    private fun AttributeRegistry.imageViewAttributes() {
        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE) { targetView, attributeValue ->
            parseScaleType(attributeValue)?.let { targetView.scaleType = it }
        }

        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC) { targetView, attributeValue ->
            setImageSource(targetView, attributeValue)
        }
    }

    private fun AttributeRegistry.textViewAttributes() {
        registerAttribute<TextView, String>(Attributes.Common.TEXT) { targetView, attributeValue ->
            targetView.text = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_SIZE) { targetView, attributeValue ->
            targetView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                attributeValue.toPixels(targetView.resources.displayMetrics) as Float
            )
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_COLOR) { targetView, attributeValue ->
            targetView.setTextColor(getColor(attributeValue, targetView.context))
        }

        registerAttribute<TextView, String>(Attributes.Common.TEXT_STYLE) { targetView, attributeValue ->
            targetView.setTypeface(null, parseTextStyle(attributeValue.lowercase()))
        }

        registerAttribute<TextView, String>(Attributes.View.VIEW_TEXT_ALIGNMENT) { targetView, attributeValue ->
            parseTextAlignment(attributeValue)?.let { targetView.textAlignment = it }
        }

        registerAttribute<TextView, String>(Attributes.Common.ELLIPSIZE) { targetView, attributeValue ->
            targetView.ellipsize = parseEllipsize(attributeValue.lowercase())
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_SINGLE_LINE) { targetView, attributeValue ->
            targetView.isSingleLine = parseBoolean(attributeValue)
        }

        registerAttribute<TextView, String>(Attributes.Common.HINT) { targetView, attributeValue ->
            targetView.hint = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_INPUT_TYPE) { targetView, attributeValue ->
            parseInputType(attributeValue).let { if (it > 0) targetView.inputType = it }
        }
    }

    private fun AttributeRegistry.viewAttributes() {
        registerAttribute<View, String>(Attributes.View.VIEW_ON_CLICK) { targetView, attributeValue ->
            targetView.setOnClickListener(
                getClickListener(
                    targetView.getParentView(), attributeValue
                )
            )
        }
    }

    private fun AttributeRegistry.marginAndPaddingAttributes() {
        arrayOf(
            Attributes.Common.LAYOUT_MARGIN to AttributesHandler::setMargin,
            Attributes.Common.LAYOUT_MARGIN_LEFT to AttributesHandler::setMarginLeft,
            Attributes.Common.LAYOUT_MARGIN_RIGHT to AttributesHandler::setMarginRight,
            Attributes.Common.LAYOUT_MARGIN_START to AttributesHandler::setMarginLeft,
            Attributes.Common.LAYOUT_MARGIN_END to AttributesHandler::setMarginRight,
            Attributes.Common.LAYOUT_MARGIN_TOP to AttributesHandler::setMarginTop,
            Attributes.Common.LAYOUT_MARGIN_BOTTOM to AttributesHandler::setMarginBottom,
            Attributes.Common.PADDING to AttributesHandler::setPadding,
            Attributes.Common.PADDING_LEFT to AttributesHandler::setPaddingLeft,
            Attributes.Common.PADDING_RIGHT to AttributesHandler::setPaddingRight,
            Attributes.Common.PADDING_START to AttributesHandler::setPaddingLeft,
            Attributes.Common.PADDING_END to AttributesHandler::setPaddingRight,
            Attributes.Common.PADDING_TOP to AttributesHandler::setPaddingTop,
            Attributes.Common.PADDING_BOTTOM to AttributesHandler::setPaddingBottom
        ).forEach { (attr, func) ->
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
                val displayMetrics = targetView.resources.displayMetrics
                val isMargin = attr.contains("margin")
                val pixels = if (isMargin) {
                    val isHorizontal = attr in listOf(
                        Attributes.Common.LAYOUT_MARGIN_LEFT,
                        Attributes.Common.LAYOUT_MARGIN_START,
                        Attributes.Common.LAYOUT_MARGIN_RIGHT,
                        Attributes.Common.LAYOUT_MARGIN_END
                    )
                    attributeValue.toPixels(
                        displayMetrics, targetView.getParentView(), isHorizontal, true
                    )
                } else {
                    attributeValue.toPixels(displayMetrics, asInt = true)
                }
                func(targetView, pixels as Int)
            }
        }
    }
}