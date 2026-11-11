package com.mamxanh.shared.data.sql

import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.currentDateKey
import com.mamxanh.shared.time.firstDayOfWeekIndex

/** Shared game rules and SQL transactions, using either platform's SQLite connection. */
class SqlGameDatabase(private val connection: GameSqlConnection) : GameDatabase {
    private fun rows(sql: String, columns: Int, vararg args: Any): List<GameSqlRow> =
        connection.query(sql, columns, args.map { it.toString() })
    private fun execute(sql: String, vararg args: Any?) = connection.execute(sql, args.toList())
    private fun count(sql: String, vararg args: Any): Int = rows(sql, 1, *args).firstOrNull()?.int(0) ?: 0
    private fun meta(key: String, fallback: String = "0"): String =
        rows("SELECT v FROM meta WHERE k=?", 1, key).firstOrNull()?.string(0) ?: fallback
    private fun putMeta(key: String, value: Any) =
        execute("INSERT OR REPLACE INTO meta(k,v) VALUES(?,?)", key, value.toString())
    private fun quantity(id: String): Int = count("SELECT quantity FROM inventory WHERE item=?", id)
    private fun seed(id: String, quantity: Int) =
        execute("INSERT OR REPLACE INTO inventory(item,quantity) VALUES(?,?)", id, quantity)
    private fun offset(date: String, days: Int): String =
        rows("SELECT date(?,?)", 1, date, "$days days").first().string(0)
    private fun weekday(date: String): Int = count("SELECT CAST(strftime('%w',?) AS INTEGER)", date)

    private val phase = PhaseOneEngine(connection)
    override fun phaseSnapshot() = phase.snapshot()
    override fun saveTask(id: Long?, config: TaskConfig) = phase.save(id,config)
    override fun taskLifecycle(id: Long,state: String) = phase.lifecycle(id,state)
    override fun taskTimer(id: Long,action: String) = phase.timer(id,action)
    override fun completeTask(id: Long) = phase.complete(id)
    override fun purchase(id: String,quantity: Int,key: String) = phase.purchase(id,quantity,key)
    override fun tasks() = phase.tasks()

    override fun garden(): Garden {
        val trees = rows("SELECT id,species_id,name,growth,planted,COALESCE((SELECT v FROM meta WHERE k='decoration_' || trees.id),'NONE'),CASE WHEN COALESCE((SELECT v FROM meta WHERE k='watered_' || trees.id),'')=? THEN 1 ELSE 0 END FROM trees ORDER BY id",
            7, currentDateKey()).map {
            Tree(it.long(0), it.string(1), it.string(2), it.int(3), it.string(4), it.string(5), it.int(6) == 1)
        }
        val inventory = rows("SELECT item,quantity FROM inventory", 2).associate { it.string(0) to it.int(1) }
        val plots = rows("SELECT id,unlocked,COALESCE(tree_id,0) FROM garden_plots ORDER BY id", 3).map {
            GardenPlot(it.int(0), it.int(1) == 1, it.long(2).takeIf { id -> id != 0L })
        }
        return Garden(trees, inventory, plots, meta("coins").toIntOrNull() ?: 0)
    }

    override fun statistics(): Statistics {
        val today = currentDateKey()
        val weekStart = offset(today, -((weekday(today) - firstDayOfWeekIndex() + 7) % 7))
        val dates = rows("SELECT DISTINCT date FROM logs ORDER BY date DESC", 1).map { it.string(0) }.toSet()
        // A consecutive-day streak is independent of the locale's weekly reporting boundary.
        var streakDate = if (today in dates) today else offset(today, -1)
        var streak = 0
        while (streakDate in dates) {
            streak++
            streakDate = offset(streakDate, -1)
        }
        val recent = (6 downTo 0).map { count("SELECT COUNT(*) FROM logs WHERE date=?", offset(today, -it)) }
        return Statistics(
            coins = meta("coins").toIntOrNull() ?: 0,
            total = count("SELECT COUNT(*) FROM logs"),
            today = count("SELECT COUNT(*) FROM logs WHERE date=?", today),
            week = count("SELECT COUNT(*) FROM logs WHERE date>=?", weekStart),
            streak = phase.snapshot().streak,
            treeCount = count("SELECT COUNT(*) FROM trees"),
            recentDailyCompletions = recent
        )
    }

    override fun addTask(title: String,kind: String,category: String,reward: Int,frequency: String,due: String) {
        phase.save(null,TaskConfig(title,kind,category,frequency=frequency,due=due))
    }
    override fun deleteTask(id: Long) { phase.lifecycle(id,"ARCHIVED") }
    override fun complete(id: Long) = phase.complete(id).success
    override fun isDailyBonusClaimed() = phase.claimed()
    override fun canClaimDailyBonus() = phase.canClear()
    override fun dailyBonus() = phase.clear().success
    override fun buySeed(id: String) = phase.purchase(id,1,"buy:$id:${com.mamxanh.shared.time.SystemGameClock().now()}").success

    override fun plant(id: String, name: String, plotId: Int?): Boolean = connection.transaction {
        val count = quantity(id)
        val item = catalog.firstOrNull { it.id == id } ?: return@transaction false
        if (count <= 0) return@transaction false
        val plot = if (plotId == null) {
            rows("SELECT id FROM garden_plots WHERE unlocked=1 AND tree_id IS NULL ORDER BY id LIMIT 1", 1)
        } else {
            rows("SELECT id FROM garden_plots WHERE id=? AND unlocked=1 AND tree_id IS NULL", 1, plotId)
        }.firstOrNull()?.int(0) ?: return@transaction false
        execute("INSERT INTO trees(species_id,name,growth,planted) VALUES(?,?,0,?)",
            id, name.trim().ifBlank { item.name }, currentDateKey())
        val treeId = rows("SELECT last_insert_rowid()", 1).first().long(0)
        execute("UPDATE garden_plots SET tree_id=? WHERE id=?", treeId, plot)
        seed(id, count - 1)
        true
    }

    override fun unlockPlot(plotId: Int): Boolean = connection.transaction {
        if (count("SELECT COUNT(*) FROM garden_plots WHERE id=? AND unlocked=0", plotId) != 1) return@transaction false
        val coins = meta("coins").toIntOrNull() ?: 0
        val price = Balance.plot(count("SELECT COUNT(*) FROM garden_plots WHERE unlocked=1"))
        if (coins < price || !phase.spend("plot:$plotId","PLOT",plotId.toString(),price)) return@transaction false
        execute("UPDATE garden_plots SET unlocked=1 WHERE id=?", plotId)
        val next = count("SELECT COALESCE(MAX(id),0)+1 FROM garden_plots")
        execute("INSERT INTO garden_plots(id,unlocked) VALUES(?,0)", next)
        true
    }

    override fun buyFertilizer() = phase.purchase("fertilizer",1,"fertilizer:${com.mamxanh.shared.time.SystemGameClock().now()}").success

    override fun fertilize(treeId: Long): Boolean = connection.transaction {
        val count = quantity("fertilizer")
        val growth = rows("SELECT growth FROM trees WHERE id=?",1,treeId).firstOrNull()?.int(0)
            ?: return@transaction false
        if (count <= 0 || growth >= 120) return@transaction false
        val change = minOf(25,120-growth)
        execute("UPDATE trees SET growth=growth+? WHERE id=?",change,treeId)
        seed("fertilizer",count-1)
        phase.care(treeId,"FERTILIZE",-1,change)
        true
    }

    override fun water(treeId: Long): TreeWateringResult = connection.transaction {
        if (count("SELECT 1 FROM trees WHERE id=?", treeId) == 0) return@transaction TreeWateringResult.TREE_NOT_FOUND
        val today = currentDateKey()
        if (meta("watered_$treeId", "") == today) return@transaction TreeWateringResult.ALREADY_WATERED
        val water = waterMl()
        if (water < 50) return@transaction TreeWateringResult.NOT_ENOUGH_WATER
        val before = count("SELECT growth FROM trees WHERE id=?",treeId)
        execute("UPDATE trees SET growth=MIN(120,growth+8) WHERE id=?", treeId)
        phase.care(treeId,"WATER",-50,minOf(8,(120-before).coerceAtLeast(0)))
        putMeta("watered_$treeId", today)
        putMeta("water_ml", water - 50)
        TreeWateringResult.WATERED
    }

    override fun waterMl(): Int = meta("water_ml", "542").toIntOrNull() ?: 542

    override fun refillWater(amountMl: Int): Int = connection.transaction {
        val refilled = (waterMl() + amountMl.coerceAtLeast(0)).coerceAtMost(1000)
        putMeta("water_ml", refilled)
        refilled
    }

    override fun decorate(treeId: Long, decoration: String) {
        require(decoration in setOf("NONE", "FLOWERS", "LANTERNS", "RIBBON"))
        connection.transaction {
            if (count("SELECT 1 FROM trees WHERE id=?", treeId) != 0) {
                putMeta("decoration_$treeId", decoration)
                phase.care(treeId,"DECORATE:$decoration",0,0)
            }
        }
    }
}
