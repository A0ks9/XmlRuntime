package com.flipkart.android.proteus.exceptions

import android.view.InflateException

/**
 * Custom exception class specifically for errors during Proteus layout inflation.
 *
 * `ProteusInflateException` is a subclass of Android's `InflateException`. It's designed to be thrown
 * when there are issues or errors encountered while the Proteus layout inflater is processing and
 * constructing views from layout definitions (like XML or JSON layouts used by Proteus).
 *
 * By using a custom exception type, it allows for more specific error handling and differentiation
 * between general layout inflation errors and those specifically occurring within the Proteus framework.
 *
 * **Purpose:**
 *
 * *   **Specific Error Reporting:** Indicates that the inflate error originated from within the Proteus inflation process, helping in debugging.
 * *   **Differentiated Exception Handling:** Allows catch blocks to specifically handle `ProteusInflateException` differently from standard `InflateException` if needed.
 * *   **Improved Error Messages:** Can be used to provide more context-rich error messages relevant to Proteus layouts.
 *
 * **Usage Scenario:**
 *
 * Proteus framework internally throws `ProteusInflateException` when it detects problems during the
 * layout inflation process, such as:
 *
 * *   Invalid layout XML/JSON structure.
 * *   Missing or incorrect view type definitions.
 * *   Problems during attribute processing or data binding setup that prevent view creation.
 * *   Resource loading failures during inflation.
 *
 * When you catch exceptions during Proteus layout inflation, you can check if it's a `ProteusInflateException`
 * to understand that the issue is specifically related to your Proteus layouts.
 *
 * **Example (Conceptual - How Proteus might use it internally):**
 *
 * ```kotlin
 * try {
 *     val proteusView = proteusLayoutInflater.inflate(layout, dataContext)
 *     // ... use proteusView ...
 * } catch (e: ProteusInflateException) {
 *     Log.e("Proteus Inflation Error", "Error inflating layout: ${e.message}", e)
 *     // Handle Proteus specific inflation error, e.g., show error UI, retry, etc.
 * } catch (e: InflateException) {
 *     Log.e("General Inflation Error", "General layout inflation error: ${e.message}", e)
 *     // Handle general inflation errors if needed
 * }
 * ```
 *
 * **Key improvements and explanations in Kotlin class documentation:**
 *
 * *   Clarified purpose as a *custom exception* for Proteus inflation errors.
 * *   Explained why a custom exception is useful (specific error reporting, differentiated handling).
 * *   Provided concrete usage scenarios (what kind of errors would cause this, and how to catch it).
 * *   Example code snippet illustrating catching `ProteusInflateException`.
 * *   Used KDoc comments for clear documentation in Kotlin style.
 */
class ProteusInflateException(message: String) : InflateException(message)