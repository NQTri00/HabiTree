package com.mamxanh.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

/** Configurable transport boundary until a real backend contract is supplied. */
class RemoteService(private val client: HttpClient, private val config: NetworkConfig) {
    val isConfigured: Boolean get() = config.baseUrl != null

    suspend fun get(pathSegments: List<String>, query: Map<String, String> = emptyMap()): String {
        val base = checkNotNull(config.baseUrl) { "No backend URL is configured." }
        require(pathSegments.all { it.isNotBlank() && it != "." && it != ".." && '/' !in it }) {
            "Supply individual non-empty path segments."
        }
        val url = URLBuilder(base).apply {
            appendPathSegments(pathSegments)
            query.forEach { (key, value) -> parameters.append(key, value) }
        }.build()
        // Ktor suspends; cancellation and transport failures propagate to the caller.
        return client.get(url).bodyAsText()
    }
}
