package com.mamxanh.shared.data.sql

/** Shared schema; migrations preserve existing tree identities and player balances. */
object GameSchema {
    const val VERSION = 3

    fun create(connection: GameSqlConnection) {
        connection.execute("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,kind TEXT NOT NULL,category TEXT NOT NULL,reward INTEGER NOT NULL,frequency TEXT NOT NULL,due TEXT NOT NULL DEFAULT '',completed TEXT NOT NULL DEFAULT '',streak INTEGER NOT NULL DEFAULT 0,last_done TEXT NOT NULL DEFAULT '')")
        connection.execute("CREATE TABLE trees(id INTEGER PRIMARY KEY AUTOINCREMENT,species_id TEXT NOT NULL,name TEXT NOT NULL,growth INTEGER NOT NULL DEFAULT 0,planted TEXT NOT NULL)")
        connection.execute("CREATE TABLE inventory(item TEXT PRIMARY KEY,quantity INTEGER NOT NULL)")
        connection.execute("CREATE TABLE meta(k TEXT PRIMARY KEY,v TEXT NOT NULL)")
        connection.execute("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,task_id INTEGER NOT NULL,title TEXT NOT NULL,coins INTEGER NOT NULL)")
        listOf("coins" to "60", "daily_bonus" to "", "water_ml" to "542").forEach { (key, value) ->
            connection.execute("INSERT INTO meta(k,v) VALUES(?,?)", listOf(key, value))
        }
        listOf("cherry", "pine").forEach {
            connection.execute("INSERT INTO inventory(item,quantity) VALUES(?,1)", listOf(it))
        }
        fun task(title: String, kind: String, category: String, reward: Int, frequency: String) {
            connection.execute("INSERT INTO tasks(title,kind,category,reward,frequency,due) VALUES(?,?,?,?,?,'')",
                listOf(title, kind, category, reward, frequency))
        }
        task("Đọc hoặc học tập trung 25 phút", "DAILY", "Học tập", 10, "EVERY_DAY")
        task("Đi bộ hoặc vận động 20 phút", "DAILY", "Sức khỏe", 10, "EVERY_DAY")
        task("Uống đủ nước", "HABIT", "Sức khỏe", 5, "EVERY_DAY")
        task("Dọn góc học tập", "DAILY", "Nếp sống", 5, "WEEKDAYS")
        task("Viết nhật ký 5 phút", "HABIT", "Phát triển bản thân", 5, "EVERY_DAY")
        task("Hoàn thành một việc quan trọng", "TODO", "Công việc", 20, "ONCE")
        createPlots(connection)
    }

    private fun createPlots(connection: GameSqlConnection) {
        connection.execute("CREATE TABLE garden_plots(id INTEGER PRIMARY KEY,unlocked INTEGER NOT NULL CHECK(unlocked IN (0,1)),tree_id INTEGER UNIQUE REFERENCES trees(id))")
        val trees = connection.query("SELECT id FROM trees ORDER BY id", 1)
        val unlocked = maxOf(com.mamxanh.shared.model.GardenPlotPolicy.INITIAL_PLOTS, trees.size)
        for (index in 0 until unlocked) {
            connection.execute("INSERT INTO garden_plots(id,unlocked,tree_id) VALUES(?,1,?)",
                listOf(index + 1, trees.getOrNull(index)?.long(0)))
        }
        connection.execute("INSERT INTO garden_plots(id,unlocked) VALUES(?,0)", listOf(unlocked + 1))
    }

    fun migrate(connection: GameSqlConnection, oldVersion: Int, newVersion: Int) {
        require(newVersion == VERSION) { "Unsupported schema version: $newVersion" }
        if (oldVersion < 3) createPlots(connection)
        if (oldVersion < 2) {
            val water = connection.query("SELECT v FROM meta WHERE k='water_ml'", 1).firstOrNull()?.string(0)
            if (water.isNullOrBlank()) {
                connection.execute("INSERT OR REPLACE INTO meta(k,v) VALUES('water_ml','542')")
            }
        }
    }
}
