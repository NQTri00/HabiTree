package com.mamxanh.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.mamxanh.app.data.local.AndroidGameSqlConnection
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import java.io.File

/** Blocking Android file adapter. UI invokes every method on Dispatchers.IO. */
class BackupService(private val context:Context,private val storage:GameStorage,private val version:String) {
    private val safety get()=File(context.noBackupFilesDir,"before-replacement.mamxanh")
    fun sizeBytes()=context.getDatabasePath("mam_xanh.db").let{it.length()+File(it.path+"-wal").length()}
    fun hasSafety()=safety.isFile
    private fun read(uri:Uri):ByteArray {
        val input=requireNotNull(context.contentResolver.openInputStream(uri))
        return input.use {
            val output=java.io.ByteArrayOutputStream()
            val buffer=ByteArray(8192);var total=0
            while(true){val n=it.read(buffer);if(n<0)break;total+=n;require(total<=GameBackupCodec.MAX_BYTES){"Backup too large"};output.write(buffer,0,n)}
            output.toByteArray()
        }
    }
    fun inspect(uri:Uri)=GameBackupCodec.decode(read(uri)).also(::validate)
    private fun validate(backup:GameBackup) {
        SQLiteDatabase.create(null).use{sql->
            val c=AndroidGameSqlConnection{sql}
            sql.execSQL("PRAGMA foreign_keys=ON")
            GameSchema.create(c)
            GameBackupEngine(c).restore(backup)
        }
    }
    fun export(uri:Uri) {
        val bytes=GameBackupCodec.encode(storage.backup(version,System.currentTimeMillis()))
        requireNotNull(context.contentResolver.openOutputStream(uri,"wt")).use{it.write(bytes)}
    }
    private fun safetyCopy() {
        val bytes=GameBackupCodec.encode(storage.backup(version,System.currentTimeMillis()))
        val atomic=android.util.AtomicFile(safety)
        val output=atomic.startWrite()
        try{output.write(bytes);atomic.finishWrite(output)}catch(e:Exception){atomic.failWrite(output);throw e}
    }
    private fun afterReplacement() {
        // OS handles are derived state, never imported from a backup. Rebuild even if UI values compare equal.
        try {
            context.getSystemService(android.app.NotificationManager::class.java)?.cancelAll()
            TaskReminders.schedule(context,emptyList())
        } catch(_:Exception) {
            // The transaction has already committed: do not misreport a reminder failure as data rollback.
            android.util.Log.w("BackupService","Reminder refresh needs lifecycle retry")
        }
    }
    fun restore(backup:GameBackup) {validate(backup);safetyCopy();storage.restore(backup);afterReplacement()}
    fun recover() {
        require(safety.length() in 80..GameBackupCodec.MAX_BYTES.toLong())
        val backup=GameBackupCodec.decode(safety.readBytes())
        validate(backup)
        // Preserve the recovery point until a later explicit export or replacement.
        storage.restore(backup);afterReplacement()
    }
    fun clear() {
        val fresh=SQLiteDatabase.create(null).use{sql->
            val c=AndroidGameSqlConnection{sql};GameSchema.create(c)
            GameBackupEngine(c).snapshot(version,System.currentTimeMillis())
        }
        restore(fresh)
    }
    fun diagnostics(uri:Uri) {
        val snapshot=storage.backup(version,System.currentTimeMillis())
        val text=buildString {
            appendLine("MamXanh local diagnostics");appendLine("App: $version");appendLine("Schema: ${GameSchema.VERSION}")
            appendLine("Android API: ${android.os.Build.VERSION.SDK_INT}")
            appendLine("Database bytes: ${sizeBytes()}")
            snapshot.tables.filter{it.name!="meta" && it.name!="sqlite_sequence"}.forEach{appendLine("${it.name}: ${it.rows.size} rows")}
            appendLine("No task titles, notes, balances, hidden tree state, identifiers or device serials included.")
        }
        requireNotNull(context.contentResolver.openOutputStream(uri,"wt")).bufferedWriter().use{it.write(text)}
    }
}
