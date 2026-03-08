package com.epic_engine.swisskit.feature.converter.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponseDto(
    @SerialName("base") val base: String,
    @SerialName("rates") val rates: Map<String, Double>
)
