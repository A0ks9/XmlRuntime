/**
 * Efficient parser utility for Android view attributes and resources.
 *
 * This utility provides optimized parsing and conversion of various Android view attributes,
 * including colors, dimensions, gravity, visibility, and more. It uses thread-safe caching
 * and efficient data structures for better performance.
 *
 * Key features:
 * - Thread-safe attribute parsing
 * - Efficient resource resolution
 * - Optimized string operations
 * - Comprehensive view attribute support
 * - Memory-efficient caching
 *
 * Performance optimizations:
 * - ConcurrentHashMap for thread-safe caching
 * - Pre-computed constant maps
 * - Efficient string parsing
 * - Minimized object creation
 * - Optimized resource lookup
 *
 * Usage example:
 * ```kotlin
 * // Parse color
 * val color = ParseHelper.getColor("#FF0000", context)
 *
 * // Parse gravity
 * val gravity = ParseHelper.parseGravity("center|bottom")
 *
 * // Parse visibility
 * val visibility = ParseHelper.parseVisibility("gone")
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.voyager.data.models.ConfigManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.javaField

@SuppressLint("RtlHardcoded")
object ParseHelper {
    private const val TAG = "ParseHelper"

    // Constants for frequently used strings
    private object Constants {
        const val VISIBLE = "visible"
        const val INVISIBLE = "invisible"
        const val GONE = "gone"
        const val CENTER = "center"
        const val CENTER_HORIZONTAL = "center_horizontal"
        const val CENTER_VERTICAL = "center_vertical"
        const val LEFT = "left"
        const val RIGHT = "right"
        const val TOP = "top"
        const val BOTTOM = "bottom"
        const val START = "start"
        const val END = "end"
        const val FILL = "fill"
        const val FILL_VERTICAL = "fill_vertical"
        const val FILL_HORIZONTAL = "fill_horizontal"
        const val CLIP_VERTICAL = "clip_vertical"
        const val CLIP_HORIZONTAL = "clip_horizontal"
        const val MIDDLE = "middle"
        const val BEGINNING = "beginning"
        const val MARQUEE = "marquee"
        const val CENTER_CROP = "centerCrop"
        const val CENTER_INSIDE = "centerInside"
        const val FIT_CENTER = "fitCenter"
        const val FIT_END = "fitEnd"
        const val FIT_START = "fitStart"
        const val FIT_XY = "fitXY"
        const val MATRIX = "matrix"
        const val BOLD = "bold"
        const val ITALIC = "italic"
        const val BOLD_ITALIC = "bold|italic"
        const val TEXT_ALIGNMENT_INHERIT = "inherit"
        const val TEXT_ALIGNMENT_GRAVITY = "gravity"
        const val TEXT_ALIGNMENT_CENTER = "center"
        const val TEXT_ALIGNMENT_TEXT_START = "textStart"
        const val TEXT_ALIGNMENT_TEXT_END = "textEnd"
        const val TEXT_ALIGNMENT_VIEW_START = "viewStart"
        const val TEXT_ALIGNMENT_VIEW_END = "viewEnd"
        const val TWEEN_LOCAL_RESOURCE_STR = "@anim/"
        const val ADD = "add"
        const val MULTIPLY = "multiply"
        const val SCREEN = "screen"
        const val SRC_ATOP = "src_atop"
        const val SRC_IN = "src_in"
        const val SRC_OVER = "src_over"
        const val AUTO = "auto"
        const val HIGH = "high"
        const val LOW = "low"
        const val ALWAYS = "always"
        const val IF_CONTENT_SCROLLS = "ifContentScrolls"
        const val NEVER = "never"
        const val YES = "yes"
        const val NO = "no"
        const val NO_HIDE_DESCENDANTS = "noHideDescendants"
    }

    // Thread-safe maps for attribute parsing
    private val sVisibilityMap = ConcurrentHashMap<Int, Int>().apply {
        this[View.VISIBLE] = View.VISIBLE
        this[View.INVISIBLE] = View.INVISIBLE
        this[View.GONE] = View.GONE
    }

    private val sGravityMap = ConcurrentHashMap<String, Int>().apply {
        this[Constants.CENTER] = Gravity.CENTER
        this[Constants.CENTER_HORIZONTAL] = Gravity.CENTER_HORIZONTAL
        this[Constants.CENTER_VERTICAL] = Gravity.CENTER_VERTICAL
        this[Constants.LEFT] = Gravity.LEFT
        this[Constants.RIGHT] = Gravity.RIGHT
        this[Constants.TOP] = Gravity.TOP
        this[Constants.BOTTOM] = Gravity.BOTTOM
        this[Constants.START] = Gravity.START
        this[Constants.END] = Gravity.END
        this[Constants.FILL] = Gravity.FILL
        this[Constants.FILL_VERTICAL] = Gravity.FILL_VERTICAL
        this[Constants.FILL_HORIZONTAL] = Gravity.FILL_HORIZONTAL
        this[Constants.CLIP_VERTICAL] = Gravity.CLIP_VERTICAL
        this[Constants.CLIP_HORIZONTAL] = Gravity.CLIP_HORIZONTAL
    }

    private val sDividerMode = ConcurrentHashMap<String, Int>().apply {
        this[Constants.END] = LinearLayout.SHOW_DIVIDER_END
        this[Constants.MIDDLE] = LinearLayout.SHOW_DIVIDER_MIDDLE
        this[Constants.BEGINNING] = LinearLayout.SHOW_DIVIDER_BEGINNING
    }

    private val sEllipsizeMode = ConcurrentHashMap<String, TextUtils.TruncateAt>().apply {
        this[Constants.END] = TextUtils.TruncateAt.END
        this[Constants.START] = TextUtils.TruncateAt.START
        this[Constants.MARQUEE] = TextUtils.TruncateAt.MARQUEE
        this[Constants.MIDDLE] = TextUtils.TruncateAt.MIDDLE
    }

    private val sVisibilityMode = ConcurrentHashMap<String, Int>().apply {
        this[Constants.VISIBLE] = View.VISIBLE
        this[Constants.INVISIBLE] = View.INVISIBLE
        this[Constants.GONE] = View.GONE
    }

    private val sTextAlignment = ConcurrentHashMap<String, Int>().apply {
        this[Constants.TEXT_ALIGNMENT_INHERIT] = View.TEXT_ALIGNMENT_INHERIT
        this[Constants.TEXT_ALIGNMENT_GRAVITY] = View.TEXT_ALIGNMENT_GRAVITY
        this[Constants.TEXT_ALIGNMENT_CENTER] = View.TEXT_ALIGNMENT_CENTER
        this[Constants.TEXT_ALIGNMENT_TEXT_START] = View.TEXT_ALIGNMENT_TEXT_START
        this[Constants.TEXT_ALIGNMENT_TEXT_END] = View.TEXT_ALIGNMENT_TEXT_END
        this[Constants.TEXT_ALIGNMENT_VIEW_START] = View.TEXT_ALIGNMENT_VIEW_START
        this[Constants.TEXT_ALIGNMENT_VIEW_END] = View.TEXT_ALIGNMENT_VIEW_END
    }

    private val sImageScaleType = ConcurrentHashMap<String, ImageView.ScaleType>().apply {
        this[Constants.CENTER] = ImageView.ScaleType.CENTER
        this[Constants.CENTER_CROP] = ImageView.ScaleType.CENTER_CROP
        this[Constants.CENTER_INSIDE] = ImageView.ScaleType.CENTER_INSIDE
        this[Constants.FIT_CENTER] = ImageView.ScaleType.FIT_CENTER
        this[Constants.FIT_XY] = ImageView.ScaleType.FIT_XY
        this[Constants.FIT_END] = ImageView.ScaleType.FIT_END
        this[Constants.FIT_START] = ImageView.ScaleType.FIT_START
        this[Constants.MATRIX] = ImageView.ScaleType.MATRIX
    }

    private val sInputType = ConcurrentHashMap<String, Int>().apply {
        this["date"] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        this["datetime"] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
        this["none"] = InputType.TYPE_NULL
        this["number"] = InputType.TYPE_CLASS_NUMBER
        this["numberDecimal"] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        this["numberPassword"] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        this["numberSigned"] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        this["phone"] = InputType.TYPE_CLASS_PHONE
        this["text"] = InputType.TYPE_CLASS_TEXT
        this["textAutoComplete"] = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        this["textAutoCorrect"] = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        this["textCapCharacters"] = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        this["textCapSentences"] = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        this["textCapWords"] = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        this["textEmailAddress"] = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        this["textEmailSubject"] = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            this["textEnableTextConversionSuggestions"] = InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS
        }
        this["textFilter"] = InputType.TYPE_TEXT_VARIATION_FILTER
        this["textImeMultiLine"] = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
        this["textLongMessage"] = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
        this["textMultiLine"] = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        this["textNoSuggestions"] = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        this["textPassword"] = InputType.TYPE_TEXT_VARIATION_PASSWORD
        this["textPersonName"] = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        this["textPhonetic"] = InputType.TYPE_TEXT_VARIATION_PHONETIC
        this["textPostalAddress"] = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        this["textShortMessage"] = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        this["textUri"] = InputType.TYPE_TEXT_VARIATION_URI
        this["textVisiblePassword"] = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        this["textWebEditText"] = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        this["textWebEmailAddress"] = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        this["textWebPassword"] = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        this["time"] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
    }

    private val sPorterDuff = ConcurrentHashMap<String, PorterDuff.Mode>().apply {
        this[Constants.ADD] = PorterDuff.Mode.ADD
        this[Constants.MULTIPLY] = PorterDuff.Mode.MULTIPLY
        this[Constants.SCREEN] = PorterDuff.Mode.SCREEN
        this[Constants.SRC_ATOP] = PorterDuff.Mode.SRC_ATOP
        this[Constants.SRC_IN] = PorterDuff.Mode.SRC_IN
        this[Constants.SRC_OVER] = PorterDuff.Mode.SRC_OVER
    }

    @Suppress("DEPRECATION")
    private val sDrawingCacheQuality = ConcurrentHashMap<String, Int>().apply {
        this[Constants.AUTO] = View.DRAWING_CACHE_QUALITY_AUTO
        this[Constants.HIGH] = View.DRAWING_CACHE_QUALITY_HIGH
        this[Constants.LOW] = View.DRAWING_CACHE_QUALITY_LOW
    }

    private val sImeOptions = ConcurrentHashMap<String, Int>().apply {
        this["actionDone"] = EditorInfo.IME_ACTION_DONE
        this["actionGo"] = EditorInfo.IME_ACTION_GO
        this["actionNext"] = EditorInfo.IME_ACTION_NEXT
        this["actionNone"] = EditorInfo.IME_ACTION_NONE
        this["actionPrevious"] = EditorInfo.IME_ACTION_PREVIOUS
        this["actionSearch"] = EditorInfo.IME_ACTION_SEARCH
        this["actionSend"] = EditorInfo.IME_ACTION_SEND
        this["actionUnspecified"] = EditorInfo.IME_ACTION_UNSPECIFIED
        this["flagForceAscii"] = EditorInfo.IME_FLAG_FORCE_ASCII
        this["flagNavigateNext"] = EditorInfo.IME_FLAG_NAVIGATE_NEXT
        this["flagNavigatePrevious"] = EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
        this["flagNoExtractUi"] = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        this["flagNoAccessoryAction"] = EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION
        this["flagNoEnterAction"] = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        this["flagNoFullscreen"] = EditorInfo.IME_FLAG_NO_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this["flagNoPersonalizedLearning"] = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        this["normal"] = EditorInfo.IME_NULL
    }

    private val sOverScrollModes = ConcurrentHashMap<String, Int>().apply {
        this[Constants.ALWAYS] = View.OVER_SCROLL_ALWAYS
        this[Constants.IF_CONTENT_SCROLLS.lowercase()] = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        this[Constants.NEVER] = View.OVER_SCROLL_NEVER
    }

    private val sImportantAccessibility = ConcurrentHashMap<String, Int>().apply {
        this[Constants.AUTO] = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        this[Constants.YES] = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        this[Constants.NO] = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        this[Constants.NO_HIDE_DESCENDANTS] = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    /**
     * Parses a string to an integer with error handling.
     *
     * @param attributeValue The string value to parse
     * @return The parsed integer or 0 if parsing fails
     */
    fun parseInt(attributeValue: String?): Int =
        attributeValue?.toIntOrNull() ?: run { Log.e(TAG, "$attributeValue is NAN"); 0 }

    /**
     * Parses a string to an integer with detailed error reporting.
     *
     * @param s The string value to parse
     * @return An IntResult containing either the parsed value or an error message
     */
    fun parseIntUnsafe(s: String?): IntResult {
        s ?: return IntResult("null string")
        var num = 0
        for (char in s) {
            val digit = char - '0'
            if (digit !in 0..9) return IntResult("Malformed:  $s")
            num = num * 10 + digit
        }
        return IntResult(null, num)
    }

    /**
     * Parses a string to a float.
     *
     * @param value The string value to parse
     * @return The parsed float or 0f if parsing fails
     */
    fun parseFloat(value: String?): Float = value?.toFloatOrNull() ?: 0f

    /**
     * Parses a string to a double with error handling.
     *
     * @param attributeValue The string value to parse
     * @return The parsed double or 0.0 if parsing fails
     */
    fun parseDouble(attributeValue: String?): Double =
        attributeValue?.toDoubleOrNull() ?: run { Log.e(TAG, "$attributeValue is NAN"); 0.0 }

    /**
     * Parses a gravity string to an integer value.
     *
     * @param value The gravity string to parse
     * @return The parsed gravity value or NO_GRAVITY if parsing fails
     */
    fun parseGravity(value: String?): Int =
        value?.split("\\|")?.sumOf { sGravityMap[it] ?: 0 } ?: Gravity.NO_GRAVITY

    /**
     * Gets the gravity value for a string.
     *
     * @param value The gravity string
     * @return The corresponding gravity value
     */
    fun getGravity(value: String): Int = parseGravity(value)

    /**
     * Parses a divider mode string to an integer value.
     *
     * @param attributeValue The divider mode string
     * @return The parsed divider mode value
     */
    fun parseDividerMode(attributeValue: String?): Int =
        sDividerMode[attributeValue] ?: LinearLayout.SHOW_DIVIDER_NONE

    /**
     * Parses an ellipsize mode string to a TruncateAt value.
     *
     * @param attributeValue The ellipsize mode string
     * @return The parsed TruncateAt value
     */
    fun parseEllipsize(attributeValue: String?): TextUtils.TruncateAt =
        sEllipsizeMode[attributeValue] ?: TextUtils.TruncateAt.END

    /**
     * Parses a visibility string to an integer value.
     *
     * @param value The visibility string
     * @return The parsed visibility value
     */
    fun parseVisibility(value: String?): Int =
        sVisibilityMode[value] ?: (value?.toIntOrNull()?.let { sVisibilityMap[it] } ?: View.VISIBLE)

    /**
     * Gets the visibility value for an integer.
     *
     * @param visibility The visibility integer
     * @return The corresponding visibility value
     */
    fun getVisibility(visibility: Int): Int = sVisibilityMap[visibility] ?: View.GONE

    /**
     * Gets a color from a string or resource.
     *
     * @param color The color string or resource name
     * @param context The application context
     * @param defaultColor The default color if parsing fails
     * @return The parsed color value
     */
    fun getColor(color: String?, context: Context, defaultColor: Int = Color.BLACK): Int =
        color?.removePrefix("@color/")?.let { c ->
            if (c.startsWith("#")) {
                when (c.length) {
                    4 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}".toColorInt()
                    5 -> "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}${c[4]}${c[4]}".toColorInt()
                    else -> c.toColorInt()
                }
            } else {
                ConfigManager.config.provider.getResId("color", c).takeIf { it != 0 }
                    ?.let { ContextCompat.getColor(context, it) } ?: defaultColor
            }
        } ?: defaultColor

    /**
     * Parses a string to a boolean value.
     *
     * @param value The string value to parse
     * @return The parsed boolean value
     */
    fun parseBoolean(value: String?): Boolean =
        (value?.isBoolean()?.takeIf { it } ?: value?.toBooleanStrictOrNull()) == true

    /**
     * Gets a drawable from a resource name.
     *
     * @param view The view to get resources from
     * @param name The drawable resource name
     * @return The drawable or null if not found
     */
    fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, ConfigManager.config.provider.getResId("drawable", name), null
        )
    }

    /**
     * Gets a string from a resource name.
     *
     * @param context The application context
     * @param name The string resource name
     * @return The string value or the name if not found
     */
    fun getString(context: Context, name: String): String? = when {
        name.startsWith("@string/") -> ContextCompat.getString(
            context, ConfigManager.config.provider.getResId("string", name.removePrefix("@string/"))
        )
        else -> name
    }

    /**
     * Converts a boolean to a RelativeLayout rule value.
     *
     * @param value The boolean value
     * @return The corresponding RelativeLayout rule value
     */
    fun parseRelativeLayoutBoolean(value: Boolean): Int = if (value) RelativeLayout.TRUE else 0

    /**
     * Adds a rule to a RelativeLayout.
     *
     * @param view The view to add the rule to
     * @param verb The rule verb
     * @param anchor The anchor view ID
     */
    fun addRelativeLayoutRule(view: View, verb: Int, anchor: Int) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            addRule(verb, anchor)
            view.layoutParams = this
        } ?: Log.e(TAG, "cannot add relative layout rules when container is not relative")
    }

    /**
     * Parses a text style string to a Typeface style.
     *
     * @param attributeValue The text style string
     * @return The corresponding Typeface style
     */
    fun parseTextStyle(attributeValue: String?): Int = when (attributeValue?.lowercase()) {
        Constants.BOLD -> Typeface.BOLD
        Constants.ITALIC -> Typeface.ITALIC
        Constants.BOLD_ITALIC -> Typeface.BOLD_ITALIC
        else -> Typeface.NORMAL
    }

    /**
     * Checks if a string is a tween animation resource.
     *
     * @param attributeValue The string to check
     * @return true if it's a tween animation resource
     */
    fun isTweenAnimationResource(attributeValue: String?): Boolean =
        attributeValue?.startsWith(Constants.TWEEN_LOCAL_RESOURCE_STR) == true

    /**
     * Gets a resource ID from a variable name and class.
     *
     * @param variableName The variable name
     * @param klass The class to search in
     * @return The resource ID or 0 if not found
     */
    fun getResId(variableName: String, klass: KClass<*>): Int =
        klass.staticProperties.find { it.name == variableName }?.javaField?.getInt(null) ?: 0

    /**
     * Gets an Android XML resource ID from a full resource ID string.
     *
     * @param fullResIdString The full resource ID string
     * @return The resource ID or NO_ID if not found
     */
    fun getAndroidXmlResId(fullResIdString: String?): Int =
        fullResIdString?.substringAfter("/")?.let {
            getResId(it, android.R.id::class)
        } ?: View.NO_ID

    /**
     * Parses a scale type string to an ImageView.ScaleType.
     *
     * @param attributeValue The scale type string
     * @return The parsed ScaleType or null if not found
     */
    fun parseScaleType(attributeValue: String?): ImageView.ScaleType? =
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sImageScaleType[it] }

    /**
     * Parses a text alignment string to an integer value.
     *
     * @param attributeValue The text alignment string
     * @return The parsed text alignment value or null if not found
     */
    fun parseTextAlignment(attributeValue: String?): Int? =
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sTextAlignment[it] }

    /**
     * Parses an input type string to an integer value.
     *
     * @param attributeValue The input type string
     * @return The parsed input type value
     */
    fun parseInputType(attributeValue: String): Int =
        sInputType[attributeValue] ?: InputType.TYPE_CLASS_TEXT

    /**
     * Parses a PorterDuff mode string to a PorterDuff.Mode.
     *
     * @param porterDuff The PorterDuff mode string
     * @return The parsed PorterDuff.Mode
     */
    fun parsePorterDuff(porterDuff: String): PorterDuff.Mode =
        sPorterDuff[porterDuff] ?: PorterDuff.Mode.SRC_IN

    /**
     * Parses a drawing cache quality string to an integer value.
     *
     * @param attributeValue The drawing cache quality string
     * @return The parsed drawing cache quality value
     */
    @Suppress("DEPRECATION")
    fun parseDrawingCacheQuality(attributeValue: String?): Int =
        sDrawingCacheQuality[attributeValue?.lowercase()] ?: View.DRAWING_CACHE_QUALITY_AUTO

    /**
     * Parses an IME option string to an integer value.
     *
     * @param attributeValue The IME option string
     * @return The parsed IME option value
     */
    fun parseImeOption(attributeValue: String?): Int =
        sImeOptions[attributeValue] ?: EditorInfo.IME_NULL

    /**
     * Parses an over scroll mode string to an integer value.
     *
     * @param attributeValue The over scroll mode string
     * @return The parsed over scroll mode value
     */
    fun parseOverScrollMode(attributeValue: String): Int =
        sOverScrollModes[attributeValue.lowercase()] ?: View.OVER_SCROLL_ALWAYS

    /**
     * Parses an important for accessibility string to an integer value.
     *
     * @param attributeValue The important for accessibility string
     * @return The parsed important for accessibility value
     */
    fun parseImportantForAccessibility(attributeValue: String): Int =
        sImportantAccessibility[attributeValue] ?: View.IMPORTANT_FOR_ACCESSIBILITY_AUTO

    /**
     * Parses a scroll indicators string to an integer value.
     *
     * @param attributeValue The scroll indicators string
     * @return The parsed scroll indicators value
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun parseScrollIndicators(attributeValue: String): Int {
        var flags = 0
        attributeValue.split("|").forEach {
            flags = when (it.trim()) {
                Constants.START -> flags or View.SCROLL_INDICATOR_START
                Constants.END -> flags or View.SCROLL_INDICATOR_END
                Constants.TOP -> flags or View.SCROLL_INDICATOR_TOP
                Constants.BOTTOM -> flags or View.SCROLL_INDICATOR_BOTTOM
                Constants.LEFT -> flags or View.SCROLL_INDICATOR_LEFT
                Constants.RIGHT -> flags or View.SCROLL_INDICATOR_RIGHT
                "none" -> 0
                else -> flags
            }
        }
        return flags
    }

    /**
     * Data class for integer parsing results with error handling.
     *
     * @property error The error message if parsing failed
     * @property result The parsed integer value
     */
    data class IntResult(val error: String?, val result: Int = -1)
}