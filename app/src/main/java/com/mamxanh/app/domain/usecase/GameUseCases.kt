package com.mamxanh.app.domain.usecase
import com.mamxanh.app.domain.repository.GameRepository
class GameUseCases(private val repo:GameRepository) {
 fun loadTasks()=repo.tasks(); fun loadGarden()=repo.garden(); fun loadStatistics()=repo.statistics()
 fun addTask(title:String,kind:String,category:String,reward:Int,frequency:String,due:String)=repo.addTask(title,kind,category,reward,frequency,due)
 fun deleteTask(id:Long)=repo.deleteTask(id); fun completeTask(id:Long)=repo.complete(id); fun claimDailyBonus()=repo.dailyBonus()
 fun buySeed(id:String)=repo.buySeed(id); fun plantSeed(id:String,name:String)=repo.plant(id,name)
 fun buyFertilizer()=repo.buyFertilizer(); fun fertilizeTree(id:Long)=repo.fertilize(id)
 fun waterTree(id:Long)=repo.water(id); fun decorateTree(id:Long,decoration:String)=repo.decorate(id,decoration)
}