package com.flipkart.android.proteus.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * A {@link FrameLayout} that maintains a specified aspect ratio for its dimensions.
 *
 * `AspectRatioFrameLayout` is a custom layout that forces a particular aspect ratio (width to height)
 * during the measurement process. This ensures that the layout always maintains the correct proportions,
 * regardless of the available space or how it's laid out in its parent.
 *
 * It is useful for scenarios where you need to display content with a fixed aspect ratio, such as:
 *
 * *   Images or videos that should not be distorted.
 * *   UI elements that need to maintain consistent proportions across different screen sizes.
 * *   Creating responsive layouts where aspect ratios play a crucial role in visual harmony.
 *
 * **Key Features:**
 *
 * *   **Aspect Ratio Control:**  Allows you to set the desired aspect ratio (width and height components).
 * *   **Dynamic Measurement:**  Overrides {@link #onMeasure(int, int)} to calculate and enforce the aspect ratio
 *     when the layout is measured.
 * *   **Handles Zero Dimensions:** Gracefully handles cases where the initially measured width or height is zero,
 *     preventing division by zero errors and still attempting to maintain the aspect ratio based on the non-zero dimension.
 * *   **XML Attributes and Programmatic Setup:**  Can be configured using XML attributes or by setting aspect ratio
 *     properties programmatically.
 * *   **Constructors for different scenarios:** Provides constructors to be instantiated in various Android layout
 *     inflation scenarios (from code, from XML, with style attributes).
 *
 * **Usage Scenario:**
 *
 * To use `AspectRatioFrameLayout`, you typically set the `aspectRatioWidth` and `aspectRatioHeight` either in XML
 * attributes or in your code. Then, when the layout is measured, it will adjust its dimensions to adhere to the
 * defined aspect ratio.
 *
 * You would then also typically provide custom setters in your `ProteusAspectRatioFrameLayout`
 * (or similar class integrating this into a UI framework) to bind data to `aspectRatioWidth` and
 * `aspectRatioHeight` attributes, making the aspect ratio dynamic.
 */
open class AspectRatioFrameLayout : FrameLayout { // 'open' for potential extension

    /**
     * The width component of the aspect ratio (e.g., 16 in 16:9).
     *
     * Defaults to 0 if not set, in which case aspect ratio enforcement is effectively disabled.
     * Set using {@link #setAspectRatioWidth(int)} or XML attribute 'aspectRatioWidth'.
     */
    private var mAspectRatioWidth = 0

    /**
     * The height component of the aspect ratio (e.g., 9 in 16:9).
     *
     * Defaults to 0 if not set, disabling aspect ratio enforcement.
     * Set using {@link #setAspectRatioHeight(int)} or XML attribute 'aspectRatioHeight'.
     */
    private var mAspectRatioHeight = 0

    /**
     * Default constructor, called when creating {@link AspectRatioFrameLayout} programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for XML inflation.
     *
     * @param context The Android {@link Context}.
     * @param attrs   Attributes from the XML tag, expected to include 'aspectRatioWidth' and 'aspectRatioHeight'.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for XML inflation with style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        Attributes from XML tag.
     * @param defStyle Style attribute resource.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    )

    /**
     * Constructor for XML inflation with style attribute and resource (API 21+).
     *
     * @param context      The Android {@link Context}.
     * @param attrs        Attributes from XML tag.
     * @param defStyleAttr Style attribute resource.
     * @param defStyleRes  Style resource.
     */
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(
        context, attrs, defStyleAttr, defStyleRes
    )

    /**
     * Measures the layout and enforces the defined aspect ratio if {@link #mAspectRatioWidth} and {@link #mAspectRatioHeight} are set.
     *
     * If aspect ratio components are both greater than 0, it calculates the final width and height based on the original measured dimensions
     * and the specified aspect ratio. It handles cases where the original width or height is zero to avoid division by zero.
     * If aspect ratio is not set (or invalid), it behaves as a normal {@link FrameLayout} and uses the default measurement logic.
     *
     * @param widthMeasureSpec  Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mAspectRatioHeight > 0 && mAspectRatioWidth > 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            val originalWidth = measuredWidth
            val originalHeight = measuredHeight

            val finalWidth: Int
            val finalHeight: Int

            if (originalHeight == 0) {
                // If height is 0, calculate height based on width and aspect ratio
                finalHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth
                finalWidth = originalWidth // Keep width as original
            } else if (originalWidth == 0) {
                // If width is 0, calculate width based on height and aspect ratio
                finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight
                finalHeight = originalHeight // Keep height as original
            } else {
                // If both width and height are non-zero, keep them as original
                finalHeight = originalHeight
                finalWidth = originalWidth
            }

            // Re-measure with calculated final dimensions
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY)
            )
        } else {
            // If aspect ratio not set, use default FrameLayout measurement
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * Sets the width component of the aspect ratio.
     *
     * For example, to set a 16:9 aspect ratio, you would call `setAspectRatioWidth(16)`.
     * Setting this and {@link #setAspectRatioHeight(int)} to values greater than 0 will enable aspect ratio enforcement in {@link #onMeasure(int, int)}.
     *
     * @param mAspectRatioWidth The width part of the aspect ratio (must be greater than 0 to enable aspect ratio).
     */
    fun setAspectRatioWidth(mAspectRatioWidth: Int) {
        this.mAspectRatioWidth = mAspectRatioWidth
    }

    /**
     * Sets the height component of the aspect ratio.
     *
     * For example, for a 16:9 aspect ratio, call `setAspectRatioHeight(9)`.
     * Must be used in conjunction with {@link #setAspectRatioWidth(int)} to define the aspect ratio.
     *
     * @param mAspectRatioHeight The height part of the aspect ratio (must be greater than 0 to enable aspect ratio).
     */
    fun setAspectRatioHeight(mAspectRatioHeight: Int) {
        this.mAspectRatioHeight = mAspectRatioHeight
    }
}