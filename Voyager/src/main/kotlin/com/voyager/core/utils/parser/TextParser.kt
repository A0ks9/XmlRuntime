package com.voyager.core.utils.parser

import android.graphics.Typeface
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing text-related Android View properties from strings.
 */
object TextParser {

    private val sEllipsizeMode = ConcurrentHashMap<String, TextUtils.TruncateAt>().apply {
        this["end"] = TextUtils.TruncateAt.END
        this["start"] = TextUtils.TruncateAt.START
        this["marquee"] = TextUtils.TruncateAt.MARQUEE
        this["middle"] = TextUtils.TruncateAt.MIDDLE
    }

    private val sTextAlignment = ConcurrentHashMap<String, Int>().apply {
        this["inherit"] = View.TEXT_ALIGNMENT_INHERIT
        this["gravity"] = View.TEXT_ALIGNMENT_GRAVITY
        this["center"] = View.TEXT_ALIGNMENT_CENTER
        this["textStart"] = View.TEXT_ALIGNMENT_TEXT_START
        this["textEnd"] = View.TEXT_ALIGNMENT_TEXT_END
        this["viewStart"] = View.TEXT_ALIGNMENT_VIEW_START
        this["viewEnd"] = View.TEXT_ALIGNMENT_VIEW_END
    }

    private val sInputType = ConcurrentHashMap<String, Int>().apply {
        this["date"] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        this["datetime"] = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
        this["none"] = InputType.TYPE_NULL
        this["number"] = InputType.TYPE_CLASS_NUMBER
        this["numberDecimal"] = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        this["numberPassword"] =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
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
        // Add API level check for this specific constant if targeting lower APIs
        // if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
        //     this["textEnableTextConversionSuggestions"] = InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS
        // }
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
        // Add API level check for this specific constant if targeting lower APIs
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //     this["flagNoPersonalizedLearning"] = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        // }
        this["normal"] = EditorInfo.IME_NULL
    }

    /**
     * Parses an ellipsize mode string to a TextUtils.TruncateAt value.
     *
     * @param attributeValue The ellipsize mode string (e.g., "end", "start", "marquee", "middle").
     * @return The parsed TextUtils.TruncateAt value, or [TextUtils.TruncateAt.END] if the input is null or doesn't match.
     */
    fun parseEllipsize(attributeValue: String?): TextUtils.TruncateAt =
        sEllipsizeMode[attributeValue] ?: TextUtils.TruncateAt.END

    /**
     * Parses a text style string (e.g., "bold", "italic", "bold|italic") to a Typeface style integer.
     * Handles combinations like "bold|italic" and "italic|bold".
     *
     * @param attributeValue The text style string.
     * @return The corresponding [Typeface] style integer ([Typeface.NORMAL], [Typeface.BOLD], [Typeface.ITALIC], or [Typeface.BOLD_ITALIC]). Defaults to [Typeface.NORMAL].
     */
    fun parseTextStyle(attributeValue: String?): Int = when (attributeValue?.lowercase()) {
        "bold" -> Typeface.BOLD
        "italic" -> Typeface.ITALIC
        "bold|italic", "italic|bold" -> Typeface.BOLD_ITALIC // Handle both orders
        else -> Typeface.NORMAL
    }

    /**
     * Parses an input type string (e.g., "text", "numberDecimal", "textPassword") to an integer value used for EditText.
     *
     * @param attributeValue The input type string.
     * @return The parsed input type integer value. Defaults to [InputType.TYPE_CLASS_TEXT].
     */
    fun parseInputType(attributeValue: String): Int =
        sInputType[attributeValue] ?: InputType.TYPE_CLASS_TEXT

    /**
     * Parses an IME option string (e.g., "actionDone", "flagNoFullscreen") to an integer value used for EditText's IME options.
     *
     * @param attributeValue The IME option string.
     * @return The parsed IME option integer value. Defaults to [EditorInfo.IME_NULL].
     */
    fun parseImeOption(attributeValue: String?): Int =
        sImeOptions[attributeValue] ?: EditorInfo.IME_NULL

    /**
     * Parses a text alignment string (e.g., "center", "viewStart", "textEnd") to an integer value used for View text alignment.
     *
     * @param attributeValue The text alignment string.
     * @return The parsed text alignment integer value, or `null` if the input is null, empty, or doesn't match a known alignment string.
     */
    fun parseTextAlignment(attributeValue: String?): Int? =
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sTextAlignment[it] }
}