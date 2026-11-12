package com.mamxanh.app.feature.garden.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*
import kotlin.math.ceil
import kotlin.math.sin

private fun newKey()=java.util.UUID.randomUUID().toString()
private fun categoryName(s:String)=when(s){"GROUND"->"Mặt đất";"BORDER"->"Viền";"ACCENT"->"Điểm nhấn";"AMBIENT"->"Hiệu ứng";"TREE"->"Phụ kiện";"THEME"->"Chủ đề";else->s}
private fun dateLabel(at:Long)=if(at<=0)"Chưa ghi nhận" else java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.getDefault()).format(java.util.Date(at))

@Composable
fun LivingGardenScreen(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit,
    onSelect:(String)->Unit,onPlace:(Int,String?)->Unit,onRemove:(String)->Unit,onUndo:()->Unit,onSave:()->Unit,onCancel:()->Unit,
    onTree:(Long)->Unit,onSeedShop:()->Unit,onMemories:()->Unit,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    active:Boolean,modifier:Modifier=Modifier,onPlant:(Int)->Unit={}) {
    val arranging=state.route=="ARRANGE"
    val title=when(state.route){"SHOP"->"Tiệm trang trí";"INVENTORY"->"Trang trí của bạn";"ARRANGE"->"Sắp xếp";"ENCYCLOPEDIA"->"Bách thảo";"SPECIES"->"Hồ sơ loài";"APPEARANCE"->"Diện mạo";"DETAIL"->"Trang trí";"EXPANSION"->"Mở khu vườn";else->state.snapshot.appearance.nickname}
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(TextStyle(fontSize=11.sp,lineHeight=15.sp,color=Deep)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal=4.dp),verticalAlignment=Alignment.CenterVertically) {
                    IconButton({if(arranging)onCancel() else if(state.route=="STUDIO")onOpen("","") else onOpen("STUDIO","")}) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack,if(arranging)"Hủy bố trí" else "Quay lại",Modifier.size(18.dp),tint=Green)
                    }
                    Text(title,Modifier.weight(1f),color=Deep,fontSize=13.sp,lineHeight=17.sp,fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(arranging) {
                        IconButton(onUndo,enabled=state.undoAvailable && !state.busy){Icon(Icons.AutoMirrored.Rounded.Undo,"Hoàn tác",Modifier.size(18.dp),tint=if(state.undoAvailable)Green else Muted)}
                        GardenCompactAction("Lưu",onSave,primary=true,enabled=!state.busy && !state.loading,icon=Icons.Rounded.Check)
                    } else {
                        Text("${state.snapshot.coins} xu",Modifier.clip(RoundedCornerShape(9.dp))
                            .background(Color.White.copy(alpha=.7f)).padding(horizontal=8.dp,vertical=5.dp),
                            color=Green,fontSize=10.sp,lineHeight=13.sp,fontWeight=FontWeight.Bold)
                        Spacer(Modifier.width(6.dp))
                    }
                }
                if(state.loading || state.busy)LinearProgressIndicator(Modifier.fillMaxWidth().height(2.dp))
                if(state.message.isNotBlank())Text(state.message,Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=3.dp),color=Green,fontSize=10.sp,lineHeight=14.sp)
                when(state.route) {
                    "STUDIO","ARRANGE"->GardenStudio(state,onOpen,onSelect,onPlace,onRemove,onTree,onPlant,reducedMotion,active)
                    "SHOP","INVENTORY"->CosmeticCatalog(state,onOpen)
                    else->key(state.route,state.selected) {
                        val scroll=rememberScrollState()
                        Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                            .verticalScroll(scroll).padding(horizontal=10.dp,vertical=4.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                            when(state.route) {
                                "EXPANSION"->ExpansionContent(state,onAct)
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
    onPlace:(Int,String?)->Unit,onRemove:(String)->Unit,onTree:(Long)->Unit,onPlant:(Int)->Unit,
    reducedMotion:Boolean,active:Boolean) {
    val arranging=state.route=="ARRANGE"
    val values=if(arranging)state.draft ?: state.snapshot.placements else state.snapshot.placements
    var moving by rememberSaveable(arranging){mutableStateOf(
        values.firstOrNull{it.sku==state.selected}?.id.takeIf{arranging && values.count{it.sku==state.selected}>=(state.snapshot.owned[state.selected] ?: 0)})}
    var section by rememberSaveable{mutableIntStateOf(1)}
    val current=state.snapshot.sections.firstOrNull{it.id==section}
    if(!arranging)Row(Modifier.fillMaxWidth().padding(horizontal=6.dp),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        listOf("Sắp xếp" to "ARRANGE","Bách thảo" to "ENCYCLOPEDIA","Diện mạo" to "APPEARANCE","Tiệm" to "SHOP").forEach{(label,route)->
            GardenPill(label,false,{onOpen(route,"")},Modifier.weight(1f))
        }
    }
    Row(Modifier.fillMaxWidth().padding(horizontal=8.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
        state.snapshot.sections.forEach{s->
            GardenPill("Khu ${s.id}",section==s.id,{section=s.id},icon=if(s.unlocked) null else Icons.Rounded.Lock)
        }
    }
    if(arranging) {
        Row(Modifier.fillMaxWidth().padding(horizontal=8.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement=Arrangement.spacedBy(5.dp),verticalAlignment=Alignment.CenterVertically) {
            gardenCosmetics.filter{it.category!="THEME" && (state.snapshot.owned[it.id] ?: 0)>0}.forEach{item->
                val copies=values.filter{it.sku==item.id}
                val owned=state.snapshot.owned[item.id] ?: 0
                GardenPill("${item.name} ×$owned",state.selected==item.id,{
                    moving=if(copies.size>=owned)copies.firstOrNull()?.id else null
                    onSelect(item.id)
                })
            }
            GardenCompactAction("Thêm",{onOpen("SHOP","")},icon=Icons.Rounded.Add)
        }
        val copies=values.filter{it.sku==state.selected}
        if(copies.isNotEmpty())Row(Modifier.fillMaxWidth().padding(horizontal=8.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement=Arrangement.spacedBy(5.dp),verticalAlignment=Alignment.CenterVertically) {
            copies.forEach{p->GardenPill(if(p.plotId==0)"Toàn vườn" else "Ô ${p.plotId}",moving==p.id,{moving=p.id},icon=Icons.Rounded.OpenWith)}
            if(moving!=null)GardenCompactAction("Cất",{moving?.let(onRemove);moving=null},icon=Icons.Rounded.Inventory2)
            if(copies.size<(state.snapshot.owned[state.selected] ?: 0))GardenCompactAction("Đặt thêm",{moving=null},icon=Icons.Rounded.Add)
        }
        if(gardenCosmetics.firstOrNull{it.id==state.selected}?.slot=="AMBIENT")
            GardenCompactAction("Đặt cho toàn vườn",{onPlace(0,moving);moving=null},Modifier.padding(horizontal=8.dp))
        GardenCaption(if(moving!=null)"Chọn ô để di chuyển · Hủy bằng nút quay lại" else "Chọn đồ trang trí, rồi chạm ô muốn đặt",
            Modifier.padding(horizontal=12.dp,vertical=2.dp))
    }
    val grid=rememberLazyGridState()
    val colors=when(state.snapshot.appearance.theme){"theme_stone"->listOf(Color(0xFFF0F2ED),Color(0xFFDDE5DC));"theme_night"->listOf(Color(0xFFDCE9E5),Color(0xFFAFC6BF));else->listOf(Color(0xFFEAF6EB),Color(0xFFD5EBD6))}
    BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().background(MintDepth.diagonal(colors))) {
        val columns=if(maxWidth<340.dp || LocalDensity.current.fontScale>1.25f)2 else maxOf(3,(maxWidth.value/120).toInt())
        LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),
            state=grid,contentPadding=PaddingValues(8.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            if(current!=null && !current.unlocked)item(span={GridItemSpan(maxLineSpan)}) {
                GardenPanel {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)){GardenHeading("Khu ${current.id} · 6 ô");GardenCaption("${current.price} xu · Chỉ mở rộng diện tích")}
                        if(!arranging)GardenCompactAction("Xem mở khu",{onOpen("EXPANSION",current.id.toString())},icon=Icons.Rounded.Add)
                    }
                }
            }
            items(state.garden.plots.filter{it.id in (current?.plots ?: emptyList())},key={it.id}) { plot->
                val tree=state.garden.trees.firstOrNull{it.id==plot.treeId}
                val placed=values.filter{it.plotId==plot.id}
                val selected=gardenCosmetics.firstOrNull{it.id==(values.firstOrNull{it.id==moving}?.sku ?: state.selected)}
                val copyAvailable=selected!=null && (moving!=null || values.count{it.sku==selected.id}<(state.snapshot.owned[selected.id] ?: 0))
                val valid=copyAvailable && selected!=null && selected.slot!="THEME" && plot.isUnlocked &&
                    (selected.slot!="TREE" || tree?.status?.alive==true) &&
                    placed.none{it.id!=moving && gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot==selected.slot}
                val shape=RoundedCornerShape(12.dp)
                Column(Modifier.fillMaxWidth().mintDepth(shape,if(arranging && valid)MintDepth.hint else MintDepth.card,2.dp)
                    .combinedClickable(onClick={
                        if(arranging) {
                            if(selected==null && placed.isNotEmpty()){moving=placed.first().id;onSelect(placed.first().sku)}
                            else {onPlace(plot.id,moving);if(valid)moving=null}
                        } else if(tree!=null)onTree(tree.id) else if(plot.isUnlocked)onPlant(plot.id)
                    },onLongClick={
                        if(!arranging)onOpen("ARRANGE",placed.firstOrNull()?.sku ?: "")
                        else placed.firstOrNull()?.let{moving=it.id;onSelect(it.sku)}
                    }).padding(5.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        Text("Ô ${plot.id.toString().padStart(2,'0')}",fontSize=9.sp,lineHeight=12.sp,color=Muted)
                        if(!plot.isUnlocked)Icon(Icons.Rounded.Lock,null,Modifier.size(11.dp),tint=Muted)
                        else if(arranging && valid)Icon(Icons.Rounded.AddCircleOutline,null,Modifier.size(12.dp),tint=Green)
                    }
                    val ground=placed.firstOrNull{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="GROUND"}
                    Box(Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp))
                        .background(if(ground?.sku=="ground_moss")Color(0xFFC5DDB6) else if(ground!=null)Color(0xFFD2E8C2) else Color.Transparent),
                        contentAlignment=Alignment.Center) {
                        if(tree!=null)GardenPlantPortrait(tree.speciesId,placed.firstOrNull{it.treeId==tree.id}?.let{p->gardenCosmetics.firstOrNull{it.id==p.sku}?.art} ?: "NONE",
                            Modifier.fillMaxSize(),withered=!tree.status.alive)
                        else Icon(if(plot.isUnlocked)Icons.Rounded.Add else Icons.Rounded.Lock,null,Modifier.size(22.dp),tint=Green.copy(alpha=.5f))
                        if(placed.any{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="BORDER"})Box(Modifier.matchParentSize().border(1.dp,Color(0xFFA69372),RoundedCornerShape(8.dp)))
                        placed.firstOrNull{gardenCosmetics.firstOrNull{g->g.id==it.sku}?.slot=="ACCENT"}?.let{p->
                            gardenCosmetics.firstOrNull{it.id==p.sku}?.let{CosmeticArtwork(it,Modifier.align(Alignment.BottomEnd).size(19.dp))}
                        }
                    }
                    val nameHeight=with(LocalDensity.current){24.sp.toDp()}
                    Box(Modifier.fillMaxWidth().height(nameHeight),contentAlignment=Alignment.Center) {
                        Text(tree?.name ?: if(plot.isUnlocked)"Ô trống" else "Chưa mở",color=Deep,fontSize=10.sp,lineHeight=12.sp,
                            fontWeight=FontWeight.SemiBold,maxLines=2,overflow=TextOverflow.Ellipsis,textAlign=TextAlign.Center)
                    }
                    if(tree!=null)PlotProgress(tree.status.stageProgress,"${tree.status.percentage}%")
                    else Spacer(Modifier.height(with(LocalDensity.current){12.sp.toDp()}))
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(2.dp)) {
                        Text(tree?.status?.waterLabel ?: if(plot.isUnlocked)"Chạm để gieo" else "Ô đang khóa",Modifier.weight(1f),
                            color=Muted,fontSize=9.sp,lineHeight=12.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
                        placed.take(2).forEach{p->gardenCosmetics.firstOrNull{it.id==p.sku}?.let{CosmeticArtwork(it,Modifier.size(9.dp))}}
                        if(placed.isNotEmpty())Text(placed.size.toString(),fontSize=9.sp,lineHeight=12.sp,color=Green)
                    }
                }
            }
            item(span={GridItemSpan(maxLineSpan)}) {
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                    GardenCaption(if(arranging)"Bản nháp · Lưu để áp dụng" else "Nhấn giữ một ô để sắp xếp")
                    values.filter{it.plotId==0}.forEach{p->gardenCosmetics.firstOrNull{it.id==p.sku}?.let{CosmeticArtwork(it,Modifier.size(14.dp))}}
                }
            }
        }
        if(active && state.snapshot.appearance.particlesEnabled(reducedMotion) && values.any{it.plotId==0})GardenParticles(state.snapshot.appearance.density=="LOW")
    }
}

@Composable private fun PlotProgress(progress:Float,label:String?=null) {
    Box(Modifier.fillMaxWidth().height(if(label==null)4.dp else with(LocalDensity.current){12.sp.toDp()}).clip(RoundedCornerShape(6.dp)).background(Color(0xFFDCEBE1)),contentAlignment=Alignment.Center) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f,1f)).align(Alignment.CenterStart).background(MintDepth.diagonal(listOf(Color(0xFFA6D869),Color(0xFF74BA64)))))
        if(label!=null)Text(label,color=Deep,fontSize=9.sp,lineHeight=11.sp,fontWeight=FontWeight.Bold)
    }
}
@Composable private fun GardenParticles(low:Boolean) {
    val transition=rememberInfiniteTransition(label="garden-ambience")
    val fraction by transition.animateFloat(0f,1f,infiniteRepeatable(tween(10000,easing=LinearEasing)),label="petals")
    Canvas(Modifier.fillMaxSize()) {repeat(if(low)4 else 8){i->
        drawCircle(Color(0xFFE6E6AC).copy(alpha=.45f),3.dp.toPx(),Offset(((i*.173f+.12f)%1f)*size.width+sin((fraction+i)*6.28f)*12.dp.toPx(),((fraction+i*.137f)%1f)*size.height))
    }}
}

@Composable private fun ColumnScope.CosmeticCatalog(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    var filter by rememberSaveable{mutableStateOf("ALL")}
    Row(Modifier.fillMaxWidth().padding(horizontal=8.dp).horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
        (listOf("ALL")+gardenCosmetics.map{it.category}.distinct()).forEach{category->GardenPill(if(category=="ALL")"Tất cả" else categoryName(category),filter==category,{filter=category})}
    }
    val grid=rememberLazyGridState()
    BoxWithConstraints(Modifier.weight(1f)) {
        val columns=if(maxWidth<340.dp && LocalDensity.current.fontScale>1.2f)2 else maxOf(3,(maxWidth.value/120).toInt())
        LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),state=grid,
            contentPadding=PaddingValues(8.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            val items=gardenCosmetics.filter{(filter=="ALL" || it.category==filter) && (state.route!="INVENTORY" || (state.snapshot.owned[it.id] ?: 0)>0)}
            items(items,key={it.id}){item->
                SmallCatalogTile(item.name,if((state.snapshot.owned[item.id] ?: 0)>0)"Sở hữu · ${state.snapshot.owned[item.id]}" else if(item.tier=="Reward")"Quà sưu tầm" else "${item.price} xu",
                    {onOpen("DETAIL",item.id)}){CosmeticArtwork(item,it)}
            }
            if(items.isEmpty())item(span={GridItemSpan(maxLineSpan)}) {GardenPanel {GardenHeading("Chưa có trang trí");GardenCompactAction("Ghé tiệm",{onOpen("SHOP","")},icon=Icons.Rounded.Storefront)}}
        }
    }
}
@Composable private fun SmallCatalogTile(name:String,value:String,onClick:()->Unit,art:@Composable (Modifier)->Unit) {
    Column(Modifier.fillMaxWidth().mintDepth(RoundedCornerShape(12.dp),MintDepth.card,2.dp).clickable(onClick=onClick).padding(5.dp),
        horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
        art(Modifier.size(24.dp))
        val nameHeight=with(LocalDensity.current){22.sp.toDp()}
        Box(Modifier.fillMaxWidth().height(nameHeight),contentAlignment=Alignment.Center) {
            Text(name,color=Deep,fontSize=10.sp,lineHeight=11.sp,fontWeight=FontWeight.SemiBold,maxLines=2,overflow=TextOverflow.Ellipsis,textAlign=TextAlign.Center)
        }
        Text(value,color=Green,fontSize=9.sp,lineHeight=12.sp,fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
    }
}

@Composable internal fun CosmeticDetail(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit) {
    val item=gardenCosmetics.firstOrNull{it.id==state.selected} ?: run{GardenCaption("Vật phẩm chưa được hỗ trợ.");return}
    val owned=state.snapshot.owned[item.id] ?: 0
    GardenPanel {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            CosmeticArtwork(item,Modifier.size(42.dp))
            Column(Modifier.weight(1f)){GardenHeading(item.name);GardenCaption("${categoryName(item.category)} · ${item.footprint} · ${item.tier}")}
            Text(if(item.tier=="Reward")"Quà tặng" else "${item.price} xu",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().height(76.dp).background(MintDepth.diagonal(listOf(Color(0xFFEAF2E3),Color(0xFFD4E7CF))),RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center) {
            GardenPlantPortrait("succulent",if(item.slot=="TREE")item.art else "NONE",Modifier.size(70.dp))
            if(item.slot!="TREE")CosmeticArtwork(item,Modifier.align(Alignment.BottomEnd).padding(8.dp).size(26.dp))
        }
        GardenCaption(when(item.slot){"TREE"->"Một phụ kiện cho mỗi cây còn sống";"AMBIENT","THEME"->"Diện mạo toàn vườn · Chỉ trang trí";else->"Một vị trí ${categoryName(item.slot).lowercase()} trên ô đã mở"})
        Row(Modifier.fillMaxWidth()) {
            GardenFact("Đã sở hữu",owned.toString(),Modifier.weight(1f))
            GardenFact("Đang đặt",state.snapshot.placements.count{it.sku==item.id}.toString(),Modifier.weight(1f))
        }
    }
    if(item.tier=="Reward")GardenCaption("Mở khóa qua mục tiêu sưu tầm.")
    else if(!item.permanent || owned==0) {
        val days=ceil((item.price-state.snapshot.coins).coerceAtLeast(0)/state.snapshot.earningRate.coerceAtLeast(1.0)).toInt()
        GardenCaption("Ước tính $days ngày để đủ xu · ${if(state.snapshot.estimatedRate)"tham khảo 40 xu/ngày" else "theo thu nhập 7 ngày"}")
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        if(item.tier!="Reward" && (!item.permanent || owned==0))GardenCompactAction("Mua · ${item.price} xu",{onAct(LivingGardenAction.Buy(item.id,newKey()))},
            Modifier.weight(1f),primary=true,enabled=!state.busy && state.snapshot.coins>=item.price,icon=Icons.Rounded.Add)
        if(owned>0)GardenCompactAction(if(item.slot=="THEME")"Chọn chủ đề" else "Đặt trong vườn",
            {onOpen(if(item.slot=="THEME")"APPEARANCE" else "ARRANGE",item.id)},Modifier.weight(1f),primary=true,icon=Icons.Rounded.Check)
    }
    GardenCaption("Mua để cất vào túi; di chuyển không mất xu.")
}

@Composable internal fun Encyclopedia(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    val records=state.snapshot.species
    val discovered=records.count{it.discoveredAt>0}
    GardenPanel {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            GardenHeading("Bộ sưu tầm",Modifier.weight(1f));Text("$discovered/${catalog.size} · ${discovered*100/catalog.size}%",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)
        }
        PlotProgress(discovered.toFloat()/catalog.size)
        GardenCaption("${catalog.size} loài trong danh mục · Giữ lịch sử khi dọn hoặc bán cây")
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns=if(maxWidth<340.dp || LocalDensity.current.fontScale>1.25f)2 else 3
        Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
            catalog.chunked(columns).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                row.forEach{s->
                    val r=records.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
                    Box(Modifier.weight(1f)) {
                        SmallCatalogTile(s.name,r.status,{onOpen("SPECIES",s.id)}) {
                            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",it)
                            else Icon(Icons.Rounded.Spa,null,it.padding(5.dp),tint=Green.copy(alpha=.35f))
                        }
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }}
        }
    }
    GardenPanel {
        GardenHeading("Dấu mốc sưu tầm")
        state.snapshot.goals.forEachIndexed{index,goal->
            if(index>0)HorizontalDivider(color=Green.copy(alpha=.08f))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                val reward=gardenCosmetics.firstOrNull{it.id==goal.reward}
                if(reward!=null)CosmeticArtwork(reward,Modifier.size(22.dp))
                else Icon(Icons.Rounded.WorkspacePremium,null,Modifier.size(22.dp),tint=Green)
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                    Text(goal.title,color=Deep,fontSize=11.sp,lineHeight=14.sp,fontWeight=FontWeight.SemiBold)
                    GardenCaption(reward?.name ?: "Huy hiệu bộ sưu tầm")
                    PlotProgress(goal.progress.toFloat()/goal.target)
                }
                if(goal.unlockedAt>0)Icon(Icons.Rounded.CheckCircle,null,Modifier.size(15.dp),tint=Green)
                else Text("${goal.progress.coerceAtMost(goal.target)}/${goal.target}",color=Muted,fontSize=10.sp)
            }
        }
    }
}

@Composable internal fun SpeciesDetail(state:LivingGardenUiState,onTree:(Long)->Unit,onShop:()->Unit) {
    val s=catalog.firstOrNull{it.id==state.selected} ?: run{GardenCaption("Loài chưa được hỗ trợ.");return}
    val r=state.snapshot.species.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
    GardenPanel {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.size(72.dp))
            else Icon(Icons.Rounded.Spa,null,Modifier.size(54.dp),tint=Green.copy(alpha=.4f))
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {GardenHeading(s.name);GardenCaption("${s.tier} · ${r.status}");GardenCaption(s.description)}
        }
    }
    GardenPanel {
        Row(Modifier.fillMaxWidth()){GardenHeading("Các giai đoạn",Modifier.weight(1f));GardenCaption("${(r.highestStage+1).coerceAtLeast(0)}/${s.formCount}")}
        val columns=if(LocalDensity.current.fontScale>1.2f)3 else 4
        s.profile.stageThresholds.indices.toList().chunked(columns).forEach{row->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                row.forEach{index->
                    Column(Modifier.weight(1f).background(if(index<=r.highestStage)Color(0xFFEDF5E9) else Color(0xFFF0F4EF),RoundedCornerShape(9.dp))
                        .padding(vertical=4.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
                        if(index<=r.highestStage)GardenPlantPortrait(s.id,"NONE",Modifier.size(32.dp))
                        else Box(Modifier.size(32.dp),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Lock,null,Modifier.size(14.dp),tint=Muted.copy(alpha=.55f))}
                        Text((index+1).toString(),color=Muted,fontSize=9.sp,lineHeight=12.sp)
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }
        }
    }
    GardenPanel {
        GardenHeading("Ghi chép cá nhân")
        Row(Modifier.fillMaxWidth()) {
            GardenFact("Đã trồng","${r.planted} cây",Modifier.weight(1f))
            GardenFact("Trong túi","${r.ownedSeeds} hạt",Modifier.weight(1f))
            GardenFact("Đang sống","${r.living} cây",Modifier.weight(1f))
        }
        HorizontalDivider(color=Green.copy(alpha=.08f))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            GardenFact("Trồng đầu tiên",dateLabel(r.firstPlanted),Modifier.weight(1f))
            GardenFact("Trưởng thành đầu",dateLabel(r.firstMatured),Modifier.weight(1f))
        }
        GardenCaption(if(r.tallestStage>=0)"Cây cao nhất đang sở hữu · giai đoạn ${r.tallestStage+1}/${s.formCount}" else "Chưa có cây sống đang sở hữu")
    }
    GardenCaption("Bình tưới: 8 / 12 / 18 / 24 giờ · Phân bón dùng khi cây đang lớn và đã tưới; không cộng dồn.")
    FlowRow(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
        state.garden.trees.filter{it.speciesId==s.id}.forEach{tree->GardenCompactAction(tree.name,{onTree(tree.id)},icon=Icons.Rounded.Park)}
        GardenCompactAction("Tìm hạt",onShop,primary=true,icon=Icons.Rounded.Storefront)
    }
}

@Composable internal fun AppearanceContent(state:LivingGardenUiState,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    onAct:(LivingGardenAction)->Unit,onMemories:()->Unit) {
    val initial=state.snapshot.appearance
    var nickname by rememberSaveable{mutableStateOf(initial.nickname)}
    var theme by rememberSaveable{mutableStateOf(initial.theme)}
    var density by rememberSaveable{mutableStateOf(initial.density)}
    var battery by rememberSaveable{mutableStateOf(initial.batterySaver)}
    GardenPanel {
        GardenHeading("Khu vườn của bạn")
        GardenTextField(nickname,{nickname=it.take(40)},"Tên vườn")
        Row(Modifier.fillMaxWidth()) {
            GardenFact("Loài cây",state.garden.trees.map{it.speciesId}.distinct().size.toString(),Modifier.weight(1f))
            GardenFact("Trưởng thành",state.garden.trees.count{it.status.state==TreeGrowthState.MATURE}.toString(),Modifier.weight(1f))
            GardenFact("Trang trí",state.snapshot.placements.size.toString(),Modifier.weight(1f))
        }
        GardenCaption("Từ ${dateLabel(initial.createdAt)}")
    }
    GardenPanel {
        GardenHeading("Chủ đề")
        FlowRow(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            (listOf("MEADOW")+gardenCosmetics.filter{it.category=="THEME"}.map{it.id}).forEach{id->
                val owned=id=="MEADOW" || (state.snapshot.owned[id] ?: 0)>0
                GardenPill(if(id=="MEADOW")"Đồng cỏ" else gardenCosmetics.first{it.id==id}.name,theme==id,{theme=id},
                    enabled=owned,icon=if(!owned)Icons.Rounded.Lock else null)
            }
        }
        HorizontalDivider(color=Green.copy(alpha=.08f))
        GardenHeading("Hạt bay")
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            listOf("OFF" to "Tắt","LOW" to "Ít","NORMAL" to "Vừa").forEach{(id,name)->GardenPill(name,density==id,{density=id})}
        }
        SettingToggle("Tiết kiệm pin",battery,{battery=it},Icons.Rounded.BatterySaver)
        SettingToggle("Giảm chuyển động",reducedMotion,onReducedMotion,Icons.Rounded.MotionPhotosOff)
        GardenCaption("Giảm chuyển động hoặc tiết kiệm pin sẽ tắt hạt bay. Âm thanh đang tắt.")
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        GardenCompactAction("Lưu diện mạo",{onAct(LivingGardenAction.Appearance(initial.copy(nickname=nickname,theme=theme,density=density,batterySaver=battery,muted=true)))},
            Modifier.weight(1f),primary=true,enabled=!state.busy && nickname.isNotBlank(),icon=Icons.Rounded.Check)
        GardenCompactAction("Kỷ niệm",onMemories,Modifier.weight(1f),icon=Icons.Rounded.History)
    }
}
@Composable private fun SettingToggle(label:String,checked:Boolean,onChecked:(Boolean)->Unit,icon:androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable{onChecked(!checked)},verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Icon(icon,null,Modifier.size(16.dp),tint=Green)
        Text(label,Modifier.weight(1f),color=Deep,fontSize=11.sp,lineHeight=15.sp)
        Switch(checked,onChecked,Modifier.graphicsLayer(scaleX=.75f,scaleY=.75f),
            colors=SwitchDefaults.colors(checkedTrackColor=Green,uncheckedTrackColor=Color(0xFFDCE9DF),uncheckedBorderColor=Color(0xFFB7CFC0)))
    }
}
@Composable private fun ExpansionContent(state:LivingGardenUiState,onAct:(LivingGardenAction)->Unit) {
    val section=state.snapshot.sections.firstOrNull{it.id==state.selected.toIntOrNull()} ?: return
    GardenPanel {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.GridView,null,Modifier.size(24.dp),tint=Green)
            Column(Modifier.weight(1f)){GardenHeading("Khu vườn ${section.id}");GardenCaption("Thêm 6 ô · Giữ nguyên cây hiện có")}
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            section.plots.forEach{id->Box(Modifier.weight(1f).height(34.dp).background(Color(0xFFEDF5E9),RoundedCornerShape(7.dp)),contentAlignment=Alignment.Center){
                Text(id.toString().padStart(2,'0'),color=Muted,fontSize=10.sp)
            }}
        }
        Row(Modifier.fillMaxWidth()) {
            GardenFact("Giá mở khu","${section.price} xu",Modifier.weight(1f))
            GardenFact("Ví hiện tại","${state.snapshot.coins} xu",Modifier.weight(1f))
        }
        GardenCompactAction("Mở khu · ${section.price} xu",{onAct(LivingGardenAction.Expand(section.id,newKey()))},Modifier.align(Alignment.End),
            primary=true,enabled=!state.busy && !section.unlocked && state.snapshot.coins>=section.price,icon=Icons.Rounded.Add)
        if(state.snapshot.coins<section.price)GardenCaption("Cần thêm ${section.price-state.snapshot.coins} xu")
    }
}

@Preview(name="Compact appearance · owner refresh",widthDp=360,heightDp=620,device="spec:width=360dp,height=620dp,dpi=160")
@Composable fun CompactAppearanceOwnerPreview() {
    MamXanhPreviewTheme {ProvideTextStyle(TextStyle(fontSize=11.sp,lineHeight=15.sp)) {
        Column(Modifier.fillMaxWidth().padding(10.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            AppearanceContent(LivingGardenUiState(loading=false),false,{}, {}, {})
        }
    }}
}
