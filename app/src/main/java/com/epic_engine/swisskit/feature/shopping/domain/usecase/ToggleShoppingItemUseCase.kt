package com.epic_engine.swisskit.feature.shopping.domain.usecase

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import javax.inject.Inject

class ToggleShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(item: ShoppingItem): Result<Unit> =
        repository.toggleItem(item.copy(isChecked = !item.isChecked))
}
