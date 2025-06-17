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
import com.voyager.core.attribute.attributesForView
import com.voyager.core.model.Attributes.Common.ALPHA
import com.voyager.core.model.Attributes.Common.BACKGROUND
import com.voyager.core.model.Attributes.Common.BACKGROUND_TINT
import com.voyager.core.model.Attributes.Common.BACKGROUND_TINT_MODE
import com.voyager.core.model.Attributes.Common.CLICKABLE
import com.voyager.core.model.Attributes.Common.CLIP_TO_PADDING
import com.voyager.core.model.Attributes.Common.CONTENT_DESCRIPTION
import com.voyager.core.model.Attributes.Common.DRAWABLE_BOTTOM
import com.voyager.core.model.Attributes.Common.DRAWABLE_END
import com.voyager.core.model.Attributes.Common.DRAWABLE_PADDING
import com.voyager.core.model.Attributes.Common.DRAWABLE_START
import com.voyager.core.model.Attributes.Common.DRAWABLE_TOP
import com.voyager.core.model.Attributes.Common.ELEVATION
import com.voyager.core.model.Attributes.Common.ELLIPSIZE
import com.voyager.core.model.Attributes.Common.ENABLED
import com.voyager.core.model.Attributes.Common.FONT_FAMILY
import com.voyager.core.model.Attributes.Common.FOREGROUND
import com.voyager.core.model.Attributes.Common.FOREGROUND_GRAVITY
import com.voyager.core.model.Attributes.Common.FOREGROUND_TINT
import com.voyager.core.model.Attributes.Common.FOREGROUND_TINT_MODE
import com.voyager.core.model.Attributes.Common.HEIGHT
import com.voyager.core.model.Attributes.Common.HINT
import com.voyager.core.model.Attributes.Common.ID
import com.voyager.core.model.Attributes.Common.IME_OPTIONS
import com.voyager.core.model.Attributes.Common.IMPORTANT_FOR_ACCESSIBILITY
import com.voyager.core.model.Attributes.Common.LAYOUT_GRAVITY
import com.voyager.core.model.Attributes.Common.LAYOUT_HEIGHT
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_BOTTOM
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_END
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_LEFT
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_RIGHT
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_START
import com.voyager.core.model.Attributes.Common.LAYOUT_MARGIN_TOP
import com.voyager.core.model.Attributes.Common.LAYOUT_WIDTH
import com.voyager.core.model.Attributes.Common.LETTER_SPACING
import com.voyager.core.model.Attributes.Common.LINE_SPACING_EXTRA
import com.voyager.core.model.Attributes.Common.LINE_SPACING_MULTIPLIER
import com.voyager.core.model.Attributes.Common.LONG_CLICKABLE
import com.voyager.core.model.Attributes.Common.MAX_HEIGHT
import com.voyager.core.model.Attributes.Common.MAX_LINES
import com.voyager.core.model.Attributes.Common.MAX_WIDTH
import com.voyager.core.model.Attributes.Common.MIN_HEIGHT
import com.voyager.core.model.Attributes.Common.MIN_WIDTH
import com.voyager.core.model.Attributes.Common.OVER_SCROLL_MODE
import com.voyager.core.model.Attributes.Common.PADDING
import com.voyager.core.model.Attributes.Common.PADDING_BOTTOM
import com.voyager.core.model.Attributes.Common.PADDING_END
import com.voyager.core.model.Attributes.Common.PADDING_HORIZONTAL
import com.voyager.core.model.Attributes.Common.PADDING_LEFT
import com.voyager.core.model.Attributes.Common.PADDING_RIGHT
import com.voyager.core.model.Attributes.Common.PADDING_START
import com.voyager.core.model.Attributes.Common.PADDING_TOP
import com.voyager.core.model.Attributes.Common.PADDING_VERTICAL
import com.voyager.core.model.Attributes.Common.ROTATION
import com.voyager.core.model.Attributes.Common.ROTATION_X
import com.voyager.core.model.Attributes.Common.ROTATION_Y
import com.voyager.core.model.Attributes.Common.SCALE_X
import com.voyager.core.model.Attributes.Common.SCALE_Y
import com.voyager.core.model.Attributes.Common.SCREEN_READER_FOCUSABLE
import com.voyager.core.model.Attributes.Common.SCROLLBARS
import com.voyager.core.model.Attributes.Common.SCROLL_INDICATORS
import com.voyager.core.model.Attributes.Common.SHADOW_COLOR
import com.voyager.core.model.Attributes.Common.SHADOW_DX
import com.voyager.core.model.Attributes.Common.SHADOW_DY
import com.voyager.core.model.Attributes.Common.SHADOW_RADIUS
import com.voyager.core.model.Attributes.Common.TAG
import com.voyager.core.model.Attributes.Common.TEXT
import com.voyager.core.model.Attributes.Common.TEXT_ALL_CAPS
import com.voyager.core.model.Attributes.Common.TEXT_COLOR
import com.voyager.core.model.Attributes.Common.TEXT_COLOR_HINT
import com.voyager.core.model.Attributes.Common.TEXT_SCALE_X
import com.voyager.core.model.Attributes.Common.TEXT_SIZE
import com.voyager.core.model.Attributes.Common.TEXT_STYLE
import com.voyager.core.model.Attributes.Common.TOOLTIP_TEXT
import com.voyager.core.model.Attributes.Common.TRANSFORMATION_METHOD
import com.voyager.core.model.Attributes.Common.TRANSLATION_X
import com.voyager.core.model.Attributes.Common.TRANSLATION_Y
import com.voyager.core.model.Attributes.Common.TRANSLATION_Z
import com.voyager.core.model.Attributes.Common.VISIBILITY
import com.voyager.core.model.Attributes.Common.WIDTH
import com.voyager.core.model.Attributes.TextView.TEXTVIEW_INPUT_TYPE
import com.voyager.core.model.Attributes.TextView.TEXTVIEW_SINGLE_LINE
import com.voyager.core.model.Attributes.View.VIEW_TEXT_ALIGNMENT
import com.voyager.core.utils.StringUtils.extractViewId
import com.voyager.core.utils.parser.BooleanParser.toBoolean
import com.voyager.core.utils.parser.ColorParser.toColor
import com.voyager.core.utils.parser.DimensionConverter.toPixels
import com.voyager.core.utils.parser.GravityParser.parseGravity
import com.voyager.core.utils.parser.PorterDuffParser.parsePorterDuff
import com.voyager.core.utils.parser.ResourceParser.getString
import com.voyager.core.utils.parser.TextParser.parseEllipsize
import com.voyager.core.utils.parser.TextParser.parseImeOption
import com.voyager.core.utils.parser.TextParser.parseInputType
import com.voyager.core.utils.parser.TextParser.parseTextAlignment
import com.voyager.core.utils.parser.TextParser.parseTextStyle
import com.voyager.core.utils.parser.ViewPropertyParser.parseImportantForAccessibility
import com.voyager.core.utils.parser.ViewPropertyParser.parseOverScrollMode
import com.voyager.core.utils.parser.ViewPropertyParser.parseScrollIndicators
import com.voyager.core.utils.parser.ViewPropertyParser.parseVisibility
import com.voyager.core.view.processor.AttributesHandler.handleBackground
import com.voyager.core.view.processor.AttributesHandler.handleDrawablePosition
import com.voyager.core.view.processor.AttributesHandler.handleForeground
import com.voyager.core.view.processor.AttributesHandler.handleForegroundTint
import com.voyager.core.view.processor.AttributesHandler.handleScrollbar
import com.voyager.core.view.processor.AttributesHandler.handleTransformationMethod
import com.voyager.core.view.processor.AttributesHandler.loadFontFromAttribute
import com.voyager.core.view.processor.AttributesHandler.setSize
import com.voyager.core.view.utils.ViewExtensions.DrawablePosition
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo
import com.voyager.core.view.utils.ViewExtensions.getParentView
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Optimized common attributes handler for the Voyager framework.
 * Provides efficient and thread-safe attribute registration for Android views.
 */
internal object CommonAttributes {
    private val attributeCache by lazy { ConcurrentHashMap<String, Any>() }

    fun clearCache() = attributeCache.clear()

    internal suspend fun commonAttributes() = coroutineScope {
        launch { registerViewAttributes() }
        launch { registerTextViewAttributes() }
        launch { registerViewGroupAttributes() }
    }

    private fun registerViewAttributes() {
        attributesForView<View> {
            // Basic View Attributes
            attribute<String>(ID) { view, value ->
                view.getParentView()?.getGeneratedViewInfo()?.let { info ->
                    view.id = View.generateViewId().also {
                        info.viewID[value.extractViewId()] = it
                    }
                }
            }
            attribute<String>(VISIBILITY) { view, value ->
                view.visibility = parseVisibility(value)
            }
            attribute<String>(CLICKABLE) { view, value -> view.isClickable = value.toBoolean }
            attribute<String>(LONG_CLICKABLE) { view, value ->
                view.isLongClickable = value.toBoolean
            }
            attribute<String>(ENABLED) { view, value -> view.isEnabled = value.toBoolean }
            attribute<String>(SCROLLBARS) { view, value -> handleScrollbar(view, value) }
            attribute<String>(OVER_SCROLL_MODE) { view, value ->
                view.overScrollMode = parseOverScrollMode(value)
            }
            attribute<String>(TAG) { view, value -> if (view.tag == null) view.tag = value }

            // Transform Attributes
            attribute<String>(ROTATION) { view, value -> view.rotation = value.toFloat() }
            attribute<String>(ROTATION_X) { view, value -> view.rotationX = value.toFloat() }
            attribute<String>(ROTATION_Y) { view, value -> view.rotationY = value.toFloat() }
            attribute<String>(SCALE_X) { view, value -> view.scaleX = value.toFloat() }
            attribute<String>(SCALE_Y) { view, value -> view.scaleY = value.toFloat() }
            attribute<String>(TRANSLATION_X) { view, value -> view.translationX = value.toFloat() }
            attribute<String>(TRANSLATION_Y) { view, value -> view.translationY = value.toFloat() }
            attribute<String>(TRANSLATION_Z) { view, value -> view.translationZ = value.toFloat() }
            attribute<String>(ELEVATION) { view, value -> view.elevation = value.toFloat() }
            attribute<String>(ALPHA) { view, value -> view.alpha = value.toFloat() }

            // Background Attributes
            attribute<String>(BACKGROUND) { view, value ->
                handleBackground(
                    view, value, view.context
                )
            }
            attribute<String>(BACKGROUND_TINT) { view, value ->
                ViewCompat.setBackgroundTintList(
                    view, ContextCompat.getColorStateList(
                        view.context, value.toColor(view.context, Color.TRANSPARENT)
                    )
                )
            }
            attribute<String>(BACKGROUND_TINT_MODE) { view, value ->
                ViewCompat.setBackgroundTintMode(view, parsePorterDuff(value))
            }

            // Size Attributes
            attribute<String>(WIDTH) { view, value ->
                setSize(
                    view, value, isWidthDimension = true
                )
            }
            attribute<String>(HEIGHT) { view, value ->
                setSize(
                    view, value, isWidthDimension = false
                )
            }
            attribute<String>(LAYOUT_WIDTH) { view, value ->
                setSize(
                    view, value, isWidthDimension = true
                )
            }
            attribute<String>(LAYOUT_HEIGHT) { view, value ->
                setSize(
                    view, value, isWidthDimension = false
                )
            }
            attribute<String>(MIN_WIDTH) { view, value ->
                view.minimumWidth = value.toPixels(
                    view.resources.displayMetrics, view.getParentView(), true, true
                ) as Int
            }
            attribute<String>(MIN_HEIGHT) { view, value ->
                view.minimumHeight = value.toPixels(
                    view.resources.displayMetrics, view.getParentView(), false, true
                ) as Int
            }
            attribute<String>(MAX_WIDTH) { view, value ->
                val pixels = value.toPixels(
                    view.resources.displayMetrics, view.getParentView(), true, true
                ) as Int
                when (view) {
                    is TextView -> view.maxWidth = pixels
                    is ImageView -> view.maxWidth = pixels
                    else -> view.layoutParams = view.layoutParams.apply { width = pixels }
                }
                view.requestLayout()
            }
            attribute<String>(MAX_HEIGHT) { view, value ->
                val pixels = value.toPixels(
                    view.resources.displayMetrics, view.getParentView(), false, true
                ) as Int
                when (view) {
                    is TextView -> view.maxHeight = pixels
                    is ImageView -> view.maxHeight = pixels
                    else -> view.layoutParams = view.layoutParams.apply { height = pixels }
                }
                view.requestLayout()
            }

            // Margin and Padding
            attribute<String>(LAYOUT_MARGIN) { view, value ->
                view.setMargin(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_LEFT) { view, value ->
                view.setMarginLeft(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_RIGHT) { view, value ->
                view.setMarginRight(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_START) { view, value ->
                view.setMarginStart(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_END) { view, value ->
                view.setMarginEnd(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_TOP) { view, value ->
                view.setMarginTop(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(LAYOUT_MARGIN_BOTTOM) { view, value ->
                view.setMarginBottom(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }

            attribute<String>(PADDING) { view, value ->
                view.setPaddingE(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_LEFT) { view, value ->
                view.setPaddingLeft(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_RIGHT) { view, value ->
                view.setPaddingRight(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_START) { view, value ->
                view.setPaddingStart(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_END) { view, value ->
                view.setPaddingEnd(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_TOP) { view, value ->
                view.setPaddingTop(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_BOTTOM) { view, value ->
                view.setPaddingBottom(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_HORIZONTAL) { view, value ->
                view.setPaddingHorizontal(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }
            attribute<String>(PADDING_VERTICAL) { view, value ->
                view.setPaddingVertical(
                    value.toPixels(
                        view.resources.displayMetrics, asInt = true
                    ) as Int
                )
            }

            // Accessibility
            attribute<String>(IMPORTANT_FOR_ACCESSIBILITY) { view, value ->
                view.importantForAccessibility = parseImportantForAccessibility(value)
            }
            attribute<String>(CONTENT_DESCRIPTION) { view, value ->
                view.contentDescription = value
            }

            // API Level specific attributes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                attribute<String>(TOOLTIP_TEXT) { view, value ->
                    view.tooltipText = getString(view.context, value)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                attribute<String>(SCROLL_INDICATORS) { view, value ->
                    view.scrollIndicators = parseScrollIndicators(value)
                }
                attribute<String>(FOREGROUND) { view, value ->
                    handleForeground(
                        view, value, view.context
                    )
                }
                attribute<String>(FOREGROUND_GRAVITY) { view, value ->
                    view.foregroundGravity = parseGravity(value)
                }
                attribute<String>(FOREGROUND_TINT) { view, value ->
                    handleForegroundTint(
                        view, value, view.context
                    )
                }
                attribute<String>(FOREGROUND_TINT_MODE) { view, value ->
                    view.foregroundTintMode = parsePorterDuff(value)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                attribute<String>(SCREEN_READER_FOCUSABLE) { view, value ->
                    view.isScreenReaderFocusable = value.toBoolean
                }
            }
        }
    }

    private fun registerTextViewAttributes() {
        attributesForView<TextView> {
            // Text Attributes
            attribute<String>(TEXT) { view, value -> view.text = getString(view.context, value) }
            attribute<String>(TEXT_SIZE) { view, value ->
                view.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    value.toPixels(view.resources.displayMetrics) as Float
                )
            }
            attribute<String>(TEXT_COLOR) { view, value ->
                view.setTextColor(
                    value.toColor(view.context)
                )
            }
            attribute<String>(TEXT_STYLE) { view, value ->
                view.setTypeface(
                    null, parseTextStyle(value.lowercase())
                )
            }
            attribute<String>(VIEW_TEXT_ALIGNMENT) { view, value ->
                parseTextAlignment(value)?.let {
                    view.textAlignment = it
                }
            }
            attribute<String>(ELLIPSIZE) { view, value ->
                view.ellipsize = parseEllipsize(value.lowercase())
            }
            attribute<String>(TEXTVIEW_SINGLE_LINE) { view, value ->
                view.isSingleLine = value.toBoolean
            }
            attribute<String>(HINT) { view, value -> view.hint = getString(view.context, value) }
            attribute<String>(TEXTVIEW_INPUT_TYPE) { view, value ->
                parseInputType(value).let {
                    if (it > 0) view.inputType = it
                }
            }
            attribute<String>(TEXT_COLOR_HINT) { view, value ->
                view.setHintTextColor(
                    value.toColor(
                        view.context, Color.GRAY
                    )
                )
            }
            attribute<String>(LETTER_SPACING) { view, value ->
                view.letterSpacing = value.toFloat()
            }
            attribute<String>(LINE_SPACING_EXTRA) { view, value ->
                view.setLineSpacing(
                    value.toFloat(), view.lineSpacingMultiplier
                )
            }
            attribute<String>(LINE_SPACING_MULTIPLIER) { view, value ->
                view.setLineSpacing(
                    view.lineSpacingExtra, value.toFloat()
                )
            }
            attribute<String>(TEXT_ALL_CAPS) { view, value -> view.isAllCaps = value.toBoolean }
            attribute<String>(MAX_LINES) { view, value -> view.maxLines = value.toInt() }
            attribute<String>(IME_OPTIONS) { view, value ->
                view.imeOptions = parseImeOption(value)
            }
            attribute<String>(FONT_FAMILY) { view, value ->
                loadFontFromAttribute(
                    view.context, value
                )?.let { view.typeface = it }
            }
            attribute<String>(TEXT_SCALE_X) { view, value ->
                val currentSp = view.textSize / view.context.resources.configuration.fontScale
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSp * value.toFloat())
            }
            attribute<String>(TRANSFORMATION_METHOD) { view, value ->
                handleTransformationMethod(
                    view, value
                )
            }
            attribute<String>(DRAWABLE_PADDING) { view, value ->
                view.compoundDrawablePadding =
                    value.toPixels(view.context.resources.displayMetrics, asInt = true) as Int
            }

            // Shadow Attributes
            attribute<String>(SHADOW_COLOR) { view, value ->
                view.setShadowLayer(
                    view.shadowRadius,
                    view.shadowDx,
                    view.shadowDy,
                    value.toColor(view.context, Color.TRANSPARENT)
                )
            }
            attribute<String>(SHADOW_RADIUS) { view, value ->
                view.setShadowLayer(
                    value.toFloat(), view.shadowDx, view.shadowDy, view.shadowColor
                )
            }
            attribute<String>(SHADOW_DX) { view, value ->
                view.setShadowLayer(
                    view.shadowRadius, value.toFloat(), view.shadowDy, view.shadowColor
                )
            }
            attribute<String>(SHADOW_DY) { view, value ->
                view.setShadowLayer(
                    view.shadowRadius, view.shadowDx, value.toFloat(), view.shadowColor
                )
            }

            // Drawable Attributes
            attribute<String>(DRAWABLE_START) { view, value ->
                handleDrawablePosition(
                    view, value, DrawablePosition.START
                )
            }
            attribute<String>(DRAWABLE_END) { view, value ->
                handleDrawablePosition(
                    view, value, DrawablePosition.END
                )
            }
            attribute<String>(DRAWABLE_TOP) { view, value ->
                handleDrawablePosition(
                    view, value, DrawablePosition.TOP
                )
            }
            attribute<String>(DRAWABLE_BOTTOM) { view, value ->
                handleDrawablePosition(
                    view, value, DrawablePosition.BOTTOM
                )
            }
        }
    }

    private fun registerViewGroupAttributes() {
        attributesForView<ViewGroup> {
            attribute<String>(LAYOUT_GRAVITY) { view, value ->
                val gravity = parseGravity(value)
                val params = view.layoutParams
                when (view) {
                    is LinearLayout -> (params as LinearLayout.LayoutParams).gravity = gravity
                    is FrameLayout -> (params as FrameLayout.LayoutParams).gravity = gravity
                    is GridLayout -> (params as GridLayout.LayoutParams).setGravity(gravity)
                }
                view.layoutParams = params
            }

            attribute<String>(CLIP_TO_PADDING) { view, value ->
                view.clipToPadding = value.toBoolean
            }
        }
    }
}