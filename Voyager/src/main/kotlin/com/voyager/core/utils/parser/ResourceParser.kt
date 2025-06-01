package com.voyager.core.utils.parser

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.voyager.core.model.ConfigManager
import kotlin.reflect.KClass
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.javaField

/**
 * Utility object for parsing and resolving Android resources.
 */
object ResourceParser {

    private val config = ConfigManager.config

    /**
     * Gets a drawable from a resource name.
     *
     * @param view The view to get resources from
     * @param name The drawable resource name (without type prefix, e.g., "my_image")
     * @return The drawable or null if not found
     */
    fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, config.provider.getResId("drawable", name), null
        )
    }

    /**
     * Gets a string from a resource name.
     *
     * @param context The application context
     * @param name The string resource name (e.g., "@string/my_string" or "literal string")
     * @return The string value or the name if not found or resource not found
     */
    fun getString(context: Context, name: String): String? = when {
        name.startsWith("@string/") -> {
            val resName = name.removePrefix("@string/")
            val resId = config.provider.getResId("string", resName)
            if (resId != 0) {
                ContextCompat.getString(context, resId)
            } else {
                // Log a warning if resource not found
                null // Or return resName if you prefer the literal name as fallback
            }
        }

        else -> name // Return the literal string if it's not a resource reference
    }

    /**
     * Gets a resource ID from a variable name and class using reflection.
     *
     * This is typically used to find resource IDs declared in `R` classes.
     *
     * @param variableName The variable name (e.g., "button1")
     * @param klass The KClass to search in (e.g., `android.R.id::class`)
     * @return The resource ID or 0 if not found
     */
    fun getResId(variableName: String, klass: KClass<*>): Int =
        klass.staticProperties.find { it.name == variableName }?.javaField?.getInt(null) ?: 0

    /**
     * Gets an Android XML resource ID from a full resource ID string (e.g., "@+id/my_view" or "@id/my_view").
     *
     * This method specifically targets Android's built-in resource IDs.
     *
     * @param fullResIdString The full resource ID string
     * @return The resource ID or `View.NO_ID` (-1) if not found or format is invalid
     */
    fun getAndroidXmlResId(fullResIdString: String?): Int =
        fullResIdString?.substringAfter("/")?.let { idName ->
            getResId(idName, android.R.id::class)
        } ?: View.NO_ID

    /**
     * Checks if a string is a tween animation resource reference (e.g., "@anim/my_animation").
     *
     * @param attributeValue The string to check
     * @return true if it's a tween animation resource reference, false otherwise
     */
    fun isTweenAnimationResource(attributeValue: String?): Boolean =
        attributeValue?.startsWith("@anim/") == true
} 