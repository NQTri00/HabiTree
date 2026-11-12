package com.mamxanh.shared.data.sql

data class BackupTable(val name:String,val columns:List<String>,val rows:List<List<String?>>)
data class GameBackup(val schema:Int,val appVersion:String,val createdAt:Long,val tables:List<BackupTable>) {
    fun count(table:String)=tables.firstOrNull{it.name==table}?.rows?.size ?: 0
}

/** Length-prefixed UTF-8 text, bounded before allocation; no ZIP paths or executable SQL. */
object GameBackupCodec {
    const val MAX_BYTES=16*1024*1024
    private const val MAX_ROWS=100_000
    fun encode(backup:GameBackup):ByteArray {
        val body=buildString {
            fun cell(v:String?){if(v==null)append("-1:") else {append(v.length);append(':');append(v)}}
            cell(backup.schema.toString());cell(backup.appVersion);cell(backup.createdAt.toString())
            cell(backup.tables.size.toString())
            backup.tables.forEach{t->cell(t.name);cell(t.columns.size.toString());t.columns.forEach{cell(it)}
                cell(t.rows.size.toString());t.rows.forEach{r->require(r.size==t.columns.size);r.forEach{cell(it)}}}
        }
        val payload=body.encodeToByteArray()
        require(payload.size<MAX_BYTES-80){"Backup exceeds size limit"}
        return ("MAMXANH/1\n"+BackupDigest.sha256(payload)+"\n"+body).encodeToByteArray()
    }
    fun decode(bytes:ByteArray):GameBackup {
        require(bytes.size in 80..MAX_BYTES){"Invalid backup size"}
        val text=bytes.decodeToString(throwOnInvalidSequence=true)
        require(text.startsWith("MAMXANH/1\n")){"Unsupported backup format"}
        require(text.getOrNull(74)=='\n'){"Invalid checksum header"}
        val body=text.substring(75)
        require(BackupDigest.sha256(body.encodeToByteArray())==text.substring(10,74)){"Backup checksum mismatch"}
        var at=0
        fun cell():String? {
            val colon=body.indexOf(':',at)
            require(colon in at..minOf(at+8,body.lastIndex)){"Malformed field length"}
            val n=body.substring(at,colon).toIntOrNull() ?: error("Invalid field length")
            at=colon+1
            if(n == -1)return null
            require(n in 0..1_000_000 && n<=body.length-at){"Invalid field bounds"}
            return body.substring(at,at+n).also{at+=n}
        }
        fun required()=requireNotNull(cell()){"Required field missing"}
        fun number(max:Int)=required().toInt().also{require(it in 0..max)}
        val schema=number(1000);val app=required().also{require(it.length in 1..80)};val created=required().toLong().also{require(it>=0)}
        val count=number(64);var total=0
        val tables=List(count) {
            val name=required();val columns=List(number(128)){required()}
            require(columns.isNotEmpty() && columns.distinct().size==columns.size)
            val rows=number(MAX_ROWS);total+=rows;require(total<=MAX_ROWS)
            BackupTable(name,columns,List(rows){List(columns.size){cell()}})
        }
        require(at==body.length && tables.map{it.name}.distinct().size==tables.size){"Trailing data or duplicate table"}
        return GameBackup(schema,app,created,tables)
    }
}

/** Shared persistence portability. Every current user table is explicitly owned and exported. */
class GameBackupEngine(private val c:GameSqlConnection) {
    companion object {
        val TABLES=listOf("tasks","trees","inventory","meta","logs","garden_plots","occurrences","wallet","plans",
            "obligations","sessions","timer_intervals","care_events","purchase_receipts","tree_growth_days",
            "tree_dispositions","tree_actions","engagement_receipts","template_copies","focus_sessions","focus_intervals",
            "reminder_preferences","reminder_deliveries","achievement_unlocks","rest_days","protection_uses",
            "memory_events","weekly_reflections","garden_layout","species_records","collection_rewards","garden_receipts","garden_sections","sqlite_sequence")
    }
    private fun columns(table:String)=c.query("PRAGMA table_info($table)",6).map{it.string(1)}
    fun snapshot(appVersion:String,createdAt:Long):GameBackup=c.transaction {
        val actual=c.query("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'android_%' AND (name NOT LIKE 'sqlite_%' OR name='sqlite_sequence')",1).map{it.string(0)}.toSet()
        require(actual==TABLES.toSet()){"Unregistered persistent table"}
        GameBackup(GameSchema.VERSION,appVersion,createdAt,TABLES.map{table->
            val names=columns(table)
            BackupTable(table,names,c.query("SELECT ${names.joinToString(",")} FROM $table ORDER BY rowid",names.size)
                .map{row->names.indices.map{row.nullableString(it)}})
        })
    }
    fun validateShape(backup:GameBackup) {
        require(backup.schema==GameSchema.VERSION){"Unsupported database schema"}
        require(backup.tables.size==TABLES.size && backup.tables.map{it.name}.toSet()==TABLES.toSet()){"Incomplete or unknown backup tables"}
        backup.tables.forEach{t->
            val definition=c.query("PRAGMA table_info(${t.name})",6)
            require(t.columns==definition.map{it.string(1)}){"Unexpected columns"}
            t.rows.forEach{row->
                require(row.size==t.columns.size)
                row.forEachIndexed{i,value->
                    val column=definition[i]
                    require(value==null || value.length<=16_384){"Gameplay field exceeds limit"}
                    require(value!=null || column.int(3)==0 && column.int(5)==0){"Missing required cell"}
                    if(value!=null && column.string(2)=="INTEGER")require(value.toLongOrNull()!=null){"Invalid integer"}
                }
            }
        }
    }
    fun restore(backup:GameBackup) {
        validateShape(backup)
        c.transaction {
            // Children first; current SQLite constraints stay enabled.
            TABLES.asReversed().filter{it!="trees" && it!="tasks" && it!="sqlite_sequence"}.forEach{c.execute("DELETE FROM $it")}
            c.execute("DELETE FROM trees");c.execute("DELETE FROM tasks");c.execute("DELETE FROM sqlite_sequence")
            TABLES.forEach{name->
                if(name=="sqlite_sequence")c.execute("DELETE FROM sqlite_sequence")
                val t=backup.tables.first{it.name==name}
                val sql="INSERT INTO $name(${t.columns.joinToString(",")}) VALUES(${t.columns.joinToString(","){"?"}})"
                t.rows.forEach{c.execute(sql,it)}
            }
            validateIntegrity()
        }
    }
    fun validateIntegrity() {
        fun reject(sql:String){require(c.query(sql,1).first().long(0)==0L){"Backup violates data integrity"}}
        reject("SELECT COUNT(*) FROM meta WHERE k='coins' AND (CAST(v AS INTEGER)<0 OR CAST(CAST(v AS INTEGER) AS TEXT)<>v)")
        require(c.query("SELECT COUNT(*) FROM meta WHERE k='coins'",1).first().int(0)==1)
        reject("SELECT COUNT(*) FROM inventory WHERE quantity<0")
        reject("SELECT COUNT(*) FROM wallet WHERE balance<0 OR ABS(amount)>ABS(nominal) OR (amount<0 AND nominal>=0) OR (amount>0 AND nominal<0)")
        reject("SELECT COUNT(*) FROM garden_plots p LEFT JOIN trees t ON t.id=p.tree_id WHERE p.tree_id IS NOT NULL AND (t.id IS NULL OR t.disposition<>'' OR p.unlocked<>1)")
        reject("SELECT COUNT(*) FROM trees WHERE growth<0 OR growth_units<0 OR growth_days<0 OR neglect_ms<0 OR (death_at>0 AND life_state<>'DEAD')")
        reject("SELECT COUNT(*) FROM occurrences o LEFT JOIN tasks t ON t.id=o.task_id WHERE (t.id IS NULL AND NOT EXISTS(SELECT 1 FROM logs l WHERE l.task_id=o.task_id AND l.date=o.date)) OR o.granted<0 OR o.granted>o.nominal")
        reject("SELECT COUNT(*) FROM sessions s LEFT JOIN tasks t ON t.id=s.task_id WHERE t.id IS NULL OR s.seconds<0 OR s.required<0 OR s.running NOT IN (0,1)")
        reject("SELECT COUNT(*) FROM timer_intervals i LEFT JOIN sessions s ON s.id=i.session_id WHERE s.id IS NULL OR i.seconds<0 OR i.ended<i.started")
        reject("SELECT COUNT(*) FROM focus_intervals i LEFT JOIN focus_sessions s ON s.id=i.session_id WHERE s.id IS NULL OR i.seconds<0 OR i.ended<i.started")
        reject("SELECT COUNT(*) FROM tree_growth_days g LEFT JOIN trees t ON t.id=g.tree_id WHERE t.id IS NULL OR g.units<0 OR g.credited NOT IN (0,1)")
        reject("SELECT COUNT(*) FROM garden_layout l LEFT JOIN garden_plots p ON p.id=l.plot_id WHERE (l.plot_id<>0 AND (p.id IS NULL OR p.unlocked<>1)) OR (l.tree_id<>0 AND NOT EXISTS(SELECT 1 FROM trees t WHERE t.id=l.tree_id AND t.disposition='' AND p.tree_id=t.id))")
        reject("SELECT COUNT(*) FROM (SELECT sku,COUNT(*) n FROM garden_layout GROUP BY sku) l LEFT JOIN inventory i ON i.item=l.sku WHERE l.n>COALESCE(i.quantity,0)")
        reject("SELECT COUNT(*) FROM sqlite_sequence WHERE seq<0 OR name NOT IN ('tasks','trees','logs','care_events','focus_intervals')")
        reject("SELECT COUNT(*) FROM trees WHERE id<=0 OR life_state NOT IN ('GROWING','MATURE','DEAD') OR disposition NOT IN ('','SOLD','REMOVED')")
        reject("SELECT COUNT(*) FROM tasks WHERE id<=0 OR kind NOT IN ('HABIT','DAILY','TODO') OR verification NOT IN ('CLICK','TIMER') OR lifecycle NOT IN ('ACTIVE','PAUSED','ARCHIVED') OR frequency NOT IN ('EVERY_DAY','WEEKDAYS','WEEKENDS','CUSTOM','ONCE') OR start_minute NOT BETWEEN -1 AND 1439 OR end_minute NOT BETWEEN -1 AND 1439")
        reject("SELECT COUNT(*) FROM focus_sessions WHERE task_id<>0 AND NOT EXISTS(SELECT 1 FROM tasks t WHERE t.id=task_id)")
        reject("SELECT COUNT(*) FROM care_events e WHERE NOT EXISTS(SELECT 1 FROM trees t WHERE t.id=e.tree_id)")
        reject("SELECT COUNT(*) FROM garden_plots WHERE id<=0")
        reject("SELECT COUNT(*) FROM (SELECT name FROM sqlite_sequence GROUP BY name HAVING COUNT(*)>1)")
        c.query("SELECT date FROM occurrences",1).forEach{require(com.mamxanh.shared.model.validDate(it.string(0))){"Invalid occurrence date"}}
        val occupied=mutableSetOf<Pair<Int,String>>()
        c.query("SELECT sku,plot_id,tree_id FROM garden_layout",3).forEach{row->
            val item=com.mamxanh.shared.model.gardenCosmetics.firstOrNull{it.id==row.string(0)}
            if(item!=null) {
                require(occupied.add(row.int(1) to item.slot)){"Overlapping decoration slot"}
                require(if(item.slot=="TREE")row.long(2)>0 else row.long(2)==0L){"Invalid decoration target"}
                require(if(item.slot=="AMBIENT")row.int(1)==0 else row.int(1)>0){"Invalid decoration footprint"}
            }
        }
        require(c.query("PRAGMA foreign_key_check",4).isEmpty()){"Foreign key violation"}
    }
}
