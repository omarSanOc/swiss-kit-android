package com.epic_engine.swisskit.feature.contacts.domain.model

data class Category(
    val id: String,
    val title: String,
    val contacts: List<Contact> = emptyList()
)
