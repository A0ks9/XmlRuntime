package com.flipkart.android.proteus.value

import android.content.Context
import android.util.LruCache
import android.util.TypedValue
import android.view.ViewGroup
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.toolbox.BiMap
import com.flipkart.android.proteus.toolbox.HashBiMap
import kotlin.math.roundToInt

class Dimension(val value: Double, val unit: Int) : Value() {


    companion object {
        const val DIMENSION_UNIT_ENUM = -1
        const val DIMENSION_UNIT_PX = TypedValue.COMPLEX_UNIT_PX
        const val DIMENSION_UNIT_DP = TypedValue.COMPLEX_UNIT_DIP
        const val DIMENSION_UNIT_SP = TypedValue.COMPLEX_UNIT_SP
        const val DIMENSION_UNIT_PT = TypedValue.COMPLEX_UNIT_PT
        const val DIMENSION_UNIT_IN = TypedValue.COMPLEX_UNIT_IN
        const val DIMENSION_UNIT_MM = TypedValue.COMPLEX_UNIT_MM

        const val MATCH_PARENT = "match_parent"
        const val FILL_PARENT = "fill_parent"
        const val WRAP_CONTENT = "wrap_content"

        const val SUFFIX_PX = "px"
        const val SUFFIX_DP = "dp"
        const val SUFFIX_SP = "sp"
        const val SUFFIX_PT = "pt"
        const val SUFFIX_IN = "in"
        const val SUFFIX_MM = "mm"


        private val dimensionsMap: BiMap<String, Int> = HashBiMap<String, Int>(3).apply {
            put(FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            put(MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            put(WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        private val dimensionsUnitsMap: BiMap<String, Int> = HashBiMap<String, Int>(6).apply {
            put(SUFFIX_PX, DIMENSION_UNIT_PX)
            put(SUFFIX_DP, DIMENSION_UNIT_DP)
            put(SUFFIX_SP, DIMENSION_UNIT_SP)
            put(SUFFIX_PT, DIMENSION_UNIT_PT)
            put(SUFFIX_IN, DIMENSION_UNIT_IN)
            put(SUFFIX_MM, DIMENSION_UNIT_MM)
        }

        @JvmField
        val ZERO = Dimension(0.0, DIMENSION_UNIT_PX)

        private val dimensionCache = LruCache<String, Dimension>(64)


        @JvmStatic
        fun valueOf(dimension: String?): Dimension {
            if (dimension.isNullOrEmpty()) return ZERO

            return dimensionCache.getOrPut(dimension) { createDimension(dimension) }
        }

        private fun createDimension(dimension: String): Dimension {

            dimensionsMap.getValue(dimension)?.let {
                return Dimension(it.toDouble(), DIMENSION_UNIT_ENUM)
            }

            if (dimension.length < 2) return ZERO

            val unit = dimensionsUnitsMap.getValue(dimension.takeLast(2)) ?: return ZERO

            val valueString = dimension.dropLast(2)

            return try {
                Dimension(ParseHelper.parseFloat(valueString).toDouble(), unit)
            } catch (e: NumberFormatException) {
                ZERO
            }
        }


        @JvmStatic
        fun apply(dimension: String, context: Context): Float {
            return valueOf(dimension).apply(context)
        }
    }


    fun apply(context: Context): Float = when (unit) {
        DIMENSION_UNIT_ENUM -> value.toFloat()
        DIMENSION_UNIT_PX, DIMENSION_UNIT_DP, DIMENSION_UNIT_SP, DIMENSION_UNIT_PT, DIMENSION_UNIT_MM, DIMENSION_UNIT_IN -> TypedValue.applyDimension(
            unit, value.toFloat(), context.resources.displayMetrics
        )

        else -> 0f
    }

    override fun copy(): Value = this

    override fun toString(): String {
        val valueString = if (value % 1 == 0.0) value.roundToInt().toString() else value.toString()

        return when (unit) {
            DIMENSION_UNIT_ENUM -> dimensionsMap.getKey(value.roundToInt()) ?: ""
            else -> valueString + (dimensionsUnitsMap.getKey(unit) ?: "")
        }
    }
}