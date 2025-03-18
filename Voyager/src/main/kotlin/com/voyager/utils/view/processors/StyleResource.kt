package com.voyager.utils.view.processors

import android.content.Context
import android.util.LruCache
import android.view.View
import com.voyager.data.models.ConfigManager

class StyleResource private constructor(val styleId: Int) {

    companion object {
        @JvmField
        val NULL = StyleResource(-1)

        private val styleMap = HashMap<String, Int>(32)
        private val classCache = HashMap<String, Class<*>>(16)
        private val config = ConfigManager.config

        fun isStyleResource(value: String): Boolean = value.startsWith("@style/")

        @JvmStatic
        fun valueOf(value: String, context: Context): StyleResource? =
            StyleCache.cache.getOrPut(value) { createStyleResource(value, context) ?: NULL }
                .takeIf { it !== NULL }

        @JvmStatic
        fun valueOf(styleId: Int, attributeId: Int) = StyleResource(styleId)

        private fun createStyleResource(value: String, context: Context): StyleResource? {
            return when {
                value.startsWith("@style/") -> StyleResource(
                    getResourceId(
                        context, value.substringAfter("@style/"), "style"
                    )
                )

                else -> null
            }
        }

        private fun getResourceId(context: Context, name: String, type: String): Int {
            val resourceId = styleMap.getOrPut(name) { config.provider.getResId(type, name) }
            if (resourceId == -1) {
                val className = "${context.packageName}.R\$$type"
                return classCache.getOrPut(className) { Class.forName(className) }.getField(name)
                    .getInt(null)
            }

            return resourceId
        }
    }

    fun apply(view: View, context: Context) {
        //use ContextThemeWrapper to apply view theme to it
    }

    private object StyleCache {
        val cache = LruCache<String, StyleResource>(64)
    }
}

fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V =
    get(key) ?: defaultValue().also { put(key, it) }
