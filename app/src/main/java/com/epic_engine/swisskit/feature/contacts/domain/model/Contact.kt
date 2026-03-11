package com.epic_engine.swisskit.feature.contacts.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val categoryId: String
)
