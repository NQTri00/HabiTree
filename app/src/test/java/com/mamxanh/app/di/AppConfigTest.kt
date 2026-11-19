package com.mamxanh.app.di

import com.mamxanh.app.core.config.HabiTreeConfigProvider
import com.mamxanh.shared.network.NetworkConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.dsl.koinApplication

class AppConfigTest {
    @Test
    fun blankEndpointKeepsTransportUnconfigured() {
        val application = koinApplication { modules(appModule(HabiTreeConfigProvider(" \t"))) }
        try {
            assertNull(application.koin.get<NetworkConfig>().baseUrl)
        } finally {
            application.close()
        }
    }

    @Test
    fun compositionRootPassesTheConfiguredEndpointToTransport() {
        val provider = HabiTreeConfigProvider("https://example.invalid/")
        val application = koinApplication { modules(appModule(provider)) }
        try {
            assertSame(provider, application.koin.get<HabiTreeConfigProvider>())
            assertEquals(provider.config.apiBaseUrl, application.koin.get<NetworkConfig>().baseUrl)
        } finally {
            application.close()
        }
    }
}
