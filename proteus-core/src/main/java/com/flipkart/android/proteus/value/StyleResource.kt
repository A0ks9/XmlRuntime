package com.flipkart.android.proteus.value

import android.content.Context
import android.content.res.TypedArray
import android.util.LruCache
import java.util.regex.Pattern

class StyleResource private constructor(val styleId: Int, val attributeId: Int) : Value() {

    companion object {
        @JvmField
        val NULL = StyleResource(-1, -1)

        private const val ATTR_START_LITERAL = "?"
        private val styleMap = mutableMapOf<String, Int>()
        private val attributeMap = mutableMapOf<String, Int>()
        private val classCache = mutableMapOf<String, Class<*>>()

        private val stylePattern = Pattern.compile("([^:]+):(.+)")

        fun isStyleResource(value: String): Boolean = value.startsWith(ATTR_START_LITERAL)

        @JvmStatic
        fun valueOf(value: String, context: Context): StyleResource? =
            StyleCache.cache.getOrPut(value) {
                createStyleResource(value, context) ?: NULL
            }.takeIf { it != NULL }


        @JvmStatic
        fun valueOf(styleId: Int, attributeId: Int): StyleResource =
            StyleResource(styleId, attributeId)

        private fun createStyleResource(value: String, context: Context): StyleResource? {
            val (style, attr) = stylePattern.matcher(value.substring(1)).takeIf { it.matches() }
                ?.let {
                    it.group(1) to it.group(2)
                } ?: return null

            val styleId = styleMap.getOrPut(style!!) {
                getResourceId(context, style, "style")
            }

            val attrId = attributeMap.getOrPut(attr!!) {
                getResourceId(context, attr, "attr")
            }

            return StyleResource(styleId, attrId)
        }

        private fun getResourceId(context: Context, name: String, type: String): Int {
            val className = "${context.packageName}.R\$$type"
            val clazz = classCache.getOrPut(className) { Class.forName(className) }
            return clazz.getField(name).getInt(null)
        }

    }

    fun apply(context: Context): TypedArray =
        context.obtainStyledAttributes(styleId, intArrayOf(attributeId))

    override fun copy(): Value = this

    private object StyleCache {
        val cache = LruCache<String, StyleResource>(64)
    }
}

fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val existing = get(key)
    return if (existing != null) {
        existing
    } else {
        val newValue = defaultValue()
        put(key, newValue)
        newValue
    }
}