package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link TextView}, allowing TextViews to be used in Proteus layouts.
 *
 * `ProteusTextView` serves as a bridge to incorporate standard Android {@link TextView} widgets within
 * the Proteus layout framework. By extending `TextView` and implementing `ProteusView`, it enables you to
 * use text display components in your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these text views.
 *
 * It effectively makes standard Android TextView components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.TextView`:** Inherits all the text display capabilities, styling options, and
 *     functionalities of a native Android TextView.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like text, textColor, textSize, etc.),
 *     and other Proteus-specific management tasks for this TextView.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link TextView} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusTextView` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusTextView` in your Proteus layouts whenever you need to display text. This is the fundamental way to include
 * text elements within a Proteus-driven UI. You can leverage Proteus data binding to dynamically set text content,
 * text styles, colors, sizes, and various other text-related attributes.
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
 *     <com.flipkart.android.proteus.view.ProteusTextView  // Using ProteusTextView (adjust package name)
 *         android:id="@+id/myTextView"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text='@{model.displayText}'      // Example data-binding for text content
 *         android:textColor='@{model.textColor}'   // Example data-binding for text color
 *         android:textSize='@{model.textSize}'     // Example data-binding for text size
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatTextView
 * @see ProteusView
 * @see Manager
 */
open class ProteusTextView : AppCompatTextView,
    ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusTextView}.
     *
     * This manager is responsible for handling data binding, attribute processing, and overall lifecycle management
     * of this TextView within the Proteus framework. It is set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' - manager is set after constructor

    /**
     * Default constructor for {@link ProteusTextView}.
     *
     * Called when creating the TextView programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusTextView} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusTextView} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusTextView}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this TextView.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusTextView}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusTextView}.
     *
     * @return The Android {@link View} instance representing this TextView.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusTextView instance as a View
}