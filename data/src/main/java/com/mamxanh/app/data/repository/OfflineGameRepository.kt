package com.mamxanh.app.data.repository
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.local.model.*
class OfflineGameRepository(private val db:GardenDatabase) {
 fun tasks()=db.tasks(); override fun garden()=db.garden(); override fun statistics()=db.statistics()
 override fun addTask(title:String,kind:String,category:String,reward:Int,frequency:String,due:String)=db.addTask(title,kind,category,reward,frequency,due)
 override fun deleteTask(id:Long)=db.deleteTask(id); override fun complete(id:Long)=db.complete(id); override fun dailyBonus()=db.dailyBonus()
 override fun buySeed(id:String)=db.buySeed(id); override fun plant(id:String,name:String)=db.plant(id,name)
 override fun buyFertilizer()=db.buyFertilizer(); override fun fertilize(treeId:Long)=db.fertilize(treeId)
 override fun water(treeId:Long)=db.water(treeId); override fun decorate(treeId:Long,decoration:String)=db.decorate(treeId,decoration)
}