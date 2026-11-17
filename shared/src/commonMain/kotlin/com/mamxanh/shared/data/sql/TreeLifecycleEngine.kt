package com.mamxanh.shared.data.sql

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.*

/** Shared, serialized SQLite authority for tree time, resources and dispositions. */
class TreeLifecycleEngine(private val db: GameSqlConnection,private val clock: GameClock = SystemGameClock()) {
    private fun rows(sql:String,n:Int,vararg args:Any)=db.query(sql,n,args.map(Any::toString))
    private fun exec(sql:String,vararg args:Any?)=db.execute(sql,args.toList())
    private fun number(sql:String,vararg args:Any)=rows(sql,1,*args).firstOrNull()?.long(0) ?: 0L
    private fun meta(key:String,fallback:String="")=rows("SELECT v FROM meta WHERE k=?",1,key).firstOrNull()?.string(0) ?: fallback
    private fun put(key:String,value:Any)=exec("INSERT OR REPLACE INTO meta(k,v) VALUES(?,?)",key,value.toString())
    private val columns="SELECT id,species_id,name,growth,planted,lifecycle_version,birth_at,zone,sim_at,growth_units,growth_days,growth_day,day_units,water_until,last_water,fertilizer_id,fertilizer_end,last_fertilizer,neglect_ms,life_state,matured_at,death_at,free_starter,COALESCE((SELECT v FROM meta WHERE k='decoration_' || trees.id),'NONE') FROM trees"
    private data class Record(val id:Long,val species:String,val name:String,val planted:String,val decoration:String,val free:Boolean,val sim:TreeSimulation)
    fun canMutateNow():Boolean {
        val high=meta("tree_clock_high","0").toLong()
        if(clock.now()<high)return false
        put("tree_clock_high",clock.now())
        return true
    }
    private fun save(id:Long,s:TreeSimulation,profile:SpeciesLifecycleProfile) {
        exec("UPDATE trees SET lifecycle_version=3,birth_at=?,zone=?,sim_at=?,growth_units=?,growth_days=?,growth_day=?,day_units=?,water_until=?,last_water=?,fertilizer_id=?,fertilizer_end=?,last_fertilizer=?,neglect_ms=?,life_state=?,matured_at=?,death_at=?,growth=? WHERE id=?",
            s.plantedAt,s.zone,s.at,s.growthUnits,s.creditedDays,s.day,s.dayUnits,s.wateredUntil,s.lastWateredAt,
            s.fertilizerId,s.fertilizerEndsAt,s.lastFertilizedAt,s.neglectMillis,s.state.name,s.maturedAt,s.deathAt,
            (s.growthUnits*120/profile.finalUnits).toInt(),id)
    }
    private fun load(row:GameSqlRow):Record {
        val id=row.long(0);val profile=lifecycleProfile(row.string(1))
        val input=if(row.int(5)==0) {
            val progress=row.int(3).coerceIn(0,120)
            val birth=if(validDate(row.string(4)))clock.localMinute(row.string(4),0).coerceAtMost(clock.now()) else clock.now()
            TreeSimulation(birth,clock.zone(),clock.now(),
                profile.finalUnits*progress/120,profile.requiredGrowthDays*progress/120,
                treeDateAt(clock.now(),clock.zone()),0,clock.now()+8*HOUR_MS,clock.now(),
                state=if(progress==120)TreeGrowthState.MATURE else TreeGrowthState.GROWING,
                maturedAt=if(progress==120)clock.now() else 0)
        } else TreeSimulation(row.long(6),row.string(7),row.long(8),row.long(9),row.int(10),row.string(11),row.long(12),
            row.long(13),row.long(14),row.string(15),row.long(16),row.long(17),row.long(18),
            TreeGrowthState.valueOf(row.string(19)),row.long(20),row.long(21))
        val result=simulateTree(input,profile,clock.now())
        if(row.int(5)==0 || result.value!=input) {
            save(id,result.value,profile)
            result.days.forEach { exec("INSERT OR REPLACE INTO tree_growth_days VALUES(?,?,?,?)",id,it.date,it.units,if(it.credited)1 else 0) }
            if(input.state!=TreeGrowthState.DEAD && result.value.state==TreeGrowthState.DEAD)
                event(id,"WITHERED",0,0)
        }
        val high=maxOf(meta("tree_clock_high","0").toLong(),result.value.at)
        put("tree_clock_high",high)
        return Record(id,row.string(1),row.string(2),row.string(4),row.string(23),row.int(22)==1,result.value)
    }
    private fun all()=rows("$columns WHERE disposition='' ORDER BY id",24).map(::load)
    private fun get(id:Long)=rows("$columns WHERE id=? AND disposition=''",24,id).firstOrNull()?.let(::load)
    private fun safe(record:Record)=clock.now()>=record.sim.at && canMutateNow()
    private fun presentation(r:Record):Tree {
        val status=treeStatus(r.sim,lifecycleProfile(r.species),r.free)
        return Tree(r.id,r.species,r.name,(r.sim.growthUnits*120/lifecycleProfile(r.species).finalUnits).toInt(),
            r.planted,r.decoration,status.waterActive,status)
    }
    fun trees():List<Tree> = db.transaction { all().map(::presentation) }
    fun initializeNew(id:Long) {
        val row=rows("SELECT species_id FROM trees WHERE id=?",1,id).first()
        val species=row.string(0)
        val free=species==meta("starter_species","succulent") && meta("free_seed_available","0")=="1"
        if(free)put("free_seed_available",0)
        exec("UPDATE trees SET free_starter=? WHERE id=?",if(free)1 else 0,id)
        save(id,TreeSimulation(clock.now(),clock.zone(),clock.now(),day=treeDateAt(clock.now(),clock.zone())),lifecycleProfile(species))
    }
    fun equippedCan()=WateringCanTier.fromId(meta("can_equipped","can_basic"))
    fun equip(id:String):ActionResult = db.transaction {
        val can=WateringCanTier.entries.firstOrNull{it.itemId==id}
            ?: return@transaction ActionResult(false,AppText.text(TextKey.CopyKhongTimThayBinhTuoi6f9090))
        if(number("SELECT quantity FROM inventory WHERE item=?",id)<1)return@transaction ActionResult(false,AppText.text(TextKey.CopyBanChuaSoHuuBinhTuoiNayDddea8))
        put("can_equipped",id)
        ActionResult(true,AppText.text(TextKey.CopyDaTrangBi0ThoiGianTuoiMoi1Gio5b667d,can.label,can.hours))
    }
    private fun event(id:Long,type:String,resource:Int,growth:Int)=
        exec("INSERT INTO care_events(tree_id,type,at,resource,growth) VALUES(?,?,?,?,?)",id,type,clock.now(),resource,growth)
    private fun waterRecord(r:Record) {
        val updated=r.sim.copy(wateredUntil=clock.now()+equippedCan().hours*HOUR_MS,lastWateredAt=clock.now(),
            neglectMillis=(r.sim.neglectMillis-3*DAY_MS).coerceAtLeast(0))
        save(r.id,updated,lifecycleProfile(r.species))
        event(r.id,"WATER",0,0)
    }
    fun water(id:Long):TreeWateringResult = db.transaction {
        val r=get(id) ?: return@transaction TreeWateringResult.TREE_NOT_FOUND
        if(!safe(r))return@transaction TreeWateringResult.CLOCK_CHANGED
        if(r.sim.state==TreeGrowthState.DEAD)return@transaction TreeWateringResult.DEAD
        if(r.sim.wateredUntil>clock.now())return@transaction TreeWateringResult.ALREADY_WATERED
        waterRecord(r)
        TreeWateringResult.WATERED
    }
    fun waterAllQuote():WaterAllSummary = db.transaction {
        val values=all().map{treeStatus(it.sim,lifecycleProfile(it.species),it.free)}
        WaterAllSummary(values.count{it.canWater},values.count{it.waterActive},values.count{!it.alive})
    }
    fun waterAll(key:String):Pair<ActionResult,WaterAllSummary> = db.transaction {
        val values=all()
        val dead=values.count{it.sim.state==TreeGrowthState.DEAD}
        val active=values.count{it.sim.state!=TreeGrowthState.DEAD && it.sim.wateredUntil>clock.now()}
        val eligible=values.filter{it.sim.state!=TreeGrowthState.DEAD && it.sim.wateredUntil<=clock.now()}
        val quote=WaterAllSummary(eligible.size,active,dead)
        if(!canMutateNow() || values.any{clock.now()<it.sim.at})
            return@transaction ActionResult(false,AppText.text(TextKey.CopyDongHoDaLuiChuaTheTuoi304eb9)) to quote
        if(number("SELECT COUNT(*) FROM tree_actions WHERE id=?",key)>0)
            return@transaction ActionResult(false,AppText.text(TextKey.CopyLuotTuoiNayDaDuocXuLy0137a8)) to quote
        if(eligible.isEmpty())return@transaction ActionResult(false,AppText.text(TextKey.CopyKhongCoCayCanTuoiE80eed)) to quote
        eligible.forEach(::waterRecord)
        exec("INSERT INTO tree_actions VALUES(?,?,?)",key,"WATER_ALL",clock.now())
        ActionResult(true,AppText.text(TextKey.CopyDaTuoi0CayBoQua1CayDangDuocTuoiVa2CayK2a0dbb,eligible.size,active,dead)) to quote.copy(affected=eligible.size)
    }
    fun fertilize(id:Long,tierId:String):ActionResult = db.transaction {
        val r=get(id) ?: return@transaction ActionResult(false,AppText.text(TextKey.CopyKhongTimThayCay2ef08e))
        if(!safe(r))return@transaction ActionResult(false,AppText.text(TextKey.CopyDongHoDaLuiChuaTheBonPhanDf5146))
        if(r.sim.state!=TreeGrowthState.GROWING)return@transaction ActionResult(false,AppText.text(TextKey.CopyKhongDungPhanChoCayTruongThanhHoacKhoHeoC74b0f))
        if(r.sim.wateredUntil<=clock.now())return@transaction ActionResult(false,AppText.text(TextKey.CopyHayTuoiCayTruocKhiBonPhan26bd2d))
        if(r.sim.fertilizerEndsAt>clock.now())return@transaction ActionResult(false,AppText.text(TextKey.CopyCayDangCoTacDungPhanBonKhongTheCongDon1a8a0b))
        if(r.sim.lastFertilizedAt>0 && clock.now()<r.sim.lastFertilizedAt+DAY_MS)
            return@transaction ActionResult(false,AppText.text(TextKey.CopyMoiCayChiDungPhanMotLanTrong24Gio9f8cec))
        val tier=if(tierId=="AUTO")FertilizerTier.entries.firstOrNull{number("SELECT quantity FROM inventory WHERE item=?",it.itemId)>0}
            else FertilizerTier.fromId(tierId)
        if(tier==null)return@transaction ActionResult(false,AppText.text(TextKey.CopyBanChuaCoPhanBonPhuHopF65443))
        val stock=number("SELECT quantity FROM inventory WHERE item=?",tier.itemId)
        if(stock<1)return@transaction ActionResult(false,AppText.text(TextKey.CopyDaHet0Af2b3b,tier.label))
        exec("UPDATE inventory SET quantity=quantity-1 WHERE item=?",tier.itemId)
        save(id,r.sim.copy(fertilizerId=tier.itemId,fertilizerEndsAt=clock.now()+tier.hours*HOUR_MS,lastFertilizedAt=clock.now()),lifecycleProfile(r.species))
        event(id,"FERTILIZE:${tier.itemId}",-1,0)
        ActionResult(true,AppText.text(TextKey.CopyDaDung01Trong2GioKhiDuocTuoi8de41c,tier.label,tier.rate/1000.0,tier.hours))
    }
    fun saleQuote(id:Long):ActionResult = db.transaction {
        val r=get(id) ?: return@transaction ActionResult(false,AppText.text(TextKey.CopyCayKhongConTrongVuon40c7ae))
        if(r.sim.state!=TreeGrowthState.MATURE)return@transaction ActionResult(false,AppText.text(TextKey.CopyChiBanCayTruongThanhConSong9b1510))
        val nominal=if(r.free)0 else lifecycleProfile(r.species).salePrice
        val earned=number("SELECT COALESCE(SUM(amount),0) FROM wallet WHERE date=? AND amount>0 AND type<>'MIGRATION'",clock.date()).toInt()
        val granted=minOf(nominal,(Balance.DAILY_CAP-earned).coerceAtLeast(0))
        ActionResult(true,AppText.text(TextKey.CopyGia0XuThucNhan1XuHomNay197b09,nominal,granted),nominal,granted)
    }
    fun dispose(id:Long,sell:Boolean,key:String):ActionResult = db.transaction {
        val r=get(id) ?: return@transaction ActionResult(false,AppText.text(TextKey.CopyCayDaDuocBanHoacDonAc3091))
        if(!safe(r))return@transaction ActionResult(false,AppText.text(TextKey.CopyDongHoDaLuiChuaTheThucHienC3bb22))
        if(number("SELECT COUNT(*) FROM tree_dispositions WHERE action_id=? OR tree_id=?",key,id)>0)
            return@transaction ActionResult(false,AppText.text(TextKey.CopyGiaoDichDaDuocXuLy1e67a0))
        var nominal=0;var granted=0
        if(sell) {
            if(r.sim.state!=TreeGrowthState.MATURE)return@transaction ActionResult(false,AppText.text(TextKey.CopyChiBanCayTruongThanhConSong9b1510))
            nominal=if(r.free)0 else lifecycleProfile(r.species).salePrice
            val receipt=PhaseOneEngine(db,clock).income("sale:$id","TREE_SALE",id.toString(),nominal)
            if(!receipt.success)return@transaction receipt
            granted=receipt.granted
        }
        val kind=if(sell)"SOLD" else "REMOVED"
        exec("INSERT INTO tree_dispositions VALUES(?,?,?,?,?,?,?)",id,key,kind,clock.now(),r.name,nominal,granted)
        exec("UPDATE trees SET disposition=? WHERE id=?",kind,id)
        exec("UPDATE garden_plots SET tree_id=NULL WHERE tree_id=?",id)
        ActionResult(true,if(sell)AppText.text(TextKey.CopyDaBanCayNhan01XuPhanVuotGioiHanKhongDuoc5316e3,granted,nominal) else AppText.text(TextKey.CopyDaDonCayOTrongDaTrongKhongHoanXuHoacHat4e6810),nominal,granted)
    }
    fun dispositions():List<TreeDispositionEvent> = rows("SELECT tree_id,name,kind,at,nominal,granted FROM tree_dispositions ORDER BY at DESC",6).map {
        TreeDispositionEvent(it.long(0),it.string(1),it.string(2),it.long(3),it.int(4),it.int(5))
    }
}
