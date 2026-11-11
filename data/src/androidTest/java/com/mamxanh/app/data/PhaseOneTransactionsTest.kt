package com.mamxanh.app.data

import android.database.sqlite.SQLiteDatabase
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.GameClock
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PhaseOneTransactionsTest {
    private lateinit var sql: SQLiteDatabase
    private lateinit var connection: GameSqlConnection
    private lateinit var engine: PhaseOneEngine
    private val clock=FakeClock()
    @Before fun setup() {
        sql=SQLiteDatabase.create(null)
        connection=object:GameSqlConnection {
            override fun query(sqlText:String,columns:Int,args:List<String>) =
                sql.rawQuery(sqlText,args.toTypedArray()).use{cursor->buildList {
                    while(cursor.moveToNext())add(GameSqlRow(List(columns){if(cursor.isNull(it))null else cursor.getString(it)}))
                }}
            override fun execute(sqlText:String,args:List<Any?>) { sql.execSQL(sqlText,args.toTypedArray()) }
            override fun <T> transaction(block:()->T):T {
                sql.beginTransaction()
                try { val value=block();sql.setTransactionSuccessful();return value } finally {sql.endTransaction()}
            }
        }
        GameSchema.create(connection)
        sql.execSQL("DELETE FROM tasks")
        engine=PhaseOneEngine(connection,clock)
    }
    @After fun close(){sql.close()}
    private fun add(c:TaskConfig=TaskConfig(title="Read")):Long {
        assertTrue(engine.save(null,c).success)
        return sql.rawQuery("SELECT MAX(id) FROM tasks",null).use{it.moveToFirst();it.getLong(0)}
    }
    private fun coins(n:Int){sql.execSQL("UPDATE meta SET v=? WHERE k='coins'",arrayOf(n.toString()))}
    @Test fun clickExactlyOnceAndAtomicLedger() {
        val id=add()
        assertEquals(8,engine.complete(id).granted)
        assertFalse(engine.complete(id).success)
        assertEquals(1,engine.snapshot().occurrences.size)
        assertEquals(8,engine.snapshot().earnedToday)
    }
    @Test fun fiftyCoinCapAndPartialReceipt() {
        repeat(5){engine.complete(add())}
        val sixth=engine.complete(add())
        assertEquals(8,sixth.granted)
        val seventh=engine.complete(add())
        assertEquals(2,seventh.granted)
        assertEquals(8,seventh.nominal)
        assertTrue(engine.complete(add()).success)
        assertEquals(50,engine.snapshot().earnedToday)
    }
    @Test fun capAt46GrantsFour() {
        sql.execSQL("INSERT INTO wallet VALUES('prior','2026-11-09','TASK','x',0,46,46,46)")
        val id=add(TaskConfig(title="Timer",verification="TIMER",minutes=45))
        engine.timer(id,"START")
        sql.execSQL("UPDATE sessions SET seconds=required")
        assertEquals(4,engine.complete(id).granted)
    }
    @Test fun spendingDoesNotRestoreCap() {
        repeat(7){engine.complete(add())}
        coins(500)
        assertTrue(engine.purchase("succulent",1,"buy").success)
        assertEquals(0,engine.complete(add()).granted)
    }
    @Test fun quantityAndRetryAreAtomic() {
        coins(1000)
        assertTrue(engine.purchase("succulent",2,"same").success)
        assertFalse(engine.purchase("succulent",2,"same").success)
        assertEquals(700,sql.rawQuery("SELECT v FROM meta WHERE k='coins'",null).use{it.moveToFirst();it.getInt(0)})
        assertEquals(3,sql.rawQuery("SELECT quantity FROM inventory WHERE item='succulent'",null).use{it.moveToFirst();it.getInt(0)})
    }
    @Test fun unaffordableLeavesEverythingUnchanged() {
        assertFalse(engine.purchase("pine",2,"no").success)
        assertEquals(0,engine.snapshot().ledger.size)
    }
    @Test fun todoTerminalAcrossDates() {
        val id=add(TaskConfig(title="Once",kind="TODO",frequency="ONCE"))
        assertTrue(engine.complete(id).success)
        clock.advanceDay()
        assertFalse(engine.complete(id).success)
    }
    @Test fun weekdayWeekendNeutralAndCustom() {
        val id=add(TaskConfig(title="Weekday",frequency="WEEKDAYS"))
        clock.set("2026-11-14 12:00")
        assertFalse(engine.tasks().first{it.id==id}.scheduledToday)
        assertFalse(engine.complete(id).success)
        assertNull(TaskConfig(title="Custom",frequency="CUSTOM",days="13").error())
        assertNotNull(TaskConfig(title="Custom",frequency="CUSTOM").error())
    }
    @Test fun morningWindowRejectsLateCompletion() {
        val id=add(TaskConfig(title="Morning",startMinute=360,endMinute=540))
        assertFalse(engine.complete(id).success)
        clock.set("2026-11-10 07:00")
        assertTrue(engine.complete(id).success)
    }
    @Test fun overnightUsesPreviousScheduledDate() {
        val id=add(TaskConfig(title="Night",startMinute=1320,endMinute=120))
        clock.set("2026-11-10 01:00")
        assertTrue(engine.complete(id).success)
        assertEquals("2026-11-09",engine.snapshot().occurrences.first().date)
        assertFalse(engine.complete(id).success)
    }
    @Test fun timerNeedsEnoughWindowAndActiveTime() {
        val id=add(TaskConfig(title="Timer",verification="TIMER",minutes=20,startMinute=660,endMinute=730))
        assertFalse(engine.timer(id,"START").success)
        clock.set("2026-11-10 11:30")
        assertTrue(engine.timer(id,"START").success)
        assertFalse(engine.complete(id).success)
        clock.millis+=600000
        engine.timer(id,"TICK")
        assertEquals(2,engine.snapshot().sessions.first().activeSeconds)
    }
    @Test fun pausedTimeNeverCountsAndRestartRetainsSession() {
        val id=add(TaskConfig(title="Timer",verification="TIMER"))
        engine.timer(id,"START")
        clock.millis+=1000;engine.timer(id,"TICK")
        engine.timer(id,"PAUSE");clock.millis+=300000
        val reopened=PhaseOneEngine(connection,clock)
        assertEquals(1,reopened.snapshot().sessions.first().activeSeconds)
        assertFalse(reopened.snapshot().sessions.first().running)
    }
    @Test fun timerCannotFinishOutsideWindow() {
        val id=add(TaskConfig(title="Timer",verification="TIMER",startMinute=660,endMinute=780))
        engine.timer(id,"START")
        sql.execSQL("UPDATE sessions SET seconds=required")
        clock.set("2026-11-09 14:00")
        assertFalse(engine.complete(id).success)
    }
    @Test fun timezoneChangeCannotClaimTimer() {
        val id=add(TaskConfig(title="Timer",verification="TIMER"))
        engine.timer(id,"START");sql.execSQL("UPDATE sessions SET seconds=required")
        clock.zoneId="Asia/Bangkok"
        assertFalse(engine.complete(id).success)
    }
    @Test fun dailyClearOnceWithSnapshotAndMiddayTask() {
        // Created after today's first materialization: optional today, required tomorrow.
        val id=add()
        clock.advanceDay()
        engine.snapshot()
        val optional=add()
        assertTrue(engine.complete(id).success)
        assertTrue(engine.clear().success)
        assertFalse(engine.clear().success)
        assertFalse(engine.tasks().first{it.id==optional}.doneToday)
    }
    @Test fun noRequiredTasksNoClear() {
        engine.snapshot()
        assertFalse(engine.clear().success)
    }
    @Test fun pausedObligationNeutralAndHistoryPreserved() {
        val id=add();clock.advanceDay();engine.snapshot()
        engine.lifecycle(id,"PAUSED")
        assertEquals(0,engine.snapshot().required)
        assertFalse(engine.complete(id).success)
        engine.lifecycle(id,"ACTIVE")
        assertTrue(engine.complete(id).success)
        engine.lifecycle(id,"ARCHIVED")
        assertEquals(1,engine.snapshot().occurrences.size)
    }
    @Test fun editingDoesNotErasePastOccurrence() {
        val id=add()
        engine.complete(id)
        engine.save(id,TaskConfig(title="New title",frequency="WEEKENDS"))
        assertEquals("Read",engine.snapshot().occurrences.first().title)
        assertFalse(engine.complete(id).success)
    }
    @Test fun clockRollbackDoesNotReissue() {
        val id=add()
        engine.complete(id);clock.advanceDay();engine.complete(id)
        clock.set("2026-11-09 12:00")
        assertFalse(engine.complete(id).success)
        assertEquals(2,engine.snapshot().ledger.size)
    }
    @Test fun validationAndRewardAreGameControlled() {
        assertNotNull(TaskConfig(title=" ").error())
        assertNotNull(TaskConfig(title="x",description="x".repeat(501)).error())
        assertNotNull(TaskConfig(title="x",verification="HOLD").error())
        assertNotNull(TaskConfig(title="x",kind="TODO",due="2026-02-30").error())
        assertEquals(15,TaskConfig(verification="TIMER",minutes=20).reward)
    }
    @Test fun pricesAndEarningFloors() {
        assertEquals(4500,catalog.first{it.id=="pine"}.price)
        assertEquals(90,4500/Balance.DAILY_CAP)
        assertEquals(113,kotlin.math.ceil(4500/40.0).toInt())
        listOf(14,30,90,180).forEach{days->listOf(20,40,50).forEach{income->
            assertEquals(days*income,(1..days).sumOf{minOf(income,Balance.DAILY_CAP)})
        }}
    }
    @Test fun existingVersionThreeMigratesWithoutLoss() {
        listOf("occurrences","wallet","plans","obligations","sessions","timer_intervals","care_events","purchase_receipts","garden_plots","tasks","trees","inventory","meta","logs").forEach{sql.execSQL("DROP TABLE $it")}
        sql.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY,title TEXT NOT NULL,kind TEXT NOT NULL,category TEXT NOT NULL,reward INTEGER NOT NULL,frequency TEXT NOT NULL,due TEXT NOT NULL DEFAULT '',completed TEXT NOT NULL DEFAULT '',streak INTEGER NOT NULL DEFAULT 0,last_done TEXT NOT NULL DEFAULT '')")
        sql.execSQL("CREATE TABLE trees(id INTEGER PRIMARY KEY,species_id TEXT,name TEXT,growth INTEGER,planted TEXT)")
        sql.execSQL("CREATE TABLE inventory(item TEXT PRIMARY KEY,quantity INTEGER)")
        sql.execSQL("CREATE TABLE meta(k TEXT PRIMARY KEY,v TEXT)")
        sql.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY,date TEXT,task_id INTEGER,title TEXT,coins INTEGER)")
        sql.execSQL("CREATE TABLE garden_plots(id INTEGER PRIMARY KEY,unlocked INTEGER,tree_id INTEGER UNIQUE)")
        sql.execSQL("INSERT INTO tasks VALUES(42,'Legacy','HABIT','Study',99,'EVERY_DAY','','2026-11-08',3,'2026-11-08')")
        sql.execSQL("INSERT INTO logs VALUES(7,'2026-11-08',42,'Legacy',99)")
        sql.execSQL("INSERT INTO trees VALUES(17,'cherry','Old tree',100,'2026-11-01')")
        sql.execSQL("INSERT INTO inventory VALUES('pine',3)")
        sql.execSQL("INSERT INTO garden_plots VALUES(1,1,17)")
        sql.execSQL("INSERT INTO meta VALUES('coins','12345')")
        sql.execSQL("INSERT INTO meta VALUES('water_ml','692')")
        sql.execSQL("INSERT INTO meta VALUES('daily_bonus','2026-11-08')")
        GameSchema.migrate(connection,3,4)
        val game=SqlGameDatabase(connection)
        assertEquals(12345,game.garden().coins)
        assertEquals(3,game.garden().inventory["pine"])
        assertEquals(17L,game.garden().plots.first().treeId)
        assertEquals(692,game.waterMl())
        assertEquals(42L,game.tasks().first().id)
        assertEquals("CLICK",game.tasks().first().config.verification)
        assertEquals(99,game.phaseSnapshot().ledger.first().amount)
        assertEquals(1,sql.rawQuery("SELECT COUNT(*) FROM occurrences WHERE task_id=42",null).use{it.moveToFirst();it.getInt(0)})
    }
    @Test fun plantingExactPlotAndConcurrentRetry() {
        val game=SqlGameDatabase(connection)
        val before=game.garden()
        assertEquals(1,before.inventory["succulent"])
        // Viewing/canceling the selection performs no mutation.
        assertEquals(before,game.garden())
        assertTrue(game.plant("succulent","Chosen",3))
        assertFalse(game.plant("succulent","Retry",3))
        assertEquals(0,game.garden().inventory["succulent"])
        assertNotNull(game.garden().plots.first{it.id==3}.treeId)
        assertNull(game.garden().plots.first{it.id==1}.treeId)
    }
    @Test fun careAtCapAndWaterRetry() {
        val game=SqlGameDatabase(connection)
        game.plant("succulent","Tree",1)
        val id=game.garden().trees.first().id
        sql.execSQL("INSERT OR REPLACE INTO inventory VALUES('fertilizer',2)")
        sql.execSQL("UPDATE trees SET growth=120")
        assertFalse(game.fertilize(id))
        assertEquals(2,game.garden().inventory["fertilizer"])
        sql.execSQL("UPDATE trees SET growth=0")
        assertTrue(game.fertilize(id))
        assertEquals(1,game.garden().inventory["fertilizer"])
        assertEquals(TreeWateringResult.WATERED,game.water(id))
        val water=game.waterMl()
        assertEquals(TreeWateringResult.ALREADY_WATERED,game.water(id))
        assertEquals(water,game.waterMl())
        assertEquals(2,game.phaseSnapshot().care.size)
    }
    @Test fun editCannotReplaceVerificationDuringTimer() {
        val id=add(TaskConfig(title="Timer",verification="TIMER",minutes=20))
        engine.timer(id,"START")
        assertFalse(engine.save(id,TaskConfig(title="Click",verification="CLICK")).success)
    }
    @Test fun daylightSavingWindowHasActualElapsedLength() {
        val previous=TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
            val real=com.mamxanh.shared.time.SystemGameClock()
            assertEquals(3600000L,real.localMinute("2026-03-08",210)-real.localMinute("2026-03-08",90))
            assertEquals(10800000L,real.localMinute("2026-11-01",150)-real.localMinute("2026-11-01",30))
        } finally {TimeZone.setDefault(previous)}
    }
    @Test fun restartReopensConfirmedStateFromDisk() {
        val context=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val file=java.io.File(context.cacheDir,"phase-one-restart-${System.nanoTime()}.db")
        sql.close()
        sql=SQLiteDatabase.openOrCreateDatabase(file,null)
        GameSchema.create(connection)
        engine=PhaseOneEngine(connection,clock)
        val id=add()
        engine.complete(id)
        coins(1000)
        engine.purchase("succulent",2,"durable-purchase")
        val game=SqlGameDatabase(connection)
        assertTrue(game.plant("succulent","Persisted tree",2))
        val before=game.garden()
        sql.close()
        sql=SQLiteDatabase.openOrCreateDatabase(file,null)
        val reopened=SqlGameDatabase(connection)
        assertEquals(before,reopened.garden())
        assertEquals(1,reopened.phaseSnapshot().occurrences.size)
        assertFalse(PhaseOneEngine(connection,clock).complete(id).success)
        file.delete()
    }
    @Test fun concurrentDoubleTapCommitsOnce() {
        val id=add()
        val executor=java.util.concurrent.Executors.newFixedThreadPool(2)
        try {
            val futures=(1..2).map{executor.submit<Boolean>{engine.complete(id).success}}
            assertEquals(1,futures.count{it.get()})
            assertEquals(8,engine.snapshot().earnedToday)
        } finally {executor.shutdownNow()}
    }
    @Test fun scheduledStreakSkipsRestDaysAndMilestoneIsUnique() {
        val id=add(TaskConfig(title="Weekdays",frequency="WEEKDAYS"))
        clock.set("2026-11-13 12:00")
        engine.complete(id);engine.clear()
        clock.set("2026-11-16 12:00")
        engine.complete(id)
        assertEquals(2,engine.snapshot().streak)
        assertEquals(2,engine.tasks().first{it.id==id}.streak)
        assertTrue(engine.clear().success)
        assertFalse(engine.clear().success)
    }
    @Test fun overnightScheduleEditPreservesTodayAnchor() {
        val id=add(TaskConfig(title="Night",startMinute=1320,endMinute=120))
        clock.advanceDay();engine.snapshot()
        clock.set("2026-11-10 23:00")
        engine.save(id,TaskConfig(title="Night",startMinute=480,endMinute=540))
        clock.set("2026-11-11 01:00")
        assertTrue(engine.complete(id).success)
        assertEquals("2026-11-10",engine.snapshot().occurrences.first().date)
    }
    @Test fun clockJumpDoesNotAddUnverifiedTimerTime() {
        val id=add(TaskConfig(title="Timer",verification="TIMER"))
        val steady=object:GameClock by clock {
            var monotonic=1000L
            override fun elapsed()=monotonic
        }
        val guarded=PhaseOneEngine(connection,steady)
        guarded.timer(id,"START")
        clock.millis+=3600000
        steady.monotonic+=1000
        guarded.timer(id,"TICK")
        assertEquals(1,guarded.snapshot().sessions.first().activeSeconds)
    }
    @Test fun shopCatalogMatchesTransactionCatalogAndEverySeedPlants() {
        val ui=com.mamxanh.shared.feature.shop.domain.model.shopCatalog
        assertEquals(catalog.map{it.id to it.price},ui.map{it.id to it.price})
        val game=SqlGameDatabase(connection)
        catalog.forEach { species ->
            sql.execSQL("INSERT OR REPLACE INTO inventory VALUES(?,1)",arrayOf(species.id))
            val plot=game.garden().plots.firstOrNull{it.isUnlocked && it.treeId==null}
            if(plot==null) {
                coins(100000)
                assertTrue(game.unlockPlot(game.garden().plots.first{!it.isUnlocked}.id))
            }
            assertTrue(species.id,game.plant(species.id,species.name,null))
        }
        assertEquals(catalog.size,game.garden().trees.size)
    }
    private class FakeClock:GameClock {
        var zoneId="UTC"
        var millis=0L
        init{set("2026-11-09 12:00")}
        private fun format(pattern:String)=SimpleDateFormat(pattern,Locale.US).apply{timeZone=TimeZone.getTimeZone(zoneId)}
        fun set(value:String){millis=format("yyyy-MM-dd HH:mm").parse(value)!!.time}
        fun advanceDay(){millis+=86400000}
        override fun now()=millis
        override fun date()=format("yyyy-MM-dd").format(java.util.Date(millis))
        override fun zone()=zoneId
        override fun localMinute(date:String,minute:Int)=format("yyyy-MM-dd HH:mm").parse("$date ${clockLabel(minute)}")!!.time
    }
}
