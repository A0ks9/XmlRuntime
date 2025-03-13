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
import com.voyager.utils.DynamicLayoutInflation.createView
import com.voyager.utils.FileHelper.getFileExtension
import com.voyager.utils.FileHelper.parseXML
import com.voyager.utils.Utils.getGeneratedViewInfo
import com.voyager.utils.Utils.parseJsonToViewNode
import com.voyager.utils.processors.AttributeRegistry
import com.voyager.utils.processors.ViewProcessor.Companion.createViewByType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Object that handles the dynamic inflation of layouts from JSON or XML resources.
 * It provides methods to parse the structure and apply attributes to the views, as well as adding them to the view hierarchy
 */
object DynamicLayoutInflation {

    internal var viewNode: ViewNode? = null

    // Initialization block where properties actions are created and added to ViewProperties map
    init {
        BaseViewAttributes.initializeAttributes()
    }

    //what if the file has JSONArray not JSONObject
    /**
     * Inflates a layout from a JSON or XML resource URI.
     *
     * @param context The context used to access resources.
     * @param layoutUri The URI of the JSON or XML resource.
     * @param parent The parent ViewGroup that will contain the inflated views.
     * @return The root View of the inflated layout or null if an error occurs.
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
                        "json" -> {
                            context.contentResolver.openInputStream(layoutUri)?.use { inputStream ->
                                val node =
                                    parseJsonToViewNode(inputStream, context.getActivityName())
                                        ?: return@launch
                                viewNode = node
                                val view = createView(
                                    contextThemeWrapper, node, parent
                                )
                                callback?.invoke(view)
                            }
                        }

                        "xml" -> {
                            val node = fromJson(
                                parseXML(
                                    context.contentResolver.openInputStream(layoutUri)!!
                                )
                            ) ?: return@launch
                            viewNode = node
                            val view = createView(
                                contextThemeWrapper, node, parent
                            )
                            callback?.invoke(view)
                        }

                        else -> callback?.invoke(null)
                    }
                }
            }
        } catch (error: Exception) {
            Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
            callback?.invoke(null) //If error is encountered then return null.
        }
    }

    /**
     * Inflates a view hierarchy from a [ViewNode] definition within a given parent.
     *
     * This function attempts to create and inflate a view hierarchy based on the provided [layout].
     * It utilizes the `createViews` function to perform the actual view creation.
     *
     * If any exception occurs during the view creation process, it logs the error and returns `null`.
     * This ensures that the application doesn't crash due to potential errors in the layout definition.
     *
     * @param context The application context used for view creation.
     * @param layout The [ViewNode] object defining the layout to be inflated. This object should
     *                         contain the necessary information to create the desired view hierarchy.
     * @param parent The [ViewGroup] into which the inflated view hierarchy will be added.
     *                  This acts as the parent parent for the new views.
     * @return The root [View] of the inflated hierarchy if successful, or `null` if an exception occurred.
     *
     * @throws Exception if there are problems during view creation, but these are caught internally and result in null return
     *
     * @see createView
     * @see ViewNode
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
            Log.e("DynamicLayoutInflation", "Error inflating JSON", error)
            callback?.invoke(null)
        }
    }

    private fun createView(
        context: ContextThemeWrapper, layout: ViewNode, parent: ViewGroup?, isFirst: Boolean = true,
    ): View? = createViewByType(context, layout.type).apply {
        parent?.addView(this)
        applyAttributes(this, layout.attributes, parent)
        if (this is ViewGroup && layout.children.isNotEmpty()) {
            parseChildren(context, layout.children, this)
        }
    }

    /**
     * Parses the child views and adds them to the parent.
     * @param context The Context used to access resources.
     * @param children The JSON children of child views.
     * @param parent The parent view where the children should be added.
     */
    private fun parseChildren(
        context: ContextThemeWrapper, children: List<ViewNode>, parent: ViewGroup,
    ) {
        children.let {
            for (index in 0 until it.size) {
                val childJson = it[index]
                createView(context, childJson, parent, false)
            }
        }
    }

    /**
     * Applies attributes to a given view.
     * @param view The View to apply attributes to.
     * @param attributes A map of attribute names to their values.
     * @param parent The parent ViewGroup of the view.
     */
    private fun applyAttributes(
        view: View, attributes: ArrayMap<String, String>, parent: ViewGroup?,
    ) {
        Log.d(
            "DynamicLayoutInflation",
            "Before apply attributes, View: ${view.javaClass.simpleName}, parent: ${parent?.javaClass?.simpleName}"
        )

        AttributeRegistry.applyAttributes(view, attributes) // Fallback to default AttributeRegistry

        Log.d(
            "DynamicLayoutInflation",
            "After applying attributes, View: ${view.javaClass.simpleName}, width: ${view.width}, height: ${view.height}"
        )
    }

    /**
     * Sets the delegate object for a view.
     * @param view The View for which to set the delegate.
     * @param delegate  The delegate object to set.
     */
    @JvmStatic
    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}