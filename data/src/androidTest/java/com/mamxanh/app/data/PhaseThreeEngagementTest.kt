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
import java.util.*

class PhaseThreeEngagementTest {
    private lateinit var sql:SQLiteDatabase
    private lateinit var connection:GameSqlConnection
    private lateinit var engagement:EngagementEngine
    private lateinit var phase:PhaseOneEngine
    private val clock=Clock()
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
        GameSchema.create(connection);engagement=EngagementEngine(connection,clock);phase=PhaseOneEngine(connection,clock)
    }
    @After fun close(){sql.close()}
    private fun count(s:String)=sql.rawQuery(s,null).use{it.moveToFirst();it.getInt(0)}
    private fun add(c:TaskConfig=TaskConfig("A task")):Long {
        assertTrue(phase.save(null,c).success)
        return sql.rawQuery("SELECT MAX(id) FROM tasks",null).use{it.moveToFirst();it.getLong(0)}
    }
    private fun tick(id:String,seconds:Int){repeat(seconds/2){clock.ms+=2000;assertTrue(engagement.act(EngagementAction.Focus(id,"TICK")).success)}}
    @Test fun onboardingChoosesCommonAndCopiesOnce() {
        assertFalse(engagement.snapshot().onboarded)
        val action=EngagementAction.Onboard("snake","Sức khỏe",listOf("brush-am","walk20"),"onboard")
        assertTrue(engagement.act(action).success);assertFalse(engagement.act(action).success)
        assertEquals(2,phase.tasks().size);assertEquals(0,count("SELECT quantity FROM inventory WHERE item='succulent'"))
        assertEquals(1,count("SELECT quantity FROM inventory WHERE item='snake'"))
        assertTrue(SqlGameDatabase(connection,clock).plant("snake","",1))
        assertEquals(1,count("SELECT free_starter FROM trees"))
    }
    @Test fun skipNeverRequiresPermissionsOrCreatesTasks() {
        assertTrue(engagement.act(EngagementAction.Onboard("","",emptyList(),"skip",true)).success)
        assertTrue(engagement.snapshot().onboarded);assertTrue(phase.tasks().isEmpty())
        assertTrue(engagement.snapshot().settings.paused)
    }
    @Test fun templateReceiptsAndDifferentRetryKeysStillCopyOnce() {
        repeat(3){assertTrue(engagement.act(EngagementAction.Templates(listOf("read20","read20"),"copy$it")).success)}
        assertEquals(1,phase.tasks().size)
        assertEquals("TIMER",phase.tasks().single().config.verification)
        assertFalse(engagement.act(EngagementAction.Templates(listOf("read20"),"copy0")).success)
    }
    @Test fun standalonePauseRecoveryDoesNotCountAbsentTime() {
        assertTrue(engagement.act(EngagementAction.StartFocus(minutes=5,key="f")).success)
        tick("f",10);engagement.act(EngagementAction.Focus("f","PAUSE"))
        clock.ms+=3600000
        assertEquals(10,engagement.snapshot().focus.single().activeSeconds.toInt())
        engagement.act(EngagementAction.Focus("f","RESUME"));tick("f",4)
        engagement=EngagementEngine(connection,clock)
        engagement.act(EngagementAction.Focus("","RECOVER"));clock.ms+=600000
        assertEquals(14,engagement.snapshot().focus.single().activeSeconds.toInt())
        assertEquals("PAUSED",engagement.snapshot().focus.single().state)
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun earlyEndIsIncompleteAndRetainsIntervals() {
        engagement.act(EngagementAction.StartFocus(minutes=5,key="f"));tick("f",12)
        engagement.act(EngagementAction.Focus("f","END"))
        assertEquals("ENDED",engagement.snapshot().focus.single().state)
        assertEquals(12,count("SELECT SUM(seconds) FROM focus_intervals"))
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun endingPausedAttachedFocusRemainsEndedAfterRecovery() {
        val task=add(TaskConfig("Stretch",verification="TIMER",minutes=5))
        engagement.act(EngagementAction.StartFocus(task,5,"paused"))
        tick("paused",12)
        engagement.act(EngagementAction.Focus("paused","PAUSE"))
        assertTrue(engagement.act(EngagementAction.Focus("paused","END")).success)
        engagement.act(EngagementAction.Focus("","RECOVER"))
        val session=EngagementEngine(connection,clock).snapshot().focus.single()
        assertEquals("ENDED",session.state)
        assertEquals(12,session.activeSeconds.toInt())
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun attachedFocusUsesOneTaskRewardAndBadgeHasNoIncome() {
        val task=add(TaskConfig("Study",verification="TIMER",minutes=25))
        assertTrue(engagement.act(EngagementAction.StartFocus(task,25,"attached")).success)
        tick("attached",1500)
        assertEquals("COMPLETE",engagement.snapshot().focus.single().state)
        assertEquals(15,phase.snapshot().earnedToday)
        assertFalse(phase.complete(task).success)
        repeat(3){engagement.snapshot()}
        assertEquals(1,count("SELECT COUNT(*) FROM occurrences"))
        assertEquals(1,count("SELECT COUNT(*) FROM achievement_unlocks WHERE id='focus25'"))
        assertEquals(15,phase.snapshot().earnedToday)
    }
    @Test fun standaloneCompletionNeverPaysCoins() {
        engagement.act(EngagementAction.StartFocus(minutes=5,key="free"));tick("free",300)
        assertEquals("COMPLETE",engagement.snapshot().focus.single().state)
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun breakPausesAndCannotAwardBeforeThreshold() {
        engagement.act(EngagementAction.Settings(EngagementSettings(breakPrompts=true)))
        engagement.act(EngagementAction.StartFocus(minutes=45,key="break"));tick("break",1500)
        assertEquals("BREAK",engagement.snapshot().focus.single().state)
        clock.ms+=600000;engagement.act(EngagementAction.Focus("break","TICK"))
        assertEquals(1500,engagement.snapshot().focus.single().activeSeconds.toInt())
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun restIsProspectiveBoundedAndNeverClearOrSuccess() {
        add();phase.snapshot()
        assertFalse(engagement.act(EngagementAction.Rest(clock.date())).success)
        assertTrue(engagement.act(EngagementAction.Rest("2026-11-10")).success)
        assertFalse(engagement.act(EngagementAction.Rest("2026-11-10")).success)
        assertTrue(engagement.act(EngagementAction.Rest("2026-11-11")).success)
        assertFalse(engagement.act(EngagementAction.Rest("2026-11-12")).success)
        clock.set("2026-11-10 12:00");assertEquals(0,phase.snapshot().required)
        assertFalse(phase.canClear());assertFalse(phase.clear().success)
        assertEquals("REST",engagement.snapshot().days.last().state)
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun protectionConsumesOneTicketAndDoesNotGrantDailyClear() {
        add();phase.snapshot();clock.set("2026-11-10 12:00");phase.snapshot();clock.set("2026-11-11 12:00");phase.snapshot()
        sql.execSQL("INSERT OR REPLACE INTO inventory VALUES('protection',1)")
        val action=EngagementAction.Protect("2026-11-10","protect")
        assertTrue(engagement.act(action).success);assertFalse(engagement.act(action).success)
        assertEquals(0,count("SELECT quantity FROM inventory WHERE item='protection'"))
        assertEquals(1,count("SELECT COUNT(*) FROM protection_uses"))
        assertEquals(0,count("SELECT cleared FROM plans WHERE date='2026-11-10'"))
        assertEquals(0,phase.snapshot().earnedToday)
    }
    @Test fun protectionPurchaseIsAtomicAndNotRepeatable() {
        sql.execSQL("UPDATE meta SET v='500' WHERE k='coins'")
        assertTrue(engagement.act(EngagementAction.BuyProtection("buy")).success)
        assertFalse(engagement.act(EngagementAction.BuyProtection("buy")).success)
        assertEquals(250,count("SELECT CAST(v AS INTEGER) FROM meta WHERE k='coins'"))
        assertEquals(1,count("SELECT quantity FROM inventory WHERE item='protection'"))
    }
    @Test fun timelineAndAchievementsDeduplicateAcrossRestart() {
        phase.complete(add())
        val before=engagement.snapshot()
        assertTrue(before.achievements.first{it.id=="first-step"}.unlockedAt>0)
        engagement=EngagementEngine(connection,clock)
        repeat(4){engagement.snapshot()}
        assertEquals(before.memories.size,engagement.snapshot().memories.size)
        assertEquals(1,count("SELECT COUNT(*) FROM memory_events WHERE id='first-task'"))
    }
    @Test fun reviewHasNoSyntheticTrendsAndReflectionPersists() {
        val empty=engagement.snapshot().review!!
        assertNull(empty.rate);assertNull(empty.previousRate);assertEquals(0,empty.earned)
        val id=add();clock.set("2026-11-10 12:00");phase.snapshot();phase.complete(id)
        val review=engagement.snapshot().review!!
        assertEquals(1,review.scheduled);assertEquals(1,review.completed);assertEquals(8,review.earned)
        assertTrue(engagement.act(EngagementAction.Reflection(review.start,"A calmer morning")).success)
        assertEquals("A calmer morning",EngagementEngine(connection,clock).snapshot().review!!.reflection)
    }
    @Test fun remindersAreOptInUniqueAndSuppressCompletedPausedTasks() {
        val id=add();engagement.act(EngagementAction.Reminder(ReminderPreference(id,"AT",780,true)))
        assertTrue(engagement.reminders().isEmpty())
        engagement.act(EngagementAction.Settings(EngagementSettings(paused=false)))
        val candidate=engagement.reminders().first{it.kind=="PRIMARY"}
        clock.ms=candidate.at;assertTrue(engagement.claimReminder(id,candidate.date,candidate.kind))
        assertFalse(engagement.claimReminder(id,candidate.date,candidate.kind))
        assertTrue(phase.complete(id).success)
        assertFalse(engagement.claimReminder(id,candidate.date,"FOLLOWUP"))
        phase.lifecycle(id,"PAUSED");assertTrue(engagement.reminders().isEmpty())
    }
    @Test fun quietHoursAndWindowDoNotOfferLateAlerts() {
        val id=add(TaskConfig("Brush",startMinute=360,endMinute=540))
        engagement.act(EngagementAction.Settings(EngagementSettings(paused=false)))
        engagement.act(EngagementAction.Reminder(ReminderPreference(id,"AT",1380,true)))
        assertTrue(engagement.reminders().isEmpty())
        clock.set("2026-11-10 12:00")
        assertFalse(engagement.claimReminder(id,"2026-11-10","PRIMARY"))
    }
    @Test fun backwardClockCannotDuplicateEngagementMutations() {
        engagement.act(EngagementAction.Templates(listOf("water"),"first"))
        clock.ms-=86400000
        assertFalse(engagement.act(EngagementAction.Templates(listOf("read20"),"second")).success)
    }
    @Test fun overnightAttachedFocusKeepsOccurrenceAnchor() {
        clock.set("2026-11-09 23:50")
        val task=add(TaskConfig("Overnight",verification="TIMER",minutes=25,startMinute=1320,endMinute=120))
        assertTrue(engagement.act(EngagementAction.StartFocus(task,25,"night")).success)
        tick("night",10);engagement.act(EngagementAction.Focus("night","PAUSE"))
        clock.set("2026-11-10 01:00")
        assertTrue(engagement.act(EngagementAction.Focus("night","RESUME")).success)
        tick("night",2)
        val saved=engagement.snapshot().focus.single()
        assertEquals("2026-11-09",saved.date);assertEquals("RUNNING",saved.state)
        assertEquals(12,saved.activeSeconds.toInt())
    }
    @Test fun partialTaskHistoryDoesNotManufactureTwentyFiveMinuteBadge() {
        val task=add(TaskConfig("Prior partial",verification="TIMER",minutes=25))
        phase.timer(task,"START");sql.execSQL("UPDATE sessions SET seconds=1200")
        phase.timer(task,"PAUSE")
        engagement.act(EngagementAction.StartFocus(task,25,"short"));tick("short",300)
        assertEquals("COMPLETE",engagement.snapshot().focus.single().state)
        assertEquals(0,engagement.snapshot().achievements.first{it.id=="focus25"}.unlockedAt.toInt())
    }
    @Test fun beforeWindowReminderCanOpenButCannotCompleteEarly() {
        val task=add(TaskConfig("Morning",startMinute=540,endMinute=660))
        engagement.act(EngagementAction.Settings(EngagementSettings(paused=false,quietStart=0,quietEnd=0)))
        engagement.act(EngagementAction.Reminder(ReminderPreference(task,"BEFORE",540,false)))
        val candidate=engagement.reminders().first()
        clock.ms=candidate.at
        assertTrue(engagement.claimReminder(task,candidate.date,"PRIMARY"))
        assertFalse(phase.complete(task).success)
        assertFalse(engagement.claimReminder(task,candidate.date,"PRIMARY"))
    }
    @Test fun legacyVersionFiveMigratesWithoutReonboardingOrTaskLoss() {
        val id=add();sql.execSQL("UPDATE tasks SET verification='HOLD' WHERE id=$id")
        listOf("engagement_receipts","template_copies","focus_sessions","focus_intervals","reminder_preferences",
            "reminder_deliveries","achievement_unlocks","rest_days","protection_uses","memory_events","weekly_reflections").forEach{sql.execSQL("DROP TABLE $it")}
        sql.execSQL("DELETE FROM meta WHERE k='onboarded'")
        GameSchema.migrate(connection,5,GameSchema.VERSION)
        assertTrue(engagement.snapshot().onboarded)
        assertEquals(id,phase.tasks().single().id)
        assertEquals("CLICK",phase.tasks().single().config.verification)
    }
    private class Clock:GameClock {
        var ms=0L;var zoneId="UTC"
        private fun format(pattern:String)=SimpleDateFormat(pattern,Locale.US).apply{timeZone=TimeZone.getTimeZone(zoneId)}
        init{set("2026-11-09 12:00")}
        fun set(value:String){ms=format("yyyy-MM-dd HH:mm").parse(value)!!.time}
        override fun now()=ms
        override fun date()=format("yyyy-MM-dd").format(Date(ms))
        override fun zone()=zoneId
        override fun localMinute(date:String,minute:Int)=format("yyyy-MM-dd HH:mm").parse("$date ${clockLabel(minute)}")!!.time
    }
}
