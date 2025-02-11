package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link ImageView}, enabling ImageViews in Proteus layouts.
 *
 * `ProteusImageView` acts as a bridge to incorporate standard Android {@link ImageView} widgets within
 * the Proteus layout framework. By extending `ImageView` and implementing `ProteusView`, it allows you to use
 * image display components in your Proteus-driven UIs and benefit from Proteus features like data binding and
 * dynamic attribute updates for these image views.
 *
 * It makes standard Android ImageView components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.ImageView`:** Inherits all the image display capabilities, scaling, and functionalities
 *     of a native Android ImageView.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like image resources, scale type, etc.),
 *     and other Proteus-specific management tasks for this ImageView.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link ImageView} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusImageView` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusImageView` in your Proteus layouts whenever you need to display images. This is the standard way to include
 * image components within a Proteus-driven UI. You can leverage Proteus data binding to dynamically set image sources
 * (drawables, URLs), scale types, and other visual attributes of your images.
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
 *     android:orientation="horizontal">
 *
 *     <com.flipkart.android.proteus.view.ProteusImageView // Using ProteusImageView (adjust package name)
 *         android:id="@+id/myImageView"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         app:srcCompat='@{model.imageUrl}'    // Example data-binding for image URL or drawable resource
 *         app:scaleType="centerCrop"            // Example setting of scale type via attribute
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatImageView
 * @see ProteusView
 * @see Manager
 */
open class ProteusImageView : AppCompatImageView,
    ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusImageView}.
     *
     * This manager is responsible for handling data binding, attribute processing, and overall management of this ImageView
     * within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusImageView}.
     *
     * Called when creating the ImageView programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusImageView} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusImageView} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusImageView}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this ImageView.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusImageView}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusImageView}.
     *
     * @return The Android {@link View} instance representing this ImageView.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusImageView instance as a View
}