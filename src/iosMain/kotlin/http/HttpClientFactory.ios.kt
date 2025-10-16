package app.cardium.tcgdex.sdk.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.utils.io.errors.IOException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createPlatformHttpClient(): HttpClient =
    HttpClient(Darwin) {
        engine {
            // Prefer allowing constrained/expensive networks; Darwin handles connectivity internally
            configureRequest {
                setAllowsExpensiveNetworkAccess(true)
                setAllowsConstrainedNetworkAccess(true)
            }
        }

        install(HttpTimeout) {
            // Conservative timeouts for mobile networks
            requestTimeoutMillis = 12_000
            connectTimeoutMillis = 8_000
            socketTimeoutMillis = 12_000
        }

        install(HttpRequestRetry) {
            maxRetries = 2
            retryIf { _, response ->
                val code = response?.status?.value ?: 0
                code in 500..599
            }
            retryOnExceptionIf { _, cause ->
                cause is IOException
            }
            exponentialDelay()
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                },
            )
        }
    }
