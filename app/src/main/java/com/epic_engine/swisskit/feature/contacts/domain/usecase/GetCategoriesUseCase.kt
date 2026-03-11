package com.epic_engine.swisskit.feature.contacts.domain.usecase

import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val repo: CategoryRepository) {
    operator fun invoke(): Flow<List<Category>> = repo.observeAll()
}
