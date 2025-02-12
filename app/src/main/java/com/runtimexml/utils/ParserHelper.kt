package com.runtimexml.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat

class ParserHelper {

    /**
     * Parses a color string into an int color value.
     * @param view Used for context.
     * @param string A string that represents a color, can be hex or a resource.
     * @return the parsed color in Int.
     * @throws IllegalStateException if the color string is null.
     */
    private fun parseColor(view: View?, string: String?): Int = when {
        string == null -> throw IllegalStateException("Color of background cannot be null")
        string.length == 4 && string.startsWith("#") -> Color.parseColor("#${string[1]}${string[1]}${string[2]}${string[2]}${string[3]}${string[3]}")
        else -> Color.parseColor(string)
    }

    /**
     * Adjusts the brightness of a given color.
     * @param color The int color value.
     * @param amount The amount that should be used for brightness adjustment.
     * @return  The adjusted color value.
     */
    private fun adjustBrightness(color: Int, amount: Float): Int =
        (color and 0xFF0000 shr 16) * amount.toInt() shl 16 or (color and 0x00FF00 shr 8) * amount.toInt() shl 8 or (color and 0x0000FF) * amount.toInt()

    /**
     * Get a drawable by name, from resources.
     * @param view used for accessing resources.
     * @param name The name of the drawable (without prefix).
     * @return The Drawable object.
     */
    @SuppressLint("DiscouragedApi")
    private fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, getIdentifier(name, "drawable", view.context.packageName), null
        )
    }

    /**
     *  Parses gravity attributes from a string.
     * @param value The string to parse.
     * @return The combined Gravity value (e.g. Gravity.START or Gravity.TOP)
     */
    private fun parseGravity(value: String): Int {
        var gravity = Gravity.NO_GRAVITY
        value.lowercase().split("[|]".toRegex()).forEach {
            gravity = gravity or when (it) {
                "center" -> Gravity.CENTER
                "left", "start" -> Gravity.START
                "right", "end" -> Gravity.END
                "top" -> Gravity.TOP
                "bottom" -> Gravity.BOTTOM
                "center_horizontal" -> Gravity.CENTER_HORIZONTAL
                "center_vertical" -> Gravity.CENTER_VERTICAL
                else -> Gravity.NO_GRAVITY
            }
        }
        return gravity
    }

    /**
     * Parses ID string by removing prefix
     * @param id The string id to parse.
     * @return the parsed id.
     */
    private fun parseID(id: String): String = id.removePrefix("@+id/")
}