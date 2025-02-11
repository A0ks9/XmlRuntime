package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link EditText}, allowing EditTexts to be used in Proteus layouts.
 *
 * `ProteusEditText` bridges the gap between standard Android {@link EditText} widgets and the Proteus layout framework.
 * By extending `EditText` and implementing `ProteusView`, it allows you to seamlessly incorporate native Android
 * EditTexts into your Proteus-driven UI and leverage Proteus's features like data binding and attribute processing for input fields.
 *
 * It makes standard Android EditText components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.widget.EditText`:** Inherits all the standard text input capabilities, styling options,
 *     and user interaction behaviors of a native Android EditText.
 * *   **Implements `ProteusView`:**  Enables seamless integration within the Proteus layout inflation and management system.
 * *   **Manages `ViewManager`:**  Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     responsible for handling data binding, dynamic attribute updates, and other management tasks for this EditText
 *     within the Proteus environment.
 * *   **Standard Constructors:**  Provides constructors that mirror those of {@link EditText} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:**  Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusEditText` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusEditText` in your Proteus layouts whenever you need to include text input fields. This is the primary way
 * to use Android's built-in EditText functionality within a Proteus UI.  You can then utilize Proteus data binding to
 * dynamically set and retrieve text, manage input types, styles, hint text, and more for your input fields.
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
 *     <com.flipkart.android.proteus.view.ProteusEditText  // Using ProteusEditText (adjust package name)
 *         android:id="@+id/myEditText"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         android:hint='@{model.inputHint}'     // Example data-binding for hint text
 *         android:text='@{model.inputText}'    // Example data-binding to set/get text
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * @see AppCompatEditText
 * @see ProteusView
 * @see Manager
 */
open class ProteusEditText : AppCompatEditText,
    ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} instance associated with this {@link ProteusEditText}.
     *
     * This manager is responsible for handling data updates, attribute processing, and overall lifecycle management
     * of this EditText within the Proteus framework. It's set up during Proteus layout inflation.
     */
    lateinit var proteusEditTextViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusEditText}.
     *
     * Called when creating the EditText programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusEditText} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusEditText} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusEditText}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this EditText.
     */
    override val viewManager: Manager = proteusEditTextViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusEditText}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusEditTextViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusEditText}.
     *
     * @return The Android {@link View} instance for this EditText.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusEditText instance as a View
}