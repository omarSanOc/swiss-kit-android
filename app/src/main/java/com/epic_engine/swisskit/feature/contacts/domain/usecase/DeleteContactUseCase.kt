package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(private val repo: ContactRepository) {
    suspend operator fun invoke(contact: Contact) = repo.delete(contact)
}
