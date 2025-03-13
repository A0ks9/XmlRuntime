package com.voyager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import com.google.gson.Gson
import com.voyager.data.models.ViewNode
import org.json.JSONArray

internal object Extensions {
    val trueValues = listOf<String>("true", "1", "yes")
    val falseValues = listOf<String>("false", "0", "no")
}

internal fun String.isBoolean(): Boolean =
    equals("true", ignoreCase = true) || equals("false", ignoreCase = true)

internal fun String.asBoolean(): Boolean? = if (isBoolean()) when (lowercase()) {
    in Extensions.trueValues -> true
    in Extensions.falseValues -> false
    else -> null
} else {
    null
}

internal fun String.isInteger(): Boolean = toIntOrNull() != null

internal fun String.isVisibility(): Boolean =
    if (!isInteger()) lowercase() in setOf("visible", "invisible", "gone") else this in setOf(
        "0", "4", "8"
    )

internal fun String.extractViewId(): String =
    Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$").find(this)?.groupValues?.get(1)!!

/**
 * Traverses the view hierarchy starting from the current [View] using Breadth-First Search (BFS)
 * to find a view ID associated with the given [id] within a [GeneratedView] tag.
 *
 * This function searches for a specific identifier within the `viewID` map of any [GeneratedView]
 * tag attached to a view in the hierarchy. If a view has a [GeneratedView] tag, and that tag's
 * `viewID` map contains the requested [id] as a key, the associated integer value (the view ID)
 * is returned.
 *
 * If no such view is found within the entire hierarchy, -1 is returned.
 *
 * @param id The string identifier to search for within the `viewID` maps of [GeneratedView] tags.
 * @return The integer view ID associated with the provided [id], or -1 if no matching ID is found.
 *
 * @see GeneratedView
 */
internal fun View.getViewID(id: String): Int {
    val queue: ArrayDeque<View> = ArrayDeque() // Queue for BFS traversal
    queue.add(this)

    while (queue.isNotEmpty()) {
        val currentView = queue.removeFirst()

        // Check if the current view has the required ID inside GeneratedView
        (currentView.tag as? GeneratedView)?.viewID?.get(id)?.let { return it }

        // If it's a ViewGroup, add its children to the queue
        if (currentView is ViewGroup) {
            repeat(currentView.childCount) { queue.add(currentView.getChildAt(it)) }
        }
    }

    return -1 // ID not found
}

internal fun View.getParentView(): ViewGroup? = parent as? ViewGroup

/**
 * Tries to find a view from root View based on String ID
 *
 * @param id String id that should match with the ID of View
 * @return The view which is found with the string id, otherwise null.
 */
fun View.findViewByIdString(id: String): View? {
    val idNum = this.getViewID(id)
    return if (idNum < 0) null else findViewById(idNum)
}

fun Context.getActivityName(): String {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext::class.simpleName ?: "Unknown"
        }
        currentContext = currentContext.baseContext
    }
    return "Unknown"
}