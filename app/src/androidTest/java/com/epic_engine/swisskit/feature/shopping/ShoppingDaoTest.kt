package com.epic_engine.swisskit.feature.shopping

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingItemEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShoppingDaoTest {

    private lateinit var database: SwissKitDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SwissKitDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_and_observe_items() = runTest {
        val dao = database.shoppingDao()
        val item = ShoppingItemEntity(id = "1", name = "Manzanas", isChecked = false, sortOrder = 0)
        dao.insert(item)

        dao.observeAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Manzanas", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun countByName_is_case_insensitive() = runTest {
        val dao = database.shoppingDao()
        dao.insert(ShoppingItemEntity(id = "1", name = "  Leche  ", isChecked = false, sortOrder = 0))
        val count = dao.countByName("leche")
        assertEquals(1, count)
    }

    @Test
    fun deleteChecked_removes_only_checked_items() = runTest {
        val dao = database.shoppingDao()
        dao.insert(ShoppingItemEntity(id = "1", name = "Pan", isChecked = false, sortOrder = 0))
        dao.insert(ShoppingItemEntity(id = "2", name = "Leche", isChecked = true, sortOrder = 1))
        dao.deleteChecked()

        dao.observeAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Pan", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uncheckAll_sets_all_items_to_unchecked() = runTest {
        val dao = database.shoppingDao()
        dao.insert(ShoppingItemEntity(id = "1", name = "Pan", isChecked = true, sortOrder = 0))
        dao.insert(ShoppingItemEntity(id = "2", name = "Leche", isChecked = true, sortOrder = 1))
        dao.uncheckAll()

        dao.observeAll().test {
            val items = awaitItem()
            assertTrue(items.all { !it.isChecked })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
