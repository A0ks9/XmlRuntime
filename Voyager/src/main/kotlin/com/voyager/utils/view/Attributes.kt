package com.voyager.utils.view

import android.os.Build
import androidx.annotation.RequiresApi
import com.voyager.data.models.Attribute

object Attributes {

    object Common {
        val ID = Attribute("id", android.R.attr.id)
        val VISIBILITY = Attribute("visibility", android.R.attr.visibility)
        val BACKGROUND = Attribute("background", android.R.attr.background)
        val BACKGROUND_TINT = Attribute("backgroundTint", android.R.attr.backgroundTint)
        val BACKGROUND_TINT_MODE =
            Attribute("backgroundTintMode", android.R.attr.backgroundTintMode)
        val CLICKABLE = Attribute("clickable", android.R.attr.clickable)
        val LONG_CLICKABLE = Attribute("longClickable", android.R.attr.longClickable)
        val ENABLED = Attribute("enabled", android.R.attr.enabled)
        val CONTENT_DESCRIPTION = Attribute("contentDescription", android.R.attr.contentDescription)
        val TAG = Attribute("tag", android.R.attr.tag)
        val PADDING = Attribute("padding", android.R.attr.padding)
        val PADDING_LEFT = Attribute("paddingLeft", android.R.attr.paddingLeft)
        val PADDING_TOP = Attribute("paddingTop", android.R.attr.paddingTop)
        val PADDING_RIGHT = Attribute("paddingRight", android.R.attr.paddingRight)
        val PADDING_BOTTOM = Attribute("paddingBottom", android.R.attr.paddingBottom)
        val PADDING_START = Attribute("paddingStart", android.R.attr.paddingStart)
        val PADDING_END = Attribute("paddingEnd", android.R.attr.paddingEnd)
        val FOREGROUND = Attribute("foreground", android.R.attr.foreground)
        val FOREGROUND_GRAVITY = Attribute("foregroundGravity", android.R.attr.foregroundGravity)
        val FOREGROUND_TINT = Attribute("foregroundTint", android.R.attr.foregroundTint)
        val FOREGROUND_TINT_MODE =
            Attribute("foregroundTintMode", android.R.attr.foregroundTintMode)
        val ROTATION = Attribute("rotation", android.R.attr.rotation)
        val ROTATION_X = Attribute("rotationX", android.R.attr.rotationX)
        val ROTATION_Y = Attribute("rotationY", android.R.attr.rotationY)
        val SCALE_X = Attribute("scaleX", android.R.attr.scaleX)
        val SCALE_Y = Attribute("scaleY", android.R.attr.scaleY)
        val TRANSLATION_X = Attribute("translationX", android.R.attr.translationX)
        val TRANSLATION_Y = Attribute("translationY", android.R.attr.translationY)
        val TRANSLATION_Z = Attribute("translationZ", android.R.attr.translationZ)
        val ELEVATION = Attribute("elevation", android.R.attr.elevation)
        val DRAWING_CACHE_QUALITY =
            Attribute("drawingCacheQuality", android.R.attr.drawingCacheQuality)
        val ALPHA = Attribute("alpha", android.R.attr.alpha)
        val LAYOUT_WIDTH = Attribute("layout_width", android.R.attr.layout_width)
        val LAYOUT_HEIGHT = Attribute("layout_height", android.R.attr.layout_height)
        val LAYOUT_MARGIN = Attribute("layout_margin", android.R.attr.layout_margin)
        val LAYOUT_MARGIN_LEFT = Attribute("layout_marginLeft", android.R.attr.layout_marginLeft)
        val LAYOUT_MARGIN_TOP = Attribute("layout_marginTop", android.R.attr.layout_marginTop)
        val LAYOUT_MARGIN_RIGHT = Attribute("layout_marginRight", android.R.attr.layout_marginRight)
        val LAYOUT_MARGIN_BOTTOM =
            Attribute("layout_marginBottom", android.R.attr.layout_marginBottom)
        val LAYOUT_MARGIN_START = Attribute("layout_marginStart", android.R.attr.layout_marginStart)
        val LAYOUT_MARGIN_END = Attribute("layout_marginEnd", android.R.attr.layout_marginEnd)
        val LAYOUT_GRAVITY = Attribute("layout_gravity", android.R.attr.layout_gravity)
        val GRAVITY = Attribute("gravity", android.R.attr.gravity)
        val WEIGHT = Attribute("layout_weight", android.R.attr.layout_weight)
        val STYLE = Attribute("style", 0)
        val MIN_WIDTH = Attribute("minWidth", android.R.attr.minWidth)
        val MIN_HEIGHT = Attribute("minHeight", android.R.attr.minHeight)
        val MAX_WIDTH = Attribute("maxWidth", android.R.attr.maxWidth)
        val MAX_HEIGHT = Attribute("maxHeight", android.R.attr.maxHeight)
        val TEXT = Attribute("text", android.R.attr.text)
        val WIDTH = Attribute(
            "width", android.R.attr.layout_width
        ) // Using layout_width as it generally refers to view width
        val HEIGHT = Attribute(
            "height", android.R.attr.layout_height
        ) // Using layout_height as it generally refers to view height
        val TEXT_SIZE = Attribute("textSize", android.R.attr.textSize)
        val TEXT_COLOR = Attribute("textColor", android.R.attr.textColor)
        val TEXT_COLOR_HINT = Attribute("textColorHint", android.R.attr.textColorHint)
        val ELLIPSIZE = Attribute("ellipsize", android.R.attr.ellipsize)
        val FONT_FAMILY = Attribute("fontFamily", android.R.attr.fontFamily)
        val LETTER_SPACING = Attribute("letterSpacing", android.R.attr.letterSpacing)
        val LINE_SPACING_EXTRA = Attribute("lineSpacingExtra", android.R.attr.lineSpacingExtra)
        val LINE_SPACING_MULTIPLIER =
            Attribute("lineSpacingMultiplier", android.R.attr.lineSpacingMultiplier)
        val SHADOW_COLOR = Attribute("shadowColor", android.R.attr.shadowColor)
        val SHADOW_DX = Attribute("shadowDx", android.R.attr.shadowDx)
        val SHADOW_DY = Attribute("shadowDy", android.R.attr.shadowDy)
        val SHADOW_RADIUS = Attribute("shadowRadius", android.R.attr.shadowRadius)
        val TEXT_SCALE_X = Attribute("textScaleX", android.R.attr.textScaleX)
        val TEXT_STYLE = Attribute("textStyle", android.R.attr.textStyle)
        val TEXT_ALL_CAPS = Attribute("textAllCaps", android.R.attr.textAllCaps)
        val HINT = Attribute("hint", android.R.attr.hint)
        val IME_OPTIONS = Attribute("imeOptions", android.R.attr.imeOptions)
        val MAX_LINES = Attribute("maxLines", android.R.attr.maxLines)
        val TRANSFORMATION_METHOD = Attribute("transformationMethod", 0)
        val PADDING_HORIZONTAL = Attribute(
            "paddingHorizontal", 0
        )
        val PADDING_VERTICAL = Attribute(
            "paddingVertical", 0
        )
        val SCROLLBARS = Attribute("scrollbars", android.R.attr.scrollbars)
        val OVER_SCROLL_MODE = Attribute("overScrollMode", android.R.attr.overScrollMode)
        val DRAWABLE_PADDING = Attribute("drawablePadding", android.R.attr.drawablePadding)
        val DRAWABLE_START = Attribute("drawableStart", android.R.attr.drawableStart)
        val DRAWABLE_END = Attribute("drawableEnd", android.R.attr.drawableEnd)
        val DRAWABLE_TOP = Attribute("drawableTop", android.R.attr.drawableTop)
        val DRAWABLE_BOTTOM = Attribute("drawableBottom", android.R.attr.drawableBottom)
        val TINT = Attribute("tint", android.R.attr.tint)
        val TINT_MODE = Attribute("tintMode", android.R.attr.tintMode)
        val IMPORTANT_FOR_ACCESSIBILITY =
            Attribute("importantForAccessibility", android.R.attr.importantForAccessibility)

        @RequiresApi(Build.VERSION_CODES.P)
        val SCREEN_READER_FOCUSABLE =
            Attribute("screenReaderFocusable", android.R.attr.screenReaderFocusable)

        @RequiresApi(Build.VERSION_CODES.O)
        val TOOLTIP_TEXT = Attribute("tooltipText", android.R.attr.tooltipText)
        val CLIP_TO_PADDING = Attribute("clipToPadding", android.R.attr.clipToPadding)

        @RequiresApi(Build.VERSION_CODES.M)
        val SCROLL_INDICATORS = Attribute("scrollIndicators", android.R.attr.scrollIndicators)

        @RequiresApi(Build.VERSION_CODES.M)
        val DRAWABLE_TINT = Attribute("drawableTint", android.R.attr.drawableTint)

        @RequiresApi(Build.VERSION_CODES.M)
        val DRAWABLE_TINT_MODE = Attribute("drawableTintMode", android.R.attr.drawableTintMode)
    }

    object View {
        val VIEW_ABOVE = Attribute("layout_above", android.R.attr.layout_above)
        val VIEW_ACTIVATED = Attribute(
            "activated", android.R.attr.state_activated
        )
        val VIEW_ALIGN_BASELINE =
            Attribute("layout_alignBaseline", android.R.attr.layout_alignBaseline)
        val VIEW_ALIGN_BOTTOM = Attribute("layout_alignBottom", android.R.attr.layout_alignBottom)
        val VIEW_ALIGN_END = Attribute("layout_alignEnd", android.R.attr.layout_alignEnd)
        val VIEW_ALIGN_LEFT = Attribute("layout_alignLeft", android.R.attr.layout_alignLeft)
        val VIEW_ALIGN_PARENT_BOTTOM =
            Attribute("layout_alignParentBottom", android.R.attr.layout_alignParentBottom)
        val VIEW_ALIGN_PARENT_END =
            Attribute("layout_alignParentEnd", android.R.attr.layout_alignParentEnd)
        val VIEW_ALIGN_PARENT_LEFT =
            Attribute("layout_alignParentLeft", android.R.attr.layout_alignParentLeft)
        val VIEW_ALIGN_PARENT_RIGHT =
            Attribute("layout_alignParentRight", android.R.attr.layout_alignParentRight)
        val VIEW_ALIGN_PARENT_START =
            Attribute("layout_alignParentStart", android.R.attr.layout_alignParentStart)
        val VIEW_ALIGN_PARENT_TOP =
            Attribute("layout_alignParentTop", android.R.attr.layout_alignParentTop)
        val VIEW_ALIGN_RIGHT = Attribute("layout_alignRight", android.R.attr.layout_alignRight)
        val VIEW_ALIGN_START = Attribute("layout_alignStart", android.R.attr.layout_alignStart)
        val VIEW_ALIGN_TOP = Attribute("layout_alignTop", android.R.attr.layout_alignTop)
        val VIEW_ANIMATION = Attribute("animation", android.R.attr.animation)
        val VIEW_BELOW = Attribute("layout_below", android.R.attr.layout_below)
        val VIEW_CENTER_HORIZONTAL =
            Attribute("layout_centerHorizontal", android.R.attr.layout_centerHorizontal)
        val VIEW_CENTER_IN_PARENT =
            Attribute("layout_centerInParent", android.R.attr.layout_centerInParent)
        val VIEW_CENTER_VERTICAL =
            Attribute("layout_centerVertical", android.R.attr.layout_centerVertical)

        @RequiresApi(Build.VERSION_CODES.M)
        val VIEW_CONTEXT_CLICKABLE = Attribute("contextClickable", android.R.attr.contextClickable)
        val VIEW_FADE_SCROLLBARS = Attribute("fadeScrollbars", android.R.attr.fadeScrollbars)
        val VIEW_FADING_EDGE_LENGTH = Attribute("fadingEdgeLength", android.R.attr.fadingEdgeLength)
        val VIEW_FILTER_TOUCHES_WHEN_OBSCURED =
            Attribute("filterTouchesWhenObscured", android.R.attr.filterTouchesWhenObscured)
        val VIEW_FITS_SYSTEM_WINDOWS =
            Attribute("fitsSystemWindows", android.R.attr.fitsSystemWindows)
        val VIEW_FOCUSABLE = Attribute("focusable", android.R.attr.focusable)
        val VIEW_FOCUSABLE_IN_TOUCH_MODE =
            Attribute("focusableInTouchMode", android.R.attr.focusableInTouchMode)

        @RequiresApi(Build.VERSION_CODES.N)
        val VIEW_FORCE_HAS_OVERLAPPING_RENDERING =
            Attribute("forceHasOverlappingRendering", android.R.attr.forceHasOverlappingRendering)
        val VIEW_HAPTIC_FEEDBACK_ENABLED =
            Attribute("hapticFeedbackEnabled", android.R.attr.hapticFeedbackEnabled)
        val VIEW_IS_SCROLL_CONTAINER =
            Attribute("isScrollContainer", android.R.attr.isScrollContainer)
        val VIEW_KEEP_SCREEN_ON = Attribute("keepScreenOn", android.R.attr.keepScreenOn)
        val VIEW_LAYER_TYPE = Attribute("layerType", android.R.attr.layerType)
        val VIEW_LAYOUT_DIRECTION = Attribute("layoutDirection", android.R.attr.layoutDirection)
        val VIEW_LONG_CLICKABLE = Attribute(
            "longClickable", android.R.attr.longClickable
        )
        val VIEW_NEXT_FOCUS_DOWN = Attribute("nextFocusDown", android.R.attr.nextFocusDown)
        val VIEW_NEXT_FOCUS_FORWARD = Attribute("nextFocusForward", android.R.attr.nextFocusForward)
        val VIEW_NEXT_FOCUS_LEFT = Attribute("nextFocusLeft", android.R.attr.nextFocusLeft)
        val VIEW_NEXT_FOCUS_RIGHT = Attribute("nextFocusRight", android.R.attr.nextFocusRight)
        val VIEW_NEXT_FOCUS_UP = Attribute("nextFocusUp", android.R.attr.nextFocusUp)

        @Suppress("DEPRECATION")
        val VIEW_ON_CLICK = Attribute("onClick", android.R.attr.onClick)
        val VIEW_ON_LONG_CLICK = Attribute("onLongClick", 0)
        val VIEW_ON_TOUCH = Attribute("onTouch", 0)
        val VIEW_REQUIRES_FADING_EDGE =
            Attribute("requiresFadingEdge", android.R.attr.requiresFadingEdge)
        val VIEW_SAVE_ENABLED = Attribute("saveEnabled", android.R.attr.saveEnabled)
        val VIEW_SCROLLBAR_DEFAULT_DELAY_BEFORE_FADE = Attribute(
            "scrollbarDefaultDelayBeforeFade", android.R.attr.scrollbarDefaultDelayBeforeFade
        )
        val VIEW_SCROLLBAR_FADE_DURATION =
            Attribute("scrollbarFadeDuration", android.R.attr.scrollbarFadeDuration)
        val VIEW_SCROLLBAR_SIZE = Attribute("scrollbarSize", android.R.attr.scrollbarSize)
        val VIEW_SCROLLBAR_STYLE = Attribute("scrollbarStyle", android.R.attr.scrollbarStyle)
        val VIEW_SELECTED = Attribute(
            "selected", android.R.attr.state_selected
        )
        val VIEW_SOUND_EFFECTS_ENABLED =
            Attribute("soundEffectsEnabled", android.R.attr.soundEffectsEnabled)
        val VIEW_TEXT_ALIGNMENT = Attribute("textAlignment", android.R.attr.textAlignment)
        val VIEW_TEXT_DIRECTION = Attribute("textDirection", android.R.attr.textDirection)
        val VIEW_TO_END_OF = Attribute("layout_toEndOf", android.R.attr.layout_toEndOf)
        val VIEW_TO_LEFT_OF = Attribute("layout_toLeftOf", android.R.attr.layout_toLeftOf)
        val VIEW_TO_RIGHT_OF = Attribute("layout_toRightOf", android.R.attr.layout_toRightOf)
        val VIEW_TO_START_OF = Attribute("layout_toStartOf", android.R.attr.layout_toStartOf)
        val VIEW_TRANSFORM_PIVOT_X = Attribute("transformPivotX", android.R.attr.transformPivotX)
        val VIEW_TRANSFORM_PIVOT_Y = Attribute("transformPivotY", android.R.attr.transformPivotY)
        val VIEW_TRANSITION_NAME = Attribute("transitionName", android.R.attr.transitionName)
        val VIEW_BASELINE_ALIGN_BOTTOM =
            Attribute("baselineAlignBottom", android.R.attr.baselineAlignBottom)
        val VIEW_STATE_LIST_ANIMATOR =
            Attribute("stateListAnimator", android.R.attr.stateListAnimator)
        val VIEW_LAYOUT_ANIMATION = Attribute("layoutAnimation", android.R.attr.layoutAnimation)
        val VIEW_ANIMATE_LAYOUT_CHANGES =
            Attribute("animateLayoutChanges", android.R.attr.animateLayoutChanges)
    }

    object WebView {
        val WEB_VIEW_URL = Attribute("url", 0)
        val WEB_VIEW_HTML = Attribute("html", 0)
        val WEB_VIEW_JAVASCRIPT_ENABLED = Attribute(
            "javaScriptEnabled", 0
        )
        val WEB_VIEW_DOM_STORAGE_ENABLED = Attribute(
            "domStorageEnabled", 0
        )
        val WEB_VIEW_USER_AGENT = Attribute(
            "userAgentString", 0
        )
        val WEB_VIEW_LOAD_WITH_OVERVIEW_MODE = Attribute(
            "loadWithOverviewMode", 0
        )
        val WEB_VIEW_USE_WIDE_VIEWPORT = Attribute(
            "useWideViewPort", 0
        )
        val WEB_VIEW_DISPLAY_ZOOM_CONTROLS = Attribute(
            "displayZoomControls", 0
        )
        val WEB_VIEW_ALLOW_FILE_ACCESS = Attribute(
            "allowFileAccessFromFileURLs", 0
        )
        val WEB_VIEW_TEXT_ZOOM = Attribute("textZoom", 0)
        val WEB_VIEW_GEOLOCATION_ENABLED = Attribute(
            "geolocationEnabled", 0
        )
        val WEB_VIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
        val WEB_VIEW_OVER_SCROLL_MODE = Attribute(
            "overScrollMode", android.R.attr.overScrollMode
        )
    }

    object RatingBar {
        val RATINGBAR_NUM_STARS = Attribute("numStars", android.R.attr.numStars)
        val RATINGBAR_RATING = Attribute("rating", android.R.attr.rating)
        val RATINGBAR_IS_INDICATOR = Attribute("isIndicator", android.R.attr.isIndicator)
        val RATINGBAR_STEP_SIZE = Attribute("stepSize", android.R.attr.stepSize)
        val RATINGBAR_PROGRESS_DRAWABLE =
            Attribute("progressDrawable", android.R.attr.progressDrawable)
    }

    object TextView {
        val TEXTVIEW_HTML = Attribute("html", 0)
        val TEXTVIEW_TEXT_COLOR_LINK = Attribute("textColorLink", android.R.attr.textColorLink)
        val TEXTVIEW_TEXT_COLOR_HIGHLIGHT =
            Attribute("textColorHighlight", android.R.attr.textColorHighlight)
        val TEXTVIEW_PAINT_FLAGS = Attribute("paintFlags", 0)
        val TEXTVIEW_PREFIX = Attribute("prefix", 0)
        val TEXTVIEW_SUFFIX = Attribute("suffix", 0)

        @Suppress("DEPRECATION")
        val TEXTVIEW_SINGLE_LINE = Attribute("singleLine", android.R.attr.singleLine)
        val TEXTVIEW_AUTO_LINK = Attribute("autoLink", android.R.attr.autoLink)

        @RequiresApi(Build.VERSION_CODES.M)
        val TEXTVIEW_BREAK_STRATEGY = Attribute("breakStrategy", android.R.attr.breakStrategy)
        val TEXTVIEW_CURSOR_VISIBLE = Attribute("cursorVisible", android.R.attr.cursorVisible)

        @Suppress("DEPRECATION")
        val TEXTVIEW_EDITABLE = Attribute("editable", android.R.attr.editable)
        val TEXTVIEW_EMS = Attribute("ems", android.R.attr.ems)

        @RequiresApi(Build.VERSION_CODES.M)
        val TEXTVIEW_HYPHENATION_FREQUENCY =
            Attribute("hyphenationFrequency", android.R.attr.hyphenationFrequency)
        val TEXTVIEW_MAX_EMS = Attribute("maxEms", android.R.attr.maxEms)
        val TEXTVIEW_MIN_EMS = Attribute("minEms", android.R.attr.minEms)
        val TEXTVIEW_PRIVATE_IME_OPTIONS =
            Attribute("privateImeOptions", android.R.attr.privateImeOptions)
        val TEXTVIEW_SELECT_ALL_ON_FOCUS =
            Attribute("selectAllOnFocus", android.R.attr.selectAllOnFocus)
        val TEXTVIEW_TEXT_SCALE_X = Attribute(
            "textScaleX", android.R.attr.textScaleX
        )
        val TEXTVIEW_MARQUEE_REPEAT_LIMIT =
            Attribute("marqueeRepeatLimit", android.R.attr.marqueeRepeatLimit)
        val TEXTVIEW_INPUT_TYPE = Attribute("inputType", android.R.attr.inputType)
        val TEXTVIEW_TEXT_APPEARANCE = Attribute("textAppearance", android.R.attr.textAppearance)

        @RequiresApi(Build.VERSION_CODES.P)
        val TEXTVIEW_FONT_VARIATION_SETTINGS =
            Attribute("fontVariationSettings", android.R.attr.fontVariationSettings)

        @RequiresApi(Build.VERSION_CODES.P)
        val TEXTVIEW_LINE_HEIGHT = Attribute("lineHeight", android.R.attr.lineHeight)

        @RequiresApi(Build.VERSION_CODES.P)
        val TEXTVIEW_FIRST_BASELINE_TO_TOP_HEIGHT =
            Attribute("firstBaselineToTopHeight", android.R.attr.firstBaselineToTopHeight)

        @RequiresApi(Build.VERSION_CODES.P)
        val TEXTVIEW_LAST_BASELINE_TO_BOTTOM_HEIGHT =
            Attribute("lastBaselineToBottomHeight", android.R.attr.lastBaselineToBottomHeight)
    }

    object EditText {
        val EDITTEXT_INPUT_TYPE = Attribute(
            "inputType", android.R.attr.inputType
        )

        @Suppress("DEPRECATION")
        val EDITTEXT_PASSWORD = Attribute("password", android.R.attr.password)

        @Suppress("DEPRECATION")
        val EDITTEXT_SINGLE_LINE = Attribute(
            "singleLine", android.R.attr.singleLine
        )
        val EDITTEXT_SELECT_ALL_ON_FOCUS = Attribute(
            "selectAllOnFocus", android.R.attr.selectAllOnFocus
        )

        @Suppress("DEPRECATION")
        val EDITTEXT_EDITABLE = Attribute("editable", android.R.attr.editable)

        @Suppress("DEPRECATION")
        val EDITTEXT_AUTO_TEXT = Attribute("autoText", android.R.attr.autoText)
        val EDITTEXT_BUFFER_TYPE = Attribute("bufferType", android.R.attr.bufferType)
        val EDITTEXT_COMPLETION_HINT = Attribute("completionHint", android.R.attr.completionHint)
        val EDITTEXT_COMPLETION_MODE = Attribute("completionMode", 0)
        val EDITTEXT_DIGITS = Attribute("digits", android.R.attr.digits)
        val EDITTEXT_ERROR = Attribute("error", 0)
        val EDITTEXT_IME_ACTION_LABEL = Attribute("imeActionLabel", android.R.attr.imeActionLabel)
        val EDITTEXT_IME_ACTION_ID = Attribute("imeActionId", android.R.attr.imeActionId)
        val EDITTEXT_MAX_EMS = Attribute(
            "maxEms", android.R.attr.maxEms
        )
        val EDITTEXT_MAX_LENGTH = Attribute("maxLength", android.R.attr.maxLength)
        val EDITTEXT_MIN_EMS = Attribute(
            "minEms", android.R.attr.minEms
        )
        val EDITTEXT_PRIVATE_IME_OPTIONS = Attribute(
            "privateImeOptions", android.R.attr.privateImeOptions
        )
        val EDITTEXT_SCROLL_HORIZONTALLY =
            Attribute("scrollHorizontally", android.R.attr.scrollHorizontally)
        val EDITTEXT_TEXT_SCALE_X = Attribute(
            "textScaleX", android.R.attr.textScaleX
        )
        val EDITTEXT_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object Button {
        val BUTTON_BACKGROUND = Attribute(
            "background", android.R.attr.background
        )
        val BUTTON_ENABLED = Attribute(
            "enabled", android.R.attr.enabled
        )
        val BUTTON_PADDING = Attribute(
            "padding", android.R.attr.padding
        )

        @Suppress("DEPRECATION")
        val BUTTON_ON_CLICK = Attribute("onClick", android.R.attr.onClick)
        val BUTTON_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object CheckBox {
        val CHECKBOX_CHECKED = Attribute("checked", android.R.attr.checked)
        val CHECKBOX_BUTTON = Attribute("button", android.R.attr.button)
    }

    object FrameLayout {
        val FRAME_LAYOUT_HEIGHT_RATIO = Attribute("heightRatio", 0)
        val FRAME_LAYOUT_WIDTH_RATIO = Attribute("widthRatio", 0)
        val FRAME_LAYOUT_MEASURE_ALL_CHILDREN =
            Attribute("measureAllChildren", android.R.attr.measureAllChildren)
    }

    object ImageView {
        val IMAGEVIEW_SRC = Attribute("src", android.R.attr.src)
        val IMAGEVIEW_SCALE_TYPE = Attribute("scaleType", android.R.attr.scaleType)
        val IMAGEVIEW_ADJUST_VIEW_BOUNDS =
            Attribute("adjustViewBounds", android.R.attr.adjustViewBounds)
        val IMAGEVIEW_CROP_TO_PADDING = Attribute("cropToPadding", android.R.attr.cropToPadding)
        val IMAGEVIEW_MAX_HEIGHT = Attribute(
            "maxHeight", android.R.attr.maxHeight
        )
        val IMAGEVIEW_MAX_WIDTH = Attribute(
            "maxWidth", android.R.attr.maxWidth
        )
        val IMAGEVIEW_FOREGROUND = Attribute(
            "foreground", android.R.attr.foreground
        )
        val IMAGEVIEW_TINT = Attribute(
            "tint", android.R.attr.tint
        )
        val IMAGEVIEW_TINT_MODE = Attribute(
            "tintMode", android.R.attr.tintMode
        )
    }

    object ViewGroup {
        val VIEW_GROUP_CHILDREN = Attribute(
            "children", 0
        )
        val VIEW_GROUP_CLIP_CHILDREN = Attribute("clipChildren", android.R.attr.clipChildren)
        val VIEW_GROUP_CLIP_TO_PADDING = Attribute(
            "clipToPadding", android.R.attr.clipToPadding
        )
        val VIEW_GROUP_LAYOUT_MODE = Attribute("layoutMode", android.R.attr.layoutMode)
        val VIEW_GROUP_SPLIT_MOTION_EVENTS =
            Attribute("splitMotionEvents", android.R.attr.splitMotionEvents)
        val VIEW_GROUP_ADD_STATES_FROM_CHILDREN =
            Attribute("addStatesFromChildren", android.R.attr.addStatesFromChildren)
        val VIEW_GROUP_ALWAYS_DRAWN_WITH_CACHE = Attribute(
            "alwaysDrawnWithCache", android.R.attr.alwaysDrawnWithCache
        )
        val VIEW_GROUP_ANIMATION_CACHE = Attribute("animationCache", android.R.attr.animationCache)
        val VIEW_GROUP_LAYOUT_ANIMATION = Attribute(
            "layoutAnimation", android.R.attr.layoutAnimation
        )
        val VIEW_GROUP_PERSISTENT_DRAWING_CACHE =
            Attribute("persistentDrawingCache", android.R.attr.persistentDrawingCache)
        val VIEW_GROUP_TOUCHSCREEN_BLOCKS_FOCUS =
            Attribute("touchscreenBlocksFocus", android.R.attr.touchscreenBlocksFocus)
        val VIEW_GROUP_TRANSITION_GROUP =
            Attribute("transitionGroup", android.R.attr.transitionGroup)
        val VIEW_GROUP_ANIMATE_LAYOUT_CHANGES = Attribute(
            "animateLayoutChanges", android.R.attr.animateLayoutChanges
        )
    }

    object LinearLayout {
        val LINEARLAYOUT_ORIENTATION = Attribute("orientation", android.R.attr.orientation)
        val LINEARLAYOUT_DIVIDER = Attribute("divider", android.R.attr.divider)
        val LINEARLAYOUT_DIVIDER_PADDING =
            Attribute("dividerPadding", android.R.attr.dividerPadding)
        val LINEARLAYOUT_SHOW_DIVIDERS = Attribute("showDividers", android.R.attr.showDividers)
        val LINEARLAYOUT_WEIGHT_SUM = Attribute("weightSum", android.R.attr.weightSum)
        val LINEARLAYOUT_MEASURE_WITH_LARGEST_CHILD =
            Attribute("measureWithLargestChild", android.R.attr.measureWithLargestChild)
        val LINEARLAYOUT_BASELINE_ALIGNED =
            Attribute("baselineAligned", android.R.attr.baselineAligned)
        val LINEARLAYOUT_BASELINE_ALIGNED_CHILD_INDEX =
            Attribute("baselineAlignedChildIndex", android.R.attr.baselineAlignedChildIndex)
        val LINEARLAYOUT_ANIMATE_LAYOUT_CHANGES = Attribute(
            "animateLayoutChanges", android.R.attr.animateLayoutChanges
        )
    }

    object ScrollView {
        val SCROLLVIEW_FILL_VIEWPORT = Attribute("fillViewport", android.R.attr.fillViewport)
        val SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED =
            Attribute("smoothScrollbar", android.R.attr.smoothScrollbar)
        val SCROLLVIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
        val SCROLLVIEW_OVER_SCROLL_MODE = Attribute(
            "overScrollMode", android.R.attr.overScrollMode
        )
    }

    object HorizontalScrollView {
        val HORIZONTAL_SCROLLVIEW_FILL_VIEWPORT =
            Attribute("fillViewPort", android.R.attr.fillViewport)
        val HORIZONTAL_SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED = Attribute(
            "smoothScrollbar", android.R.attr.smoothScrollbar
        )
        val HORIZONTAL_SCROLLVIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
        val HORIZONTAL_SCROLLVIEW_OVER_SCROLL_MODE = Attribute(
            "overScrollMode", android.R.attr.overScrollMode
        )
    }

    object ProgressBar {
        val PROGRESSBAR_PROGRESS = Attribute("progress", android.R.attr.progress)
        val PROGRESSBAR_MAX = Attribute("max", android.R.attr.max)
        val PROGRESSBAR_PROGRESS_TINT = Attribute("progressTint", android.R.attr.progressTint)
        val PROGRESSBAR_INDETERMINATE_TINT =
            Attribute("indeterminateTint", android.R.attr.indeterminateTint)
        val PROGRESSBAR_SECONDARY_PROGRESS_TINT =
            Attribute("secondaryProgressTint", android.R.attr.secondaryProgressTint)
        val PROGRESSBAR_INDETERMINATE = Attribute("indeterminate", android.R.attr.indeterminate)
        val PROGRESSBAR_INDETERMINATE_DURATION =
            Attribute("indeterminateDuration", android.R.attr.indeterminateDuration)
        val PROGRESSBAR_INDETERMINATE_ONLY =
            Attribute("indeterminateOnly", android.R.attr.indeterminateOnly)
        val PROGRESSBAR_PROGRESS_BACKGROUND_TINT =
            Attribute("progressBackgroundTint", android.R.attr.progressBackgroundTint)
        val PROGRESSBAR_PROGRESS_TINT_MODE =
            Attribute("progressTintMode", android.R.attr.progressTintMode)
        val PROGRESSBAR_ROTATION = Attribute(
            "rotation", android.R.attr.rotation
        )
        val PROGRESSBAR_SCALE_X = Attribute(
            "scaleX", android.R.attr.scaleX
        )
        val PROGRESSBAR_SCALE_Y = Attribute(
            "scaleY", android.R.attr.scaleY
        )
        val PROGRESSBAR_SECONDARY_PROGRESS =
            Attribute("secondaryProgress", android.R.attr.secondaryProgress)
        val PROGRESSBAR_SPLIT_TRACK = Attribute("splitTrack", android.R.attr.splitTrack)
    }

    object RecyclerView {
        val RECYCLERVIEW_LAYOUT_MANAGER =
            Attribute("layoutManager", androidx.recyclerview.R.attr.layoutManager)
        val RECYCLERVIEW_ITEM_DECORATION = Attribute("itemDecoration", 0)
        val RECYCLERVIEW_HAS_FIXED_SIZE = Attribute("hasFixed", 0)
        val RECYCLERVIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
        val RECYCLERVIEW_CLIP_TO_PADDING = Attribute(
            "clipToPadding", android.R.attr.clipToPadding
        )
        val RECYCLERVIEW_OVER_SCROLL_MODE = Attribute(
            "overScrollMode", android.R.attr.overScrollMode
        )
    }

    object ListView {
        val LISTVIEW_DIVIDER = Attribute(
            "divider", android.R.attr.divider
        )
        val LISTVIEW_DIVIDER_HEIGHT = Attribute("dividerHeight", android.R.attr.dividerHeight)
        val LISTVIEW_ENTRIES = Attribute("entries", android.R.attr.entries)
        val LISTVIEW_FOOTER_DIVIDERS_ENABLED =
            Attribute("footerDividersEnabled", android.R.attr.footerDividersEnabled)
        val LISTVIEW_HEADER_DIVIDERS_ENABLED =
            Attribute("headerDividersEnabled", android.R.attr.headerDividersEnabled)
        val LISTVIEW_LIST_SELECTOR = Attribute("listSelector", android.R.attr.listSelector)
        val LISTVIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
        val LISTVIEW_SMOOTH_SCROLLBAR_ENABLED = Attribute(
            "smoothScrollbar", android.R.attr.smoothScrollbar
        )
        val LISTVIEW_TRANSCRIPT_MODE = Attribute("transcriptMode", android.R.attr.transcriptMode)
        val LISTVIEW_STACK_FROM_BOTTOM =
            Attribute("stackFromBottom", android.R.attr.stackFromBottom)
    }

    object GridView {
        val GRIDVIEW_NUM_COLUMNS = Attribute("numColumns", android.R.attr.numColumns)
        val GRIDVIEW_COLUMN_WIDTH = Attribute("columnWidth", android.R.attr.columnWidth)
        val GRIDVIEW_GRAVITY = Attribute(
            "gravity", android.R.attr.gravity
        )
        val GRIDVIEW_HORIZONTAL_SPACING =
            Attribute("horizontalSpacing", android.R.attr.horizontalSpacing)
        val GRIDVIEW_STRETCH_MODE = Attribute("stretchMode", android.R.attr.stretchMode)
        val GRIDVIEW_VERTICAL_SPACING = Attribute("verticalSpacing", android.R.attr.verticalSpacing)
        val GRIDVIEW_OVER_SCROLL_MODE = Attribute(
            "overScrollMode", android.R.attr.overScrollMode
        )
        val GRIDVIEW_SCROLLBARS = Attribute(
            "scrollbars", android.R.attr.scrollbars
        )
    }

    object GridLayout {
        val GRIDLAYOUT_COLUMN_COUNT = Attribute("columnCount", android.R.attr.columnCount)
        val GRIDLAYOUT_ROW_COUNT = Attribute("rowCount", android.R.attr.rowCount)
        val GRIDLAYOUT_ORIENTATION = Attribute(
            "orientation", android.R.attr.orientation
        )
        val GRIDLAYOUT_USE_DEFAULT_MARGINS =
            Attribute("useDefaultMargins", android.R.attr.useDefaultMargins)
        val GRIDLAYOUT_ALIGNMENT_MODE = Attribute("alignmentMode", android.R.attr.alignmentMode)
        val GRIDLAYOUT_COLUMN_ORDER_PRESERVED =
            Attribute("columnOrderPreserved", android.R.attr.columnOrderPreserved)
        val GRIDLAYOUT_ROW_ORDER_PRESERVED =
            Attribute("rowOrderPreserved", android.R.attr.rowOrderPreserved)

        object LayoutParams {
            val LAYOUT_COLUMN_SPAN =
                Attribute("layout_columnSpan", android.R.attr.layout_columnSpan)
            val LAYOUT_ROW_SPAN = Attribute("layout_rowSpan", android.R.attr.layout_rowSpan)
            val LAYOUT_COLUMN_WEIGHT =
                Attribute("layout_columnWeight", android.R.attr.layout_columnWeight)
            val LAYOUT_ROW_WEIGHT = Attribute("layout_rowWeight", android.R.attr.layout_rowWeight)
            val LAYOUT_GRAVITY = Attribute(
                "layout_gravity", android.R.attr.layout_gravity
            )
            val LAYOUT_COLUMN = Attribute("layout_column", android.R.attr.layout_column)
            val LAYOUT_ROW = Attribute("layout_row", android.R.attr.layout_row)
        }
    }

    object FlexboxLayout {
        val FLEXBOX_LAYOUT_FLEX_DIRECTION =
            Attribute("flexDirection", com.google.android.flexbox.R.attr.flexDirection)
        val FLEXBOX_LAYOUT_FLEX_WRAP =
            Attribute("flexWrap", com.google.android.flexbox.R.attr.flexWrap)
        val FLEXBOX_LAYOUT_JUSTIFY_CONTENT =
            Attribute("justifyContent", com.google.android.flexbox.R.attr.justifyContent)
        val FLEXBOX_LAYOUT_ALIGN_ITEMS =
            Attribute("alignItems", com.google.android.flexbox.R.attr.alignItems)
        val FLEXBOX_LAYOUT_ALIGN_CONTENT =
            Attribute("alignContent", com.google.android.flexbox.R.attr.alignContent)

        object LayoutParams {
            val LAYOUT_FLEX_GROW =
                Attribute("layout_flexGrow", com.google.android.flexbox.R.attr.layout_flexGrow)
            val LAYOUT_FLEX_SHRINK =
                Attribute("layout_flexShrink", com.google.android.flexbox.R.attr.layout_flexShrink)
            val LAYOUT_FLEX_BASIS_PERCENT = Attribute(
                "layout_flexBasisPercent", com.google.android.flexbox.R.attr.layout_flexBasisPercent
            )
            val LAYOUT_ORDER =
                Attribute("layout_order", com.google.android.flexbox.R.attr.layout_order)
            val LAYOUT_ALIGN_SELF =
                Attribute("layout_alignSelf", com.google.android.flexbox.R.attr.layout_alignSelf)
            val LAYOUT_MIN_WIDTH =
                Attribute("layout_minWidth", com.google.android.flexbox.R.attr.layout_minWidth)
            val LAYOUT_MIN_HEIGHT =
                Attribute("layout_minHeight", com.google.android.flexbox.R.attr.layout_minHeight)
            val LAYOUT_MAX_WIDTH =
                Attribute("layout_maxWidth", com.google.android.flexbox.R.attr.layout_maxWidth)
            val LAYOUT_MAX_HEIGHT =
                Attribute("layout_maxHeight", com.google.android.flexbox.R.attr.layout_maxHeight)
            val LAYOUT_MARGIN = Attribute(
                "layout_margin", android.R.attr.layout_margin
            )
            val LAYOUT_MARGIN_LEFT = Attribute(
                "layout_marginLeft", android.R.attr.layout_marginLeft
            )
            val LAYOUT_MARGIN_TOP = Attribute(
                "layout_marginTop", android.R.attr.layout_marginTop
            )
            val LAYOUT_MARGIN_RIGHT = Attribute(
                "layout_marginRight", android.R.attr.layout_marginRight
            )
            val LAYOUT_MARGIN_BOTTOM = Attribute(
                "layout_marginBottom", android.R.attr.layout_marginBottom
            )
            val LAYOUT_ASPECT_RATIO = Attribute("layout_aspectRatio", 0)
        }
    }

    object CardView {
        val CARD_VIEW_CARD_BACKGROUND_COLOR =
            Attribute("cardBackgroundColor", androidx.cardview.R.attr.cardBackgroundColor)
        val CARD_VIEW_CARD_CORNER_RADIUS =
            Attribute("cardCornerRadius", androidx.cardview.R.attr.cardCornerRadius)
        val CARD_VIEW_CARD_ELEVATION =
            Attribute("cardElevation", androidx.cardview.R.attr.cardElevation)
        val CARD_VIEW_CARD_MAX_ELEVATION =
            Attribute("cardMaxElevation", androidx.cardview.R.attr.cardMaxElevation)
        val CARD_VIEW_CARD_USE_COMPAT_PADDING =
            Attribute("cardUseCompatPadding", androidx.cardview.R.attr.cardUseCompatPadding)
        val CARD_VIEW_CARD_PREVENT_CORNER_OVERLAP =
            Attribute("cardPreventCornerOverlap", androidx.cardview.R.attr.cardPreventCornerOverlap)
        val CARD_VIEW_CONTENT_PADDING =
            Attribute("contentPadding", androidx.cardview.R.attr.contentPadding)
        val CARD_VIEW_CONTENT_PADDING_LEFT =
            Attribute("contentPaddingLeft", androidx.cardview.R.attr.contentPaddingLeft)
        val CARD_VIEW_CONTENT_PADDING_TOP =
            Attribute("contentPaddingTop", androidx.cardview.R.attr.contentPaddingTop)
        val CARD_VIEW_CONTENT_PADDING_RIGHT =
            Attribute("contentPaddingRight", androidx.cardview.R.attr.contentPaddingRight)
        val CARD_VIEW_CONTENT_PADDING_BOTTOM =
            Attribute("contentPaddingBottom", androidx.cardview.R.attr.contentPaddingBottom)
        val CARD_VIEW_FOREGROUND = Attribute(
            "foreground", android.R.attr.foreground
        )
        val CARD_VIEW_FOREGROUND_GRAVITY = Attribute(
            "foregroundGravity", android.R.attr.foregroundGravity
        )
    }

    object ConstraintLayout {
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_BEGIN = Attribute(
            "layout_constraintGuide_begin",
            androidx.constraintlayout.widget.R.attr.layout_constraintGuide_begin
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_END = Attribute(
            "layout_constraintGuide_end",
            androidx.constraintlayout.widget.R.attr.layout_constraintGuide_end
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_PERCENT = Attribute(
            "layout_constraintGuide_percent",
            androidx.constraintlayout.widget.R.attr.layout_constraintGuide_percent
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = Attribute(
            "layout_constraintLeft_toLeftOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintLeft_toLeftOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = Attribute(
            "layout_constraintLeft_toRightOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintLeft_toRightOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = Attribute(
            "layout_constraintRight_toLeftOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintRight_toLeftOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = Attribute(
            "layout_constraintRight_toRightOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintRight_toRightOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = Attribute(
            "layout_constraintTop_toTopOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintTop_toTopOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = Attribute(
            "layout_constraintTop_toBottomOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintTop_toBottomOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = Attribute(
            "layout_constraintBottom_toTopOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintBottom_toTopOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = Attribute(
            "layout_constraintBottom_toBottomOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintBottom_toBottomOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = Attribute(
            "layout_constraintBaseline_toBaselineOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintBaseline_toBaselineOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_START_OF = Attribute(
            "layout_constraintStart_toStartOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintStart_toStartOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_END_OF = Attribute(
            "layout_constraintStart_toEndOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintStart_toEndOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_START_OF = Attribute(
            "layout_constraintEnd_toStartOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintEnd_toStartOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_END_OF = Attribute(
            "layout_constraintEnd_toEndOf",
            androidx.constraintlayout.widget.R.attr.layout_constraintEnd_toEndOf
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = Attribute(
            "layout_constraintHorizontal_bias",
            androidx.constraintlayout.widget.R.attr.layout_constraintHorizontal_bias
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS = Attribute(
            "layout_constraintVertical_bias",
            androidx.constraintlayout.widget.R.attr.layout_constraintVertical_bias
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_WIDTH = Attribute(
            "layout_constrainedWidth",
            androidx.constraintlayout.widget.R.attr.layout_constrainedWidth
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_HEIGHT = Attribute(
            "layout_constrainedHeight",
            androidx.constraintlayout.widget.R.attr.layout_constrainedHeight
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE = Attribute(
            "layout_constraintCircle",
            androidx.constraintlayout.widget.R.attr.layout_constraintCircle
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_RADIUS = Attribute(
            "layout_constraintCircleRadius",
            androidx.constraintlayout.widget.R.attr.layout_constraintCircleRadius
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_ANGLE = Attribute(
            "layout_constraintCircleAngle",
            androidx.constraintlayout.widget.R.attr.layout_constraintCircleAngle
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_LEFT = Attribute(
            "layout_marginLeft", android.R.attr.layout_marginLeft
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_TOP = Attribute(
            "layout_marginTop", android.R.attr.layout_marginTop
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_RIGHT = Attribute(
            "layout_marginRight", android.R.attr.layout_marginRight
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_BOTTOM = Attribute(
            "layout_marginBottom", android.R.attr.layout_marginBottom
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_START = Attribute(
            "layout_marginStart", android.R.attr.layout_marginStart
        )
        val CONSTRAINTLAYOUT_LAYOUT_MARGIN_END = Attribute(
            "layout_marginEnd", android.R.attr.layout_marginEnd
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_LEFT = Attribute(
            "layout_goneMarginLeft", androidx.constraintlayout.widget.R.attr.layout_goneMarginLeft
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_TOP = Attribute(
            "layout_goneMarginTop", androidx.constraintlayout.widget.R.attr.layout_goneMarginTop
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_RIGHT = Attribute(
            "layout_goneMarginRight", androidx.constraintlayout.widget.R.attr.layout_goneMarginRight
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_BOTTOM = Attribute(
            "layout_goneMarginBottom",
            androidx.constraintlayout.widget.R.attr.layout_goneMarginBottom
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_START = Attribute(
            "layout_goneMarginStart", androidx.constraintlayout.widget.R.attr.layout_goneMarginStart
        )
        val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_END = Attribute(
            "layout_goneMarginEnd", androidx.constraintlayout.widget.R.attr.layout_goneMarginEnd
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_PERCENT = Attribute(
            "layout_constraintWidth_percent",
            androidx.constraintlayout.widget.R.attr.layout_constraintWidth_percent
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HEIGHT_PERCENT = Attribute(
            "layout_constraintHeight_percent",
            androidx.constraintlayout.widget.R.attr.layout_constraintHeight_percent
        )
        val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_RATIO = Attribute(
            "layout_constraintDimensionRatio",
            androidx.constraintlayout.widget.R.attr.layout_constraintDimensionRatio
        )
        val CONSTRAINTLAYOUT_CHAIN_USED_POSITION =
            Attribute("chainUseRtl", androidx.constraintlayout.widget.R.attr.chainUseRtl)
        val CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE = Attribute(
            "layout_constraintHorizontal_chainStyle",
            androidx.constraintlayout.widget.R.attr.layout_constraintHorizontal_chainStyle
        )
        val CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE = Attribute(
            "layout_constraintVertical_chainStyle",
            androidx.constraintlayout.widget.R.attr.layout_constraintVertical_chainStyle
        )
        val CONSTRAINTLAYOUT_DIMENSION_RATIO = Attribute(
            "layout_constraintDimensionRatio",
            androidx.constraintlayout.widget.R.attr.layout_constraintDimensionRatio
        )
        val CONSTRAINTLAYOUT_LAYOUT_ANIMATE_LAYOUT_CHANGES = Attribute(
            "animateLayoutChanges", android.R.attr.animateLayoutChanges
        )
    }

    object VideoView {
        val VIDEO_VIEW_URI = Attribute("uri", 0)
        val VIDEO_VIEW_PATH = Attribute("path", 0)
        val VIDEO_VIEW_KEEP_SCREEN_ON = Attribute(
            "keepScreenOn", android.R.attr.keepScreenOn
        )
    }

    object Switch {
        val SWITCH_TEXT_ON = Attribute("textOn", android.R.attr.textOn)
        val SWITCH_TEXT_OFF = Attribute("textOff", android.R.attr.textOff)
        val SWITCH_SHOW_TEXT = Attribute("showText", android.R.attr.showText)
        val SWITCH_THUMB_TEXT_PADDING =
            Attribute("thumbTextPadding", android.R.attr.thumbTextPadding)
        val SWITCH_SWITCH_PADDING = Attribute("switchPadding", android.R.attr.switchPadding)
        val SWITCH_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object TabLayout {
        val TAB_INDICATOR_GRAVITY = Attribute(
            "tabIndicatorGravity", com.google.android.material.R.attr.tabIndicatorGravity
        )
        val TAB_ITEM_PADDING =
            Attribute("tabPadding", com.google.android.material.R.attr.tabPadding)
        val TAB_ITEM_BACKGROUND =
            Attribute("tabBackground", com.google.android.material.R.attr.tabBackground)
        val TAB_MODE = Attribute("tabMode", com.google.android.material.R.attr.tabMode)
    }

    object MediaController {
        val MEDIA_PREVIOUS = Attribute(
            "showPrevious", 0
        )
        val MEDIA_NEXT = Attribute("showNext", 0)
        val MEDIA_TITLE = Attribute("showTitle", 0)
    }

    object MaterialButton {
        val MATERIAL_BUTTON_BACKGROUND_TINT = Attribute(
            "backgroundTint", com.google.android.material.R.attr.backgroundTint
        )
        val MATERIAL_BUTTON_CORNER_RADIUS =
            Attribute("cornerRadius", com.google.android.material.R.attr.cornerRadius)
        val MATERIAL_BUTTON_ICON = Attribute("icon", com.google.android.material.R.attr.icon)
        val MATERIAL_BUTTON_ICON_GRAVITY =
            Attribute("iconGravity", com.google.android.material.R.attr.iconGravity)
        val MATERIAL_BUTTON_ICON_PADDING =
            Attribute("iconPadding", com.google.android.material.R.attr.iconPadding)
        val MATERIAL_BUTTON_ICON_TINT =
            Attribute("iconTint", com.google.android.material.R.attr.iconTint)
        val MATERIAL_BUTTON_ICON_SIZE =
            Attribute("iconSize", com.google.android.material.R.attr.iconSize)
        val MATERIAL_BUTTON_STROKE_COLOR =
            Attribute("strokeColor", com.google.android.material.R.attr.strokeColor)
        val MATERIAL_BUTTON_STROKE_WIDTH =
            Attribute("strokeWidth", com.google.android.material.R.attr.strokeWidth)
        val MATERIAL_BUTTON_RIPPLE_COLOR =
            Attribute("rippleColor", com.google.android.material.R.attr.rippleColor)
        val MATERIAL_BUTTON_SHAPE_APPEARANCE_OVERRIDE = Attribute(
            "shapeAppearanceOverlay", com.google.android.material.R.attr.shapeAppearanceOverlay
        )
        val MATERIAL_BUTTON_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object MaterialTextView {
        val MATERIAL_TEXTVIEW_HINT_TEXT_APPEARANCE =
            Attribute("hintTextAppearance", com.google.android.material.R.attr.hintTextAppearance)
        val MATERIAL_TEXTVIEW_HINT_TEXT_COLOR =
            Attribute("hintTextColor", com.google.android.material.R.attr.hintTextColor)
        val MATERIAL_TEXTVIEW_ERROR_TEXT_COLOR =
            Attribute("errorTextColor", com.google.android.material.R.attr.errorTextColor)
        val MATERIAL_TEXTVIEW_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object TextInputLayout {
        val TEXT_INPUT_LAYOUT_HINT_ENABLED =
            Attribute("hintEnabled", com.google.android.material.R.attr.hintEnabled)
        val TEXT_INPUT_LAYOUT_HINT_ANIMATED = Attribute(
            "hintAnimationEnabled", com.google.android.material.R.attr.hintAnimationEnabled
        )
        val TEXT_INPUT_LAYOUT_HINT_TEXT_APPEARANCE2 = Attribute(
            "hintTextAppearance", com.google.android.material.R.attr.hintTextAppearance
        )
        val TEXT_INPUT_LAYOUT_ERROR_ENABLED =
            Attribute("errorEnabled", com.google.android.material.R.attr.errorEnabled)
        val TEXT_INPUT_LAYOUT_END_ICON_MODE =
            Attribute("endIconMode", com.google.android.material.R.attr.endIconMode)
        val TEXT_INPUT_LAYOUT_START_ICON_DRAWABLE =
            Attribute("startIconDrawable", com.google.android.material.R.attr.startIconDrawable)
        val TEXT_INPUT_LAYOUT_END_ICON_DRAWABLE =
            Attribute("endIconDrawable", com.google.android.material.R.attr.endIconDrawable)
        val TEXT_INPUT_LAYOUT_BOX_BACKGROUND_MODE =
            Attribute("boxBackgroundMode", com.google.android.material.R.attr.boxBackgroundMode)
        val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_START = Attribute(
            "boxCornerRadiusTopStart", com.google.android.material.R.attr.boxCornerRadiusTopStart
        )
        val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_END = Attribute(
            "boxCornerRadiusTopEnd", com.google.android.material.R.attr.boxCornerRadiusTopEnd
        )
        val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_START = Attribute(
            "boxCornerRadiusBottomStart",
            com.google.android.material.R.attr.boxCornerRadiusBottomStart
        )
        val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_END = Attribute(
            "boxCornerRadiusBottomEnd", com.google.android.material.R.attr.boxCornerRadiusBottomEnd
        )
        val TEXT_INPUT_LAYOUT_PREFIX_TEXT =
            Attribute("prefixText", com.google.android.material.R.attr.prefixText)
        val TEXT_INPUT_LAYOUT_SUFFIX_TEXT =
            Attribute("suffixText", com.google.android.material.R.attr.suffixText)
        val TEXT_INPUT_LAYOUT_COUNTER_ENABLED =
            Attribute("counterEnabled", com.google.android.material.R.attr.counterEnabled)
        val TEXT_INPUT_LAYOUT_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object MaterialNavigationBarView {
        val NAVIGATION_BAR_VIEW_MENU = Attribute("menu", com.google.android.material.R.attr.menu)
        val NAVIGATION_BAR_VIEW_ITEM_ICON_TINT =
            Attribute("itemIconTint", com.google.android.material.R.attr.itemIconTint)
        val NAVIGATION_BAR_VIEW_ITEM_TEXT_COLOR =
            Attribute("itemTextColor", com.google.android.material.R.attr.itemTextColor)
        val NAVIGATION_BAR_VIEW_LABEL_VISIBILITY_MODE =
            Attribute("labelVisibilityMode", com.google.android.material.R.attr.labelVisibilityMode)
        val NAVIGATION_BAR_VIEW_ITEM_PADDING =
            Attribute("itemPadding", com.google.android.material.R.attr.itemPadding)
        val ITEM_HORIZONTAL_TRANSLATION_ENABLED = Attribute(
            "itemHorizontalTranslationEnabled",
            com.google.android.material.R.attr.itemHorizontalTranslationEnabled
        )
        val ITEM_ICON_SIZE =
            Attribute("itemIconSize", com.google.android.material.R.attr.itemIconSize)
    }

    object MaterialBottomNavigationView {
        val BOTTOM_NAVIGATION_VIEW_ITEM_SHRINK = Attribute(
            "itemHorizontalTranslationEnabled",
            com.google.android.material.R.attr.itemHorizontalTranslationEnabled
        )
        val BOTTOM_NAVIGATION_VIEW_ITEM_SHAPE_FILL_COLOR =
            Attribute("itemShapeFillColor", com.google.android.material.R.attr.itemShapeFillColor)
        val ITEM_SHAPE_APPEARANCE =
            Attribute("itemShapeAppearance", com.google.android.material.R.attr.itemShapeAppearance)
    }

    object ExtendedFloatingActionButton {
        val EXTENDED_FLOATING_ACTION_BUTTON_SHOW_MOTION_SPEC =
            Attribute("showMotionSpec", com.google.android.material.R.attr.showMotionSpec)
        val EXTENDED_FLOATING_ACTION_BUTTON_HIDE_MOTION_SPEC =
            Attribute("hideMotionSpec", com.google.android.material.R.attr.hideMotionSpec)
        val FAB_SIZE = Attribute("fabSize", com.google.android.material.R.attr.fabSize)
    }

    object Chip {
        val CHIP_CHIP_BACKGROUND_COLOR =
            Attribute("chipBackgroundColor", com.google.android.material.R.attr.chipBackgroundColor)
        val CHIP_CLOSE_ICON_ENABLED =
            Attribute("closeIconEnabled", com.google.android.material.R.attr.closeIconEnabled)
        val CHIP_CHIP_ICON_SIZE =
            Attribute("chipIconSize", com.google.android.material.R.attr.chipIconSize)
        val CHIP_CLOSE_ICON = Attribute("closeIcon", com.google.android.material.R.attr.closeIcon)
        val CHIP_TEXT_APPEARANCE = Attribute(
            "textAppearance", android.R.attr.textAppearance
        )
    }

    object ChipGroup {
        val CHIP_GROUP_SINGLE_LINE = Attribute(
            "singleLine", com.google.android.material.R.attr.singleLine
        )
        val CHIP_GROUP_SINGLE_SELECTION =
            Attribute("singleSelection", com.google.android.material.R.attr.singleSelection)
    }

    object Calender {
        val YEAR_VIEW = Attribute("yearView", 0)
        val MONTH_VIEW = Attribute("monthView", 0)
        val DATE_VIEW = Attribute("dayView", 0)
        val SHOW_WEEK = Attribute("showWeekNumber", 0)
    }

    object BottomSheet {
        val BEHAVIOR_PEEK_HEIGHT = Attribute(
            "behavior_peekHeight", 0
        ) // No direct android.R.attr, BottomSheet behavior property
        val BEHAVIOR_HIDE_ABLE = Attribute(
            "behavior_hideable", 0
        ) // No direct android.R.attr, BottomSheet behavior property
    }

    object ScrollBar {
        val VIEW_VERTICAL_THUMB_DRAWABLE = Attribute(
            "verticalScrollbarThumbDrawable", 0
        )
        val VERTICAL_TRACK = Attribute(
            "verticalScrollbarTrackDrawable", 0
        )

        @RequiresApi(Build.VERSION_CODES.M)
        val SCROLL_DEFAULT = Attribute(
            "scrollIndicators", android.R.attr.scrollIndicators
        )
        val SMOOTH_SCROLL = Attribute(
            "smoothScrollbar", android.R.attr.smoothScrollbar
        )
    }
}