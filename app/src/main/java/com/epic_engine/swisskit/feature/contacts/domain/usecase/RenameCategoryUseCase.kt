package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import javax.inject.Inject

class RenameCategoryUseCase @Inject constructor(private val repo: CategoryRepository) {
    suspend operator fun invoke(categoryId: String, newTitle: String) {
        require(newTitle.isNotBlank()) { "El nombre no puede estar vacío" }
        repo.rename(categoryId, newTitle.trim())
    }
}
