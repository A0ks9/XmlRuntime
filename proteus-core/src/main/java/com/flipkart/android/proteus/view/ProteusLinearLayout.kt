package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link LinearLayout}, allowing LinearLayouts to be used in Proteus layouts.
 *
 * `ProteusLinearLayout` serves as a bridge to integrate standard Android {@link LinearLayout} widgets within
 * the Proteus layout framework. By extending `LinearLayout` and implementing `ProteusView`, it enables you to
 * incorporate linear layout containers into your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these layout components.
 *
 * It effectively makes standard Android LinearLayout components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.LinearLayout`:** Inherits all the layout capabilities, orientation modes (horizontal/vertical),
 *     and functionalities of a native Android LinearLayout.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like orientation, gravity, weights),
 *     and other Proteus-specific management tasks for this LinearLayout.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link LinearLayout} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusLinearLayout` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusLinearLayout` in your Proteus layouts as the foundation for arranging child views in a linear fashion,
 * either horizontally or vertically. This is the most basic and fundamental layout container for structuring UI elements
 * in a line within a Proteus-driven user interface. You can leverage Proteus data binding to dynamically set layout
 * orientation, gravity, weights of child views, background, padding, and other layout attributes.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <com.flipkart.android.proteus.view.ProteusLinearLayout // Using ProteusLinearLayout (adjust package name)
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/linearLayoutContainer"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:orientation='@{model.layoutOrientation}' // Example data-binding for layout orientation (horizontal/vertical)
 *     android:gravity="center_vertical">
 *
 *     <!-- Child views arranged linearly will go here -->
 *     <TextView
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text="Item 1"/>
 *
 *     <TextView
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text="Item 2"/>
 *
 * </com.flipkart.android.proteus.view.ProteusLinearLayout>
 * ```
 *
 * @see LinearLayout
 * @see ProteusView
 * @see Manager
 */
open class ProteusLinearLayout : LinearLayout,
    ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusLinearLayout}.
     *
     * This manager is responsible for handling data binding, attribute processing, and overall layout management
     * of this LinearLayout within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusLinearLayout}.
     *
     * Called when creating the LinearLayout programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusLinearLayout} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusLinearLayout} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusLinearLayout} when inflating from XML with style attribute and resource (API Level 21+).
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
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusLinearLayout}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this LinearLayout.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusLinearLayout}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusLinearLayout}.
     *
     * @return The Android {@link View} instance for this LinearLayout.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusLinearLayout instance as a View
}