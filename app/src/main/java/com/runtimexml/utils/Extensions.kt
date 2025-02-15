package com.runtimexml.utils

import android.view.View
import android.view.ViewGroup

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
    Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$").find(this)?.groupValues?.get(2)!!

/**
 * Retrieves the view ID of a view inside the ViewGroup using BFS.
 * If the view does not exist, returns -1.
 *
 * @param root The root view to search inside.
 * @param id The ID to search for.
 * @return The integer ID of the view if present, or -1 if not found.
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