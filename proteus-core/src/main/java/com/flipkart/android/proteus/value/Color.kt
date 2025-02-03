package com.flipkart.android.proteus.value

import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.LruCache
import android.util.StateSet
import androidx.annotation.ColorInt
import android.graphics.Color as AndroidColor
import kotlin.Array as array
import kotlin.Int as int

abstract class Color : Value() {

    companion object {

        private const val COLOR_PREFIX_LITERAL = "#"
        private val colorCache = LruCache<String, Color>(64)
        private val attributesMap = mutableMapOf(
            "type" to android.R.attr.type,
            "color" to android.R.attr.color,
            "alpha" to android.R.attr.alpha,
            "state_pressed" to android.R.attr.state_pressed,
            "state_focused" to android.R.attr.state_focused,
            "state_selected" to android.R.attr.state_selected,
            "state_checkable" to android.R.attr.state_checkable,
            "state_checked" to android.R.attr.state_checked,
            "state_enabled" to android.R.attr.state_enabled,
            "state_window_focused" to android.R.attr.state_window_focused
        )

        @JvmStatic
        fun valueOf(value: String?): Color = valueOf(value, Int.BLACK)


        @JvmStatic
        fun valueOf(value: String?, defaultValue: Color): Color =
            if (value.isNullOrEmpty()) defaultValue
            else colorCache.getOrPut(value) {
                if (isColor(value)) Int(AndroidColor.parseColor(value)) else defaultValue
            }

        @JvmStatic
        fun valueOf(value: ObjectValue, context: Context): Color = when {
            value.isPrimitive("type") && TextUtils.equals(
                value.asString("type"), "selector"
            ) -> if (value.isArray("children")) createSelectorColor(
                value["children"]?.asArray
            ) else Int.BLACK

            else -> Int.BLACK
        }

        private fun createSelectorColor(children: Array?): Color {
            val colorList = mutableListOf<int>()
            val stateSpecList = mutableListOf<IntArray>()

            val iterator = children?.iterator()
            while (iterator?.hasNext() == true) {
                val value = iterator.next()
                val child = value.asObject
                if (child.size() == 0) continue

                var baseColor: int? = null
                var alphaMod = 1.0f
                val stateSpec = mutableListOf<int>()
                var ignoreItem = false


                child.forEach { key, value ->
                    if (ignoreItem) return@forEach
                    if (!value.isPrimitive) return@forEach
                    val attributeId = attributesMap[key] ?: return@forEach
                    when (attributeId) {
                        android.R.attr.type -> if (!TextUtils.equals(
                                "item", value.asString()
                            )
                        ) ignoreItem = true

                        android.R.attr.color -> baseColor =
                            value.asString().takeIf { it.isNotEmpty() }?.let { apply(it) }

                        android.R.attr.alpha -> value.asString().takeIf { it.isNotEmpty() }
                            ?.let { alphaMod = it.toFloat() }

                        else -> stateSpec.add(if (value.asBoolean()) attributeId else -attributeId)
                    }
                }

                if (!ignoreItem) {
                    val trimmedStateSpec =
                        StateSet.trimStateSet(stateSpec.toIntArray(), stateSpec.size)
                    val actualColor = baseColor?.let { modulateColorAlpha(it, alphaMod) }
                        ?: throw IllegalStateException("No ColorValue Specified")
                    colorList.add(actualColor)
                    stateSpecList.add(trimmedStateSpec)
                }
            }
            return if (colorList.isNotEmpty()) StateList(
                stateSpecList.toTypedArray(), colorList.toIntArray()
            ) else Int.BLACK
        }

        private fun apply(value: String): int {
            return when (val color = valueOf(value)) {
                is Int -> color.value
                else -> Int.BLACK.value
            }
        }

        fun isColor(color: String): Boolean = color.startsWith(COLOR_PREFIX_LITERAL)

        private fun idealByteArraySize(need: int): int {
            for (i in 4 until 32) {
                if (need <= (1 shl i) - 12) {
                    return (1 shl i) - 12
                }
            }
            return need
        }

        private fun idealIntArraySize(need: int): int = idealByteArraySize(need * 4) / 4

        private fun constrain(amount: int, low: int, high: int): int =
            if (amount < low) low else if (amount > high) high else amount

        private fun modulateColorAlpha(baseColor: int, alphaMod: Float): int {
            if (alphaMod == 1.0f) return baseColor
            val baseAlpha = AndroidColor.alpha(baseColor)
            val alpha = constrain((baseAlpha * alphaMod + 0.5f).toInt(), 0, 255)
            return (baseColor and 0xFFFFFF) or (alpha shl 24)
        }
    }

    abstract fun apply(context: Context): Result

    data class Int(@ColorInt val value: int) : Color() {
        companion object {
            @JvmField
            val BLACK = Int(0)

            @JvmStatic
            fun valueOf(number: int): Int {
                if (number == 0) {
                    return BLACK
                }
                val value = number.toString()
                return colorCache.getOrPut(value) { Int(number) } as Int
            }
        }

        override fun copy(): Value = this
        override fun apply(context: Context): Result = Result.color(value)
    }

    data class StateList(val states: array<IntArray>, val colors: IntArray) : Color() {
        companion object {
            @JvmStatic
            fun valueOf(states: array<IntArray>, colors: IntArray): StateList =
                StateList(states, colors)
        }

        override fun copy(): Value = this
        override fun apply(context: Context): Result = Result.colors(ColorStateList(states, colors))
    }


    data class Result(val color: int, val colors: ColorStateList?) {
        companion object {
            @JvmStatic
            fun color(color: int) = Result(color, null)

            @JvmStatic
            fun colors(colors: ColorStateList) = Result(colors.defaultColor, colors)
        }
    }
}