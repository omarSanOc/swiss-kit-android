package com.epic_engine.swisskit.feature.shopping.domain.usecase

import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import javax.inject.Inject

class DeleteCheckedItemsUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteChecked()
}
