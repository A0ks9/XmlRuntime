package com.voyager.data.models

/**
 * Represents a framework attribute, typically an Android attribute, storing its
 * programmatic name and its integer resource ID.
 *
 * This data class is used to define and identify attributes that can be processed
 * by the Voyager framework, particularly for mapping string names found in dynamic
 * layout definitions (XML/JSON) to their corresponding Android resource IDs.
 *
 * Example:
 * ```kotlin
 * val layoutWidthAttr = Attribute("layout_width", android.R.attr.layout_width)
 * val textColorAttr = Attribute("textColor", android.R.attr.textColor)
 * ```
 *
 * @property name The programmatic or XML/JSON name of the attribute.
 *                For example, "layout_width", "textColor", "app:customAttribute".
 * @property reference The integer resource ID of the attribute, as defined in `R.attr`
 *                     (e.g., `android.R.attr.layout_width`, `android.R.attr.textColor`).
 *                     This ID is used by Android systems to look up attribute metadata and values.
 */
data class Attribute(val name: String, val reference: Int)
