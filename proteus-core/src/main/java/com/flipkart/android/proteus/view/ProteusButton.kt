package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link Button}, allowing Buttons to be used in Proteus layouts.
 *
 * This class, `ProteusButton`, extends the standard Android {@link Button} and implements the {@link ProteusView}
 * interface. This integration enables you to use native Android Buttons within your Proteus-based UI designs,
 * benefiting from Proteus's powerful features like data binding and dynamic attribute updates for standard Buttons.
 *
 * It acts as a bridge, making it possible to treat Android Buttons as fully manageable components within the
 * Proteus framework.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.Button`:**  Inherits all the standard visual appearance and interactive behavior
 *     of a native Android Button.
 * *   **Implements `ProteusView`:**  Allows integration into the Proteus layout inflation and management pipeline.
 * *   **Manages `ViewManager`:**  Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     to handle data binding, attribute updates, and other management aspects within the Proteus environment.
 * *   **Standard Constructors:**  Provides constructors to be correctly instantiated when inflated from XML layouts
 *     or created programmatically in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:**  Implements the {@link ProteusView} interface methods to manage
 *     the associated {@link com.google.android.flexbox.ProteusView.Manager} instance.
 * *   **`getAsView()`:**  Provides a way to get the underlying Android {@link View} (which is 'this' Button instance itself).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusButton` in your Proteus layouts whenever you need to include interactive button elements. This is the
 * most common and direct way to use Android's built-in button functionality within a Proteus-driven UI. You can then
 * use Proteus data binding to dynamically set button text, styles, click listeners, and other attributes.
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
 *     <com.flipkart.android.proteus.view.ProteusButton  // Using ProteusButton (adjust package name)
 *         android:id="@+id/myButton"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text='@{model.buttonText}'    // Example data-binding for button text
 *         android:onClick='@{clickAction}'     // Example action binding for click event
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatButton
 * @see ProteusView
 * @see Manager
 */
open class ProteusButton : AppCompatButton,
    ProteusView { // 'open' keyword allows subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusButton}.
     *
     * This manager handles data updates, attribute processing, and overall management within the Proteus framework.
     * It is set during the Proteus layout inflation process via {@link #setViewManager(Manager)} and accessed by {@link #getViewManager()}.
     */
    lateinit var proteusButtonViewManager: Manager // 'late init var' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusButton}.
     *
     * Called when creating the button programmatically in code.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusButton} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusButton} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusButton}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this button.
     */
    override val viewManager: Manager = proteusButtonViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusButton}.
     *
     * This is called by the Proteus framework during the layout inflation process to associate a manager for this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusButtonViewManager = manager
    }

    /**
     * Returns the Android {@link View} instance, which is 'this' {@link ProteusButton} itself.
     *
     * @return The Android {@link View} instance representing this button.
     */
    override val asView: View
        get() = this // Kotlin getter for the 'asView' property, returning 'this'
}