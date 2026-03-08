package com.epic_engine.swisskit.feature.converter.domain.model

data class Rates(
    val base: String,
    val values: Map<String, Double>,
    val isFromCache: Boolean = false
)
