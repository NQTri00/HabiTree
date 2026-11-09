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

    override fun tasks(): List<Task> =
        rows("SELECT id,title,kind,category,reward,frequency,due,completed,streak,last_done FROM tasks ORDER BY CASE kind WHEN 'HABIT' THEN 0 WHEN 'DAILY' THEN 1 ELSE 2 END,id DESC", 10).map {
            Task(it.long(0), it.string(1), it.string(2), it.string(3), it.int(4), it.string(5),
                it.string(6), it.string(7), it.int(8), it.string(9))
        }

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
            streak = streak,
            treeCount = count("SELECT COUNT(*) FROM trees"),
            recentDailyCompletions = recent
        )
    }

    override fun addTask(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) {
        execute("INSERT INTO tasks(title,kind,category,reward,frequency,due) VALUES(?,?,?,?,?,?)",
            title.trim(), kind, category, reward.coerceIn(1, 100), frequency, due.trim())
    }

    override fun deleteTask(id: Long) { execute("DELETE FROM tasks WHERE id=?", id) }

    override fun complete(id: Long): Boolean = connection.transaction {
        val row = rows("SELECT title,kind,reward,completed,last_done,streak FROM tasks WHERE id=?", 6, id)
            .firstOrNull() ?: return@transaction false
        val today = currentDateKey()
        val title = row.string(0)
        val kind = row.string(1)
        val reward = row.int(2)
        val completed = row.string(3)
        if (completed == today || (kind == "TODO" && completed.isNotBlank())) return@transaction false
        val oldStreak = row.int(5)
        val nextStreak = if (row.string(4) == offset(today, -1)) oldStreak + 1 else 1
        execute("UPDATE tasks SET completed=?,last_done=?,streak=? WHERE id=?",
            today, today, if (kind == "TODO") oldStreak else nextStreak, id)
        execute("INSERT INTO logs(date,task_id,title,coins) VALUES(?,?,?,?)", today, id, title, reward)
        putMeta("coins", (meta("coins").toIntOrNull() ?: 0) + reward)
        execute("UPDATE trees SET growth=MIN(120,growth+?)", reward * 2)
        true
    }

    override fun dailyBonus(): Boolean = connection.transaction {
        val today = currentDateKey()
        if (meta("daily_bonus", "") == today) return@transaction false
        val daily = tasks().filter { it.kind == "DAILY" && scheduled(it, today) }
        if (daily.isEmpty() || daily.any { it.completed != today }) return@transaction false
        putMeta("coins", (meta("coins").toIntOrNull() ?: 0) + 20)
        putMeta("daily_bonus", today)
        true
    }

    private fun scheduled(task: Task, date: String): Boolean = when (task.frequency) {
        "WEEKDAYS" -> weekday(date) in 1..5
        "WEEKENDS" -> weekday(date) == 0 || weekday(date) == 6
        else -> true
    }

    override fun buySeed(id: String): Boolean {
        val item = catalog.firstOrNull { it.id == id } ?: return false
        return connection.transaction {
            val coins = meta("coins").toIntOrNull() ?: 0
            if (coins < item.price) return@transaction false
            putMeta("coins", coins - item.price)
            seed(id, quantity(id) + 1)
            true
        }
    }

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
        if (coins < GardenPlotPolicy.UNLOCK_PRICE) return@transaction false
        putMeta("coins", coins - GardenPlotPolicy.UNLOCK_PRICE)
        execute("UPDATE garden_plots SET unlocked=1 WHERE id=?", plotId)
        val next = count("SELECT COALESCE(MAX(id),0)+1 FROM garden_plots")
        execute("INSERT INTO garden_plots(id,unlocked) VALUES(?,0)", next)
        true
    }

    override fun buyFertilizer(): Boolean = connection.transaction {
        val coins = meta("coins").toIntOrNull() ?: 0
        if (coins < 25) return@transaction false
        putMeta("coins", coins - 25)
        seed("fertilizer", quantity("fertilizer") + 1)
        true
    }

    override fun fertilize(treeId: Long): Boolean = connection.transaction {
        val count = quantity("fertilizer")
        if (count <= 0) return@transaction false
        execute("UPDATE trees SET growth=MIN(120,growth+25) WHERE id=?", treeId)
        seed("fertilizer", count - 1)
        true
    }

    override fun water(treeId: Long): TreeWateringResult = connection.transaction {
        if (count("SELECT 1 FROM trees WHERE id=?", treeId) == 0) return@transaction TreeWateringResult.TREE_NOT_FOUND
        val today = currentDateKey()
        if (meta("watered_$treeId", "") == today) return@transaction TreeWateringResult.ALREADY_WATERED
        val water = waterMl()
        if (water < 50) return@transaction TreeWateringResult.NOT_ENOUGH_WATER
        execute("UPDATE trees SET growth=MIN(120,growth+8) WHERE id=?", treeId)
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
        if (count("SELECT 1 FROM trees WHERE id=?", treeId) != 0) putMeta("decoration_$treeId", decoration)
    }
}
