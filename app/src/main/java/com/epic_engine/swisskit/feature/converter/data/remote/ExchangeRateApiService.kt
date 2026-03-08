package com.epic_engine.swisskit.feature.converter.data.remote

import com.epic_engine.swisskit.feature.converter.data.remote.dto.CurrencyResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {
    @GET("v4/latest/{base}")
    suspend fun getLatestRates(
        @Path("base") base: String = "USD"
    ): CurrencyResponseDto
}
