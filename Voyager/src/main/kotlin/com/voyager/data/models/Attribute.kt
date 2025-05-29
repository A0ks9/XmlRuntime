package com.voyager.data.models

/**
 * Represents an XML attribute of a view.
 *
 * This data class holds the name of an attribute and its potential reference value,
 * which could be a resource ID or a default/literal value if not a resource.
 *
 * @property name The name of the XML attribute (e.g., "android:id", "android:text").
 * @property reference The integer value associated with the attribute. This can be a resource ID
 *                     (e.g., R.id.my_button, R.string.my_text) or a literal value if the attribute
 *                     does not refer to a resource (e.g., color values, enum constants, or default numeric values).
 */
data class Attribute(val name: String, val reference: Int)
