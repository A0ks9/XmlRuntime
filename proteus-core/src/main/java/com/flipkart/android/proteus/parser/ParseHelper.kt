package com.flipkart.android.proteus.parser

import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.value.Primitive
import com.flipkart.android.proteus.value.Value
import java.lang.reflect.Field

/**
 * Kotlin object containing utility functions for parsing and handling various attribute values in Proteus.
 */
object ParseHelper { // Converted class to object as it's a utility class with static members

    private const val TAG = "ParseHelper" // Converted to const val
    private const val FALSE = "false" // Converted to const val

    private const val VISIBLE = "visible" // Converted to const val
    private const val INVISIBLE = "invisible" // Converted to const val
    private const val GONE = "gone" // Converted to const val

    private const val CENTER = "center" // Converted to const val
    private const val CENTER_HORIZONTAL = "center_horizontal" // Converted to const val
    private const val CENTER_VERTICAL = "center_vertical" // Converted to const val
    private const val LEFT = "left" // Converted to const val
    private const val RIGHT = "right" // Converted to const val
    private const val TOP = "top" // Converted to const val
    private const val BOTTOM = "bottom" // Converted to const val
    private const val START = "start" // Converted to const val
    private const val END = "end" // Converted to const val
    private const val MIDDLE = "middle" // Converted to const val
    private const val BEGINNING = "beginning" // Converted to const val
    private const val MARQUEE = "marquee" // Converted to const val

    private const val BOLD = "bold" // Converted to const val
    private const val ITALIC = "italic" // Converted to const val
    private const val BOLD_ITALIC = "bold|italic" // Converted to const val

    private const val TEXT_ALIGNMENT_INHERIT = "inherit" // Converted to const val
    private const val TEXT_ALIGNMENT_GRAVITY = "gravity" // Converted to const val
    private const val TEXT_ALIGNMENT_CENTER = "center" // Converted to const val
    private const val TEXT_ALIGNMENT_TEXT_START = "start" // Converted to const val
    private const val TEXT_ALIGNMENT_TEXT_END = "end" // Converted to const val
    private const val TEXT_ALIGNMENT_VIEW_START = "viewStart" // Converted to const val
    private const val TEXT_ALIGNMENT_VIEW_END = "viewEnd" // Converted to const val

    private const val TWEEN_LOCAL_RESOURCE_STR = "@anim/" // Converted to const val

    private val sVisibilityMap = mutableMapOf<Int, Primitive>() // Converted to mutableMapOf
    private val sGravityMap = mutableMapOf<String, Primitive>() // Converted to mutableMapOf
    private val sDividerMode = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sEllipsizeMode =
        mutableMapOf<String, TextUtils.TruncateAt>() // Converted to mutableMapOf, Enum inference
    private val sVisibilityMode = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sTextAlignment = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sImageScaleType =
        mutableMapOf<String, ImageView.ScaleType>() // Converted to mutableMapOf, Enum inference

    init { // Static initializer in Java becomes init block in Kotlin object

        sVisibilityMap[View.VISIBLE] = Primitive(View.VISIBLE) // Using map[key] = value syntax
        sVisibilityMap[View.INVISIBLE] = Primitive(View.INVISIBLE)
        sVisibilityMap[View.GONE] = Primitive(View.GONE)

        sGravityMap[CENTER] = Primitive(Gravity.CENTER)
        sGravityMap[CENTER_HORIZONTAL] = Primitive(Gravity.CENTER_HORIZONTAL)
        sGravityMap[CENTER_VERTICAL] = Primitive(Gravity.CENTER_VERTICAL)
        sGravityMap[LEFT] = Primitive(Gravity.LEFT)
        sGravityMap[RIGHT] = Primitive(Gravity.RIGHT)
        sGravityMap[TOP] = Primitive(Gravity.TOP)
        sGravityMap[BOTTOM] = Primitive(Gravity.BOTTOM)
        sGravityMap[START] = Primitive(Gravity.START)
        sGravityMap[END] = Primitive(Gravity.END)

        sDividerMode[END] = LinearLayout.SHOW_DIVIDER_END
        sDividerMode[MIDDLE] = LinearLayout.SHOW_DIVIDER_MIDDLE
        sDividerMode[BEGINNING] = LinearLayout.SHOW_DIVIDER_BEGINNING

        sEllipsizeMode[END] = TextUtils.TruncateAt.END
        sEllipsizeMode[START] = TextUtils.TruncateAt.START
        sEllipsizeMode[MARQUEE] = TextUtils.TruncateAt.MARQUEE
        sEllipsizeMode[MIDDLE] = TextUtils.TruncateAt.MIDDLE

        sVisibilityMode[VISIBLE] = View.VISIBLE
        sVisibilityMode[INVISIBLE] = View.INVISIBLE
        sVisibilityMode[GONE] = View.GONE

        sImageScaleType[CENTER] = ImageView.ScaleType.CENTER
        sImageScaleType["center_crop"] = ImageView.ScaleType.CENTER_CROP
        sImageScaleType["center_inside"] = ImageView.ScaleType.CENTER_INSIDE
        sImageScaleType["fitCenter"] = ImageView.ScaleType.FIT_CENTER
        sImageScaleType["fit_xy"] = ImageView.ScaleType.FIT_XY
        sImageScaleType["matrix"] = ImageView.ScaleType.MATRIX

        sTextAlignment[TEXT_ALIGNMENT_INHERIT] = View.TEXT_ALIGNMENT_INHERIT
        sTextAlignment[TEXT_ALIGNMENT_GRAVITY] = View.TEXT_ALIGNMENT_GRAVITY
        sTextAlignment[TEXT_ALIGNMENT_CENTER] = View.TEXT_ALIGNMENT_CENTER
        sTextAlignment[TEXT_ALIGNMENT_TEXT_START] = View.TEXT_ALIGNMENT_TEXT_START
        sTextAlignment[TEXT_ALIGNMENT_TEXT_END] = View.TEXT_ALIGNMENT_TEXT_END
        sTextAlignment[TEXT_ALIGNMENT_VIEW_START] = View.TEXT_ALIGNMENT_VIEW_START
        sTextAlignment[TEXT_ALIGNMENT_VIEW_END] = View.TEXT_ALIGNMENT_VIEW_END
    }

    /**
     * Parses an integer from a string attribute value, returning 0 if parsing fails.
     */
    fun parseInt(attributeValue: String?): Int { // Converted to Kotlin function, nullable String
        if (ProteusConstants.DATA_NULL == attributeValue) return 0 // Early return if attributeValue is DATA_NULL
        return try {
            attributeValue?.toInt()
                ?: 0 // Safe toInt conversion using ?.toInt() ?: 0 elvis operator
        } catch (e: NumberFormatException) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "$attributeValue is NAN. Error: ${e.message}") // String interpolation
            }
            0
        }
            ?: 0 // Added another elvis operator just in case toIntOrNull could theoretically return null if value is null
    }


    /**
     * Parses an integer from a string attribute value without exception handling, returns IntResult to indicate success/failure.
     */
    fun parseIntUnsafe(s: String?): IntResult { // Converted to Kotlin function, nullable String
        s ?: return IntResult("null string") // Null check with early return using elvis operator

        var num: Int
        val len = s.length
        val ch = s[0]
        val d = ch - '0'
        if (d < 0 || d > 9) {
            return IntResult("Malformed:  $s") // String interpolation
        }
        num = d

        var i = 1
        while (i < len) {
            val digit = s[i++] - '0'
            if (digit < 0 || digit > 9) {
                return IntResult("Malformed:  $s") // String interpolation
            }
            num *= 10
            num += digit
        }

        return IntResult(null, num) // Success result
    }

    /**
     * Parses a float from a string value, returning 0f if parsing fails.
     */
    fun parseFloat(value: String?): Float { // Converted to Kotlin function, nullable String
        return try {
            value?.toFloat() ?: 0f // Safe toFloat conversion using ?.toFloat() ?: 0f elvis operator
        } catch (e: NumberFormatException) {
            0f
        }
            ?: 0f // Added another elvis operator just in case toFloatOrNull could theoretically return null if value is null
    }

    /**
     * Parses a double from a string attribute value, returning 0.0 if parsing fails.
     */
    fun parseDouble(attributeValue: String?): Double { // Converted to Kotlin function, nullable String
        if (ProteusConstants.DATA_NULL == attributeValue) return 0.0 // Early return if attributeValue is DATA_NULL
        return try {
            attributeValue?.toDouble()
                ?: 0.0 // Safe toDouble conversion using ?.toDouble() ?: 0.0 elvis operator
        } catch (e: NumberFormatException) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "$attributeValue is NAN. Error: ${e.message}") // String interpolation
            }
            0.0
        }
            ?: 0.0 // Added another elvis operator just in case toDoubleOrNull could theoretically return null if value is null
    }

    /**
     * Parses gravity from a string value (e.g., "center|top").
     */
    fun parseGravity(value: String?): Int { // Converted to Kotlin function, nullable String
        value ?: return Gravity.NO_GRAVITY // Null check with early return using elvis operator

        val gravities = value.split("\\|") // Split by "|"
        var returnGravity = Gravity.NO_GRAVITY
        for (gravity in gravities) {
            sGravityMap[gravity]?.let { gravityValue -> // Safe map access using ?.let
                returnGravity =
                    returnGravity or gravityValue.asInt() // Use 'or' (bitwise OR) in Kotlin
            }
        }
        return returnGravity
    }

    /**
     * Gets a Primitive Value for a gravity value string.
     */
    fun getGravity(value: String): Primitive { // Converted to Kotlin function
        return Primitive(parseGravity(value))
    }

    /**
     * Parses divider mode from a string attribute value.
     */
    fun parseDividerMode(attributeValue: String?): Int { // Converted to Kotlin function, nullable String
        return sDividerMode[attributeValue]
            ?: LinearLayout.SHOW_DIVIDER_NONE // Safe map access with elvis operator for default value
    }

    /**
     * Parses ellipsize mode from a string attribute value.
     */
    fun parseEllipsize(attributeValue: String?): TextUtils.TruncateAt { // Converted to Kotlin function, nullable String
        return sEllipsizeMode[attributeValue]
            ?: TextUtils.TruncateAt.END // Safe map access with elvis operator for default value
    }

    /**
     * Parses visibility from a Value (can be a string or null).
     */
    fun parseVisibility(value: Value?): Int { // Converted to Kotlin function, nullable Value
        var returnValue: Int? = null
        if (value != null && value.isPrimitive) { //value?.isPrimitive()  safe call and null check combined
            val attributeValue = value.asString()
            returnValue = sVisibilityMode[attributeValue]
            if (returnValue == null && (attributeValue.isEmpty() || FALSE == attributeValue || ProteusConstants.DATA_NULL == attributeValue)) {
                returnValue = View.GONE
            }
        } else if (value == null || value.isNull) { //Simplified null check condition in kotlin and using || (or)
            returnValue = View.GONE
        }
        return returnValue ?: View.VISIBLE // Elvis operator for default visibility
    }

    /**
     * Gets a Primitive Value for a visibility integer code.
     */
    fun getVisibility(visibility: Int): Primitive { // Converted to Kotlin function
        return sVisibilityMap[visibility]
            ?: sVisibilityMap[View.GONE]!! // Safe map access with elvis operator, non-null assertion for default (assuming GONE is always in map)
    }

    /**
     * Parses a color from a color string (e.g., "#RRGGBB" or named color).
     */
    fun parseColor(color: String?): Int { // Converted to Kotlin function, nullable String
        return try {
            Color.parseColor(color)
        } catch (ex: IllegalArgumentException) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Invalid color : $color. Using #000000") // String interpolation
            }
            Color.BLACK // Default to Color.BLACK in case of exception
        }
    }

    /**
     * Parses a boolean value from a Value (can be a string, boolean primitive, or null).
     */
    fun parseBoolean(value: Value?): Boolean { // Converted to Kotlin function, nullable Value
        return value?.let { // Using let scope function for concise null handling and chaining
            if (it.isPrimitive && it.asPrimitive.isBoolean()) { // Check if Value is Primitive and boolean
                it.asBoolean() // Return boolean value if it is
            } else {
                !it.isNull && value.asString()
                    .toBoolean() // If not primitive boolean, check for null and try parsing string to boolean
            }
        } == true // Elvis operator: if value is null, return false as default
    }

    /**
     * Converts a boolean value to a RelativeLayout rule constant (RelativeLayout.TRUE or 0).
     */
    fun parseRelativeLayoutBoolean(value: Boolean): Int { // Converted to Kotlin function
        return if (value) RelativeLayout.TRUE else 0 // Kotlin 'if' is an expression, more concise
    }

    /**
     * Adds a RelativeLayout rule to a View's layout parameters.
     */
    fun addRelativeLayoutRule(view: View, verb: Int, anchor: Int) { // Converted to Kotlin function
        val layoutParams = view.layoutParams
        if (layoutParams is RelativeLayout.LayoutParams) { // Using 'is' for type check and smart cast
            val params = layoutParams // Smart cast, no explicit cast needed
            params.addRule(verb, anchor)
            view.layoutParams = params
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "cannot add relative layout rules when container is not relative")
            }
        }
    }

    /**
     * Parses text style from a string attribute value (e.g., "bold", "italic").
     */
    fun parseTextStyle(attributeValue: String?): Int { // Converted to Kotlin function, nullable String
        return when (attributeValue?.lowercase()) { // Using when expression, safe call ?.lowercase() and null check
            BOLD -> Typeface.BOLD
            ITALIC -> Typeface.ITALIC
            BOLD_ITALIC -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL // Default case
        }
    }

    /**
     * Checks if an attribute value string indicates a tween animation resource.
     */
    fun isTweenAnimationResource(attributeValue: String?): Boolean { // Converted to Kotlin function, nullable String
        return attributeValue?.startsWith(TWEEN_LOCAL_RESOURCE_STR) == true // Safe call ?.startsWith and elvis for null handling
    }

    /**
     * Uses reflection to get a resource ID from a class.
     * Faster than Resources.getResourceName().
     */
    fun getResId(
        variableName: String, klass: Class<*>
    ): Int { // Converted to Kotlin function, Class<?> becomes Class<*>
        var resId = 0
        try {
            val field: Field? = klass.getField(variableName) // getField can return null
            resId = field?.getInt(null)
                ?: 0 // Safe call ?.getInt with elvis operator for default 0 if field or getInt fails
        } catch (e: Exception) { // Catching generic Exception to handle various reflection exceptions
            e.printStackTrace() // Still print stack trace for reflection errors
        }
        return resId
    }

    /**
     * Gets Android XML resource ID from a full resource ID string (e.g., "@+android:id/text1").
     * Only works for @android:id or @+android:id.
     */
    fun getAndroidXmlResId(fullResIdString: String?): Int { // Converted to Kotlin function, nullable String
        fullResIdString ?: return View.NO_ID // Null check with early return using elvis operator

        val i = fullResIdString.indexOf("/")
        if (i >= 0) {
            val idString = fullResIdString.substring(i + 1)
            return getResId(
                idString, android.R.id::class.java
            ) // Use ::class.java for Kotlin reflection
        }
        return View.NO_ID // Default value if not found or invalid format
    }

    /**
     * Parses an ImageView.ScaleType from a string attribute value.
     */
    fun parseScaleType(attributeValue: String?): ImageView.ScaleType? { // Converted to Kotlin function, nullable String, nullable return type
        return if (!TextUtils.isEmpty(attributeValue)) sImageScaleType[attributeValue] else null // Kotlin 'if' as expression, safe map access, null if empty or not found
    }

    /**
     * Parses text alignment from a string attribute value.
     */
    fun parseTextAlignment(attributeValue: String?): Int? { // Converted to Kotlin function, nullable String, nullable return type
        return if (!TextUtils.isEmpty(attributeValue)) sTextAlignment[attributeValue] else null // Kotlin 'if' as expression, safe map access, null if empty or not found
    }


    /**
     * Data class to hold the result of parseIntUnsafe, including potential error message.
     */
    data class IntResult( // Converted inner class to data class, more concise in Kotlin
        val error: String?, // Kept nullable as in Java, but could also consider Kotlin Result type for error handling
        val result: Int = -1 // Default value for result, similar to Java, but more explicit with default parameter in constructor
    )
}