package com.mamxanh.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Calendar
import com.mamxanh.app.domain.model.*
class GardenDatabase(context: Context) : SQLiteOpenHelper(context, "mam_xanh.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,kind TEXT NOT NULL,category TEXT NOT NULL,reward INTEGER NOT NULL,frequency TEXT NOT NULL,due TEXT NOT NULL DEFAULT '',completed TEXT NOT NULL DEFAULT '',streak INTEGER NOT NULL DEFAULT 0,last_done TEXT NOT NULL DEFAULT '')")
        db.execSQL("CREATE TABLE trees(id INTEGER PRIMARY KEY AUTOINCREMENT,species_id TEXT NOT NULL,name TEXT NOT NULL,growth INTEGER NOT NULL DEFAULT 0,planted TEXT NOT NULL)")
        db.execSQL("CREATE TABLE inventory(item TEXT PRIMARY KEY,quantity INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE meta(k TEXT PRIMARY KEY,v TEXT NOT NULL)")
        db.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,task_id INTEGER NOT NULL,title TEXT NOT NULL,coins INTEGER NOT NULL)")
        meta(db, "coins", "60")
        meta(db, "daily_bonus", "")
        seed(db, "cherry", 1); seed(db, "pine", 1)
        task(db, "Đọc hoặc học tập trung 25 phút", "DAILY", "Học tập", 10, "EVERY_DAY", "")
        task(db, "Đi bộ hoặc vận động 20 phút", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "")
        task(db, "Uống đủ nước", "HABIT", "Sức khỏe", 5, "EVERY_DAY", "")
        task(db, "Dọn góc học tập", "DAILY", "Nếp sống", 5, "WEEKDAYS", "")
        task(db, "Viết nhật ký 5 phút", "HABIT", "Phát triển bản thân", 5, "EVERY_DAY", "")
        task(db, "Hoàn thành một việc quan trọng", "TODO", "Công việc", 20, "ONCE", "")
    }
    private fun meta(db: SQLiteDatabase, k: String, v: String) {
        val c = ContentValues().apply { put("k", k); put("v", v) }
        db.insertWithOnConflict("meta", null, c, SQLiteDatabase.CONFLICT_REPLACE)
    }
    private fun getMeta(db: SQLiteDatabase, k: String, fallback: String = "0"): String =
        db.rawQuery("SELECT v FROM meta WHERE k=?", arrayOf(k)).use { if (it.moveToFirst()) it.getString(0) else fallback }
    private fun seed(db: SQLiteDatabase, id: String, quantity: Int) {
        val c = ContentValues().apply { put("item", id); put("quantity", quantity) }
        db.insertWithOnConflict("inventory", null, c, SQLiteDatabase.CONFLICT_REPLACE)
    }
    private fun task(db: SQLiteDatabase, title: String, kind: String, category: String, reward: Int, frequency: String, due: String) {
        val c = ContentValues().apply {
            put("title", title); put("kind", kind); put("category", category)
            put("reward", reward); put("frequency", frequency); put("due", due)
        }
        db.insert("tasks", null, c)
    }
    fun tasks(): List<Task> = readableDatabase.rawQuery("SELECT id,title,kind,category,reward,frequency,due,completed,streak,last_done FROM tasks ORDER BY CASE kind WHEN 'HABIT' THEN 0 WHEN 'DAILY' THEN 1 ELSE 2 END,id DESC", null).use { c ->
        buildList { while (c.moveToNext()) add(Task(c.getLong(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4),c.getString(5),c.getString(6),c.getString(7),c.getInt(8),c.getString(9))) }
    }
    fun garden(): Garden {
        val trees = readableDatabase.rawQuery("SELECT id,species_id,name,growth,planted,COALESCE((SELECT v FROM meta WHERE k='decoration_' || trees.id),'NONE'),CASE WHEN COALESCE((SELECT v FROM meta WHERE k='watered_' || trees.id),'')=? THEN 1 ELSE 0 END FROM trees ORDER BY id", arrayOf(today())).use { c ->
            buildList { while (c.moveToNext()) add(Tree(c.getLong(0),c.getString(1),c.getString(2),c.getInt(3),c.getString(4),c.getString(5),c.getInt(6)==1)) }
        }
        val inventory = readableDatabase.rawQuery("SELECT item,quantity FROM inventory", null).use { c ->
            buildMap { while (c.moveToNext()) put(c.getString(0),c.getInt(1)) }
        }
        return Garden(trees, inventory)
    }
    fun statistics(): Statistics {
        val db = readableDatabase
        val coins = getMeta(db, "coins").toIntOrNull() ?: 0
        fun count(sql: String, args: Array<String>? = null) = db.rawQuery(sql,args).use { if (it.moveToFirst()) it.getInt(0) else 0 }
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek); set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0)
        }
        val weekStart = key(cal.time)
        val dates = mutableSetOf<String>()
        db.rawQuery("SELECT DISTINCT date FROM logs ORDER BY date DESC",null).use { c -> while(c.moveToNext()) dates.add(c.getString(0)) }
        var streak = 0
        if (today() !in dates) cal.add(Calendar.DAY_OF_YEAR,-1)
        while (key(cal.time) in dates) { streak++; cal.add(Calendar.DAY_OF_YEAR,-1) }
        return Statistics(coins,count("SELECT COUNT(*) FROM logs"),count("SELECT COUNT(*) FROM logs WHERE date=?", arrayOf(today())),
            count("SELECT COUNT(*) FROM logs WHERE date>=?",arrayOf(weekStart)),streak,count("SELECT COUNT(*) FROM trees"))
    }
    fun addTask(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) {
        task(writableDatabase,title.trim(),kind,category,reward.coerceIn(1,100),frequency,due.trim())
    }
    fun deleteTask(id: Long) { writableDatabase.delete("tasks","id=?",arrayOf(id.toString())) }
    fun complete(id: Long): Boolean {
        val db = writableDatabase
        var result = false
        db.beginTransaction()
        try {
            db.rawQuery("SELECT title,kind,reward,completed,last_done,streak FROM tasks WHERE id=?",arrayOf(id.toString())).use { c ->
                if (c.moveToFirst()) {
                    val title=c.getString(0); val kind=c.getString(1); val reward=c.getInt(2)
                    val completed=c.getString(3); val last=c.getString(4); val oldStreak=c.getInt(5)
                    if (completed != today() && !(kind=="TODO" && completed.isNotBlank())) {
                        val yesterday=Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR,-1) }
                        val nextStreak=if(last==key(yesterday.time)) oldStreak+1 else 1
                        val values=ContentValues().apply {
                            put("completed",today()); put("last_done",today())
                            put("streak",if(kind=="TODO") oldStreak else nextStreak)
                        }
                        db.update("tasks",values,"id=?",arrayOf(id.toString()))
                        val log=ContentValues().apply { put("date",today()); put("task_id",id); put("title",title); put("coins",reward) }
                        db.insert("logs",null,log)
                        meta(db,"coins",((getMeta(db,"coins").toIntOrNull()?:0)+reward).toString())
                        db.execSQL("UPDATE trees SET growth=MIN(120,growth+?)",arrayOf(reward*2))
                        result=true
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        return result
    }
    fun dailyBonus(): Boolean {
        val db=writableDatabase
        if(getMeta(db,"daily_bonus","")==today()) return false
        val daily=tasks().filter { it.kind=="DAILY" && scheduled(it) }
        if(daily.isEmpty() || daily.any { !it.doneToday }) return false
        meta(db,"coins",((getMeta(db,"coins").toIntOrNull()?:0)+20).toString())
        meta(db,"daily_bonus",today())
        return true
    }
    private fun scheduled(t: Task): Boolean {
        val day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when(t.frequency) {
            "WEEKDAYS" -> day in Calendar.MONDAY..Calendar.FRIDAY
            "WEEKENDS" -> day==Calendar.SATURDAY || day==Calendar.SUNDAY
            else -> true
        }
    }
    fun buySeed(id: String): Boolean {
        val item=catalog.firstOrNull { it.id==id } ?: return false
        val db=writableDatabase; db.beginTransaction()
        try {
            val coins=getMeta(db,"coins").toIntOrNull()?:0
            if(coins<item.price) return false
            meta(db,"coins",(coins-item.price).toString())
            val count=db.rawQuery("SELECT quantity FROM inventory WHERE item=?",arrayOf(id)).use { if(it.moveToFirst()) it.getInt(0) else 0 }
            seed(db,id,count+1); db.setTransactionSuccessful(); return true
        } finally { db.endTransaction() }
    }
    fun plant(id: String,name: String): Boolean {
        val db=writableDatabase; db.beginTransaction()
        try {
            val count=db.rawQuery("SELECT quantity FROM inventory WHERE item=?",arrayOf(id)).use { if(it.moveToFirst()) it.getInt(0) else 0 }
            if(count<=0) return false
            val item=catalog.first { it.id==id }
            val c=ContentValues().apply { put("species_id",id); put("name",name.ifBlank { item.name }); put("growth",0); put("planted",today()) }
            db.insert("trees",null,c); seed(db,id,count-1); db.setTransactionSuccessful(); return true
        } finally { db.endTransaction() }
    }
    fun buyFertilizer(): Boolean {
        val db=writableDatabase; db.beginTransaction()
        try {
            val coins=getMeta(db,"coins").toIntOrNull()?:0
            if(coins<25) return false
            meta(db,"coins",(coins-25).toString())
            val count=db.rawQuery("SELECT quantity FROM inventory WHERE item='fertilizer'",null).use { if(it.moveToFirst()) it.getInt(0) else 0 }
            seed(db,"fertilizer",count+1); db.setTransactionSuccessful(); return true
        } finally { db.endTransaction() }
    }
    fun fertilize(treeId: Long): Boolean {
        val db=writableDatabase; db.beginTransaction()
        try {
            val count=db.rawQuery("SELECT quantity FROM inventory WHERE item='fertilizer'",null).use { if(it.moveToFirst()) it.getInt(0) else 0 }
            if(count<=0) return false
            db.execSQL("UPDATE trees SET growth=MIN(120,growth+25) WHERE id=?",arrayOf(treeId))
            seed(db,"fertilizer",count-1); db.setTransactionSuccessful(); return true
        } finally { db.endTransaction() }
    }
    fun water(treeId: Long): Boolean {
        val db=writableDatabase
        db.beginTransaction()
        try {
            val exists=db.rawQuery("SELECT 1 FROM trees WHERE id=?",arrayOf(treeId.toString())).use { it.moveToFirst() }
            if(!exists || getMeta(db,"watered_$treeId","") == today()) return false
            db.execSQL("UPDATE trees SET growth=MIN(120,growth+8) WHERE id=?",arrayOf(treeId))
            meta(db,"watered_$treeId",today())
            db.setTransactionSuccessful()
            return true
        } finally { db.endTransaction() }
    }
    fun decorate(treeId: Long, decoration: String) {
        require(decoration in setOf("NONE","FLOWERS","LANTERNS","RIBBON"))
        val db=writableDatabase
        val exists=db.rawQuery("SELECT 1 FROM trees WHERE id=?",arrayOf(treeId.toString())).use { it.moveToFirst() }
        if(exists) meta(db,"decoration_$treeId",decoration)
    }
}
