package com.runtimexml.utils

/**
 * Kotlin object containing constants for Android View attributes, consolidated from various classes.
 *
 * This object provides a single, organized place for all attribute constants. It uses nested objects
 * (View, WebView, RatingBar, etc.) to group related attributes.
 *
 * Using `const val` ensures these values are compile-time constants, improving performance and readability.
 *  All attributes are named with a `VIEW_NAME_ATTRIBUTE` format for clarity.
 */
object Attributes {

    /**
     * Contains constants for attributes shared across multiple view types.
     */
    object Common {
        const val ID = "id" // Unique identifier for the view
        const val VISIBILITY = "visibility" //  "visible", "invisible", or "gone"
        const val BACKGROUND = "background" //  Color, drawable, or reference to a drawable
        const val BACKGROUND_TINT = "backgroundTint" //  Color to tint the background
        const val BACKGROUND_TINT_MODE =
            "backgroundTintMode" //  PorterDuff mode for background tint (src_over, src_in, etc.)
        const val CLICKABLE = "clickable" //  Whether the view is clickable (true or false)
        const val LONG_CLICKABLE =
            "longClickable" //  Whether the view is long-clickable (true or false)
        const val ENABLED = "enabled" //  Whether the view is enabled (true or false)
        const val CONTENT_DESCRIPTION =
            "contentDescription" //  Textual description for accessibility
        const val TAG = "tag" //  Arbitrary data associated with the view
        const val PADDING = "padding" //  Padding on all sides
        const val PADDING_LEFT = "paddingLeft" //  Left padding
        const val PADDING_TOP = "paddingTop" //  Top padding
        const val PADDING_RIGHT = "paddingRight" //  Right padding
        const val PADDING_BOTTOM = "paddingBottom" //  Bottom padding
        const val PADDING_START =
            "paddingStart" //  Padding on the starting edge (left in LTR, right in RTL)
        const val PADDING_END =
            "paddingEnd" //  Padding on the ending edge (right in LTR, left in RTL)
        const val FOREGROUND = "foreground"//Sets the foreground drawable of the view
        const val FOREGROUND_GRAVITY =
            "foregroundGravity"//Sets the gravity of the foreground drawable
        const val FOREGROUND_TINT = "foregroundTint"//Applies a tint to the foreground of the view
        const val FOREGROUND_TINT_MODE =
            "foregroundTintMode"//Sets the blending mode for the foreground tint
        const val ROTATION = "rotation"//Sets the rotation of the view in degrees
        const val ROTATION_X = "rotationX"//Sets the rotation of the view around the X axis
        const val ROTATION_Y = "rotationY"//Sets the rotation of the view around the Y axis
        const val SCALE_X = "scaleX"//Sets the scale factor on the X axis
        const val SCALE_Y = "scaleY"//Sets the scale factor on the Y axis
        const val TRANSLATION_X = "translationX"//Sets the translation of the view on the X axis
        const val TRANSLATION_Y = "translationY"//Sets the translation of the view on the Y axis
        const val TRANSLATION_Z =
            "translationZ"//Sets the translation of the view on the Z axis (for elevation)
        const val ELEVATION = "elevation"//Sets the elevation of the view for shadow effects
        const val DRAWING_CACHE_QUALITY =
            "drawingCacheQuality"//Sets the quality of the drawing cache
        const val ALPHA = "alpha"//Sets the alpha transparency of the view
        const val WIDTH = "width" // Layout width (match_parent, wrap_content, or dimension)
        const val LAYOUT_WIDTH =
            "layout_width" // Layout width (match_parent, wrap_content, or dimension)
        const val HEIGHT = "height" // Layout height (match_parent, wrap_content, or dimension)
        const val LAYOUT_HEIGHT =
            "layout_height" // Layout height (match_parent, wrap_content, or dimension)
        const val LAYOUT_MARGIN = "layout_margin" // Margin on all sides
        const val LAYOUT_MARGIN_LEFT = "layout_marginLeft" // Left margin
        const val LAYOUT_MARGIN_TOP = "layout_marginTop" // Top margin
        const val LAYOUT_MARGIN_RIGHT = "layout_marginRight" // Right margin
        const val LAYOUT_MARGIN_BOTTOM = "layout_marginBottom" // Bottom margin
        const val LAYOUT_MARGIN_START = "layout_marginStart"//Sets the start margin of the view
        const val LAYOUT_MARGIN_END = "layout_marginEnd"//Sets the end margin of the view
        const val LAYOUT_GRAVITY = "layout_gravity" // Gravity within the parent layout
        const val GRAVITY = "gravity" // Sets the gravity of the content within the view
        const val WEIGHT =
            "layout_weight" // Assigns a weight to the view for layout within a LinearLayout
        const val STYLE = "style"//Sets the style resource for the view
        const val ENABLED_STATE = "android:enabled"// enable stat
        const val FOCUSED_STATE = "android:state_focused" //Set Focus mode
        const val MIN_WIDTH = "minWidth" // Sets the minimum width of the view
        const val MIN_HEIGHT = "minHeight" // Sets the minimum height of the view
        const val MAX_WIDTH = "maxWidth" // Sets the maximum width of the view
        const val MAX_HEIGHT = "maxHeight" // Sets the maximum height of the view
        const val TEXT = "text"
    }

    /**
     * Contains constants for Android View attributes.
     *
     * Mirrors the Java `Attributes.View` class.
     */
    object View {
        // Layout attributes related to positioning relative to other views
        const val VIEW_ABOVE =
            "layout_above" // Positions the view above another view with a given ID
        const val VIEW_ACTIVATED = "activated" // Sets the activated state of the view
        const val VIEW_ALIGN_BASELINE =
            "layout_alignBaseline" // Aligns the baseline of this view to the baseline of another view
        const val VIEW_ALIGN_BOTTOM =
            "layout_alignBottom" // Aligns the bottom edge of this view to the bottom edge of another view
        const val VIEW_ALIGN_END =
            "layout_alignEnd" // Aligns the end edge of this view to the end edge of another view
        const val VIEW_ALIGN_LEFT =
            "layout_alignLeft" // Aligns the left edge of this view to the left edge of another view
        const val VIEW_ALIGN_PARENT_BOTTOM =
            "layout_alignParentBottom" // Aligns the bottom edge of this view to the bottom of its parent
        const val VIEW_ALIGN_PARENT_END =
            "layout_alignParentEnd" // Aligns the end edge of this view to the end of its parent
        const val VIEW_ALIGN_PARENT_LEFT =
            "layout_alignParentLeft" // Aligns the left edge of this view to the left of its parent
        const val VIEW_ALIGN_PARENT_RIGHT =
            "layout_alignParentRight" // Aligns the right edge of this view to the right of its parent
        const val VIEW_ALIGN_PARENT_START =
            "layout_alignParentStart" // Aligns the start edge of this view to the start of its parent
        const val VIEW_ALIGN_PARENT_TOP =
            "layout_alignParentTop" // Aligns the top edge of this view to the top of its parent
        const val VIEW_ALIGN_RIGHT =
            "layout_alignRight" // Aligns the right edge of this view to the right edge of another view
        const val VIEW_ALIGN_START =
            "layout_alignStart" // Aligns the start edge of this view to the start edge of another view
        const val VIEW_ALIGN_TOP =
            "layout_alignTop" // Aligns the top edge of this view to the top edge of another view

        const val VIEW_ANIMATION = "animation" // Specifies an animation to run for the view
        const val VIEW_BELOW =
            "layout_below" // Positions the view below another view with a given ID

        // Layout attributes for centering within parent
        const val VIEW_CENTER_HORIZONTAL =
            "layout_centerHorizontal" // Centers the view horizontally within its parent
        const val VIEW_CENTER_IN_PARENT =
            "layout_centerInParent" // Centers the view both horizontally and vertically within its parent
        const val VIEW_CENTER_VERTICAL =
            "layout_centerVertical" // Centers the view vertically within its parent

        // Interaction attributes
        const val VIEW_CONTEXT_CLICKABLE =
            "contextClickable" // Makes the view context-clickable (long press)

        // Visual appearance and behavior attributes
        const val VIEW_FADE_SCROLLBARS =
            "fadeScrollbars" // Makes scrollbars fade out when not in use
        const val VIEW_FADING_EDGE_LENGTH =
            "fadingEdgeLength" // Sets the length of the fading edge effect
        const val VIEW_FILTER_TOUCHES_WHEN_OBSCURED =
            "filterTouchesWhenObscured" // Filters touches when the view is obscured by another view
        const val VIEW_FITS_SYSTEM_WINDOWS =
            "fitsSystemWindows" // Indicates if the view should fit system window insets

        // Focus related attributes
        const val VIEW_FOCUSABLE = "focusable" // Makes the view focusable
        const val VIEW_FOCUSABLE_IN_TOUCH_MODE =
            "focusableInTouchMode" // Makes the view focusable in touch mode
        const val VIEW_FORCE_HAS_OVERLAPPING_RENDERING =
            "forceHasOverlappingRendering" // Forces the view to be treated as having overlapping rendering

        const val VIEW_HAPTIC_FEEDBACK_ENABLED =
            "hapticFeedbackEnabled" // Enables or disables haptic feedback for the view
        const val VIEW_IS_SCROLL_CONTAINER =
            "isScrollContainer" // Indicates if the view is a scroll container
        const val VIEW_KEEP_SCREEN_ON =
            "keepScreenOn" // Keeps the screen on while the view is visible
        const val VIEW_LAYER_TYPE =
            "layerType" // Sets the layer type for hardware or software rendering
        const val VIEW_LAYOUT_DIRECTION =
            "layoutDirection" // Sets the layout direction (LTR or RTL)
        const val VIEW_LONG_CLICKABLE = "longClickable" // Makes the view long-clickable

        // Focus navigation attributes
        const val VIEW_NEXT_FOCUS_DOWN =
            "nextFocusDown" // Specifies the view to receive focus when navigating down
        const val VIEW_NEXT_FOCUS_FORWARD =
            "nextFocusForward" // Specifies the view to receive focus when navigating forward
        const val VIEW_NEXT_FOCUS_LEFT =
            "nextFocusLeft" // Specifies the view to receive focus when navigating left
        const val VIEW_NEXT_FOCUS_RIGHT =
            "nextFocusRight" // Specifies the view to receive focus when navigating right
        const val VIEW_NEXT_FOCUS_UP =
            "nextFocusUp" // Specifies the view to receive focus when navigating up

        // Event handler attributes (usually set programmatically, but can be in layout for simple cases)
        const val VIEW_ON_CLICK = "onClick" // Specifies the method to call when the view is clicked
        const val VIEW_ON_LONG_CLICK =
            "onLongClick" // Specifies the method to call when the view is long-clicked
        const val VIEW_ON_TOUCH = "onTouch" // Specifies the method to call when the view is touched

        // Scrolling related attributes
        const val VIEW_REQUIRES_FADING_EDGE =
            "requiresFadingEdge" // Indicates if the view should have fading edges

        const val VIEW_SAVE_ENABLED =
            "saveEnabled" // Indicates if the view's state should be saved during configuration changes
        const val VIEW_SCROLL_INDICATORS =
            "scrollIndicators" // Sets which scroll indicators to display
        const val VIEW_SCROLLBAR_DEFAULT_DELAY_BEFORE_FADE =
            "scrollbarDefaultDelayBeforeFade" // Sets the delay before scrollbars start to fade
        const val VIEW_SCROLLBAR_FADE_DURATION =
            "scrollbarFadeDuration" // Sets the duration of the scrollbar fade animation
        const val VIEW_SCROLLBAR_SIZE = "scrollbarSize" // Sets the thickness of the scrollbars
        const val VIEW_SCROLLBAR_STYLE = "scrollbarStyle" // Sets the style of the scrollbars
        const val VIEW_SELECTED = "selected" // Sets the selected state of the view
        const val VIEW_SOUND_EFFECTS_ENABLED =
            "soundEffectsEnabled" // Enables or disables sound effects for the view
        const val VIEW_TEXT_ALIGNMENT = "textAlignment" // Sets the text alignment
        const val VIEW_TEXT_DIRECTION = "textDirection" // Sets the text direction

        // Layout attributes related to positioning relative to other views (End/Start variants)
        const val VIEW_TO_END_OF =
            "layout_toEndOf" // Positions the right edge of the view to the end edge of another view
        const val VIEW_TO_LEFT_OF =
            "layout_toLeftOf" // Positions the right edge of the view to the left edge of another view
        const val VIEW_TO_RIGHT_OF =
            "layout_toRightOf" // Positions the left edge of the view to the right edge of another view
        const val VIEW_TO_START_OF =
            "layout_toStartOf" // Positions the left edge of the view to the start edge of another view

        // Transformation pivot attributes
        const val VIEW_TRANSFORM_PIVOT_X =
            "transformPivotX" // Sets the pivot point for rotation and scaling on the X axis
        const val VIEW_TRANSFORM_PIVOT_Y =
            "transformPivotY" // Sets the pivot point for rotation and scaling on the Y axis
        const val VIEW_TRANSITION_NAME =
            "transitionName" // Sets a transition name for shared element transitions
    }

    /**
     * Contains constants for WebView attributes.
     *
     * Mirrors the Java `Attributes.WebView` class.
     */
    object WebView {
        const val WEB_VIEW_URL = "url" // Loads a URL in the WebView
        const val WEB_VIEW_HTML = "html" // Loads HTML content in the WebView
        const val WEB_VIEW_JAVASCRIPT_ENABLED = "javaScriptEnabled" // Whether JavaScript is enabled
        const val WEB_VIEW_DOM_STORAGE_ENABLED =
            "domStorageEnabled" // Whether DOM storage is enabled
        const val WEB_VIEW_SCROLLBARS = "scrollbars"// Sets scrollbar visibility and style
        const val WEB_VIEW_OVER_SCROLL_MODE = "overScrollMode"//Sets over scroll mode

        // WebSettings
        const val WEB_VIEW_USER_AGENT = "userAgentString"// set custom UserAgent.
        const val WEB_VIEW_LOAD_WITH_OVERVIEW_MODE =
            "loadWithOverviewMode" //loads web page in overview mode.
        const val WEB_VIEW_USE_WIDE_VIEWPORT = "useWideViewPort"//set  wide viewport.
        const val WEB_VIEW_DISPLAY_ZOOM_CONTROLS = "displayZoomControls"//Zoom option
        const val WEB_VIEW_ALLOW_FILE_ACCESS = "allowFileAccessFromFileURLs" //allow Access File
        const val WEB_VIEW_TEXT_ZOOM = "textZoom" //change the zoom text
        const val WEB_VIEW_GEOLOCATION_ENABLED =
            "geolocationEnabled" //Permission Geolocation web sites and web content that accesses
    }

    /**
     * Contains constants for RatingBar attributes.
     *
     * Mirrors the Java `Attributes.RatingBar` class.
     */
    object RatingBar {
        const val RATINGBAR_NUM_STARS = "numStars" // Sets the number of stars in the rating bar
        const val RATINGBAR_RATING = "rating" // Sets the current rating value
        const val RATINGBAR_IS_INDICATOR =
            "isIndicator" // Makes the rating bar an indicator (non-interactive)
        const val RATINGBAR_STEP_SIZE = "stepSize" // Sets the step size for rating changes
        const val RATINGBAR_PROGRESS_DRAWABLE =
            "progressDrawable" // Sets a custom progress drawable
    }

    /**
     * Contains constants for TextView attributes.
     *
     * Mirrors the Java `Attributes.TextView` class.
     */
    object TextView {
        const val TEXTVIEW_HTML = "html" // Sets HTML formatted text content
        const val TEXTVIEW_TEXT_SIZE = "textSize" // Sets the text size
        const val TEXTVIEW_TEXT_COLOR = "textColor" // Sets the text color
        const val TEXTVIEW_TEXT_COLOR_HINT = "textColorHint" // Sets the hint text color
        const val TEXTVIEW_TEXT_COLOR_LINK = "textColorLink" // Sets the link text color
        const val TEXTVIEW_TEXT_COLOR_HIGHLIGHT =
            "textColorHighlight" // Sets the highlight text color
        const val TEXTVIEW_DRAWABLE_LEFT = "drawableLeft" // Sets a drawable to the left of the text
        const val TEXTVIEW_DRAWABLE_RIGHT =
            "drawableRight" // Sets a drawable to the right of the text
        const val TEXTVIEW_DRAWABLE_TOP = "drawableTop" // Sets a drawable above the text
        const val TEXTVIEW_DRAWABLE_BOTTOM = "drawableBottom" // Sets a drawable below the text
        const val TEXTVIEW_DRAWABLE_PADDING =
            "drawablePadding" // Sets padding between drawables and text
        const val TEXTVIEW_MAX_LINES = "maxLines" // Sets the maximum number of lines
        const val TEXTVIEW_ELLIPSIZE =
            "ellipsize" // Sets the type of ellipsis to use for text overflow
        const val TEXTVIEW_PAINT_FLAGS =
            "paintFlags" // Sets paint flags for styling (e.g., bold, italic)
        const val TEXTVIEW_PREFIX = "prefix" // Sets a prefix text
        const val TEXTVIEW_SUFFIX = "suffix" // Sets a suffix text
        const val TEXTVIEW_TEXT_STYLE = "textStyle" // Sets the text style (normal, bold, italic)
        const val TEXTVIEW_SINGLE_LINE = "singleLine" // Restricts text to a single line
        const val TEXTVIEW_TEXT_ALL_CAPS = "textAllCaps" // Converts text to all caps
        const val TEXTVIEW_HINT = "hint" // Sets the hint text
        const val TEXTVIEW_INPUT_TYPE =
            "inputType" // Sets the input type (e.g., text, number, email)
        const val TEXTVIEW_IME_OPTIONS =
            "imeOptions" // Sets the IME options (e.g., actionDone, actionNext)
        const val TEXTVIEW_AUTO_LINK =
            "autoLink"//Sets whether links are automatically found and highlighted
        const val TEXTVIEW_BREAK_STRATEGY =
            "breakStrategy"//Sets the breaking strategy for breaking lines of text
        const val TEXTVIEW_CURSOR_VISIBLE = "cursorVisible"//Sets whether the cursor is visible
        const val TEXTVIEW_EDITABLE = "editable"//Sets whether the text is editable
        const val TEXTVIEW_EMS = "ems"//Sets the width of the TextView to n 'M' characters
        const val TEXTVIEW_FONT_FAMILY = "fontFamily"//Sets the font family
        const val TEXTVIEW_HYPHENATION_FREQUENCY =
            "hyphenationFrequency"//Sets the hyphenation frequency
        const val TEXTVIEW_LETTER_SPACING = "letterSpacing"//Sets the extra character spacing
        const val TEXTVIEW_LINE_SPACING_EXTRA = "lineSpacingExtra"//Sets the extra line spacing
        const val TEXTVIEW_LINE_SPACING_MULTIPLIER =
            "lineSpacingMultiplier"//Sets the line spacing multiplier
        const val TEXTVIEW_MAX_EMS =
            "maxEms"//Sets the maximum width of the TextView to n 'M' characters
        const val TEXTVIEW_MIN_EMS =
            "minEms"//Sets the minimum width of the TextView to n 'M' characters
        const val TEXTVIEW_PRIVATE_IME_OPTIONS = "privateImeOptions"//Sets the private IME options
        const val TEXTVIEW_SELECT_ALL_ON_FOCUS =
            "selectAllOnFocus"//Sets whether to select all text when focused
        const val TEXTVIEW_SHADOW_COLOR = "shadowColor"//Sets the color of the shadow
        const val TEXTVIEW_SHADOW_DX = "shadowDx"//Sets the horizontal offset of the shadow
        const val TEXTVIEW_SHADOW_DY = "shadowDy"//Sets the vertical offset of the shadow
        const val TEXTVIEW_SHADOW_RADIUS = "shadowRadius"//Sets the radius of the shadow
        const val TEXTVIEW_TEXT_SCALE_X =
            "textScaleX"//Sets the horizontal scaling factor for the text
        const val TEXTVIEW_TRANSFORMATION_METHOD =
            "transformationMethod"//Sets the transformation method
        const val TEXTVIEW_MARQUEE_REPEAT_LIMIT = "marqueeRepeatLimit"//
    }

    /**
     * Contains constants for EditText attributes.
     */
    object EditText {
        const val EDITTEXT_HINT = "hint" // Sets the hint text
        const val EDITTEXT_INPUT_TYPE =
            "inputType" // Sets the input type (e.g., text, number, email)
        const val EDITTEXT_MAX_LINES = "maxLines" // Sets the maximum number of lines
        const val EDITTEXT_IME_OPTIONS =
            "imeOptions" // Sets the IME options (e.g., actionDone, actionNext)
        const val EDITTEXT_TEXT_SIZE = "textSize"
        const val EDITTEXT_TEXT_COLOR = "textColor"
        const val EDITTEXT_TEXT_COLOR_HINT = "textColorHint"
        const val EDITTEXT_GRAVITY = "gravity"
        const val EDITTEXT_PASSWORD = "password"//Sets whether the text is masked as a password
        const val EDITTEXT_SINGLE_LINE = "singleLine"//Sets whether the text is a single line
        const val EDITTEXT_SELECT_ALL_ON_FOCUS =
            "selectAllOnFocus"//Sets whether to select all text when focused
        const val EDITTEXT_EDITABLE = "editable"//Sets whether the text is editable
        const val EDITTEXT_AUTO_TEXT = "autoText"//Sets whether auto text is enabled
        const val EDITTEXT_BUFFER_TYPE = "bufferType"//Sets the buffer type
        const val EDITTEXT_COMPLETION_HINT = "completionHint"//Sets the completion hint
        const val EDITTEXT_COMPLETION_MODE = "completionMode"//Sets the completion mode
        const val EDITTEXT_DIGITS = "digits"//Sets the allowed digits
        const val EDITTEXT_ERROR = "error"//Sets the error message
        const val EDITTEXT_IME_ACTION_LABEL = "imeActionLabel"//Sets the IME action label
        const val EDITTEXT_IME_ACTION_ID = "imeActionId"//Sets the IME action ID
        const val EDITTEXT_LINE_SPACING_EXTRA = "lineSpacingExtra"//Sets the extra line spacing
        const val EDITTEXT_LINE_SPACING_MULTIPLIER =
            "lineSpacingMultiplier"//Sets the line spacing multiplier
        const val EDITTEXT_MAX_EMS = "maxEms"//Sets the maximum number of EMS
        const val EDITTEXT_MAX_LENGTH = "maxLength"//Sets the maximum length
        const val EDITTEXT_MIN_EMS = "minEms"//Sets the minimum number of EMS
        const val EDITTEXT_PRIVATE_IME_OPTIONS = "privateImeOptions"//Sets the private IME options
        const val EDITTEXT_SCROLL_HORIZONTALLY =
            "scrollHorizontally"//Sets whether to scroll horizontally
        const val EDITTEXT_SHADOW_COLOR = "shadowColor"//Sets the shadow color
        const val EDITTEXT_SHADOW_DX = "shadowDx"//Sets the shadow DX
        const val EDITTEXT_SHADOW_DY = "shadowDy"//Sets the shadow DY
        const val EDITTEXT_SHADOW_RADIUS = "shadowRadius"//Sets the shadow radius
        const val EDITTEXT_TEXT_SCALE_X = "textScaleX"//Sets the text scale X
        const val EDITTEXT_TRANSFORMATION_METHOD =
            "transformationMethod"//Sets the transformation method
    }

    /**
     * Contains constants for Button attributes.
     */
    object Button {
        const val BUTTON_TEXT_SIZE = "textSize" // Sets the text size
        const val BUTTON_TEXT_COLOR = "textColor" // Sets the text color
        const val BUTTON_BACKGROUND = "background" // Sets the background color or drawable
        const val BUTTON_ENABLED = "enabled" // Sets whether the button is enabled
        const val BUTTON_PADDING = "padding" // Sets the padding
        const val BUTTON_GRAVITY = "gravity" // Sets the gravity of the text
        const val BUTTON_TEXT_STYLE = "textStyle"//Sets the text style (normal, bold, italic)
        const val BUTTON_TEXT_ALL_CAPS = "textAllCaps"//Sets whether the text is all caps
        const val BUTTON_ON_CLICK = "onClick"//Sets the onClick event
        const val BUTTON_ELLIPSIZE = "ellipsize"//Sets the ellipsize
        const val BUTTON_FONT_FAMILY = "fontFamily"//Sets the font family
        const val BUTTON_LETTER_SPACING = "letterSpacing"//Sets the letter spacing
        const val BUTTON_LINE_SPACING_EXTRA = "lineSpacingExtra"//Sets the extra line spacing
        const val BUTTON_LINE_SPACING_MULTIPLIER =
            "lineSpacingMultiplier"//Sets the line spacing multiplier
        const val BUTTON_SHADOW_COLOR = "shadowColor"//Sets the shadow color
        const val BUTTON_SHADOW_DX = "shadowDx"//Sets the shadow DX
        const val BUTTON_SHADOW_DY = "shadowDy"//Sets the shadow DY
        const val BUTTON_SHADOW_RADIUS = "shadowRadius"//Sets the shadow radius
        const val BUTTON_TEXT_SCALE_X = "textScaleX"//Sets the text scale X
        const val BUTTON_TRANSFORMATION_METHOD =
            "transformationMethod"//Sets the transformation method
    }

    /**
     * Contains constants for CheckBox attributes.
     *
     * Mirrors the Java `Attributes.CheckBox` class.
     */
    object CheckBox {
        const val CHECKBOX_CHECKED = "checked" // Sets the checked state
        const val CHECKBOX_BUTTON = "button" // Sets a custom button drawable
        const val CHECKBOX_TEXT_SIZE = "textSize"//Sets the text size
        const val CHECKBOX_TEXT_COLOR = "textColor"//Sets the text color
        const val CHECKBOX_DRAWABLE_START = "drawableStart" // Sets the Drawable stat
        const val CHECKBOX_DRAWABLE_END = "drawableEnd" // Sets the Drawable End
        const val CHECKBOX_DRAWABLE_TOP = "drawableTop" //set Top drawable for TextView

    }

    /**
     * Contains constants for FrameLayout attributes.
     *
     * Mirrors the Java `Attributes.FrameLayout` class.
     */
    object FrameLayout {
        const val FRAME_LAYOUT_HEIGHT_RATIO = "heightRatio" // Sets a height ratio for aspect ratio
        const val FRAME_LAYOUT_WIDTH_RATIO = "widthRatio" // Sets a width ratio for aspect ratio
        const val FRAME_LAYOUT_MEASURE_ALL_CHILDREN =
            "measureAllChildren"//Sets whether all children are measured

    }

    /**
     * Contains constants for ImageView attributes.
     *
     * Mirrors the Java `Attributes.ImageView` class.
     */
    object ImageView {
        const val IMAGEVIEW_SRC = "src" // Sets the image source
        const val IMAGEVIEW_SCALE_TYPE = "scaleType" // Sets how the image should be scaled
        const val IMAGEVIEW_ADJUST_VIEW_BOUNDS =
            "adjustViewBounds" // Adjusts view bounds to maintain aspect ratio
        const val IMAGEVIEW_CROP_TO_PADDING = "cropToPadding"//Sets whether to crop to padding
        const val IMAGEVIEW_MAX_HEIGHT = "maxHeight"//Sets the maximum height
        const val IMAGEVIEW_MAX_WIDTH = "maxWidth"//Sets the maximum width
    }

    /**
     * Contains constants for ViewGroup attributes.
     *
     * Mirrors the Java `Attributes.ViewGroup` class.
     */
    object ViewGroup {
        const val VIEW_GROUP_CHILDREN =
            "children" // Represents child views (not an XML attribute, likely for programmatic use)
        const val VIEW_GROUP_CLIP_CHILDREN =
            "clipChildren" // Clips drawing of children to the bounds of the ViewGroup
        const val VIEW_GROUP_CLIP_TO_PADDING =
            "clipToPadding" // Clips drawing of children to the padding of the ViewGroup
        const val VIEW_GROUP_LAYOUT_MODE = "layoutMode" // Sets the layout mode (e.g., clipBounds)
        const val VIEW_GROUP_SPLIT_MOTION_EVENTS =
            "splitMotionEvents" // Enables splitting of motion events to child views
        const val VIEW_GROUP_ADD_STATES_FROM_CHILDREN =
            "addStatesFromChildren"//Sets whether to add states from children
        const val VIEW_GROUP_ALWAYS_DRAWN_HORIZONTAL_FADING_EDGE =
            "alwaysDrawnHorizontalFadingEdge"//Sets whether the horizontal fading edge is always drawn
        const val VIEW_GROUP_ALWAYS_DRAWN_VERTICAL_FADING_EDGE =
            "alwaysDrawnVerticalFadingEdge"//Sets whether the vertical fading edge is always drawn
        const val VIEW_GROUP_ANIMATION_CACHE_ENABLED =
            "animationCacheEnabled"//Sets whether the animation cache is enabled
        const val VIEW_GROUP_LAYOUT_ANIMATION = "layoutAnimation"//Sets the layout animation
        const val VIEW_GROUP_PERSISTENT_DRAWING_CACHE =
            "persistentDrawingCache"//Sets the persistent drawing cache
        const val VIEW_GROUP_TOUCHSCREEN_BLOCKS_FOCUS =
            "touchscreenBlocksFocus"//Sets whether the touchscreen blocks focus
        const val VIEW_GROUP_TRANSITION_GROUP =
            "transitionGroup"//Sets whether this view group is a transition group
    }

    /**
     * Contains constants for LinearLayout attributes.
     *
     * Mirrors the Java `Attributes.LinearLayout` class.
     */
    object LinearLayout {
        const val LINEARLAYOUT_ORIENTATION =
            "orientation" // Sets the orientation (horizontal or vertical)
        const val LINEARLAYOUT_DIVIDER = "divider" // Sets a divider drawable between children
        const val LINEARLAYOUT_DIVIDER_PADDING = "dividerPadding" // Sets padding for dividers
        const val LINEARLAYOUT_SHOW_DIVIDERS = "showDividers" // Controls when dividers are shown
        const val LINEARLAYOUT_WEIGHT_SUM = "weightSum" // Defines the total weight sum for children
        const val LINEARLAYOUT_MEASURE_WITH_LARGEST_CHILD =
            "measureWithLargestChild"//Sets whether to measure with the largest child
        const val LINEARLAYOUT_BASELINE_ALIGNED =
            "baselineAligned"//Sets whether to baseline align children
        const val LINEARLAYOUT_BASELINE_ALIGNED_CHILD_INDEX =
            "baselineAlignedChildIndex"//Sets the index of the child to baseline align
    }

    /**
     * Contains constants for ScrollView attributes.
     *
     * Mirrors the Java `Attributes.ScrollView` class.
     */
    object ScrollView {
        const val SCROLLVIEW_SCROLLBARS = "scrollbars" // Sets scrollbar visibility and style
        const val SCROLLVIEW_FILL_VIEWPORT =
            "fillViewport" // Makes the scroll view stretch its content to fill the viewport
        const val SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED =
            "smoothScrollbar"//Sets whether smooth scrollbar is enabled

    }

    /**
     * Contains constants for HorizontalScrollView attributes.
     *
     * Mirrors the Java `Attributes.HorizontalScrollView` class.
     */
    object HorizontalScrollView {
        const val HORIZONTAL_SCROLLVIEW_FILL_VIEWPORT =
            "fillViewPort" // Makes the scroll view stretch its content to fill the viewport
        const val HORIZONTAL_SCROLLVIEW_SMOOTH_SCROLLBAR_ENABLED =
            "smoothScrollbar"//Sets whether smooth scrollbar is enabled

    }

    /**
     * Contains constants for ProgressBar attributes.
     *
     * Mirrors the Java `Attributes.ProgressBar` class.
     */
    object ProgressBar {
        const val PROGRESSBAR_PROGRESS = "progress" // Sets the current progress value
        const val PROGRESSBAR_MAX = "max" // Sets the maximum progress value
        const val PROGRESSBAR_PROGRESS_TINT = "progressTint" // Sets the progress bar tint color
        const val PROGRESSBAR_INDETERMINATE_TINT =
            "indeterminateTint" // Sets the indeterminate progress bar tint color
        const val PROGRESSBAR_SECONDARY_PROGRESS_TINT =
            "secondaryProgressTint" // Sets the secondary progress bar tint color
        const val PROGRESSBAR_INDETERMINATE =
            "indeterminate"//Sets whether the progress bar is indeterminate
        const val PROGRESSBAR_INDETERMINATE_DURATION =
            "indeterminateDuration"//Sets the duration of the indeterminate animation
        const val PROGRESSBAR_INDETERMINATE_ONLY =
            "indeterminateOnly"//Sets whether the progress bar is indeterminate only
        const val PROGRESSBAR_INCREMENTAL_SECONDARY_PROGRESS =
            "incrementSecondaryProgress"//Sets the incremental secondary progress
        const val PROGRESSBAR_PROGRESS_BACKGROUND_TINT =
            "progressBackgroundTint"//Sets the progress background tint color
        const val PROGRESSBAR_PROGRESS_TINT_MODE = "progressTintMode"//Sets the progress tint mode
        const val PROGRESSBAR_ROTATION = "rotation"//Sets the rotation
        const val PROGRESSBAR_SCALE_X = "scaleX"//Sets the scale X
        const val PROGRESSBAR_SCALE_Y = "scaleY"//Sets the scale Y
        const val PROGRESSBAR_SECONDARY_PROGRESS = "secondaryProgress"//Sets the secondary progress
        const val PROGRESSBAR_SPLIT_TRACK = "splitTrack"//Sets whether to split the track
    }

    /**
     * Contains constants for RecyclerView attributes.
     */
    object RecyclerView {
        const val RECYCLERVIEW_LAYOUT_MANAGER =
            "layoutManager" // Sets the layout manager (e.g., LinearLayoutManager, GridLayoutManager)
        const val RECYCLERVIEW_ITEM_DECORATION = "itemDecoration" // Adds an item decoration
        const val RECYCLERVIEW_SCROLLBARS = "scrollbars" // Sets scrollbar visibility and style
        const val RECYCLERVIEW_CLIP_TO_PADDING = "clipToPadding"//Sets whether to clip to padding
        const val RECYCLERVIEW_HAS_FIXED_SIZE = "hasFixed"
        const val RECYCLERVIEW_OVER_SCROLL_MODE = "overScrollMode"//Sets the over scroll mode
    }

    /**
     * Contains constants for ListView attributes.
     */
    object ListView {
        const val LISTVIEW_DIVIDER = "divider" // Sets the divider drawable
        const val LISTVIEW_DIVIDER_HEIGHT = "dividerHeight" // Sets the divider height
        const val LISTVIEW_ENTRIES = "entries" // Sets the entries from a resource array
        const val LISTVIEW_FOOTER_DIVIDERS_ENABLED =
            "footerDividersEnabled"//Sets whether footer dividers are enabled
        const val LISTVIEW_HEADER_DIVIDERS_ENABLED =
            "headerDividersEnabled"//Sets whether header dividers are enabled
        const val LISTVIEW_LIST_SELECTOR = "listSelector"//Sets the list selector
        const val LISTVIEW_SCROLLBARS = "scrollbars"//Sets the scrollbars
        const val LISTVIEW_SMOOTH_SCROLLBAR_ENABLED =
            "smoothScrollbar"//Sets whether smooth scrollbar is enabled
        const val LISTVIEW_TRANSCRIPT_MODE = "transcriptMode"//Sets the transcript mode
        const val LISTVIEW_STACK_FROM_BOTTOM = "stackFromBottom"
    }

    /**
     * Contains constants for GridView attributes.
     */
    object GridView {
        const val GRIDVIEW_NUM_COLUMNS = "numColumns" // Sets the number of columns
        const val GRIDVIEW_COLUMN_WIDTH = "columnWidth" // Sets the column width
        const val GRIDVIEW_GRAVITY = "gravity"//Sets the gravity
        const val GRIDVIEW_HORIZONTAL_SPACING = "horizontalSpacing"//Sets the horizontal spacing
        const val GRIDVIEW_STRETCH_MODE = "stretchMode"//Sets the stretch mode
        const val GRIDVIEW_VERTICAL_SPACING = "verticalSpacing"//Sets the vertical spacing
    }

    /**
     * Contains constants for GridLayout attributes.
     */
    object GridLayout {
        const val GRIDLAYOUT_COLUMN_COUNT = "columnCount" // Sets the number of columns
        const val GRIDLAYOUT_ROW_COUNT = "rowCount" // Sets the number of rows
        const val GRIDLAYOUT_ORIENTATION = "orientation"//Sets the orientation
        const val GRIDLAYOUT_USE_DEFAULT_MARGINS =
            "useDefaultMargins"//Sets whether to use default margins
        const val GRIDLAYOUT_ALIGNMENT_MODE = "alignmentMode"//Sets the alignment mode
        const val GRIDLAYOUT_COLUMN_ORDER_PRESERVED =
            "columnOrderPreserved"//Sets whether column order is preserved
        const val GRIDLAYOUT_ROW_ORDER_PRESERVED =
            "rowOrderPreserved"//Sets whether row order is preserved

        object LayoutParams {
            const val LAYOUT_COLUMN_SPAN = "layout_columnSpan"//Sets the column span
            const val LAYOUT_ROW_SPAN = "layout_rowSpan"//Sets the row span
            const val LAYOUT_COLUMN_WEIGHT = "layout_columnWeight"//Sets the column weight
            const val LAYOUT_ROW_WEIGHT = "layout_rowWeight"//Sets the row weight
            const val LAYOUT_GRAVITY = "layout_gravity"//Sets the gravity
            const val LAYOUT_COLUMN = "layout_column"//Sets the column
            const val LAYOUT_ROW = "layout_row"//Sets the row
        }
    }

    /**
     * Contains constants for FlexboxLayout attributes.
     *
     * Note: Requires the `com.google.android:flexbox` library.
     */
    object FlexboxLayout {
        const val FLEXBOX_LAYOUT_FLEX_DIRECTION =
            "flexDirection" // Sets the direction of the flex items
        const val FLEXBOX_LAYOUT_FLEX_WRAP = "flexWrap" // Sets whether to wrap the flex items
        const val FLEXBOX_LAYOUT_JUSTIFY_CONTENT =
            "justifyContent" // Sets how to align the flex items horizontally
        const val FLEXBOX_LAYOUT_ALIGN_ITEMS =
            "alignItems" // Sets how to align the flex items vertically
        const val FLEXBOX_LAYOUT_ALIGN_CONTENT = "alignContent" // Sets how to align the flex lines

        object LayoutParams {
            const val LAYOUT_FLEX_GROW = "layout_flexGrow" // Sets the flex grow factor
            const val LAYOUT_FLEX_SHRINK = "layout_flexShrink" // Sets the flex shrink factor
            const val LAYOUT_FLEX_BASIS_PERCENT =
                "layout_flexBasisPercent"//Sets the flex basis percent
            const val LAYOUT_ORDER = "layout_order"//Sets the order
            const val LAYOUT_ALIGN_SELF = "layout_alignSelf"//Sets the align self
            const val LAYOUT_MIN_WIDTH = "layout_minWidth"//Sets the minimum width
            const val LAYOUT_MIN_HEIGHT = "layout_minHeight"//Sets the minimum height
            const val LAYOUT_MAX_WIDTH = "layout_maxWidth"//Sets the maximum width
            const val LAYOUT_MAX_HEIGHT = "layout_maxHeight"//Sets the maximum height
            const val LAYOUT_MARGIN = "layout_margin"//Sets the margin
            const val LAYOUT_MARGIN_LEFT = "layout_marginLeft"//Sets the left margin
            const val LAYOUT_MARGIN_TOP = "layout_marginTop"//Sets the top margin
            const val LAYOUT_MARGIN_RIGHT = "layout_marginRight"//Sets the right margin
            const val LAYOUT_MARGIN_BOTTOM = "layout_marginBottom"//Sets the bottom margin
            const val LAYOUT_ASPECT_RATIO = "layout_aspectRatio"//Sets the aspect ratio
        }
    }

    /**
     * Contains constants for CardView attributes.
     * Requires the `androidx.cardview:cardview` library.
     */
    object CardView {
        const val CARD_VIEW_CARD_BACKGROUND_COLOR =
            "cardBackgroundColor" // Sets the background color of the card
        const val CARD_VIEW_CARD_CORNER_RADIUS = "cardCornerRadius" // Sets the corner radius
        const val CARD_VIEW_CARD_ELEVATION = "cardElevation" // Sets the base elevation of the card
        const val CARD_VIEW_CARD_MAX_ELEVATION = "cardMaxElevation" // Sets the maximum elevation
        const val CARD_VIEW_CARD_USE_COMPAT_PADDING =
            "cardUseCompatPadding" // Adds padding to compensate for shadows on older platforms
        const val CARD_VIEW_CARD_PREVENT_CORNER_OVERLAP =
            "cardPreventCornerOverlap"//Sets whether to prevent corner overlap
        const val CARD_VIEW_CONTENT_PADDING = "contentPadding"//Sets the content padding
        const val CARD_VIEW_CONTENT_PADDING_LEFT =
            "contentPaddingLeft"//Sets the content padding left
        const val CARD_VIEW_CONTENT_PADDING_TOP = "contentPaddingTop"//Sets the content padding top
        const val CARD_VIEW_CONTENT_PADDING_RIGHT =
            "contentPaddingRight"//Sets the content padding right
        const val CARD_VIEW_CONTENT_PADDING_BOTTOM =
            "contentPaddingBottom"//Sets the content padding bottom

    }

    /**
     * Contains constants for ConstraintLayout attributes.
     */
    object ConstraintLayout {

        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_BEGIN =
            "layout_constraintGuide_begin" // Constrains the start edge to the start of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_END =
            "layout_constraintGuide_end" // Constrains the end edge to the end of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_GUIDELINE_PERCENT =
            "layout_constraintGuide_percent" // Constraint using a percentage

        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF =
            "layout_constraintLeft_toLeftOf" // Constrains the left edge to the left edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF =
            "layout_constraintLeft_toRightOf" // Constrains the left edge to the right edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF =
            "layout_constraintRight_toLeftOf" // Constrains the right edge to the left edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF =
            "layout_constraintRight_toRightOf" // Constrains the right edge to the right edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_TOP_OF =
            "layout_constraintTop_toTopOf" // Constrains the top edge to the top edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF =
            "layout_constraintTop_toBottomOf" // Constrains the top edge to the bottom edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF =
            "layout_constraintBottom_toTopOf" // Constrains the bottom edge to the top edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF =
            "layout_constraintBottom_toBottomOf" // Constrains the bottom edge to the bottom edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF =
            "layout_constraintBaseline_toBaselineOf" // Baseline constraint
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_START_OF =
            "layout_constraintStart_toStartOf" // Constrains the start edge to the start edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_END_OF =
            "layout_constraintStart_toEndOf" // Constrains the start edge to the end edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_START_OF =
            "layout_constraintEnd_toStartOf" // Constrains the end edge to the start edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_END_OF =
            "layout_constraintEnd_toEndOf" // Constrains the end edge to the end edge of another view
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS =
            "layout_constraintHorizontal_bias" // Horizontal bias
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS =
            "layout_constraintVertical_bias" // Vertical bias

        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_WIDTH =
            "layout_constrainedWidth"//Constrained width
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINED_HEIGHT =
            "layout_constrainedHeight"//Constrained height
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE =
            "layout_constraintCircle"//Constraint circle
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_RADIUS =
            "layout_constraintCircleRadius"//Constraint circle radius
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_CIRCLE_ANGLE =
            "layout_constraintCircleAngle"//Constraint circle angle

        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_LEFT = "layout_marginLeft"//Layout margin left
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_TOP = "layout_marginTop"//Layout margin top
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_RIGHT = "layout_marginRight"//Layout margin right
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_BOTTOM =
            "layout_marginBottom"//Layout margin bottom
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_START = "layout_marginStart"//Layout margin start
        const val CONSTRAINTLAYOUT_LAYOUT_MARGIN_END = "layout_marginEnd"//Layout margin end
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_LEFT =
            "layout_goneMarginLeft"//Layout gone margin left
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_TOP =
            "layout_goneMarginTop"//Layout gone margin top
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_RIGHT =
            "layout_goneMarginRight"//Layout gone margin right
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_BOTTOM =
            "layout_goneMarginBottom"//Layout gone margin bottom
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_START =
            "layout_goneMarginStart"//Layout gone margin start
        const val CONSTRAINTLAYOUT_LAYOUT_GONE_MARGIN_END =
            "layout_goneMarginEnd"//Layout gone margin end
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_PERCENT =
            "layout_constraintWidth_percent"//Layout constraint width percent
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HEIGHT_PERCENT =
            "layout_constraintHeight_percent"//Layout constraint height percent
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_WIDTH_RATIO =
            "layout_constraintWidth_ratio"//Layout constraint width ratio
        const val CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HEIGHT_RATIO =
            "layout_constraintHeight_ratio"//Layout constraint height ratio
        const val CONSTRAINTLAYOUT_CHAIN_USED_POSITION = "chainUseRtl" //chainUseRtl (Deprecated)
        const val CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE =
            "layout_constraintHorizontal_chainStyle"//Horizontal chain style
        const val CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE =
            "layout_constraintVertical_chainStyle"//Vertical chain style
        const val CONSTRAINTLAYOUT_DIMENSION_RATIO =
            "layout_constraintDimensionRatio" // layout_constraintDimensionRatio
    }

    /**
     * Contains constants for VideoView attributes.
     */
    object VideoView {
        const val VIDEO_VIEW_URI = "uri" // Sets the video source using a Uri
        const val VIDEO_VIEW_PATH = "path" // Sets the video source using a file path.
        const val VIDEO_VIEW_KEEP_SCREEN_ON = "keepScreenOn" //Keeps the screen on during playback.
    }

    /**
     * Switch Attributes
     */

    object Switch {
        const val SWITCH_TEXT_ON = "textOn"//Text to display when in the ON state.
        const val SWITCH_TEXT_OFF = "textOff" //	Text to display when in the OFF state.
        const val SWITCH_SHOW_TEXT = "showText"// Whether to display text.
        const val SWITCH_THUMB_TEXT_PADDING =
            "thumbTextPadding"// Amount of padding on either side of text within the switch thumb.
        const val SWITCH_SWITCH_PADDING =
            "switchPadding"//Amount of padding between the switch and the associated text.
    }

    /**
     * TabLayout Attributes.
     */
    object TabLayout {
        const val TAB_INDICATOR_GRAVITY = "indicatorGravity"// set Bottom  Tab indicator
        const val TAB_ITEM_PADDING = "tabPadding"//padding between item inside  tabLayout
        const val TAB_ITEM_BACKGROUND = "tabBackground" //Color resource to use for tab strip
        const val TAB_MODE = "tabMode" //TabLayoutMode fixed scrollable

    }

    /**
     * Media Controller View
     */

    object MediaController {

        const val MEDIA_PREVIOUS = "showPrevious" // show Previous Video option
        const val MEDIA_NEXT = "showNext"// Show Next option on control View
        const val MEDIA_TITTLE = "showTittle" //Show the tittle on mediaController View.
    }

    /**
     * Material Components -  These require the
     * `com.google.android.material:material` dependency.
     */
    object MaterialButton {
        const val MATERIAL_BUTTON_BACKGROUND_TINT = "backgroundTint"//Sets background tint.
        const val MATERIAL_BUTTON_CORNER_RADIUS = "cornerRadius"
        const val MATERIAL_BUTTON_ICON = "icon"
        const val MATERIAL_BUTTON_ICON_GRAVITY = "iconGravity"//Sets the gravity.
        const val MATERIAL_BUTTON_ICON_PADDING = "iconPadding"
        const val MATERIAL_BUTTON_ICON_TINT = "iconTint"
        const val MATERIAL_BUTTON_ICON_SIZE = "iconSize"
        const val MATERIAL_BUTTON_STROKE_COLOR = "strokeColor"
        const val MATERIAL_BUTTON_STROKE_WIDTH = "strokeWidth"
        const val MATERIAL_BUTTON_RIPPLE_COLOR = "rippleColor"
        const val MATERIAL_BUTTON_SHAPE_APPEARANCE_OVERRIDE =
            "shapeAppearanceOverlay" //The Shape Appearance style that will be used to shape this component

    }

    object MaterialTextView {
        const val MATERIAL_TEXTVIEW_HINT_TEXT_APPEARANCE =
            "hintTextAppearance" // hintTextAppearance (attr)
        const val MATERIAL_TEXTVIEW_HINT_TEXT_COLOR = "hintTextColor" //hintTextColor (attr)
        const val MATERIAL_TEXTVIEW_ERROR_TEXT_COLOR = "errorTextColor" // errorTextColor (attr)

    }

    object TextInputLayout {
        const val TEXT_INPUT_LAYOUT_HINT_ENABLED = "hintEnabled"//Sets whether hint is enabled
        const val TEXT_INPUT_LAYOUT_HINT_ANIMATED =
            "hintAnimationEnabled"//Sets whether hint is animated
        const val TEXT_INPUT_LAYOUT_HINT_TEXT_APPEARANCE2 = "hintTextAppearance"
        const val TEXT_INPUT_LAYOUT_ERROR_ENABLED = "errorEnabled"//Sets whether error is enabled
        const val TEXT_INPUT_LAYOUT_END_ICON_MODE = "endIconMode"
        const val TEXT_INPUT_LAYOUT_START_ICON_DRAWABLE = "startIconDrawable"//Sets a start icon
        const val TEXT_INPUT_LAYOUT_END_ICON_DRAWABLE = "endIconDrawable"
        const val TEXT_INPUT_LAYOUT_BOX_BACKGROUND_MODE =
            "boxBackgroundMode" // boxBackgroundMode (attr)  e.g. outlined, filled.
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_START =
            "boxCornerRadiusTopStart"//Sets radius on the corner
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_TOP_END = "boxCornerRadiusTopEnd"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_START = "boxCornerRadiusBottomStart"
        const val TEXT_INPUT_LAYOUT_BOX_CORNER_RADIUS_BOTTOM_END = "boxCornerRadiusBottomEnd"
        const val TEXT_INPUT_LAYOUT_PREFIX_TEXT =
            "prefixText" //  Display prefixes in TextInputLayout
        const val TEXT_INPUT_LAYOUT_SUFFIX_TEXT = "suffixText" //Display suffixes in TextInputLayout
        const val TEXT_INPUT_LAYOUT_COUNTER_ENABLED =
            "counterEnabled" // Display suffixes in TextInputLayout

    }

    object MaterialNavigationBarView {
        // NavigationBarView (Material3)
        const val NAVIGATION_BAR_VIEW_MENU = "menu"//set The navigation items that will be displayed
        const val NAVIGATION_BAR_VIEW_ITEM_ICON_TINT = "itemIconTint"//set Tint color
        const val NAVIGATION_BAR_VIEW_ITEM_TEXT_COLOR =
            "itemTextColor"//Sets the text color of the navigation items
        const val NAVIGATION_BAR_VIEW_LABEL_VISIBILITY_MODE =
            "labelVisibilityMode" //label Visibility
        const val NAVIGATION_BAR_VIEW_ITEM_PADDING = "itemPadding" //Item Padding
        const val NAVIGATION_BAR_VIEW_SELECTED_ITEM_ID =
            "selectedItemId" // Item selected identifier
        const val ITEM_HORIZONTAL_TRANSLATION_ENABLED =
            "itemHorizontalTranslationEnabled" // show item from bottom side on first att.
        const val ITEM_ICON_SIZE = "itemIconSize" // Change the size inside
    }

    object MaterialBottomNavigationView {
        const val BOTTOM_NAVIGATION_VIEW_ITEM_SHRINK =
            "itemHorizontalTranslationEnabled" // Sets Item Shrink
        const val BOTTOM_NAVIGATION_VIEW_ITEM_ACTIVE_INDICATOR_ENABLED =
            "itemActiveIndicatorEnabled"//Sets whether the itemActiveIndicatorEnabled
        const val BOTTOM_NAVIGATION_VIEW_ACTIVE_INDICATOR_INSET =
            "itemActiveIndicatorInset" // ItemActiveIndicatorInset
        const val BOTTOM_NAVIGATION_VIEW_ACTIVE_INDICATOR_WIDTH =
            "itemActiveIndicatorWidth" // ActiveIndicatorWidth
        const val BOTTOM_NAVIGATION_VIEW_ACTIVE_INDICATOR_HEIGHT =
            "itemActiveIndicatorHeight"// Sets height.
        const val BOTTOM_NAVIGATION_VIEW_ITEM_SHAPE_FILL_COLOR = "itemShapeFillColor" // Sets Color
        const val BOTTOM_NAVIGATION_VIEW_ACTIVE_INDICATOR_SHAPE_APPEARANCE =
            "itemActiveIndicatorShapeAppearance"
        const val ITEM_SHAPE_APPEARANCE =
            "itemShapeAppearance" // add Custom SHAPE APPEARANCE TO TAB VIEW ITEM
        const val SHAPE_APPEARANCE_CORNER_FAMILY = "itemShapeAppearanceCornerFamily"// edit radius

    }

    object ExtendedFloatingActionButton {
        const val EXTENDED_FLOATING_ACTION_BUTTON_EXTENDED =
            "extended" //Determines is the is initially in extended
        const val EXTENDED_FLOATING_ACTION_BUTTON_SHOW_MOTION_SPEC =
            "showMotionSpec" //show the floating action bar by code
        const val EXTENDED_FLOATING_ACTION_BUTTON_HIDE_MOTION_SPEC =
            "hideMotionSpec" // hide the bar motion effect in activity
        const val FAB_SIZE = "fabSize"// The size of the FAB

    }

    object Chip {
        const val CHIP_CHIP_BACKGROUND_COLOR =
            "chipBackgroundColor" //Customize background with a new color.
        const val CHIP_CHIP_TEXT_COLOR = "chipTextColor" // Changing the text color in the chip
        const val CHIP_CLOSE_ICON_ENABLED =
            "closeIconEnabled"//Whether the chip displays a close icon.
        const val CHIP_CHIP_ICON_SIZE = "chipIconSize"//The size of the chip icon
        const val CHIP_CLOSE_ICON =
            "closeIcon" // Customizing closing item using local resources drawable or icons


    }

    object ChipGroup {
        const val CHIP_GROUP_SINGLE_LINE = "singleLine"
        const val CHIP_GROUP_SINGLE_SELECTION = "singleSelection"

    }

    /** Calender VIew
     * Set custom item attributes .
     * set The attributes you want here , on Calendar.
     */

    object Calender {
        const val YEAR_VIEW = "yearView"
        const val MONTH_VIEW = "monthView"// show Calender to select month to user
        const val DATE_VIEW = "dayView"
        const val SHOW_WEEK =
            "showWeekNumber" // enable Or disable The Number week options inside calender
    }

    object BottomSheet {
        const val BEHAVIOR_PEEK_HEIGHT =
            "behavior_peekHeight"  // set Custom Peek Height of View bottom Sheet Dialog
        const val BEHAVIOR_HIDE_ABLE =
            "behavior_hideable"//Enable and disable Draggable function in sheet item option
    }

    object ScrollBar {
        const val VIEW_VERTICAL_THUMB_DRAWABLE =
            "verticalScrollbarThumbDrawable" // add vertical drawable from xml
        const val VERTICAL_TRACK = "verticalScrollbarTrackDrawable"// Set Vertical track
        const val SCROLL_DEFAULT = "scrollIndicators" //indicator to make Custom Scrollbar inside
        const val SMOOTH_SCROLL = "smoothScrollbar"
    }
}