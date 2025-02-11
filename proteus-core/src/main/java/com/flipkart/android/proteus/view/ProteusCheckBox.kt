package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link CheckBox}, enabling CheckBoxes within Proteus layouts.
 *
 * This class, `ProteusCheckBox`, extends the standard Android {@link CheckBox} and implements the {@link ProteusView}
 * interface. This makes it possible to use native Android CheckBoxes within a Proteus-driven user interface and leverage
 * Proteus features such as data binding and dynamic attribute updates for these standard checkbox components.
 *
 * It acts as a bridge to incorporate native Android CheckBox widgets into the Proteus framework seamlessly.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.CheckBox`:**  Inherits all the standard visual characteristics, states (checked/unchecked),
 *     and interactive behaviors of a native Android CheckBox.
 * *   **Implements `ProteusView`:**  Integrates smoothly with the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:**  Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, applying attribute changes, and overseeing the lifecycle of this CheckBox
 *     within the Proteus environment.
 * *   **Standard Constructors:**  Provides constructors mirroring those of {@link CheckBox}, allowing for proper instantiation
 *     when the `ProteusCheckBox` is created programmatically or inflated from XML in different contexts.
 * *   **`getViewManager()` and `setViewManager()`:**  Implements the required methods from the {@link ProteusView} interface
 *     to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:**  Provides a way to retrieve the underlying Android {@link View} instance, which in this case is
 *     the `ProteusCheckBox` object itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusCheckBox` whenever you need to incorporate a standard checkbox UI element into a Proteus-based layout.
 * This is the primary way to use Android's native checkbox functionality in a Proteus UI. You can then utilize Proteus's
 * data binding to control properties like the checked state, text, styles, and potentially set up actions based on checkbox state changes.
 *
 * **Example (Conceptual - within a Proteus layout definition):**
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
 *     <com.flipkart.android.proteus.view.ProteusCheckBox  // Using ProteusCheckBox (adjust package name)
 *         android:id="@+id/myCheckBox"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text='@{model.checkboxText}'   // Example data-binding for checkbox text
 *         app:isChecked='@{model.isChecked}'    // Example data-binding for checked state
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatCheckBox
 * @see ProteusView
 * @see Manager
 */
open class ProteusCheckBox : AppCompatCheckBox, ProteusView { // 'open' for potential subclassing

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} instance associated with this {@link ProteusCheckBox}.
     *
     * This manager is responsible for handling data binding, attribute updates, and overall management
     * of this CheckBox within the Proteus framework. It is set by the Proteus layout inflation process.
     */
    lateinit var proteusCheckBoxViewManager: Manager // 'late init var' as it will be set after constructor

    /**
     * Default constructor for {@link ProteusCheckBox}.
     *
     * Called when creating the checkbox programmatically in code.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusCheckBox} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusCheckBox} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusCheckBox}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this checkbox.
     */
    override val viewManager: Manager = proteusCheckBoxViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusCheckBox}.
     *
     * Called by the Proteus framework to associate a manager with this view, enabling data binding and attribute processing.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusCheckBoxViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusCheckBox} itself.
     *
     * @return The Android {@link View} instance representing this checkbox.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' instance of ProteusCheckBox as a View
}