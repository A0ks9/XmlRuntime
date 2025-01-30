package com.flipkart.android.proteus.value

import com.flipkart.android.proteus.toolbox.Utils

class Layout(
    val type: String,
    val attributes: List<Attribute>?,
    val data: Map<String, Value>?,
    val extras: ObjectValue?
) : Value() {

    override fun copy(): Layout {
        val copiedAttributes = attributes?.map { it.copy() }
        return Layout(type, copiedAttributes, data, extras)
    }

    fun merge(include: Layout): Layout {
        val mergedAttributes =
            (attributes.orEmpty() + include.attributes.orEmpty()).ifEmpty { null }

        val mergedData = (data.orEmpty() + include.data.orEmpty()).ifEmpty { null }
        val mergedExtras = ObjectValue().apply {
            Utils.addAllEntries(this, this@Layout.extras!!)
            Utils.addAllEntries(this, include.extras!!)
        }.takeIf { !it.isEmpty }
        return Layout(type, mergedAttributes, mergedData, mergedExtras)
    }

    /**
     * Attribute
     */
    data class Attribute(val id: Int, val value: Value) {
        fun copy(): Attribute = Attribute(id, value.copy())
    }
}