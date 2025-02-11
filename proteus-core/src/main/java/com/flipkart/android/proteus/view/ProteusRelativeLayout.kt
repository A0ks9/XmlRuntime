package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link RelativeLayout}, enabling RelativeLayouts to be used in Proteus layouts.
 *
 * `ProteusRelativeLayout` acts as a bridge to integrate standard Android {@link RelativeLayout} widgets within
 * the Proteus layout framework. By extending `RelativeLayout` and implementing `ProteusView`, it enables you to
 * incorporate relative layout containers into your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these layout components.
 *
 * It effectively makes standard Android RelativeLayout components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.RelativeLayout`:** Inherits all the flexible layout positioning capabilities and
 *     functionalities of a native Android RelativeLayout.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like layout rules, gravity, etc.),
 *     and other Proteus-specific management tasks for this RelativeLayout.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link RelativeLayout} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusRelativeLayout` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusRelativeLayout` in your Proteus layouts whenever you need to position child views relative to each other or
 * to the parent layout boundaries. RelativeLayout is highly versatile for creating complex and overlapping layouts. You
 * can leverage Proteus data binding to dynamically control layout rules, element visibility, background, padding, and
 * other attributes of the `ProteusRelativeLayout` and its children within the Proteus framework.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <com.flipkart.android.proteus.view.ProteusRelativeLayout // Using ProteusRelativeLayout (adjust package name)
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/relativeLayoutContainer"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:background="@color/light_gray">
 *
 *     <!-- Child views positioned relatively will go here -->
 *     <TextView
 *         android:id="@+id/textView1"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text="Text 1"
 *         android:layout_alignParentTop="true"
 *         android:layout_alignParentStart="true" />
 *
 *     <ImageView
 *         android:id="@+id/imageView1"
 *         android:layout_width="50dp"
 *         android:layout_height="50dp"
 *         android:src="@drawable/icon"
 *         android:layout_below="@+id/textView1"
 *         android:layout_marginStart="16dp" />
 *
 * </com.flipkart.android.proteus.view.ProteusRelativeLayout>
 * ```
 *
 * @see RelativeLayout
 * @see ProteusView
 * @see Manager
 */
open class ProteusRelativeLayout : RelativeLayout,
    ProteusView { // 'open' allows for subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusRelativeLayout}.
     *
     * This manager handles data binding, attribute processing, and overall layout management within the Proteus framework.
     * It is set by the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' - manager is set after constructor

    /**
     * Default constructor for {@link ProteusRelativeLayout}.
     *
     * Called when creating the RelativeLayout programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusRelativeLayout} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusRelativeLayout} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusRelativeLayout} when inflating from XML with style attribute and resource (API Level 21+).
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
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusRelativeLayout}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this RelativeLayout.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusRelativeLayout}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusRelativeLayout}.
     *
     * @return The Android {@link View} instance representing this RelativeLayout.
     */
    override val asView: View
        get() = this // Kotlin getter for 'asView' property, returning 'this'
}