package com.mamxanh.app.data.repository
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.local.model.*
class GameStorage(private val db:GardenDatabase) {
 fun tasks()=db.tasks(); fun garden()=db.garden(); fun statistics()=db.statistics()
 fun addTask(title:String,kind:String,category:String,reward:Int,frequency:String,due:String)=db.addTask(title,kind,category,reward,frequency,due)
 fun deleteTask(id:Long)=db.deleteTask(id); fun complete(id:Long)=db.complete(id); fun dailyBonus()=db.dailyBonus()
 fun buySeed(id:String)=db.buySeed(id); fun plant(id:String,name:String)=db.plant(id,name)
 fun buyFertilizer()=db.buyFertilizer(); fun fertilize(treeId:Long)=db.fertilize(treeId)
 fun water(treeId:Long)=db.water(treeId); fun decorate(treeId:Long,decoration:String)=db.decorate(treeId,decoration)
}