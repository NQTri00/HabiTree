package com.mamxanh.shared.data.sql

import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.currentDateKey
import com.mamxanh.shared.time.firstDayOfWeekIndex

/** Shared game rules and SQL transactions, using either platform's SQLite connection. */
class SqlGameDatabase(private val connection: GameSqlConnection, private val clock: com.mamxanh.shared.time.GameClock = com.mamxanh.shared.time.SystemGameClock()) : GameDatabase {
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

    override fun backup(appVersion:String,createdAt:Long) = GameBackupEngine(connection).snapshot(appVersion,createdAt)
    override fun restore(backup:GameBackup) = GameBackupEngine(connection).restore(backup)
    private val living = LivingGardenEngine(connection,clock)
    override fun livingGarden() = living.snapshot()
    override fun designGarden(action: LivingGardenAction) = living.act(action)
    private val engagement = EngagementEngine(connection,clock)
    override fun engagementSnapshot() = engagement.snapshot()
    override fun engage(action: EngagementAction) = engagement.act(action)
    override fun reminderCandidates() = engagement.reminders()
    override fun claimReminder(id: Long,date: String,kind: String) = engagement.claimReminder(id,date,kind)
    private val phase = PhaseOneEngine(connection,clock)
    private val life = TreeLifecycleEngine(connection,clock)
    override fun waterAllQuote() = life.waterAllQuote()
    override fun waterAll(key: String) = life.waterAll(key).first
    override fun applyFertilizer(treeId: Long,tierId: String) = life.fertilize(treeId,tierId)
    override fun equipCan(id: String) = life.equip(id)
    override fun treeSaleQuote(id: Long) = life.saleQuote(id)
    override fun disposeTree(id: Long,sell: Boolean,key: String):ActionResult {
        living.snapshot()
        return life.dispose(id,sell,key).also { if(it.success){putMeta("garden_layout_version",meta("garden_layout_version","0").toLong()+1);living.snapshot()} }
    }
    override fun treeDispositions() = life.dispositions()
    override fun phaseSnapshot() = phase.snapshot()
    override fun saveTask(id: Long?, config: TaskConfig) = phase.save(id,config)
    override fun taskLifecycle(id: Long,state: String) = phase.lifecycle(id,state)
    override fun taskTimer(id: Long,action: String) = phase.timer(id,action)
    override fun completeTask(id: Long) = phase.complete(id)
    override fun purchase(id: String,quantity: Int,key: String) = phase.purchase(id,quantity,key)
    override fun tasks() = phase.tasks()

    override fun garden(): Garden {
        val trees = life.trees()
        val inventory = rows("SELECT item,quantity FROM inventory", 2).associate { it.string(0) to it.int(1) }
        val plots = rows("SELECT id,unlocked,COALESCE(tree_id,0) FROM garden_plots ORDER BY id", 3).map {
            GardenPlot(it.int(0), it.int(1) == 1, it.long(2).takeIf { id -> id != 0L })
        }
        return Garden(trees, inventory, plots, meta("coins").toIntOrNull() ?: 0,life.equippedCan().itemId,
            WaterAllSummary(trees.count{it.status.canWater},trees.count{it.status.waterActive},trees.count{!it.status.alive}))
    }

    override fun statistics(): Statistics {
        val today = clock.date()
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
            treeCount = count("SELECT COUNT(*) FROM trees WHERE disposition=''"),
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
        if(!life.canMutateNow())return@transaction false
        val count = quantity(id)
        val item = catalog.firstOrNull { it.id == id } ?: return@transaction false
        if (count <= 0) return@transaction false
        val plot = if (plotId == null) {
            rows("SELECT id FROM garden_plots WHERE unlocked=1 AND tree_id IS NULL ORDER BY id LIMIT 1", 1)
        } else {
            rows("SELECT id FROM garden_plots WHERE id=? AND unlocked=1 AND tree_id IS NULL", 1, plotId)
        }.firstOrNull()?.int(0) ?: return@transaction false
        execute("INSERT INTO trees(species_id,name,growth,planted) VALUES(?,?,0,?)",
            id, name.trim().ifBlank { item.name }, clock.date())
        val treeId = rows("SELECT last_insert_rowid()", 1).first().long(0)
        life.initializeNew(treeId)
        execute("UPDATE garden_plots SET tree_id=? WHERE id=?", treeId, plot)
        seed(id, count - 1)
        putMeta("garden_layout_version",meta("garden_layout_version","0").toLong()+1)
        living.snapshot()
        true
    }

    override fun unlockPlot(plotId: Int): Boolean = connection.transaction {
        if (count("SELECT COUNT(*) FROM garden_plots WHERE id=? AND unlocked=0", plotId) != 1) return@transaction false
        if(count("SELECT COUNT(*) FROM garden_sections WHERE id<? AND unlocked=0",(plotId-1)/6+1)>0)return@transaction false
        val coins = meta("coins").toIntOrNull() ?: 0
        val price = Balance.plot(count("SELECT COUNT(*) FROM garden_plots WHERE unlocked=1"))
        if (coins < price || !phase.spend("plot:$plotId","PLOT",plotId.toString(),price)) return@transaction false
        execute("UPDATE garden_plots SET unlocked=1 WHERE id=?", plotId)
        val section=(plotId-1)/6+1
        if(count("SELECT COUNT(*) FROM garden_plots WHERE id BETWEEN ? AND ? AND unlocked=0",(section-1)*6+1,section*6)==0)
            execute("UPDATE garden_sections SET unlocked=1 WHERE id=?",section)
        putMeta("garden_layout_version",meta("garden_layout_version","0").toLong()+1)
        true
    }

    override fun buyFertilizer() = phase.purchase("fertilizer",1,"fertilizer:${com.mamxanh.shared.time.SystemGameClock().now()}").success

    override fun fertilize(treeId: Long) = life.fertilize(treeId,"AUTO").success
    override fun water(treeId: Long) = life.water(treeId)

    override fun decorate(treeId: Long, decoration: String) {
        require(decoration in setOf("NONE", "FLOWERS", "LANTERNS", "RIBBON"))
        connection.transaction {
            if (life.trees().any { it.id==treeId && it.status.alive }) {
                val snapshot=living.snapshot()
                val sku=when(decoration) {"FLOWERS"->"tree_flowers";"RIBBON"->"tree_ribbon";"LANTERNS"->"tree_lantern";else->""}
                val plot=rows("SELECT id FROM garden_plots WHERE tree_id=?",1,treeId).firstOrNull()?.int(0) ?: return@transaction
                val next=snapshot.placements.filterNot{it.treeId==treeId} + if(sku.isBlank())emptyList() else listOf(GardenPlacement("tree:$treeId",sku,plot,treeId))
                living.act(LivingGardenAction.Layout(snapshot.version,next,"accessory:$treeId:${clock.now()}"))
            }
        }
    }
}
