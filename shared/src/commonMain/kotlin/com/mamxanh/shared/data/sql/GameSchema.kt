package com.mamxanh.shared.data.sql

/** Shared schema; migrations preserve existing tree identities and player balances. */
object GameSchema {
    const val VERSION = 4

    fun create(connection: GameSqlConnection) {
        connection.execute("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,kind TEXT NOT NULL,category TEXT NOT NULL,reward INTEGER NOT NULL,frequency TEXT NOT NULL,due TEXT NOT NULL DEFAULT '',completed TEXT NOT NULL DEFAULT '',streak INTEGER NOT NULL DEFAULT 0,last_done TEXT NOT NULL DEFAULT '')")
        connection.execute("CREATE TABLE trees(id INTEGER PRIMARY KEY AUTOINCREMENT,species_id TEXT NOT NULL,name TEXT NOT NULL,growth INTEGER NOT NULL DEFAULT 0,planted TEXT NOT NULL)")
        connection.execute("CREATE TABLE inventory(item TEXT PRIMARY KEY,quantity INTEGER NOT NULL)")
        connection.execute("CREATE TABLE meta(k TEXT PRIMARY KEY,v TEXT NOT NULL)")
        connection.execute("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,task_id INTEGER NOT NULL,title TEXT NOT NULL,coins INTEGER NOT NULL)")
        listOf("coins" to "0", "daily_bonus" to "", "water_ml" to "542").forEach { (key, value) ->
            connection.execute("INSERT INTO meta(k,v) VALUES(?,?)", listOf(key, value))
        }
        listOf("succulent").forEach {
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
        phaseOne(connection)
    }

    private fun phaseOne(c: GameSqlConnection) {
        listOf(
            "description TEXT NOT NULL DEFAULT ''", "days TEXT NOT NULL DEFAULT ''",
            "verification TEXT NOT NULL DEFAULT 'CLICK'", "minutes INTEGER NOT NULL DEFAULT 5",
            "start_minute INTEGER NOT NULL DEFAULT -1", "end_minute INTEGER NOT NULL DEFAULT -1",
            "reminder INTEGER NOT NULL DEFAULT -1", "lifecycle TEXT NOT NULL DEFAULT 'ACTIVE'",
            "created INTEGER NOT NULL DEFAULT 0", "updated INTEGER NOT NULL DEFAULT 0",
            "archived INTEGER NOT NULL DEFAULT 0"
        ).forEach { c.execute("ALTER TABLE tasks ADD COLUMN $it") }
        c.execute("CREATE TABLE occurrences(task_id INTEGER NOT NULL,date TEXT NOT NULL,title TEXT NOT NULL,category TEXT NOT NULL,kind TEXT NOT NULL,frequency TEXT NOT NULL,days TEXT NOT NULL,start_minute INTEGER NOT NULL,end_minute INTEGER NOT NULL,nominal INTEGER NOT NULL,granted INTEGER NOT NULL,at INTEGER NOT NULL,PRIMARY KEY(task_id,date))")
        c.execute("CREATE UNIQUE INDEX todo_once ON occurrences(task_id) WHERE kind='TODO'")
        c.execute("CREATE TABLE wallet(id TEXT PRIMARY KEY,date TEXT NOT NULL,type TEXT NOT NULL,entity TEXT NOT NULL,at INTEGER NOT NULL,nominal INTEGER NOT NULL,amount INTEGER NOT NULL,balance INTEGER NOT NULL)")
        c.execute("CREATE TABLE plans(date TEXT PRIMARY KEY,cleared INTEGER NOT NULL DEFAULT 0)")
        c.execute("CREATE TABLE obligations(date TEXT NOT NULL,task_id INTEGER NOT NULL,title TEXT NOT NULL,category TEXT NOT NULL,frequency TEXT NOT NULL,days TEXT NOT NULL,start_minute INTEGER NOT NULL,end_minute INTEGER NOT NULL,waived INTEGER NOT NULL DEFAULT 0,PRIMARY KEY(date,task_id))")
        c.execute("CREATE TABLE sessions(id TEXT PRIMARY KEY,task_id INTEGER NOT NULL,date TEXT NOT NULL,seconds INTEGER NOT NULL DEFAULT 0,required INTEGER NOT NULL,reward INTEGER NOT NULL,running INTEGER NOT NULL DEFAULT 0,tick INTEGER NOT NULL DEFAULT 0,zone TEXT NOT NULL,start_at INTEGER NOT NULL,end_at INTEGER NOT NULL,UNIQUE(task_id,date))")
        c.execute("CREATE TABLE timer_intervals(session_id TEXT NOT NULL,started INTEGER NOT NULL,ended INTEGER NOT NULL,seconds INTEGER NOT NULL)")
        c.execute("CREATE TABLE care_events(id INTEGER PRIMARY KEY AUTOINCREMENT,tree_id INTEGER NOT NULL,type TEXT NOT NULL,at INTEGER NOT NULL,resource INTEGER NOT NULL,growth INTEGER NOT NULL)")
        c.execute("CREATE TABLE purchase_receipts(id TEXT PRIMARY KEY,item TEXT NOT NULL,quantity INTEGER NOT NULL,total INTEGER NOT NULL,at INTEGER NOT NULL)")
        // Historical rewards are audited without rebalancing existing wealth or side effects.
        c.execute("INSERT OR IGNORE INTO occurrences SELECT logs.task_id,logs.date,logs.title,COALESCE(tasks.category,''),COALESCE(tasks.kind,'DAILY'),COALESCE(tasks.frequency,'EVERY_DAY'),'',-1,-1,logs.coins,logs.coins,0 FROM logs LEFT JOIN tasks ON tasks.id=logs.task_id ORDER BY logs.id")
        c.execute("INSERT OR IGNORE INTO occurrences SELECT id,completed,title,category,kind,frequency,'',-1,-1,reward,reward,0 FROM tasks WHERE completed<>''")
        c.execute("INSERT INTO wallet SELECT 'legacy-log-' || id,date,'LEGACY_REWARD',CAST(task_id AS TEXT),0,coins,coins,0 FROM logs")
        c.execute("INSERT OR IGNORE INTO plans(date,cleared) SELECT v,1 FROM meta WHERE k='daily_bonus' AND v<>''")
        c.execute("UPDATE tasks SET reward=8,verification='CLICK'")
        c.execute("INSERT OR IGNORE INTO meta(k,v) VALUES('balance_version','2')")
        c.execute("INSERT OR IGNORE INTO meta(k,v) VALUES('starter_claimed','1')")
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
        if (oldVersion < 4) phaseOne(connection)
        if (oldVersion < 2) {
            val water = connection.query("SELECT v FROM meta WHERE k='water_ml'", 1).firstOrNull()?.string(0)
            if (water.isNullOrBlank()) {
                connection.execute("INSERT OR REPLACE INTO meta(k,v) VALUES('water_ml','542')")
            }
        }
    }
}
