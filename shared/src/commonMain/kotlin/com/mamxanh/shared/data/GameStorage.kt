package com.mamxanh.shared.data

/** Shared repository boundary; platform transactions remain owned by GameDatabase. */
class GameStorage(private val database: GameDatabase) {
    fun tasks() = database.tasks()
    fun garden() = database.garden()
    fun statistics() = database.statistics()
    fun addTask(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) =
        database.addTask(title, kind, category, reward, frequency, due)
    fun deleteTask(id: Long) = database.deleteTask(id)
    fun complete(id: Long) = database.complete(id)
    fun dailyBonus() = database.dailyBonus()
    fun buySeed(id: String) = database.buySeed(id)
    fun plant(id: String, name: String, plotId: Int? = null) = database.plant(id, name, plotId)
    fun unlockPlot(plotId: Int) = database.unlockPlot(plotId)
    fun buyFertilizer() = database.buyFertilizer()
    fun fertilize(treeId: Long) = database.fertilize(treeId)
    fun water(treeId: Long) = database.water(treeId)
    fun waterMl() = database.waterMl()
    fun refillWater(amountMl: Int = 100) = database.refillWater(amountMl)
    fun decorate(treeId: Long, decoration: String) = database.decorate(treeId, decoration)
}
