package com.voyager.core.utils.parser

import android.widget.ImageView
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing ImageView-specific properties from strings.
 */
object ImageViewParser {

    private val sImageScaleType = ConcurrentHashMap<String, ImageView.ScaleType>().apply {
        this["center"] = ImageView.ScaleType.CENTER
        this["centerCrop"] = ImageView.ScaleType.CENTER_CROP
        this["centerInside"] = ImageView.ScaleType.CENTER_INSIDE
        this["fitCenter"] = ImageView.ScaleType.FIT_CENTER
        this["fitXY"] = ImageView.ScaleType.FIT_XY
        this["fitEnd"] = ImageView.ScaleType.FIT_END
        this["fitStart"] = ImageView.ScaleType.FIT_START
        this["matrix"] = ImageView.ScaleType.MATRIX
    }

    /**
     * Parses a scale type string to an ImageView.ScaleType.
     *
     * @param attributeValue The scale type string (e.g., "centerCrop", "fitXY")
     * @return The parsed ScaleType or null if not found
     */
    fun parseScaleType(attributeValue: String?): ImageView.ScaleType? =
        attributeValue?.takeUnless { it.isEmpty() }?.let { sImageScaleType[it] }
} 