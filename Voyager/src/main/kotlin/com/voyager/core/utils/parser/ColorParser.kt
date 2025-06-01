package com.voyager.core.utils.parser

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.voyager.core.model.ConfigManager // Assuming ConfigManager is now in core.model

/**
 * Utility object for parsing color strings and resources.
 */
object ColorParser {

    private val config = ConfigManager.config

    /**
     * Checks if this string likely represents a color value.
     * It currently checks if the string starts with "#" (for hex colors) or "@color/" (for color resources).
     *
     * @receiver The string to check.
     * @return `true` if the string format suggests a color value, `false` otherwise.
     */
    fun String.isColor(): Boolean = this.startsWith("#") || this.startsWith("@color/")

    /**
     * Gets a color from a string or resource.
     *
     * This function handles hex color strings (e.g., "#RRGGBB", "#AARRGGBB", "#RGB", "#ARGB")
     * and Android color resource names (e.g., "@color/my_color").
     *
     * For hex colors:
     * - "#RRGGBB" or "#RGB" are parsed directly.
     * - "#AARRGGBB" or "#ARGB" are parsed directly.
     * - Short hex formats like "#RGB" (length 4 including '#') are expanded to "#RRGGBB".
     * - Short hex formats like "#ARGB" (length 5 including '#') are expanded to "#AARRGGBB".
     *
     * For resource names:
     * - Strings starting with "@color/" are treated as resource names.
     * - The "@color/" prefix is removed, and the remaining string is used to look up
     *   the resource ID using the ConfigManager's provider.
     * - The color is then retrieved using ContextCompat.getColor.
     *
     * If the input string is null, or if a resource lookup fails, the `defaultColor` is returned.
     *
     * @param colorValue The color string or resource name (e.g., "#RRGGBB", "@color/my_color")
     * @param context The application context
     * @param defaultColor The default color to return if parsing fails. Defaults to Color.BLACK.
     * @return The parsed color value as an integer.
     */
    fun getColor(colorValue: String?, context: Context, defaultColor: Int = Color.BLACK): Int =
        colorValue?.removePrefix("@color/")?.let { c ->
            if (c.startsWith("#")) {
                // Handle hex colors
                when (c.length) {
                    // Expand short hex colors if necessary
                    4 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}".toColorInt() // #RGB -> #RRGGBB
                    5 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}${c[4]}${c[4]}".toColorInt() // #ARGB -> #AARRGGBB
                    // Assume full hex (6 or 8 chars) for other lengths
                    else -> c.toColorInt()
                }
            } else {
                // Handle color resources
                // Assuming ConfigManager.config.provider is available and has getResId
                config.provider.getResId("color", c).takeIf { it != 0 }
                    ?.let { ContextCompat.getColor(context, it) } ?: defaultColor
            }
        } ?: defaultColor // Return default color if input string is null
}