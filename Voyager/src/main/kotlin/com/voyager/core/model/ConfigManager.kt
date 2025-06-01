package com.voyager.core.model

object ConfigManager {
    private var _config: VoyagerConfig? = null
    val config: VoyagerConfig
        get() = _config ?: error("VoyagerConfig not initialized")
    fun initialize(config: VoyagerConfig) {
        check(_config == null) { "VoyagerConfig already initialized" }
        _config = config
    }
} 