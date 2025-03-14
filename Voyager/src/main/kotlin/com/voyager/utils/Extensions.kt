package com.voyager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

private val booleanValuesSet =
    hashSetOf("true", "false", "0", "1", "yes", "no", "t", "f", "on", "off")
private val viewIdRegex = Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$")
private val viewIdCache = ConcurrentHashMap<String, Int>()
private val activityNameCache = WeakHashMap<Context, String>()
private const val EMPTY_VIEW_ID_RESULT = -1

internal fun String.isBoolean(): Boolean = booleanValuesSet.contains(this.lowercase())

internal fun String.isColor(): Boolean = startsWith("#") || startsWith("@color/")

internal fun String.extractViewId(): String =
    viewIdRegex.find(this)?.groupValues?.getOrNull(1) ?: ""

internal fun View.getViewID(id: String): Int {
    val cacheKey = id + this.hashCode()
    return viewIdCache.getOrPut(cacheKey) {
        val queue = ArrayDeque<View>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val currentView = queue.removeFirst()
            (currentView.tag as? GeneratedView)?.viewID?.get(id)?.let { return@getOrPut it }
            if (currentView is ViewGroup) {
                for (i in 0 until currentView.childCount) queue.add(currentView.getChildAt(i))
            }
        }
        EMPTY_VIEW_ID_RESULT
    }
}

internal fun View.getParentView(): ViewGroup? = parent as? ViewGroup

internal fun String.toLayoutParam(
    displayMetrics: DisplayMetrics, parentView: ViewGroup?, isHorizontal: Boolean,
) = when (this) {
    "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
    "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
    else -> this.toPixels(displayMetrics, parentView, isHorizontal, true) as Int
}

fun View.findViewByIdString(id: String): View? =
    getViewID(id).takeIf { it != EMPTY_VIEW_ID_RESULT }?.let(::findViewById)

fun Context.getActivityName(): String = activityNameCache.getOrPut(this) {
    var currentContext: Context = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return@getOrPut currentContext::class.java.simpleName
        currentContext = currentContext.baseContext
    }
    "Unknown"
}