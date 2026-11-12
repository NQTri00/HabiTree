package com.mamxanh.app.data

import android.database.sqlite.SQLiteDatabase
import com.mamxanh.app.data.local.AndroidGameSqlConnection
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.model.*
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class PhaseFiveBackupTest {
    private lateinit var sql:SQLiteDatabase
    private lateinit var c:GameSqlConnection
    private lateinit var backup:GameBackupEngine
    private lateinit var game:SqlGameDatabase
    @Before fun setup() {
        sql=SQLiteDatabase.create(null);sql.execSQL("PRAGMA foreign_keys=ON")
        c=AndroidGameSqlConnection{sql};GameSchema.create(c)
        backup=GameBackupEngine(c);game=SqlGameDatabase(c)
        sql.execSQL("UPDATE meta SET v='5000' WHERE k='coins'")
    }
    @After fun close(){sql.close()}
    private fun snapshot()=backup.snapshot("test",1234)
    private fun seed() {
        game.saveTask(null,TaskConfig("Riêng tư 🌱\ncolon: pipe|", "HABIT","Test"))
        assertTrue(game.plant("succulent","Unicode 🌿",1))
        sql.execSQL("INSERT INTO weekly_reflections VALUES('2026-11-09','Ghi chú riêng tư',1234)")
        sql.execSQL("UPDATE trees SET neglect_ms=123456 WHERE id=1")
        assertTrue(game.purchase("fertilizer",1,"purchase").success)
    }
    private fun rejects(block:()->Unit){try{block();fail("Expected rejection")}catch(_:IllegalArgumentException){}catch(_:IllegalStateException){}catch(_:android.database.SQLException){}}
    private fun modify(s:GameBackup,table:String,column:String,value:String?)=s.copy(tables=s.tables.map{t->
        if(t.name!=table)t else t.copy(rows=t.rows.mapIndexed{i,r->if(i!=0)r else r.mapIndexed{j,v->if(t.columns[j]==column)value else v}})})
    @Test fun digestMatchesPublishedVectors() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",BackupDigest.sha256(byteArrayOf()))
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",BackupDigest.sha256("abc".encodeToByteArray()))
        val bytes=ByteArray(1000){it.toByte()}
        assertEquals(java.security.MessageDigest.getInstance("SHA-256").digest(bytes).joinToString(""){"%02x".format(it)},BackupDigest.sha256(bytes))
    }
    @Test fun everyPersistentTableIsRegistered(){assertEquals(34,snapshot().tables.size)}
    @Test fun unicodeAndNullableCellsRoundTripExactly() {
        seed();val original=snapshot();assertEquals(original,GameBackupCodec.decode(GameBackupCodec.encode(original)))
        assertTrue(original.tables.first{it.name=="garden_plots"}.rows.any{it[2]==null})
    }
    @Test fun restoreTwiceDoesNotDuplicateAnything() {
        seed();val original=snapshot();game.saveTask(null,TaskConfig("Later","TODO","Test"))
        backup.restore(original);assertEquals(original,snapshot())
        backup.restore(original);assertEquals(original,snapshot())
    }
    @Test fun hiddenTreeStateIsPreservedExactly() {
        seed();val original=snapshot()
        sql.execSQL("UPDATE trees SET neglect_ms=0")
        backup.restore(original)
        assertEquals(original.tables.first{it.name=="trees"},snapshot().tables.first{it.name=="trees"})
    }
    @Test fun corruptionIsRejectedBeforeLiveWrites() {
        seed();val original=snapshot();val bytes=GameBackupCodec.encode(original);bytes[bytes.lastIndex]=(bytes.last()+1).toByte()
        rejects{GameBackupCodec.decode(bytes)};assertEquals(original,snapshot())
    }
    @Test fun unknownFormatAndTruncatedDataAreRejected() {
        val bytes=GameBackupCodec.encode(snapshot())
        rejects{GameBackupCodec.decode(bytes.copyOf(30))}
        bytes[8]='2'.code.toByte();rejects{GameBackupCodec.decode(bytes)}
    }
    @Test fun forgedTableAndColumnNamesNeverExecuteSql() {
        val original=snapshot()
        rejects{backup.restore(original.copy(tables=original.tables.map{if(it.name=="tasks")it.copy(name="tasks; DROP TABLE trees")else it}))}
        rejects{backup.restore(original.copy(tables=original.tables.map{if(it.name=="tasks")it.copy(columns=listOf("unknown"))else it}))}
        assertEquals(original,snapshot())
    }
    @Test fun missingOrDuplicateTablesRejected() {
        val s=snapshot()
        rejects{backup.restore(s.copy(tables=s.tables.drop(1)))}
        rejects{GameBackupCodec.decode(GameBackupCodec.encode(s.copy(tables=s.tables+s.tables.first())))}
    }
    @Test fun unsupportedSchemaRejectedWithoutChanges() {
        val s=snapshot();rejects{backup.restore(s.copy(schema=999))};assertEquals(s,snapshot())
    }
    @Test fun negativeInventoryRollsBackAllTables() {
        seed();val s=snapshot();rejects{backup.restore(modify(s,"inventory","quantity","-10"))};assertEquals(s,snapshot())
    }
    @Test fun invalidTreeStateAndMalformedOccurrencesAreRejected() {
        seed();val s=snapshot();rejects{backup.restore(modify(s,"trees","life_state","UNKNOWN"))};assertEquals(s,snapshot())
        rejects{backup.restore(modify(s,"tasks","verification","UNKNOWN"))};assertEquals(s,snapshot())
    }
    @Test fun invalidPlotReferenceRollsBackAllTables() {
        seed();val s=snapshot();rejects{backup.restore(modify(s,"garden_plots","tree_id","999999"))};assertEquals(s,snapshot())
    }
    @Test fun duplicateIdentityRollsBackAllTables() {
        seed();val s=snapshot()
        rejects{backup.restore(s.copy(tables=s.tables.map{if(it.name=="tasks")it.copy(rows=it.rows+listOf(it.rows.first()))else it}))}
        assertEquals(s,snapshot())
    }
    @Test fun deletedHighIdentityIsNotReusedAfterRestore() {
        sql.execSQL("INSERT INTO tasks(id,title,kind,category,reward,frequency) VALUES(999,'Removed','TODO','Test',8,'ONCE')")
        sql.execSQL("DELETE FROM tasks WHERE id=999")
        val s=snapshot();backup.restore(s)
        game.saveTask(null,TaskConfig("Next","TODO","Test"));assertTrue(game.tasks().single().id>999)
    }
    @Test fun historyAndReceiptTablesSurviveReplacement() {
        seed();val s=snapshot();backup.restore(s)
        assertEquals(s.tables.first{it.name=="wallet"},snapshot().tables.first{it.name=="wallet"})
        val wallet=snapshot().tables.first{it.name=="wallet"}
        game.purchase("fertilizer",1,"purchase")
        assertEquals(wallet,snapshot().tables.first{it.name=="wallet"})
    }
    @Test fun malformedFieldLengthsRejectedWithValidChecksum() {
        val body="99999999:x"
        val bytes=("MAMXANH/1\n"+BackupDigest.sha256(body.encodeToByteArray())+"\n"+body).encodeToByteArray()
        rejects{GameBackupCodec.decode(bytes)}
    }
    @Test fun oversizeInputRejectedBeforeParsing(){rejects{GameBackupCodec.decode(ByteArray(GameBackupCodec.MAX_BYTES+1))}}
    @Test fun upgradesFromEverySupportedSchemaPreserveWealthAndIdentities() {
        for(version in 1..7) {
            SQLiteDatabase.create(null).use{legacy->
                val lc=AndroidGameSqlConnection{legacy}
                GameSchema.create(lc)
                legacy.execSQL("INSERT INTO tasks(id,title,kind,category,reward,frequency) VALUES(42,'Old task','HABIT','Test',8,'EVERY_DAY')")
                legacy.execSQL("INSERT INTO trees(id,species_id,name,growth,planted) VALUES(17,'cherry','Old tree',100,'2026-01-01')")
                legacy.execSQL("UPDATE meta SET v='12345' WHERE k='coins'")
                val phase2=listOf("lifecycle_version","birth_at","zone","sim_at","growth_units","growth_days","growth_day","day_units","water_until","last_water","fertilizer_id","fertilizer_end","last_fertilizer","neglect_ms","life_state","matured_at","death_at","disposition","free_starter")
                val phase1=listOf("description","days","verification","minutes","start_minute","end_minute","reminder","lifecycle","created","updated","archived")
                val phase1Tables=listOf("occurrences","wallet","plans","obligations","sessions","timer_intervals","care_events","purchase_receipts")
                val phase2Tables=listOf("tree_growth_days","tree_dispositions","tree_actions")
                val phase3Tables=listOf("engagement_receipts","template_copies","focus_sessions","focus_intervals","reminder_preferences","reminder_deliveries","achievement_unlocks","rest_days","protection_uses","memory_events","weekly_reflections")
                val phase4Tables=listOf("garden_layout","species_records","collection_rewards","garden_receipts","garden_sections")
                if(version<7)phase4Tables.forEach{legacy.execSQL("DROP TABLE $it")}
                if(version<6)phase3Tables.forEach{legacy.execSQL("DROP TABLE $it")}
                if(version<5){phase2Tables.forEach{legacy.execSQL("DROP TABLE $it")};phase2.forEach{legacy.execSQL("ALTER TABLE trees DROP COLUMN $it")}}
                if(version<4){phase1Tables.forEach{legacy.execSQL("DROP TABLE $it")};phase1.forEach{legacy.execSQL("ALTER TABLE tasks DROP COLUMN $it")}}
                if(version<3)legacy.execSQL("DROP TABLE garden_plots")
                if(version<7)lc.transaction{GameSchema.migrate(lc,version,7)}
                assertEquals("version $version","12345",lc.query("SELECT v FROM meta WHERE k='coins'",1).single().string(0))
                assertEquals(42L,lc.query("SELECT id FROM tasks",1).single().long(0))
                assertEquals(17L,lc.query("SELECT id FROM trees",1).single().long(0))
                assertEquals(100,lc.query("SELECT growth FROM trees",1).single().int(0))
                assertEquals(34,GameBackupEngine(lc).snapshot("test",1234).tables.size)
            }
        }
    }
    @Test fun androidFileExportPreviewSafetyRestoreAndRecovery() {
        val context=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val file=java.io.File(context.cacheDir,"phase-five-test.mamxanh")
        val diagnostics=java.io.File(context.cacheDir,"phase-five-diagnostics.txt")
        val service=BackupService(context,com.mamxanh.shared.data.GameStorage(game),"test")
        seed();val original=snapshot()
        try {
            service.export(android.net.Uri.fromFile(file))
            val preview=service.inspect(android.net.Uri.fromFile(file))
            assertEquals(original.tables,preview.tables)
            game.saveTask(null,TaskConfig("After backup","HABIT","Test"))
            val beforeReplace=snapshot()
            service.restore(preview);assertEquals(original.tables,snapshot().tables);assertTrue(service.hasSafety())
            service.recover();assertEquals(beforeReplace.tables,snapshot().tables)
            service.diagnostics(android.net.Uri.fromFile(diagnostics))
            val output=diagnostics.readText()
            assertFalse(output.contains("Riêng tư"));assertFalse(output.contains("Ghi chú riêng tư"))
            assertFalse(output.contains("neglect"));assertFalse(output.contains("5000"))
            service.clear()
            assertTrue(game.tasks().isEmpty());assertTrue(game.garden().trees.isEmpty())
            service.recover();assertEquals(beforeReplace.tables,snapshot().tables)
        } finally{file.delete();diagnostics.delete();java.io.File(context.noBackupFilesDir,"before-replacement.mamxanh").delete()}
    }
    @Test fun thousandOccurrencesAndHundredCareEventsRemainUsable() {
        val result=game.saveTask(null,TaskConfig("Scale fixture","HABIT","Test"))
        assertTrue(result.success);val id=game.tasks().single().id
        assertTrue(game.plant("succulent","Scale",1))
        c.transaction {
            repeat(1000){day->
                c.execute("INSERT INTO occurrences VALUES(?,date('2020-01-01',?), 'History','Test','HABIT','EVERY_DAY','',-1,-1,8,8,0)",
                    listOf(id,"+$day days"))
            }
            repeat(100){c.execute("INSERT INTO care_events(tree_id,type,at,resource,growth) VALUES(1,'WATER',?,0,0)",listOf(it.toLong()))}
        }
        val started=System.nanoTime();assertTrue(game.completeTask(id).success)
        val elapsed=(System.nanoTime()-started)/1_000_000
        android.util.Log.i("PhaseFiveBenchmark","debug emulator API ${android.os.Build.VERSION.SDK_INT}: completion transaction ${elapsed}ms; 1000 occurrences / 100 care events")
        val exported=snapshot();backup.restore(exported);assertEquals(exported,snapshot())
    }
    @Test fun snapshotDoesNotContainDatabaseFilePath() {
        val text=GameBackupCodec.encode(snapshot()).decodeToString()
        assertFalse(text.contains("/data/user/"));assertFalse(text.contains("mam_xanh.db"))
    }
}
