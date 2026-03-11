package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import com.epic_engine.swisskit.feature.contacts.domain.util.PhoneNumberNormalizer
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val repo: ContactRepository
) {
    suspend operator fun invoke(name: String, phone: String, categoryId: String): Contact {
        require(name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(PhoneNumberNormalizer.isValid(phone)) { "Número de teléfono inválido" }
        return repo.add(name.trim(), PhoneNumberNormalizer.normalize(phone), categoryId)
    }
}
