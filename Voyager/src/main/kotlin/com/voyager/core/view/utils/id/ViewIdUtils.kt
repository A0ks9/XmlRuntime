package com.voyager.core.view.utils.id

import android.view.View

/**
 * Regex used to parse string ID references like "@+id/name",
 * "@android:id/name", or simply "name". Captures the actual ID name.
 */
private val viewIdRegex = Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$")

/**
 * Extracts the actual ID name from a string that might represent a resource ID
 * (e.g., "@+id/submit_button", "@id/submit_button", "submit_button").
 *
 * It uses [viewIdRegex] to capture the ID name part.
 *
 * @receiver The string to extract the ID from.
 * @return The extracted ID name (e.g., "submit_button") or an empty string if the
 *         input string does not match the expected ID format.
 */
internal fun String.extractViewId(): String =
    viewIdRegex.find(this)?.groupValues?.getOrNull(1) ?: "" 