package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager
import com.flipkart.android.proteus.view.custom.FixedRatingBar

/**
 * A {@link ProteusView} implementation of {@link FixedRatingBar}, integrating FixedRatingBar into Proteus layouts.
 *
 * `ProteusFixedRatingBar` serves as a bridge, allowing you to utilize the custom {@link FixedRatingBar} (which provides
 * width-fixing based on tile bitmaps) within the Proteus layout framework. By extending `FixedRatingBar` and
 * implementing `ProteusView`, it makes this custom RatingBar component fully manageable by Proteus, enabling
 * data binding, attribute updates, and seamless integration into Proteus-driven UIs.
 *
 * It enables you to use `FixedRatingBar` within Proteus layouts as if it were a standard Proteus-compatible view.
 *
 * **Key Features:**
 *
 * *   **Extends `com.flipkart.android.proteus.view.custom.FixedRatingBar`:** Inherits the custom width calculation and
 *     tiling capabilities of `FixedRatingBar`, ensuring that the rating bar's width is correctly determined based on
 *     the tile bitmap and number of stars.
 * *   **Implements `ProteusView`:** Enables integration into the Proteus layout system, making it data-bindable and
 *     dynamically manageable by Proteus.
 * *   **Manages `ViewManager`:**  Holds a {@link com.google.android.flexbox.ProteusView.Manager} (`viewManager`) to
 *     handle data updates, attribute processing, and other Proteus-related management tasks for this custom RatingBar.
 * *   **Constructors for XML and Programmatic Creation:** Provides constructors to support instantiation when inflated
 *     from XML layouts or when created directly in code.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the necessary {@link ProteusView} interface methods to
 *     get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:**  Provides access to the underlying Android {@link View} instance (which is `this` in this case).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusFixedRatingBar` within your Proteus layouts whenever you need a rating bar that:
 *
 * 1.  Requires its width to be precisely calculated based on the tile bitmap size.
 * 2.  Leverages the custom features of {@link FixedRatingBar} (like width fixing and tiled drawables).
 * 3.  Needs to be dynamically controlled and data-bound within a Proteus-driven UI.
 *
 * This is particularly useful when you have specific visual requirements for your rating bars and need precise control
 * over their dimensions based on custom star images.
 *
 * **Example (Conceptual - within a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <LinearLayout
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:orientation="vertical">
 *
 *     <com.flipkart.android.proteus.view.ProteusFixedRatingBar  // Using ProteusFixedRatingBar (adjust package name)
 *         android:id="@+id/myFixedRatingBar"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:numStars='@{model.starCount}'        // Example data-binding for number of stars
 *         app:rating='@{model.ratingValue}'           // Example data-binding for rating value
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * **Note:**  Make sure to also provide a sample tile bitmap to the `ProteusFixedRatingBar` either programmatically or via
 * a custom attribute if you intend to use the width-fixing functionality of the underlying {@link FixedRatingBar}.
 *
 * @see FixedRatingBar
 * @see ProteusView
 * @see Manager
 */
open class ProteusFixedRatingBar : FixedRatingBar, ProteusView { // 'open' for potential subclassing

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusFixedRatingBar}.
     *
     * Manages data binding, attribute updates, and Proteus framework integration for this custom RatingBar.
     */
    lateinit var proteusFixedRatingBarViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Constructor for {@link ProteusFixedRatingBar}.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusFixedRatingBar}.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusFixedRatingBar}.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Gets the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusFixedRatingBar}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this view.
     */
    override val viewManager: Manager = proteusFixedRatingBarViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusFixedRatingBar}.
     *
     * Called by the Proteus framework during view inflation to associate a manager for data binding and attribute processing.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusFixedRatingBarViewManager = manager
    }

    /**
     * Returns the Android {@link View} instance (which is 'this' {@link ProteusFixedRatingBar} itself).
     *
     * @return The Android {@link View} instance.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' instance of ProteusFixedRatingBar as a View
}