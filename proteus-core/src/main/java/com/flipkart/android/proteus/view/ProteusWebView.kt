package com.flipkart.android.proteus.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ProteusView.Manager

/**
 * A {@link ProteusView} implementation of {@link WebView}, allowing WebViews to be used in Proteus layouts.
 *
 * `ProteusWebView` acts as a bridge to incorporate standard Android {@link WebView} widgets within
 * the Proteus layout framework. By extending `WebView` and implementing `ProteusView`, it enables you to
 * use web content display components in your Proteus-driven UIs and leverage Proteus features like data binding and
 * dynamic attribute updates for these web views.
 *
 * It effectively makes standard Android WebView components fully manageable within the Proteus ecosystem.
 *
 * **Key Features:**
 *
 * *   **Extends `android.webkit.WebView`:** Inherits all the web browsing and content display capabilities and
 *     functionalities of a native Android WebView.
 * *   **Implements `ProteusView`:** Enables seamless integration into the Proteus layout inflation and view management system.
 * *   **Manages `ViewManager`:** Holds a {@link com.google.android.flexbox.ProteusView.Manager} instance (`viewManager`)
 *     that is responsible for handling data binding, dynamic attribute updates (like URL loading, settings adjustments),
 *     and other Proteus-specific management tasks for this WebView.
 * *   **Standard Constructors:** Provides constructors mirroring those of {@link WebView} to ensure correct
 *     instantiation when created programmatically or inflated from XML layouts in various scenarios.
 * *   **`getViewManager()` and `setViewManager()`:** Implements the required methods from the {@link ProteusView}
 *     interface to get and set the associated {@link com.google.android.flexbox.ProteusView.Manager}.
 * *   **`getAsView()`:** Provides a way to retrieve the underlying Android {@link View} instance, which is 'this'
 *     `ProteusWebView` itself.
 *
 * **Usage Scenario:**
 *
 * Use `ProteusWebView` in your Proteus layouts whenever you need to display web content, load web pages, or integrate
 * web-based elements into your user interface. This component is essential for scenarios like displaying help content,
 * embedded web applications, dynamic online dashboards, and more within a Proteus-driven UI. You can leverage Proteus
 * data binding to dynamically load URLs, configure WebView settings, handle JavaScript interaction (with appropriate
 * security considerations), and control other aspects of the WebView's behavior and appearance.
 *
 * **Example (Conceptual - in a Proteus layout definition):**
 *
 * ```xml
 * <!-- Example XML layout for Proteus -->
 * <LinearLayout
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:orientation="vertical">
 *
 *     <com.flipkart.android.proteus.view.ProteusWebView  // Using ProteusWebView (adjust package name)
 *         android:id="@+id/myWebView"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"
 *         app:loadUrl='@{model.webPageUrl}'    // Example data-binding for loading a URL
 *         app:javaScriptEnabled='@{model.isJavaScriptEnabled}' // Example for enabling JavaScript (with caution)
 *         />
 *
 * </LinearLayout>
 * ```
 *
 * **Security Note:** When using `ProteusWebView` (or any WebView), be extremely cautious about loading content from
 * untrusted sources or enabling JavaScript if not absolutely necessary, as it can introduce security vulnerabilities.
 * Sanitize inputs and carefully manage interactions between your app's code and the loaded web content.
 *
 * @see WebView
 * @see ProteusView
 * @see Manager
 */
open class ProteusWebView : WebView, ProteusView { // 'open' for potential subclassing in Kotlin

    /**
     * The {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusWebView}.
     *
     * This manager is responsible for handling data binding, attribute processing, and overall lifecycle management
     * of this WebView within the Proteus framework. It's set during the Proteus layout inflation process.
     */
    lateinit var proteusViewManager: Manager // 'late init' because manager is set after constructor

    /**
     * Default constructor for {@link ProteusWebView}.
     *
     * Called when creating the WebView programmatically.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor for {@link ProteusWebView} when inflating from XML layouts.
     *
     * @param context The Android {@link Context}.
     * @param attrs   The attributes from the XML tag.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Constructor for {@link ProteusWebView} when inflating from XML with a style attribute.
     *
     * @param context      The Android {@link Context}.
     * @param attrs        The attributes from the XML tag.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    /**
     * Constructor for {@link ProteusWebView} when inflating from XML with style attribute and resource (API Level 21+).
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
     * Returns the {@link com.google.android.flexbox.ProteusView.Manager} associated with this {@link ProteusWebView}.
     *
     * @return The {@link com.google.android.flexbox.ProteusView.Manager} for this WebView.
     */
    override val viewManager: Manager = proteusViewManager

    /**
     * Sets the {@link com.google.android.flexbox.ProteusView.Manager} for this {@link ProteusWebView}.
     *
     * Called by the Proteus framework during layout inflation to associate a manager with this view.
     *
     * @param manager The {@link com.google.android.flexbox.ProteusView.Manager} to set.
     */
    override fun setViewManager(manager: Manager) {
        this.proteusViewManager = manager
    }

    /**
     * Returns the underlying Android {@link View} instance, which is 'this' {@link ProteusWebView}.
     *
     * @return The Android {@link View} instance representing this WebView.
     */
    override val asView: View
        get() = this // Kotlin getter, returns 'this' ProteusWebView instance as a View
}