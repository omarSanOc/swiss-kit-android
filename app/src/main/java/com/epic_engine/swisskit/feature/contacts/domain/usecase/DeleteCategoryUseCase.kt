package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val repo: CategoryRepository) {
    suspend operator fun invoke(categoryId: String) = repo.delete(categoryId)
}
