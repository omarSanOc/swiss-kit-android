package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchContactsUseCase @Inject constructor(private val repo: ContactRepository) {
    operator fun invoke(categoryId: String, query: String): Flow<List<Contact>> =
        if (query.isBlank()) repo.observeByCategory(categoryId)
        else repo.searchInCategory(categoryId, query)
}
