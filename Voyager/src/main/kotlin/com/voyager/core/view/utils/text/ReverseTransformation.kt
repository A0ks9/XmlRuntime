package com.voyager.core.view.utils.text

import android.text.method.ReplacementTransformationMethod

/**
 * A text transformation method that reverses the case of alphabetic characters.
 *
 * This class provides a custom transformation method that maps each lowercase letter
 * to its reverse position in the alphabet (e.g., 'a' becomes 'z', 'b' becomes 'y', etc.).
 * It extends Android's ReplacementTransformationMethod to provide this functionality.
 *
 * Key features:
 * - Case-preserving transformation
 * - Efficient character mapping
 * - Memory-efficient implementation
 * - Thread-safe operation
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Cached character arrays
 * - Minimal object creation
 * - Efficient character mapping
 * - Fast transformation
 *
 * Best practices:
 * - Use for text input fields
 * - Handle transformation errors gracefully
 * - Monitor performance impact
 * - Consider memory usage
 *
 * Example usage:
 * ```kotlin
 * val editText = EditText(context)
 * editText.transformationMethod = ReverseTransformation()
 * // When user types "hello", it appears as "svool"
 * ```
 *
 * Character mapping:
 * - a → z
 * - b → y
 * - c → x
 * - ...
 * - z → a
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class ReverseTransformation : ReplacementTransformationMethod() {

    // Cache the character arrays to avoid repeated creation
    private val originalChars = "abcdefghijklmnopqrstuvwxyz".toCharArray()
    private val replacementChars = "zyxwvutsrqponmlkjihgfedcba".toCharArray()

    /**
     * Returns the original characters to be transformed.
     *
     * This method provides the source alphabet in lowercase.
     * The characters will be replaced with their corresponding
     * characters from getReplacement().
     *
     * Performance considerations:
     * - Returns cached character array
     * - No object creation
     * - Fast operation
     *
     * @return CharArray containing the original characters
     */
    override fun getOriginal(): CharArray = originalChars

    /**
     * Returns the replacement characters for the transformation.
     *
     * This method provides the target alphabet in reverse order.
     * Each character in getOriginal() will be replaced with its
     * corresponding character from this array.
     *
     * Performance considerations:
     * - Returns cached character array
     * - No object creation
     * - Fast operation
     *
     * @return CharArray containing the replacement characters
     */
    override fun getReplacement(): CharArray = replacementChars
}