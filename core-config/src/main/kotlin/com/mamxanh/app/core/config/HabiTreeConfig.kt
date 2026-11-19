package com.mamxanh.app.core.config

import kotlinx.serialization.Serializable

@Serializable
data class HabiTreeConfig(val apiBaseUrl: String? = null)

class HabiTreeConfigProvider(apiBaseUrl: String? = null) {
    val config = HabiTreeConfig(apiBaseUrl?.takeIf { it.isNotBlank() })
}
