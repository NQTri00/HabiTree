package com.mamxanh.app.domain.repository
import com.mamxanh.app.domain.model.*
interface GameRepository {
 fun tasks():List<Task>; fun garden():Garden; fun statistics():Statistics
 fun addTask(title:String,kind:String,category:String,reward:Int,frequency:String,due:String)
 fun deleteTask(id:Long); fun complete(id:Long):Boolean; fun dailyBonus():Boolean
 fun buySeed(id:String):Boolean; fun plant(id:String,name:String):Boolean
 fun buyFertilizer():Boolean; fun fertilize(treeId:Long):Boolean
 fun water(treeId:Long):Boolean; fun decorate(treeId:Long,decoration:String)
}