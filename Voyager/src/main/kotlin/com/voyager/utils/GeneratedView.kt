/**
 * Data class representing a generated view with its associated properties.
 *
 * This class holds essential information about a dynamically generated view,
 * including its ID mapping, delegate object, and background drawable.
 *
 * Key features:
 * - Thread-safe view ID mapping
 * - Flexible delegate support
 * - Gradient background support
 *
 * Performance considerations:
 * - Uses HashMap for O(1) ID lookups
 * - Lazy initialization of properties
 * - Efficient memory usage
 *
 * Usage example:
 * ```kotlin
 * val generatedView = GeneratedView(
 *     viewID = hashMapOf("button1" to R.id.button1),
 *     delegate = viewDelegate,
 *     bgDrawable = gradientDrawable
 * )
 * ```
 *
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.graphics.drawable.GradientDrawable

internal data class GeneratedView(
    /**
     * Thread-safe mapping of view identifiers to their resource IDs.
     * Provides O(1) lookup time for view identification.
     */
    var viewID: HashMap<String, Int> = HashMap(),

    /**
     * Optional delegate object for handling view-specific logic.
     * Can be used for callbacks, event handling, or custom behavior.
     */
    var delegate: Any? = null,

    /**
     * Optional gradient drawable for the view's background.
     * Supports dynamic background styling and effects.
     */
    var bgDrawable: GradientDrawable? = null
)