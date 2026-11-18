package com.mamxanh.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout

/** Factory owns the platform engine, which is closed with the client. */
expect fun createHttpClient(): HttpClient

internal fun HttpClientConfig<*>.configureNetworking() {
    expectSuccess = true
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 15_000
    }
}
