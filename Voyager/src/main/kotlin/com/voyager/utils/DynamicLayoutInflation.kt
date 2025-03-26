/**
 * Efficient dynamic layout inflation utility for Android.
 *
 * This utility provides optimized inflation of layouts from JSON or XML resources,
 * with a focus on performance and memory efficiency.
 *
 * Key features:
 * - Efficient layout parsing
 * - Memory-optimized view creation
 * - Support for JSON and XML layouts
 * - Thread-safe operations
 * - Comprehensive attribute handling
 *
 * Performance optimizations:
 * - Cached view creation
 * - Optimized attribute processing
 * - Minimized object creation
 * - Efficient view hierarchy building
 * - Reduced memory allocations
 *
 * Usage example:
 * ```kotlin
 * // Inflate from JSON
 * DynamicLayoutInflation.inflate(context, theme, jsonUri, parent) { view ->
 *     // Handle inflated view
 * }
 *
 * // Inflate from XML
 * DynamicLayoutInflation.inflate(context, theme, xmlUri, parent) { view ->
 *     // Handle inflated view
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.collection.ArrayMap
import com.voyager.data.models.ViewNode
import com.voyager.data.models.ViewNodeParser.fromJson
import com.voyager.utils.FileHelper.getFileExtension
import com.voyager.utils.FileHelper.parseXML
import com.voyager.utils.Utils.getGeneratedViewInfo
import com.voyager.utils.Utils.parseJsonToViewNode
import com.voyager.utils.processors.AttributeProcessor
import com.voyager.utils.processors.ViewProcessor.Companion.createViewByType
import com.voyager.utils.view.BaseViewAttributes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Constants for logging
private const val TAG = "DynamicLayoutInflation"
private const val JSON_EXTENSION = "json"
private const val XML_EXTENSION = "xml"

object DynamicLayoutInflation {
    // Thread-safe view node storage
    @Volatile
    internal var viewNode: ViewNode? = null

    // Initialization block for attribute registration
    init {
        BaseViewAttributes.initializeAttributes()
    }

    /**
     * Inflates a layout from a JSON or XML resource URI.
     *
     * @param context The context used to access resources
     * @param theme The theme resource ID
     * @param layoutUri The URI of the JSON or XML resource
     * @param parent The parent ViewGroup that will contain the inflated views
     * @param callback Optional callback to handle the inflated view
     */
    @JvmStatic
    fun inflate(
        context: Context,
        theme: Int,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)? = null,
    ) {
        try {
            getFileExtension(context, layoutUri) { extension ->
                CoroutineScope(Dispatchers.Main).launch {
                    val contextThemeWrapper = ContextThemeWrapper(context, theme)
                    when (extension.lowercase()) {
                        JSON_EXTENSION -> inflateJson(
                            contextThemeWrapper, layoutUri, parent, callback
                        )

                        XML_EXTENSION -> inflateXml(
                            contextThemeWrapper, layoutUri, parent, callback
                        )

                        else -> callback?.invoke(null)
                    }
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, "Error inflating layout", error)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a layout from a JSON resource.
     *
     * @param context The themed context
     * @param layoutUri The URI of the JSON resource
     * @param parent The parent ViewGroup
     * @param callback Optional callback to handle the inflated view
     */
    private suspend fun inflateJson(
        context: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
            val node = parseJsonToViewNode(inputStream, context.getActivityName()) ?: return
            viewNode = node
            val view = createView(context, node, parent)
            callback?.invoke(view)
        }
    }

    /**
     * Inflates a layout from an XML resource.
     *
     * @param context The themed context
     * @param layoutUri The URI of the XML resource
     * @param parent The parent ViewGroup
     * @param callback Optional callback to handle the inflated view
     */
    private suspend fun inflateXml(
        context: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
            val node = fromJson(parseXML(inputStream)) ?: return
            viewNode = node
            val view = createView(context, node, parent)
            callback?.invoke(view)
        }
    }

    /**
     * Inflates a view hierarchy from a ViewNode definition.
     *
     * @param context The themed context
     * @param layout The ViewNode defining the layout
     * @param parent The parent ViewGroup
     * @param callback Optional callback to handle the inflated view
     */
    internal fun inflate(
        context: ContextThemeWrapper,
        layout: ViewNode,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)? = null,
    ) {
        try {
            viewNode = layout
            val view = createView(context, layout, parent)
            callback?.invoke(view)
        } catch (error: Exception) {
            Log.e(TAG, "Error inflating view hierarchy", error)
            callback?.invoke(null)
        }
    }

    /**
     * Creates a view from a ViewNode definition.
     *
     * @param context The themed context
     * @param layout The ViewNode defining the view
     * @param parent The parent ViewGroup
     * @return The created view
     */
    private fun createView(
        context: ContextThemeWrapper,
        layout: ViewNode,
        parent: ViewGroup?,
    ): View? = createViewByType(context, layout.type).apply {
        parent?.addView(this)
        applyAttributes(this, layout.attributes)
        if (this is ViewGroup && layout.children.isNotEmpty()) {
            parseChildren(context, layout.children, this)
        }
    }

    /**
     * Parses and creates child views.
     *
     * @param context The themed context
     * @param children The list of child ViewNodes
     * @param parent The parent ViewGroup
     */
    private fun parseChildren(
        context: ContextThemeWrapper,
        children: List<ViewNode>,
        parent: ViewGroup,
    ) {
        children.forEach { childNode ->
            createView(context, childNode, parent)
        }
    }

    /**
     * Applies attributes to a view.
     *
     * @param view The view to apply attributes to
     * @param attributes The map of attributes
     */
    private fun applyAttributes(
        view: View,
        attributes: ArrayMap<String, String>,
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Applying attributes to ${view.javaClass.simpleName}")
        }
        AttributeProcessor.applyAttributes(view, attributes)
    }

    /**
     * Sets a delegate object for a view.
     *
     * @param view The view to set the delegate for
     * @param delegate The delegate object
     */
    @JvmStatic
    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}