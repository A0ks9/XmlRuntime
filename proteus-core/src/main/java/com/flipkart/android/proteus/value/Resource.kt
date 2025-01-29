package com.flipkart.android.proteus.value

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import androidx.annotation.StringDef
import androidx.appcompat.content.res.AppCompatResources

class Resource(val resId: Int) : Value() {

    companion object {
        const val RESOURCE_PREFIX_ANIMATION = "@anim/"
        const val RESOURCE_PREFIX_BOOLEAN = "@bool/"
        const val RESOURCE_PREFIX_COLOR = "@color/"
        const val RESOURCE_PREFIX_DIMENSION = "@dimen/"
        const val RESOURCE_PREFIX_DRAWABLE = "@drawable/"
        const val RESOURCE_PREFIX_STRING = "@string/"

        const val ANIM = "anim"
        const val BOOLEAN = "bool"
        const val COLOR = "color"
        const val DIMEN = "dimen"
        const val DRAWABLE = "drawable"
        const val STRING = "string"

        @JvmField
        val NOT_FOUND = Resource(0)

        private val resourceCache = LruCache<String, Resource>(64)

        fun isAnimation(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_ANIMATION)
        fun isBoolean(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_BOOLEAN)
        fun isColor(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_COLOR)
        fun isDimension(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_DIMENSION)
        fun isDrawable(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_DRAWABLE)
        fun isString(string: String): Boolean = string.startsWith(RESOURCE_PREFIX_STRING)

        fun isResource(string: String): Boolean =
            isAnimation(string) || isBoolean(string) || isColor(string) || isDimension(string) || isDrawable(
                string
            ) || isString(string)


        @JvmStatic
        fun getBoolean(resId: Int, context: Context): Boolean? = try {
            context.resources.getBoolean(resId)
        } catch (e: Resources.NotFoundException) {
            null
        }

        @JvmStatic
        fun getColor(resId: Int, context: Context): Int? = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context.resources.getColor(
                resId, context.theme
            )
            else context.resources.getColor(resId)
        } catch (e: Resources.NotFoundException) {
            null
        }


        @JvmStatic
        fun getColorStateList(resId: Int, context: Context): ColorStateList? = try {
            AppCompatResources.getColorStateList(context, resId)
        } catch (nfe: Resources.NotFoundException) {
            null
        }


        @JvmStatic
        fun getDrawable(resId: Int, context: Context): Drawable? = try {
            AppCompatResources.getDrawable(context, resId)
        } catch (e: Resources.NotFoundException) {
            null
        }

        @JvmStatic
        fun getDimension(resId: Int, context: Context): Float? = try {
            context.resources.getDimension(resId)
        } catch (e: Resources.NotFoundException) {
            null
        }

        @JvmStatic
        fun getString(resId: Int, context: Context): String? = try {
            context.getString(resId)
        } catch (e: Resources.NotFoundException) {
            null
        }


        @JvmStatic
        fun valueOf(value: String?, type: String?, context: Context): Resource? {
            if (value.isNullOrEmpty()) return null

            var resource = resourceCache.get(value)

            if (resource == null) {
                val resId = context.resources.getIdentifier(value, type, context.packageName)
                resource = if (resId == 0) NOT_FOUND else Resource(resId)
                resourceCache.put(value, resource)
            }
            return if (resource == NOT_FOUND) null else resource
        }

        @JvmStatic
        fun valueOf(resId: Int): Resource = Resource(resId)
    }


    fun getBoolean(context: Context): Boolean? = getBoolean(resId, context)
    fun getColor(context: Context): Int? = getColor(resId, context)
    fun getColorStateList(context: Context): ColorStateList? = getColorStateList(resId, context)
    fun getDrawable(context: Context): Drawable? = getDrawable(resId, context)
    fun getDimension(context: Context): Float? = getDimension(resId, context)
    fun getInteger(context: Context): Int? = getInteger(resId, context)
    fun getString(context: Context): String? = getString(resId, context)


    fun getInteger(resId: Int, context: Context): Int? = try {
        context.resources.getInteger(resId)
    } catch (e: Resources.NotFoundException) {
        null
    }

    override fun copy(): Value = this

    @StringDef(ANIM, BOOLEAN, COLOR, DRAWABLE, DIMEN, STRING)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ResourceType
}