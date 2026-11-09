package com.mamxanh.shared.network

import io.ktor.http.Url
import io.ktor.http.URLProtocol

/** No backend is contacted unless a caller explicitly performs a request. */
data class NetworkConfig(val baseUrl: String? = null) {
    init {
        baseUrl?.let {
            val url = Url(it)
            require(url.protocol == URLProtocol.HTTPS && url.host.isNotBlank()) {
                "Backend URL must use HTTPS."
            }
            require(url.user.isNullOrEmpty() && url.password.isNullOrEmpty() && url.fragment.isEmpty()) {
                "Backend URL must not contain credentials or a fragment."
            }
        }
    }
}
