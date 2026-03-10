package com.epic_engine.swisskit.feature.finance

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.epic_engine.swisskit.core.database.MIGRATION_1_2
import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.finance.data.local.FinanceEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FinanceDaoTest {

    private lateinit var database: SwissKitDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SwissKitDatabase::class.java
        ).addMigrations(MIGRATION_1_2).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() { database.close() }

    private fun entity(id: String, type: String = "INCOME") = FinanceEntity(
        id = id, title = "Test $id", amount = 100.0,
        date = System.currentTimeMillis(), notes = null,
        category = "Salario", type = type
    )

    @Test
    fun insert_and_observe_all() = runTest {
        val dao = database.financeDao()
        dao.insert(entity("1"))
        dao.observeAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("1", items.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delete_by_ids_removes_correct_items() = runTest {
        val dao = database.financeDao()
        dao.insert(entity("1"))
        dao.insert(entity("2"))
        dao.insert(entity("3"))
        dao.deleteByIds(listOf("1", "3"))
        dao.observeAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("2", items.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delete_all_clears_table() = runTest {
        val dao = database.financeDao()
        dao.insert(entity("1"))
        dao.insert(entity("2"))
        dao.deleteAll()
        val all = dao.getAll()
        assertTrue(all.isEmpty())
    }

    @Test
    fun update_modifies_existing_entity() = runTest {
        val dao = database.financeDao()
        dao.insert(entity("1"))
        dao.update(entity("1").copy(title = "Modificado"))
        dao.observeAll().test {
            val items = awaitItem()
            assertEquals("Modificado", items.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
