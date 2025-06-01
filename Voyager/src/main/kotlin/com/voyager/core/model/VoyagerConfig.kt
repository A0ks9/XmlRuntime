package com.voyager.core.model

import com.voyager.core.data.ResourcesProvider

/**
 * Voyager framework configuration settings.
 *
 * @property version Framework version string.
 * @property isLoggingEnabled Enable/disable logging.
 * @property provider Resource provider implementation.
 */
data class VoyagerConfig(
    val version: String = "1.0.0-Beta01",
    val isLoggingEnabled: Boolean = false,
    val provider: ResourcesProvider,
) 