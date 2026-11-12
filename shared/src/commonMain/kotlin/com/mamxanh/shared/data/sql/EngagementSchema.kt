package com.mamxanh.shared.data.sql

internal object EngagementSchema {
    fun create(c:GameSqlConnection,fresh:Boolean) {
        listOf(
            "CREATE TABLE IF NOT EXISTS engagement_receipts(id TEXT PRIMARY KEY,kind TEXT NOT NULL,at INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS template_copies(template_id TEXT PRIMARY KEY,task_id INTEGER NOT NULL,at INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS focus_sessions(id TEXT PRIMARY KEY,task_id INTEGER NOT NULL DEFAULT 0,title TEXT NOT NULL,date TEXT NOT NULL,target INTEGER NOT NULL,seconds INTEGER NOT NULL DEFAULT 0,state TEXT NOT NULL DEFAULT 'PAUSED',tick INTEGER NOT NULL DEFAULT 0,at INTEGER NOT NULL,zone TEXT NOT NULL,break_shown INTEGER NOT NULL DEFAULT 0)",
            "CREATE TABLE IF NOT EXISTS focus_intervals(id INTEGER PRIMARY KEY AUTOINCREMENT,session_id TEXT NOT NULL,started INTEGER NOT NULL,ended INTEGER NOT NULL,seconds INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS reminder_preferences(task_id INTEGER PRIMARY KEY,mode TEXT NOT NULL,minute INTEGER NOT NULL,follow_up INTEGER NOT NULL DEFAULT 0)",
            "CREATE TABLE IF NOT EXISTS reminder_deliveries(task_id INTEGER NOT NULL,date TEXT NOT NULL,kind TEXT NOT NULL,at INTEGER NOT NULL,PRIMARY KEY(task_id,date,kind))",
            "CREATE TABLE IF NOT EXISTS achievement_unlocks(id TEXT PRIMARY KEY,profile_id TEXT NOT NULL,at INTEGER NOT NULL,source TEXT NOT NULL,UNIQUE(id,profile_id))",
            "CREATE TABLE IF NOT EXISTS rest_days(date TEXT PRIMARY KEY,planned_at INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS protection_uses(date TEXT PRIMARY KEY,receipt TEXT UNIQUE NOT NULL,at INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS memory_events(id TEXT PRIMARY KEY,type TEXT NOT NULL,entity INTEGER NOT NULL,title TEXT NOT NULL,at INTEGER NOT NULL,note TEXT NOT NULL DEFAULT '')",
            "CREATE TABLE IF NOT EXISTS weekly_reflections(week TEXT PRIMARY KEY,note TEXT NOT NULL,updated INTEGER NOT NULL)"
        ).forEach{c.execute(it)}
        c.execute("INSERT OR IGNORE INTO meta(k,v) VALUES('onboarded',?)",listOf(if(fresh)"0" else "1"))
        if(fresh)c.execute("DELETE FROM tasks")
        c.execute("UPDATE tasks SET verification='CLICK' WHERE verification='HOLD'")
    }
}
