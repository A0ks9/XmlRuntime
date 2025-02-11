package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link ProgressBar}, allowing ProgressBars to be used in Proteus layouts.
 *
 * `ProteusProgressBar` acts as a bridge to incorporate standard Android {@link ProgressBar} widgets within
 * the Proteus layout framework. By extending `ProgressBar` and implementing `ProteusView`, it enables you to
 * use progress indicators in your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these progress bar components.
 *
 * It makes standard Android ProgressBar components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.ProgressBar`:** Inherits all the progress display capabilities, styling options, and
 *     functionalities of a native Android ProgressBar.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like progress, max, style, visibility),
 *     and other Proteus-specific management tasks for this ProgressBar.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link ProgressBar} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusProgressBar` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusProgressBar` in your Proteus layouts whenever you need to display progress indication to the user.
 * This is the standard way to include progress bars within a Proteus-driven UI. You can leverage Proteus data binding
 * to dynamically control progress values, visibility, and potentially customize the appearance of the progress bar using
 * themes and styles.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
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
 *     <com.flipkart.android.proteus.view.ProteusProgressBar // Using ProteusProgressBar (adjust package name)
 *         android:id="@+id/progressBar"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:max='@{model.maxProgressValue}'    // Example data-binding for max progress
 *         app:progress='@{model.currentProgress}'  // Example data-binding for current progress
 *         app:visibility='@{model.isProgressVisible ? View.VISIBLE : View.GONE}' // Example visibility binding
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see ProgressBar
 * @see ProteusView
 * @see Manager
 */
open class ProteusProgressBar : ProgressBar,
    ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusProgressBar}.
     *
     * This manager handles data binding, attribute processing, and overall management of this ProgressBar
     * within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusProgressBar}.
     *
     * Called when creating the ProgressBar programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusProgressBar} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusProgressBar} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusProgressBar} when inflating from XML with style attribute and resource (API Level 21+).
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusProgressBar}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this ProgressBar.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusProgressBar}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusProgressBar}.
     *
     * @return The Android {@link View} instance representing this ProgressBar.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusProgressBar instance as a View
}