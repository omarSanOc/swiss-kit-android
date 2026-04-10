package com.epic_engine.swisskit.feature.converter.data.remote

import com.epic_engine.swisskit.core.common.SwissKitLogger
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Retry interceptor with exponential backoff. Maximum 2 retries with 1s and 2s delays.
 */
class RetryInterceptor(
    private val maxRetries: Int = 2
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var lastException: Exception? = null

        while (attempt <= maxRetries) {
            try {
                val response = chain.proceed(chain.request())
                if (response.isSuccessful) return response
                response.close()
            } catch (e: Exception) {
                lastException = e
                SwissKitLogger.w("Converter", "Retry $attempt/${maxRetries}: ${e.message}")
            }

            if (attempt < maxRetries) {
                val delayMs = when (attempt) {
                    0 -> 1_000L
                    else -> 2_000L
                }
                Thread.sleep(delayMs)
            }
            attempt++
        }

        throw lastException ?: Exception("Request failed after $maxRetries retries")
    }
}
