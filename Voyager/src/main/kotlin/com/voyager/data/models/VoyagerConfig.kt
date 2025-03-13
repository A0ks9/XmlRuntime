package com.voyager.data.models

import com.voyager.utils.interfaces.ResourcesProvider

data class VoyagerConfig(
    val version: String = "1.0.0-Beta01",
    val isLoggingEnabled: Boolean = false,
    val provider: ResourcesProvider,
)