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
import com.voyager.data.models.ConfigManager
import com.voyager.data.models.ViewNode
import com.voyager.data.models.ViewNodeParser
import com.voyager.utils.FileHelper.getFileExtension
import com.voyager.utils.FileHelper.parseXML
import com.voyager.utils.Utils.getGeneratedViewInfo
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
    /**
     * Stores the root [ViewNode] of the most recently inflated layout.
     * This property is thread-safe due to `@Volatile` annotation.
     * It's marked `internal` to allow access from within the Voyager module, primarily for
     * debugging or advanced state inspection.
     */
    @Volatile
    internal var viewNode: ViewNode? = null

    // Initialization block for attribute registration
    init {
        BaseViewAttributes.initializeAttributes()
    }

    /**
     * Inflates a layout asynchronously from a specified JSON or XML resource URI.
     *
     * The inflation process involves:
     * 1. Determining the file type (JSON or XML) from the [layoutUri].
     * 2. Launching a coroutine on [Dispatchers.Main] to perform inflation off the main thread initially,
     *    with view creation and manipulation happening on the Main thread.
     * 3. Wrapping the provided [context] with the specified [theme].
     * 4. Delegating to [inflateJson] or [inflateXml] based on the file type.
     * 5. Invoking the [callback] with the inflated [View] or `null` if inflation fails.
     *
     * Error handling is provided via a `try-catch` block, logging any exceptions and
     * invoking the [callback] with `null`.
     *
     * @param context The [Context] used for accessing resources and creating views.
     * @param theme The theme resource ID (e.g., `R.style.AppTheme`) to apply to the inflated layout.
     * @param layoutUri The [Uri] of the JSON or XML layout resource.
     * @param parent The optional parent [ViewGroup] to which the inflated view will be added.
     *                 If `null`, the view will be created but not attached to any parent.
     * @param callback An optional lambda function that will be invoked with the inflated [View]
     *                 (or `null` on failure) once the inflation process is complete.
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
                    processInflateRequest(context, theme, layoutUri, parent, extension, callback)
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, "Error initiating layout inflation", error)
            callback?.invoke(null) // Ensure callback is always invoked on error
        }
    }

    /**
     * Handles the core inflation logic after the file extension is determined.
     * This function is called within a coroutine.
     *
     * @param context Original context.
     * @param theme Theme resource ID.
     * @param layoutUri URI of the layout resource.
     * @param parent Optional parent ViewGroup.
     * @param extension File extension (json or xml).
     * @param callback Callback for the inflated view.
     */
    private fun processInflateRequest(
        context: Context,
        theme: Int,
        layoutUri: Uri,
        parent: ViewGroup?,
        extension: String,
        callback: ((View?) -> Unit)?,
    ) {
        val contextThemeWrapper = ContextThemeWrapper(context, theme)
        when (extension.lowercase()) {
            JSON_EXTENSION -> inflateJsonInternal(contextThemeWrapper, layoutUri, parent, callback)
            XML_EXTENSION -> inflateXmlInternal(contextThemeWrapper, layoutUri, parent, callback)
            else -> {
                Log.e(TAG, "Unsupported layout file extension: $extension")
                callback?.invoke(null)
            }
        }
    }

    /**
     * Inflates a layout from a JSON resource.
     * This is an internal implementation detail.
     *
     * @param contextWrapper The themed [ContextThemeWrapper] for inflation.
     * @param layoutUri The [Uri] of the JSON resource.
     * @param parent The optional parent [ViewGroup].
     * @param callback Optional callback to handle the inflated [View].
     */
    private fun inflateJsonInternal(
        contextWrapper: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        try {
            contextWrapper.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val parsedNode = ViewNodeParser.fromJson(jsonString)
                if (parsedNode == null) {
                    Log.e(TAG, "Failed to parse JSON from URI: $layoutUri")
                    callback?.invoke(null)
                    return
                }
                viewNode = parsedNode // Cache the root node
                val inflatedView = createViewHierarchy(contextWrapper, parsedNode, parent)
                callback?.invoke(inflatedView)
            } ?: {
                Log.e(TAG, "Could not open InputStream for URI: $layoutUri")
                callback?.invoke(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inflating JSON layout: $layoutUri", e)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a layout from an XML resource.
     * This is an internal implementation detail.
     *
     * @param contextWrapper The themed [ContextThemeWrapper] for inflation.
     * @param layoutUri The [Uri] of the XML resource.
     * @param parent The optional parent [ViewGroup].
     * @param callback Optional callback to handle the inflated [View].
     */
    private fun inflateXmlInternal(
        contextWrapper: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        try {
            contextWrapper.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                val jsonFromXml = parseXML(inputStream) // Native call
                if (jsonFromXml == null) {
                    Log.e(TAG, "Failed to parse XML to JSON from URI: $layoutUri")
                    callback?.invoke(null)
                    return
                }
                val parsedNode = ViewNodeParser.fromJson(jsonFromXml)
                if (parsedNode == null) {
                    Log.e(TAG, "Failed to parse JSON (from XML) for URI: $layoutUri")
                    callback?.invoke(null)
                    return
                }
                viewNode = parsedNode // Cache the root node
                val inflatedView = createViewHierarchy(contextWrapper, parsedNode, parent)
                callback?.invoke(inflatedView)
            } ?: {
                Log.e(TAG, "Could not open InputStream for XML URI: $layoutUri")
                callback?.invoke(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inflating XML layout: $layoutUri", e)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a view hierarchy directly from a [ViewNode] definition.
     * This is useful for scenarios where the [ViewNode] is already available (e.g., from cache or programmatic creation).
     *
     * @param contextWrapper The themed [ContextThemeWrapper] to use for creating views.
     * @param node The root [ViewNode] defining the layout to be inflated.
     * @param parent The optional parent [ViewGroup] to which the inflated view will be added.
     * @param callback An optional lambda function that will be invoked with the inflated [View]
     *                 (or `null` on failure) once the inflation process is complete.
     */
    internal fun inflateFromNode(
        contextWrapper: ContextThemeWrapper,
        node: ViewNode,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)? = null,
    ) {
        try {
            viewNode = node // Cache the root node
            val inflatedView = createViewHierarchy(contextWrapper, node, parent)
            callback?.invoke(inflatedView)
        } catch (error: Exception) {
            Log.e(TAG, "Error inflating view hierarchy from ViewNode", error)
            callback?.invoke(null)
        }
    }

    /**
     * Recursively creates a [View] hierarchy from a given [ViewNode].
     *
     * This function performs the following steps:
     * 1. Creates a [View] instance based on the `layout.type` using [ViewProcessor.createViewByType].
     * 2. Adds the created view to the [parent] [ViewGroup], if provided.
     * 3. Applies attributes to the view using [applyAttributesToView].
     * 4. If the created view is a [ViewGroup] and the [layout] has children,
     *    it recursively calls itself for each child [ViewNode] to build the hierarchy.
     *
     * @param contextWrapper The themed [ContextThemeWrapper] for creating views.
     * @param layout The [ViewNode] defining the current view to be created.
     * @param parent The optional parent [ViewGroup] for the new view.
     * @return The root [View] of the created hierarchy, or `null` if creation failed.
     */
    private fun createViewHierarchy(
        contextWrapper: ContextThemeWrapper,
        layout: ViewNode,
        parent: ViewGroup?,
    ): View? {
        val view = ViewProcessor.createViewByType(contextWrapper, layout.type)
        parent?.addView(view)
        applyAttributesToView(view, layout.attributes)

        if (view is ViewGroup && layout.children.isNotEmpty()) {
            for (childNode in layout.children) {
                createViewHierarchy(contextWrapper, childNode, view) // Recursive call
            }
        }
        return view
    }

    /**
     * Applies a map of attributes to the given [View].
     * Logs attribute application if [ConfigManager.config.isLoggingEnabled] is true.
     * Delegates the actual attribute processing to [AttributeProcessor.applyAttributes].
     *
     * @param view The [View] to which attributes will be applied.
     * @param attributes The [ArrayMap] of attribute names to values.
     */
    private fun applyAttributesToView(
        view: View,
        attributes: ArrayMap<String, String>,
    ) {
        if (ConfigManager.config.isLoggingEnabled) {
            Log.d(TAG, "Applying attributes to ${view.javaClass.simpleName} (ID: ${view.id})")
        }
        AttributeProcessor.applyAttributes(view, attributes) // Delegate to specialized processor
    }

    /**
     * Sets a delegate object for a given [View].
     * The delegate can be used to handle events or provide data to the view,
     * decoupling view logic from specific controller implementations.
     *
     * The delegate is stored in a [GeneratedView] object, which is attached to the view's tag.
     * If the view is `null`, this function does nothing.
     *
     * @param view The [View] for which the delegate is being set. Can be `null`.
     * @param delegate The delegate object to associate with the view.
     *
     * @see Utils.getGeneratedViewInfo
     * @see GeneratedView.delegate
     */
    @JvmStatic
    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}