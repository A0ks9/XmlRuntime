package com.flipkart.android.proteus.toolbox

/**
 * Kotlin object containing constants for Android View attributes.
 *
 * This object mirrors the Java `Attributes.View` class, providing
 * a collection of `const val` strings representing common XML attributes
 * used for Android Views in layout files.
 *
 * Using `const val` ensures these values are compile-time constants,
 * improving performance and readability.
 */
object ViewAttributes { // Converted class to object for simplicity and Kotlin idiomatic style
    // Layout attributes related to positioning relative to other views
    const val Above = "layout_above" // Positions the view above another view with a given ID
    const val Activated = "activated" // Sets the activated state of the view
    const val AlignBaseline =
        "layout_alignBaseline" // Aligns the baseline of this view to the baseline of another view
    const val AlignBottom =
        "layout_alignBottom" // Aligns the bottom edge of this view to the bottom edge of another view
    const val AlignEnd =
        "layout_alignEnd" // Aligns the end edge of this view to the end edge of another view
    const val AlignLeft =
        "layout_alignLeft" // Aligns the left edge of this view to the left edge of another view
    const val AlignParentBottom =
        "layout_alignParentBottom" // Aligns the bottom edge of this view to the bottom of its parent
    const val AlignParentEnd =
        "layout_alignParentEnd" // Aligns the end edge of this view to the end of its parent
    const val AlignParentLeft =
        "layout_alignParentLeft" // Aligns the left edge of this view to the left of its parent
    const val AlignParentRight =
        "layout_alignParentRight" // Aligns the right edge of this view to the right of its parent
    const val AlignParentStart =
        "layout_alignParentStart" // Aligns the start edge of this view to the start of its parent
    const val AlignParentTop =
        "layout_alignParentTop" // Aligns the top edge of this view to the top of its parent
    const val AlignRight =
        "layout_alignRight" // Aligns the right edge of this view to the right edge of another view
    const val AlignStart =
        "layout_alignStart" // Aligns the start edge of this view to the start edge of another view
    const val AlignTop =
        "layout_alignTop" // Aligns the top edge of this view to the top edge of another view

    // Basic view attributes
    const val Alpha = "alpha" // Sets the alpha transparency of the view
    const val Animation = "animation" // Specifies an animation to run for the view
    const val Background = "background" // Sets the background of the view
    const val BackgroundTint = "backgroundTint" // Applies a tint to the background of the view
    const val BackgroundTintMode =
        "backgroundTintMode" // Sets the blending mode for the background tint
    const val Below = "layout_below" // Positions the view below another view with a given ID

    // Layout attributes for centering within parent
    const val CenterHorizontal =
        "layout_centerHorizontal" // Centers the view horizontally within its parent
    const val CenterInParent =
        "layout_centerInParent" // Centers the view both horizontally and vertically within its parent
    const val CenterVertical =
        "layout_centerVertical" // Centers the view vertically within its parent

    // Interaction attributes
    const val Clickable = "clickable" // Makes the view clickable
    const val ContentDescription =
        "contentDescription" // Provides a textual description of the view for accessibility
    const val ContextClickable = "contextClickable" // Makes the view context-clickable (long press)
    const val DrawingCacheQuality = "drawingCacheQuality" // Sets the quality of the drawing cache

    // Visual appearance and behavior attributes
    const val Elevation = "elevation" // Sets the elevation of the view for shadow effects
    const val Enabled = "enabled" // Enables or disables the view
    const val FadeScrollbars = "fadeScrollbars" // Makes scrollbars fade out when not in use
    const val FadingEdgeLength = "fadingEdgeLength" // Sets the length of the fading edge effect
    const val FilterTouchesWhenObscured =
        "filterTouchesWhenObscured" // Filters touches when the view is obscured by another view
    const val FitsSystemWindows =
        "fitsSystemWindows" // Indicates if the view should fit system window insets

    // Focus related attributes
    const val Focusable = "focusable" // Makes the view focusable
    const val FocusableInTouchMode =
        "focusableInTouchMode" // Makes the view focusable in touch mode
    const val ForceHasOverlappingRendering =
        "forceHasOverlappingRendering" // Forces the view to be treated as having overlapping rendering

    // Foreground related attributes
    const val Foreground = "foreground" // Sets the foreground drawable of the view
    const val ForegroundGravity = "foregroundGravity" // Sets the gravity of the foreground drawable
    const val ForegroundTint = "foregroundTint" // Applies a tint to the foreground of the view
    const val ForegroundTintMode =
        "foregroundTintMode" // Sets the blending mode for the foreground tint

    // General attributes
    const val Gravity = "gravity" // Sets the gravity of the content within the view
    const val HapticFeedbackEnabled =
        "hapticFeedbackEnabled" // Enables or disables haptic feedback for the view
    const val Height = "layout_height" // Sets the height of the view
    const val Id = "id" // Sets a unique ID for the view
    const val IsScrollContainer = "isScrollContainer" // Indicates if the view is a scroll container
    const val KeepScreenOn = "keepScreenOn" // Keeps the screen on while the view is visible
    const val LayerType = "layerType" // Sets the layer type for hardware or software rendering
    const val LayoutDirection = "layoutDirection" // Sets the layout direction (LTR or RTL)
    const val LayoutGravity = "layout_gravity" // Sets the gravity of the view within its layout
    const val LongClickable = "longClickable" // Makes the view long-clickable

    // Margin related layout attributes
    const val Margin = "layout_margin" // Sets the margin on all sides of the view
    const val MarginBottom = "layout_marginBottom" // Sets the bottom margin of the view
    const val MarginLeft = "layout_marginLeft" // Sets the left margin of the view
    const val MarginRight = "layout_marginRight" // Sets the right margin of the view
    const val MarginTop = "layout_marginTop" // Sets the top margin of the view

    // Size constraints
    const val MinHeight = "minHeight" // Sets the minimum height of the view
    const val MinWidth = "minWidth" // Sets the minimum width of the view

    // Focus navigation attributes
    const val NextFocusDown =
        "nextFocusDown" // Specifies the view to receive focus when navigating down
    const val NextFocusForward =
        "nextFocusForward" // Specifies the view to receive focus when navigating forward
    const val NextFocusLeft =
        "nextFocusLeft" // Specifies the view to receive focus when navigating left
    const val NextFocusRight =
        "nextFocusRight" // Specifies the view to receive focus when navigating right
    const val NextFocusUp = "nextFocusUp" // Specifies the view to receive focus when navigating up

    // Event handler attributes (usually set programmatically, but can be in layout for simple cases)
    const val OnClick = "onClick" // Specifies the method to call when the view is clicked
    const val OnLongClick =
        "onLongClick" // Specifies the method to call when the view is long-clicked
    const val OnTouch = "onTouch" // Specifies the method to call when the view is touched

    // Padding related attributes
    const val Padding = "padding" // Sets the padding on all sides of the view
    const val PaddingBottom = "paddingBottom" // Sets the bottom padding of the view
    const val PaddingEnd = "paddingEnd" // Sets the end padding of the view
    const val PaddingLeft = "paddingLeft" // Sets the left padding of the view
    const val PaddingRight = "paddingRight" // Sets the right padding of the view
    const val PaddingStart = "paddingStart" // Sets the start padding of the view
    const val PaddingTop = "paddingTop" // Sets the top padding of the view

    // Scrolling related attributes
    const val RequiresFadingEdge =
        "requiresFadingEdge" // Indicates if the view should have fading edges

    // Transformation attributes
    const val Rotation = "rotation" // Sets the rotation of the view in degrees
    const val RotationX = "rotationX" // Sets the rotation of the view around the X axis
    const val RotationY = "rotationY" // Sets the rotation of the view around the Y axis
    const val SaveEnabled =
        "saveEnabled" // Indicates if the view's state should be saved during configuration changes
    const val ScaleX = "scaleX" // Sets the scale factor on the X axis
    const val ScaleY = "scaleY" // Sets the scale factor on the Y axis
    const val ScrollIndicators = "scrollIndicators" // Sets which scroll indicators to display
    const val ScrollbarDefaultDelayBeforeFade =
        "scrollbarDefaultDelayBeforeFade" // Sets the delay before scrollbars start to fade
    const val ScrollbarFadeDuration =
        "scrollbarFadeDuration" // Sets the duration of the scrollbar fade animation
    const val ScrollbarSize = "scrollbarSize" // Sets the thickness of the scrollbars
    const val ScrollbarStyle = "scrollbarStyle" // Sets the style of the scrollbars
    const val Selected = "selected" // Sets the selected state of the view
    const val SoundEffectsEnabled =
        "soundEffectsEnabled" // Enables or disables sound effects for the view
    const val Style = "style" // Sets the style resource for the view
    const val Tag = "tag" // Sets a tag for the view, useful for finding views later
    const val TextAlignment = "textAlignment" // Sets the text alignment
    const val TextDirection = "textDirection" // Sets the text direction

    // Layout attributes related to positioning relative to other views (End/Start variants)
    const val ToEndOf =
        "layout_toEndOf" // Positions the right edge of the view to the end edge of another view
    const val ToLeftOf =
        "layout_toLeftOf" // Positions the right edge of the view to the left edge of another view
    const val ToRightOf =
        "layout_toRightOf" // Positions the left edge of the view to the right edge of another view
    const val ToStartOf =
        "layout_toStartOf" // Positions the left edge of the view to the start edge of another view

    // Transformation pivot attributes
    const val TransformPivotX =
        "transformPivotX" // Sets the pivot point for rotation and scaling on the X axis
    const val TransformPivotY =
        "transformPivotY" // Sets the pivot point for rotation and scaling on the Y axis
    const val TransitionName =
        "transitionName" // Sets a transition name for shared element transitions

    // Translation attributes
    const val TranslationX = "translationX" // Sets the translation of the view on the X axis
    const val TranslationY = "translationY" // Sets the translation of the view on the Y axis
    const val TranslationZ =
        "translationZ" // Sets the translation of the view on the Z axis (for elevation)
    const val Visibility =
        "visibility" // Sets the visibility of the view (visible, invisible, gone)

    // Layout weight attribute for LinearLayout
    const val Weight =
        "layout_weight" // Assigns a weight to the view for layout within a LinearLayout

    // Size attributes
    const val Width = "layout_width" // Sets the width of the view
}

/**
 * Kotlin object containing constants for WebView attributes.
 *
 *  Mirrors the Java `Attributes.WebView` class.
 */
object WebViewAttributes { // Kotlin object for WebView attributes
    const val Url = "url" // Loads a URL in the WebView
    const val HTML = "html" // Loads HTML content in the WebView
}

/**
 * Kotlin object containing constants for RatingBar attributes.
 *
 * Mirrors the Java `Attributes.RatingBar` class.
 */
object RatingBarAttributes { // Kotlin object for RatingBar attributes
    const val NumStars = "numStars" // Sets the number of stars in the rating bar
    const val Rating = "rating" // Sets the current rating value
    const val IsIndicator = "isIndicator" // Makes the rating bar an indicator (non-interactive)
    const val StepSize = "stepSize" // Sets the step size for rating changes
    const val ProgressDrawable = "progressDrawable" // Sets a custom progress drawable
    const val MinHeight = "minHeight" // Sets the minimum height
}

/**
 * Kotlin object containing constants for TextView attributes.
 *
 * Mirrors the Java `Attributes.TextView` class.
 */
object TextViewAttributes { // Kotlin object for TextView attributes
    const val Gravity = "gravity" // Sets text gravity within the TextView
    const val Text = "text" // Sets the text content
    const val HTML = "html" // Sets HTML formatted text content
    const val TextSize = "textSize" // Sets the text size
    const val TextColor = "textColor" // Sets the text color
    const val TextColorHint = "textColorHint" // Sets the hint text color
    const val TextColorLink = "textColorLink" // Sets the link text color
    const val TextColorHighLight = "textColorHighlight" // Sets the highlight text color
    const val DrawableLeft = "drawableLeft" // Sets a drawable to the left of the text
    const val DrawableRight = "drawableRight" // Sets a drawable to the right of the text
    const val DrawableTop = "drawableTop" // Sets a drawable above the text
    const val DrawableBottom = "drawableBottom" // Sets a drawable below the text
    const val DrawablePadding = "drawablePadding" // Sets padding between drawables and text
    const val MaxLines = "maxLines" // Sets the maximum number of lines
    const val Ellipsize = "ellipsize" // Sets the type of ellipsis to use for text overflow
    const val PaintFlags = "paintFlags" // Sets paint flags for styling (e.g., bold, italic)
    const val Prefix = "prefix" // Sets a prefix text
    const val Suffix = "suffix" // Sets a suffix text
    const val TextStyle = "textStyle" // Sets the text style (normal, bold, italic)
    const val SingleLine = "singleLine" // Restricts text to a single line
    const val TextAllCaps = "textAllCaps" // Converts text to all caps
    const val Hint = "hint" // Sets the hint text
}

/**
 * Kotlin object containing constants for CheckBox attributes.
 *
 * Mirrors the Java `Attributes.CheckBox` class.
 */
object CheckBoxAttributes { // Kotlin object for CheckBox attributes
    const val Checked = "checked" // Sets the checked state
    const val Button = "button" // Sets a custom button drawable
}

/**
 * Kotlin object containing constants for FrameLayout attributes.
 *
 * Mirrors the Java `Attributes.FrameLayout` class.
 */
object FrameLayoutAttributes { // Kotlin object for FrameLayout attributes
    const val HeightRatio = "heightRatio" // Sets a height ratio for aspect ratio
    const val WidthRatio = "widthRatio" // Sets a width ratio for aspect ratio
}

/**
 * Kotlin object containing constants for ImageView attributes.
 *
 * Mirrors the Java `Attributes.ImageView` class.
 */
object ImageViewAttributes { // Kotlin object for ImageView attributes
    const val Src = "src" // Sets the image source
    const val ScaleType = "scaleType" // Sets how the image should be scaled
    const val AdjustViewBounds = "adjustViewBounds" // Adjusts view bounds to maintain aspect ratio
}

/**
 * Kotlin object containing constants for ViewGroup attributes.
 *
 * Mirrors the Java `Attributes.ViewGroup` class.
 */
object ViewGroupAttributes { // Kotlin object for ViewGroup attributes
    const val Children =
        "children" // Represents child views (not an XML attribute, likely for programmatic use)
    const val ClipChildren =
        "clipChildren" // Clips drawing of children to the bounds of the ViewGroup
    const val ClipToPadding =
        "clipToPadding" // Clips drawing of children to the padding of the ViewGroup
    const val LayoutMode = "layoutMode" // Sets the layout mode (e.g., clipBounds)
    const val SplitMotionEvents =
        "splitMotionEvents" // Enables splitting of motion events to child views
}

/**
 * Kotlin object containing constants for LinearLayout attributes.
 *
 * Mirrors the Java `Attributes.LinearLayout` class.
 */
object LinearLayoutAttributes { // Kotlin object for LinearLayout attributes
    const val Orientation = "orientation" // Sets the orientation (horizontal or vertical)
    const val Divider = "divider" // Sets a divider drawable between children
    const val DividerPadding = "dividerPadding" // Sets padding for dividers
    const val ShowDividers = "showDividers" // Controls when dividers are shown
    const val WeightSum = "weightSum" // Defines the total weight sum for children
}

/**
 * Kotlin object containing constants for ScrollView attributes.
 *
 * Mirrors the Java `Attributes.ScrollView` class.
 */
object ScrollViewAttributes { // Kotlin object for ScrollView attributes
    const val Scrollbars = "scrollbars" // Sets scrollbar visibility and style
}

/**
 * Kotlin object containing constants for HorizontalScrollView attributes.
 *
 * Mirrors the Java `Attributes.HorizontalScrollView` class.
 */
object HorizontalScrollViewAttributes { // Kotlin object for HorizontalScrollView attributes
    const val FillViewPort =
        "fillViewPort" // Makes the scroll view stretch its content to fill the viewport
}

/**
 * Kotlin object containing constants for ProgressBar attributes.
 *
 * Mirrors the Java `Attributes.ProgressBar` class.
 */
object ProgressBarAttributes { // Kotlin object for ProgressBar attributes
    const val Progress = "progress" // Sets the current progress value
    const val Max = "max" // Sets the maximum progress value
    const val ProgressTint = "progressTint" // Sets the progress bar tint color
    const val IndeterminateTint =
        "indeterminateTint" // Sets the indeterminate progress bar tint color
    const val SecondaryProgressTint =
        "secondaryProgressTint" // Sets the secondary progress bar tint color
}