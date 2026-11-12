package com.mamxanh.shared.data.sql

import com.mamxanh.shared.model.*
import com.mamxanh.shared.time.*

/** All cosmetic writes share the same serialized SQLite transaction as inventory and the wallet. */
class LivingGardenEngine(private val db:GameSqlConnection,private val clock:GameClock=SystemGameClock()) {
    private fun rows(s:String,n:Int,vararg a:Any)=db.query(s,n,a.map(Any::toString))
    private fun exec(s:String,vararg a:Any?)=db.execute(s,a.toList())
    private fun num(s:String,vararg a:Any)=rows(s,1,*a).firstOrNull()?.long(0) ?: 0L
    private fun meta(k:String,f:String="")=rows("SELECT v FROM meta WHERE k=?",1,k).firstOrNull()?.string(0) ?: f
    private fun put(k:String,v:Any)=exec("INSERT OR REPLACE INTO meta VALUES(?,?)",k,v.toString())
    private val phase=PhaseOneEngine(db,clock)
    private val life=TreeLifecycleEngine(db,clock)
    private fun version()=meta("garden_layout_version","0").toLong()
    private fun bump(){put("garden_layout_version",version()+1)}
    private fun memory(id:String,title:String,entity:Long=0,at:Long=clock.now()) {
        exec("INSERT OR IGNORE INTO memory_events(id,type,entity,title,at) VALUES(?,'GARDEN',?,?,?)",id,entity,title,at)
    }
    private fun own(sku:String)=num("SELECT quantity FROM inventory WHERE item=?",sku).toInt()
    private fun appearance()=GardenAppearance(meta("garden_nickname","Vườn bình yên"),
        meta("garden_created","0").toLong(),meta("garden_theme","MEADOW"),meta("garden_density","OFF"),
        meta("garden_battery","0")=="1",meta("garden_muted","1")=="1")
    private fun placements()=rows("SELECT id,sku,plot_id,tree_id FROM garden_layout ORDER BY id",4).map {
        GardenPlacement(it.string(0),it.string(1),it.int(2),it.long(3))
    }
    private fun sync() {
        val living=life.trees()
        // Dispositions retain tree rows, so observed history is independent of plot occupancy.
        val all=rows("SELECT id,species_id,birth_at,matured_at,growth_units,disposition FROM trees ORDER BY id",6)
        if(meta("garden_created").isBlank())put("garden_created",all.map{it.long(2)}.filter{it>0}.minOrNull() ?: clock.now())
        for(s in catalog) {
            val individuals=all.filter{it.string(1)==s.id}
            val active=living.filter{it.speciesId==s.id}
            val stage=maxOf(active.maxOfOrNull{it.status.stageIndex} ?: -1,
                individuals.maxOfOrNull{tree->
                    // Reconstruct pre-Phase4 dispositions from saved effort, never unlock a final form without maturity.
                    s.profile.stageThresholds.indexOfLast{it<=tree.long(4)}.coerceAtMost(if(tree.long(3)>0)s.formCount-1 else s.formCount-2)
                } ?: -1)
            val planted=individuals.map{it.long(2)}.filter{it>0}.minOrNull() ?: 0
            val mature=individuals.map{it.long(3)}.filter{it>0}.minOrNull() ?: 0
            val discovered=if(individuals.isNotEmpty() || own(s.id)>0)clock.now() else 0L
            exec("INSERT OR IGNORE INTO species_records(id) VALUES(?)",s.id)
            exec("UPDATE species_records SET discovered_at=CASE WHEN discovered_at=0 THEN ? ELSE discovered_at END,highest_stage=MAX(highest_stage,?),first_planted=CASE WHEN first_planted=0 THEN ? ELSE first_planted END,first_matured=CASE WHEN first_matured=0 THEN ? ELSE first_matured END,flourished=MAX(flourished,?) WHERE id=?",
                discovered,stage,planted,mature,if(mature>0 && s.id in setOf("cherry","cactus","lavender"))1 else 0,s.id)
        }
        // Disposed trees return accessories to the owned pool; no inventory/coin mutation.
        val gone=placements().filter{it.treeId!=0L && living.none{tree->tree.id==it.treeId && tree.status.alive}}
        if(gone.isNotEmpty()) {
            gone.forEach{exec("DELETE FROM garden_layout WHERE id=?",it.id);put("decoration_${it.treeId}","NONE")}
            bump()
        }
        val goals=goals()
        goals.filter{it.progress>=it.target && it.unlockedAt==0L}.forEach { goal->
            exec("INSERT OR IGNORE INTO collection_rewards VALUES(?,?,?)",goal.id,clock.now(),goal.reward)
            if(goal.reward.isNotBlank())exec("INSERT OR IGNORE INTO inventory VALUES(?,1)",goal.reward)
            memory("collection:${goal.id}","Bộ sưu tầm · ${goal.title}")
        }
    }
    private fun goals():List<CollectionGoal> {
        val distinct=num("SELECT COUNT(*) FROM species_records WHERE first_planted>0").toInt()
        val mature=num("SELECT COUNT(*) FROM species_records WHERE first_matured>0").toInt()
        val bloom=num("SELECT COUNT(*) FROM species_records WHERE flourished=1").toInt()
        val expansion=num("SELECT COUNT(*) FROM garden_sections WHERE id>1 AND unlocked=1").toInt()
        val full=rows("SELECT s.id FROM garden_sections s WHERE NOT EXISTS(SELECT 1 FROM garden_plots p WHERE p.id BETWEEN (s.id-1)*6+1 AND s.id*6 AND (p.unlocked=0 OR p.tree_id IS NULL)) AND (SELECT COUNT(DISTINCT t.species_id) FROM garden_plots p JOIN trees t ON t.id=p.tree_id WHERE p.id BETWEEN (s.id-1)*6+1 AND s.id*6 AND t.disposition='' AND t.life_state<>'DEAD')=6",1).size
        return listOf(
            CollectionGoal("collector","Trồng 3 loài",distinct,3,""),
            CollectionGoal("mature3","Nuôi 3 loài trưởng thành",mature,3,"reward_sign"),
            CollectionGoal("bloom","Cây hoa đầu tiên nở rộ",bloom,1,"reward_petals"),
            CollectionGoal("expansion","Mở khu vườn mới",expansion,1,"reward_path"),
            CollectionGoal("section","Một khu với 6 loài khác nhau",full,1,"reward_plaque")
        ).map{it.copy(unlockedAt=num("SELECT at FROM collection_rewards WHERE id=?",it.id))}
    }
    fun snapshot():LivingGardenSnapshot=db.transaction {
        sync()
        val inventory=rows("SELECT item,quantity FROM inventory",2).associate{it.string(0) to it.int(1)}
        val living=life.trees()
        val records=rows("SELECT id,discovered_at,highest_stage,first_planted,first_matured,flourished FROM species_records",6).associateBy{it.string(0)}
        val species=catalog.map{s->
            val r=records[s.id]!!
            SpeciesRecord(s.id,r.long(1),r.int(2),num("SELECT COUNT(*) FROM trees WHERE species_id=?",s.id).toInt(),
                r.long(3),r.long(4),inventory[s.id] ?: 0,living.count{it.speciesId==s.id && it.status.alive},
                living.filter{it.speciesId==s.id && it.status.alive}.maxOfOrNull{it.status.stageIndex} ?: -1,r.int(5)==1)
        }
        val unlocked=num("SELECT COUNT(*) FROM garden_plots WHERE unlocked=1").toInt()
        val sections=rows("SELECT id,unlocked FROM garden_sections ORDER BY id",2).map { r->
            val ids=((r.int(0)-1)*6+1..r.int(0)*6).toList()
            val missing=num("SELECT COUNT(*) FROM garden_plots WHERE id BETWEEN ? AND ? AND unlocked=0",ids.first(),ids.last()).toInt()
            GardenSection(r.int(0),ids,r.int(1)==1,(0 until missing).sumOf{Balance.plot(unlocked+it)})
        }
        val dates=num("SELECT COUNT(DISTINCT date) FROM wallet WHERE amount>0 AND date>=date(?,'-6 days') AND date<=? AND type<>'LEGACY_REWARD'",clock.date(),clock.date())
        val sum=num("SELECT COALESCE(SUM(amount),0) FROM wallet WHERE amount>0 AND date>=date(?,'-6 days') AND date<=? AND type<>'LEGACY_REWARD'",clock.date(),clock.date())
        LivingGardenSnapshot(version(),placements(),appearance(),sections,species,goals(),inventory,
            meta("coins","0").toInt(),if(dates<3)40.0 else sum/7.0,dates<3)
    }
    fun act(action:LivingGardenAction):ActionResult=db.transaction {
        sync()
        when(action) {
            is LivingGardenAction.Discover -> {
                action.ids.filter{id->catalog.any{it.id==id}}.forEach {
                    exec("UPDATE species_records SET discovered_at=CASE WHEN discovered_at=0 THEN ? ELSE discovered_at END WHERE id=?",clock.now(),it)
                };ActionResult(true,"")
            }
            is LivingGardenAction.Buy -> {
                val item=gardenCosmetics.firstOrNull{it.id==action.sku} ?: return@transaction ActionResult(false,"Không tìm thấy vật phẩm")
                if(item.tier=="Reward")return@transaction ActionResult(false,"Mở khóa qua bộ sưu tầm")
                if(num("SELECT COUNT(*) FROM garden_receipts WHERE id=?",action.key)>0)return@transaction ActionResult(false,"Giao dịch đã xử lý")
                if(item.permanent && own(item.id)>0)return@transaction ActionResult(false,"Bạn đã sở hữu")
                if(!phase.spend(action.key,"COSMETIC",item.id,item.price))return@transaction ActionResult(false,"Chưa đủ xu")
                exec("INSERT OR REPLACE INTO inventory VALUES(?,?)",item.id,own(item.id)+1)
                receipt(action.key,"BUY")
                ActionResult(true,"Đã thêm vào túi; chưa đặt trong vườn")
            }
            is LivingGardenAction.Layout -> {
                if(num("SELECT COUNT(*) FROM garden_receipts WHERE id=?",action.key)>0)return@transaction ActionResult(false,"Bản lưu đã xử lý")
                if(action.expectedVersion!=version())return@transaction ActionResult(false,"Vườn đã thay đổi. Hủy bản nháp rồi mở lại.")
                val error=validate(action.placements)
                if(error!=null)return@transaction ActionResult(false,error)
                val before=placements()
                // Unknown future entries are retained verbatim; this version may not edit them.
                if(before.filter{p->gardenCosmetics.none{it.id==p.sku}}.any{it !in action.placements})
                    return@transaction ActionResult(false,"Giữ nguyên vật phẩm từ phiên bản mới hơn")
                exec("DELETE FROM garden_layout")
                action.placements.forEach{exec("INSERT INTO garden_layout VALUES(?,?,?,?)",it.id,it.sku,it.plotId,it.treeId)}
                (before+action.placements).map{it.treeId}.filter{it!=0L}.distinct().forEach{id->
                    val sku=action.placements.firstOrNull{it.treeId==id}?.sku
                    put("decoration_$id",gardenCosmetics.firstOrNull{it.id==sku}?.art?.ifBlank{"NONE"} ?: "NONE")
                }
                bump();receipt(action.key,"LAYOUT")
                ActionResult(true,"Đã lưu khu vườn")
            }
            is LivingGardenAction.Expand -> {
                if(num("SELECT COUNT(*) FROM garden_receipts WHERE id=?",action.key)>0)return@transaction ActionResult(false,"Giao dịch đã xử lý")
                val section=rows("SELECT unlocked FROM garden_sections WHERE id=?",1,action.section).firstOrNull()
                    ?: return@transaction ActionResult(false,"Ngoài giới hạn khu vườn")
                if(section.int(0)==1 || action.section==1)return@transaction ActionResult(false,"Khu này đã mở")
                if(num("SELECT COUNT(*) FROM garden_sections WHERE id<? AND unlocked=0",action.section)>0)
                    return@transaction ActionResult(false,"Mở khu liền trước trước")
                val missing=num("SELECT COUNT(*) FROM garden_plots WHERE id BETWEEN ? AND ? AND unlocked=0",(action.section-1)*6+1,action.section*6).toInt()
                val unlocked=num("SELECT COUNT(*) FROM garden_plots WHERE unlocked=1").toInt()
                val price=(0 until missing).sumOf{Balance.plot(unlocked+it)}
                if(!phase.spend(action.key,"EXPANSION",action.section.toString(),price))return@transaction ActionResult(false,"Chưa đủ xu hoặc giao dịch đã xử lý")
                exec("UPDATE garden_sections SET unlocked=1 WHERE id=?",action.section)
                exec("UPDATE garden_plots SET unlocked=1 WHERE id BETWEEN ? AND ?",(action.section-1)*6+1,action.section*6)
                bump();receipt(action.key,"EXPANSION")
                memory("expansion:${action.section}","Mở khu vườn ${action.section}")
                sync();ActionResult(true,"Đã mở khu mới")
            }
            is LivingGardenAction.Appearance -> {
                val a=action.value
                if(a.nickname.trim().length !in 1..40 || a.density !in setOf("OFF","LOW","NORMAL") ||
                    a.theme !in setOf("MEADOW","theme_stone","theme_night"))return@transaction ActionResult(false,"Tùy chọn không hợp lệ")
                if(a.theme!="MEADOW" && own(a.theme)<1)return@transaction ActionResult(false,"Chưa sở hữu chủ đề")
                put("garden_nickname",a.nickname.trim());put("garden_theme",a.theme);put("garden_density",a.density)
                put("garden_battery",if(a.batterySaver)1 else 0);put("garden_muted",if(a.muted)1 else 0)
                ActionResult(true,"Đã lưu diện mạo; tiến trình không thay đổi")
            }
        }
    }
    private fun receipt(key:String,kind:String)=exec("INSERT INTO garden_receipts VALUES(?,?,?)",key,kind,clock.now())
    private fun validate(values:List<GardenPlacement>):String? {
        if(values.size>200 || values.any{it.id.isBlank()} || values.map{it.id}.distinct().size!=values.size)return "Bản bố trí không hợp lệ"
        val known=values.filter{p->gardenCosmetics.any{it.id==p.sku}}
        val old=placements()
        if(values.filter{it !in known}.any{it !in old})return "Vật phẩm chưa được hỗ trợ"
        for((sku,items) in known.groupBy{it.sku})if(items.size>own(sku))return "Không đủ vật phẩm sở hữu"
        val slots=mutableSetOf<String>()
        for(p in known) {
            val item=gardenCosmetics.first{it.id==p.sku}
            if(item.category=="THEME")return "Chọn chủ đề tại Diện mạo"
            if(item.slot=="AMBIENT") {
                if(p.plotId!=0 || p.treeId!=0L)return "Hiệu ứng thuộc toàn vườn"
            } else {
                if(num("SELECT COUNT(*) FROM garden_plots WHERE id=? AND unlocked=1",p.plotId)==0L)return "Ô đang khóa hoặc ngoài lưới"
                if(item.slot=="TREE") {
                    if(num("SELECT COUNT(*) FROM garden_plots p JOIN trees t ON t.id=p.tree_id WHERE p.id=? AND t.id=? AND t.disposition='' AND t.life_state<>'DEAD'",p.plotId,p.treeId)==0L)return "Phụ kiện cần cây còn sống đúng ô"
                } else if(p.treeId!=0L)return "Vật phẩm này không gắn lên cây"
            }
            if(!slots.add("${p.plotId}:${item.slot}"))return "Vị trí đã có vật phẩm cùng loại"
        }
        return null
    }
}
