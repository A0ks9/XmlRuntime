package com.voyager.data.models

import com.voyager.utils.interfaces.ResourcesProvider

/**
 * Holds configuration settings for the Voyager library.
 *
 * This data class is used to initialize and configure the behavior of the Voyager library,
 * such as enabling logging and providing access to application resources.
 *
 * @property version The version of the Voyager library. Defaults to "1.0.0-Beta01".
 * @property isLoggingEnabled Flag to enable or disable internal logging within the library. Defaults to false.
 * @property provider An instance of [ResourcesProvider] that allows Voyager to access application
 *                    resources, such as strings and drawables, by their names. This is crucial for
 *                    resolving resource references found in view attributes.
 */
data class VoyagerConfig(
    val version: String = "1.0.0-Beta01",
    val isLoggingEnabled: Boolean = false,
    val provider: ResourcesProvider,
)