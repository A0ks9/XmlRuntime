/**
 * Common attributes handler for the Voyager framework.
 *
 * This file provides efficient registration and handling of common Android view attributes,
 * including layout parameters, styling, and view-specific attributes.
 *
 * Key features:
 * - Efficient attribute registration
 * - Optimized layout parameter handling
 * - Memory-efficient attribute processing
 * - Thread-safe operations
 * - Comprehensive attribute support
 *
 * Performance optimizations:
 * - Lazy initialization of attributes
 * - Caching of frequently used values
 * - Efficient string operations
 * - Optimized view traversal
 * - Reduced object creation
 *
 * Usage example:
 * ```kotlin
 * // Initialize common attributes
 * commonAttributes(isLoggingEnabled = true)
 *
 * // Register custom attributes
 * registerAttribute<TextView, String>("text") { view, value ->
 *     view.text = value
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

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
import com.voyager.utils.ParseHelper.parseEllipsize
import com.voyager.utils.ParseHelper.parseFloat
import com.voyager.utils.ParseHelper.parseGravity
import com.voyager.utils.ParseHelper.parseImeOption
import com.voyager.utils.ParseHelper.parseImportantForAccessibility
import com.voyager.utils.ParseHelper.parseInputType
import com.voyager.utils.ParseHelper.parseInt
import com.voyager.utils.ParseHelper.parseOverScrollMode
import com.voyager.utils.ParseHelper.parsePorterDuff
import com.voyager.utils.ParseHelper.parseScrollIndicators
import com.voyager.utils.ParseHelper.parseTextAlignment
import com.voyager.utils.ParseHelper.parseTextStyle
import com.voyager.utils.ParseHelper.parseVisibility
import com.voyager.utils.Utils.DrawablePosition
import com.voyager.utils.Utils.getGeneratedViewInfo
import com.voyager.utils.extractViewId
import com.voyager.utils.getParentView
import com.voyager.utils.processors.AttributeProcessor.registerAttribute
import com.voyager.utils.setMargin
import com.voyager.utils.setMarginBottom
import com.voyager.utils.setMarginEnd
import com.voyager.utils.setMarginLeft
import com.voyager.utils.setMarginRight
import com.voyager.utils.setMarginStart
import com.voyager.utils.setMarginTop
import com.voyager.utils.setPaddingBottom
import com.voyager.utils.setPaddingE
import com.voyager.utils.setPaddingEnd
import com.voyager.utils.setPaddingHorizontal
import com.voyager.utils.setPaddingLeft
import com.voyager.utils.setPaddingRight
import com.voyager.utils.setPaddingStart
import com.voyager.utils.setPaddingTop
import com.voyager.utils.setPaddingVertical
import com.voyager.utils.toPixels
import com.voyager.utils.view.AttributesHandler.handleBackground
import com.voyager.utils.view.AttributesHandler.handleDrawablePosition
import com.voyager.utils.view.AttributesHandler.handleForeground
import com.voyager.utils.view.AttributesHandler.handleForegroundTint
import com.voyager.utils.view.AttributesHandler.handleScrollbar
import com.voyager.utils.view.AttributesHandler.handleTint
import com.voyager.utils.view.AttributesHandler.handleTintMode
import com.voyager.utils.view.AttributesHandler.handleTransformationMethod
import com.voyager.utils.view.AttributesHandler.loadFontFromAttribute
import com.voyager.utils.view.AttributesHandler.setSize

// Constants for attribute names and frequently used values
private const val MARGIN = "margin"
private const val TEXT = "text"
private const val HINT = "hint"
private const val MAX_WIDTH = "maxWidth"
private const val MAX_HEIGHT = "maxHeight"
private const val SHADOW_COLOR = "shadowColor"
private const val SHADOW_RADIUS = "shadowRadius"
private const val SHADOW_DX = "shadowDx"
private const val SHADOW_DY = "shadowDy"

// Cache for frequently used values
private val horizontalMarginAttributes = setOf(
    Attributes.Common.LAYOUT_MARGIN_LEFT,
    Attributes.Common.LAYOUT_MARGIN_START,
    Attributes.Common.LAYOUT_MARGIN_RIGHT,
    Attributes.Common.LAYOUT_MARGIN_END
)

// Reusable arrays for attribute registration
private val basicViewAttributes = arrayOf(
    Attributes.Common.VISIBILITY to View::setVisibility,
    Attributes.Common.CLICKABLE to View::isClickable,
    Attributes.Common.LONG_CLICKABLE to View::isLongClickable,
    Attributes.Common.ENABLED to View::isEnabled,
    Attributes.Common.SCROLLBARS to "scrollbars",
    Attributes.Common.OVER_SCROLL_MODE to "overScrollMode"
)

private val transformAttributes = arrayOf(
    Attributes.Common.ROTATION to View::setRotation,
    Attributes.Common.ROTATION_X to View::setRotationX,
    Attributes.Common.ROTATION_Y to View::setRotationY,
    Attributes.Common.SCALE_X to View::setScaleX,
    Attributes.Common.SCALE_Y to View::setScaleY,
    Attributes.Common.TRANSLATION_X to View::setTranslationX,
    Attributes.Common.TRANSLATION_Y to View::setTranslationY,
    Attributes.Common.TRANSLATION_Z to View::setTranslationZ,
    Attributes.Common.ELEVATION to View::setElevation,
    Attributes.Common.ALPHA to View::setAlpha
)

private val layoutSizeAttributes = arrayOf(
    Attributes.Common.MIN_WIDTH to View::setMinimumWidth,
    Attributes.Common.MIN_HEIGHT to View::setMinimumHeight,
    Attributes.Common.MAX_WIDTH to MAX_WIDTH,
    Attributes.Common.MAX_HEIGHT to MAX_HEIGHT
)

private val textViewAttributes = arrayOf(
    Attributes.Common.TEXT to TEXT,
    Attributes.Common.TEXT_SIZE to "textSize",
    Attributes.Common.TEXT_COLOR to "textColor",
    Attributes.Common.TEXT_STYLE to "textStyle",
    Attributes.View.VIEW_TEXT_ALIGNMENT to "textAlignment",
    Attributes.Common.ELLIPSIZE to "ellipsize",
    Attributes.TextView.TEXTVIEW_SINGLE_LINE to "singleLine",
    Attributes.Common.HINT to HINT,
    Attributes.TextView.TEXTVIEW_INPUT_TYPE to "inputType",
    Attributes.Common.TEXT_COLOR_HINT to "HintColor",
    Attributes.Common.LETTER_SPACING to "letterSpacing",
    Attributes.Common.LINE_SPACING_EXTRA to "lineSpacingExtra",
    Attributes.Common.LINE_SPACING_MULTIPLIER to "lineSpacingMultiplier",
    Attributes.Common.TEXT_ALL_CAPS to "isAllCaps",
    Attributes.Common.MAX_LINES to "maxLines",
    Attributes.Common.IME_OPTIONS to "imeOptions",
    Attributes.Common.FONT_FAMILY to "fontFamily",
    Attributes.Common.TEXT_SCALE_X to "textScaleX",
    Attributes.Common.TRANSFORMATION_METHOD to "transformationMethod",
    Attributes.Common.DRAWABLE_PADDING to "drawablePadding"
)

private val shadowAttributes = arrayOf(
    Attributes.Common.SHADOW_COLOR to SHADOW_COLOR,
    Attributes.Common.SHADOW_RADIUS to SHADOW_RADIUS,
    Attributes.Common.SHADOW_DX to SHADOW_DX,
    Attributes.Common.SHADOW_DY to SHADOW_DY
)

/**
 * Registers common attributes for views.
 *
 * @param isLoggingEnabled Whether to enable logging for debugging
 */
internal fun commonAttributes(isLoggingEnabled: Boolean) {
    // ID attribute
    registerAttribute<View, String>(Attributes.Common.ID.name) { targetView, attributeValue ->
        targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
            if (isLoggingEnabled) Log.d(
                "view", "ID: $attributeValue, viewID: ${attributeValue.extractViewId()}"
            )
            targetView.id = View.generateViewId().also {
                info.viewID[attributeValue.extractViewId()] = it
            }
        }
    }

    // Width and height attributes
    widthAndHeightAttributes()

    // Gravity attribute
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

    // Margin and padding attributes
    marginAndPaddingAttributes()

    // Basic view attributes
    registerBasicViewAttributes(isLoggingEnabled)

    // Background and foreground attributes
    registerBackgroundAttributes()

    // Transform attributes
    registerTransformAttributes()

    // Layout attributes
    registerLayoutAttributes()

    // Text view specific attributes
    registerTextViewAttributes()

    // Accessibility attributes
    registerAccessibilityAttributes()

    // Drawable attributes
    registerDrawableAttributes()
}

/**
 * Registers basic view attributes.
 */
private fun registerBasicViewAttributes(isLoggingEnabled: Boolean) {
    basicViewAttributes.forEach { (attr, property) ->
        registerAttribute<View, String>(attr.name) { targetView, value ->
            when (property) {
                View::setVisibility -> targetView.visibility = parseVisibility(value)
                View::isClickable -> targetView.isClickable = parseBoolean(value)
                View::isLongClickable -> targetView.isLongClickable = parseBoolean(value)
                View::isEnabled -> targetView.isEnabled = parseBoolean(value)
                "scrollbars" -> handleScrollbar(targetView, value)
                "overScrollMode" -> targetView.overScrollMode = parseOverScrollMode(value)
                else -> if (isLoggingEnabled) Log.w("view", "Unsupported property: $property")
            }
        }
    }

    registerAttribute<View, String>(Attributes.Common.TAG.name) { targetView, value ->
        if (targetView.tag == null) targetView.tag = value
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        registerAttribute<View, String>(Attributes.Common.TOOLTIP_TEXT.name) { targetView, value ->
            targetView.tooltipText = getString(targetView.context, value)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        registerAttribute<View, String>(Attributes.Common.SCROLL_INDICATORS.name) { targetView, value ->
            targetView.scrollIndicators = parseScrollIndicators(value)
        }
    }
}

/**
 * Registers background and foreground attributes.
 */
private fun registerBackgroundAttributes() {
    registerAttribute<View, String>(Attributes.Common.BACKGROUND.name) { targetView, value ->
        handleBackground(targetView, value, targetView.context)
    }

    registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT.name) { targetView, value ->
        ViewCompat.setBackgroundTintList(
            targetView, ContextCompat.getColorStateList(
                targetView.context, getColor(value, targetView.context, Color.TRANSPARENT)
            )
        )
    }

    registerAttribute<View, String>(Attributes.Common.BACKGROUND_TINT_MODE.name) { targetView, value ->
        ViewCompat.setBackgroundTintMode(targetView, parsePorterDuff(value))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        registerAttribute<View, String>(Attributes.Common.FOREGROUND.name) { targetView, value ->
            handleForeground(targetView, value, targetView.context)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_GRAVITY.name) { targetView, value ->
            targetView.foregroundGravity = parseGravity(value)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT.name) { targetView, value ->
            handleForegroundTint(targetView, value, targetView.context)
        }

        registerAttribute<View, String>(Attributes.Common.FOREGROUND_TINT_MODE.name) { targetView, value ->
            targetView.foregroundTintMode = parsePorterDuff(value)
        }

        registerAttribute<View, String>(Attributes.Common.DRAWABLE_TINT.name) { targetView, value ->
            handleTint(targetView, value)
        }

        registerAttribute<View, String>(Attributes.Common.DRAWABLE_TINT_MODE.name) { targetView, value ->
            handleTintMode(targetView, value)
        }
    }
}

/**
 * Registers transform attributes.
 */
private fun registerTransformAttributes() {
    transformAttributes.forEach { (attr, property) ->
        registerAttribute<View, String>(attr.name) { targetView, value ->
            property.call(targetView, value.toFloat())
        }
    }
}

/**
 * Registers layout attributes.
 */
private fun registerLayoutAttributes() {
    registerAttribute<ViewGroup, String>(Attributes.Common.LAYOUT_GRAVITY.name) { targetView, value ->
        val gravity = parseGravity(value)
        val params = targetView.layoutParams
        when (targetView) {
            is LinearLayout -> (params as LinearLayout.LayoutParams).gravity = gravity
            is FrameLayout -> (params as FrameLayout.LayoutParams).gravity = gravity
            is GridLayout -> (params as GridLayout.LayoutParams).setGravity(gravity)
        }
        targetView.layoutParams = params
    }

    registerAttribute<ViewGroup, String>(Attributes.Common.CLIP_TO_PADDING.name) { targetView, value ->
        targetView.clipToPadding = parseBoolean(value)
    }

    layoutSizeAttributes.forEach { (attr, property) ->
        registerAttribute<View, String>(attr.name) { targetView, value ->
            val pixels = value.toPixels(
                targetView.resources.displayMetrics,
                targetView.getParentView(),
                attr == Attributes.Common.MIN_WIDTH || attr == Attributes.Common.MAX_WIDTH,
                true
            ) as Int

            when (property) {
                View::setMinimumWidth -> targetView.minimumWidth = pixels
                View::setMinimumHeight -> targetView.minimumHeight = pixels
                MAX_WIDTH -> {
                    when (targetView) {
                        is TextView -> targetView.maxWidth = pixels
                        is ImageView -> targetView.maxWidth = pixels
                        else -> targetView.layoutParams =
                            targetView.layoutParams.apply { width = pixels }
                    }
                    targetView.requestLayout()
                }

                MAX_HEIGHT -> {
                    when (targetView) {
                        is TextView -> targetView.maxHeight = pixels
                        is ImageView -> targetView.maxHeight = pixels
                        else -> targetView.layoutParams =
                            targetView.layoutParams.apply { height = pixels }
                    }
                    targetView.requestLayout()
                }

                else -> {}
            }
        }
    }
}

/**
 * Registers text view specific attributes.
 */
private fun registerTextViewAttributes() {
    textViewAttributes.forEach { (attr, property) ->
        registerAttribute<TextView, String>(attr.name) { targetView, value ->
            when (property) {
                TEXT -> targetView.text = getString(targetView.context, value)
                "textSize" -> targetView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    value.toPixels(targetView.resources.displayMetrics) as Float
                )

                "textColor" -> targetView.setTextColor(getColor(value, targetView.context))
                "textStyle" -> targetView.setTypeface(null, parseTextStyle(value.lowercase()))
                "textAlignment" -> parseTextAlignment(value)?.let { targetView.textAlignment = it }
                "ellipsize" -> targetView.ellipsize = parseEllipsize(value.lowercase())
                "singleLine" -> targetView.isSingleLine = parseBoolean(value)
                HINT -> targetView.hint = getString(targetView.context, value)
                "inputType" -> parseInputType(value).let { if (it > 0) targetView.inputType = it }
                "HintColor" -> targetView.setHintTextColor(
                    getColor(
                        value, targetView.context, Color.GRAY
                    )
                )

                "letterSpacing" -> targetView.letterSpacing = parseFloat(value)
                "lineSpacingExtra" -> targetView.setLineSpacing(
                    parseFloat(value), targetView.lineSpacingMultiplier
                )

                "lineSpacingMultiplier" -> targetView.setLineSpacing(
                    targetView.lineSpacingExtra, parseFloat(value)
                )

                "isAllCaps" -> targetView.isAllCaps = parseBoolean(value)
                "maxLines" -> targetView.maxLines = parseInt(value)
                "imeOptions" -> targetView.imeOptions = parseImeOption(value)
                "fontFamily" -> loadFontFromAttribute(
                    targetView.context, value
                )?.let { targetView.typeface = it }

                "textScaleX" -> {
                    val currentSp =
                        targetView.textSize / targetView.context.resources.configuration.fontScale
                    targetView.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP, currentSp * parseFloat(value)
                    )
                }

                "transformationMethod" -> handleTransformationMethod(targetView, value)
                "drawablePadding" -> {
                    val padding = value.toPixels(
                        targetView.context.resources.displayMetrics, asInt = true
                    ) as Int
                    targetView.compoundDrawablePadding = padding
                }

                else -> {}
            }
        }
    }

    shadowAttributes.forEach { (attr, property) ->
        registerAttribute<TextView, String>(attr.name) { targetView, value ->
            when (property) {
                SHADOW_COLOR -> targetView.setShadowLayer(
                    targetView.shadowRadius,
                    targetView.shadowDx,
                    targetView.shadowDy,
                    getColor(value, targetView.context, Color.TRANSPARENT)
                )

                SHADOW_RADIUS -> targetView.setShadowLayer(
                    parseFloat(value),
                    targetView.shadowDx,
                    targetView.shadowDy,
                    targetView.shadowColor
                )

                SHADOW_DX -> targetView.setShadowLayer(
                    targetView.shadowRadius,
                    parseFloat(value),
                    targetView.shadowDy,
                    targetView.shadowColor
                )

                SHADOW_DY -> targetView.setShadowLayer(
                    targetView.shadowRadius,
                    targetView.shadowDx,
                    parseFloat(value),
                    targetView.shadowColor
                )

                else -> {}
            }
        }
    }
}

/**
 * Registers accessibility attributes.
 */
private fun registerAccessibilityAttributes() {
    registerAttribute<View, String>(Attributes.Common.IMPORTANT_FOR_ACCESSIBILITY.name) { targetView, value ->
        targetView.importantForAccessibility = parseImportantForAccessibility(value)
    }

    registerAttribute<View, String>(Attributes.Common.CONTENT_DESCRIPTION.name) { targetView, value ->
        targetView.contentDescription = value
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        registerAttribute<View, String>(Attributes.Common.SCREEN_READER_FOCUSABLE.name) { targetView, value ->
            targetView.isScreenReaderFocusable = parseBoolean(value)
        }
    }
}

/**
 * Registers drawable attributes.
 */
private fun registerDrawableAttributes() {
    arrayOf(
        Attributes.Common.DRAWABLE_START to DrawablePosition.START,
        Attributes.Common.DRAWABLE_END to DrawablePosition.END,
        Attributes.Common.DRAWABLE_TOP to DrawablePosition.TOP,
        Attributes.Common.DRAWABLE_BOTTOM to DrawablePosition.BOTTOM
    ).forEach { (attr, position) ->
        registerAttribute<TextView, String>(attr.name) { targetView, value ->
            handleDrawablePosition(targetView, value, position)
        }
    }

    registerAttribute<View, String>(Attributes.Common.TINT.name) { targetView, value ->
        handleTint(targetView, value)
    }

    registerAttribute<View, String>(Attributes.Common.TINT_MODE.name) { targetView, value ->
        handleTintMode(targetView, value)
    }

    @Suppress("DEPRECATION") registerAttribute<View, String>(Attributes.Common.DRAWING_CACHE_QUALITY.name) { targetView, value ->
        targetView.drawingCacheQuality = parseDrawingCacheQuality(value)
    }
}

/**
 * Registers width and height attributes.
 */
private fun widthAndHeightAttributes() {
    arrayOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attribute ->
        registerAttribute<View, String>(attribute.name) { targetView, value ->
            setSize(targetView, value, isWidthDimension = true)
        }
    }
    arrayOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attribute ->
        registerAttribute<View, String>(attribute.name) { targetView, value ->
            setSize(targetView, value, isWidthDimension = false)
        }
    }
}

/**
 * Registers margin and padding attributes.
 */
private fun marginAndPaddingAttributes() {
    arrayOf(
        Attributes.Common.LAYOUT_MARGIN to View::setMargin,
        Attributes.Common.LAYOUT_MARGIN_LEFT to View::setMarginLeft,
        Attributes.Common.LAYOUT_MARGIN_RIGHT to View::setMarginRight,
        Attributes.Common.LAYOUT_MARGIN_START to View::setMarginStart,
        Attributes.Common.LAYOUT_MARGIN_END to View::setMarginEnd,
        Attributes.Common.LAYOUT_MARGIN_TOP to View::setMarginTop,
        Attributes.Common.LAYOUT_MARGIN_BOTTOM to View::setMarginBottom,
        Attributes.Common.PADDING to View::setPaddingE,
        Attributes.Common.PADDING_LEFT to View::setPaddingLeft,
        Attributes.Common.PADDING_RIGHT to View::setPaddingRight,
        Attributes.Common.PADDING_START to View::setPaddingStart,
        Attributes.Common.PADDING_END to View::setPaddingEnd,
        Attributes.Common.PADDING_TOP to View::setPaddingTop,
        Attributes.Common.PADDING_BOTTOM to View::setPaddingBottom,
        Attributes.Common.PADDING_HORIZONTAL to View::setPaddingHorizontal,
        Attributes.Common.PADDING_VERTICAL to View::setPaddingVertical
    ).forEach { (attr, func) ->
        registerAttribute<View, String>(attr.name) { targetView, value ->
            val displayMetrics = targetView.resources.displayMetrics
            val isMargin = attr.name.contains(MARGIN)
            val pixels = if (isMargin) {
                val isHorizontal = attr in horizontalMarginAttributes
                value.toPixels(displayMetrics, targetView.getParentView(), isHorizontal, true)
            } else {
                value.toPixels(displayMetrics, asInt = true)
            }
            func.call(targetView, pixels as Int)
        }
    }
}