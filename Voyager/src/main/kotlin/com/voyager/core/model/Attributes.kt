package com.voyager.core.model

import android.os.Build
import androidx.annotation.RequiresApi

object Attributes {

    object Common {
        const val ID = "id"
        const val VISIBILITY = "visibility"
        const val BACKGROUND = "background"
        const val BACKGROUND_TINT = "backgroundTint"
        const val BACKGROUND_TINT_MODE =
            "backgroundTintMode"
        const val CLICKABLE = "clickable"
        const val LONG_CLICKABLE = "longClickable"
        const val ENABLED = "enabled"
        const val CONTENT_DESCRIPTION = "contentDescription"
        const val TAG = "tag"
        const val PADDING = "padding"
        const val PADDING_LEFT = "paddingLeft"
        const val PADDING_TOP = "paddingTop"
        const val PADDING_RIGHT = "paddingRight"
        const val PADDING_BOTTOM = "paddingBottom"
        const val PADDING_START = "paddingStart"
        const val PADDING_END = "paddingEnd"
        const val FOREGROUND = "foreground"
        const val FOREGROUND_GRAVITY = "foregroundGravity"
        const val FOREGROUND_TINT = "foregroundTint"
        const val FOREGROUND_TINT_MODE =
            "foregroundTintMode"
        const val ROTATION = "rotation"
        const val ROTATION_X = "rotationX"
        const val ROTATION_Y = "rotationY"
        const val SCALE_X = "scaleX"
        const val SCALE_Y = "scaleY"
        const val TRANSLATION_X = "translationX"
        const val TRANSLATION_Y = "translationY"
        const val TRANSLATION_Z = "translationZ"
        const val ELEVATION = "elevation"
        const val DRAWING_CACHE_QUALITY =
            "drawingCacheQuality"
        const val ALPHA = "alpha"
        const val LAYOUT_WIDTH = "layout_width"
        const val LAYOUT_HEIGHT = "layout_height"
        const val LAYOUT_MARGIN = "layout_margin"
        const val LAYOUT_MARGIN_LEFT = "layout_marginLeft"
        const val LAYOUT_MARGIN_TOP = "layout_marginTop"
        const val LAYOUT_MARGIN_RIGHT = "layout_marginRight"
        const val LAYOUT_MARGIN_BOTTOM =
            "layout_marginBottom"
        const val LAYOUT_MARGIN_START = "layout_marginStart"
        const val LAYOUT_MARGIN_END = "layout_marginEnd"
        const val LAYOUT_GRAVITY = "layout_gravity"
        const val GRAVITY = "gravity"
        const val WEIGHT = "layout_weight"
        const val STYLE = "style"
        const val MIN_WIDTH = "minWidth"
        const val MIN_HEIGHT = "minHeight"
        const val MAX_WIDTH = "maxWidth"
        const val MAX_HEIGHT = "maxHeight"
        const val TEXT = "text"
        const val WIDTH = "width" // Using layout_width as it generally refers to view width
        const val HEIGHT = "height" // Using layout_height as it generally refers to view height
        const val TEXT_SIZE = "textSize"
        const val TEXT_COLOR = "textColor"
        const val TEXT_COLOR_HINT = "textColorHint"
        const val ELLIPSIZE = "ellipsize"
        const val FONT_FAMILY = "fontFamily"
        const val LETTER_SPACING = "letterSpacing"
        const val LINE_SPACING_EXTRA = "lineSpacingExtra"
        const val LINE_SPACING_MULTIPLIER =
            "lineSpacingMultiplier"
        const val SHADOW_COLOR = "shadowColor"
        const val SHADOW_DX = "shadowDx"
        const val SHADOW_DY = "shadowDy"
        const val SHADOW_RADIUS = "shadowRadius"
        const val TEXT_SCALE_X = "textScaleX"
        const val TEXT_STYLE = "textStyle"
        const val TEXT_ALL_CAPS = "textAllCaps"
        const val HINT = "hint"
        const val IME_OPTIONS = "imeOptions"
        const val MAX_LINES = "maxLines"
        const val TRANSFORMATION_METHOD = "transformationMethod"
        const val PADDING_HORIZONTAL = "paddingHorizontal"
        const val PADDING_VERTICAL = "paddingVertical"
        const val SCROLLBARS = "scrollbars"
        const val OVER_SCROLL_MODE = "overScrollMode"
        const val DRAWABLE_PADDING = "drawablePadding"
        const val DRAWABLE_START = "drawableStart"
        const val DRAWABLE_END = "drawableEnd"
        const val DRAWABLE_TOP = "drawableTop"
        const val DRAWABLE_BOTTOM = "drawableBottom"
        const val TINT = "tint"
        const val TINT_MODE = "tintMode"
        const val IMPORTANT_FOR_ACCESSIBILITY =
            "importantForAccessibility"

        @RequiresApi(Build.VERSION_CODES.P)
        const val SCREEN_READER_FOCUSABLE =
            "screenReaderFocusable"

        @RequiresApi(Build.VERSION_CODES.O)
        const val TOOLTIP_TEXT = "tooltipText"
        const val CLIP_TO_PADDING = "clipToPadding"

        @RequiresApi(Build.VERSION_CODES.M)
        const val SCROLL_INDICATORS = "scrollIndicators"

        @RequiresApi(Build.VERSION_CODES.M)
        const val DRAWABLE_TINT = "drawableTint"

        @RequiresApi(Build.VERSION_CODES.M)
        const val DRAWABLE_TINT_MODE = "drawableTintMode"
    }

    object View {
        const val VIEW_ABOVE = "layout_above"
        const val VIEW_ACTIVATED = "activated"
        const val VIEW_ALIGN_BASELINE =
            "layout_alignBaseline"
        const val VIEW_ALIGN_BOTTOM = "layout_alignBottom"
        const val VIEW_ALIGN_END = "layout_alignEnd"
        const val VIEW_ALIGN_LEFT = "layout_alignLeft"
        const val VIEW_ALIGN_PARENT_BOTTOM =
            "layout_alignParentBottom"
        const val VIEW_ALIGN_PARENT_END =
            "layout_alignParentEnd"
        const val VIEW_ALIGN_PARENT_LEFT =
            "layout_alignParentLeft"
        const val VIEW_ALIGN_PARENT_RIGHT =
            "layout_alignParentRight"
        const val VIEW_ALIGN_PARENT_START =
            "layout_alignParentStart"
        const val VIEW_ALIGN_PARENT_TOP =
            "layout_alignParentTop"
        const val VIEW_ALIGN_RIGHT = "layout_alignRight"
        const val VIEW_ALIGN_START = "layout_alignStart"
        const val VIEW_ALIGN_TOP = "layout_alignTop"
        const val VIEW_ANIMATION = "animation"
        const val VIEW_BELOW = "layout_below"
        const val VIEW_CENTER_HORIZONTAL =
            "layout_centerHorizontal"
        const val VIEW_CENTER_IN_PARENT =
            "layout_centerInParent"
        const val VIEW_CENTER_VERTICAL =
            "layout_centerVertical"

        @RequiresApi(Build.VERSION_CODES.M)
        const val VIEW_CONTEXT_CLICKABLE = "contextClickable"
        const val VIEW_FADE_SCROLLBARS = "fadeScrollbars"
        const val VIEW_FADING_EDGE_LENGTH = "fadingEdgeLength"
        const val VIEW_FILTER_TOUCHES_WHEN_OBSCURED =
            "filterTouchesWhenObscured"
        const val VIEW_FITS_SYSTEM_WINDOWS =
            "fitsSystemWindows"
        const val VIEW_FOCUSABLE = "focusable"
        const val VIEW_FOCUSABLE_IN_TOUCH_MODE =
            "focusableInTouchMode"

        @RequiresApi(Build.VERSION_CODES.N)
        const val VIEW_FORCE_HAS_OVERLAPPING_RENDERING =
            "forceHasOverlappingRendering"
        const val VIEW_HAPTIC_FEEDBACK_ENABLED =
            "hapticFeedbackEnabled"
        const val VIEW_IS_SCROLL_CONTAINER =
            "isScrollContainer"
        const val VIEW_KEEP_SCREEN_ON = "keepScreenOn"
        const val VIEW_LAYER_TYPE = "layerType"
        const val VIEW_LAYOUT_DIRECTION = "layoutDirection"
        const val VIEW_LONG_CLICKABLE = "longClickable"
        const val VIEW_NEXT_FOCUS_DOWN = "nextFocusDown"
        const val VIEW_NEXT_FOCUS_FORWARD = "nextFocusForward"
        const val VIEW_NEXT_FOCUS_LEFT = "nextFocusLeft"
        const val VIEW_NEXT_FOCUS_RIGHT = "nextFocusRight"
        const val VIEW_NEXT_FOCUS_UP = "nextFocusUp"

        @Suppress("DEPRECATION")
        const val VIEW_ON_CLICK = "onClick"
        const val VIEW_ON_LONG_CLICK = "onLongClick"
        const val VIEW_ON_TOUCH = "onTouch"
        const val VIEW_REQUIRES_FADING_EDGE =
            "requiresFadingEdge"
        const val VIEW_SAVE_ENABLED = "saveEnabled"
        const val VIEW_SCROLLBAR_DEFAULT_DELAY_BEFORE_FADE = "scrollbarDefaultDelayBeforeFade"
        const val VIEW_SCROLLBAR_FADE_DURATION =
            "scrollbarFadeDuration"
        const val VIEW_SCROLLBAR_SIZE = "scrollbarSize"
        const val VIEW_SCROLLBAR_STYLE = "scrollbarStyle"
        const val VIEW_SELECTED = "selected"
        const val VIEW_SOUND_EFFECTS_ENABLED =
            "soundEffectsEnabled"
        const val VIEW_TEXT_ALIGNMENT = "textAlignment"
        const val VIEW_TEXT_DIRECTION = "textDirection"
        const val VIEW_TO_END_OF = "layout_toEndOf"
        const val VIEW_TO_LEFT_OF = "layout_toLeftOf"
        const val VIEW_TO_RIGHT_OF = "layout_toRightOf"
        const val VIEW_TO_START_OF = "layout_toStartOf"
        const val VIEW_TRANSFORM_PIVOT_X = "transformPivotX"
        const val VIEW_TRANSFORM_PIVOT_Y = "transformPivotY"
        const val VIEW_TRANSITION_NAME = "transitionName"
        const val VIEW_BASELINE_ALIGN_BOTTOM =
            "baselineAlignBottom"
        const val VIEW_STATE_LIST_ANIMATOR =
            "stateListAnimator"
        const val VIEW_LAYOUT_ANIMATION = "layoutAnimation"
        const val VIEW_ANIMATE_LAYOUT_CHANGES =
            "animateLayoutChanges"
    }

    object WebView {
        const val WEB_VIEW_URL = "url"
        const val WEB_VIEW_HTML = "html"
        const val WEB_VIEW_JAVASCRIPT_ENABLED = "javaScriptEnabled"
        const val WEB_VIEW_DOM_STORAGE_ENABLED = "domStorageEnabled"
        const val WEB_VIEW_USER_AGENT = "userAgentString"
        const val WEB_VIEW_LOAD_WITH_OVERVIEW_MODE = "loadWithOverviewMode"
        const val WEB_VIEW_USE_WIDE_VIEWPORT = "useWideViewPort"
        const val WEB_VIEW_DISPLAY_ZOOM_CONTROLS = "displayZoomControls"
        const val WEB_VIEW_ALLOW_FILE_ACCESS = "allowFileAccessFromFileURLs"
        const val WEB_VIEW_TEXT_ZOOM = "textZoom"
        const val WEB_VIEW_GEOLOCATION_ENABLED = "geolocationEnabled"
        const val WEB_VIEW_SCROLLBARS = "scrollbars"
        const val WEB_VIEW_OVER_SCROLL_MODE = "overScrollMode"
    }

    object RatingBar {
        const val RATINGBAR_NUM_STARS = "numStars"
        const val RATINGBAR_RATING = "rating"
        const val RATINGBAR_IS_INDICATOR = "isIndicator"
        const val RATINGBAR_STEP_SIZE = "stepSize"
        const val RATINGBAR_PROGRESS_DRAWABLE =
            "progressDrawable"
    }

    object TextView {
        const val TEXTVIEW_HTML = "html"
        const val TEXTVIEW_TEXT_COLOR_LINK = "textColorLink"
        const val TEXTVIEW_TEXT_COLOR_HIGHLIGHT =
            "textColorHighlight"
        const val TEXTVIEW_PAINT_FLAGS = "paintFlags"
        const val TEXTVIEW_PREFIX = "prefix"
        const val TEXTVIEW_SUFFIX = "suffix"

        @Suppress("DEPRECATION")
        const val TEXTVIEW_SINGLE_LINE = "singleLine"
        const val TEXTVIEW_AUTO_LINK = "autoLink"

        @RequiresApi(Build.VERSION_CODES.M)
        const val TEXTVIEW_BREAK_STRATEGY = "breakStrategy"
        const val TEXTVIEW_CURSOR_VISIBLE = "cursorVisible"

        @Suppress("DEPRECATION")
        const val TEXTVIEW_EDITABLE = "editable"
        const val TEXTVIEW_EMS = "ems"

        @RequiresApi(Build.VERSION_CODES.M)
        const val TEXTVIEW_HYPHENATION_FREQUENCY =
            "hyphenationFrequency"
        const val TEXTVIEW_MAX_EMS = "maxEms"
        const val TEXTVIEW_MIN_EMS = "minEms"
        const val TEXTVIEW_PRIVATE_IME_OPTIONS =
            "privateImeOptions"
        const val TEXTVIEW_SELECT_ALL_ON_FOCUS =
            "selectAllOnFocus"
        const val TEXTVIEW_TEXT_SCALE_X = "textScaleX"
        const val TEXTVIEW_MARQUEE_REPEAT_LIMIT =
            "marqueeRepeatLimit"
        const val TEXTVIEW_INPUT_TYPE = "inputType"
        const val TEXTVIEW_TEXT_APPEARANCE = "textAppearance"

        @RequiresApi(Build.VERSION_CODES.P)
        const val TEXTVIEW_FONT_VARIATION_SETTINGS =
            "fontVariationSettings"

        @RequiresApi(Build.VERSION_CODES.P)
        const val TEXTVIEW_LINE_HEIGHT = "lineHeight"

        @RequiresApi(Build.VERSION_CODES.P)
        const val TEXTVIEW_FIRST_BASELINE_TO_TOP_HEIGHT =
            "firstBaselineToTopHeight"

        @RequiresApi(Build.VERSION_CODES.P)
        const val TEXTVIEW_LAST_BASELINE_TO_BOTTOM_HEIGHT =
            "lastBaselineToBottomHeight"
    }

    object EditText {
        const val EDITTEXT_INPUT_TYPE = "inputType"

        @Suppress("DEPRECATION")
        const val EDITTEXT_PASSWORD = "password"

        @Suppress("DEPRECATION")
        const val EDITTEXT_SINGLE_LINE = "singleLine"
        const val EDITTEXT_SELECT_ALL_ON_FOCUS = "selectAllOnFocus"

        @Suppress("DEPRECATION")
        const val EDITTEXT_EDITABLE = "editable"

        @Suppress("DEPRECATION")
        const val EDITTEXT_AUTO_TEXT = "autoText"
        const val EDITTEXT_BUFFER_TYPE = "bufferType"
        const val EDITTEXT_COMPLETION_HINT = "completionHint"
        const val EDITTEXT_COMPLETION_MODE = "completionMode"
        const val EDITTEXT_DIGITS = "digits"
        const val EDITTEXT_ERROR = "error"
        const val EDITTEXT_IME_ACTION_LABEL = "imeActionLabel"
        const val EDITTEXT_IME_ACTION_ID = "imeActionId"
        const val EDITTEXT_MAX_EMS = "maxEms"
        const val EDITTEXT_MAX_LENGTH = "maxLength"
        const val EDITTEXT_MIN_EMS = "minEms"
        const val EDITTEXT_PRIVATE_IME_OPTIONS = "privateImeOptions"
        const val EDITTEXT_SCROLL_HORIZONTALLY = "scrollHorizontally"
        const val EDITTEXT_TEXT_SCALE_X = "textScaleX"
        const val EDITTEXT_TEXT_APPEARANCE = "textAppearance"
    }

    object Button {
        const val BUTTON_BACKGROUND = "background"
        const val BUTTON_ENABLED = "enabled"
        const val BUTTON_PADDING = "padding"

        @Suppress("DEPRECATION")
        const val BUTTON_ON_CLICK = "onClick"
        const val BUTTON_TEXT_APPEARANCE = "textAppearance"
    }

    object CheckBox {
        const val CHECKBOX_CHECKED = "checked"
        const val CHECKBOX_BUTTON = "button"
    }

    object FrameLayout {
        const val FRAME_LAYOUT_HEIGHT_RATIO = "heightRatio"
        const val FRAME_LAYOUT_WIDTH_RATIO = "widthRatio"
        const val FRAME_LAYOUT_MEASURE_ALL_CHILDREN = "measureAllChildren"
    }

    object ImageView {
        const val IMAGEVIEW_SRC = "src"
        const val IMAGEVIEW_SCALE_TYPE = "scaleType"
        const val IMAGEVIEW_ADJUST_VIEW_BOUNDS = "adjustViewBounds"
        const val IMAGEVIEW_CROP_TO_PADDING = "cropToPadding"
        const val IMAGEVIEW_MAX_HEIGHT = "maxHeight"
        const val IMAGEVIEW_MAX_WIDTH = "maxWidth"
        const val IMAGEVIEW_FOREGROUND = "foreground"
        const val IMAGEVIEW_TINT = "tint"
        const val IMAGEVIEW_TINT_MODE = "tintMode"
    }

    object ViewGroup {
        const val VIEW_GROUP_CHILDREN = "children"
        const val VIEW_GROUP_CLIP_CHILDREN = "clipChildren"
        const val VIEW_GROUP_CLIP_TO_PADDING = "clipToPadding"
        const val VIEW_GROUP_LAYOUT_MODE = "layoutMode"
        const val VIEW_GROUP_SPLIT_MOTION_EVENTS = "splitMotionEvents"
        const val VIEW_GROUP_ADD_STATES_FROM_CHILDREN = "addStatesFromChildren"
        const val VIEW_GROUP_ALWAYS_DRAWN_WITH_CACHE = "alwaysDrawnWithCache"
        const val VIEW_GROUP_ANIMATION_CACHE = "animationCache"
        const val VIEW_GROUP_LAYOUT_ANIMATION = "layoutAnimation"
        const val VIEW_GROUP_PERSISTENT_DRAWING_CACHE = "persistentDrawingCache"
        const val VIEW_GROUP_TOUCHSCREEN_BLOCKS_FOCUS = "touchscreenBlocksFocus"
        const val VIEW_GROUP_TRANSITION_GROUP = "transitionGroup"
        const val VIEW_GROUP_ANIMATE_LAYOUT_CHANGES = "animateLayoutChanges"
    }

    object LinearLayout {
        const val LINEARLAYOUT_ORIENTATION = "orientation"
        const val LINEARLAYOUT_DIVIDER = "divider"
        const val LINEARLAYOUT_DIVIDER_PADDING = "dividerPadding"
        const val LINEARLAYOUT_SHOW_DIVIDERS = "showDividers"
        const val LINEARLAYOUT_WEIGHT_SUM = "weightSum"
        const val LINEARLAYOUT_MEASURE_WITH_LARGEST_CHILD = "measureWithLargestChild"
        const val LINEARLAYOUT_BASELINE_ALIGNED = "baselineAligned"
        const val LINEARLAYOUT_BASELINE_ALIGNED_CHILD_INDEX = "baselineAlignedChildIndex"
        const val LINEARLAYOUT_ANIMATE_LAYOUT_CHANGES = "animateLayoutChanges"
    }

    object ScrollView {
        const val SCROLLVIEW_FILL_VIEWPORT = "fillViewport"
        const val SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED = "smoothScrollbar"
        const val SCROLLVIEW_SCROLLBARS = "scrollbars"
        const val SCROLLVIEW_OVER_SCROLL_MODE = "overScrollMode"
    }

    object HorizontalScrollView {
        const val HORIZONTAL_SCROLLVIEW_FILL_VIEWPORT = "fillViewPort"
        const val HORIZONTAL_SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED = "smoothScrollbar"
        const val HORIZONTAL_SCROLLVIEW_SCROLLBARS = "scrollbars"
        const val HORIZONTAL_SCROLLVIEW_OVER_SCROLL_MODE = "overScrollMode"
    }

    object ProgressBar {
        const val PROGRESSBAR_PROGRESS = "progress"
        const val PROGRESSBAR_MAX = "max"
        const val PROGRESSBAR_PROGRESS_TINT = "progressTint"
        const val PROGRESSBAR_INDETERMINATE_TINT = "indeterminateTint"
        const val PROGRESSBAR_SECONDARY_PROGRESS_TINT = "secondaryProgressTint"
        const val PROGRESSBAR_INDETERMINATE = "indeterminate"
        const val PROGRESSBAR_INDETERMINATE_DURATION = "indeterminateDuration"
        const val PROGRESSBAR_INDETERMINATE_ONLY = "indeterminateOnly"
        const val PROGRESSBAR_PROGRESS_BACKGROUND_TINT = "progressBackgroundTint"
        const val PROGRESSBAR_PROGRESS_TINT_MODE = "progressTintMode"
        const val PROGRESSBAR_ROTATION = "rotation"
        const val PROGRESSBAR_SCALE_X = "scaleX"
        const val PROGRESSBAR_SCALE_Y = "scaleY"
        const val PROGRESSBAR_SECONDARY_PROGRESS = "secondaryProgress"
        const val PROGRESSBAR_SPLIT_TRACK = "splitTrack"
    }

    object RecyclerView {
        const val RECYCLERVIEW_LAYOUT_MANAGER = "layoutManager"
        const val RECYCLERVIEW_ITEM_DECORATION = "itemDecoration"
        const val RECYCLERVIEW_HAS_FIXED_SIZE = "hasFixed"
        const val RECYCLERVIEW_SCROLLBARS = "scrollbars"
        const val RECYCLERVIEW_CLIP_TO_PADDING = "clipToPadding"
        const val RECYCLERVIEW_OVER_SCROLL_MODE = "overScrollMode"
    }

    object ListView {
        const val LISTVIEW_DIVIDER = "divider"
        const val LISTVIEW_DIVIDER_HEIGHT = "dividerHeight"
        const val LISTVIEW_ENTRIES = "entries"
        const val LISTVIEW_FOOTER_DIVIDERS_ENABLED = "footerDividersEnabled"
        const val LISTVIEW_HEADER_DIVIDERS_ENABLED = "headerDividersEnabled"
        const val LISTVIEW_LIST_SELECTOR = "listSelector"
        const val LISTVIEW_SCROLLBARS = "scrollbars"
        const val LISTVIEW_SMOOTH_SCROLLBAR_ENABLED = "smoothScrollbar"
        const val LISTVIEW_TRANSCRIPT_MODE = "transcriptMode"
        const val LISTVIEW_STACK_FROM_BOTTOM = "stackFromBottom"
    }

    object GridView {
        const val GRIDVIEW_NUM_COLUMNS = "numColumns"
        const val GRIDVIEW_COLUMN_WIDTH = "columnWidth"
        const val GRIDVIEW_GRAVITY = "gravity"
        const val GRIDVIEW_HORIZONTAL_SPACING = "horizontalSpacing"
        const val GRIDVIEW_STRETCH_MODE = "stretchMode"
        const val GRIDVIEW_VERTICAL_SPACING = "verticalSpacing"
        const val GRIDVIEW_OVER_SCROLL_MODE = "overScrollMode"
        const val GRIDVIEW_SCROLLBARS = "scrollbars"
    }

    object GridLayout {
        const val GRIDLAYOUT_COLUMN_COUNT = "columnCount"
        const val GRIDLAYOUT_ROW_COUNT = "rowCount"
        const val GRIDLAYOUT_ORIENTATION = "orientation"
        const val GRIDLAYOUT_USE_DEFAULT_MARGINS = "useDefaultMargins"
        const val GRIDLAYOUT_ALIGNMENT_MODE = "alignmentMode"
        const val GRIDLAYOUT_COLUMN_ORDER_PRESERVED = "columnOrderPreserved"
        const val GRIDLAYOUT_ROW_ORDER_PRESERVED = "rowOrderPreserved"

        object LayoutParams {
            const val LAYOUT_COLUMN_SPAN = "layout_columnSpan"
            const val LAYOUT_ROW_SPAN = "layout_rowSpan"
            const val LAYOUT_COLUMN_WEIGHT = "layout_columnWeight"
            const val LAYOUT_ROW_WEIGHT = "layout_rowWeight"
            const val LAYOUT_GRAVITY = "layout_gravity"
            const val LAYOUT_COLUMN = "layout_column"
            const val LAYOUT_ROW = "layout_row"
        }
    }

    object FlexboxLayout {
        const val FLEXBOX_LAYOUT_FLEX_DIRECTION = "flexDirection"
        const val FLEXBOX_LAYOUT_FLEX_WRAP = "flexWrap"
        const val FLEXBOX_LAYOUT_JUSTIFY_CONTENT = "justifyContent"
        const val FLEXBOX_LAYOUT_ALIGN_ITEMS = "alignItems"
        const val FLEXBOX_LAYOUT_ALIGN_CONTENT = "alignContent"

        object LayoutParams {
            const val LAYOUT_FLEX_GROW = "layout_flexGrow"
            const val LAYOUT_FLEX_SHRINK = "layout_flexShrink"
            const val LAYOUT_FLEX_BASIS_PERCENT = "layout_flexBasisPercent"
            const val LAYOUT_ORDER = "layout_order"
            const val LAYOUT_ALIGN_SELF = "layout_alignSelf"
            const val LAYOUT_MIN_WIDTH = "layout_minWidth"
            const val LAYOUT_MIN_HEIGHT = "layout_minHeight"
            const val LAYOUT_MAX_WIDTH = "layout_maxWidth"
            const val LAYOUT_MAX_HEIGHT = "layout_maxHeight"
            const val LAYOUT_MARGIN = "layout_margin"
            const val LAYOUT_MARGIN_LEFT = "layout_marginLeft"
            const val LAYOUT_MARGIN_TOP = "layout_marginTop"
            const val LAYOUT_MARGIN_RIGHT = "layout_marginRight"
            const val LAYOUT_MARGIN_BOTTOM = "layout_marginBottom"
            const val LAYOUT_ASPECT_RATIO = "layout_aspectRatio"
        }
    }

    object CardView {
        const val CARD_VIEW_CARD_BACKGROUND_COLOR = "cardBackgroundColor"
        const val CARD_VIEW_CARD_CORNER_RADIUS = "cardCornerRadius"
        const val CARD_VIEW_CARD_ELEVATION = "cardElevation"
        const val CARD_VIEW_CARD_MAX_ELEVATION = "cardMaxElevation"
        const val CARD_VIEW_CARD_USE_COMPAT_PADDING = "cardUseCompatPadding"
        const val CARD_VIEW_CARD_PREVENT_CORNER_OVERLAP = "cardPreventCornerOverlap"
        const val CARD_VIEW_CONTENT_PADDING = "contentPadding"
        const val CARD_VIEW_CONTENT_PADDING_LEFT = "contentPaddingLeft"
        const val CARD_VIEW_CONTENT_PADDING_TOP = "contentPaddingTop"
        const val CARD_VIEW_CONTENT_PADDING_RIGHT = "contentPaddingRight"
        const val CARD_VIEW_CONTENT_PADDING_BOTTOM = "contentPaddingBottom"
        const val CARD_VIEW_FOREGROUND = "foreground"
        const val CARD_VIEW_FOREGROUND_GRAVITY = "foregroundGravity"
    }

    object ConstraintLayout {
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_BEGIN = "layout_constraintGuide_begin"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_END = "layout_constraintGuide_end"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_PERCENT = "layout_constraintGuide_percent"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = "layout_constraintLeft_toLeftOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = "layout_constraintLeft_toRightOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = "layout_constraintRight_toLeftOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = "layout_constraintRight_toRightOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = "layout_constraintTop_toTopOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = "layout_constraintTop_toBottomOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = "layout_constraintBottom_toTopOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = "layout_constraintBottom_toBottomOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = "layout_constraintBaseline_toBaselineOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_START_OF = "layout_constraintStart_toStartOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_END_OF = "layout_constraintStart_toEndOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_START_OF = "layout_constraintEnd_toStartOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_END_OF = "layout_constraintEnd_toEndOf"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = "layout_constraintHorizontal_bias"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS = "layout_constraintVertical_bias"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_WIDTH = "layout_constrainedWidth"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_HEIGHT = "layout_constrainedHeight"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE = "layout_constraintCircle"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_RADIUS = "layout_constraintCircleRadius"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_ANGLE = "layout_constraintCircleAngle"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_LEFT = "layout_marginLeft"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_TOP = "layout_marginTop"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_RIGHT = "layout_marginRight"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_BOTTOM = "layout_marginBottom"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_START = "layout_marginStart"
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_END = "layout_marginEnd"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_LEFT = "layout_goneMarginLeft"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_TOP = "layout_goneMarginTop"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_RIGHT = "layout_goneMarginRight"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_BOTTOM = "layout_goneMarginBottom"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_START = "layout_goneMarginStart"
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_END = "layout_goneMarginEnd"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_PERCENT = "layout_constraintWidth_percent"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HEIGHT_PERCENT = "layout_constraintHeight_percent"
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_RATIO = "layout_constraintDimensionRatio"
        const val CONSTRAINTLAYOUT_CHAIN_USED_POSITION = "chainUseRtl"
        const val CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE = "layout_constraintHorizontal_chainStyle"
        const val CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE = "layout_constraintVertical_chainStyle"
        const val CONSTRAINTLAYOUT_DIMENSION_RATIO = "layout_constraintDimensionRatio"
        const val CONSTRAINTLAYOUT_LAYOUT_ANIMATE_LAYOUT_CHANGES = "animateLayoutChanges"
    }

    object VideoView {
        const val VIDEO_VIEW_URI = "uri"
        const val VIDEO_VIEW_PATH = "path"
        const val VIDEO_VIEW_KEEP_SCREEN_ON = "keepScreenOn"
    }

    object Switch {
        const val SWITCH_TEXT_ON = "textOn"
        const val SWITCH_TEXT_OFF = "textOff"
        const val SWITCH_SHOW_TEXT = "showText"
        const val SWITCH_THUMB_TEXT_PADDING = "thumbTextPadding"
        const val SWITCH_SWITCH_PADDING = "switchPadding"
        const val SWITCH_TEXT_APPEARANCE = "textAppearance"
    }

    object TabLayout {
        const val TAB_INDICATOR_GRAVITY = "tabIndicatorGravity"
        const val TAB_ITEM_PADDING = "tabPadding"
        const val TAB_ITEM_BACKGROUND = "tabBackground"
        const val TAB_MODE = "tabMode"
    }

    object MediaController {
        const val MEDIA_PREVIOUS = "showPrevious"
        const val MEDIA_NEXT = "showNext"
        const val MEDIA_TITLE = "showTitle"
    }

    object MaterialButton {
        const val MATERIAL_BUTTON_BACKGROUND_TINT = "backgroundTint"
        const val MATERIAL_BUTTON_CORNER_RADIUS = "cornerRadius"
        const val MATERIAL_BUTTON_ICON = "icon"
        const val MATERIAL_BUTTON_ICON_GRAVITY = "iconGravity"
        const val MATERIAL_BUTTON_ICON_PADDING = "iconPadding"
        const val MATERIAL_BUTTON_ICON_TINT = "iconTint"
        const val MATERIAL_BUTTON_ICON_SIZE = "iconSize"
        const val MATERIAL_BUTTON_STROKE_COLOR = "strokeColor"
        const val MATERIAL_BUTTON_STROKE_WIDTH = "strokeWidth"
        const val MATERIAL_BUTTON_RIPPLE_COLOR = "rippleColor"
        const val MATERIAL_BUTTON_SHAPE_APPEARANCE_OVERRIDE = "shapeAppearanceOverlay"
        const val MATERIAL_BUTTON_TEXT_APPEARANCE = "textAppearance"
    }

    object MaterialTextView {
        const val MATERIAL_TEXTVIEW_HINT_TEXT_APPEARANCE = "hintTextAppearance"
        const val MATERIAL_TEXTVIEW_HINT_TEXT_COLOR = "hintTextColor"
        const val MATERIAL_TEXTVIEW_ERROR_TEXT_COLOR = "errorTextColor"
        const val MATERIAL_TEXTVIEW_TEXT_APPEARANCE = "textAppearance"
    }

    object TextInputLayout {
        const val TEXT_INPUT_LAYOUT_HINT_ENABLED = "hintEnabled"
        const val TEXT_INPUT_LAYOUT_HINT_ANIMATED = "hintAnimationEnabled"
        const val TEXT_INPUT_LAYOUT_HINT_TEXT_APPEARANCE2 = "hintTextAppearance"
        const val TEXT_INPUT_LAYOUT_ERROR_ENABLED = "errorEnabled"
        const val TEXT_INPUT_LAYOUT_END_ICON_MODE = "endIconMode"
        const val TEXT_INPUT_LAYOUT_START_ICON_DRAWABLE = "startIconDrawable"
        const val TEXT_INPUT_LAYOUT_END_ICON_DRAWABLE = "endIconDrawable"
        const val TEXT_INPUT_LAYOUT_BOX_BACKGROUND_MODE = "boxBackgroundMode"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_START = "boxCornerRadiusTopStart"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_END = "boxCornerRadiusTopEnd"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_START = "boxCornerRadiusBottomStart"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_END = "boxCornerRadiusBottomEnd"
        const val TEXT_INPUT_LAYOUT_PREFIX_TEXT = "prefixText"
        const val TEXT_INPUT_LAYOUT_SUFFIX_TEXT = "suffixText"
        const val TEXT_INPUT_LAYOUT_COUNTER_ENABLED = "counterEnabled"
        const val TEXT_INPUT_LAYOUT_TEXT_APPEARANCE = "textAppearance"
    }

    object MaterialNavigationBarView {
        const val NAVIGATION_BAR_VIEW_MENU = "menu"
        const val NAVIGATION_BAR_VIEW_ITEM_ICON_TINT = "itemIconTint"
        const val NAVIGATION_BAR_VIEW_ITEM_TEXT_COLOR = "itemTextColor"
        const val NAVIGATION_BAR_VIEW_LABEL_VISIBILITY_MODE = "labelVisibilityMode"
        const val NAVIGATION_BAR_VIEW_ITEM_PADDING = "itemPadding"
        const val ITEM_HORIZONTAL_TRANSLATION_ENABLED = "itemHorizontalTranslationEnabled"
        const val ITEM_ICON_SIZE = "itemIconSize"
    }

    object MaterialBottomNavigationView {
        const val BOTTOM_NAVIGATION_VIEW_ITEM_SHRINK = "itemHorizontalTranslationEnabled"
        const val BOTTOM_NAVIGATION_VIEW_ITEM_SHAPE_FILL_COLOR = "itemShapeFillColor"
        const val ITEM_SHAPE_APPEARANCE = "itemShapeAppearance"
    }

    object ExtendedFloatingActionButton {
        const val EXTENDED_FLOATING_ACTION_BUTTON_SHOW_MOTION_SPEC = "showMotionSpec"
        const val EXTENDED_FLOATING_ACTION_BUTTON_HIDE_MOTION_SPEC = "hideMotionSpec"
        const val FAB_SIZE = "fabSize"
    }

    object Chip {
        const val CHIP_CHIP_BACKGROUND_COLOR = "chipBackgroundColor"
        const val CHIP_CLOSE_ICON_ENABLED = "closeIconEnabled"
        const val CHIP_CHIP_ICON_SIZE = "chipIconSize"
        const val CHIP_CLOSE_ICON = "closeIcon"
        const val CHIP_TEXT_APPEARANCE = "textAppearance"
    }

    object ChipGroup {
        const val CHIP_GROUP_SINGLE_LINE = "singleLine"
        const val CHIP_GROUP_SINGLE_SELECTION = "singleSelection"
    }

    object Calender {
        const val YEAR_VIEW = "yearView"
        const val MONTH_VIEW = "monthView"
        const val DATE_VIEW = "dayView"
        const val SHOW_WEEK = "showWeekNumber"
    }

    object BottomSheet {
        const val BEHAVIOR_PEEK_HEIGHT = "behavior_peekHeight"
        const val BEHAVIOR_HIDE_ABLE = "behavior_hideable"
    }

    object ScrollBar {
        const val VIEW_VERTICAL_THUMB_DRAWABLE = "verticalScrollbarThumbDrawable"
        const val VERTICAL_TRACK = "verticalScrollbarTrackDrawable"

        @RequiresApi(Build.VERSION_CODES.M)
        const val SCROLL_DEFAULT = "scrollIndicators"
        const val SMOOTH_SCROLL = "smoothScrollbar"
    }
}