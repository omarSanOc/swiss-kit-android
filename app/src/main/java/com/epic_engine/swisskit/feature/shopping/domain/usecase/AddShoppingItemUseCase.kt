package com.epic_engine.swisskit.feature.shopping.domain.usecase

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import javax.inject.Inject

class AddShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(name: String): Result<ShoppingItem> {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        if (repository.isDuplicate(trimmed)) {
            return Result.failure(DuplicateItemException(trimmed))
        }
        return repository.addItem(trimmed)
    }
}

class DuplicateItemException(val itemName: String) : Exception("'$itemName' ya está en la lista")
