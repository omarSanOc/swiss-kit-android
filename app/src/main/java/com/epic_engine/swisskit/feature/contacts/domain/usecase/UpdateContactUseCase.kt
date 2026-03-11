package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import com.epic_engine.swisskit.feature.contacts.domain.util.PhoneNumberNormalizer
import javax.inject.Inject

class UpdateContactUseCase @Inject constructor(
    private val repo: ContactRepository
) {
    suspend operator fun invoke(contact: Contact) {
        require(contact.name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(PhoneNumberNormalizer.isValid(contact.phone)) { "Número de teléfono inválido" }
        repo.update(contact.copy(
            name = contact.name.trim(),
            phone = PhoneNumberNormalizer.normalize(contact.phone)
        ))
    }
}
