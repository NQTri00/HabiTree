package com.mamxanh.app.data

import android.database.sqlite.SQLiteDatabase
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.GameClock
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class PhaseFourGardenTest {
    private lateinit var sql:SQLiteDatabase
    private lateinit var connection:GameSqlConnection
    private lateinit var engine:LivingGardenEngine
    private lateinit var game:SqlGameDatabase
    private val clock=object:GameClock {
        override fun now()=1_794_484_800_000L
        override fun date()="2026-11-12"
        override fun zone()="UTC"
        override fun localMinute(date:String,minute:Int):Long {
            val f=java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.US).apply{timeZone=java.util.TimeZone.getTimeZone("UTC")}
            return f.parse(date)!!.time+minute*60_000
        }
    }
    @Before fun setup() {
        sql=SQLiteDatabase.create(null)
        connection=object:GameSqlConnection {
            override fun query(s:String,n:Int,args:List<String>)=sql.rawQuery(s,args.toTypedArray()).use{c->
                buildList{while(c.moveToNext())add(GameSqlRow(List(n){if(c.isNull(it))null else c.getString(it)}))}}
            override fun execute(s:String,args:List<Any?>){sql.execSQL(s,args.toTypedArray())}
            override fun <T> transaction(block:()->T):T {
                sql.beginTransaction();try{val result=block();sql.setTransactionSuccessful();return result}finally{sql.endTransaction()}
            }
        }
        GameSchema.create(connection);engine=LivingGardenEngine(connection,clock);game=SqlGameDatabase(connection,clock)
        sql.execSQL("UPDATE meta SET v='100000' WHERE k='coins'")
    }
    @After fun close(){sql.close()}
    private fun number(s:String)=sql.rawQuery(s,null).use{it.moveToFirst();it.getLong(0)}
    private fun buy(sku:String,key:String=sku){assertTrue(engine.act(LivingGardenAction.Buy(sku,key)).success)}
    private fun save(items:List<GardenPlacement>,key:String="layout")=
        engine.act(LivingGardenAction.Layout(engine.snapshot().version,items,key))
    private fun plant(id:String="succulent",plot:Int=1):Long {
        sql.execSQL("INSERT OR REPLACE INTO inventory VALUES(?,1)",arrayOf(id))
        assertTrue(game.plant(id,"Test $id",plot))
        return game.garden().plots.first{it.id==plot}.treeId!!
    }
    @Test fun freshSectionsAreFiniteWithStableInitialPlotIds() {
        val garden=game.garden()
        assertEquals(24,garden.plots.size)
        assertEquals((1..5).toList(),garden.plots.filter{it.isUnlocked}.map{it.id})
        assertEquals(4,engine.snapshot().sections.size)
        assertFalse(engine.act(LivingGardenAction.Expand(5,"outside")).success)
        assertFalse(game.unlockPlot(25))
    }
    @Test fun migrationPreservesTreePlotsWalletAndLegacyAccessories() {
        val tree=plant()
        sql.execSQL("INSERT OR REPLACE INTO meta VALUES('decoration_$tree','RIBBON')")
        listOf("garden_layout","species_records","collection_rewards","garden_receipts","garden_sections").forEach{sql.execSQL("DROP TABLE $it")}
        sql.execSQL("DELETE FROM garden_plots WHERE id>6")
        val coins=number("SELECT CAST(v AS INTEGER) FROM meta WHERE k='coins'")
        GameSchema.migrate(connection,6,GameSchema.VERSION)
        assertEquals(tree,game.garden().plots.first{it.id==1}.treeId)
        assertEquals(coins,engine.snapshot().coins.toLong())
        assertEquals("tree_ribbon",engine.snapshot().placements.single().sku)
        assertEquals(1,engine.snapshot().owned["tree_ribbon"])
    }
    @Test fun buyingUpdatesOwnershipButNeverPlaces() {
        buy("accent_lantern")
        assertEquals(1,engine.snapshot().owned["accent_lantern"])
        assertTrue(engine.snapshot().placements.isEmpty())
        assertEquals(99700,engine.snapshot().coins)
    }
    @Test fun permanentEntitlementCannotChargeTwiceEvenWithDifferentKeys() {
        buy("ground_grass")
        assertFalse(engine.act(LivingGardenAction.Buy("ground_grass","retry")).success)
        assertEquals(99900,engine.snapshot().coins)
        assertEquals(1,engine.snapshot().owned["ground_grass"])
    }
    @Test fun stackableDecorationPurchaseReceiptIsUnique() {
        buy("accent_lantern","one")
        assertFalse(engine.act(LivingGardenAction.Buy("accent_lantern","one")).success)
        buy("accent_lantern","two")
        assertEquals(2,engine.snapshot().owned["accent_lantern"])
    }
    @Test fun cancelledDraftHasNoSideEffects() {
        buy("ground_grass")
        val before=engine.snapshot()
        val localDraft=before.placements+GardenPlacement("draft","ground_grass",1)
        assertEquals(1,localDraft.size)
        assertEquals(before,LivingGardenEngine(connection,clock).snapshot())
    }
    @Test fun validSavePersistsExactLayoutAndReceipt() {
        buy("ground_grass");val layout=listOf(GardenPlacement("a","ground_grass",1))
        assertTrue(save(layout).success)
        val restored=LivingGardenEngine(connection,clock).snapshot()
        assertEquals(layout,restored.placements)
        assertFalse(engine.act(LivingGardenAction.Layout(restored.version,layout,"layout")).success)
    }
    @Test fun lockedAndOffGridPlacementAreAtomicFailures() {
        buy("ground_grass")
        for(plot in listOf(6,25,-1))assertFalse(save(listOf(GardenPlacement("a","ground_grass",plot)),"bad$plot").success)
        assertTrue(engine.snapshot().placements.isEmpty())
        assertEquals(1,engine.snapshot().owned["ground_grass"])
    }
    @Test fun overlapAndOverallocatedOwnershipNeverConsumeOrOverwrite() {
        buy("ground_grass");buy("ground_moss")
        assertFalse(save(listOf(GardenPlacement("a","ground_grass",1),GardenPlacement("b","ground_moss",1))).success)
        assertFalse(save(listOf(GardenPlacement("a","ground_grass",1),GardenPlacement("b","ground_grass",2))).success)
        assertEquals(99900-250,engine.snapshot().coins)
        assertTrue(engine.snapshot().placements.isEmpty())
    }
    @Test fun separateSlotsCoexistWithoutBlockingTree() {
        buy("ground_grass");buy("border_wood");buy("accent_stone")
        assertTrue(save(listOf(GardenPlacement("a","ground_grass",1),GardenPlacement("b","border_wood",1),GardenPlacement("c","accent_stone",1))).success)
        plant()
        assertEquals(3,engine.snapshot().placements.size)
    }
    @Test fun movingAndReturningOwnedDecorationNeverCharges() {
        buy("ground_grass")
        assertTrue(save(listOf(GardenPlacement("a","ground_grass",1)),"one").success)
        val coins=engine.snapshot().coins
        assertTrue(save(listOf(GardenPlacement("a","ground_grass",2)),"two").success)
        assertTrue(save(emptyList(),"three").success)
        assertEquals(coins,engine.snapshot().coins)
        assertEquals(1,engine.snapshot().owned["ground_grass"])
    }
    @Test fun staleVersionCannotOverwriteAnotherLayout() {
        buy("ground_grass");val version=engine.snapshot().version
        assertTrue(save(listOf(GardenPlacement("a","ground_grass",1))).success)
        assertFalse(engine.act(LivingGardenAction.Layout(version,emptyList(),"stale")).success)
        assertEquals(1,engine.snapshot().placements.size)
    }
    @Test fun accessoryRequiresLivingTreeInMatchingPlot() {
        buy("tree_ribbon")
        assertFalse(save(listOf(GardenPlacement("a","tree_ribbon",1,99))).success)
        val tree=plant()
        assertFalse(save(listOf(GardenPlacement("a","tree_ribbon",2,tree))).success)
        assertTrue(save(listOf(GardenPlacement("a","tree_ribbon",1,tree))).success)
        assertEquals("RIBBON",game.garden().trees.single().decoration)
    }
    @Test fun dispositionRetainsRecordsAndReturnsAccessory() {
        val tree=plant();buy("tree_ribbon")
        save(listOf(GardenPlacement("a","tree_ribbon",1,tree)))
        val before=engine.snapshot().species.first{it.id=="succulent"}
        assertTrue(game.disposeTree(tree,false,"remove").success)
        val after=engine.snapshot()
        assertEquals(before.firstPlanted,after.species.first{it.id=="succulent"}.firstPlanted)
        assertEquals(before.highestStage,after.species.first{it.id=="succulent"}.highestStage)
        assertTrue(after.placements.isEmpty())
        assertEquals(1,after.owned["tree_ribbon"])
    }
    @Test fun galleryUsesActualStageAndNeverSeedOwnershipAsMaturity() {
        val initial=engine.snapshot().species.first{it.id=="succulent"}
        assertEquals(-1,initial.highestStage)
        plant()
        val planted=engine.snapshot().species.first{it.id=="succulent"}
        assertEquals(0,planted.highestStage)
        assertEquals(0L,planted.firstMatured)
        assertEquals(6,catalog.first{it.id=="succulent"}.formCount)
    }
    @Test fun maturityRewardsAreOneTimeCosmeticsWithNoIncome() {
        listOf("succulent","cherry","snake").forEachIndexed{index,species->
            sql.execSQL("INSERT INTO trees(species_id,name,growth,planted) VALUES(?,?,120,'2026-01-01')",arrayOf(species,species))
            val id=number("SELECT MAX(id) FROM trees")
            sql.execSQL("UPDATE garden_plots SET tree_id=? WHERE id=?",arrayOf<Any>(id,index+1))
        }
        val before=engine.snapshot()
        assertEquals(1,before.owned["reward_sign"])
        assertEquals(1,before.owned["reward_petals"])
        repeat(3){engine.snapshot()}
        assertEquals(1,engine.snapshot().owned["reward_sign"])
        assertEquals(before.coins,engine.snapshot().coins)
        assertEquals(0,game.phaseSnapshot().earnedToday)
    }
    @Test fun themesAndParticleSettingsHaveNoGameplayEffect() {
        val tree=plant();buy("theme_night")
        val before=game.garden().trees.single()
        val inventory=engine.snapshot().owned
        assertTrue(engine.act(LivingGardenAction.Appearance(engine.snapshot().appearance.copy(theme="theme_night",density="NORMAL",batterySaver=true))).success)
        assertEquals(before,game.garden().trees.first{it.id==tree})
        assertEquals(inventory,engine.snapshot().owned)
        assertFalse(engine.act(LivingGardenAction.Appearance(engine.snapshot().appearance.copy(theme="theme_stone"))).success)
    }
    @Test fun reducedMotionAndBatterySaverSuppressParticlesWithoutDroppingLayout() {
        val value=GardenAppearance(density="NORMAL")
        assertTrue(value.particlesEnabled(false));assertFalse(value.particlesEnabled(true))
        assertFalse(value.copy(batterySaver=true).particlesEnabled(false))
        assertFalse(value.copy(density="OFF").particlesEnabled(false))
    }
    @Test fun expansionIsFiniteAtomicAndRewardsOnce() {
        assertFalse(engine.act(LivingGardenAction.Expand(3,"skip")).success)
        assertTrue(engine.act(LivingGardenAction.Expand(2,"expand")).success)
        val before=engine.snapshot()
        assertFalse(engine.act(LivingGardenAction.Expand(2,"retry")).success)
        assertEquals(before.coins,engine.snapshot().coins)
        assertEquals(1,before.owned["reward_path"])
        assertTrue(game.garden().plots.filter{it.id in 7..12}.all{it.isUnlocked})
    }
    @Test fun unknownFutureLayoutIsPreservedAndCannotBeInvented() {
        sql.execSQL("INSERT INTO garden_layout VALUES('future','future_sku',1,0)")
        val before=engine.snapshot()
        assertFalse(save(emptyList()).success)
        assertTrue(save(before.placements).success)
        assertFalse(save(before.placements+GardenPlacement("new","future_sku",2),"invent").success)
    }
    @Test fun prePhaseFourDisposedTreeRetainsIntermediateGallery() {
        val tree=plant()
        val units=catalog.first{it.id=="succulent"}.profile.stageThresholds[3]
        sql.execSQL("UPDATE trees SET growth_units=?,disposition='REMOVED',life_state='GROWING' WHERE id=?",arrayOf<Any>(units,tree))
        sql.execSQL("UPDATE garden_plots SET tree_id=NULL WHERE tree_id=?",arrayOf(tree))
        sql.execSQL("DELETE FROM species_records")
        assertEquals(3,engine.snapshot().species.first{it.id=="succulent"}.highestStage)
        assertEquals(0L,engine.snapshot().species.first{it.id=="succulent"}.firstMatured)
    }
    @Test fun legacyAccessoryCopiesAreAllGrandfathered() {
        val a=plant("succulent",1);val b=plant("snake",2)
        sql.execSQL("INSERT OR REPLACE INTO meta VALUES('decoration_$a','RIBBON')")
        sql.execSQL("INSERT OR REPLACE INTO meta VALUES('decoration_$b','RIBBON')")
        listOf("garden_layout","species_records","collection_rewards","garden_receipts","garden_sections").forEach{sql.execSQL("DROP TABLE $it")}
        GameSchema.migrate(connection,6,GameSchema.VERSION)
        val snapshot=engine.snapshot()
        assertEquals(2,snapshot.owned["tree_ribbon"])
        assertEquals(2,snapshot.placements.size)
        assertTrue(save(snapshot.placements).success)
    }
    @Test fun expansionCannotReuseLayoutReceiptOrSpendOnFailure() {
        assertTrue(save(emptyList(),"shared-key").success)
        val before=engine.snapshot()
        assertFalse(engine.act(LivingGardenAction.Expand(2,"shared-key")).success)
        assertEquals(before.coins,engine.snapshot().coins)
        assertFalse(engine.snapshot().sections.first{it.id==2}.unlocked)
    }
    @Test fun discoveriesAreDurableAndDenominatorMatchesShippedCatalog() {
        engine.act(LivingGardenAction.Discover(listOf("pine","future")))
        val records=LivingGardenEngine(connection,clock).snapshot().species
        assertEquals(catalog.size,records.size)
        assertTrue(records.first{it.id=="pine"}.discoveredAt>0)
        assertFalse(records.any{it.id=="future"})
        assertEquals(-1,records.first{it.id=="pine"}.highestStage)
    }
}
