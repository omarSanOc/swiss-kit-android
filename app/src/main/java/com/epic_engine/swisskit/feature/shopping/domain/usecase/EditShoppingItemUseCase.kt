package com.epic_engine.swisskit.feature.shopping.domain.usecase

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import javax.inject.Inject

class EditShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(item: ShoppingItem, newName: String): Result<Unit> {
        if (newName.isBlank()) return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        return repository.editItem(item, newName)
    }
}
