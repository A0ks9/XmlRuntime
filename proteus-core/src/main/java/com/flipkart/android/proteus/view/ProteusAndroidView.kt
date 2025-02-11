package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.flipkart.android.proteus.ProteusView

/**
 * A basic implementation of {@link ProteusView} using a standard Android {@link View}.
 *
 * This class, `ProteusAndroidView`, is designed to be a simple bridge between the Proteus layout system
 * and a native Android `View`. It acts as a fundamental building block when you need to incorporate
 * standard Android `View` components within a Proteus-driven UI.
 *
 * It extends `android.view.View` and implements the `ProteusView` interface, fulfilling the requirements
 * to be managed by the Proteus framework. It provides the necessary methods to associate a
 * {@link com.google.android.flexbox.ProteusView.Manager} with it, which is crucial for data binding
 * and attribute handling within Proteus.
 *
 * **Key features:**
 *
 * *   **Extends `android.view.View`:**  Inherits all the standard behaviors and functionalities of a native Android `View`.
 * *   **Implements `ProteusView`:**  Makes it compatible with the Proteus layout inflation and management system.
 * *   **Manages `ViewManager`:**  Holds a reference to a `ProteusView.Manager` (`viewManager`), which is responsible for handling data updates, attribute processing, and other management tasks for this view within Proteus.
 * *   **Constructors for different scenarios:** Provides constructors to be properly instantiated in various Android layout inflation scenarios (from code, from XML with or without style attributes/resources).
 * *   **`viewManager` and `setViewManager()`:**  Implements the {@link ProteusView} interface methods to get and set the associated `ViewManager`.
 * *   **`asView`:** Implements the {@link ProteusView} interface method to return the underlying Android {@link View} (which is `this` itself in this case).
 *
 * **Usage Scenario:**
 *
 * Use `ProteusAndroidView` when you need to directly include a standard, un-customized Android `View`
 * (like a basic `View` for drawing backgrounds, handling generic touch events, or as a simple container)
 * into a Proteus layout structure.  It allows you to apply Proteus data binding and attribute processing
 * to this fundamental Android View component.
 *
 * For more specialized UI components (like TextViews, ImageViews, Custom Views with specific logic),
 * you would typically use other concrete `ProteusView` implementations or create your own that
 * extend from a more suitable Android View base class (e.g., create a `ProteusTextView` extending `TextView`).
 *
 * @see ProteusView
 * @see ProteusView.Manager
 */
open class ProteusAndroidView : View, ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusAndroidView}.
     *
     * This manager is responsible for handling data updates, attribute processing, and other
     * management tasks specific to this view within the Proteus framework.
     * It is set via {@link #setViewManager(Manager)} and retrieved by {@link #getViewManager()}.
     */
    lateinit var proteusAndroidViewManager: ProteusView.Manager // 'late init var' as it's set after constructor

    /**
     * Default constructor for {@link ProteusAndroidView}.
     *
     * Called when creating the view programmatically or inflating from XML without attributes.
     *
     * @param context The {@link Context} in which the view is created.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusAndroidView} when inflating from XML.
     *
     * Called when the view is created from an XML layout file.
     *
     * @param context The {@link Context} in which the view is created.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusAndroidView} when inflating from XML with a style attribute.
     *
     * Called when the view is created from XML and is being styled with a style attribute.
     *
     * @param context      The {@link Context} in which the view is created.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusAndroidView} with a style attribute and style resource.
     *
     * Called when the view is inflated from XML and is being styled using both a style attribute and a style resource.
     * This is available on API Level 21 (LOLLIPOP) and above.
     *
     * @param context      The {@link Context} in which the view is created.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
     */
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(
        context, attrs, defStyleAttr, defStyleRes
    )

    /**
     * Gets the {@link ProteusView.Manager} associated with this view.
     *
     * @return The {@link ProteusView.Manager} for this view.
     */
    override val viewManager: ProteusView.Manager = proteusAndroidViewManager


    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this view.
     *
     * This is called by the Proteus framework during the view inflation process to associate a manager
     * that will handle data updates and attribute processing for this view.
     *
     * @param manager The {@link Manager} to be associated with this view.
     */
    override fun setViewManager(manager: ProteusView.Manager) {
        this.proteusAndroidViewManager = manager
    }

    /**
     * Returns the Android {@link View} instance represented by this {@link ProteusView}.
     *
     * In the case of {@link ProteusAndroidView}, it simply returns `this` instance as it itself extends {@link View}.
     *
     * @return The Android {@link View} instance (which is 'this' for {@link ProteusAndroidView}).
     */
    override val asView: View
        get() = this // Kotlin getter for property 'asView'
}