package com.mamxanh.shared.data.sql

/** Synchronous SQLite access; implementations confine transactions to the calling thread. */
interface GameSqlConnection {
    fun query(sql: String, columns: Int, args: List<String> = emptyList()): List<GameSqlRow>
    fun execute(sql: String, args: List<Any?> = emptyList())
    fun <T> transaction(block: () -> T): T
}

class GameSqlRow(private val values: List<String?>) {
    fun string(index: Int): String = requireNotNull(values[index])
    fun long(index: Int): Long = string(index).toLong()
    fun int(index: Int): Int = string(index).toInt()
}
