package com.mamxanh.shared.data

/** Shared repository boundary; platform transactions remain owned by GameDatabase. */
class GameStorage(private val database: GameDatabase) {
    val changes = kotlinx.coroutines.flow.MutableStateFlow(0L)
    private fun changed() { while (true) { val old=changes.value; if(changes.compareAndSet(old,old+1))return } }
    fun waterAllQuote() = database.waterAllQuote()
    fun waterAll(key: String) = database.waterAll(key).also { changed() }
    fun applyFertilizer(id: Long,tier: String) = database.applyFertilizer(id,tier).also { changed() }
    fun equipCan(id: String) = database.equipCan(id).also { changed() }
    fun treeSaleQuote(id: Long) = database.treeSaleQuote(id)
    fun disposeTree(id: Long,sell: Boolean,key: String) = database.disposeTree(id,sell,key).also { changed() }
    fun treeDispositions() = database.treeDispositions()
    fun phaseSnapshot() = database.phaseSnapshot()
    fun saveTask(id: Long?, config: com.mamxanh.shared.model.TaskConfig) = database.saveTask(id,config).also { changed() }
    fun taskLifecycle(id: Long,state: String) = database.taskLifecycle(id,state).also { changed() }
    fun taskTimer(id: Long,action: String) = database.taskTimer(id,action)
    fun completeTask(id: Long) = database.completeTask(id).also { changed() }
    fun purchase(id: String,quantity: Int,key: String) = database.purchase(id,quantity,key).also { changed() }
    fun tasks() = database.tasks()
    fun garden() = database.garden()
    fun statistics() = database.statistics()
    fun addTask(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) =
        database.addTask(title, kind, category, reward, frequency, due)
    fun deleteTask(id: Long) = database.deleteTask(id)
    fun complete(id: Long) = database.complete(id)
    fun dailyBonus() = database.dailyBonus().also { changed() }
    fun isDailyBonusClaimed() = database.isDailyBonusClaimed()
    fun canClaimDailyBonus() = database.canClaimDailyBonus()
    fun buySeed(id: String) = database.buySeed(id).also { changed() }
    fun plant(id: String, name: String, plotId: Int? = null) = database.plant(id, name, plotId).also { changed() }
    fun unlockPlot(plotId: Int) = database.unlockPlot(plotId).also { changed() }
    fun buyFertilizer() = database.buyFertilizer().also { changed() }
    fun fertilize(treeId: Long) = database.fertilize(treeId).also { changed() }
    fun water(treeId: Long) = database.water(treeId).also { changed() }
    fun decorate(treeId: Long, decoration: String) = database.decorate(treeId, decoration).also { changed() }
}
