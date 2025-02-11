package com.flipkart.android.proteus.parser.custom

import android.view.ViewGroup
import android.webkit.WebView
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.processor.StringAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.view.ProteusWebView

/**
 * Kotlin implementation of WebViewParser, responsible for creating and configuring WebView views.
 *
 * This class extends ViewTypeParser and specializes in handling "WebView" view types within the Proteus framework.
 * It defines how WebView views are created, their type, parent type, and handles specific attributes like
 * `url` and `html` for loading web content.
 *
 * @param T The type of WebView view this parser handles, must be a subclass of WebView.
 *           In the context of Proteus, this is likely `ProteusWebView`.
 */
class WebViewParser<T : WebView> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser

    /**
     * Returns the type of view this parser is responsible for, which is "WebView".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "WebView", representing the view type.
     */
    override fun getType(): String =
        "WebView" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the WebView view, which is "View".
     * This indicates that WebView inherits properties and behaviors from View in the Proteus framework.
     *
     * @return The string "View", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "View".
     */
    override fun getParentType(): String? =
        "View" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of the WebView view (`ProteusWebView`) within the Proteus framework.
     *
     * This method is responsible for instantiating the actual WebView view object that will be used in the UI.
     * It takes the ProteusContext, Layout information, data for binding, the parent ViewGroup, and data index as parameters.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the WebView view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a ProteusWebView in this case.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusWebView(context) // Creates and returns a new ProteusWebView instance using expression body

    /**
     * Overrides the `addAttributeProcessors` method to define attribute processors specific to WebView.
     * This method registers processors for handling attributes like `url` and `html`.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'url' attribute (String - URL to load) - using lambda
        addAttributeProcessor(
            Attributes.WebView.Url,
            StringAttributeProcessor { view, value -> view.loadUrl(value) }) // Lambda for setting URL using loadUrl

        // Attribute processor for 'html' attribute (String - HTML content to load) - using lambda
        addAttributeProcessor(Attributes.WebView.HTML, StringAttributeProcessor { view, value ->
            view.loadData(
                value, "text/html", "UTF-8"
            )
        }) // Lambda for setting HTML content using loadData
    }
}