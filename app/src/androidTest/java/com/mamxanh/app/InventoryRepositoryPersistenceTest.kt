package com.mamxanh.app

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import com.mamxanh.app.feature.inventory.data.OfflineInventoryRepository
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.GameSchema
import com.mamxanh.shared.data.sql.GameSqlConnection
import com.mamxanh.shared.data.sql.GameSqlRow
import com.mamxanh.shared.data.sql.SqlGameDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.context.GlobalContext

class InventoryRepositoryPersistenceTest {
    @Test
    fun appResolvesTheFeatureOwnedInventoryRepository() {
        assertTrue(GlobalContext.get().get<InventoryRepository>() is OfflineInventoryRepository)
    }

    @Test
    fun rejectedAndFailedPlantingPreserveTheSeedUntilPlantingSucceeds() {
        SQLiteDatabase.create(null).use { database ->
            val connection = InMemoryConnection(database)
            GameSchema.create(connection)
            val storage = GameStorage(SqlGameDatabase(connection))
            val inventory = OfflineInventoryRepository(storage)
            val initial = storage.garden()
            val openPlot = initial.plots.first { it.isUnlocked && it.treeId == null }.id
            val lockedPlot = initial.plots.first { !it.isUnlocked }.id
            val seed = "succulent"

            assertEquals(1, inventory.quantities()[seed])
            assertFalse(inventory.plantSeed(seed, "My tree", lockedPlot))
            assertFalse(inventory.equipWateringCan("unowned-can").success)
            assertEquals(initial.equippedCan, storage.garden().equippedCan)
            assertEquals(1, inventory.quantities()[seed])

            database.execSQL(
                """
                CREATE TEMP TRIGGER reject_layout_change BEFORE INSERT ON meta
                WHEN NEW.k = 'garden_layout_version'
                BEGIN SELECT RAISE(ABORT, 'simulated layout write failure'); END
                """.trimIndent(),
            )
            assertThrows(SQLiteConstraintException::class.java) {
                inventory.plantSeed(seed, "My tree", openPlot)
            }
            database.execSQL("DROP TRIGGER reject_layout_change")
            val rolledBack = storage.garden()
            assertEquals(initial.trees.map { it.id }, rolledBack.trees.map { it.id })
            assertNull(rolledBack.plots.single { it.id == openPlot }.treeId)
            assertEquals(1, inventory.quantities()[seed])

            assertTrue(inventory.plantSeed(seed, "My tree", openPlot))
            val planted = storage.garden()
            val treeId = planted.plots.single { it.id == openPlot }.treeId
            assertEquals("My tree", planted.trees.single { it.id == treeId }.name)
            assertEquals(0, inventory.quantities()[seed] ?: 0)
        }
    }

    private class InMemoryConnection(private val database: SQLiteDatabase) : GameSqlConnection {
        override fun query(sql: String, columns: Int, args: List<String>): List<GameSqlRow> =
            database.rawQuery(sql, args.toTypedArray()).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(GameSqlRow(List(columns) { if (cursor.isNull(it)) null else cursor.getString(it) }))
                    }
                }
            }

        override fun execute(sql: String, args: List<Any?>) {
            database.execSQL(sql, args.toTypedArray())
        }

        override fun <T> transaction(block: () -> T): T {
            database.beginTransaction()
            try {
                val result = block()
                database.setTransactionSuccessful()
                return result
            } finally {
                database.endTransaction()
            }
        }
    }
}
