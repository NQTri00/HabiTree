package com.mamxanh.app.feature.garden.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*
import kotlin.math.ceil
import kotlin.math.sin

private fun newKey()=java.util.UUID.randomUUID().toString()
private fun categoryName(s:String)=when(s){"GROUND"->"Mặt đất";"BORDER"->"Viền";"ACCENT"->"Điểm nhấn";"AMBIENT"->"Không khí";"TREE"->"Phụ kiện";"THEME"->"Chủ đề";else->s}
@Composable private fun SmallTitle(value:String){Text(value,color=Deep,fontSize=13.sp,lineHeight=17.sp,fontWeight=FontWeight.Bold)}
@Composable private fun Hint(value:String){Text(value,color=Muted,fontSize=11.sp,lineHeight=16.sp)}
@Composable private fun Action(value:String,enabled:Boolean=true,onClick:()->Unit){TextButton(onClick,enabled=enabled){Text(value,fontSize=11.sp,lineHeight=15.sp)}}

@Composable
fun LivingGardenScreen(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit,
    onSelect:(String)->Unit,onPlace:(Int,String?)->Unit,onRemove:(String)->Unit,onUndo:()->Unit,onSave:()->Unit,onCancel:()->Unit,
    onTree:(Long)->Unit,onSeedShop:()->Unit,onMemories:()->Unit,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    active:Boolean,modifier:Modifier=Modifier,onPlant:(Int)->Unit={}) {
    val title=when(state.route){"SHOP"->"Trang trí vườn";"INVENTORY"->"Trang trí đã sở hữu";"ARRANGE"->"Sắp xếp vườn";"ENCYCLOPEDIA"->"Bách thảo";"SPECIES"->"Hồ sơ loài";"APPEARANCE"->"Diện mạo";"DETAIL"->"Xem vật phẩm";else->state.snapshot.appearance.nickname}
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(TextStyle(fontSize=12.sp,lineHeight=17.sp,color=Deep)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically) {
                    Action("‹ Quay lại"){if(state.route=="ARRANGE")onCancel() else if(state.route=="STUDIO")onOpen("","") else onOpen("STUDIO","")}
                    Text(title,Modifier.weight(1f),fontSize=14.sp,lineHeight=18.sp,fontWeight=FontWeight.Bold,color=Deep)
                    Text("${state.snapshot.coins} xu",fontSize=11.sp,color=Green)
                }
                if(state.loading || state.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
                if(state.message.isNotBlank())Text(state.message,Modifier.fillMaxWidth().padding(10.dp),color=Green,fontSize=11.sp,lineHeight=15.sp)
                when(state.route) {
                    "STUDIO","ARRANGE"->GardenStudio(state,onOpen,onSelect,onPlace,onRemove,onUndo,onSave,onCancel,onTree,onPlant,reducedMotion,active)
                    "SHOP","INVENTORY"->CosmeticCatalog(state,onOpen)
                    else-> {
                        val scroll=rememberScrollState()
                        Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                            .verticalScroll(scroll).padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                            when(state.route) {
                                "EXPANSION"->{
                                    val section=state.snapshot.sections.firstOrNull{it.id==state.selected.toIntOrNull()}
                                    if(section!=null) {
                                        SmallTitle("Khu ${section.id} · 6 ô cố định")
                                        Hint("Giá ${section.price} xu · Ví ${state.snapshot.coins} xu. Cây và các ô hiện tại được giữ nguyên.")
                                        GardenPaintedButton("Xác nhận mở khu",{onAct(LivingGardenAction.Expand(section.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy && !section.unlocked && state.snapshot.coins>=section.price)
                                    }
                                }
                                "DETAIL"->CosmeticDetail(state,onOpen,onAct)
                                "ENCYCLOPEDIA"->Encyclopedia(state,onOpen)
                                "SPECIES"->SpeciesDetail(state,onTree,onSeedShop)
                                "APPEARANCE"->AppearanceContent(state,reducedMotion,onReducedMotion,onAct,onMemories)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.GardenStudio(state:LivingGardenUiState,onOpen:(String,String)->Unit,onSelect:(String)->Unit,
    onPlace:(Int,String?)->Unit,onRemove:(String)->Unit,onUndo:()->Unit,onSave:()->Unit,onCancel:()->Unit,
    onTree:(Long)->Unit,onPlant:(Int)->Unit,reducedMotion:Boolean,active:Boolean) {
    val arranging=state.route=="ARRANGE"
    val values=state.draft ?: state.snapshot.placements
    var moving by rememberSaveable{mutableStateOf<String?>(null)}
    var section by rememberSaveable{mutableIntStateOf(1)}
    val sections=state.snapshot.sections
    val current=sections.firstOrNull{it.id==section}
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),verticalAlignment=Alignment.CenterVertically) {
        if(arranging) {
            Action("Hủy",!state.busy){moving=null;onCancel()}
            Action("Hoàn tác",state.undoAvailable && !state.busy){moving=null;onUndo()}
            Action("Lưu",!state.busy,onSave)
        } else {
            Action("Sắp xếp"){onOpen("ARRANGE","")}
            Action("Bách thảo"){onOpen("ENCYCLOPEDIA","")}
            Action("Diện mạo"){onOpen("APPEARANCE","")}
            Action("Tiệm trang trí"){onOpen("SHOP","")}
        }
    }
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        sections.forEach{s->FilterChip(section==s.id,{section=s.id;moving=null},label={Text("Khu ${s.id}${if(s.unlocked)"" else " · 🔒"}",fontSize=11.sp)})}
    }
    if(arranging) {
        Hint(if(moving!=null)"Chọn ô mới cho vật phẩm. Hủy hoặc Hoàn tác không mất xu." else "Chọn vật phẩm, rồi chạm một ô hợp lệ. Mỗi ô có một chỗ cho từng loại.")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
            gardenCosmetics.filter{it.category!="THEME" && (state.snapshot.owned[it.id] ?: 0)>0}.forEach{item->
                FilterChip(state.selected==item.id && moving==null,{moving=null;onSelect(item.id)},
                    label={Text("${item.icon} ${item.name} · ${values.count{it.sku==item.id}}/${state.snapshot.owned[item.id]}",fontSize=11.sp)})
            }
            Action("Thêm trang trí"){onOpen("SHOP","")}
        }
        if(gardenCosmetics.firstOrNull{it.id==state.selected}?.slot=="AMBIENT")
            Action("Đặt cho toàn vườn"){onPlace(0,moving);moving=null}
    }
    val grid=rememberLazyGridState()
    val theme=state.snapshot.appearance.theme
    val colors=when(theme){"theme_stone"->listOf(Color(0xFFEDF0EC),Color(0xFFCED8D1));"theme_night"->listOf(Color(0xFFCEDDDC),Color(0xFF809B97));else->listOf(Color(0xFFE7F6E8),Color(0xFFC5E4C8))}
    Box(Modifier.weight(1f).fillMaxWidth().background(Brush.linearGradient(colors))) {
        LazyVerticalGrid(GridCells.Adaptive(140.dp),Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),
            state=grid,contentPadding=PaddingValues(10.dp),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            if(current!=null && !current.unlocked)item(span={GridItemSpan(maxLineSpan)}) {
                Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    SmallTitle("Khu ${current.id} · 6 ô")
                    Hint("Xem trước các ô cố định. Mở khu không di chuyển cây hiện có.")
                    if(!arranging)GardenPaintedButton("Mở khu · ${current.price} xu",{onOpen("EXPANSION",current.id.toString())},Modifier.fillMaxWidth(),enabled=!state.busy)
                }
            }
            items(state.garden.plots.filter{it.id in (current?.plots ?: emptyList())},key={it.id}) { plot->
                val tree=state.garden.trees.firstOrNull{it.id==plot.treeId}
                val placed=values.filter{it.plotId==plot.id}
                val selected=gardenCosmetics.firstOrNull{it.id==(values.firstOrNull{it.id==moving}?.sku ?: state.selected)}
                val valid=selected!=null && selected.slot!="THEME" && plot.isUnlocked && (selected?.slot!="TREE" || tree?.status?.alive==true) &&
                    placed.none{it.id!=moving && gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot==selected?.slot}
                val ground=placed.firstOrNull{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="GROUND"}
                GardenBotanicalFrame(Modifier.fillMaxWidth().combinedClickable(
                    onClick={if(arranging){onPlace(plot.id,moving);moving=null}else if(tree!=null)onTree(tree.id) else if(plot.isUnlocked)onPlant(plot.id)},
                    onLongClick={if(!arranging)onOpen("ARRANGE","")}),
                    contentPadding=PaddingValues(8.dp),cornerSize=14.dp,skin=if(arranging && valid)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
                    Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        Text("Ô ${plot.id} · ${if(!plot.isUnlocked)"Khóa" else if(arranging && valid)"Có thể đặt" else "Vườn"}",fontSize=10.sp,color=Muted)
                        Box(Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(10.dp))
                            .background(if(ground?.sku=="ground_moss")Color(0xFFB4CF9C) else if(ground!=null)Color(0xFFC8E4B4) else Color.Transparent),contentAlignment=Alignment.Center) {
                            if(tree!=null)GardenPlantPortrait(tree.speciesId,
                                placed.firstOrNull{it.treeId==tree.id}?.let{p->gardenCosmetics.firstOrNull{it.id==p.sku}?.art} ?: "NONE",
                                Modifier.fillMaxSize(),withered=!tree.status.alive)
                            else Text(if(plot.isUnlocked)"🌱" else "🔒",fontSize=28.sp)
                            if(placed.any{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="BORDER"})
                                Box(Modifier.matchParentSize().border(2.dp,Color(0xFF9B896A),RoundedCornerShape(10.dp)))
                            placed.filter{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="ACCENT"}.forEach {
                                gardenCosmetics.firstOrNull{g->g.id==it.sku}?.let{item->CosmeticArtwork(item,Modifier.align(Alignment.BottomEnd).size(30.dp))}
                            }
                        }
                        Text(tree?.name ?: "Ô trống",fontSize=12.sp,lineHeight=16.sp,color=Deep,fontWeight=FontWeight.Bold)
                        if(tree!=null) { MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth());Hint("${tree.status.percentage}% · ${tree.status.waterLabel}") }
                        placed.forEach{p->
                            val item=gardenCosmetics.firstOrNull{it.id==p.sku}
                            if(arranging) {
                                TextButton({moving=p.id;onSelect(p.sku)},Modifier.fillMaxWidth()) {Text("${if(moving==p.id)"● " else ""}${item?.icon ?: "◇"} ${item?.name ?: p.sku} · Di chuyển",fontSize=10.sp,lineHeight=13.sp)}
                                Action("Cất vào túi"){onRemove(p.id);moving=null}
                            } else Text("${item?.icon ?: "◇"} ${item?.name ?: "Vật phẩm khác"}",fontSize=10.sp,lineHeight=14.sp)
                        }
                    }
                }
            }
            item(span={GridItemSpan(maxLineSpan)}) {
                Column {
                    Hint("Nhấn giữ một ô để sắp xếp.")
                    values.filter{it.plotId==0}.forEach{p->
                        Hint("${gardenCosmetics.firstOrNull{it.id==p.sku}?.icon ?: "◇"} ${gardenCosmetics.firstOrNull{it.id==p.sku}?.name ?: p.sku} · toàn vườn")
                        if(arranging)Action("Cất hiệu ứng"){onRemove(p.id)}
                    }
                }
            }
        }
        if(active && state.snapshot.appearance.particlesEnabled(reducedMotion) && values.any{it.plotId==0})
            GardenParticles(state.snapshot.appearance.density=="LOW")
    }
}

@Composable private fun GardenParticles(low:Boolean) {
    val transition=rememberInfiniteTransition(label="garden-ambience")
    val fraction by transition.animateFloat(0f,1f,infiniteRepeatable(tween(10000,easing=LinearEasing)),label="petals")
    Canvas(Modifier.fillMaxSize()) {
        repeat(if(low)4 else 8){i->
            val y=((fraction+i*.137f)%1f)*size.height
            val x=((i*.173f+.12f)%1f)*size.width+sin((fraction+i)*6.28f)*12.dp.toPx()
            drawCircle(Color(0xFFE6E6AC).copy(alpha=.45f),3.dp.toPx(),Offset(x,y))
        }
    }
}

@Composable private fun ColumnScope.CosmeticCatalog(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    var filter by rememberSaveable{mutableStateOf("ALL")}
    Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        (listOf("ALL")+gardenCosmetics.map{it.category}.distinct()).forEach{category->
            FilterChip(filter==category,{filter=category},label={Text(if(category=="ALL")"Tất cả" else categoryName(category),fontSize=11.sp)})
        }
    }
    val grid=rememberLazyGridState()
    LazyVerticalGrid(GridCells.Adaptive(100.dp),Modifier.weight(1f).mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),state=grid,
        contentPadding=PaddingValues(10.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
        items(gardenCosmetics.filter{(filter=="ALL" || it.category==filter) &&
            (state.route!="INVENTORY" || (state.snapshot.owned[it.id] ?: 0)>0)},key={it.id}){item->
            MintCatalogCard(item.name,if((state.snapshot.owned[item.id] ?: 0)>0)"Sở hữu · ${state.snapshot.owned[item.id]}" else if(item.tier=="Reward")"Bộ sưu tầm" else "${item.price} xu",
                {onOpen("DETAIL",item.id)}){CosmeticArtwork(item,it)}
        }
    }
}

@Composable internal fun CosmeticDetail(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit) {
    val item=gardenCosmetics.firstOrNull{it.id==state.selected}
    if(item==null){Hint("Vật phẩm chưa được phiên bản này hỗ trợ.");return}
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
            CosmeticArtwork(item,Modifier.size(64.dp))
            SmallTitle(item.name)
            Hint("${categoryName(item.category)} · ${item.footprint} · ${item.tier}")
            Text("${item.price} xu",color=Green,fontWeight=FontWeight.Bold)
            Box(Modifier.fillMaxWidth().height(110.dp).background(Color(0xFFDFEBD8),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center) {
                GardenPlantPortrait("succulent",if(item.slot=="TREE")item.art else "NONE",Modifier.size(100.dp))
                if(item.slot!="TREE")CosmeticArtwork(item,Modifier.align(Alignment.BottomEnd).padding(10.dp).size(40.dp))
            }
            Hint(if(item.slot=="TREE")"Gắn vào một cây còn sống; mỗi cây một phụ kiện." else if(item.slot=="AMBIENT" || item.slot=="THEME")"Áp dụng toàn vườn; chỉ thay đổi diện mạo." else "Một vị trí ${categoryName(item.slot).lowercase()} trên ô đã mở. Không chiếm chỗ cây hoặc lối đi.")
        }
    }
    val owned=state.snapshot.owned[item.id] ?: 0
    Hint("Sở hữu $owned · Đang đặt ${state.snapshot.placements.count{it.sku==item.id}}")
    if(item.tier=="Reward")Hint("Nhận qua mục tiêu Bộ sưu tầm; không bán bằng xu.")
    else if(!item.permanent || owned==0) {
        Hint("Ước tính ${ceil((item.price-state.snapshot.coins).coerceAtLeast(0)/state.snapshot.earningRate.coerceAtLeast(1.0)).toInt()} ngày để đủ xu · ${if(state.snapshot.estimatedRate)"mốc tham khảo 40 xu/ngày" else "trung bình thu nhập 7 ngày"}. Không bảo đảm.")
        GardenPaintedButton("Mua · ${item.price} xu",{onAct(LivingGardenAction.Buy(item.id,newKey()))},Modifier.fillMaxWidth(),
            enabled=!state.busy && state.snapshot.coins>=item.price)
    }
    if(owned>0)GardenPaintedButton(if(item.slot=="THEME")"Chọn diện mạo" else "Đặt trong vườn",
        {onOpen(if(item.slot=="THEME")"APPEARANCE" else "ARRANGE",item.id)},Modifier.fillMaxWidth())
    Hint("Mua chỉ thêm vào túi. Di chuyển và cất lại không mất xu; Hủy bố trí không tiêu thụ vật phẩm.")
}

@Composable internal fun Encyclopedia(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    val records=state.snapshot.species
    SmallTitle("Khám phá ${records.count{it.discoveredAt>0}} / ${catalog.size} loài · ${records.count{it.discoveredAt>0}*100/catalog.size}%")
    Hint("Mẫu số là ${catalog.size} loài trong danh mục hiện tại. Lịch sử được giữ khi bán hoặc dọn cây.")
    catalog.forEach{s->
        val record=records.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable{onOpen("SPECIES",s.id)},contentPadding=PaddingValues(8.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                if(record.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.size(44.dp))
                else Text("🌱",Modifier.size(44.dp).wrapContentSize(),fontSize=24.sp)
                Column(Modifier.weight(1f)){SmallTitle(s.name);Hint("${s.tier} · ${record.status}")}
                Text("›")
            }
        }
    }
    SmallTitle("Mục tiêu bộ sưu tầm")
    state.snapshot.goals.forEach{goal->
        GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(8.dp)) {
            Column(verticalArrangement=Arrangement.spacedBy(3.dp)) {
                SmallTitle("${if(goal.unlockedAt>0)"✓ " else ""}${goal.title}")
                Hint("${goal.progress.coerceAtMost(goal.target)}/${goal.target} · ${gardenCosmetics.firstOrNull{it.id==goal.reward}?.name ?: "Huy hiệu bộ sưu tầm"}")
                MintProgress(goal.progress.toFloat()/goal.target,Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable internal fun SpeciesDetail(state:LivingGardenUiState,onTree:(Long)->Unit,onShop:()->Unit) {
    val s=catalog.firstOrNull{it.id==state.selected} ?: run{Hint("Loài chưa được hỗ trợ.");return}
    val r=state.snapshot.species.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
    SmallTitle(s.name);Hint("${s.tier} · ${r.status}")
    if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.fillMaxWidth().height(150.dp))
    else Text("🌱",fontSize=48.sp)
    Hint(s.description)
    SmallTitle("Bộ hình · ${s.formCount} giai đoạn")
    Hint("Các giai đoạn mở theo tiến trình đã được ghi nhận.")
    s.profile.stageThresholds.indices.toList().chunked(3).forEach{row->
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            row.forEach{index->
                Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) {
                    if(index<=r.highestStage)GardenPlantPortrait(s.id,"NONE",Modifier.size(56.dp))
                    else Text("🔒",Modifier.size(56.dp).wrapContentSize(),fontSize=22.sp)
                    Hint("Giai đoạn ${index+1}")
                }
            }
            repeat(3-row.size){Spacer(Modifier.weight(1f))}
        }
    }
    Hint("Tưới theo bình đang trang bị: 8 / 12 / 18 / 24 giờ. Phân bón chỉ dùng khi cây đang lớn và được tưới; không cộng dồn.")
    SmallTitle("Ghi chép cá nhân")
    Hint("Đã trồng ${r.planted} cây · ${r.ownedSeeds} hạt · ${r.living} cây sống")
    Hint("Trồng đầu: ${dateLabel(r.firstPlanted)} · Trưởng thành đầu: ${dateLabel(r.firstMatured)}")
    Hint(if(r.tallestStage>=0)"Cây đang sở hữu cao nhất: giai đoạn ${r.tallestStage+1}/${s.formCount}" else "Chưa có cây sống đang sở hữu")
    state.garden.trees.filter{it.speciesId==s.id}.forEach{tree->Action("Ghé ${tree.name}"){onTree(tree.id)}}
    GardenPaintedButton("Tìm hạt trong Tiệm",onShop,Modifier.fillMaxWidth(),primary=false)
}
private fun dateLabel(at:Long)=if(at<=0)"Chưa ghi nhận" else java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.getDefault()).format(java.util.Date(at))

@Composable internal fun AppearanceContent(state:LivingGardenUiState,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    onAct:(LivingGardenAction)->Unit,onMemories:()->Unit) {
    val initial=state.snapshot.appearance
    var nickname by rememberSaveable{mutableStateOf(initial.nickname)}
    var theme by rememberSaveable{mutableStateOf(initial.theme)}
    var density by rememberSaveable{mutableStateOf(initial.density)}
    var battery by rememberSaveable{mutableStateOf(initial.batterySaver)}
    SmallTitle("Hồ sơ khu vườn")
    GardenTextField(nickname,{nickname=it.take(40)},"Tên vườn")
    Hint("Tạo ngày ${dateLabel(initial.createdAt)} · ${state.garden.trees.map{it.speciesId}.distinct().size} loài đang trồng")
    Hint("${state.garden.trees.count{it.status.state==TreeGrowthState.MATURE}} cây trưởng thành sống · ${state.snapshot.placements.size} trang trí")
    SmallTitle("Chủ đề tĩnh")
    (listOf("MEADOW")+gardenCosmetics.filter{it.category=="THEME"}.map{it.id}).forEach{id->
        val owned=id=="MEADOW" || (state.snapshot.owned[id] ?: 0)>0
        FilterChip(theme==id,{theme=id},enabled=owned,label={Text(if(id=="MEADOW")"Đồng cỏ" else gardenCosmetics.first{it.id==id}.name+if(owned)"" else " · Chưa mở",fontSize=12.sp)})
    }
    SmallTitle("Không khí")
    Hint("Không thay đổi sinh trưởng hoặc giá bán. Hiệu ứng cần được đặt trong bố trí trước.")
    listOf("OFF" to "Tắt","LOW" to "Ít","NORMAL" to "Vừa").forEach{(id,name)->
        FilterChip(density==id,{density=id},label={Text(name,fontSize=12.sp)})
    }
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Tiết kiệm pin",Modifier.weight(1f));Switch(battery,{battery=it})}
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Giảm chuyển động",Modifier.weight(1f));Switch(reducedMotion,onReducedMotion)}
    Hint("Giảm chuyển động hoặc Tiết kiệm pin tắt toàn bộ hạt bay; vật phẩm và nội dung vẫn hiển thị. Hiệu ứng dừng khi rời ứng dụng.")
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Tắt âm thanh",Modifier.weight(1f));Switch(true,{},enabled=false)}
    Hint("Âm thanh mặc định tắt; chưa có bản âm thanh môi trường được cấp phép.")
    GardenPaintedButton("Lưu diện mạo",{onAct(LivingGardenAction.Appearance(initial.copy(nickname=nickname,theme=theme,density=density,batterySaver=battery,muted=true)))},
        Modifier.fillMaxWidth(),enabled=!state.busy && nickname.isNotBlank())
    Action("Khoảnh khắc vườn",onClick=onMemories)
}
