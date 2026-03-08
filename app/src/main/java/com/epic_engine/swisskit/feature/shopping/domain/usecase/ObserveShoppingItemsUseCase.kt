package com.epic_engine.swisskit.feature.shopping.domain.usecase

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShoppingItemsUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    operator fun invoke(): Flow<List<ShoppingItem>> = repository.observeItems()
}
