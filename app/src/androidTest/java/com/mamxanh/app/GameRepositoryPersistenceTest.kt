package com.mamxanh.app

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.garden.data.GardenCollectionRepositoryImpl
import com.mamxanh.app.feature.garden.domain.GardenCollectionRepository
import com.mamxanh.app.feature.inventory.data.InventoryRepositoryImpl
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
import com.mamxanh.app.feature.shop.data.ShopRepositoryImpl
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.tree.data.TreeRepositoryImpl
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.app.feature.tree.domain.WateringOutcome
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

class GameRepositoryPersistenceTest {
    @Test
    fun appResolvesTheFeatureOwnedRepositories() {
        assertTrue(GlobalContext.get().get<InventoryRepository>() is InventoryRepositoryImpl)
        assertTrue(GlobalContext.get().get<ShopRepository>() is ShopRepositoryImpl)
        assertTrue(GlobalContext.get().get<TreeRepository>() is TreeRepositoryImpl)
        assertTrue(GlobalContext.get().get<GardenCollectionRepository>() is GardenCollectionRepositoryImpl)
    }

    @Test
    fun directAccessoryPurchaseEquipsAndUnequipsWithoutChargingTwice() {
        SQLiteDatabase.create(null).use { database ->
            val connection = InMemoryConnection(database)
            GameSchema.create(connection)
            val storage = GameStorage(SqlGameDatabase(connection))
            assertTrue(InventoryRepositoryImpl(storage).plantSeed("succulent", "My tree", plotId = 1))
            val treeId = storage.garden().trees.single().id
            database.execSQL("UPDATE meta SET v='1000' WHERE k='coins'")
            val tree = TreeRepositoryImpl(storage)
            val collection = GardenCollectionRepositoryImpl(storage)
            val sku = "tree_ribbon"

            assertTrue(tree.equipAccessory(sku, treeId, "direct-purchase") is RepositoryResult.Success)
            assertEquals(sku, collection.load().decorationsByTree[treeId])
            assertEquals(1, collection.load().ownedAccessories[sku])
            val remaining = storage.garden().coins
            assertTrue(remaining < 1000)

            assertTrue(tree.equipAccessory(sku, treeId, "equip-again") is RepositoryResult.Success)
            assertEquals(remaining, storage.garden().coins)
            assertTrue(tree.unequipAccessory(treeId) is RepositoryResult.Success)
            assertNull(collection.load().decorationsByTree[treeId])
            assertEquals(1, collection.load().ownedAccessories[sku])
        }
    }

    @Test
    fun collectionRetainsSpeciesHistoryAfterRemovingOrSellingTheTree() {
        for (sell in listOf(false, true)) {
            SQLiteDatabase.create(null).use { database ->
                val connection = InMemoryConnection(database)
                GameSchema.create(connection)
                val storage = GameStorage(SqlGameDatabase(connection))
                val inventory = InventoryRepositoryImpl(storage)
                assertTrue(inventory.plantSeed("succulent", "My tree", plotId = 1))
                val treeId = storage.garden().trees.single().id
                if (sell) {
                    database.execSQL(
                        "UPDATE trees SET life_state='MATURE', matured_at=? WHERE id=?",
                        arrayOf(System.currentTimeMillis(), treeId),
                    )
                }

                val collection = GardenCollectionRepositoryImpl(storage)
                val before = collection.load().species.single { it.id == "succulent" }
                assertTrue(before.discovered)
                assertTrue(before.firstPlanted > 0)
                assertTrue(before.highestStage > 0)
                if (sell) assertTrue(before.firstMatured > 0)

                val result = TreeRepositoryImpl(storage).dispose(treeId, sell, "disposition")
                assertTrue(result is RepositoryResult.Success)
                assertNull(storage.garden().plots.single { it.id == 1 }.treeId)
                val after = collection.load().species.single { it.id == before.id }
                assertEquals(before.firstPlanted, after.firstPlanted)
                assertEquals(before.firstMatured, after.firstMatured)
                assertEquals(before.highestStage, after.highestStage)
                assertTrue(after.discovered)
                assertEquals(0, after.ownedSeeds)
            }
        }
    }

    @Test
    fun wateringSurvivesRecreatingTheRepositoryAndRejectsASecondWatering() {
        SQLiteDatabase.create(null).use { database ->
            val connection = InMemoryConnection(database)
            GameSchema.create(connection)
            val storage = GameStorage(SqlGameDatabase(connection))
            val inventory = InventoryRepositoryImpl(storage)
            assertTrue(inventory.plantSeed("succulent", "My tree", plotId = null))
            val treeId = storage.garden().trees.single().id
            val tree = TreeRepositoryImpl(storage)
            assertEquals(WateringOutcome.MissingTree, tree.water(0))
            assertEquals(WateringOutcome.Watered, tree.water(treeId))

            val reopenedStorage = GameStorage(SqlGameDatabase(connection))
            val reopenedTree = TreeRepositoryImpl(reopenedStorage)
            assertTrue(reopenedStorage.garden().trees.single().status.waterActive)
            assertEquals(WateringOutcome.AlreadyWatered, reopenedTree.water(treeId))
        }
    }

    @Test
    fun shopPurchaseCreditsInventoryExactlyOnce() {
        SQLiteDatabase.create(null).use { database ->
            val connection = InMemoryConnection(database)
            GameSchema.create(connection)
            val storage = GameStorage(SqlGameDatabase(connection))
            val shop = ShopRepositoryImpl(storage)
            val inventory = InventoryRepositoryImpl(storage)
            val seed = "succulent"
            val initial = shop.loadWallet()
            assertTrue(shop.purchase(seed, 2, "insufficient") is RepositoryResult.Error)
            assertEquals(initial, shop.loadWallet())

            database.execSQL("UPDATE meta SET v='1000' WHERE k='coins'")
            val before = shop.loadWallet()
            assertTrue(shop.purchase(seed, 2, "purchase") is RepositoryResult.Success)
            val after = shop.loadWallet()
            assertTrue(after.coins < before.coins)
            assertEquals((before.inventory[seed] ?: 0) + 2, inventory.quantities()[seed])
            assertTrue(shop.purchase(seed, 2, "purchase") is RepositoryResult.Error)
            assertEquals(after, shop.loadWallet())
        }
    }

    @Test
    fun rejectedAndFailedPlantingPreserveTheSeedUntilPlantingSucceeds() {
        SQLiteDatabase.create(null).use { database ->
            val connection = InMemoryConnection(database)
            GameSchema.create(connection)
            val storage = GameStorage(SqlGameDatabase(connection))
            val inventory = InventoryRepositoryImpl(storage)
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
