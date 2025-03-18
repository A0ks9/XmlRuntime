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
    private const val VISIBLE = "visible"
    private const val INVISIBLE = "invisible"
    private const val GONE = "gone"
    private const val CENTER = "center"
    private const val CENTER_HORIZONTAL = "center_horizontal"
    private const val CENTER_VERTICAL = "center_vertical"
    private const val LEFT = "left"
    private const val RIGHT = "right"
    private const val TOP = "top"
    private const val BOTTOM = "bottom"
    private const val START = "start"
    private const val END = "end"
    private const val FILL = "fill"
    private const val FILL_VERTICAL = "fill_vertical"
    private const val FILL_HORIZONTAL = "fill_horizontal"
    private const val CLIP_VERTICAL = "clip_vertical"
    private const val CLIP_HORIZONTAL = "clip_horizontal"
    private const val MIDDLE = "middle"
    private const val BEGINNING = "beginning"
    private const val MARQUEE = "marquee"
    private const val CENTER_CROP = "centerCrop"
    private const val CENTER_INSIDE = "centerInside"
    private const val FIT_CENTER = "fitCenter"
    private const val FIT_END = "fitEnd"
    private const val FIT_START = "fitStart"
    private const val FIT_XY = "fitXY"
    private const val MATRIX = "matrix"
    private const val BOLD = "bold"
    private const val ITALIC = "italic"
    private const val BOLD_ITALIC = "bold|italic"
    private const val TEXT_ALIGNMENT_INHERIT = "inherit"
    private const val TEXT_ALIGNMENT_GRAVITY = "gravity"
    private const val TEXT_ALIGNMENT_CENTER = "center"
    private const val TEXT_ALIGNMENT_TEXT_START = "textStart"
    private const val TEXT_ALIGNMENT_TEXT_END = "textEnd"
    private const val TEXT_ALIGNMENT_VIEW_START = "viewStart"
    private const val TEXT_ALIGNMENT_VIEW_END = "viewEnd"
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
    private const val TWEEN_LOCAL_RESOURCE_STR = "@anim/"
    private const val ADD = "add"
    private const val MULTIPLY = "multiply"
    private const val SCREEN = "screen"
    private const val SRC_ATOP = "src_atop"
    private const val SRC_IN = "src_in"
    private const val SRC_OVER = "src_over"
    private const val AUTO = "auto"
    private const val HIGH = "high"
    private const val LOW = "low"
    private const val ACTION_DONE = "actionDone"
    private const val ACTION_GO = "actionGo"
    private const val ACTION_NEXT = "actionNext"
    private const val ACTION_NONE = "actionNone"
    private const val ACTION_PREVIOUS = "actionPrevious"
    private const val ACTION_SEARCH = "actionSearch"
    private const val ACTION_SEND = "actionSend"
    private const val ACTION_UNSPECIFIED = "actionUnspecified"
    private const val FLAG_FORCE_ASCII = "flagForceAscii"
    private const val FLAG_NAVIGATE_NEXT = "flagNavigateNext"
    private const val FLAG_NAVIGATE_PREVIOUS = "flagNavigatePrevious"
    private const val FLAG_NO_ACCESSORY_ACTION = "flagNoAccessoryAction"
    private const val FLAG_NO_ENTER_ACTION = "flagNoEnterAction"
    private const val FLAG_NO_EXTRACT_UI = "flagNoExtractUi"
    private const val FLAG_NO_FULLSCREEN = "flagNoFullscreen"
    private const val FLAG_NO_PERSONALIZED_LEARNING = "flagNoPersonalizedLearning"
    private const val NORMAL = "normal"
    private const val ALWAYS = "always"
    private const val IF_CONTENT_SCROLLS = "ifContentScrolls"
    private const val NEVER = "never"
    private const val YES = "yes"
    private const val NO = "no"
    private const val NO_HIDE_DESCENDANTS = "noHideDescendants"


    private val sVisibilityMap = ConcurrentHashMap<Int, Int>().apply {
        this[View.VISIBLE] = View.VISIBLE
        this[View.INVISIBLE] = View.INVISIBLE
        this[View.GONE] = View.GONE
    }
    private val sGravityMap = ConcurrentHashMap<String, Int>().apply {
        this[CENTER] = Gravity.CENTER
        this[CENTER_HORIZONTAL] = Gravity.CENTER_HORIZONTAL
        this[CENTER_VERTICAL] = Gravity.CENTER_VERTICAL
        this[LEFT] = Gravity.LEFT
        this[RIGHT] = Gravity.RIGHT
        this[TOP] = Gravity.TOP
        this[BOTTOM] = Gravity.BOTTOM
        this[START] = Gravity.START
        this[END] = Gravity.END
        this[FILL] = Gravity.FILL
        this[FILL_VERTICAL] = Gravity.FILL_VERTICAL
        this[FILL_HORIZONTAL] = Gravity.FILL_HORIZONTAL
        this[CLIP_VERTICAL] = Gravity.CLIP_VERTICAL
        this[CLIP_HORIZONTAL] = Gravity.CLIP_HORIZONTAL
    }
    private val sDividerMode = ConcurrentHashMap<String, Int>().apply {
        this[END] = LinearLayout.SHOW_DIVIDER_END
        this[MIDDLE] = LinearLayout.SHOW_DIVIDER_MIDDLE
        this[BEGINNING] = LinearLayout.SHOW_DIVIDER_BEGINNING
    }
    private val sEllipsizeMode = ConcurrentHashMap<String, TextUtils.TruncateAt>().apply {
        this[END] = TextUtils.TruncateAt.END
        this[START] = TextUtils.TruncateAt.START
        this[MARQUEE] = TextUtils.TruncateAt.MARQUEE
        this[MIDDLE] = TextUtils.TruncateAt.MIDDLE
    }
    private val sVisibilityMode = ConcurrentHashMap<String, Int>().apply {
        this[VISIBLE] = View.VISIBLE
        this[INVISIBLE] = View.INVISIBLE
        this[GONE] = View.GONE
    }
    private val sTextAlignment = ConcurrentHashMap<String, Int>().apply {
        this[TEXT_ALIGNMENT_INHERIT] = View.TEXT_ALIGNMENT_INHERIT
        this[TEXT_ALIGNMENT_GRAVITY] = View.TEXT_ALIGNMENT_GRAVITY
        this[TEXT_ALIGNMENT_CENTER] = View.TEXT_ALIGNMENT_CENTER
        this[TEXT_ALIGNMENT_TEXT_START] = View.TEXT_ALIGNMENT_TEXT_START
        this[TEXT_ALIGNMENT_TEXT_END] = View.TEXT_ALIGNMENT_TEXT_END
        this[TEXT_ALIGNMENT_VIEW_START] = View.TEXT_ALIGNMENT_VIEW_START
        this[TEXT_ALIGNMENT_VIEW_END] = View.TEXT_ALIGNMENT_VIEW_END
    }
    private val sImageScaleType = ConcurrentHashMap<String, ImageView.ScaleType>().apply {
        this[CENTER] = ImageView.ScaleType.CENTER
        this[CENTER_CROP] = ImageView.ScaleType.CENTER_CROP
        this[CENTER_INSIDE] = ImageView.ScaleType.CENTER_INSIDE
        this[FIT_CENTER] = ImageView.ScaleType.FIT_CENTER
        this[FIT_XY] = ImageView.ScaleType.FIT_XY
        this[FIT_END] = ImageView.ScaleType.FIT_END
        this[FIT_START] = ImageView.ScaleType.FIT_START
        this[MATRIX] = ImageView.ScaleType.MATRIX
    }
    private val sInputType = ConcurrentHashMap<String, Int>().apply {
        this[DATE] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        this[DATE_TIME] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
        this[NONE] = InputType.TYPE_NULL
        this[NUMBER] = InputType.TYPE_CLASS_NUMBER
        this[NUMBER_DECIMAL] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        this[NUMBER_PASSWORD] =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        this[NUMBER_SIGNED] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        this[PHONE] = InputType.TYPE_CLASS_PHONE
        this[TEXT] = InputType.TYPE_CLASS_TEXT
        this[TEXT_AUTOCOMPLETE] = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        this[TEXT_AUTOCORRECT] = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        this[TEXT_CAP_CHARACTERS] = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        this[TEXT_CAP_SENTENCES] = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        this[TEXT_CAP_WORDS] = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        this[TEXT_EMAIL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        this[TEXT_EMAIL_SUBJECT] = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) this[TEXT_ENABLE_TEXT_CONVERSION_SUGGESTIONS] =
            InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS
        this[TEXT_FILTER] = InputType.TYPE_TEXT_VARIATION_FILTER
        this[TEXT_IME_MULTILINE] = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
        this[TEXT_LONG_MESSAGE] = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
        this[TEXT_MULTILINE] = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        this[TEXT_NO_SUGGESTIONS] = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        this[TEXT_PASSWORD] = InputType.TYPE_TEXT_VARIATION_PASSWORD
        this[TEXT_PERSON_NAME] = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        this[TEXT_PHONETIC] = InputType.TYPE_TEXT_VARIATION_PHONETIC
        this[TEXT_POSTAL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        this[TEXT_SHORT_MESSAGE] = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        this[TEXT_URI] = InputType.TYPE_TEXT_VARIATION_URI
        this[TEXT_VISIBLE_PASSWORD] = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        this[TEXT_WEB_EDITTEXT] = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        this[TEXT_WEB_EMAIL_ADDRESS] = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        this[TEXT_WEB_PASSWORD] = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        this[TIME] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
    }

    private val sPorterDuff = ConcurrentHashMap<String, PorterDuff.Mode>().apply {
        this[ADD] = PorterDuff.Mode.ADD
        this[MULTIPLY] = PorterDuff.Mode.MULTIPLY
        this[SCREEN] = PorterDuff.Mode.SCREEN
        this[SRC_ATOP] = PorterDuff.Mode.SRC_ATOP
        this[SRC_IN] = PorterDuff.Mode.SRC_IN
        this[SRC_OVER] = PorterDuff.Mode.SRC_OVER
    }

    @Suppress("DEPRECATION")
    private val sDrawingCacheQuality = ConcurrentHashMap<String, Int>().apply {
        this[AUTO] = View.DRAWING_CACHE_QUALITY_AUTO
        this[HIGH] = View.DRAWING_CACHE_QUALITY_HIGH
        this[LOW] = View.DRAWING_CACHE_QUALITY_LOW
    }

    private val sImeOptions = ConcurrentHashMap<String, Int>().apply {
        this[ACTION_DONE] = EditorInfo.IME_ACTION_DONE
        this[ACTION_GO] = EditorInfo.IME_ACTION_GO
        this[ACTION_NEXT] = EditorInfo.IME_ACTION_NEXT
        this[ACTION_NONE] = EditorInfo.IME_ACTION_NONE
        this[ACTION_PREVIOUS] = EditorInfo.IME_ACTION_PREVIOUS
        this[ACTION_SEARCH] = EditorInfo.IME_ACTION_SEARCH
        this[ACTION_SEND] = EditorInfo.IME_ACTION_SEND
        this[ACTION_UNSPECIFIED] = EditorInfo.IME_ACTION_UNSPECIFIED
        this[FLAG_FORCE_ASCII] = EditorInfo.IME_FLAG_FORCE_ASCII
        this[FLAG_NAVIGATE_NEXT] = EditorInfo.IME_FLAG_NAVIGATE_NEXT
        this[FLAG_NAVIGATE_PREVIOUS] = EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
        this[FLAG_NO_EXTRACT_UI] = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        this[FLAG_NO_ACCESSORY_ACTION] = EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION
        this[FLAG_NO_ENTER_ACTION] = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        this[FLAG_NO_FULLSCREEN] = EditorInfo.IME_FLAG_NO_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) this[FLAG_NO_PERSONALIZED_LEARNING] =
            EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        this[NORMAL] = EditorInfo.IME_NULL
    }

    private val sOverScrollModes = ConcurrentHashMap<String, Int>().apply {
        this[ALWAYS] = View.OVER_SCROLL_ALWAYS
        this[IF_CONTENT_SCROLLS.lowercase()] = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        this[NEVER] = View.OVER_SCROLL_NEVER
    }

    private val sImportantAccessibility = ConcurrentHashMap<String, Int>().apply {
        this[AUTO] = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        this[YES] = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        this[NO] = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        this[NO_HIDE_DESCENDANTS] = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    private val sScrollIndicators = ConcurrentHashMap<String, Int>().apply {
        this[START] = View.SCROLL_INDICATOR_START
        this[END] = View.SCROLL_INDICATOR_END
        this[TOP] = View.SCROLL_INDICATOR_TOP
        this[BOTTOM] = View.SCROLL_INDICATOR_BOTTOM
        this[NONE] = 0
        this[RIGHT] = View.SCROLL_INDICATOR_RIGHT
        this[LEFT] = View.SCROLLBAR_POSITION_LEFT
    }

    fun parseInt(attributeValue: String?): Int =
        attributeValue?.toIntOrNull() ?: run { Log.e(TAG, "$attributeValue is NAN"); 0 }

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

    fun parseFloat(value: String?): Float = value?.toFloatOrNull() ?: 0f
    fun parseDouble(attributeValue: String?): Double =
        attributeValue?.toDoubleOrNull() ?: run { Log.e(TAG, "$attributeValue is NAN"); 0.0 }

    fun parseGravity(value: String?): Int =
        value?.split("\\|")?.sumOf { sGravityMap[it] ?: 0 } ?: Gravity.NO_GRAVITY

    fun getGravity(value: String): Int = parseGravity(value)
    fun parseDividerMode(attributeValue: String?): Int =
        sDividerMode[attributeValue] ?: LinearLayout.SHOW_DIVIDER_NONE

    fun parseEllipsize(attributeValue: String?): TextUtils.TruncateAt =
        sEllipsizeMode[attributeValue] ?: TextUtils.TruncateAt.END

    fun parseVisibility(value: String?): Int =
        sVisibilityMode[value] ?: (value?.toIntOrNull()?.let { sVisibilityMap[it] } ?: View.VISIBLE)

    fun getVisibility(visibility: Int): Int = sVisibilityMap[visibility] ?: View.GONE

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

    fun parseBoolean(value: String?): Boolean =
        (value?.isBoolean()?.takeIf { it } ?: value?.toBooleanStrictOrNull()) == true

    fun getDrawable(view: View, name: String): Drawable? = view.resources.run {
        ResourcesCompat.getDrawable(
            this, ConfigManager.config.provider.getResId("drawable", name), null
        )
    }

    fun getString(context: Context, name: String): String? = when {
        name.startsWith("@string/") -> ContextCompat.getString(
            context, ConfigManager.config.provider.getResId("string", name.removePrefix("@string/"))
        )

        else -> name
    }

    fun parseRelativeLayoutBoolean(value: Boolean): Int = if (value) RelativeLayout.TRUE else 0
    fun addRelativeLayoutRule(view: View, verb: Int, anchor: Int) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            addRule(verb, anchor)
            view.layoutParams = this
        } ?: Log.e(TAG, "cannot add relative layout rules when container is not relative")
    }

    fun parseTextStyle(attributeValue: String?): Int = when (attributeValue?.lowercase()) {
        BOLD -> Typeface.BOLD
        ITALIC -> Typeface.ITALIC
        BOLD_ITALIC -> Typeface.BOLD_ITALIC
        else -> Typeface.NORMAL
    }

    fun isTweenAnimationResource(attributeValue: String?): Boolean =
        attributeValue?.startsWith(TWEEN_LOCAL_RESOURCE_STR) == true

    fun getResId(variableName: String, klass: KClass<*>): Int =
        klass.staticProperties.find { it.name == variableName }?.javaField?.getInt(null) ?: 0

    fun getAndroidXmlResId(fullResIdString: String?): Int =
        fullResIdString?.substringAfter("/")?.let {
            getResId(it, android.R.id::class)
        } ?: View.NO_ID


    fun parseScaleType(attributeValue: String?): ImageView.ScaleType? =
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sImageScaleType[it] }

    fun parseTextAlignment(attributeValue: String?): Int? =
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sTextAlignment[it] }

    fun parseInputType(attributeValue: String): Int =
        sInputType[attributeValue] ?: InputType.TYPE_CLASS_TEXT

    fun parsePorterDuff(porterDuff: String): PorterDuff.Mode =
        sPorterDuff[porterDuff] ?: PorterDuff.Mode.SRC_IN

    @Suppress("DEPRECATION")
    fun parseDrawingCacheQuality(attributeValue: String?): Int =
        sDrawingCacheQuality[attributeValue?.lowercase()] ?: View.DRAWING_CACHE_QUALITY_AUTO

    fun parseImeOption(attributeValue: String?): Int =
        sImeOptions[attributeValue] ?: EditorInfo.IME_NULL

    fun parseOverScrollMode(attributeValue: String): Int =
        sOverScrollModes[attributeValue.lowercase()] ?: View.OVER_SCROLL_ALWAYS

    fun parseImportantForAccessibility(attributeValue: String): Int =
        sImportantAccessibility[attributeValue] ?: View.IMPORTANT_FOR_ACCESSIBILITY_AUTO

    fun parseScrollIndicators(attributeValue: String): Int {
        var flags = 0
        attributeValue.split("|").forEach {
            flags = when (it.trim()) {
                START -> flags or View.SCROLL_INDICATOR_START
                END -> flags or View.SCROLL_INDICATOR_END
                TOP -> flags or View.SCROLL_INDICATOR_TOP
                BOTTOM -> flags or View.SCROLL_INDICATOR_BOTTOM
                LEFT -> flags or View.SCROLL_INDICATOR_LEFT
                RIGHT -> flags or View.SCROLL_INDICATOR_RIGHT
                NONE -> 0
                else -> flags
            }
        }
        return flags
    }

    data class IntResult(val error: String?, val result: Int = -1)
}