package com.mamxanh.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.data.sql.GameSchema
import com.mamxanh.shared.data.sql.GameSqlConnection
import com.mamxanh.shared.data.sql.GameSqlRow
import com.mamxanh.shared.data.sql.SqlGameDatabase

/** Keeps the existing file/helper; game rules and schema are shared with iOS. */
class GardenDatabase private constructor(
    private val lease: GardenHelperLease,
) : GameDatabase by SqlGameDatabase(AndroidGameSqlConnection { lease.helper.writableDatabase }),
    AutoCloseable {
    constructor(context: Context) : this(GardenHelperPool.acquire(context.applicationContext))

    override fun close() = lease.close()
}

/** Receivers and UI share one connection pool; closing one owner cannot close another's DB. */
private class GardenHelperLease(
    val helper: GardenSqliteHelper,
    private val release: () -> Unit,
) : AutoCloseable {
    private val closed =
        java.util.concurrent.atomic
            .AtomicBoolean(false)

    override fun close() {
        if (closed.compareAndSet(false, true)) release()
    }
}

private object GardenHelperPool {
    private class Entry(
        val helper: GardenSqliteHelper,
        var owners: Int = 0,
    )

    private val entries = mutableMapOf<String, Entry>()

    @Synchronized fun acquire(context: Context): GardenHelperLease {
        val path = context.getDatabasePath("mam_xanh.db").absolutePath
        val entry = entries.getOrPut(path) { Entry(GardenSqliteHelper(context)) }
        entry.owners++
        return GardenHelperLease(entry.helper) { release(path, entry) }
    }

    @Synchronized private fun release(
        path: String,
        entry: Entry,
    ) {
        if (--entry.owners == 0) {
            entries.remove(path)
            entry.helper.close()
        }
    }
}

private class GardenSqliteHelper(
    context: Context,
) : SQLiteOpenHelper(context, "mam_xanh.db", null, GameSchema.VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        GameSchema.create(AndroidGameSqlConnection { db })
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int,
    ) {
        GameSchema.migrate(AndroidGameSqlConnection { db }, oldVersion, newVersion)
    }
}

internal class AndroidGameSqlConnection(
    private val database: () -> SQLiteDatabase,
) : GameSqlConnection {
    override fun query(
        sql: String,
        columns: Int,
        args: List<String>,
    ): List<GameSqlRow> =
        database().rawQuery(sql, args.toTypedArray()).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        GameSqlRow(
                            List(columns) { index ->
                                if (cursor.isNull(index)) null else cursor.getString(index)
                            },
                        ),
                    )
                }
            }
        }

    override fun execute(
        sql: String,
        args: List<Any?>,
    ) {
        if (args.isEmpty()) {
            database().execSQL(sql)
        } else {
            database().execSQL(sql, args.toTypedArray())
        }
    }

    override fun <T> transaction(block: () -> T): T {
        val db = database()
        db.beginTransaction()
        try {
            val result = block()
            db.setTransactionSuccessful()
            return result
        } finally {
            db.endTransaction()
        }
    }
}
