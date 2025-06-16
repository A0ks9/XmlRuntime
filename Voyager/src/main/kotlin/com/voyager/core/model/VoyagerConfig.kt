package com.voyager.core.model

import com.voyager.core.data.ResourcesProvider

/**
 * Voyager framework configuration settings.
 *
 * @property caching Enable/disable caching.
 * @property isLoggingEnabled Enable/disable logging.
 * @property provider Resource provider implementation.
 */
data class VoyagerConfig(
    val caching: Boolean = true,
    val isLoggingEnabled: Boolean = false,
    val provider: ResourcesProvider,
) 