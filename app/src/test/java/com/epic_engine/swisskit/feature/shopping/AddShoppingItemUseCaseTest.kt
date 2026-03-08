package com.epic_engine.swisskit.feature.shopping

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import com.epic_engine.swisskit.feature.shopping.domain.usecase.AddShoppingItemUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.DuplicateItemException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddShoppingItemUseCaseTest {

    private lateinit var repository: ShoppingRepository
    private lateinit var useCase: AddShoppingItemUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = AddShoppingItemUseCase(repository)
    }

    @Test
    fun `blank name returns failure`() = runTest {
        val result = useCase("   ")
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.addItem(any()) }
    }

    @Test
    fun `duplicate name returns DuplicateItemException`() = runTest {
        coEvery { repository.isDuplicate("Leche") } returns true
        val result = useCase("Leche")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DuplicateItemException)
    }

    @Test
    fun `valid unique name calls repository addItem`() = runTest {
        val fakeItem = ShoppingItem(id = "1", name = "Pan", isChecked = false, sortOrder = 0)
        coEvery { repository.isDuplicate("Pan") } returns false
        coEvery { repository.addItem("Pan") } returns Result.success(fakeItem)

        val result = useCase("Pan")
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addItem("Pan") }
    }

    @Test
    fun `name is trimmed before duplicate check`() = runTest {
        coEvery { repository.isDuplicate("Pan") } returns false
        coEvery { repository.addItem("Pan") } returns Result.success(
            ShoppingItem("1", "Pan", false, 0)
        )
        useCase("  Pan  ")
        coVerify { repository.isDuplicate("Pan") }
    }
}
