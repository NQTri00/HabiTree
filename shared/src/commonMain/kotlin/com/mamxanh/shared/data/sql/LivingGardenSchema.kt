package com.mamxanh.shared.data.sql

internal object LivingGardenSchema {
    fun create(c:GameSqlConnection) {
        listOf(
            "CREATE TABLE IF NOT EXISTS garden_layout(id TEXT PRIMARY KEY,sku TEXT NOT NULL,plot_id INTEGER NOT NULL,tree_id INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS species_records(id TEXT PRIMARY KEY,discovered_at INTEGER NOT NULL DEFAULT 0,highest_stage INTEGER NOT NULL DEFAULT -1,first_planted INTEGER NOT NULL DEFAULT 0,first_matured INTEGER NOT NULL DEFAULT 0,flourished INTEGER NOT NULL DEFAULT 0)",
            "CREATE TABLE IF NOT EXISTS collection_rewards(id TEXT PRIMARY KEY,at INTEGER NOT NULL,sku TEXT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS garden_receipts(id TEXT PRIMARY KEY,kind TEXT NOT NULL,at INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS garden_sections(id INTEGER PRIMARY KEY,unlocked INTEGER NOT NULL)"
        ).forEach{c.execute(it)}
        c.execute("INSERT OR IGNORE INTO meta VALUES('garden_layout_version','0')")
        // Preserve every legacy plot ID/assignment; sections only add missing locked cells.
        val max=c.query("SELECT COALESCE(MAX(id),0) FROM garden_plots",1).first().int(0)
        val sections=maxOf(4,(max+5)/6)
        for(section in 1..sections) {
            val unlocked=section==1 || c.query("SELECT COUNT(*) FROM garden_plots WHERE id BETWEEN ? AND ? AND unlocked=1",1,
                listOf(((section-1)*6+1).toString(),(section*6).toString())).first().int(0)>0
            c.execute("INSERT OR IGNORE INTO garden_sections VALUES(?,?)",listOf(section,if(unlocked)1 else 0))
            for(plot in (section-1)*6+1..section*6)c.execute("INSERT OR IGNORE INTO garden_plots(id,unlocked) VALUES(?,0)",listOf(plot))
        }
        val legacy=c.query("SELECT k,v FROM meta WHERE k LIKE 'decoration_%' AND v<>'NONE'",2)
        legacy.forEach {
            val tree=it.string(0).removePrefix("decoration_").toLongOrNull()
            val sku=when(it.string(1)){"FLOWERS"->"tree_flowers";"LANTERNS"->"tree_lantern";"RIBBON"->"tree_ribbon";else->null}
            if(tree!=null && sku!=null) {
                c.execute("INSERT OR IGNORE INTO inventory VALUES(?,1)",listOf(sku))
                val plot=c.query("SELECT id FROM garden_plots WHERE tree_id=?",1,listOf(tree.toString())).firstOrNull()?.int(0)
                if(plot!=null)c.execute("INSERT OR IGNORE INTO garden_layout VALUES(?,?,?,?)",listOf("legacy:$tree",sku,plot,tree))
                // Grandfather multiple legacy copies without invalidating their committed layout.
                val copies=c.query("SELECT COUNT(*) FROM garden_layout WHERE sku=?",1,listOf(sku)).first().int(0)
                c.execute("UPDATE inventory SET quantity=MAX(quantity,?) WHERE item=?",listOf(copies,sku))
            }
        }
    }
}
