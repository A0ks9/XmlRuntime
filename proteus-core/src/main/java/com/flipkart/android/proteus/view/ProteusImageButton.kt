package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link ImageButton}, enabling ImageButtons in Proteus layouts.
 *
 * `ProteusImageButton` acts as a bridge, allowing you to use standard Android {@link ImageButton} widgets within
 * the Proteus layout framework. By extending `ImageButton` and implementing `ProteusView`, it allows you to incorporate
 * interactive image-based buttons into your Proteus-driven UIs and benefit from Proteus features like data binding and
 * dynamic attribute updates for these button components.
 *
 * It makes standard Android ImageButton components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.ImageButton`:** Inherits all the visual representation (image display), button behaviors,
 *     and interaction functionalities of a native Android ImageButton.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like image resources, scale type),
 *     and other Proteus-specific management tasks for this ImageButton.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link ImageButton} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusImageButton` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusImageButton` in your Proteus layouts whenever you need to include interactive buttons that display images
 * instead of (or in addition to) text. This is a common UI pattern for icon buttons, action buttons with visuals, and
 * more graphical interactive elements within your Proteus-based user interfaces. You can leverage Proteus's data binding
 * to dynamically set image resources, button styles, click listeners, and other attributes.
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
 *     <com.flipkart.android.proteus.view.ProteusImageButton // Using ProteusImageButton (adjust package name)
 *         android:id="@+id/myImageButton"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         app:srcCompat='@{model.buttonIcon}'   // Example data-binding for image resource
 *         android:onClick='@{buttonClickAction}' // Example action binding for click event
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatImageButton
 * @see ProteusView
 * @see Manager
 */
open class ProteusImageButton : AppCompatImageButton,
    ProteusView { // 'open' keyword allows subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusImageButton}.
     *
     * This manager handles data binding, attribute processing, and overall lifecycle management of this ImageButton
     * within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' as manager is set after constructor

    /**
     * Default constructor for {@link ProteusImageButton}.
     *
     * Called when creating the ImageButton programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusImageButton} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusImageButton} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusImageButton}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this ImageButton.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusImageButton}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusImageButton}.
     *
     * @return The Android {@link View} instance representing this ImageButton.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusImageButton instance as a View
}