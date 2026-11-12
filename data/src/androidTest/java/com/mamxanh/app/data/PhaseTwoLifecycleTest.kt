package com.mamxanh.app.data

import android.database.sqlite.SQLiteDatabase
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.*
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PhaseTwoLifecycleTest {
    private lateinit var db:SQLiteDatabase
    private lateinit var sql:GameSqlConnection
    private lateinit var game:SqlGameDatabase
    private lateinit var life:TreeLifecycleEngine
    private val clock=Clock()
    @Before fun setup() {
        db=SQLiteDatabase.create(null)
        sql=object:GameSqlConnection {
            override fun query(sql:String,columns:Int,args:List<String>) =
                db.rawQuery(sql,args.toTypedArray()).use{c->buildList{
                    while(c.moveToNext())add(GameSqlRow(List(columns){if(c.isNull(it))null else c.getString(it)}))
                }}
            override fun execute(sql:String,args:List<Any?>){db.execSQL(sql,args.toTypedArray())}
            override fun <T> transaction(block:()->T):T {
                db.beginTransaction()
                try{val result=block();db.setTransactionSuccessful();return result}finally{db.endTransaction()}
            }
        }
        GameSchema.create(sql)
        game=SqlGameDatabase(sql,clock);life=TreeLifecycleEngine(sql,clock)
    }
    @After fun close(){db.close()}
    private fun value(statement:String):Long=db.rawQuery(statement,null).use{it.moveToFirst();it.getLong(0)}
    private fun plant(species:String="succulent",plot:Int=1):Long {
        db.execSQL("INSERT OR REPLACE INTO inventory VALUES(?,1)",arrayOf(species))
        assertTrue(game.plant(species,"Test",plot))
        return game.garden().trees.first{it.speciesId==species}.id
    }
    private fun units(id:Long)=value("SELECT growth_units FROM trees WHERE id=$id")
    private fun advance(ms:Long){clock.time+=ms;game.garden()}
    private fun stock(id:String,n:Int){db.execSQL("INSERT OR REPLACE INTO inventory VALUES(?,?)",arrayOf<Any>(id,n))}
    private fun rich(){db.execSQL("UPDATE meta SET v='100000' WHERE k='coins'")}
    @Test fun dryTreeDoesNotGrow() {
        val id=plant()
        advance(5*DAY_MS)
        assertEquals(0L,units(id))
        assertEquals(TreeGrowthState.GROWING,game.garden().trees.first().status.state)
    }
    @Test fun waterWindowExpiresExactlyAndCannotExtendByTapping() {
        val id=plant()
        assertEquals(TreeWateringResult.WATERED,game.water(id))
        val deadline=game.garden().trees.first().status.wateredUntil
        assertEquals(clock.now()+8*HOUR_MS,deadline)
        assertEquals(TreeWateringResult.ALREADY_WATERED,game.water(id))
        advance(12*HOUR_MS)
        assertEquals(GROWTH_DAY_UNITS,units(id))
        assertEquals(deadline,game.garden().trees.first().status.wateredUntil)
        advance(3*HOUR_MS)
        assertEquals(GROWTH_DAY_UNITS,units(id))
    }
    @Test fun canUpgradeOnlyChangesFutureWindowsAndCannotBeBoughtTwice() {
        val id=plant()
        game.water(id)
        val first=game.garden().trees.first().status.wateredUntil
        rich()
        assertTrue(game.purchase("can_gold",1,"gold").success)
        assertFalse(game.purchase("can_gold",1,"duplicate").success)
        assertFalse(game.purchase("can_silver",2,"two").success)
        assertTrue(game.equipCan("can_gold").success)
        assertEquals(first,game.garden().trees.first().status.wateredUntil)
        advance(8*HOUR_MS)
        assertEquals(TreeWateringResult.WATERED,game.water(id))
        assertEquals(clock.now()+24*HOUR_MS,game.garden().trees.first().status.wateredUntil)
    }
    @Test fun waterAllIncludesMatureAndSkipsActiveDeadAndRetries() {
        val a=plant("succulent",1);val b=plant("pine",2);val c=plant("cherry",3)
        db.execSQL("UPDATE trees SET life_state='MATURE',matured_at=? WHERE id=?",arrayOf(clock.now(),b))
        db.execSQL("UPDATE trees SET life_state='DEAD',death_at=? WHERE id=?",arrayOf(clock.now(),c))
        game.water(a)
        val quote=game.waterAllQuote()
        assertEquals(1,quote.eligible);assertEquals(1,quote.active);assertEquals(1,quote.dead)
        assertTrue(game.waterAll("batch").success)
        val deadlines=game.garden().trees.map{it.status.wateredUntil}
        assertFalse(game.waterAll("batch").success)
        assertEquals(deadlines,game.garden().trees.map{it.status.wateredUntil})
        assertFalse(game.waterAll("empty").success)
    }
    @Test fun fertilizerOnlyMultipliesItsOverlap() {
        val id=plant()
        stock("fertilizer_rich",2)
        assertFalse(game.applyFertilizer(id,"fertilizer_rich").success)
        game.water(id)
        advance(HOUR_MS)
        assertTrue(game.applyFertilizer(id,"fertilizer_rich").success)
        advance(2*HOUR_MS)
        assertEquals(HOUR_MS*1000+2*HOUR_MS*1200,units(id))
        assertFalse(game.applyFertilizer(id,"fertilizer_rich").success)
        assertEquals(1L,value("SELECT quantity FROM inventory WHERE item='fertilizer_rich'"))
        advance(10*HOUR_MS)
        assertEquals(GROWTH_DAY_UNITS,units(id))
    }
    @Test fun fertilizerCooldownSurvivesEffectExpiration() {
        val id=plant();stock("fertilizer",3)
        game.water(id);assertTrue(game.applyFertilizer(id,"fertilizer").success)
        advance(5*HOUR_MS)
        assertFalse(game.applyFertilizer(id,"fertilizer").success)
        assertEquals(2L,value("SELECT quantity FROM inventory WHERE item='fertilizer'"))
        advance(19*HOUR_MS)
        game.water(id)
        assertTrue(game.applyFertilizer(id,"fertilizer").success)
    }
    @Test fun fertilizerCannotConsumeForMatureOrDeadTrees() {
        val id=plant();stock("fertilizer_premium",3);game.water(id)
        db.execSQL("UPDATE trees SET life_state='MATURE' WHERE id=?",arrayOf(id))
        assertFalse(game.applyFertilizer(id,"fertilizer_premium").success)
        db.execSQL("UPDATE trees SET life_state='DEAD' WHERE id=?",arrayOf(id))
        assertFalse(game.applyFertilizer(id,"fertilizer_premium").success)
        assertEquals(3L,value("SELECT quantity FROM inventory WHERE item='fertilizer_premium'"))
    }
    @Test fun neglectIsPreciseAndWaterReducesThreeDays() {
        val id=plant()
        advance(15*DAY_MS+HOUR_MS/2)
        assertEquals(15*DAY_MS+HOUR_MS/2,value("SELECT neglect_ms FROM trees WHERE id=$id"))
        assertEquals(TreeWateringResult.WATERED,game.water(id))
        assertEquals(12*DAY_MS+HOUR_MS/2,value("SELECT neglect_ms FROM trees WHERE id=$id"))
        repeat(4){game.garden()}
        assertEquals(12*DAY_MS+HOUR_MS/2,value("SELECT neglect_ms FROM trees WHERE id=$id"))
    }
    @Test fun deathAtThirtyDryDaysIsIrreversible() {
        val id=plant()
        advance(30*DAY_MS)
        assertEquals(TreeGrowthState.DEAD,game.garden().trees.first().status.state)
        val death=game.garden().trees.first().status.deathAt
        assertEquals(clock.now(),death)
        assertEquals(TreeWateringResult.DEAD,game.water(id))
        stock("fertilizer",1)
        assertFalse(game.applyFertilizer(id,"fertilizer").success)
        assertFalse(game.disposeTree(id,true,"dead-sale").success)
        advance(DAY_MS)
        assertEquals(death,game.garden().trees.first().status.deathAt)
        assertTrue(game.disposeTree(id,false,"remove-dead").success)
        assertTrue(game.garden().trees.isEmpty())
    }
    @Test fun lateWateringSimulatesDeathBeforeAttemptingRecovery() {
        val id=plant()
        clock.time+=31*DAY_MS
        assertEquals(TreeWateringResult.DEAD,game.water(id))
        assertEquals(30*DAY_MS,value("SELECT neglect_ms FROM trees WHERE id=$id"))
    }
    @Test fun savedDayCreditIsUniqueAcrossRestartAndMidnight() {
        clock.set("2026-11-01 20:00")
        val id=plant();game.water(id)
        advance(8*HOUR_MS)
        assertEquals(0,game.garden().trees.first().status.growthDays)
        // Midnight split is4h+4h: neither partial day becomes a full credit.
        assertEquals(2L,value("SELECT COUNT(*) FROM tree_growth_days"))
        val before=units(id)
        game=SqlGameDatabase(sql,clock)
        assertEquals(before,units(id))
        assertEquals(0,game.garden().trees.first().status.growthDays)
        clock.time+=DAY_MS
        game.water(id);advance(8*HOUR_MS)
        assertEquals(1,game.garden().trees.first().status.growthDays)
    }
    @Test fun goldAndPremiumNeverCreateSecondDailyCredit() {
        val id=plant();stock("can_gold",1);stock("fertilizer_premium",1)
        game.equipCan("can_gold");game.water(id);game.applyFertilizer(id,"fertilizer_premium")
        advance(20*HOUR_MS)
        assertEquals(1,game.garden().trees.first().status.growthDays)
        assertEquals(GROWTH_DAY_UNITS,units(id))
        repeat(3){game.garden()}
        assertEquals(1L,value("SELECT COUNT(*) FROM tree_growth_days WHERE credited=1"))
    }
    @Test fun freeTreeCannotMatureBeforeFourteenDaysAndCredits() {
        val id=plant()
        val birth=clock.now()
        repeat(14) {
            game.water(id);advance(8*HOUR_MS)
            if(it<13)assertNotEquals(TreeGrowthState.MATURE,game.garden().trees.first().status.state)
            advance(16*HOUR_MS)
        }
        val tree=game.garden().trees.first()
        assertEquals(TreeGrowthState.MATURE,tree.status.state)
        assertTrue(tree.status.maturedAt>=birth+14*DAY_MS)
        assertEquals(14,tree.status.growthDays)
        assertEquals(5,tree.status.stageIndex)
        assertEquals(0,game.treeSaleQuote(id).nominal)
        val capped=units(id);game.water(id);advance(8*HOUR_MS)
        assertEquals(capped,units(id))
    }
    @Test fun legendaryCannotMatureBefore127CreditsWithBestUpgrades() {
        val id=plant("pine")
        val birth=clock.now()
        stock("can_gold",1);stock("fertilizer_premium",150)
        game.equipCan("can_gold")
        repeat(127) { day->
            game.water(id);game.applyFertilizer(id,"fertilizer_premium")
            advance(DAY_MS)
            if(day<126)assertNotEquals(TreeGrowthState.MATURE,game.garden().trees.first().status.state)
        }
        val tree=game.garden().trees.first()
        assertEquals(TreeGrowthState.MATURE,tree.status.state)
        assertEquals(127,tree.status.growthDays)
        assertTrue(tree.status.maturedAt>=birth+lifecycleProfile("pine").minimumAgeMillis)
        assertEquals(11,tree.status.stageIndex)
    }
    @Test fun matureSaleIsCappedIdempotentAndFreesOnlyItsPlot() {
        val id=plant("pine")
        db.execSQL("UPDATE trees SET life_state='MATURE',matured_at=? WHERE id=?",arrayOf(clock.now(),id))
        db.execSQL("INSERT INTO wallet VALUES('earlier',?,'TASK','x',0,46,46,46)",arrayOf(clock.date()))
        val quote=game.treeSaleQuote(id)
        assertEquals(900,quote.nominal);assertEquals(4,quote.granted)
        val result=game.disposeTree(id,true,"sale")
        assertTrue(result.success);assertEquals(4,result.granted)
        assertFalse(game.disposeTree(id,true,"retry").success)
        assertNull(game.garden().plots.first{it.id==1}.treeId)
        assertEquals(1,game.treeDispositions().size)
        assertEquals(4L,value("SELECT v FROM meta WHERE k='coins'"))
        assertEquals(1L,value("SELECT COUNT(*) FROM trees WHERE disposition='SOLD'"))
    }
    @Test fun removalNeverRefundsSeedOrCoinsAndKeepsHistory() {
        val id=plant()
        assertTrue(game.disposeTree(id,false,"remove").success)
        assertFalse(game.disposeTree(id,false,"remove").success)
        assertEquals(0L,value("SELECT v FROM meta WHERE k='coins'"))
        assertEquals(0L,value("SELECT quantity FROM inventory WHERE item='succulent'"))
        assertNull(game.garden().plots.first().treeId)
        assertEquals("REMOVED",game.treeDispositions().single().kind)
    }
    @Test fun rollbackCannotAdvanceGrowOrRepeatCare() {
        val id=plant();game.water(id);advance(HOUR_MS)
        val before=units(id);val until=game.garden().trees.first().status.wateredUntil
        clock.time-=HOUR_MS
        assertEquals(before,units(id))
        assertEquals(TreeWateringResult.CLOCK_CHANGED,game.water(id))
        assertFalse(game.waterAll("rollback").success)
        assertEquals(until,game.garden().trees.first().status.wateredUntil)
    }
    @Test fun taskRewardDoesNotGiveInstantTreeGrowth() {
        val id=plant()
        val tasks=PhaseOneEngine(sql,clock)
        tasks.save(null,TaskConfig(title="Click"))
        assertTrue(tasks.complete(tasks.tasks().maxBy{it.id}.id).success)
        assertEquals(0L,units(id))
    }
    @Test fun splitSimulationMatchesOfflineCatchUp() {
        val p=lifecycleProfile("pine")
        val start=clock.now()
        val initial=TreeSimulation(start,"UTC",start,day=clock.date(),wateredUntil=start+24*HOUR_MS,
            fertilizerId="fertilizer_rich",fertilizerEndsAt=start+6*HOUR_MS,lastFertilizedAt=start)
        val whole=simulateTree(initial,p,start+10*DAY_MS).value
        var incremental=initial
        repeat(240){incremental=simulateTree(incremental,p,start+(it+1)*HOUR_MS).value}
        assertEquals(whole,incremental)
    }
    @Test fun plantingZonePinsCreditsAcrossDeviceTimezoneChanges() {
        val id=plant();game.water(id)
        clock.zoneId="Asia/Tokyo"
        advance(8*HOUR_MS)
        assertEquals(1,game.garden().trees.first().status.growthDays)
        assertEquals("UTC",db.rawQuery("SELECT zone FROM trees",null).use{it.moveToFirst();it.getString(0)})
    }
    @Test fun daylightSavingSplitsAtRealLocalMidnight() {
        val at=SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US).apply{timeZone=TimeZone.getTimeZone("America/New_York")}.parse("2026-03-08 00:00")!!.time
        assertEquals(23*HOUR_MS,treeMidnightAfter(at,"America/New_York")-at)
        val p=lifecycleProfile("pine")
        val result=simulateTree(TreeSimulation(at,"America/New_York",at,wateredUntil=at+24*HOUR_MS),p,at+24*HOUR_MS)
        assertEquals(1,result.value.creditedDays)
        assertEquals(2,result.days.size)
    }
    @Test fun profileStageCountsAndLongTermSimulations() {
        assertEquals(6,lifecycleProfile("succulent").stageCount)
        assertEquals(12,lifecycleProfile("pine").stageCount)
        assertEquals(catalog.map{it.id}.toSet(),lifecycleProfiles.map{it.id}.toSet())
        lifecycleProfiles.forEach{p->
            assertEquals(p.stageCount,p.stageThresholds.distinct().size)
            assertEquals(p.seedPrice,catalog.first{it.id==p.id}.price)
        }
        listOf(14,30,90,180).forEach{days->
            listOf(20,40,50).forEach{income->
                assertEquals(days*income,(1..days).sumOf{minOf(income,Balance.DAILY_CAP)})
            }
            val start=clock.now()
            var s=TreeSimulation(start,"UTC",start)
            repeat(days){d->
                s=s.copy(wateredUntil=start+d*DAY_MS+8*HOUR_MS,lastWateredAt=start+d*DAY_MS,
                    neglectMillis=(s.neglectMillis-3*DAY_MS).coerceAtLeast(0))
                s=simulateTree(s,lifecycleProfile("pine"),start+(d+1)*DAY_MS).value
            }
            assertEquals(if(days>=127)TreeGrowthState.MATURE else TreeGrowthState.GROWING,s.state)
            assertTrue(s.growthUnits<=lifecycleProfile("pine").finalUnits)
        }
    }
    @Test fun universalGeometricScheduleAndFourTierCatalog() {
        val common=lifecycleProfile("succulent")
        val rare=lifecycleProfile("cherry")
        val legendary=lifecycleProfile("pine")
        assertEquals(14*GROWTH_DAY_UNITS,common.finalUnits)
        assertEquals(9,rare.stageCount)
        assertEquals(44,rare.requiredGrowthDays)
        assertEquals(127,legendary.requiredGrowthDays)
        assertEquals(10_911_676_905L,legendary.minimumAgeMillis)
        assertEquals(900,rare.seedPrice)
        assertEquals(listOf("Common","Rare","Legendary"),lifecycleProfiles.map{it.tier}.distinct())
        val transitions=legendary.stageThresholds.zipWithNext { a,b-> b-a }
        transitions.zipWithNext { a,b->assertEquals(1.4,b.toDouble()/a,0.000000001) }
        assertEquals(common.stageThresholds,legendary.stageThresholds.take(6))
        assertEquals(rare.stageThresholds,legendary.stageThresholds.take(9))
        val at=clock.now()
        val justBefore=TreeSimulation(at,"UTC",at+legendary.minimumAgeMillis,
            growthUnits=legendary.finalUnits,creditedDays=126,wateredUntil=at+200*DAY_MS)
        assertEquals(TreeGrowthState.GROWING,simulateTree(justBefore,legendary,justBefore.at+1).value.state)
        assertEquals(10,treeStatus(justBefore,legendary,false).stageIndex)
    }
    @Test fun phaseFourMigrationPreservesAssetsAndGrantsFairGrace() {
        db.execSQL("DROP TABLE tree_growth_days")
        db.execSQL("DROP TABLE tree_dispositions")
        db.execSQL("DROP TABLE tree_actions")
        db.execSQL("ALTER TABLE trees RENAME TO newer_trees")
        db.execSQL("CREATE TABLE trees(id INTEGER PRIMARY KEY,species_id TEXT NOT NULL,name TEXT NOT NULL,growth INTEGER NOT NULL,planted TEXT NOT NULL)")
        db.execSQL("INSERT INTO trees VALUES(71,'succulent','Old starter',120,'2020-01-01')")
        db.execSQL("INSERT INTO trees VALUES(72,'cherry','Old partial',60,'2020-01-01')")
        db.execSQL("DROP TABLE newer_trees")
        db.execSQL("UPDATE garden_plots SET tree_id=71 WHERE id=1")
        db.execSQL("UPDATE garden_plots SET tree_id=72 WHERE id=2")
        db.execSQL("DELETE FROM inventory WHERE item='can_basic'")
        stock("pine",3);stock("fertilizer",9);rich()
        db.execSQL("INSERT OR REPLACE INTO meta VALUES('water_ml','9999')")
        val now=clock.now()
        GameSchema.migrate(sql,4,GameSchema.VERSION)
        GameSchema.migrate(sql,GameSchema.VERSION,GameSchema.VERSION)
        val garden=game.garden()
        assertEquals(100000,garden.coins)
        assertEquals(3,garden.inventory["pine"]);assertEquals(9,garden.inventory["fertilizer"])
        assertEquals(1,garden.inventory["can_basic"])
        assertEquals(71L,garden.plots.first().treeId)
        val mature=garden.trees.first{it.id==71L}
        val partial=garden.trees.first{it.id==72L}
        assertEquals(TreeGrowthState.MATURE,mature.status.state)
        assertEquals(0,mature.status.salePrice)
        assertEquals(TreeGrowthState.GROWING,partial.status.state)
        assertEquals(lifecycleProfile("cherry").finalUnits/2,units(72))
        assertEquals(now+8*HOUR_MS,partial.status.wateredUntil)
        assertEquals(0L,value("SELECT neglect_ms FROM trees WHERE id=72"))
        assertEquals(0L,value("SELECT COUNT(*) FROM meta WHERE k='water_ml'"))
        assertEquals(3L,value("SELECT lifecycle_version FROM trees WHERE id=72"))
        game=SqlGameDatabase(sql,clock)
        assertEquals(garden,game.garden())
    }
    @Test fun concurrentCareAndSaleCommitOnlyOnce() {
        val id=plant("pine");stock("fertilizer",2)
        val pool=java.util.concurrent.Executors.newFixedThreadPool(2)
        try {
            val waters=(1..2).map{pool.submit<TreeWateringResult>{game.water(id)}}
            assertEquals(1,waters.count{it.get()==TreeWateringResult.WATERED})
            val fertilizers=(1..2).map{pool.submit<Boolean>{game.applyFertilizer(id,"fertilizer").success}}
            assertEquals(1,fertilizers.count{it.get()})
            assertEquals(1L,value("SELECT quantity FROM inventory WHERE item='fertilizer'"))
            db.execSQL("UPDATE trees SET life_state='MATURE',matured_at=? WHERE id=?",arrayOf(clock.now(),id))
            val sales=(1..2).map{n->pool.submit<Boolean>{game.disposeTree(id,true,"parallel-$n").success}}
            assertEquals(1,sales.count{it.get()})
            assertEquals(50L,value("SELECT v FROM meta WHERE k='coins'"))
            assertEquals(1L,value("SELECT COUNT(*) FROM tree_dispositions"))
        } finally {pool.shutdownNow()}
    }
    private class Clock:GameClock {
        var zoneId="UTC"
        var time=0L
        init{set("2026-11-01 00:00")}
        fun set(s:String){time=formatter().parse(s)!!.time}
        private fun formatter()=SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US).apply{timeZone=TimeZone.getTimeZone(zoneId)}
        override fun now()=time
        override fun date()=treeDateAt(time,zoneId)
        override fun zone()=zoneId
        override fun localMinute(date:String,minute:Int)=formatter().parse("$date ${clockLabel(minute)}")!!.time
    }
}
