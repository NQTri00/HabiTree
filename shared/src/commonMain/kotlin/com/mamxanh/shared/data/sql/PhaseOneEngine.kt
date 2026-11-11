package com.mamxanh.shared.data.sql

import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.GameClock
import com.mamxanh.shared.time.SystemGameClock

/** All entry points run in the platform's serial SQLite transaction. */
class PhaseOneEngine(private val db: GameSqlConnection, private val clock: GameClock = SystemGameClock()) {
    private fun rows(sql: String, n: Int, vararg args: Any) = db.query(sql,n,args.map(Any::toString))
    private fun exec(sql: String, vararg args: Any?) = db.execute(sql,args.toList())
    private fun number(sql: String, vararg args: Any) = rows(sql,1,*args).firstOrNull()?.long(0) ?: 0L
    private fun meta(key: String, fallback: String = "") = rows("SELECT v FROM meta WHERE k=?",1,key).firstOrNull()?.string(0) ?: fallback
    private fun put(key: String, value: Any) = exec("INSERT OR REPLACE INTO meta(k,v) VALUES(?,?)",key,value.toString())
    private fun offset(date: String, days: Int) = rows("SELECT date(?,?)",1,date,"$days days").first().string(0)
    private fun weekday(date: String) = number("SELECT CAST(strftime('%w',?) AS INTEGER)",date).toInt()
    private fun scheduled(c: TaskConfig, date: String): Boolean = when(c.frequency) {
        "WEEKDAYS" -> weekday(date) in 1..5
        "WEEKENDS" -> weekday(date) in setOf(0,6)
        "CUSTOM" -> weekday(date).toString() in c.days
        else -> true
    }
    private fun clockSafe(): Boolean {
        val now = clock.now()
        val high = meta("clock_high","0").toLong()
        val date = clock.date()
        if (date < meta("day_high",date) || now + 2000 < high) return false
        put("clock_high",maxOf(now,high)); put("day_high",date)
        return true
    }
    private fun bounds(c: TaskConfig, date: String): Pair<Long,Long> {
        if (c.startMinute < 0) return 0L to Long.MAX_VALUE
        return clock.localMinute(date,c.startMinute) to clock.localMinute(
            if (c.endMinute < c.startMinute) offset(date,1) else date,c.endMinute)
    }
    private fun anchor(c: TaskConfig,id: Long? = null): String {
        val previous=offset(clock.date(),-1)
        if(id!=null) {
            val prior=rows("SELECT start_minute,end_minute FROM obligations WHERE date=? AND task_id=?",2,previous,id).firstOrNull()
            val saved=meta("schedule:$id:$previous").split('|')
            val start=prior?.int(0) ?: saved.getOrNull(2)?.toIntOrNull()
            val end=prior?.int(1) ?: saved.getOrNull(3)?.toIntOrNull()
            if(start!=null && end!=null && start>=0 && end<start &&
                clock.now()<clock.localMinute(clock.date(),end))return previous
        }
        val today = clock.date()
        if (c.startMinute >= 0 && c.endMinute < c.startMinute &&
            clock.now() < clock.localMinute(today,c.endMinute)) return offset(today,-1)
        return today
    }
    private fun config(r: GameSqlRow) = TaskConfig(r.string(1),r.string(2),r.string(3),r.string(10),
        r.string(5),r.string(11),r.string(6),r.string(12),r.int(13),r.int(14),r.int(15),r.int(16))
    private val select = "SELECT id,title,kind,category,reward,frequency,due,completed,streak,last_done,description,days,verification,minutes,start_minute,end_minute,reminder,lifecycle FROM tasks"
    private fun task(id: Long) = rows("$select WHERE id=?",18,id).firstOrNull()
    private fun freezeSchedule(id: Long,date: String,c: TaskConfig) {
        exec("INSERT OR IGNORE INTO meta(k,v) VALUES(?,?)","schedule:$id:$date",
            listOf(c.frequency,c.days,c.startMinute.toString(),c.endMinute.toString()).joinToString("|"))
    }
    private fun effective(c: TaskConfig, id: Long, date: String): TaskConfig {
        val p = rows("SELECT frequency,days,start_minute,end_minute FROM obligations WHERE date=? AND task_id=?",4,date,id).firstOrNull()
        if(p!=null)return c.copy(frequency=p.string(0),days=p.string(1),startMinute=p.int(2),endMinute=p.int(3))
        val saved=meta("schedule:$id:$date").split('|')
        return if(saved.size!=4)c else c.copy(frequency=saved[0],days=saved[1],startMinute=saved[2].toInt(),endMinute=saved[3].toInt())
    }
    private fun materialize() {
        val today = clock.date()
        if (number("SELECT COUNT(*) FROM plans WHERE date=?",today) > 0) return
        // Fill missed dates from the last evaluation, using retained task configuration.
        val last = meta("plan_last",today)
        var date = if(last < today) offset(last,1) else today
        var guard = 0
        while (date <= today && guard++ < 3660) {
            exec("INSERT OR IGNORE INTO plans(date) VALUES(?)",date)
            rows("$select WHERE lifecycle='ACTIVE'",18).forEach { row ->
                val c = config(row)
                freezeSchedule(row.long(0),date,c)
                if (c.kind!="TODO" && scheduled(c,date)) exec("INSERT OR IGNORE INTO obligations(date,task_id,title,category,frequency,days,start_minute,end_minute) VALUES(?,?,?,?,?,?,?,?)",
                    date,row.long(0),c.title,c.category,c.frequency,c.days,c.startMinute,c.endMinute)
            }
            if (date == today) break
            date = offset(date,1)
        }
        if(today >= last) put("plan_last",today)
    }
    fun tasks(): List<Task> = db.transaction {
        materialize()
        rows("$select ORDER BY id DESC",18).map { r ->
            val c = config(r)
            freezeSchedule(r.long(0),clock.date(),c)
            val date = anchor(c,r.long(0))
            val snap = effective(c,r.long(0),date)
            val b = bounds(snap,date)
            val remaining=rows("SELECT required-seconds FROM sessions WHERE task_id=? AND date=?",1,r.long(0),date).firstOrNull()?.long(0) ?: c.minutes*60L
            val enough=c.verification!="TIMER" || b.second==Long.MAX_VALUE || clock.now()+remaining*1000<=b.second
            val active = r.string(17) == "ACTIVE"
            val eligible = active && (c.kind == "TODO" || scheduled(snap,date))
            Task(r.long(0),c.title,c.kind,c.category,c.reward,c.frequency,c.due,r.string(7),taskStreak(r.long(0)),r.string(9),
                c,r.string(17),eligible,eligible && enough && clock.now() in b.first until b.second,
                (if(snap.startMinute < 0) "" else "Khả dụng ${clockLabel(snap.startMinute)}–${clockLabel(snap.endMinute)}" +
                    if(snap.endMinute < snap.startMinute) " (qua đêm)" else "") +
                    if(!enough)" · Không đủ thời gian trước khi đóng khung giờ" else "",
                rows("SELECT date,title,category,nominal,granted FROM occurrences WHERE task_id=? AND date>=? ORDER BY date DESC",5,r.long(0),offset(clock.date(),-6)).map {
                    TaskOccurrence(it.string(0),it.string(1),it.string(2),it.int(3),it.int(4))
                },number("SELECT COUNT(*) FROM obligations WHERE date=? AND task_id=? AND waived=0",clock.date(),r.long(0))>0,
                number("SELECT COUNT(*) FROM occurrences WHERE date=? AND task_id=?",date,r.long(0))>0)
        }
    }
    fun save(id: Long?, c: TaskConfig): ActionResult = db.transaction {
        c.error()?.let { return@transaction ActionResult(false,it) }
        materialize()
        if(id == null) {
            exec("INSERT INTO tasks(title,kind,category,reward,frequency,due,description,days,verification,minutes,start_minute,end_minute,reminder,created,updated) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            c.title.trim(),c.kind,c.category,c.reward,c.frequency,c.due,c.description,c.days,c.verification,c.minutes,c.startMinute,c.endMinute,c.reminderMinute,clock.now(),clock.now())
            freezeSchedule(number("SELECT last_insert_rowid()"),clock.date(),c)
        } else {
            val old=task(id) ?: return@transaction ActionResult(false,"Không tìm thấy việc")
            val existing=config(old)
            freezeSchedule(id,clock.date(),existing)
            if(number("SELECT COUNT(*) FROM sessions WHERE task_id=? AND date=?",id,clock.date())>0 &&
                (c.verification!=existing.verification || c.minutes!=existing.minutes || c.kind!=existing.kind))
                return@transaction ActionResult(false,"Giữ kiểu xác nhận và thời lượng của phiên hôm nay; có thể sửa lại ngày mai")
            exec("UPDATE tasks SET title=?,kind=?,category=?,reward=?,frequency=?,due=?,description=?,days=?,verification=?,minutes=?,start_minute=?,end_minute=?,reminder=?,updated=? WHERE id=?",
                c.title.trim(),c.kind,c.category,c.reward,c.frequency,c.due,c.description,c.days,c.verification,c.minutes,c.startMinute,c.endMinute,c.reminderMinute,clock.now(),id)
        }
        ActionResult(true,"Đã lưu việc")
    }
    fun lifecycle(id: Long, state: String): ActionResult = db.transaction {
        require(state in setOf("ACTIVE","PAUSED","ARCHIVED"))
        if(task(id) == null) return@transaction ActionResult(false,"Không tìm thấy việc")
        materialize()
        exec("UPDATE tasks SET lifecycle=?,updated=?,archived=? WHERE id=?",state,clock.now(),if(state=="ARCHIVED")clock.now() else 0,id)
        if(state != "ACTIVE") {
            exec("UPDATE obligations SET waived=1 WHERE date=? AND task_id=?",clock.date(),id)
            exec("UPDATE sessions SET running=0,tick=0 WHERE task_id=?",id)
        }
        ActionResult(true,if(state=="ARCHIVED")"Đã lưu trữ; lịch sử được giữ lại" else "Đã cập nhật trạng thái")
    }
    private fun earned(date: String) = number("SELECT COALESCE(SUM(amount),0) FROM wallet WHERE date=? AND amount>0 AND type<>'MIGRATION'",date).toInt()
    private fun award(key: String,type: String,entity: String,nominal: Int,date: String): Int {
        if(number("SELECT COUNT(*) FROM wallet WHERE id=?",key)>0) return 0
        val amount = minOf(nominal,(Balance.DAILY_CAP-earned(date)).coerceAtLeast(0))
        val balance = meta("coins","0").toInt()+amount
        put("coins",balance)
        exec("INSERT INTO wallet VALUES(?,?,?,?,?,?,?,?)",key,date,type,entity,clock.now(),nominal,amount,balance)
        return amount
    }
    fun income(key: String,type: String,entity: String,nominal: Int): ActionResult {
        if(!clockSafe())return ActionResult(false,"Đồng hồ đã lùi; chưa thể nhận xu")
        if(number("SELECT COUNT(*) FROM wallet WHERE id=?",key)>0)return ActionResult(false,"Giao dịch đã được xử lý")
        val amount=award(key,type,entity,nominal,clock.date())
        return ActionResult(true,"Nhận $amount / $nominal xu",nominal,amount)
    }
    fun spend(key: String,type: String,entity: String,total: Int): Boolean {
        if(number("SELECT COUNT(*) FROM wallet WHERE id=?",key)>0) return false
        val balance = meta("coins","0").toInt()
        if(total<0 || balance<total) return false
        put("coins",balance-total)
        exec("INSERT INTO wallet VALUES(?,?,?,?,?,?,?,?)",key,clock.date(),type,entity,clock.now(),-total,-total,balance-total)
        return true
    }
    private fun eligible(c: TaskConfig,id: Long,date: String): String? {
        if(task(id)?.string(17)!="ACTIVE") return "Việc đang tạm dừng hoặc lưu trữ"
        if(c.kind!="TODO" && !scheduled(c,date)) return "Hôm nay không có lịch"
        val b=bounds(c,date)
        if(clock.now() !in b.first until b.second) return "Ngoài khung giờ ${clockLabel(c.startMinute)}–${clockLabel(c.endMinute)}"
        if(number("SELECT COUNT(*) FROM occurrences WHERE task_id=? AND (date=? OR kind='TODO')",id,date)>0) return "Đã hoàn thành; không nhận thưởng hai lần"
        return null
    }
    fun complete(id: Long): ActionResult = db.transaction {
        materialize()
        if(!clockSafe()) return@transaction ActionResult(false,"Đồng hồ đã lùi; chờ giờ thực bắt kịp để nhận thưởng")
        val row=task(id) ?: return@transaction ActionResult(false,"Không tìm thấy việc")
        val original=config(row)
        val session=rows("SELECT id,date,seconds,required,reward,zone,end_at FROM sessions WHERE task_id=? ORDER BY date DESC LIMIT 1",7,id).firstOrNull()
        val date=if(original.verification=="TIMER" && session!=null)session.string(1) else anchor(original,id)
        val c=effective(original,id,date)
        eligible(c,id,date)?.let { return@transaction ActionResult(false,it) }
        var nominal=c.reward
        if(c.verification=="TIMER") {
            if(session==null || session.long(2)<session.long(3)) return@transaction ActionResult(false,"Chưa đủ thời gian hoạt động")
            if(session.string(5)!=clock.zone() || clock.now()>=session.long(6)) return@transaction ActionResult(false,"Khung giờ hoặc múi giờ đã thay đổi")
            nominal=session.int(4)
        }
        val granted=award("task:$id:$date","TASK",id.toString(),nominal,clock.date())
        exec("INSERT INTO occurrences VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",id,date,c.title,c.category,c.kind,c.frequency,c.days,c.startMinute,c.endMinute,nominal,granted,clock.now())
        exec("INSERT INTO logs(date,task_id,title,coins) VALUES(?,?,?,?)",date,id,c.title,granted)
        exec("UPDATE tasks SET completed=?,last_done=?,streak=streak+1 WHERE id=?",date,date,id)
        exec("UPDATE sessions SET running=0,tick=0 WHERE task_id=?",id)
        // Task rewards never bypass the timed, watered growth engine.
        ActionResult(true,"+${granted} xu / ${nominal} xu" + if(granted<nominal)" · Giới hạn ${Balance.DAILY_CAP} xu/ngày" else "",nominal,granted)
    }
    fun timer(id: Long,action: String): ActionResult = db.transaction {
        materialize()
        val r=task(id) ?: return@transaction ActionResult(false,"Không tìm thấy việc")
        val c=config(r); val date=anchor(c,id); val e=effective(c,id,date)
        if(action=="PAUSE") {
            exec("UPDATE sessions SET running=0,tick=0 WHERE task_id=?",id)
            return@transaction ActionResult(true,"Đã tạm dừng")
        }
        if(c.verification!="TIMER") return@transaction ActionResult(false,"Việc này dùng Click")
        if(!clockSafe()) return@transaction ActionResult(false,"Kiểm tra lại đồng hồ")
        eligible(e,id,date)?.let {
            if(action=="TICK")exec("UPDATE sessions SET running=0,tick=0 WHERE task_id=?",id)
            return@transaction ActionResult(false,it)
        }
        val b=bounds(e,date)
        val session=rows("SELECT id,seconds,required,tick,running,zone,end_at FROM sessions WHERE task_id=? AND date=?",7,id,date).firstOrNull()
        if(action=="TICK") {
            if(session==null || session.int(4)==0) return@transaction ActionResult(false,"Bộ đếm đang dừng")
            if(session.string(5)!=clock.zone() || clock.now()>=session.long(6)) {
                exec("UPDATE sessions SET running=0,tick=0 WHERE id=?",session.string(0))
                return@transaction ActionResult(false,"Khung giờ đã kết thúc hoặc múi giờ thay đổi")
            }
            val delta=((clock.elapsed()-session.long(3))/1000).coerceIn(0,2)
            exec("UPDATE sessions SET seconds=MIN(required,seconds+?),tick=? WHERE id=?",delta,clock.elapsed(),session.string(0))
            if(delta>0)exec("INSERT INTO timer_intervals VALUES(?,?,?,?)",session.string(0),clock.now()-delta*1000,clock.now(),delta)
            return@transaction ActionResult(true,"Đang tập trung")
        }
        val remaining=(session?.long(2) ?: c.minutes*60L)-(session?.long(1) ?: 0)
        if(b.second!=Long.MAX_VALUE && clock.now()+remaining*1000>b.second) return@transaction ActionResult(false,"Không đủ thời gian trước khi khung giờ kết thúc")
        exec("INSERT OR IGNORE INTO sessions(id,task_id,date,required,reward,zone,start_at,end_at) VALUES(?,?,?,?,?,?,?,?)",
            "timer:$id:$date",id,date,c.minutes*60,c.reward,clock.zone(),b.first,b.second)
        if(session!=null && session.string(5)!=clock.zone()) return@transaction ActionResult(false,"Múi giờ thay đổi; tiếp tục vào ngày phù hợp")
        exec("UPDATE sessions SET running=1,tick=? WHERE task_id=? AND date=?",clock.elapsed(),id,date)
        ActionResult(true,"Đang tập trung · Chỉ tính khi ứng dụng ở phía trước")
    }
    private fun clearEligible(date: String): Boolean =
        number("SELECT cleared FROM plans WHERE date=?",date)==0L &&
        number("SELECT COUNT(*) FROM obligations WHERE date=? AND waived=0",date)>0 &&
        number("SELECT COUNT(*) FROM obligations o WHERE date=? AND waived=0 AND NOT EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",date)==0L
    private fun clearDate(): String? =
        listOf(clock.date(),offset(clock.date(),-1)).firstOrNull { clearEligible(it) }
    fun canClear(): Boolean = db.transaction { materialize(); clearDate()!=null }
    fun claimed(): Boolean = number("SELECT cleared FROM plans WHERE date=?",clock.date())>0
    fun clear(): ActionResult = db.transaction {
        materialize()
        val date=clearDate()
        if(!clockSafe() || date==null) return@transaction ActionResult(false,"Hoàn thành kế hoạch bắt buộc để nhận thưởng")
        // An overnight occurrence belongs to yesterday; its income still uses today's cap.
        val amount=award("clear:$date","DAILY_CLEAR",date,Balance.DAILY_CLEAR,clock.date())
        exec("UPDATE plans SET cleared=1 WHERE date=?",date)
        put("daily_bonus",date)
        val streak=streak()
        val milestone=Balance.milestones[streak] ?: 0
        val extra=if(milestone>0)award("milestone:$streak","MILESTONE",streak.toString(),milestone,clock.date()) else 0
        ActionResult(true,"Daily Clear +$amount xu" + if(extra>0)" · Cột mốc +$extra xu" else "",Balance.DAILY_CLEAR+milestone,amount+extra)
    }
    private fun taskStreak(id: Long): Int {
        var total=0
        val dates=rows("SELECT date FROM obligations WHERE task_id=? AND waived=0 UNION SELECT date FROM occurrences WHERE task_id=? ORDER BY date DESC",1,id,id)
        for(row in dates) {
            val date=row.string(0)
            if(number("SELECT COUNT(*) FROM occurrences WHERE task_id=? AND date=?",id,date)>0)total++
            else if(date!=clock.date())break
        }
        return total
    }
    private fun streak(): Int {
        var count=0
        val dates=rows("SELECT date,cleared FROM plans ORDER BY date DESC",2)
        for(r in dates) {
            val required=number("SELECT COUNT(*) FROM obligations WHERE date=? AND waived=0",r.string(0))
            if(required==0L)continue
            val missing=number("SELECT COUNT(*) FROM obligations o WHERE date=? AND waived=0 AND NOT EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",r.string(0))
            if(missing==0L)count++ else if(r.string(0)!=clock.date())break
        }
        return count
    }
    fun snapshot(): PhaseSnapshot = db.transaction {
        materialize()
        val date=clock.date()
        val days=rows("SELECT date,cleared FROM plans WHERE date>=? ORDER BY date DESC",2,offset(date,-6)).map { r ->
            val d=r.string(0)
            DaySummary(d,number("SELECT COUNT(*) FROM obligations WHERE date=? AND waived=0",d).toInt(),
                number("SELECT COUNT(*) FROM obligations o WHERE date=? AND waived=0 AND EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",d).toInt(),r.int(1)==1,earned(d))
        }
        PhaseSnapshot(earned(date),days.firstOrNull()?.eligible ?: 0,days.firstOrNull()?.completed ?: 0,streak(),
            rows("SELECT id,date,type,entity,nominal,amount,balance FROM wallet ORDER BY at DESC,rowid DESC LIMIT 100",7).map { WalletEntry(it.string(0),it.string(1),it.string(2),it.string(3),it.int(4),it.int(5),it.int(6)) },
            days,
            rows("SELECT date,title,category,nominal,granted FROM occurrences WHERE date>=? ORDER BY date DESC",5,offset(date,-6)).map{TaskOccurrence(it.string(0),it.string(1),it.string(2),it.int(3),it.int(4))},
            rows("SELECT id,task_id,date,seconds,required,running,reward FROM sessions WHERE date>=? ORDER BY date DESC",7,offset(date,-1)).map{TimerSession(it.string(0),it.long(1),it.string(2),it.long(3),it.long(4),it.int(5)==1,it.int(6))},
            rows("SELECT tree_id,type,at,resource,growth FROM care_events ORDER BY id DESC LIMIT 50",5).map{CareEvent(it.long(0),it.string(1),it.long(2),it.int(3),it.int(4))},
            rows("SELECT category,COUNT(*) FROM occurrences WHERE date>=? GROUP BY category",2,offset(date,-6)).associate{it.string(0) to it.int(1)})
    }
    fun care(treeId: Long,type: String,resource: Int,growth: Int) =
        exec("INSERT INTO care_events(tree_id,type,at,resource,growth) VALUES(?,?,?,?,?)",treeId,type,clock.now(),resource,growth)
    fun purchase(id: String,quantity: Int,key: String): ActionResult = db.transaction {
        if(number("SELECT COUNT(*) FROM purchase_receipts WHERE id=?",key)>0) return@transaction ActionResult(false,"Giao dịch này đã được xử lý")
        if(quantity !in 1..99) return@transaction ActionResult(false,"Số lượng từ 1 đến 99")
        val product=careProducts.firstOrNull{it.id==id}
        if(product?.permanent==true && (quantity!=1 || number("SELECT quantity FROM inventory WHERE item=?",id)>0))
            return@transaction ActionResult(false,"Bình tưới đã sở hữu hoặc số lượng không hợp lệ")
        val price=product?.price ?: catalog.firstOrNull{it.id==id}?.price
            ?: return@transaction ActionResult(false,"Không tìm thấy sản phẩm")
        val total=price*quantity
        if(!spend(key,"PURCHASE",id,total))return@transaction ActionResult(false,"Không đủ xu; túi đồ không thay đổi")
        exec("INSERT OR REPLACE INTO inventory(item,quantity) VALUES(?,?)",id,number("SELECT quantity FROM inventory WHERE item=?",id)+quantity)
        exec("INSERT INTO purchase_receipts VALUES(?,?,?,?,?)",key,id,quantity,total,clock.now())
        ActionResult(true,"Đã mua $quantity · Tổng $total xu · Còn ${meta("coins")} xu")
    }
}
