/**
 * Efficient utility functions for Android view operations and JSON parsing.
 *
 * This utility provides optimized methods for view manipulation, event handling,
 * and JSON parsing with proper error handling and performance optimizations.
 *
 * Key features:
 * - Efficient view traversal
 * - Optimized JSON parsing
 * - Thread-safe operations
 * - Memory-efficient processing
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Efficient view hierarchy traversal
 * - Optimized string operations
 * - Minimized object creation
 * - Efficient memory usage
 * - Safe resource handling
 *
 * Usage example:
 * ```kotlin
 * // Get a click listener
 * val listener = Utils.getClickListener(parentView, "onButtonClick")
 *
 * // Find view by string ID
 * val view = rootView.findViewByIdString("button_id")
 *
 * // Parse JSON to ViewNode
 * val viewNode = Utils.parseJsonToViewNode(inputStream, "MainActivity")
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import com.google.gson.stream.JsonReader
import com.voyager.data.models.ViewNode
import com.voyager.utils.view.Attributes
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException

/**
 * Internal utility object for efficient view operations and JSON parsing.
 */
internal object Utils {

    /**
     * Enumeration for drawable positions in views.
     */
    enum class DrawablePosition { START, END, TOP, BOTTOM }

    // Constants for frequently used strings
    private object Constants {
        const val QUOT = "&quot;"
        const val QUOTE = "\""
        const val LEFT_PAREN = "("
        const val LEFT_BRACKET = "["
        const val RIGHT_BRACKET = "]"
        const val EMPTY_STRING = ""
    }

    /**
     * Creates a click listener for a view that invokes a specified method on the delegate.
     *
     * @param parent The parent view containing the delegate
     * @param methodName The name of the method to invoke
     * @return An OnClickListener that invokes the specified method
     */
    fun getClickListener(parent: ViewGroup?, methodName: String): View.OnClickListener =
        View.OnClickListener { view ->
            parent?.getGeneratedViewInfo()?.delegate?.let {
                invokeMethod(it, methodName, false, view)
            }
        }

    /**
     * Invokes a method using reflection with proper error handling.
     *
     * @param delegate The object containing the method to invoke
     * @param methodName The name of the method to invoke
     * @param withView Whether to pass the view as a parameter
     * @param view The view to pass as parameter if needed
     */
    fun invokeMethod(delegate: Any?, methodName: String, withView: Boolean, view: View?) {
        var args: Array<Any>? = null
        var finalMethod = methodName

        if (methodName.endsWith(Constants.RIGHT_BRACKET)) {
            val parts = methodName.split("[(]".toRegex(), 2)
            finalMethod = parts[0]
            try {
                val argText = parts[1].replace(Constants.QUOT, Constants.QUOTE)
                val arr = JSONArray("${Constants.LEFT_BRACKET}$argText".dropLast(1))
                args = Array(arr.length()) { arr.get(it) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (withView && view != null) {
            args = arrayOf(view)
        }

        val klass = delegate?.javaClass
        try {
            val argClasses = args?.map { arg ->
                when (arg::class.java) {
                    Integer::class.java -> Int::class.java
                    else -> arg::class.java
                }
            }?.toTypedArray()

            val method = if (argClasses == null) {
                klass?.getMethod(finalMethod)
            } else {
                klass?.getMethod(finalMethod, *argClasses)
            }

            method?.invoke(delegate, *(args ?: emptyArray()))
        } catch (e: Exception) {
            when (e) {
                is IllegalAccessException,
                is InvocationTargetException,
                is NoSuchMethodException -> {
                    e.printStackTrace()
                    if (!withView && !methodName.endsWith(Constants.RIGHT_BRACKET)) {
                        invokeMethod(delegate, methodName, true, view)
                    }
                }
                else -> e.printStackTrace()
            }
        }
    }

    /**
     * Gets or creates a GeneratedView associated with a View.
     *
     * @return The existing GeneratedView or a new instance
     */
    fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    /**
     * Retrieves the view ID of a view inside the viewGroup.
     *
     * @param root The root view to search in
     * @param id The ID to search for
     * @return The integer ID of the view if present, or -1 if not found
     */
    private fun getViewID(root: View, id: String): Int {
        if (root !is ViewGroup) return -1
        return (root.tag as? GeneratedView)?.viewID?.get(id) 
            ?: root.childrenSequence()
                .mapNotNull { getViewID(it, id).takeIf { it > -1 } }
                .firstOrNull() 
            ?: -1
    }

    /**
     * Creates a sequence of view's children for efficient iteration.
     *
     * @return A sequence of child views
     */
    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var index = 0
            override fun hasNext(): Boolean = index < childCount
            override fun next(): View = getChildAt(index++)
        }
    }

    /**
     * Parses JSON input stream to a ViewNode structure.
     *
     * @param inputStream The input stream containing JSON data
     * @param activityName The name of the activity
     * @return The parsed ViewNode or null if parsing fails
     */
    fun parseJsonToViewNode(inputStream: InputStream, activityName: String): ViewNode? {
        return try {
            JsonReader(InputStreamReader(inputStream)).use { reader ->
                readViewNode(reader, activityName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads a ViewNode from a JsonReader.
     *
     * @param reader The JsonReader to read from
     * @param activityName The name of the activity
     * @return The parsed ViewNode or null if parsing fails
     */
    private fun readViewNode(reader: JsonReader, activityName: String): ViewNode? {
        var id: String? = null
        var type: String? = null
        val attributes = ArrayMap<String, String>()
        val children = mutableListOf<ViewNode>()

        try {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "type" -> type = reader.nextString()
                    "attributes" -> {
                        reader.beginObject()
                        while (reader.hasNext()) {
                            val name = reader.nextName()
                            val value = reader.nextString()
                            if (name == Attributes.Common.ID.name) id = value
                            attributes[name] = value
                        }
                        reader.endObject()
                    }
                    "children" -> {
                        reader.beginArray()
                        while (reader.hasNext()) {
                            readViewNode(reader, activityName)?.let { children.add(it) }
                        }
                        reader.endArray()
                    }
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return type?.let { ViewNode(id, it, activityName, attributes, children) }
    }
}