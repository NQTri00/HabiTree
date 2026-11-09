package com.mamxanh.shared.data

import com.mamxanh.shared.model.*

/** Blocking platform persistence. Call from a worker dispatcher, never from composition. */
interface GameDatabase {
    fun tasks(): List<Task>
    fun garden(): Garden
    fun statistics(): Statistics
    fun addTask(title: String, kind: String, category: String, reward: Int, frequency: String, due: String)
    fun deleteTask(id: Long)
    fun complete(id: Long): Boolean
    fun dailyBonus(): Boolean
    fun isDailyBonusClaimed(): Boolean
    fun canClaimDailyBonus(): Boolean
    fun buySeed(id: String): Boolean
    fun plant(id: String, name: String, plotId: Int? = null): Boolean
    fun unlockPlot(plotId: Int): Boolean
    fun buyFertilizer(): Boolean
    fun fertilize(treeId: Long): Boolean
    fun water(treeId: Long): TreeWateringResult
    fun waterMl(): Int
    fun refillWater(amountMl: Int): Int
    fun decorate(treeId: Long, decoration: String)
}
