package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.view.custom.AspectRatioFrameLayout

/**
 * A {@link ProteusView} implementation of {@link AspectRatioFrameLayout}, which maintains a specific aspect ratio.
 *
 * This class, `ProteusAspectRatioFrameLayout`, combines the functionality of {@link AspectRatioFrameLayout}
 * (a custom layout that enforces a given aspect ratio for its dimensions) with the capabilities of {@link ProteusView}
 * to be managed within the Proteus framework.
 *
 * It extends `AspectRatioFrameLayout` (presumably a custom component from Flipkart Proteus library or related
 * custom UI components) and implements the `ProteusView` interface. This integration allows you to use
 * `AspectRatioFrameLayout` in your Proteus layouts, benefiting from Proteus's data binding and attribute
 * processing features while also ensuring your layout adheres to a defined aspect ratio.
 *
 * **Key features:**
 *
 * *   **Extends `AspectRatioFrameLayout`:**  Inherits the aspect ratio enforcement behavior from `AspectRatioFrameLayout`,
 *     ensuring that the layout maintains its proportions correctly regardless of available space.
 * *   **Implements `ProteusView`:**  Integrates with the Proteus layout system, enabling data binding,
 *     dynamic attribute updates, and management through a {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **Constructors for XML inflation and programmatic creation:** Provides constructors to be used when
 *     inflating from XML layouts or when creating instances programmatically in code.
 * *   **`getViewManager()` and `setViewManager()`:**  Required methods from the {@link ProteusView} interface to
 *     manage the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:**  Provides access to the underlying Android {@link View} instance (`this`).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusAspectRatioFrameLayout` in Proteus layouts whenever you need a FrameLayout that
 * automatically maintains a consistent aspect ratio for its content. This is particularly useful for:
 *
 * *   Displaying images or videos in a layout where you want to prevent distortion and ensure correct proportions.
 * *   Creating UI elements with fixed aspect ratios, regardless of screen size or available layout space.
 * *   Responsive layouts where aspect ratios are important for maintaining visual harmony across different devices.
 *
 * @see AspectRatioFrameLayout
 * @see ProteusView
 * @see ProteusView.Manager
 */
open class ProteusAspectRatioFrameLayout : AspectRatioFrameLayout,
    ProteusView { // 'open' for potential subclassing

    /**
     * The {@link ProteusView.Manager} responsible for managing this view within Proteus.
     */
    lateinit var frameLayoutViewManager: ProteusView.Manager // 'late init' for initialization after constructor

    /**
     * Default constructor, called when creating {@link ProteusAspectRatioFrameLayout} programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for XML inflation.
     *
     * @param context The Android {@link Context}.
     * @param attrs   Attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for XML inflation with style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        Attributes from the XML tag.
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
     * Returns the {@link ProteusView.Manager} associated with this view.
     *
     * @return The {@link ProteusView.Manager} for this view.
     */
    override val viewManager: ProteusView.Manager = frameLayoutViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this view.
     *
     * Called by the Proteus framework to associate a manager for handling data binding and attribute processing.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: ProteusView.Manager) {
        this.frameLayoutViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} (which is 'this' instance).
     *
     * @return The Android {@link View} instance.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' View instance
}