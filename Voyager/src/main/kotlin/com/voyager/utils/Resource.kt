package com.voyager.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import com.voyager.data.models.ConfigManager

internal class Resource private constructor(val resId: Int) {

    companion object {
        private val PREFIX_MAP = mapOf(
            "@anim/" to "anim",
            "@bool/" to "bool",
            "@color/" to "color",
            "@dimen/" to "dimen",
            "@drawable/" to "drawable",
            "@string/" to "string"
        )

        val NOT_FOUND = Resource(-1)

        // Fast prefix checking
        fun isResource(value: String) = PREFIX_MAP.keys.any { value.startsWith(it) }

        // Inline to reduce method overhead
        private inline fun <T> safeCall(block: () -> T) = try {
            block()
        } catch (_: Resources.NotFoundException) {
            null
        }

        fun getBoolean(resId: Int, context: Context) =
            safeCall { context.resources.getBoolean(resId) }

        fun getColor(resId: Int, context: Context): Int? {
            val res = context.resources
            return safeCall {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) res.getColor(
                    resId, context.theme
                )
                else @Suppress("DEPRECATION") res.getColor(resId)
            }
        }

        fun getColorStateList(resId: Int, context: Context) = safeCall {
            AppCompatResources.getColorStateList(context, resId)
        }

        fun getDrawable(resId: Int, context: Context) = safeCall {
            AppCompatResources.getDrawable(context, resId)
        }

        fun getDimension(resId: Int, context: Context) =
            safeCall { context.resources.getDimension(resId) }

        fun getString(resId: Int, context: Context) = safeCall { context.getString(resId) }

        fun getInteger(resId: Int, context: Context) =
            safeCall { context.resources.getInteger(resId) }

        fun valueOf(value: String?, type: String) = value?.let {
            Resource(
                ConfigManager.config.provider.getResId(
                    type, it
                )
            )
        } ?: NOT_FOUND

        fun valueOf(resId: Int) = Resource(resId)
    }
}
