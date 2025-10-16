package app.cardium.tcgdex.sdk.http

import io.ktor.client.HttpClient

/**
 * Provides a platform-specific Ktor HttpClient with JSON configured.
 * Actual implementations live in androidMain and iosMain.
 */
expect fun createPlatformHttpClient(): HttpClient
