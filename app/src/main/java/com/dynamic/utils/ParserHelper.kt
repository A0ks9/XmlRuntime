package com.dynamic.utils

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import java.lang.reflect.Field
import kotlin.collections.get

/**
 * Kotlin object containing utility functions for parsing and handling various attribute values in Proteus.
 */
@SuppressLint("RtlHardcoded")
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
    private const val CENTER_CROP = "centerCrop"
    private const val CENTER_INSIDE = "centerInside"
    private const val FIT_CENTER = "fitCenter"
    private const val FIT_END = "fitEnd"
    private const val FIT_START = "fitStart"
    private const val FIT_XY = "fitXY"
    private const val MATRIX = "matrix"

    private const val BOLD = "bold" // Converted to const val
    private const val ITALIC = "italic" // Converted to const val
    private const val BOLD_ITALIC = "bold|italic" // Converted to const val

    private const val TEXT_ALIGNMENT_INHERIT = "inherit" // Converted to const val
    private const val TEXT_ALIGNMENT_GRAVITY = "gravity" // Converted to const val
    private const val TEXT_ALIGNMENT_CENTER = "center" // Converted to const val
    private const val TEXT_ALIGNMENT_TEXT_START = "textStart" // Converted to const val
    private const val TEXT_ALIGNMENT_TEXT_END = "textEnd" // Converted to const val
    private const val TEXT_ALIGNMENT_VIEW_START = "viewStart" // Converted to const val
    private const val TEXT_ALIGNMENT_VIEW_END = "viewEnd" // Converted to const val

    private const val DATE = "date"
    private const val DATE_TIME = "datetime"
    private const val NONE = "none"
    private const val NUMBER = "number"
    private const val NUMBER_DECIMAL = "numberDecimal"
    private const val NUMBER_PASSWORD = "numberPassword"
    private const val NUMBER_SIGNED = "numberSigned"
    private const val PHONE = "phone"
    private const val TEXT = "text"
    private const val TEXT_AUTOCOMPLETE = "textAutoComplete"
    private const val TEXT_AUTOCORRECT = "textAutoCorrect"
    private const val TEXT_CAP_CHARACTERS = "textCapCharacters"
    private const val TEXT_CAP_SENTENCES = "textCapSentences"
    private const val TEXT_CAP_WORDS = "textCapWords"
    private const val TEXT_EMAIL_ADDRESS = "textEmailAddress"
    private const val TEXT_EMAIL_SUBJECT = "textEmailSubject"
    private const val TEXT_ENABLE_TEXT_CONVERSION_SUGGESTIONS =
        "textEnableTextConversionSuggestions"
    private const val TEXT_FILTER = "textFilter"
    private const val TEXT_IME_MULTILINE = "textImeMultiLine"
    private const val TEXT_LONG_MESSAGE = "textLongMessage"
    private const val TEXT_MULTILINE = "textMultiLine"
    private const val TEXT_NO_SUGGESTIONS = "textNoSuggestions"
    private const val TEXT_PASSWORD = "textPassword"
    private const val TEXT_PERSON_NAME = "textPersonName"
    private const val TEXT_PHONETIC = "textPhonetic"
    private const val TEXT_POSTAL_ADDRESS = "textPostalAddress"
    private const val TEXT_SHORT_MESSAGE = "textShortMessage"
    private const val TEXT_URI = "textUri"
    private const val TEXT_VISIBLE_PASSWORD = "textVisiblePassword"
    private const val TEXT_WEB_EDITTEXT = "textWebEditText"
    private const val TEXT_WEB_EMAIL_ADDRESS = "textWebEmailAddress"
    private const val TEXT_WEB_PASSWORD = "textWebPassword"
    private const val TIME = "time"

    private const val TWEEN_LOCAL_RESOURCE_STR = "@anim/" // Converted to const val

    private val sVisibilityMap = mutableMapOf<Int, Int>() // Converted to mutableMapOf
    private val sGravityMap = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sDividerMode = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sEllipsizeMode =
        mutableMapOf<String, TextUtils.TruncateAt>() // Converted to mutableMapOf, Enum inference
    private val sVisibilityMode = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sTextAlignment = mutableMapOf<String, Int>() // Converted to mutableMapOf
    private val sImageScaleType =
        mutableMapOf<String, ImageView.ScaleType>() // Converted to mutableMapOf, Enum inference
    private val sInputType = mutableMapOf<String, Int>()

    init { // Static initializer in Java becomes init block in Kotlin object

        sVisibilityMap[View.VISIBLE] = View.VISIBLE // Using map[key] = value syntax
        sVisibilityMap[View.INVISIBLE] = View.INVISIBLE
        sVisibilityMap[View.GONE] = View.GONE

        sGravityMap[CENTER] = Gravity.CENTER
        sGravityMap[CENTER_HORIZONTAL] = Gravity.CENTER_HORIZONTAL
        sGravityMap[CENTER_VERTICAL] = Gravity.CENTER_VERTICAL
        sGravityMap[LEFT] = Gravity.LEFT
        sGravityMap[RIGHT] = Gravity.RIGHT
        sGravityMap[TOP] = Gravity.TOP
        sGravityMap[BOTTOM] = Gravity.BOTTOM
        sGravityMap[START] = Gravity.START
        sGravityMap[END] = Gravity.END

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
        sImageScaleType[CENTER_CROP] = ImageView.ScaleType.CENTER_CROP
        sImageScaleType[CENTER_INSIDE] = ImageView.ScaleType.CENTER_INSIDE
        sImageScaleType[FIT_CENTER] = ImageView.ScaleType.FIT_CENTER
        sImageScaleType[FIT_XY] = ImageView.ScaleType.FIT_XY
        sImageScaleType[FIT_END] = ImageView.ScaleType.FIT_END
        sImageScaleType[FIT_START] = ImageView.ScaleType.FIT_START
        sImageScaleType[MATRIX] = ImageView.ScaleType.MATRIX

        sTextAlignment[TEXT_ALIGNMENT_INHERIT] = View.TEXT_ALIGNMENT_INHERIT
        sTextAlignment[TEXT_ALIGNMENT_GRAVITY] = View.TEXT_ALIGNMENT_GRAVITY
        sTextAlignment[TEXT_ALIGNMENT_CENTER] = View.TEXT_ALIGNMENT_CENTER
        sTextAlignment[TEXT_ALIGNMENT_TEXT_START] = View.TEXT_ALIGNMENT_TEXT_START
        sTextAlignment[TEXT_ALIGNMENT_TEXT_END] = View.TEXT_ALIGNMENT_TEXT_END
        sTextAlignment[TEXT_ALIGNMENT_VIEW_START] = View.TEXT_ALIGNMENT_VIEW_START
        sTextAlignment[TEXT_ALIGNMENT_VIEW_END] = View.TEXT_ALIGNMENT_VIEW_END

        sInputType[DATE] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        sInputType[DATE_TIME] =
            InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
        sInputType[NONE] = InputType.TYPE_NULL
        sInputType[NUMBER] = InputType.TYPE_CLASS_NUMBER
        sInputType[NUMBER_DECIMAL] =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        sInputType[NUMBER_PASSWORD] =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        sInputType[NUMBER_SIGNED] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        sInputType[PHONE] = InputType.TYPE_CLASS_PHONE
        sInputType[TEXT] = InputType.TYPE_CLASS_TEXT
        sInputType[TEXT_AUTOCOMPLETE] = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        sInputType[TEXT_AUTOCORRECT] = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        sInputType[TEXT_CAP_CHARACTERS] = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        sInputType[TEXT_CAP_SENTENCES] = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        sInputType[TEXT_CAP_WORDS] = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        sInputType[TEXT_EMAIL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        sInputType[TEXT_EMAIL_SUBJECT] = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) sInputType[TEXT_ENABLE_TEXT_CONVERSION_SUGGESTIONS] =
            InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS

        sInputType[TEXT_FILTER] = InputType.TYPE_TEXT_VARIATION_FILTER
        sInputType[TEXT_IME_MULTILINE] = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
        sInputType[TEXT_LONG_MESSAGE] = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
        sInputType[TEXT_MULTILINE] = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        sInputType[TEXT_NO_SUGGESTIONS] = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        sInputType[TEXT_PASSWORD] = InputType.TYPE_TEXT_VARIATION_PASSWORD
        sInputType[TEXT_PERSON_NAME] = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        sInputType[TEXT_PHONETIC] = InputType.TYPE_TEXT_VARIATION_PHONETIC
        sInputType[TEXT_POSTAL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        sInputType[TEXT_SHORT_MESSAGE] = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        sInputType[TEXT_URI] = InputType.TYPE_TEXT_VARIATION_URI
        sInputType[TEXT_VISIBLE_PASSWORD] = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        sInputType[TEXT_WEB_EDITTEXT] = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        sInputType[TEXT_WEB_EMAIL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        sInputType[TEXT_WEB_PASSWORD] = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        sInputType[TIME] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
    }

    /**
     * Parses an integer from a string attribute value, returning 0 if parsing fails.
     */
    fun parseInt(attributeValue: String?): Int { // Converted to Kotlin function, nullable String
        if (attributeValue == null || attributeValue == "") return 0 // Early return if attributeValue is DATA_NULL
        return try {
            attributeValue.toInt()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "$attributeValue is NAN. Error: ${e.message}") // String interpolation
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
        if (attributeValue == null || attributeValue == "") return 0.0 // Early return if attributeValue is NULL
        return try {
            attributeValue.toDouble()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "$attributeValue is NAN. Error: ${e.message}") // String interpolation
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
                returnGravity = returnGravity or gravityValue // Use 'or' (bitwise OR) in Kotlin
            }
        }
        return returnGravity
    }

    /**
     * Gets a Value for a gravity value string.
     */
    fun getGravity(value: String): Int { // Converted to Kotlin function
        return parseGravity(value)
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
    fun parseVisibility(value: String?): Int { // Converted to Kotlin function, nullable Value
        var returnValue: Int? = null
        if (value != null && value.isVisibility() && !value.isInteger()) {
            returnValue = sVisibilityMode[value]
            if (returnValue == null && (value.isEmpty() || FALSE == value || "null" == value)) {
                returnValue = View.GONE
            }
        } else if (value != null && value.isInteger()) {
            returnValue = sVisibilityMap[value.toInt()]
        } else if (value == null) { //Simplified null check condition in kotlin and using || (or)
            returnValue = View.GONE
        }
        return returnValue ?: View.VISIBLE // Elvis operator for default visibility
    }

    /**
     * Gets a Int Value for a visibility integer code.
     */
    fun getVisibility(visibility: Int): Int { // Converted to Kotlin function
        return sVisibilityMap[visibility]
            ?: sVisibilityMap[View.GONE]!! // Safe map access with elvis operator, non-null assertion for default (assuming GONE is always in map)
    }

    /**
     * Converts a JSON color string into an Android color integer.
     *
     * This function handles:
     * - Hex color strings (e.g., "#RRGGBB" or "#AARRGGBB")
     * - Shorthand hex codes (e.g., "#RGB" or "#ARGB")
     * - Named color resources, with an optional "@color/" prefix.
     *
     * @param color The color string from JSON.
     * @return The corresponding color as an Int, or Color.BLACK if the color is invalid.
     */
    @SuppressLint("DiscouragedApi")
    fun getColor(color: String?, context: Context): Int =
        // Remove the "@color/" prefix if it exists, then process the string.
        color?.removePrefix("@color/")?.let { c ->
            // If the string starts with "#", treat it as a hex color code.
            if (c.startsWith("#")) {
                // Expand shorthand hex colors if necessary.
                // For example: "#ABC" becomes "#AABBCC" and "#FABC" becomes "#FFAABBCC"
                val hex = when (c.length) {
                    4 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}"
                    5 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}${c[4]}${c[4]}"
                    else -> c // Use the original string if it's not in shorthand format.
                }
                // Parse the hex string into a color integer.
                Color.parseColor(hex)
            } else {
                // Otherwise, treat the string as a resource name.
                // Get the resource identifier for the color in the "color" resource type.
                val res = context.resources.getIdentifier(c, "color", context.packageName)
                // If the resource is found, return the color from the resources; otherwise, return Color.BLACK.
                if (res != 0) ContextCompat.getColor(context, res) else Color.BLACK
            }
        } ?: Color.BLACK // If the input is null or empty, return Color.BLACK.


    /**
     * Parses a boolean value from a Value (can be a string, boolean or null).
     */
    fun parseBoolean(value: String?): Boolean { // Converted to Kotlin function, nullable Value
        return value?.let { // Using let scope function for concise null handling and chaining
            if (value.isBoolean()) { // Check if Value is Primitive and boolean
                it.asBoolean() // Return boolean value if it is
            } else {
                it.toBoolean() // If not primitive boolean, check for null and try parsing string to boolean
            }
        } == true // Elvis operator: if value is null, return false as default
    }

    /**
     * Get a drawable by name, from resources.
     * @param view used for accessing resources.
     * @param name The name of the drawable (without prefix).
     * @return The Drawable object.
     */
    @SuppressLint("DiscouragedApi")
    fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, getIdentifier(name, "drawable", view.context.packageName), null
        )
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
            Log.e(TAG, "cannot add relative layout rules when container is not relative")
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
                idString, R.id::class.java
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

    fun parseInputType(attributeValue: String): Int {
        return sInputType[attributeValue] ?: InputType.TYPE_CLASS_TEXT
    }

    /**
     * Data class to hold the result of parseIntUnsafe, including potential error message.
     */
    data class IntResult( // Converted inner class to data class, more concise in Kotlin
        val error: String?, // Kept nullable as in Java, but could also consider Kotlin Result type for error handling
        val result: Int = -1 // Default value for result, similar to Java, but more explicit with default parameter in constructor
    )
}