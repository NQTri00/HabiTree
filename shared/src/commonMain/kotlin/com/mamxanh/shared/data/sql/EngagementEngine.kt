package com.mamxanh.shared.data.sql

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.GameClock

/** Offline, transaction-bound engagement rules. No badge or focus-session coin source. */
class EngagementEngine(private val db:GameSqlConnection,private val clock:GameClock) {
    private val phase=PhaseOneEngine(db,clock)
    private fun rows(sql:String,n:Int,vararg args:Any)=db.query(sql,n,args.map(Any::toString))
    private fun exec(sql:String,vararg args:Any?)=db.execute(sql,args.toList())
    private fun num(sql:String,vararg args:Any)=rows(sql,1,*args).firstOrNull()?.long(0) ?: 0L
    private fun meta(k:String,f:String="")=rows("SELECT v FROM meta WHERE k=?",1,k).firstOrNull()?.string(0) ?: f
    private fun put(k:String,v:Any)=exec("INSERT OR REPLACE INTO meta VALUES(?,?)",k,v.toString())
    private fun offset(d:String,n:Int)=rows("SELECT date(?,?)",1,d,"$n days").first().string(0)
    private fun localDate(at:Long):String {
        var d=rows("SELECT date(?/1000,'unixepoch')",1,at).first().string(0)
        repeat(3) {
            if(at<clock.localMinute(d,0))d=offset(d,-1)
            else if(at>=clock.localMinute(offset(d,1),0))d=offset(d,1)
        }
        return d
    }
    private fun memory(id:String,type:String,entity:Long,title:String,at:Long) {
        exec("INSERT OR IGNORE INTO memory_events(id,type,entity,title,at) VALUES(?,?,?,?,?)",id,type,entity,title,at)
    }
    private fun receipt(id:String,kind:String):Boolean {
        if(id.isBlank() || num("SELECT COUNT(*) FROM engagement_receipts WHERE id=?",id)>0)return false
        exec("INSERT INTO engagement_receipts VALUES(?,?,?)",id,kind,clock.now());return true
    }
    private fun safe()=clock.now()+2000>=meta("engagement_high","0").toLong() && clock.date()>=meta("engagement_day",clock.date())
    private fun markClock(){put("engagement_high",maxOf(clock.now(),meta("engagement_high","0").toLong()));put("engagement_day",clock.date())}
    fun settings()=EngagementSettings(meta("reminders_paused","1")=="1",meta("quiet_start","1320").toInt(),
        meta("quiet_end","420").toInt(),meta("tree_reminders","0")=="1",meta("reduced_motion","0")=="1",meta("break_prompts","0")=="1")
    private fun quiet(at:Long):Boolean {
        val s=settings();if(s.quietStart==s.quietEnd)return false
        val d=localDate(at)
        val start=clock.localMinute(d,s.quietStart);val end=clock.localMinute(d,s.quietEnd)
        return if(s.quietStart<s.quietEnd)at in start until end else at>=start || at<end
    }
    private fun copyTemplates(ids:List<String>) {
        ids.distinct().forEach { id ->
            if(num("SELECT COUNT(*) FROM template_copies WHERE template_id=?",id)==0L) {
                val t=habitTemplates.first{it.id==id}
                check(phase.save(null,t.config).success)
                val task=num("SELECT MAX(id) FROM tasks")
                exec("INSERT INTO template_copies VALUES(?,?,?)",id,task,clock.now())
            }
        }
    }
    fun act(a:EngagementAction):ActionResult=db.transaction {
        if(!safe())return@transaction ActionResult(false,AppText.text(TextKey.CopyDongHoDaLuiChoGioThucBatKip1475f3))
        val result=when(a) {
            is EngagementAction.Onboard -> {
                if(meta("onboarded")=="1")ActionResult(false,AppText.text(TextKey.CopyHuongDanDaHoanTatDungThuVienDeThemViecBeee35))
                else if(!a.skip && (a.seed !in listOf("succulent","monstera","snake") || a.templates.distinct().size !in 2..5 ||
                    a.templates.any{id->habitTemplates.none{it.id==id}}))ActionResult(false,AppText.text(TextKey.CopyChonCayVa25ViecPhuHopB6e17e))
                else if(!receipt(a.key,"ONBOARD"))ActionResult(false,AppText.text(TextKey.CopyDaXuLy602337))
                else {
                    if(!a.skip) {
                        if(meta("free_seed_available")=="1" && num("SELECT COUNT(*) FROM trees")==0L) {
                            exec("UPDATE inventory SET quantity=quantity-1 WHERE item='succulent' AND quantity>0")
                            exec("INSERT OR IGNORE INTO inventory VALUES(?,0)",a.seed)
                            exec("UPDATE inventory SET quantity=quantity+1 WHERE item=?",a.seed)
                            put("starter_species",a.seed)
                        }
                        put("personal_focus",a.focus);copyTemplates(a.templates)
                        if(a.reminders) {
                            put("reminders_paused",0)
                            a.templates.forEach { template ->
                                val id=num("SELECT task_id FROM template_copies WHERE template_id=?",template)
                                exec("INSERT OR REPLACE INTO reminder_preferences VALUES(?,'AT',540,0)",id)
                            }
                        }
                    }
                    put("onboarded","1");ActionResult(true,AppText.text(TextKey.CopySanSangChoNhungViecNho96b4f6))
                }
            }
            is EngagementAction.Templates -> {
                if(a.ids.isEmpty() || a.ids.any{id->habitTemplates.none{it.id==id}})ActionResult(false,AppText.text(TextKey.CopyChonViecMau74fa0a))
                else if(!receipt(a.key,"TEMPLATES"))ActionResult(false,AppText.text(TextKey.CopyDaXuLy602337))
                else {copyTemplates(a.ids);ActionResult(true,AppText.text(TextKey.CopyDaThemViecMau3cca51))}
            }
            is EngagementAction.StartFocus -> start(a)
            is EngagementAction.Focus -> control(a.id,a.action)
            is EngagementAction.Rest -> {
                if(!validDate(a.date) || a.date<=clock.date() || a.date>offset(clock.date(),366))ActionResult(false,AppText.text(TextKey.CopyChonNgayNghiTuNgayMai60732f))
                else if(num("SELECT COUNT(*) FROM rest_days WHERE date=?",a.date)>0)ActionResult(false,AppText.text(TextKey.CopyNgayNghiDaDuocLenLich560b54))
                else if(num("SELECT COUNT(*) FROM rest_days WHERE substr(date,1,7)=?",a.date.take(7))>=2)ActionResult(false,AppText.text(TextKey.CopyToiDa2NgayNghiMoiThangCf72ae))
                else {
                    exec("INSERT INTO rest_days VALUES(?,?)",a.date,clock.now())
                    exec("UPDATE obligations SET waived=1 WHERE date=?",a.date)
                    memory("rest:${a.date}","REST",0,AppText.text(TextKey.CopyNgayNghiDaLenLich75e8a1),clock.now())
                    ActionResult(true,AppText.text(TextKey.CopyDaLenLichNghiKhongTinhLaNgayThanhCong44b511))
                }
            }
            is EngagementAction.BuyProtection -> {
                if(!phase.spend("protection-buy:${a.key}","PURCHASE","protection",250))ActionResult(false,AppText.text(TextKey.CopyCan250XuChuaMuaVeE8abf3))
                else {
                    exec("INSERT OR IGNORE INTO inventory VALUES('protection',0)")
                    exec("UPDATE inventory SET quantity=quantity+1 WHERE item='protection'")
                    ActionResult(true,AppText.text(TextKey.CopyDaThem1VeBaoVe9989f3))
                }
            }
            is EngagementAction.Protect -> {
                phase.snapshot()
                val missed=num("SELECT COUNT(*) FROM obligations o WHERE date=? AND waived=0 AND NOT EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",a.date)
                if(!validDate(a.date) || a.date>=clock.date() || missed==0L || num("SELECT COUNT(*) FROM rest_days WHERE date=?",a.date)>0)
                    ActionResult(false,AppText.text(TextKey.CopyChiBaoVeMotNgayDaBoLoCoLichBatBuoc559497))
                else if(num("SELECT COUNT(*) FROM protection_uses WHERE date=? OR receipt=?",a.date,a.key)>0)ActionResult(false,AppText.text(TextKey.CopyNgayNayDaDuocBaoVe836dc8))
                else if(num("SELECT quantity FROM inventory WHERE item='protection'")<=0)ActionResult(false,AppText.text(TextKey.CopyChuaCoVeBaoVe515182))
                else {
                    exec("UPDATE inventory SET quantity=quantity-1 WHERE item='protection'")
                    exec("INSERT INTO protection_uses VALUES(?,?,?)",a.date,a.key,clock.now())
                    memory("protect:${a.date}","PROTECTION",0,AppText.text(TextKey.CopyMotNgayDuocBaoVeF0f58e),clock.now())
                    ActionResult(true,AppText.text(TextKey.CopyDaBaoVeNgay0KhongNhanDailyClearF822ac,a.date))
                }
            }
            is EngagementAction.Reflection -> {
                if(!validDate(a.week) || a.note.length>2000)ActionResult(false,AppText.text(TextKey.CopyGhiChuToiDa2000KyTuF518c7))
                else {exec("INSERT OR REPLACE INTO weekly_reflections VALUES(?,?,?)",a.week,a.note,clock.now());ActionResult(true,AppText.text(TextKey.CopyDaLuuSuyNgamTrenThietBiEba47d))}
            }
            is EngagementAction.MemoryNote -> {
                if(a.note.length>1000)ActionResult(false,AppText.text(TextKey.CopyGhiChuToiDa1000KyTuFc83f2))
                else {exec("UPDATE memory_events SET note=? WHERE id=?",a.note,a.id);ActionResult(true,AppText.text(TextKey.CopyDaLuuGhiChuCaf027))}
            }
            is EngagementAction.Reminder -> {
                val p=a.preference
                if(p.mode !in setOf("OFF","AT","BEFORE") || p.minute !in 0..1439)ActionResult(false,AppText.text(TextKey.CopyGioNhacKhongHopLeC63ad9))
                else {
                    exec("INSERT OR REPLACE INTO reminder_preferences VALUES(?,?,?,?)",p.taskId,p.mode,p.minute,if(p.followUp)1 else 0)
                    ActionResult(true,AppText.text(TextKey.CopyDaLuuNhacViec91fc70))
                }
            }
            is EngagementAction.Settings -> {
                val s=a.value
                if(s.quietStart !in 0..1439 || s.quietEnd !in 0..1439)ActionResult(false,AppText.text(TextKey.CopyGioYenLangKhongHopLe4439ed))
                else {
                    put("reminders_paused",if(s.paused)1 else 0);put("quiet_start",s.quietStart);put("quiet_end",s.quietEnd)
                    put("tree_reminders",if(s.treeReminders)1 else 0);put("reduced_motion",if(s.reducedMotion)1 else 0)
                    put("break_prompts",if(s.breakPrompts)1 else 0)
                    ActionResult(true,AppText.text(TextKey.CopyDaLuuTuyChon6ceb29))
                }
            }
        }
        if(result.success){markClock();sync()}
        result
    }
    private fun start(a:EngagementAction.StartFocus):ActionResult {
        if(a.minutes !in 5..120)return ActionResult(false,AppText.text(TextKey.CopyPhienTuDoTu5Den120PhutBd4094))
        if(num("SELECT COUNT(*) FROM focus_sessions WHERE id=?",a.key)>0)return ActionResult(false,AppText.text(TextKey.CopyPhienNayDaTonTai58b7ad))
        val task=if(a.taskId==0L)null else phase.tasks().firstOrNull{it.id==a.taskId}
        if(a.taskId!=0L && (task==null || task.config.verification!="TIMER" || !task.available || task.doneToday || task.finished))
            return ActionResult(false,AppText.text(TextKey.CopyChonViecTimerDangKhaDungE11ec8))
        rows("SELECT id FROM focus_sessions WHERE state='RUNNING'",1).forEach{control(it.string(0),"PAUSE")}
        if(task!=null) {
            val result=phase.timer(task.id,"START")
            if(!result.success)return result
        }
        val saved=if(task==null)null else rows("SELECT seconds,required,date FROM sessions WHERE task_id=? ORDER BY date DESC LIMIT 1",3,task.id).firstOrNull()
        exec("INSERT INTO focus_sessions(id,task_id,title,date,target,seconds,state,tick,at,zone) VALUES(?,?,?,?,?,?,'RUNNING',?,?,?)",
            a.key,a.taskId,task?.title ?: AppText.text(TextKey.CopyTapTrungTuDo7ed36c),saved?.string(2) ?: clock.date(),saved?.long(1) ?: a.minutes*60L,saved?.long(0) ?: 0,clock.elapsed(),clock.now(),clock.zone())
        return ActionResult(true,AppText.text(TextKey.CopyBatDauTapTrungD198d9))
    }
    private fun control(id:String,action:String):ActionResult {
        if(action=="RECOVER") {
            rows("SELECT task_id FROM focus_sessions WHERE state='RUNNING' AND task_id<>0",1).forEach{phase.timer(it.long(0),"PAUSE")}
            exec("UPDATE focus_sessions SET state='PAUSED',tick=0 WHERE state='RUNNING'")
            return ActionResult(true,AppText.text(TextKey.CopyDaKhoiPhucPhienDangTamDungA70ac6))
        }
        val r=rows("SELECT task_id,title,date,target,seconds,state,tick,zone,break_shown FROM focus_sessions WHERE id=?",9,id).firstOrNull()
            ?: return ActionResult(false,AppText.text(TextKey.CopyKhongTimThayPhienE1d131))
        if(r.string(5) in setOf("COMPLETE","ENDED"))return ActionResult(false,AppText.text(TextKey.CopyPhienDaKetThucDa3bc0))
        val taskId=r.long(0)
        if(action=="RESUME") {
            if(r.string(7)!=clock.zone() || taskId==0L && r.string(2)!=clock.date())return ActionResult(false,AppText.text(TextKey.CopyNgayHoacMuiGioThayDoiBatDauPhienMoiAd49b0))
            if(taskId!=0L){val result=phase.timer(taskId,"START");if(!result.success)return result}
            exec("UPDATE focus_sessions SET state='RUNNING',tick=? WHERE id=?",clock.elapsed(),id)
            return ActionResult(true,AppText.text(TextKey.CopyTiepTuc87a7ae))
        }
        var seconds=r.long(4)
        if(r.string(5)=="RUNNING") {
            if(taskId!=0L) {
                phase.timer(taskId,"TICK")
                seconds=num("SELECT seconds FROM sessions WHERE task_id=? AND date=?",taskId,r.string(2))
            } else if(r.string(7)==clock.zone() && r.string(2)==clock.date())
                seconds=(seconds+((clock.elapsed()-r.long(6))/1000).coerceIn(0,2)).coerceAtMost(r.long(3))
            val delta=(seconds-r.long(4)).coerceAtLeast(0)
            if(delta>0)exec("INSERT INTO focus_intervals(session_id,started,ended,seconds) VALUES(?,?,?,?)",
                id,clock.now()-delta*1000,clock.now(),delta)
        }
        var state=if(action=="END")"ENDED" else if(action=="PAUSE")"PAUSED" else r.string(5)
        if(seconds>=r.long(3)) {
            state="COMPLETE"
            if(taskId!=0L)phase.complete(taskId) // Existing unique task receipt owns the only possible reward.
            memory("focus:$id","FOCUS",taskId,AppText.text(TextKey.CopyMotPhienTapTrungTronVen6970b8),clock.now())
        } else if(action!="END" && (r.string(7)!=clock.zone() || taskId==0L && r.string(2)!=clock.date() ||
            taskId!=0L && num("SELECT running FROM sessions WHERE task_id=? AND date=?",taskId,r.string(2))==0L))state="PAUSED"
        else if(action!="END" && settings().breakPrompts && seconds>=1500 && r.int(8)==0) {
            state="BREAK";exec("UPDATE focus_sessions SET break_shown=1 WHERE id=?",id)
        }
        if(state!="RUNNING" && taskId!=0L)phase.timer(taskId,"PAUSE")
        exec("UPDATE focus_sessions SET seconds=?,state=?,tick=? WHERE id=?",seconds,state,if(state=="RUNNING")clock.elapsed() else 0,id)
        return ActionResult(true,when(state){"COMPLETE"->AppText.text(TextKey.CopyDaLuuPhienTapTrung2df5d3);"ENDED"->AppText.text(TextKey.CopyDaLuuPhanThoiGianDaLam1eecbe);"BREAK"->AppText.text(TextKey.CopyNghiMotChutKhiBanMuonD17dee);else->AppText.text(TextKey.CopyDaCapNhatPhien31f373)})
    }
    private fun successDays()=num("SELECT COUNT(*) FROM plans p WHERE EXISTS(SELECT 1 FROM obligations o WHERE o.date=p.date AND waived=0) AND NOT EXISTS(SELECT 1 FROM obligations o WHERE o.date=p.date AND waived=0 AND NOT EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date))").toInt()
    private fun sync():List<Achievement> {
        val first=rows("SELECT task_id,title,at FROM occurrences ORDER BY at,task_id LIMIT 1",3).firstOrNull()
        if(first!=null)memory("first-task","TASK",first.long(0),AppText.text(TextKey.CopyViecDauTien0Dd1cd5,first.string(1)),first.long(2))
        val trees=rows("SELECT id,species_id,name,birth_at,matured_at,life_state FROM trees ORDER BY id",6)
        trees.firstOrNull()?.let{memory("first-tree","TREE",it.long(0),AppText.text(TextKey.CopyMamCayDauTienE1297d),it.long(3))}
        trees.groupBy{it.string(1)}.forEach{(species,values)->values.first().let{memory("species:$species","TREE",it.long(0),AppText.text(TextKey.CopyLoaiCayMoi00d5d82,it.string(2)),it.long(3))}}
        trees.filter{it.long(4)>0}.forEach{memory("mature:${it.long(0)}","TREE",it.long(0),AppText.text(TextKey.CopyCayTruongThanh0D56fbb,it.string(2)),it.long(4))}
        rows("SELECT id,task_id,at FROM focus_sessions ORDER BY at LIMIT 1",3).firstOrNull()?.let{memory("first-focus","FOCUS",it.long(1),AppText.text(TextKey.CopyPhienTapTrungDauTien951e5b),it.long(2))}
        rows("SELECT id,entity,at FROM wallet WHERE type='MILESTONE'",3).forEach {
            memory("streak:${it.string(0)}","STREAK",0,AppText.text(TextKey.CopyCotMoc0Ngay4c76f2,it.string(1)),it.long(2))
        }
        return badges(first!=null,trees)
    }
    private fun badges(first:Boolean,trees:List<GameSqlRow>):List<Achievement> {
        val good=successDays()
        val waterDays=rows("SELECT at FROM care_events WHERE type='WATER' OR type='WATER_ALL'",1).map{localDate(it.long(0))}.toSet().size
        val flowering=setOf("cherry","lavender","cactus")
        val mature=trees.filter{it.long(4)>0}
        val specs=listOf(
            Achievement("first-step",AppText.text(TextKey.CopyBuocDauTien5dbfda),AppText.text(TextKey.CopyHoanThanhMotViecHopLe912065),if(first)1 else 0,1),
            Achievement("three-days",AppText.text(TextKey.CopyBaNgayTotLanhD6a627),AppText.text(TextKey.Copy3NgayCoLichHoanThanh0527b6),good,3),
            Achievement("week-care",AppText.text(TextKey.CopyMotTuanDeuDanD7d5ad),AppText.text(TextKey.Copy7NgayCoLichHoanThanh86a291),good,7),
            Achievement("focus25",AppText.text(TextKey.CopyKhoiDauTapTrungC3b5b9),AppText.text(TextKey.CopyHoanThanhMotPhienItNhat25Phut6bcfa2),num("SELECT COUNT(*) FROM focus_sessions s WHERE state='COMPLETE' AND (SELECT COALESCE(SUM(seconds),0) FROM focus_intervals i WHERE i.session_id=s.id)>=1500").toInt(),1),
            Achievement("study10",AppText.text(TextKey.CopyNhipHocTapF10229),AppText.text(TextKey.Copy10LanHoanThanhViecHocDcd041),num("SELECT COUNT(*) FROM occurrences WHERE category='Học tập'").toInt(),10),
            Achievement("green-thumb",AppText.text(TextKey.CopyBanTayXanh9bd1dc),AppText.text(TextKey.CopyTuoiCayTrong10NgayKhacNhau786a07),waterDays,10),
            Achievement("first-bloom",AppText.text(TextKey.CopyBongHoaDauTien4d67d1),AppText.text(TextKey.CopyMotLoaiCoHoaDatGiaiDoanCuoi27bd13),mature.count{it.string(1) in flowering},1),
            Achievement("first-mature",AppText.text(TextKey.CopyCayTruongThanhDauTienF9a6e3),AppText.text(TextKey.CopyMotLoaiKhongCoHoaDatGiaiDoanCuoi149d7d),mature.count{it.string(1) !in flowering},1),
            Achievement("collector",AppText.text(TextKey.CopyGocVuonDaDang8a3813),AppText.text(TextKey.CopyDangSoHuu3LoaiKhacNhauA27bf0),num("SELECT COUNT(DISTINCT species_id) FROM trees WHERE disposition=''").toInt(),3),
            Achievement("steady20",AppText.text(TextKey.CopyThangDeuDanD8b460),AppText.text(TextKey.Copy20NgayCoLichHoanThanh4b7346),good,20)
        )
        return specs.map{a->
            if(a.progress>=a.target) {
                exec("INSERT OR IGNORE INTO achievement_unlocks VALUES(?,'local',?,?)",a.id,clock.now(),achievementSource(a.id))
                val at=num("SELECT at FROM achievement_unlocks WHERE id=?",a.id)
                memory("achievement:${a.id}","ACHIEVEMENT",0,a.title,at)
            }
            a.copy(unlockedAt=num("SELECT at FROM achievement_unlocks WHERE id=?",a.id))
        }
    }
    private fun achievementSource(id:String):String = when(id) {
        "first-step","study10" -> rows("SELECT task_id,date FROM occurrences ORDER BY at DESC,task_id DESC LIMIT 1",2).firstOrNull()?.let{"occurrence:${it.long(0)}:${it.string(1)}"}
        "focus25" -> rows("SELECT id FROM focus_sessions WHERE state='COMPLETE' ORDER BY at DESC LIMIT 1",1).firstOrNull()?.let{"focus:${it.string(0)}"}
        "green-thumb" -> rows("SELECT id FROM care_events WHERE type='WATER' ORDER BY at DESC LIMIT 1",1).firstOrNull()?.let{"care:${it.long(0)}"}
        "first-bloom","first-mature","collector" -> rows("SELECT id FROM trees ORDER BY id",1).joinToString(","){"tree:${it.long(0)}"}
        else -> rows("SELECT date FROM plans ORDER BY date DESC LIMIT 1",1).firstOrNull()?.let{"plan:${it.string(0)}"}
    } ?: "saved-history:$id"
    private fun dayState(date:String,taskId:Long=0):String {
        if(num("SELECT COUNT(*) FROM rest_days WHERE date=?",date)>0)return "REST"
        if(num("SELECT COUNT(*) FROM protection_uses WHERE date=?",date)>0)return "PROTECTED"
        val required=if(taskId==0L)num("SELECT COUNT(*) FROM obligations WHERE date=? AND waived=0",date)
            else num("SELECT COUNT(*) FROM obligations WHERE date=? AND task_id=? AND waived=0",date,taskId)
        val done=if(taskId==0L)num("SELECT COUNT(*) FROM occurrences WHERE date=?",date)
            else num("SELECT COUNT(*) FROM occurrences WHERE date=? AND task_id=?",date,taskId)
        if(taskId!=0L && done>0)return "SUCCESS"
        if(required==0L)return "NONE"
        val missing=if(taskId==0L)num("SELECT COUNT(*) FROM obligations o WHERE date=? AND waived=0 AND NOT EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",date)
            else if(done>0)0 else 1
        return if(missing==0L)"SUCCESS" else if(date==clock.date())"PENDING" else "MISSED"
    }
    private fun focusSeconds(start:Long,end:Long,taskId:Long?=null):Long {
        val timed=if(taskId==null)num("SELECT COALESCE(SUM(seconds),0) FROM timer_intervals WHERE ended>=? AND ended<?",start,end)
            else num("SELECT COALESCE(SUM(i.seconds),0) FROM timer_intervals i JOIN sessions s ON s.id=i.session_id WHERE s.task_id=? AND i.ended>=? AND i.ended<?",taskId,start,end)
        val free=if(taskId!=null)0 else num("SELECT COALESCE(SUM(i.seconds),0) FROM focus_intervals i JOIN focus_sessions s ON s.id=i.session_id WHERE s.task_id=0 AND i.ended>=? AND i.ended<?",start,end)
        return timed+free
    }
    fun snapshot():EngagementSnapshot=db.transaction {
        phase.snapshot()
        val achievements=sync()
        rows("SELECT tree_id,kind,at,name FROM tree_dispositions",4).forEach{memory("disposition:${it.long(0)}","TREE_HISTORY",it.long(0),"${it.string(1)} · ${it.string(3)}",it.long(2))}
        val today=clock.date()
        val weekday=num("SELECT CAST(strftime('%w',?) AS INTEGER)",today).toInt()
        val start=offset(today,-((weekday+6)%7));val previous=offset(start,-7)
        val scheduled=num("SELECT COUNT(*) FROM obligations WHERE date>=? AND date<? AND waived=0",start,offset(today,1)).toInt()
        val completed=num("SELECT COUNT(*) FROM obligations o WHERE date>=? AND date<? AND waived=0 AND EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",start,offset(today,1)).toInt()
        val priorScheduled=num("SELECT COUNT(*) FROM obligations WHERE date>=? AND date<? AND waived=0",previous,start)
        val priorDone=num("SELECT COUNT(*) FROM obligations o WHERE date>=? AND date<? AND waived=0 AND EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date)",previous,start)
        val lo=clock.localMinute(start,0);val hi=clock.localMinute(offset(today,1),0)
        val categories=rows("SELECT o.category,COUNT(*),SUM(CASE WHEN EXISTS(SELECT 1 FROM occurrences x WHERE x.task_id=o.task_id AND x.date=o.date) THEN 1 ELSE 0 END) FROM obligations o WHERE date>=? AND date<? AND waived=0 GROUP BY category",3,start,offset(today,1))
        val review=WeeklyReview(start,scheduled,completed,if(priorScheduled==0L)null else (priorDone*100/priorScheduled).toInt(),
            focusSeconds(lo,hi)/60,num("SELECT COALESCE(SUM(amount),0) FROM wallet WHERE at>=? AND at<? AND amount>0 AND type<>'MIGRATION'",lo,hi).toInt(),
            -num("SELECT COALESCE(SUM(amount),0) FROM wallet WHERE at>=? AND at<? AND amount<0",lo,hi).toInt(),
            rows("SELECT at FROM care_events WHERE at>=? AND at<? AND type IN ('WATER','WATER_ALL')",1,lo,hi).map{localDate(it.long(0))}.toSet().size,
            num("SELECT COUNT(*) FROM memory_events WHERE type='TREE' AND id LIKE 'mature:%' AND at>=? AND at<?",lo,hi).toInt(),
            categories.filter{it.int(1)>0}.maxByOrNull{it.long(2).toDouble()/it.long(1)}?.string(0).orEmpty(),
            categories.filter{it.int(2)<it.int(1)}.minByOrNull{it.long(2).toDouble()/it.long(1)}?.string(0).orEmpty(),
            rows("SELECT note FROM weekly_reflections WHERE week=?",1,start).firstOrNull()?.string(0).orEmpty())
        val insights=phase.tasks().map{task->
            val dates=(29 downTo 0).map{ReviewDay(offset(today,-it),dayState(offset(today,-it),task.id))}
            var current=0;var longest=0
            rows("SELECT date FROM obligations WHERE task_id=? UNION SELECT date FROM occurrences WHERE task_id=? ORDER BY date",1,task.id,task.id).forEach{r->
                val state=dayState(r.string(0),task.id)
                if(state=="SUCCESS"){current++;longest=maxOf(longest,current)}else if(state=="MISSED")current=0
            }
            val counted=dates.count{it.state in setOf("SUCCESS","MISSED","PENDING")}
            val done=dates.count{it.state=="SUCCESS"}
            val times=rows("SELECT at FROM occurrences WHERE task_id=? AND at>0 AND date>=?",1,task.id,offset(today,-29))
            val hours=times.groupingBy{((it.long(0)-clock.localMinute(localDate(it.long(0)),0))/3600000).toInt().coerceIn(0,23)}.eachCount()
            TaskInsight(task.id,task.title,dates,if(counted==0)null else done*100/counted,current,longest,
                hours.maxByOrNull{it.value}?.key?.let{"${it.toString().padStart(2,'0')}:00"}.orEmpty(),
                focusSeconds(clock.localMinute(offset(today,-29),0),hi,task.id)/60,
                if(task.config.frequency=="EVERY_DAY" && dates.count{it.state=="MISSED"}>=3)AppText.text(TextKey.CopyBanCoMuonChuyenSangCacNgayTrongTuan02f638) else "")
        }
        EngagementSnapshot(meta("onboarded","1")=="1",
            rows("SELECT id,task_id,title,date,target,seconds,state FROM focus_sessions ORDER BY at DESC LIMIT 50",7).map{FocusSession(it.string(0),it.long(1),it.string(2),it.string(3),it.long(4),it.long(5),it.string(6),it.string(6)=="BREAK")},
            review,achievements,rows("SELECT id,type,entity,title,at,note FROM memory_events ORDER BY at DESC,id DESC LIMIT 200",6).map{MemoryEvent(it.string(0),it.string(1),it.long(2),it.string(3),it.long(4),it.string(5))},
            (29 downTo 0).map{ReviewDay(offset(today,-it),dayState(offset(today,-it)))},insights,settings(),
            rows("SELECT task_id,mode,minute,follow_up FROM reminder_preferences",4).map{ReminderPreference(it.long(0),it.string(1),it.int(2),it.int(3)==1)},
            rows("SELECT template_id FROM template_copies",1).map{it.string(0)}.toSet(),
            num("SELECT quantity FROM inventory WHERE item='protection'").toInt(),offset(today,1),
            num("SELECT COUNT(*) FROM focus_sessions WHERE state='COMPLETE'").toInt())
    }
    fun reminders():List<ReminderDelivery> = db.transaction {
        if(settings().paused)return@transaction emptyList()
        val now=clock.now();val deliveries=mutableListOf<ReminderDelivery>()
        val prefs=rows("SELECT task_id,mode,minute,follow_up FROM reminder_preferences WHERE mode<>'OFF'",4)
        val tasks=phase.tasks()
        prefs.forEach{p->
            val task=tasks.firstOrNull{it.id==p.long(0) && it.lifecycle=="ACTIVE" && !it.finished} ?: return@forEach
            var chosen=false
            for(i in 0..7) {
                val date=offset(clock.date(),i);val c=task.config
                val day=num("SELECT CAST(strftime('%w',?) AS INTEGER)",date).toInt()
                val scheduled=when(c.frequency){"WEEKDAYS"->day in 1..5;"WEEKENDS"->day in setOf(0,6);"CUSTOM"->day.toString() in c.days;else->true}
                if(!scheduled || num("SELECT COUNT(*) FROM occurrences WHERE task_id=? AND date=?",task.id,date)>0 ||
                    num("SELECT COUNT(*) FROM rest_days WHERE date=?",date)>0)continue
                val base=if(p.string(1)=="BEFORE" && c.startMinute>=0)clock.localMinute(date,c.startMinute)-15*60000 else clock.localMinute(date,p.int(2))
                listOf("PRIMARY" to base,"FOLLOWUP" to base+3600000).forEach{(kind,at)->
                    val end=if(c.startMinute<0)Long.MAX_VALUE else clock.localMinute(if(c.endMinute<c.startMinute)offset(date,1) else date,c.endMinute)
                    val start=if(c.startMinute<0)0L else clock.localMinute(date,c.startMinute)
                    if(at>now && !quiet(at) && (kind=="PRIMARY" && (p.string(1)=="BEFORE" || at>=start && at<end) || p.int(3)==1 && kind=="FOLLOWUP" && at>=start && at<end) &&
                        num("SELECT COUNT(*) FROM reminder_deliveries WHERE task_id=? AND date=? AND kind=?",task.id,date,kind)==0L) {
                        deliveries+=ReminderDelivery(task.id,date,kind,at,task.title,c.verification=="TIMER");chosen=true
                    }
                }
                if(chosen)break
            }
        }
        if(settings().treeReminders)rows("SELECT id,name,water_until FROM trees WHERE disposition='' AND life_state<>'DEAD' AND water_until>0",3).forEach{r->
            val kind="WATER:${r.long(2)}";val at=maxOf(now+1000,r.long(2))
            if(!quiet(at) && num("SELECT COUNT(*) FROM reminder_deliveries WHERE task_id=? AND kind=?",-r.long(0),kind)==0L)
                deliveries+=ReminderDelivery(-r.long(0),localDate(at),kind,at,AppText.text(TextKey.CopyTuoiCay082b107,r.string(1)),false)
        }
        deliveries
    }
    fun claimReminder(id:Long,date:String,kind:String):Boolean=db.transaction {
        if(settings().paused || quiet(clock.now()) || num("SELECT COUNT(*) FROM reminder_deliveries WHERE task_id=? AND date=? AND kind=?",id,date,kind)>0)return@transaction false
        if(id<0) {
            if(!settings().treeReminders || num("SELECT COUNT(*) FROM trees WHERE id=? AND disposition='' AND life_state<>'DEAD' AND water_until<=? AND water_until>0",-id,clock.now())==0L)return@transaction false
        } else {
            val p=rows("SELECT mode,follow_up FROM reminder_preferences WHERE task_id=?",2,id).firstOrNull() ?: return@transaction false
            val task=phase.tasks().firstOrNull{it.id==id} ?: return@transaction false
            if(p.string(0)=="OFF" || kind=="FOLLOWUP" && p.int(1)==0 || task.lifecycle!="ACTIVE" || task.finished ||
                num("SELECT COUNT(*) FROM occurrences WHERE task_id=? AND date=?",id,date)>0 ||
                num("SELECT COUNT(*) FROM rest_days WHERE date=?",date)>0)return@transaction false
            // Revalidate current preferences/schedule after delayed delivery, reboot or edits.
            val c=task.config
            val weekday=num("SELECT CAST(strftime('%w',?) AS INTEGER)",date).toInt()
            val scheduled=when(c.frequency){"WEEKDAYS"->weekday in 1..5;"WEEKENDS"->weekday in setOf(0,6);
                "CUSTOM"->weekday.toString() in c.days;else->true}
            if(!scheduled)return@transaction false
            val pref=rows("SELECT minute FROM reminder_preferences WHERE task_id=?",1,id).first()
            val primary=if(p.string(0)=="BEFORE" && c.startMinute>=0)clock.localMinute(date,c.startMinute)-900000
                else clock.localMinute(date,pref.int(0))
            val expected=primary+if(kind=="FOLLOWUP")3600000 else 0
            if(clock.now()<expected)return@transaction false
            val end=if(c.startMinute<0)clock.localMinute(offset(date,1),0)
                else clock.localMinute(if(c.endMinute<c.startMinute)offset(date,1) else date,c.endMinute)
            if(clock.now()>=end || clock.now()<minOf(clock.localMinute(date,0),primary))return@transaction false
        }
        exec("INSERT INTO reminder_deliveries VALUES(?,?,?,?)",id,date,kind,clock.now());true
    }
}
