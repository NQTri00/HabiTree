package com.mamxanh.shared.data

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.mamxanh.shared.data.sql.GameSchema
import com.mamxanh.shared.data.sql.GameSqlConnection
import com.mamxanh.shared.data.sql.GameSqlRow
import com.mamxanh.shared.data.sql.SqlGameDatabase

/** Native SQLite persistence. Open/read/write on a worker, and close when its owner ends. */
class IosGameDatabase private constructor(private val driver: NativeSqliteDriver) :
    GameDatabase by SqlGameDatabase(NativeGameSqlConnection(driver)), AutoCloseable {
    constructor(name: String = "mam_xanh.db") : this(NativeSqliteDriver(NativeGameSchema, name))
    override fun close() = driver.close()
}

private object NativeGameSchema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long = GameSchema.VERSION.toLong()

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
        GameSchema.create(NativeGameSqlConnection(driver))
        return QueryResult.Value(Unit)
    }

    override fun migrate(
        driver: SqlDriver, oldVersion: Long, newVersion: Long, vararg callbacks: AfterVersion
    ): QueryResult.Value<Unit> {
        GameSchema.migrate(NativeGameSqlConnection(driver), oldVersion.toInt(), newVersion.toInt())
        callbacks.filter { it.afterVersion in oldVersion until newVersion }
            .sortedBy { it.afterVersion }.forEach { it.block(driver) }
        return QueryResult.Value(Unit)
    }
}

private class NativeGameSqlConnection(private val driver: SqlDriver) : GameSqlConnection {
    private val transacter = object : TransacterImpl(driver) {}

    override fun query(sql: String, columns: Int, args: List<String>): List<GameSqlRow> =
        driver.executeQuery(
            identifier = null,
            sql = sql,
            mapper = { cursor ->
                QueryResult.Value(buildList {
                    while (cursor.next().value) {
                        add(GameSqlRow(List(columns) { cursor.getString(it) }))
                    }
                })
            },
            parameters = args.size,
            binders = { args.forEachIndexed { index, value -> bindString(index, value) } }
        ).value

    override fun execute(sql: String, args: List<Any?>) {
        driver.execute(null, sql, args.size) {
            args.forEachIndexed { index, value ->
                when (value) {
                    null -> bindString(index, null)
                    is String -> bindString(index, value)
                    is Int -> bindLong(index, value.toLong())
                    is Long -> bindLong(index, value)
                    else -> error("Unsupported SQLite parameter: $value")
                }
            }
        }.value
    }

    override fun <T> transaction(block: () -> T): T = transacter.transactionWithResult { block() }
}
