package com.voyager.utils.view.transformation

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
    /**
     * Returns the original characters to be transformed.
     * 
     * This method provides the source alphabet in lowercase.
     * The characters will be replaced with their corresponding
     * characters from getReplacement().
     *
     * @return CharArray containing the original characters
     */
    override fun getOriginal(): CharArray = "abcdefghijklmnopqrstuvwxyz".toCharArray()

    /**
     * Returns the replacement characters for the transformation.
     * 
     * This method provides the target alphabet in reverse order.
     * Each character in getOriginal() will be replaced with its
     * corresponding character from this array.
     *
     * @return CharArray containing the replacement characters
     */
    override fun getReplacement(): CharArray = "zyxwvutsrqponmlkjihgfedcba".toCharArray()
}