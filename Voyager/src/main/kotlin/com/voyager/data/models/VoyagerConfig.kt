package com.voyager.data.models

import com.voyager.utils.interfaces.ResourcesProvider

/**
 * Holds global configuration settings for the Voyager library.
 *
 * This data class is used to initialize and customize the behavior of various
 * Voyager components. An instance of `VoyagerConfig` is typically created and
 * passed during the library's setup (e.g., via a Koin module) and then made
 * accessible through [ConfigManager].
 *
 * @property version The current version of the Voyager library. This can be useful for debugging
 *                   or conditional logic based on library version. Defaults to "1.0.0-Beta01".
 * @property isLoggingEnabled A boolean flag that controls whether internal logging within the
 *                            Voyager library is enabled. Setting this to `true` can help in
 *                            diagnosing issues during development. Defaults to `false`.
 * @property provider An implementation of the [ResourcesProvider] interface. This is a crucial
 *                    component that allows Voyager to dynamically access application resources
 *                    (such as strings, drawables, dimensions, styles, etc.) by their string names.
 *                    The consuming application must provide a concrete implementation of this interface.
 */
data class VoyagerConfig(
    val version: String = "1.0.0-Beta01",
    val isLoggingEnabled: Boolean = false,
    val provider: ResourcesProvider,
)