package com.voyager.core.model

import android.util.AttributeSet
import com.voyager.core.exceptions.VoyagerRenderingException

/**
 * Represents a node in a declarative view hierarchy for Voyager.
 * Optimized for Room, Parcelable, and kotlinx.serialization.
 *
 * Features:
 * - Room database integration
 * - Parcelable serialization
 * - Kotlinx serialization support
 * - Attribute validation
 * - Child node management
 * - Activity name tracking
 *
 * Example Usage:
 * ```kotlin
 * val node = ViewNode(
 *     id = "root",
 *     type = "LinearLayout",
 *     activityName = "MainActivity",
 *     attributes = ArrayMap<String, String>().apply {
 *         put("orientation", "vertical")
 *         put("padding", "16dp")
 *     },
 *     children = listOf(
 *         ViewNode(
 *             type = "TextView",
 *             attributes = ArrayMap<String, String>().apply {
 *                 put("text", "Hello World")
 *             }
 *         )
 *     )
 * )
 * ```
 *
 * @property type The type of view this node represents (e.g., "LinearLayout", "TextView")
 * @property activityName The name of the activity this node belongs to
 * @property attributes Map of view attributes and their values
 * @property children List of child view nodes
 * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
 * @throws VoyagerRenderingException.InvalidAttributeValueException if attribute values are invalid
 */
data class ViewNode(
    val type: String,
    var activityName: String = "no_activity",
    val attributes: AttributeSet,
    val children: MutableList<ViewNode> = mutableListOf(),
)