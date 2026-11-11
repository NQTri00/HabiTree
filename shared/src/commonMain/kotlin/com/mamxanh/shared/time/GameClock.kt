package com.mamxanh.shared.time
interface GameClock {
    fun now(): Long
    fun elapsed(): Long = now()
    fun date(): String
    fun zone(): String
    fun localMinute(date: String, minute: Int): Long
}
expect class SystemGameClock() : GameClock {
    override fun now(): Long
    override fun elapsed(): Long
    override fun date(): String
    override fun zone(): String
    override fun localMinute(date: String, minute: Int): Long
}
