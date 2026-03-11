package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(private val repo: CategoryRepository) {
    suspend operator fun invoke(title: String): Category {
        require(title.isNotBlank()) { "El nombre de la categoría no puede estar vacío" }
        return repo.add(title.trim())
    }
}
