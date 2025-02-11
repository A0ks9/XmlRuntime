package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link HorizontalScrollView}, allowing horizontal scrolling views in Proteus layouts.
 *
 * `ProteusHorizontalScrollView` serves as a bridge to use the standard Android {@link HorizontalScrollView} within
 * the Proteus layout framework. By extending `HorizontalScrollView` and implementing `ProteusView`, it enables you to
 * incorporate horizontal scrolling containers into your Proteus-driven user interfaces and leverage Proteus features
 * like data binding and dynamic attribute updates for these scrollable views.
 *
 * It effectively makes `HorizontalScrollView` a fully manageable and integratable component within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.HorizontalScrollView`:** Inherits the horizontal scrolling behavior and functionalities
 *     of a native Android HorizontalScrollView.
 * *   **Implements `ProteusView`:**  Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} (`viewManager`) which is
 *     responsible for handling data binding, dynamic attribute updates, and Proteus-specific management for this scroll view.
 * *   **Standard Constructors:** Provides constructors to support instantiation when inflated from XML layouts or when
 *     created directly in code, mirroring the constructors of `HorizontalScrollView`.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView} interface
 *     to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to get the underlying Android {@link View} instance (which is 'this' instance).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusHorizontalScrollView` within your Proteus layouts whenever you need to provide horizontal scrolling capability
 * for content. This is common for horizontally scrollable lists, carousels, or layouts that exceed the screen width. You can
 * then utilize Proteus data binding to control attributes of the `HorizontalScrollView` itself (like background, padding) or
 * its children managed within the scrollable area.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <com.flipkart.android.proteus.view.ProteusHorizontalScrollView // Using ProteusHorizontalScrollView (adjust package)
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/horizontalScrollView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:scrollbars="horizontal" >  <!-- Example: enabling horizontal scrollbars -->
 *
 *     <LinearLayout
 *         android:layout_width="wrap_content" <!-- Ensure content width is wider than screen -->
 *         android:layout_height="wrap_content"
 *         android:orientation="horizontal">
 *
 *         <!-- Horizontally scrollable content here, e.g., a row of ImageViews or TextViews -->
 *
 *     </LinearLayout>
 *
 * </com.flipkart.android.proteus.view.ProteusHorizontalScrollView>
 * ```
 *
 * @see HorizontalScrollView
 * @see ProteusView
 * @see Manager
 */
open class ProteusHorizontalScrollView : HorizontalScrollView,
    ProteusView { // 'open' allows subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusHorizontalScrollView}.
     *
     * This manager handles data binding, attribute updates, and overall lifecycle management for this horizontal scroll view
     * within the Proteus framework. It's set up by the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init var' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusHorizontalScrollView}.
     *
     * Called when creating the horizontal scroll view programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusHorizontalScrollView} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusHorizontalScrollView} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusHorizontalScrollView} when inflating from XML with style attribute and resource (API Level 21+).
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
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusHorizontalScrollView}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this horizontal scroll view.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusHorizontalScrollView}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusHorizontalScrollView}.
     *
     * @return The Android {@link View} instance for this horizontal scroll view.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' instance of ProteusHorizontalScrollView as a View
}