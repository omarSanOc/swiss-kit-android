package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteAllContactsUseCase @Inject constructor(private val repo: ContactRepository) {
    suspend operator fun invoke(categoryId: String) = repo.deleteAll(categoryId)
}
