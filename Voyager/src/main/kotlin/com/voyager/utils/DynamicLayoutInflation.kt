package com.voyager.utils

/**
 * `DynamicLayoutInflation` is the primary engine for Voyager's dynamic layout inflation capabilities.
 * It provides a robust and optimized mechanism for parsing layout definitions from JSON or XML sources
 * (via a `Uri`) and constructing the corresponding Android `View` hierarchy at runtime.
 *
 * This object handles:
 * - Determining the layout type (JSON or XML) based on the file extension.
 * - Orchestrating the parsing of layout files into an intermediate `ViewNode` tree structure.
 *   It utilizes [com.voyager.data.models.ViewNodeParser] for JSON and a combination of
 *   [FileHelper.parseXML] and [com.voyager.data.models.ViewNodeParser.fromJson] for XML.
 * - Managing the creation of actual Android `View` instances from `ViewNode`s using
 *   [com.voyager.utils.processors.ViewProcessor].
 * - Applying attributes defined in the `ViewNode`s to the created `View` instances via
 *   [com.voyager.utils.processors.AttributeProcessor].
 * - Handling the recursive inflation of child views.
 * - Operations are performed asynchronously on the main thread using coroutines.
 *
 * Key features include:
 * - **Efficient Parsing:** Optimized for speed in processing layout definitions.
 * - **Memory Optimization:** Designed to minimize memory footprint during view creation.
 * - **Format Support:** Inflates layouts from both JSON and XML files.
 * - **Asynchronous Inflation:** Uses coroutines to perform inflation on the main thread without
 *   blocking UI, with results delivered via a callback.
 * - **Theming:** Supports applying a specific theme (`ContextThemeWrapper`) during inflation.
 * - **Extensibility:** Works in conjunction with `ViewProcessor` and `AttributeProcessor`, which
 *   can be extended to support custom views and attributes.
 *
 * Usage Example:
 * ```kotlin
 * // In your Activity or Fragment
 * val layoutUri: Uri = Uri.parse("android.resource://your.package.name/raw/your_layout_file") // Can be .json or .xml
 * val parentView: ViewGroup = findViewById(R.id.your_container_viewgroup)
 * val themeResId: Int = R.style.YourAppTheme_VoyagerCompatible // Optional theme
 *
 * DynamicLayoutInflation.inflate(this, themeResId, layoutUri, parentView) { inflatedView ->
 *     if (inflatedView != null) {
 *         // Successfully inflated the view.
 *         // The view is already added to parentView if parentView was not null.
 *         Log.d("VoyagerDemo", "View inflated: ${inflatedView::class.java.simpleName}")
 *     } else {
 *         // Handle inflation error
 *         Log.e("VoyagerDemo", "Failed to inflate layout from $layoutUri")
 *     }
 * }
 * ```
 *
 * @see com.voyager.data.models.ViewNode
 * @see com.voyager.data.models.ViewNodeParser
 * @see com.voyager.utils.processors.ViewProcessor
 * @see com.voyager.utils.processors.AttributeProcessor
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.collection.ArrayMap
import com.voyager.data.models.ConfigManager
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
    /**
     * Stores the root [ViewNode] of the most recently requested layout inflation.
     * This property is thread-safe due to `@Volatile` annotation.
     * It's marked `internal` as it's primarily used within Voyager's core utilities or for debugging.
     */
    @Volatile
    internal var viewNode: ViewNode? = null

    // Initialization block to ensure essential attributes are registered when the object is first accessed.
    init {
        BaseViewAttributes.initializeAttributes()
    }

    /**
     * Inflates a layout from a JSON or XML resource URI asynchronously.
     *
     * This is the primary public entry point for layout inflation. It determines the file type
     * (JSON or XML) from the `layoutUri` and delegates to the appropriate internal inflation logic.
     * The inflation process happens on the main thread via a coroutine.
     *
     * @param context The base [Context] used for resource access and view creation.
     *                It will be wrapped in a [ContextThemeWrapper] with the specified `theme`.
     * @param theme The theme resource ID (e.g., `R.style.YourTheme`) to apply to the inflated views.
     *              This allows the dynamically inflated views to adopt a specific style.
     * @param layoutUri The [Uri] pointing to the JSON or XML layout resource. This can be obtained
     *                  from various sources like `android.resource://`, `file://`, or network URIs.
     * @param parent An optional [ViewGroup] to which the root view of the inflated layout will be added.
     *               If `null`, the root view is created but not attached to any parent.
     * @param callback An optional lambda function that will be invoked on the main thread
     *                 with the root [View] of the inflated layout, or `null` if inflation fails.
     *                 The inflated view (if successful and `parent` is not null) will already be added to the `parent`
     *                 ViewGroup when this callback is invoked.
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
            // Asynchronously determine the file extension from the URI.
            // The actual inflation logic is then dispatched to the main thread.
            getFileExtension(context, layoutUri) { extension ->
                CoroutineScope(Dispatchers.Main).launch {
                    // Create a ContextThemeWrapper to apply the specified theme to the inflated views.
                    val contextThemeWrapper = ContextThemeWrapper(context, theme)

                    // Decide inflation strategy based on file extension.
                    when (extension.lowercase()) {
                        JSON_EXTENSION -> {
                            // Handle JSON layout inflation.
                            inflateJson(contextThemeWrapper, layoutUri, parent, callback)
                        }
                        XML_EXTENSION -> {
                            // Handle XML layout inflation.
                            inflateXml(contextThemeWrapper, layoutUri, parent, callback)
                        }
                        else -> {
                            // Log an error and invoke callback with null for unsupported file types.
                            Log.e(TAG, "Unsupported file extension: $extension for URI: $layoutUri")
                            callback?.invoke(null)
                        }
                    }
                }
            }
        } catch (error: Exception) {
            // Catch any unexpected errors during the initiation of the inflation process.
            Log.e(TAG, "Error initiating layout inflation for URI: $layoutUri", error)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a layout from a JSON resource URI.
     * This function is called internally after the file type has been identified as JSON.
     *
     * It reads the content from the `layoutUri`, parses it into a [ViewNode] tree using
     * [com.voyager.utils.Utils.parseJsonToViewNode], and then proceeds to create the view hierarchy.
     *
     * @param context The [ContextThemeWrapper] to be used for view creation and resource access,
     *                ensuring the correct theme is applied.
     * @param layoutUri The [Uri] of the JSON layout resource.
     * @param parent The optional parent [ViewGroup] to attach the inflated view to.
     * @param callback The callback to invoke with the inflated [View] or `null` on failure.
     */
    private fun inflateJson(
        context: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        try {
            // Attempt to open an InputStream from the content resolver for the given layout URI.
            context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                // Parse the JSON input stream.
                // context.getActivityName() is passed, potentially for context-aware parsing or logging.
                val node = parseJsonToViewNode(inputStream, context.getActivityName())

                // If parsing fails (node is null), log an error and invoke callback with null.
                if (node == null) {
                    Log.e(TAG, "Failed to parse JSON into ViewNode from URI: $layoutUri")
                    callback?.invoke(null)
                    return // Exit if parsing failed.
                }

                // Store the parsed root ViewNode. This might be used for debugging or internal state.
                viewNode = node

                // Create the Android View hierarchy from the ViewNode structure.
                val view = createView(context, node, parent)

                // Invoke the callback with the inflated view (or null if creation failed).
                callback?.invoke(view)
            } ?: run {
                // If inputStream is null (e.g., URI is invalid or resource not found), log an error.
                Log.e(TAG, "Could not open InputStream for JSON URI: $layoutUri")
                callback?.invoke(null)
            }
        } catch (e: Exception) {
            // Catch any exceptions during JSON parsing or view creation.
            Log.e(TAG, "Error inflating JSON layout from URI: $layoutUri", e)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a layout from an XML resource URI.
     * This function is called internally after the file type has been identified as XML.
     *
     * It reads the content from the `layoutUri`, parses the XML into a generic JSON-like structure
     * using [com.voyager.utils.FileHelper.parseXML], then converts this structure into a [ViewNode]
     * tree using [com.voyager.data.models.ViewNodeParser.fromJson]. Finally, it creates the view hierarchy.
     *
     * @param context The [ContextThemeWrapper] to be used for view creation and resource access.
     * @param layoutUri The [Uri] of the XML layout resource.
     * @param parent The optional parent [ViewGroup] to attach the inflated view to.
     * @param callback The callback to invoke with the inflated [View] or `null` on failure.
     */
    private fun inflateXml(
        context: ContextThemeWrapper,
        layoutUri: Uri,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)?,
    ) {
        try {
            // Attempt to open an InputStream for the given XML layout URI.
            context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                // Step 1: Parse the raw XML into a generic JSON-like structure (ArrayMap).
                // This intermediate step simplifies the subsequent conversion to ViewNode.
                val parsedXmlJson = parseXML(inputStream)

                // Step 2: Convert the JSON-like structure into Voyager's ViewNode hierarchy.
                // 'fromJson' is a static method in ViewNodeParser companion object.
                val node = fromJson(parsedXmlJson)

                // If parsing or conversion fails (node is null), log an error and exit.
                if (node == null) {
                    Log.e(TAG, "Failed to parse XML into ViewNode from URI: $layoutUri")
                    callback?.invoke(null)
                    return // Exit if parsing failed.
                }

                // Store the root ViewNode.
                viewNode = node

                // Step 3: Create the Android View hierarchy from the ViewNode structure.
                val view = createView(context, node, parent)

                // Invoke the callback with the inflated view.
                callback?.invoke(view)
            } ?: run {
                // If inputStream is null, log an error.
                Log.e(TAG, "Could not open InputStream for XML URI: $layoutUri")
                callback?.invoke(null)
            }
        } catch (e: Exception) {
            // Catch any exceptions during XML processing or view creation.
            Log.e(TAG, "Error inflating XML layout from URI: $layoutUri", e)
            callback?.invoke(null)
        }
    }

    /**
     * Inflates a view hierarchy directly from a provided [ViewNode] definition.
     * This internal function is useful for scenarios where the `ViewNode` tree is already
     * constructed (e.g., from a cache or programmatically).
     *
     * @param context The [ContextThemeWrapper] for view creation.
     * @param layout The root [ViewNode] defining the layout to be inflated.
     * @param parent The optional parent [ViewGroup] to attach the inflated view to.
     * @param callback The optional callback to invoke with the inflated [View] or `null` on failure.
     */
    internal fun inflate(
        context: ContextThemeWrapper,
        layout: ViewNode,
        parent: ViewGroup?,
        callback: ((View?) -> Unit)? = null,
    ) {
        try {
            viewNode = layout // Store the provided root node.
            // Create the view hierarchy.
            val view = createView(context, layout, parent)
            callback?.invoke(view)
        } catch (error: Exception) {
            Log.e(TAG, "Error inflating view hierarchy from ViewNode", error)
            callback?.invoke(null)
        }
    }

    /**
     * Creates a single [View] instance from a [ViewNode] definition and recursively inflates its children.
     *
     * This core private function performs the following steps:
     * 1. Creates the [View] instance based on the `layout.type` using [com.voyager.utils.processors.ViewProcessor.createViewByType].
     * 2. If a `parent` [ViewGroup] is provided, adds the newly created view to it.
     * 3. Applies attributes defined in `layout.attributes` to the view using [applyAttributes].
     * 4. If the created view is a [ViewGroup] and the `layout` has children, recursively calls [parseChildren]
     *    to inflate and attach the child views.
     *
     * @param context The [ContextThemeWrapper] used for creating views.
     * @param layout The [ViewNode] defining the view to be created and its attributes/children.
     * @param parent The optional parent [ViewGroup] to which this view should be added.
     *               If `null`, the view is created but not attached.
     * @return The created [View] instance, or `null` if creation failed (though `createViewByType` typically throws an exception on failure).
     */
    private fun createView(
        context: ContextThemeWrapper,
        layout: ViewNode,
        parent: ViewGroup?,
    ): View? {
        // Step 1: Create the base View instance using ViewProcessor.
        // ViewProcessor handles mapping the 'layout.type' string (e.g., "TextView") to an actual View class.
        val view = createViewByType(context, layout.type)

        // Step 2: Add the newly created view to its parent ViewGroup, if a parent is provided.
        // If parent is null, the view is created but not attached to any hierarchy yet.
        parent?.addView(view)

        // Step 3: Apply the attributes defined in the ViewNode to the created View instance.
        // AttributeProcessor handles the logic for applying various attribute types.
        applyAttributes(view, layout.attributes)

        // Step 4: If the created view is a ViewGroup and the ViewNode has children,
        // recursively parse and create these child views.
        if (view is ViewGroup && layout.children.isNotEmpty()) {
            parseChildren(context, layout.children, view)
        }

        // Return the created view (root of this particular inflation step).
        return view
    }

    /**
     * Parses and creates child views for a given parent [ViewGroup].
     * This function iterates through a list of child [ViewNode]s and calls [createView] for each one,
     * effectively building the sub-hierarchy under the `parent` view.
     *
     * @param context The [ContextThemeWrapper] for creating child views.
     * @param children A list of [ViewNode]s representing the child views to be created.
     * @param parent The [ViewGroup] to which the created child views will be added.
     */
    private fun parseChildren(
        context: ContextThemeWrapper,
        children: List<ViewNode>, // List of child ViewNode definitions.
        parent: ViewGroup, // The parent ViewGroup to attach child views to.
    ) {
        // Iterate over each child ViewNode.
        children.forEach { childNode ->
            // Recursively call createView for each child.
            // The 'parent' argument in this recursive call ensures children are added to the current 'parent' ViewGroup.
            createView(context, childNode, parent)
        }
    }

    /**
     * Applies a map of attributes to a given [View].
     * This function delegates the actual attribute application logic to
     * [com.voyager.utils.processors.AttributeProcessor.applyAttributes].
     * It also logs the process if logging is enabled in [com.voyager.data.models.ConfigManager].
     *
     * @param view The [View] instance to which attributes will be applied.
     * @param attributes An [ArrayMap] where keys are attribute names (e.g., "android:layout_width")
     *                   and values are the attribute values (e.g., "match_parent").
     */
    private fun applyAttributes(
        view: View,
        attributes: ArrayMap<String, String>,
    ) {
        if (ConfigManager.config.isLoggingEnabled) {
            Log.d(TAG, "Applying attributes to ${view.javaClass.simpleName} with ID: ${view.id}")
        }
        AttributeProcessor.applyAttributes(view, attributes)
    }

    /**
     * Sets a delegate object for a given [View].
     * The delegate can be used to handle events or provide data to custom views
     * that are aware of the Voyager framework's delegate mechanism.
     * It relies on [com.voyager.utils.Utils.getGeneratedViewInfo] to access Voyager-specific
     * information associated with the view.
     *
     * @param view The [View] for which the delegate is being set. Can be null, in which case the operation is a no-op.
     * @param delegate The delegate object to associate with the view.
     */
    @JvmStatic
    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}