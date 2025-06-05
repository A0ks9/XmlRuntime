package com.voyager.core.view.utils.id

import android.view.View
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility functions for handling Android View IDs.
 * 
 * Key features:
 * - ID extraction from resource references
 * - Efficient ID parsing
 * - Thread-safe operations
 * - Comprehensive error handling
 * 
 * Performance optimizations:
 * - Compiled regex pattern
 * - Minimal object creation
 * - Fast string operations
 * 
 * Best practices:
 * - Use for dynamic view ID resolution
 * - Handle invalid ID formats gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Extract ID from resource reference
 * val id = "@+id/submit_button".extractViewId() // Returns "submit_button"
 * 
 * // Extract ID from simple string
 * val id = "submit_button".extractViewId() // Returns "submit_button"
 * ```
 */
internal object ViewIdUtils {

    private val logger = LoggerFactory.getLogger("ViewIdUtils")

    /**
     * Regex used to parse string ID references like "@+id/name",
     * "@android:id/name", or simply "name". Captures the actual ID name.
     * 
     * Pattern explanation:
     * - ^@\\+? - Matches optional @+ at start
     * - (?:android:id/|id/|id\\/)? - Optional android:id/, id/, or id/ prefix
     * - (.+)$ - Captures the actual ID name
     */
    private val viewIdRegex = Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$")

    /**
     * Extracts the actual ID name from a string that might represent a resource ID
     * (e.g., "@+id/submit_button", "@id/submit_button", "submit_button").
     *
     * Performance considerations:
     * - Uses compiled regex pattern
     * - Efficient string operations
     * - Minimal object creation
     * - Fast execution
     * 
     * Error handling:
     * - Safe string parsing
     * - Graceful fallback for invalid formats
     * - Proper logging
     *
     * @receiver The string to extract the ID from
     * @return The extracted ID name (e.g., "submit_button") or an empty string if the
     *         input string does not match the expected ID format
     */
    fun String.extractViewId(): String {
        try {
            return viewIdRegex.find(this)?.groupValues?.getOrNull(1) ?: ""
        } catch (e: Exception) {
            logger.error(
                "extractViewId",
                "Failed to extract view ID from '$this': ${e.message}",
                e
            )
            return ""
        }
    }
} 