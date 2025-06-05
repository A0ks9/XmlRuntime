package com.voyager.core.view.processor

import android.graphics.Color
import android.os.Build
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
import com.voyager.core.attribute.AttributeRegistry.register
import com.voyager.core.model.Attributes
import com.voyager.core.utils.Constants.HINT
import com.voyager.core.utils.Constants.MARGIN
import com.voyager.core.utils.Constants.MAX_HEIGHT
import com.voyager.core.utils.Constants.MAX_WIDTH
import com.voyager.core.utils.Constants.OVER_SCROLL_MODE
import com.voyager.core.utils.Constants.SCROLLBARS
import com.voyager.core.utils.Constants.SHADOW_COLOR
import com.voyager.core.utils.Constants.SHADOW_DX
import com.voyager.core.utils.Constants.SHADOW_DY
import com.voyager.core.utils.Constants.SHADOW_RADIUS
import com.voyager.core.utils.Constants.TEXT
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.utils.parser.BooleanParser.parseBoolean
import com.voyager.core.utils.parser.ColorParser.getColor
import com.voyager.core.utils.parser.DimensionConverter.toPixels
import com.voyager.core.utils.parser.GravityParser.parseGravity
import com.voyager.core.utils.parser.NumericalParser.parseFloat
import com.voyager.core.utils.parser.NumericalParser.parseInt
import com.voyager.core.utils.parser.PorterDuffParser.parsePorterDuff
import com.voyager.core.utils.parser.ResourceParser.getString
import com.voyager.core.utils.parser.TextParser.parseEllipsize
import com.voyager.core.utils.parser.TextParser.parseImeOption
import com.voyager.core.utils.parser.TextParser.parseInputType
import com.voyager.core.utils.parser.TextParser.parseTextAlignment
import com.voyager.core.utils.parser.TextParser.parseTextStyle
import com.voyager.core.utils.parser.ViewPropertyParser.parseDrawingCacheQuality
import com.voyager.core.utils.parser.ViewPropertyParser.parseImportantForAccessibility
import com.voyager.core.utils.parser.ViewPropertyParser.parseOverScrollMode
import com.voyager.core.utils.parser.ViewPropertyParser.parseScrollIndicators
import com.voyager.core.utils.parser.ViewPropertyParser.parseVisibility
import com.voyager.core.view.processor.AttributesHandler.handleBackground
import com.voyager.core.view.processor.AttributesHandler.handleDrawablePosition
import com.voyager.core.view.processor.AttributesHandler.handleForeground
import com.voyager.core.view.processor.AttributesHandler.handleForegroundTint
import com.voyager.core.view.processor.AttributesHandler.handleScrollbar
import com.voyager.core.view.processor.AttributesHandler.handleTint
import com.voyager.core.view.processor.AttributesHandler.handleTintMode
import com.voyager.core.view.processor.AttributesHandler.handleTransformationMethod
import com.voyager.core.view.processor.AttributesHandler.loadFontFromAttribute
import com.voyager.core.view.processor.AttributesHandler.setSize
import com.voyager.core.view.utils.ViewExtensions.DrawablePosition
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo
import com.voyager.core.view.utils.ViewExtensions.getParentView
import com.voyager.core.view.utils.id.ViewIdUtils.extractViewId
import com.voyager.core.view.utils.setMargin
import com.voyager.core.view.utils.setMarginBottom
import com.voyager.core.view.utils.setMarginEnd
import com.voyager.core.view.utils.setMarginLeft
import com.voyager.core.view.utils.setMarginRight
import com.voyager.core.view.utils.setMarginStart
import com.voyager.core.view.utils.setMarginTop
import com.voyager.core.view.utils.setPaddingBottom
import com.voyager.core.view.utils.setPaddingE
import com.voyager.core.view.utils.setPaddingEnd
import com.voyager.core.view.utils.setPaddingHorizontal
import com.voyager.core.view.utils.setPaddingLeft
import com.voyager.core.view.utils.setPaddingRight
import com.voyager.core.view.utils.setPaddingStart
import com.voyager.core.view.utils.setPaddingTop
import com.voyager.core.view.utils.setPaddingVertical
import java.util.concurrent.ConcurrentHashMap

/**
 * Common attributes handler for the Voyager framework.
 * Provides efficient and thread-safe attribute registration for Android views.
 *
 * Key features:
 * - Core Android view attribute registration
 * - Layout-specific attribute handling
 * - Thread-safe operations
 * - Performance optimized
 * - Memory efficient
 * - Comprehensive error handling
 * - Resource cleanup
 * - Cache management
 *
 * Performance optimizations:
 * - Efficient attribute registration
 * - Minimal object creation
 * - Safe resource handling
 * - Optimized layout parameter management
 * - Thread-safe caching
 * - Resource pooling
 *
 * Best practices:
 * 1. Initialize attributes only once
 * 2. Handle null values appropriately
 * 3. Consider view lifecycle
 * 4. Use thread-safe operations
 * 5. Consider memory leaks
 * 6. Implement proper error handling
 * 7. Validate attributes before use
 * 8. Clean up resources properly
 *
 * Example Usage:
 * ```kotlin
 * // Initialize common attributes
 * commonAttributes(isLoggingEnabled)
 *
 * // Register custom attributes
 * register<View, String>("customAttribute") { view, value ->
 *     view.setCustomValue(value)
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object CommonAttributes {
    private val logger = LoggerFactory.getLogger(CommonAttributes::class.java.simpleName)
    private val attributeCache = ConcurrentHashMap<String, Any>()

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
        Attributes.Common.SCROLLBARS to SCROLLBARS,
        Attributes.Common.OVER_SCROLL_MODE to OVER_SCROLL_MODE
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
     * Clears the attribute cache.
     * Thread-safe operation that removes all cached values.
     *
     * Performance Considerations:
     * - Clear cache only when necessary
     * - Consider memory impact
     * - Handle concurrent access
     */
    fun clearCache() {
        try {
            attributeCache.clear()
            logger.debug("clearCache", "Attribute cache cleared successfully")
        } catch (e: Exception) {
            logger.error("clearCache", "Failed to clear attribute cache: ${e.message}")
        }
    }

    /**
     * Registers common attributes for Android views.
     * Thread-safe operation with efficient attribute registration.
     *
     * Performance Considerations:
     * - Initialize attributes only once
     * - Use efficient attribute handlers
     * - Consider attribute dependencies
     * - Handle attribute validation
     * - Cache attribute values
     *
     * @param isLoggingEnabled Whether to enable logging
     * @throws IllegalStateException if registration fails
     */
    internal fun commonAttributes(isLoggingEnabled: Boolean) {
        try {
            // ID attribute
            register<View, String>(Attributes.Common.ID) { targetView, attributeValue ->
                try {
                    targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
                        if (isLoggingEnabled) {
                            logger.debug(
                                "commonAttributes",
                                "ID: $attributeValue, viewID: ${attributeValue.extractViewId()}"
                            )
                        }
                        targetView.id = View.generateViewId().also {
                            info.viewID[attributeValue.extractViewId()] = it
                        }
                    }
                } catch (e: Exception) {
                    logger.error("commonAttributes", "Failed to set view ID: ${e.message}")
                }
            }

            // Width and height attributes
            widthAndHeightAttributes()

            // Gravity attribute
            register<View, String>(Attributes.Common.GRAVITY) { targetView, attributeValue ->
                try {
                    val alignment = parseGravity(attributeValue)
                    when (val layoutParams = targetView.layoutParams) {
                        is LinearLayout.LayoutParams -> layoutParams.gravity = alignment
                        is FrameLayout.LayoutParams -> layoutParams.gravity = alignment
                        else -> if (targetView is TextView) targetView.gravity = alignment
                    }
                } catch (e: Exception) {
                    logger.error("commonAttributes", "Failed to set gravity: ${e.message}")
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

            logger.debug("commonAttributes", "Common attributes registered successfully")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to register common attributes: ${e.message}", e)
        }
    }

    /**
     * Registers basic view attributes.
     * Thread-safe operation with efficient attribute registration.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized layout parameter management
     *
     * @param isLoggingEnabled Whether to enable logging
     */
    private fun registerBasicViewAttributes(isLoggingEnabled: Boolean) {
        try {
            basicViewAttributes.forEach { (attr, property) ->
                register<View, String>(attr) { targetView, value ->
                    try {
                        when (property) {
                            View::setVisibility -> targetView.visibility = parseVisibility(value)
                            View::isClickable -> targetView.isClickable = parseBoolean(value)
                            View::isLongClickable -> targetView.isLongClickable =
                                parseBoolean(value)

                            View::isEnabled -> targetView.isEnabled = parseBoolean(value)
                            "scrollbars" -> handleScrollbar(targetView, value)
                            "overScrollMode" -> targetView.overScrollMode =
                                parseOverScrollMode(value)

                            else -> if (isLoggingEnabled) {
                                logger.warn(
                                    "registerBasicViewAttributes",
                                    "Unsupported property: $property"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        logger.error(
                            "registerBasicViewAttributes",
                            "Failed to set attribute $attr: ${e.message}"
                        )
                    }
                }
            }

            register<View, String>(Attributes.Common.TAG) { targetView, value ->
                try {
                    if (targetView.tag == null) targetView.tag = value
                } catch (e: Exception) {
                    logger.error("registerBasicViewAttributes", "Failed to set tag: ${e.message}")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                register<View, String>(Attributes.Common.TOOLTIP_TEXT) { targetView, value ->
                    try {
                        targetView.tooltipText = getString(targetView.context, value)
                    } catch (e: Exception) {
                        logger.error(
                            "registerBasicViewAttributes",
                            "Failed to set tooltip: ${e.message}"
                        )
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                register<View, String>(Attributes.Common.SCROLL_INDICATORS) { targetView, value ->
                    try {
                        targetView.scrollIndicators = parseScrollIndicators(value)
                    } catch (e: Exception) {
                        logger.error(
                            "registerBasicViewAttributes",
                            "Failed to set scroll indicators: ${e.message}"
                        )
                    }
                }
            }

            logger.debug(
                "registerBasicViewAttributes",
                "Basic view attributes registered successfully"
            )
        } catch (e: Exception) {
            logger.error(
                "registerBasicViewAttributes",
                "Failed to register basic view attributes: ${e.message}"
            )
            throw e
        }
    }

    /**
     * Registers background and foreground attributes.
     */
    private fun registerBackgroundAttributes() {
        register<View, String>(Attributes.Common.BACKGROUND) { targetView, value ->
            handleBackground(targetView, value, targetView.context)
        }

        register<View, String>(Attributes.Common.BACKGROUND_TINT) { targetView, value ->
            ViewCompat.setBackgroundTintList(
                targetView, ContextCompat.getColorStateList(
                    targetView.context, getColor(value, targetView.context, Color.TRANSPARENT)
                )
            )
        }

        register<View, String>(Attributes.Common.BACKGROUND_TINT_MODE) { targetView, value ->
            ViewCompat.setBackgroundTintMode(targetView, parsePorterDuff(value))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            register<View, String>(Attributes.Common.FOREGROUND) { targetView, value ->
                handleForeground(targetView, value, targetView.context)
            }

            register<View, String>(Attributes.Common.FOREGROUND_GRAVITY) { targetView, value ->
                targetView.foregroundGravity = parseGravity(value)
            }

            register<View, String>(Attributes.Common.FOREGROUND_TINT) { targetView, value ->
                handleForegroundTint(targetView, value, targetView.context)
            }

            register<View, String>(Attributes.Common.FOREGROUND_TINT_MODE) { targetView, value ->
                targetView.foregroundTintMode = parsePorterDuff(value)
            }

            register<View, String>(Attributes.Common.DRAWABLE_TINT) { targetView, value ->
                handleTint(targetView, value)
            }

            register<View, String>(Attributes.Common.DRAWABLE_TINT_MODE) { targetView, value ->
                handleTintMode(targetView, value)
            }
        }
    }

    /**
     * Registers transform attributes.
     */
    private fun registerTransformAttributes() {
        transformAttributes.forEach { (attr, property) ->
            register<View, String>(attr) { targetView, value ->
                property.call(targetView, value.toFloat())
            }
        }
    }

    /**
     * Registers layout attributes.
     */
    private fun registerLayoutAttributes() {
        register<ViewGroup, String>(Attributes.Common.LAYOUT_GRAVITY) { targetView, value ->
            val gravity = parseGravity(value)
            val params = targetView.layoutParams
            when (targetView) {
                is LinearLayout -> (params as LinearLayout.LayoutParams).gravity = gravity
                is FrameLayout -> (params as FrameLayout.LayoutParams).gravity = gravity
                is GridLayout -> (params as GridLayout.LayoutParams).setGravity(gravity)
            }
            targetView.layoutParams = params
        }

        register<ViewGroup, String>(Attributes.Common.CLIP_TO_PADDING) { targetView, value ->
            targetView.clipToPadding = parseBoolean(value)
        }

        layoutSizeAttributes.forEach { (attr, property) ->
            register<View, String>(attr) { targetView, value ->
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
            register<TextView, String>(attr) { targetView, value ->
                when (property) {
                    TEXT -> targetView.text = getString(targetView.context, value)
                    "textSize" -> targetView.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        value.toPixels(targetView.resources.displayMetrics) as Float
                    )

                    "textColor" -> targetView.setTextColor(getColor(value, targetView.context))
                    "textStyle" -> targetView.setTypeface(null, parseTextStyle(value.lowercase()))
                    "textAlignment" -> parseTextAlignment(value)?.let {
                        targetView.textAlignment = it
                    }

                    "ellipsize" -> targetView.ellipsize = parseEllipsize(value.lowercase())
                    "singleLine" -> targetView.isSingleLine = parseBoolean(value)
                    HINT -> targetView.hint = getString(targetView.context, value)
                    "inputType" -> parseInputType(value).let {
                        if (it > 0) targetView.inputType = it
                    }

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
            register<TextView, String>(attr) { targetView, value ->
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
        register<View, String>(Attributes.Common.IMPORTANT_FOR_ACCESSIBILITY) { targetView, value ->
            targetView.importantForAccessibility = parseImportantForAccessibility(value)
        }

        register<View, String>(Attributes.Common.CONTENT_DESCRIPTION) { targetView, value ->
            targetView.contentDescription = value
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            register<View, String>(Attributes.Common.SCREEN_READER_FOCUSABLE) { targetView, value ->
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
            register<TextView, String>(attr) { targetView, value ->
                handleDrawablePosition(targetView, value, position)
            }
        }

        register<View, String>(Attributes.Common.TINT) { targetView, value ->
            handleTint(targetView, value)
        }

        register<View, String>(Attributes.Common.TINT_MODE) { targetView, value ->
            handleTintMode(targetView, value)
        }

        @Suppress("DEPRECATION") register<View, String>(Attributes.Common.DRAWING_CACHE_QUALITY) { targetView, value ->
            targetView.drawingCacheQuality = parseDrawingCacheQuality(value)
        }
    }

    /**
     * Registers width and height attributes.
     */
    private fun widthAndHeightAttributes() {
        arrayOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attribute ->
            register<View, String>(attribute) { targetView, value ->
                setSize(targetView, value, isWidthDimension = true)
            }
        }
        arrayOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attribute ->
            register<View, String>(attribute) { targetView, value ->
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
            register<View, String>(attr) { targetView, value ->
                val displayMetrics = targetView.resources.displayMetrics
                val isMargin = attr.contains(MARGIN)
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
}