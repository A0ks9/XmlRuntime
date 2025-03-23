package com.voyager.utils.view

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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.voyager.utils.ParseHelper.getColor
import com.voyager.utils.ParseHelper.getString
import com.voyager.utils.ParseHelper.parseBoolean
import com.voyager.utils.ParseHelper.parseDrawingCacheQuality
import com.voyager.utils.ParseHelper.parseFloat
import com.voyager.utils.ParseHelper.parseGravity
import com.voyager.utils.ParseHelper.parseImeOption
import com.voyager.utils.ParseHelper.parseImportantForAccessibility
import com.voyager.utils.ParseHelper.parseInt
import com.voyager.utils.ParseHelper.parseOverScrollMode
import com.voyager.utils.ParseHelper.parsePorterDuff
import com.voyager.utils.ParseHelper.parseScrollIndicators
import com.voyager.utils.ParseHelper.parseVisibility
import com.voyager.utils.Utils.DrawablePosition
import com.voyager.utils.Utils.getGeneratedViewInfo
import com.voyager.utils.extractViewId
import com.voyager.utils.getParentView
import com.voyager.utils.processors.AttributeProcessor.registerAttribute
import com.voyager.utils.toPixels
import com.voyager.utils.view.AttributesHandler.handleBackground
import com.voyager.utils.view.AttributesHandler.handleDrawablePosition
import com.voyager.utils.view.AttributesHandler.handleForeground
import com.voyager.utils.view.AttributesHandler.handleForegroundTint
import com.voyager.utils.view.AttributesHandler.handleResString
import com.voyager.utils.view.AttributesHandler.handleScrollbar
import com.voyager.utils.view.AttributesHandler.handleTint
import com.voyager.utils.view.AttributesHandler.handleTintMode
import com.voyager.utils.view.AttributesHandler.handleTransformationMethod
import com.voyager.utils.view.AttributesHandler.loadFontFromAttribute
import com.voyager.utils.view.AttributesHandler.setSize

internal fun commonAttributes(isLoggingEnabled: Boolean) {
    registerAttribute<View, String>(Attributes.Common.ID.name) { targetView, attributeValue ->
        targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
            if (isLoggingEnabled) Log.d(
                "view", "ID: $attributeValue, viewID: ${attributeValue.extractViewId()}"
            )
            targetView.id =
                View.generateViewId().also { info.viewID[attributeValue.extractViewId()] = it }
        }
    }

    widthAndHeightAttributes()

    registerAttribute<View, String>(Attributes.Common.GRAVITY.name) { targetView, attributeValue ->
        val alignment = parseGravity(attributeValue)
        when (val layoutParams = targetView.layoutParams) {
            is LinearLayout.LayoutParams -> layoutParams.gravity = alignment
            is FrameLayout.LayoutParams -> layoutParams.gravity = alignment
            else -> try {
                if (targetView is TextView) targetView.gravity = alignment
            } catch (e: Exception) {
                if (isLoggingEnabled) Log.w(
                    "view", "Gravity not applicable: ${targetView.javaClass.simpleName}", e
                )
            }
        }
    }

    marginAndPaddingAttributes()

    registerAttribute<View, String>(Attributes.Common.VISIBILITY.name) { targetView, attributeValue ->
        targetView.visibility = parseVisibility(attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.CLICKABLE.name) { targetView, attributeValue ->
        targetView.isClickable = parseBoolean(attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.LONG_CLICKABLE.name) { targetView, attributeValue ->
        targetView.isLongClickable = parseBoolean(attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.TAG.name) { targetView, attributeValue ->
        if (targetView.tag == null) targetView.tag = attributeValue
    }

    registerAttribute<View, String>(Attributes.Common.ENABLED.name) { targetView, attributeValue ->
        targetView.isEnabled = parseBoolean(attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.BACKGROUND.name) { targetView, attributeValue ->
        handleBackground(targetView, attributeValue, targetView.context)
    }

    registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT.name) { targetView, attributeValue ->
        ViewCompat.setBackgroundTintList(
            targetView, ContextCompat.getColorStateList(
                targetView.context, getColor(attributeValue, targetView.context, Color.TRANSPARENT)
            )
        )
    }

    registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT_MODE.name) { targetView, attributeValue ->
        ViewCompat.setBackgroundTintMode(targetView, parsePorterDuff(attributeValue))
    }

    registerAttribute<View, String>(Attributes.Common.CONTENT_DESCRIPTION.name) { targetView, attributeValue ->
        targetView.contentDescription = handleResString(attributeValue, targetView.context)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        registerAttribute<View, String>(Attributes.Common.FOREGROUND.name) { targetView, attributeValue ->
            handleForeground(targetView, attributeValue, targetView.context)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_GRAVITY.name) { targetView, attributeValue ->
            targetView.foregroundGravity = parseGravity(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT.name) { targetView, attributeValue ->
            handleForegroundTint(targetView, attributeValue, targetView.context)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT_MODE.name) { targetView, attributeValue ->
            targetView.foregroundTintMode = parsePorterDuff(attributeValue)
        }
    }

    registerAttribute<View, String>(Attributes.Common.ROTATION.name) { targetView, attributeValue ->
        targetView.rotation = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.ROTATION_X.name) { targetView, attributeValue ->
        targetView.rotationX = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.ROTATION_Y.name) { targetView, attributeValue ->
        targetView.rotationY = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.SCALE_X.name) { targetView, attributeValue ->
        targetView.scaleX = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.SCALE_Y.name) { targetView, attributeValue ->
        targetView.scaleY = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.TRANSLATION_X.name) { targetView, attributeValue ->
        targetView.translationX = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.TRANSLATION_Y.name) { targetView, attributeValue ->
        targetView.translationY = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.TRANSLATION_Z.name) { targetView, attributeValue ->
        targetView.translationZ = attributeValue.toFloat()
    }

    registerAttribute<View, String>(Attributes.Common.ELEVATION.name) { targetView, attributeValue ->
        targetView.elevation = attributeValue.toFloat()
    }

    @Suppress("DEPRECATION") registerAttribute<View, String>(Attributes.Common.DRAWING_CACHE_QUALITY.name) { targetView, attributeValue ->
        targetView.drawingCacheQuality = parseDrawingCacheQuality(attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.ALPHA.name) { targetView, attributeValue ->
        targetView.alpha = attributeValue.toFloat()
    }

    registerAttribute<ViewGroup, String>(Attributes.Common.LAYOUT_GRAVITY.name) { targetView, attributeValue ->
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

//    registerAttribute<View, String>(Attributes.Common.STYLE.name) { targetView, attributeValue ->
//        val style = StyleResource.valueOf(attributeValue, targetView.context)
//        style?.apply(targetView.context)
//    }

    registerAttribute<View, String>(Attributes.Common.MIN_WIDTH.name) { targetView, attributeValue ->
        targetView.minimumWidth = attributeValue.toPixels(
            targetView.resources.displayMetrics, targetView.getParentView(), true, true
        ) as Int
    }

    registerAttribute<View, String>(Attributes.Common.MIN_HEIGHT.name) { targetView, attributeValue ->
        targetView.minimumHeight = attributeValue.toPixels(
            targetView.resources.displayMetrics, targetView.getParentView(), false, true
        ) as Int
    }

    registerAttribute<View, String>(Attributes.Common.MAX_WIDTH.name) { targetView, attributeValue ->
        val maxWidth = attributeValue.toPixels(
            targetView.context.resources.displayMetrics, targetView.getParentView(), true, true
        ) as Int
        when (targetView) {
            is TextView -> targetView.maxWidth = maxWidth
            is ImageView -> targetView.maxWidth = maxWidth

            else -> targetView.layoutParams = targetView.layoutParams.apply {
                width = maxWidth
            }
        }
        targetView.requestLayout()
    }

    registerAttribute<View, String>(Attributes.Common.MAX_HEIGHT.name) { targetView, attributeValue ->
        val maxHeight = attributeValue.toPixels(
            targetView.context.resources.displayMetrics, asInt = true
        ) as Int
        when (targetView) {
            is TextView -> targetView.maxHeight = maxHeight
            is ImageView -> targetView.maxHeight = maxHeight

            else -> targetView.layoutParams = targetView.layoutParams.apply {
                height = maxHeight
            }
        }
        targetView.requestLayout()
    }

    registerAttribute<TextView, String>(Attributes.Common.TEXT_COLOR_HINT.name) { targetView, attributeValue ->
        targetView.setHintTextColor(getColor(attributeValue, targetView.context, Color.GRAY))
    }

    registerAttribute<TextView, String>(Attributes.Common.FONT_FAMILY.name) { targetView, attributeValue ->
        val typeface = loadFontFromAttribute(targetView.context, attributeValue)
        typeface?.let { targetView.typeface = it }
    }

    registerAttribute<TextView, String>(Attributes.Common.LETTER_SPACING.name) { targetView, attributeValue ->
        val letterSpacing = parseFloat(attributeValue)
        targetView.letterSpacing = letterSpacing
    }

    registerAttribute<TextView, String>(Attributes.Common.LINE_SPACING_EXTRA.name) { targetView, attributeValue ->
        val lineSpacing = parseFloat(attributeValue)
        val lineSpacingMultiplier = targetView.lineSpacingMultiplier
        targetView.setLineSpacing(lineSpacing, lineSpacingMultiplier)
    }

    registerAttribute<TextView, String>(Attributes.Common.LINE_SPACING_MULTIPLIER.name) { targetView, attributeValue ->
        val lineSpacing = targetView.lineSpacingExtra
        val lineSpacingMultiplier = parseFloat(attributeValue)
        targetView.setLineSpacing(lineSpacing, lineSpacingMultiplier)
    }

    registerAttribute<TextView, String>(Attributes.Common.SHADOW_COLOR.name) { targetView, attributeValue ->
        val shadowColor = getColor(attributeValue, targetView.context, Color.TRANSPARENT)
        targetView.setShadowLayer(
            targetView.shadowRadius, targetView.shadowDx, targetView.shadowDy, shadowColor
        )
    }

    registerAttribute<TextView, String>(Attributes.Common.SHADOW_RADIUS.name) { targetView, attributeValue ->
        val shadowRadius = parseFloat(attributeValue)
        targetView.setShadowLayer(
            shadowRadius, targetView.shadowDx, targetView.shadowDy, targetView.shadowColor
        )

    }

    registerAttribute<TextView, String>(Attributes.Common.SHADOW_DX.name) { targetView, attributeValue ->
        val shadowDx = parseFloat(attributeValue)
        targetView.setShadowLayer(
            targetView.shadowRadius, shadowDx, targetView.shadowDy, targetView.shadowColor
        )
    }

    registerAttribute<TextView, String>(Attributes.Common.SHADOW_DY.name) { targetView, attributeValue ->
        val shadowDy = parseFloat(attributeValue)
        targetView.setShadowLayer(
            targetView.shadowRadius, targetView.shadowDx, shadowDy, targetView.shadowColor
        )
    }

    registerAttribute<TextView, String>(Attributes.Common.TEXT_SCALE_X.name) { targetView, attributeValue ->
        val currentSp = targetView.textSize / targetView.context.resources.configuration.fontScale
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSp * parseFloat(attributeValue))
    }

    registerAttribute<TextView, String>(Attributes.Common.TEXT_ALL_CAPS.name) { targetView, attributeValue ->
        val isALlCaps = parseBoolean(attributeValue)
        targetView.isAllCaps = isALlCaps
    }

    registerAttribute<TextView, String>(Attributes.Common.IME_OPTIONS.name) { targetView, attributeValue ->
        targetView.imeOptions = parseImeOption(attributeValue)
    }

    registerAttribute<TextView, String>(Attributes.Common.MAX_LINES.name) { targetView, attributeValue ->
        targetView.maxLines = parseInt(attributeValue)
    }

    registerAttribute<TextView, String>(Attributes.Common.TRANSFORMATION_METHOD.name) { targetView, attributeValue ->
        handleTransformationMethod(targetView, attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.PADDING_HORIZONTAL.name) { targetView, attributeValue ->
        val horizontalPadding = attributeValue.toPixels(
            targetView.context.resources.displayMetrics, targetView.getParentView(), true, true
        ) as Int
        targetView.setPadding(
            horizontalPadding, targetView.paddingTop, horizontalPadding, targetView.paddingBottom
        )
    }

    registerAttribute<View, String>(Attributes.Common.PADDING_VERTICAL.name) { targetView, attributeValue ->
        val verticalPadding = attributeValue.toPixels(
            targetView.context.resources.displayMetrics, targetView.getParentView(), false, true
        ) as Int
        targetView.setPadding(
            targetView.paddingLeft, verticalPadding, targetView.paddingRight, verticalPadding
        )
    }

    registerAttribute<View, String>(Attributes.Common.SCROLLBARS.name) { targetView, attributeValue ->
        handleScrollbar(targetView, attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.OVER_SCROLL_MODE.name) { targetView, attributeValue ->
        targetView.overScrollMode = parseOverScrollMode(attributeValue)
    }

    registerAttribute<TextView, String>(Attributes.Common.DRAWABLE_PADDING.name) { targetView, attributeValue ->
        val padding = attributeValue.toPixels(
            targetView.context.resources.displayMetrics, asInt = true
        ) as Int
        targetView.compoundDrawablePadding = padding
    }

    arrayOf(
        Attributes.Common.DRAWABLE_START to DrawablePosition.START,
        Attributes.Common.DRAWABLE_END to DrawablePosition.END,
        Attributes.Common.DRAWABLE_TOP to DrawablePosition.TOP,
        Attributes.Common.DRAWABLE_BOTTOM to DrawablePosition.BOTTOM
    ).forEach { (attribute, position) ->
        registerAttribute<TextView, String>(attribute.name) { targetView, attributeValue ->
            handleDrawablePosition(targetView, attributeValue, position)
        }
    }

    registerAttribute<View, String>(Attributes.Common.TINT.name) { targetView, attributeValue ->
        handleTint(targetView, attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.TINT_MODE.name) { targetView, attributeValue ->
        handleTintMode(targetView, attributeValue)
    }

    registerAttribute<View, String>(Attributes.Common.IMPORTANT_FOR_ACCESSIBILITY.name) { targetView, attributeValue ->
        targetView.importantForAccessibility = parseImportantForAccessibility(attributeValue)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        registerAttribute<View, String>(Attributes.Common.SCREEN_READER_FOCUSABLE.name) { targetView, attributeValue ->
            targetView.isScreenReaderFocusable = parseBoolean(attributeValue)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        registerAttribute<View, String>(Attributes.Common.TOOLTIP_TEXT.name) { targetView, attributeValue ->
            targetView.tooltipText = getString(targetView.context, attributeValue)
        }
    }

    registerAttribute<ViewGroup, String>(Attributes.Common.CLIP_TO_PADDING.name) { targetView, attributeValue ->
        targetView.clipToPadding = parseBoolean(attributeValue)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        registerAttribute<View, String>(Attributes.Common.SCROLL_INDICATORS.name) { targetView, attributeValue ->
            targetView.scrollIndicators = parseScrollIndicators(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.DRAWABLE_TINT.name) { targetView, attributeValue ->
            handleTint(targetView, attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.DRAWABLE_TINT_MODE.name) { targetView, attributeValue ->
            handleTintMode(targetView, attributeValue)
        }
    }
}

private fun widthAndHeightAttributes() {
    arrayOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attribute ->
        registerAttribute<View, String>(attribute.name) { targetView, attributeValue ->
            setSize(
                targetView, attributeValue, isWidthDimension = true
            )
        }
    }
    arrayOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attribute ->
        registerAttribute<View, String>(attribute.name) { targetView, attributeValue ->
            setSize(
                targetView, attributeValue, isWidthDimension = false
            )
        }
    }
}

private fun marginAndPaddingAttributes() {
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
        registerAttribute<View, String>(attr.name) { targetView, attributeValue ->
            val displayMetrics = targetView.resources.displayMetrics
            val isMargin = attr.name.contains("margin")
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