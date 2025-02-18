package com.runtimexml.utils

import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException
import java.lang.reflect.InvocationTargetException

internal object Utils {


    /**
     * Get a click listener.
     * @param parent The parent view, for obtaining delegate.
     * @param methodName The name of method to call when the button is clicked.
     * @return The OnClickListener.
     */
    fun getClickListener(parent: ViewGroup?, methodName: String): View.OnClickListener =
        View.OnClickListener { view ->
            (parent?.getGeneratedViewInfo()?.delegate)?.let {
                invokeMethod(it, methodName, false, view)
            }
        }

    /**
     * Invokes a method using reflection.
     * @param delegate The object that contains the method.
     * @param methodName The name of method.
     * @param withView  If it needs to pass View as a parameter.
     * @param view The view, if needs to be passed as parameter.
     */
    fun invokeMethod(delegate: Any?, methodName: String, withView: Boolean, view: View?) {
        var args: Array<Any>? = null
        var finalMethod = methodName
        if (methodName.endsWith(")")) {
            val parts = methodName.split("[(]".toRegex(), 2)
            finalMethod = parts[0]
            try {
                val argText = parts[1].replace("&quot;", "\"")
                val arr = JSONArray("[$argText]".dropLast(1))
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
                var argClass = arg::class.java
                if (argClass == Integer::class.java) argClass = Int::class.java
                argClass
            }?.toTypedArray()
            val method = if (argClasses == null) {
                klass?.getMethod(finalMethod)
            } else {
                klass?.getMethod(finalMethod, *argClasses)
            }
            method?.invoke(delegate, *(args ?: emptyArray()))
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            if (!withView && !methodName.endsWith(")")) {
                invokeMethod(delegate, methodName, true, view)
            }
        }
    }

    /**
     * Gets or creates a `GeneratedView` associated with a View.
     * If the View has a tag of type `GeneratedView` it returns that,
     * otherwise it creates a new `GeneratedView` instance and stores it as a tag.
     *
     * @return The generated view or the tag value as `GeneratedView`
     */
    fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    /**
     *  Retrieves the view id of a view inside the viewGroup.
     *  If the view does not exists, then returns -1
     *  @param root The root view that we are search inside
     *  @param id The id to search for.
     *  @return The integer Id of the view if present, or -1 if not found
     */
    private fun getViewID(root: View, id: String): Int {
        if (root !is ViewGroup) return -1
        return (root.tag as? GeneratedView)?.viewID?.get(id) ?: root.childrenSequence()
            .mapNotNull { getViewID(it, id).takeIf { it > -1 } }.firstOrNull() ?: -1
    }

    /**
     * Get a sequence of view's children for iteration.
     * @return Sequence of the children.
     */
    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var index = 0
            override fun hasNext(): Boolean = index < childCount
            override fun next(): View = getChildAt(index++)
        }
    }

    /**
     * Tries to find a view from root View based on String ID
     *
     * @param id String id that should match with the ID of View
     * @return The view which is found with the string id, otherwise null.
     */
    fun View.findViewByIdString(id: String): View? {
        val idNum = getViewID(this, id)
        return if (idNum < 0) null else findViewById(idNum)
    }
}