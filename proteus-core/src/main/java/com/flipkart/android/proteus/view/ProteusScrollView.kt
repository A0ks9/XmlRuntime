package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link ScrollView}, allowing ScrollViews to be used in Proteus layouts.
 *
 * `ProteusScrollView` acts as a bridge to incorporate standard Android {@link ScrollView} widgets within
 * the Proteus layout framework. By extending `ScrollView` and implementing `ProteusView`, it enables you to
 * use vertical scrolling containers in your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these scrollable view components.
 *
 * It effectively makes standard Android ScrollView components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.ScrollView`:** Inherits the vertical scrolling behavior and functionalities
 *     of a native Android ScrollView.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like scroll bar visibility, background),
 *     and other Proteus-specific management tasks for this ScrollView.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link ScrollView} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusScrollView` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusScrollView` within your Proteus layouts whenever you need to provide vertical scrolling capability for
 * content that might exceed the screen height. This is a fundamental layout component for displaying long content (like
 * text, lists of items) within a limited vertical screen space in a Proteus-driven user interface. You can leverage
 * Proteus data binding to control attributes of the `ProteusScrollView` itself or its children managed within the
 * scrollable area.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <com.flipkart.android.proteus.view.ProteusScrollView // Using ProteusScrollView (adjust package name)
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/scrollViewContainer"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:scrollbars="vertical">  <!-- Example: enabling vertical scrollbars -->
 *
 *     <LinearLayout
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content" <!-- Content height will determine scrollable area -->
 *         android:orientation="vertical">
 *
 *         <!-- Vertically scrollable content here, e.g., a long TextView, or a vertical LinearLayout -->
 *
 *     </LinearLayout>
 *
 * </com.flipkart.android.proteus.view.ProteusScrollView>
 * ```
 *
 * @see ScrollView
 * @see ProteusView
 * @see Manager
 */
open class ProteusScrollView : ScrollView, ProteusView { // 'open' allows subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusScrollView}.
     *
     * This manager handles data binding, attribute updates, and overall lifecycle management of this ScrollView
     * within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusScrollView}.
     *
     * Called when creating the ScrollView programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusScrollView} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusScrollView} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusScrollView} when inflating from XML with style attribute and resource (API Level 21+).
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
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusScrollView}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this ScrollView.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusScrollView}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusScrollView}.
     *
     * @return The Android {@link View} instance for this ScrollView.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusScrollView instance as a View
}