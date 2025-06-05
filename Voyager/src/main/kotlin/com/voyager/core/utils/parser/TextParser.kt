package com.voyager.core.utils.parser

import android.graphics.Typeface
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing text-related Android View properties from strings.
 * Provides efficient and thread-safe text property parsing operations.
 *
 * Key Features:
 * - Text style parsing (bold, italic, etc.)
 * - Input type parsing
 * - IME options parsing
 * - Text alignment parsing
 * - Ellipsize mode parsing
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe text handling
 *
 * Best Practices:
 * 1. Use appropriate text styles
 * 2. Handle null values appropriately
 * 3. Consider text property availability
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse text style
 * val style = TextParser.parseTextStyle("bold|italic")
 *
 * // Parse input type
 * val inputType = TextParser.parseInputType("textEmailAddress")
 *
 * // Parse IME options
 * val imeOptions = TextParser.parseImeOption("actionDone")
 * ```
 */
object TextParser {
    private val logger = LoggerFactory.getLogger(TextParser::class.java.simpleName)

    // Thread-safe maps for text properties
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
        this["normal"] = EditorInfo.IME_NULL
    }

    /**
     * Parses an ellipsize mode string to a TextUtils.TruncateAt value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe text handling
     *
     * @param attributeValue The ellipsize mode string (e.g., "end", "start", "marquee", "middle")
     * @return The parsed TextUtils.TruncateAt value, or [TextUtils.TruncateAt.END] if the input is null or doesn't match
     */
    fun parseEllipsize(attributeValue: String?): TextUtils.TruncateAt = try {
        sEllipsizeMode[attributeValue] ?: run {
            logger.warn("parseEllipsize", "Invalid ellipsize mode: '$attributeValue', using END")
            TextUtils.TruncateAt.END
        }
    } catch (e: Exception) {
        logger.error("parseEllipsize", "Failed to parse ellipsize mode: ${e.message}")
        TextUtils.TruncateAt.END
    }

    /**
     * Parses a text style string to a Typeface style integer.
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Efficient string operations
     * - Minimal object creation
     * - Safe text handling
     *
     * @param attributeValue The text style string (e.g., "bold", "italic", "bold|italic")
     * @return The corresponding [Typeface] style integer
     */
    fun parseTextStyle(attributeValue: String?): Int = try {
        when (attributeValue?.lowercase()) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            "bold|italic", "italic|bold" -> Typeface.BOLD_ITALIC
            else -> {
                logger.warn("parseTextStyle", "Invalid text style: '$attributeValue', using NORMAL")
                Typeface.NORMAL
            }
        }
    } catch (e: Exception) {
        logger.error("parseTextStyle", "Failed to parse text style: ${e.message}")
        Typeface.NORMAL
    }

    /**
     * Parses an input type string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe text handling
     *
     * @param attributeValue The input type string
     * @return The parsed input type integer value
     */
    fun parseInputType(attributeValue: String): Int = try {
        sInputType[attributeValue] ?: run {
            logger.warn("parseInputType", "Invalid input type: '$attributeValue', using TYPE_CLASS_TEXT")
            InputType.TYPE_CLASS_TEXT
        }
    } catch (e: Exception) {
        logger.error("parseInputType", "Failed to parse input type: ${e.message}")
        InputType.TYPE_CLASS_TEXT
    }

    /**
     * Parses an IME option string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe text handling
     *
     * @param attributeValue The IME option string
     * @return The parsed IME option integer value
     */
    fun parseImeOption(attributeValue: String?): Int = try {
        sImeOptions[attributeValue] ?: run {
            logger.warn("parseImeOption", "Invalid IME option: '$attributeValue', using IME_NULL")
            EditorInfo.IME_NULL
        }
    } catch (e: Exception) {
        logger.error("parseImeOption", "Failed to parse IME option: ${e.message}")
        EditorInfo.IME_NULL
    }

    /**
     * Parses a text alignment string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe text handling
     *
     * @param attributeValue The text alignment string
     * @return The parsed text alignment integer value, or null if invalid
     */
    fun parseTextAlignment(attributeValue: String?): Int? = try {
        attributeValue?.takeUnless(TextUtils::isEmpty)?.let { sTextAlignment[it] } ?: run {
            logger.warn("parseTextAlignment", "Invalid text alignment: '$attributeValue'")
            null
        }
    } catch (e: Exception) {
        logger.error("parseTextAlignment", "Failed to parse text alignment: ${e.message}")
        null
    }

    /**
     * Clears all text property caches.
     * Useful for testing or when text properties change.
     * Thread-safe operation.
     */
    fun clearCache() {
        sEllipsizeMode.clear()
        sTextAlignment.clear()
        sInputType.clear()
        sImeOptions.clear()
        logger.debug("clearCache", "Text property caches cleared")
    }
}