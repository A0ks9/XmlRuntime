package com.flipkart.android.proteus.value

import android.content.Context
import android.content.res.TypedArray
import android.util.LruCache
import java.util.regex.Pattern

class AttributeResource private constructor(val attributeId: Int) : Value() {

    companion object {
        @JvmField
        val NULL = AttributeResource(-1)
        private const val ATTR_START_LITERAL = "?"
        private const val ATTR_LITERAL = "attr/"
        private val attributePattern = Pattern.compile(
            "(\\?)(\\S*)(:?)(attr/?)(\\S*)", Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        )

        private val classCache = mutableMapOf<String, Class<*>>()

        fun isAttributeResource(value: String): Boolean =
            value.startsWith(ATTR_START_LITERAL) && value.contains(ATTR_LITERAL)

        fun valueOf(value: String, context: Context): AttributeResource? {
            return AttributeCache.cache.get(value) ?: run {
                try {
                    val attribute = createAttributeResource(value, context)
                    AttributeCache.cache.put(value, attribute)
                    attribute
                } catch (e: Exception) {
                    if (ProteusConstants.isLoggingEnabled()) {
                        e.printStackTrace()
                    }
                    NULL
                }
            }.takeIf { it != NULL }
        }

        fun valueOf(value: Int): AttributeResource = AttributeResource(value)

        private fun createAttributeResource(value: String, context: Context): AttributeResource {
            val matcher = attributePattern.matcher(value)
            val (packageName, attributeName) = if (matcher.matches()) {
                matcher.group(2) to matcher.group(5)
            } else {
                null to value.substring(1)
            }

            val actualPackageName =
                packageName?.takeIf { it.isNotEmpty() }?.dropLast(1) ?: context.packageName

            val className = "$actualPackageName.R\$attr"
            val clazz = classCache.getOrPut(className) { Class.forName(className) }

            val field = clazz.getField(attributeName!!)
            val attributeId = field.getInt(null)
            return AttributeResource(attributeId)

        }
    }


    fun apply(context: Context): TypedArray =
        context.obtainStyledAttributes(intArrayOf(attributeId))

    override fun copy(): Value = this

    private object AttributeCache {
        val cache = LruCache<String, AttributeResource>(16)
    }
}