package com.flipkart.android.proteus.view

import android.content.Context
import android.view.View
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager
import com.flipkart.android.proteus.view.custom.HorizontalProgressBar

/**
 * A {@link ProteusView} implementation of {@link HorizontalProgressBar}, allowing HorizontalProgressBars in Proteus layouts.
 *
 * `ProteusHorizontalProgressBar` acts as an adapter to use the custom {@link HorizontalProgressBar} component within
 * the Proteus framework. By extending `HorizontalProgressBar` and implementing `ProteusView`, it enables you to
 * incorporate this specialized progress bar (which is pre-styled for horizontal orientation) into your Proteus-driven UIs
 * and leverage Proteus features like data binding and dynamic attribute updates.
 *
 * It effectively makes `HorizontalProgressBar` a fully manageable and integratable component within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `com.flipkart.android.proteus.view.custom.HorizontalProgressBar`:**  Inherits the horizontal progress bar
 *     style and default behavior from the base `HorizontalProgressBar` class.
 * *   **Implements `ProteusView`:**  Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} (`viewManager`) which is
 *     responsible for handling data binding, dynamic attribute updates, and Proteus-specific management for this progress bar.
 * *   **Simple Constructor:** Provides a basic constructor that takes a {@link Context}, aligning with typical custom view constructors.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView} interface
 *     to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to get the underlying Android {@link View} instance (which is 'this' instance).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusHorizontalProgressBar` within your Proteus layouts whenever you need to display a horizontal progress bar.
 * This class provides a straightforward way to integrate the pre-styled `HorizontalProgressBar` into your Proteus UI designs
 * and benefit from Proteus's dynamic UI capabilities for this type of progress indicator.
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
 *     <com.flipkart.android.proteus.view.ProteusHorizontalProgressBar // Using ProteusHorizontalProgressBar (adjust package)
 *         android:id="@+id/horizontalProgressBar"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         android:max='@{model.maxProgress}'         // Example data-binding for maximum progress
 *         app:progress='@{model.currentProgress}'   // Example data-binding for current progress
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see HorizontalProgressBar
 * @see ProteusView
 * @see Manager
 */
open class ProteusHorizontalProgressBar : HorizontalProgressBar,
    ProteusView { // 'open' for potential subclassing

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusHorizontalProgressBar}.
     *
     * This manager handles data binding, attribute updates, and overall lifecycle management for this progress bar
     * within the Proteus framework.
     */
    lateinit var proteusViewManager: Manager // 'late init var' as it's set after constructor

    /**
     * Constructor for {@link ProteusHorizontalProgressBar}.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusHorizontalProgressBar}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this view.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusHorizontalProgressBar}.
     *
     * Called by the Proteus framework to associate a manager with this view, enabling data binding and attribute processing.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance (which is 'this' {@link ProteusHorizontalProgressBar} itself).
     *
     * @return The Android {@link View} instance.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' instance of ProteusHorizontalProgressBar as a View
}