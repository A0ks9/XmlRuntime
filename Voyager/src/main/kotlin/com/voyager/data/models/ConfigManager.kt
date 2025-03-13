package com.voyager.data.models

object ConfigManager {
    private var _config: VoyagerConfig? = null

    val config: VoyagerConfig
        get() = _config ?: throw IllegalStateException("VoyagerConfig not initialized")

    fun initialize(config: VoyagerConfig) {
        _config = config
    }
}