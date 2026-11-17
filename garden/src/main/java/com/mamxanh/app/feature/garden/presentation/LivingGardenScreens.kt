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
    active:Boolean,modifier:Modifier=Modifier,onPlant:(Int)->Unit={},onBack:()->Unit={onOpen("","")}) {
    val arranging=false
    val title=when(state.route){"SHOP"->"Tiệm trang trí";"INVENTORY"->"Trang trí của bạn";"ARRANGE"->"Góc vườn";"ENCYCLOPEDIA"->"Bách thảo";"SPECIES"->"Hồ sơ loài";"APPEARANCE"->"Diện mạo";"DETAIL"->"Trang trí";"EXPANSION"->"Mở khu vườn";else->state.snapshot.appearance.nickname}
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(fontSize=13.sp,lineHeight=19.sp,color=Deep)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal=4.dp),verticalAlignment=Alignment.CenterVertically) {
                    IconButton({if(arranging)onCancel() else onBack()}) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack,if(arranging)"Hủy bố trí" else "Quay lại",Modifier.size(18.dp),tint=Green)
                    }
                    Text(title,Modifier.weight(1f),color=Deep,fontSize=13.sp,lineHeight=17.sp,fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(arranging) {
                        IconButton(onUndo,enabled=state.undoAvailable && !state.busy){Icon(Icons.AutoMirrored.Rounded.Undo,"Hoàn tác",Modifier.size(18.dp),tint=if(state.undoAvailable)Green else Muted)}
                        GardenCompactAction("Lưu",onSave,primary=true,enabled=!state.busy && !state.loading,icon=Icons.Rounded.Check)
                    } else {
                        MintCoinBadge(state.snapshot.coins)
                        Spacer(Modifier.width(6.dp))
                    }
                }
                if(state.loading || state.busy)LinearProgressIndicator(Modifier.fillMaxWidth().height(2.dp))
                if(state.message.isNotBlank())Text(state.message,Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=3.dp),color=Green,fontSize=12.sp,lineHeight=17.sp)
                when(state.route) {
                    "STUDIO","ARRANGE"->GardenStudio(state,onOpen,onSelect,onPlace,onRemove,onTree,onPlant,reducedMotion,active)
                    "SHOP","INVENTORY"->CosmeticCatalog(state,onOpen)
                    else->key(state.route,state.selected) {
                        val scroll=rememberScrollState()
                        Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                            .verticalScroll(scroll).padding(horizontal=24.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
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
    var section by rememberSaveable{mutableIntStateOf(1)}
    val current=state.snapshot.sections.firstOrNull{it.id==section}
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=24.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
        GardenScreenIntro(state.snapshot.appearance.nickname,"KHÔNG GIAN CỦA BẠN")
        GardenNavigationCard("Bách thảo",{onOpen("ENCYCLOPEDIA","")},art=GardenArt.BOOK)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            state.snapshot.sections.forEach{s->GardenPill("Khu ${s.id}",section==s.id,{section=s.id},icon=if(s.unlocked)null else Icons.Rounded.Lock)}
        }
        if(current!=null&&!current.unlocked)GardenPanel{
            GardenHeading("Khu $section đang khóa")
            GardenCaption("${current.price} xu · Thêm 6 ô")
            GardenPaintedButton("Xem mở khu",{onOpen("EXPANSION",section.toString())},Modifier.fillMaxWidth())
        }else {
            state.garden.plots.filter{it.id in (current?.plots?:emptyList())}.chunked(2).forEach{row->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(14.dp)){
                    row.forEach{plot->
                        val tree=state.garden.trees.firstOrNull{it.id==plot.treeId}
                        val placed=state.snapshot.placements.filter{it.plotId==plot.id}
                        GardenBotanicalFrame(Modifier.weight(1f).clickable{if(tree!=null)onTree(tree.id) else if(plot.isUnlocked)onPlant(plot.id)},contentPadding=PaddingValues(12.dp),cornerSize=20.dp){
                            Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)){
                                GardenCaption(plot.id.toString().padStart(2,'0'))
                                Box(Modifier.fillMaxWidth().height(84.dp).background(Pale,RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){
                                    if(tree!=null)GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.fillMaxSize(),withered=!tree.status.alive)
                                    else Icon(if(plot.isUnlocked)Icons.Rounded.Add else Icons.Rounded.Lock,null,tint=Green)
                                }
                                Text(tree?.name?:if(plot.isUnlocked)"Gieo cây" else "Chưa mở",color=Deep,fontSize=13.sp,lineHeight=18.sp,fontWeight=FontWeight.SemiBold)
                                if(tree!=null){MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth());GardenCaption("${tree.status.percentage}%");GardenCaption(tree.status.waterLabel)}
                                else GardenCaption(if(plot.isUnlocked)"Một khởi đầu mới" else "Ô đang khóa")
                                if(placed.isNotEmpty())GardenCaption("${placed.size} trang trí")
                            }
                        }
                    }
                    if(row.size==1)Spacer(Modifier.weight(1f))
                }
            }
        }
        GardenNavigationCard("Tiệm trang trí",{onOpen("SHOP","")},art=GardenArt.SHOP)
        GardenNavigationCard("Trang trí của bạn",{onOpen("INVENTORY","")},art=GardenArt.SEED)
        GardenNavigationCard("Diện mạo",{onOpen("APPEARANCE","")},art=GardenArt.LANTERN)
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
    Row(Modifier.fillMaxWidth().padding(horizontal=24.dp,vertical=12.dp).background(Pale,RoundedCornerShape(15.dp)).padding(4.dp)) {
        listOf("SHOP" to "Tiệm trang trí","INVENTORY" to "Đã sở hữu").forEach{(route,label)->
            Box(Modifier.weight(1f).heightIn(min=48.dp).background(if(state.route==route)Color.White else Pale,RoundedCornerShape(12.dp)).clickable{onOpen(route,"")},contentAlignment=Alignment.Center){Text(label,color=if(state.route==route)Green else Muted,fontSize=12.sp)}
        }
    }
    var filter by rememberSaveable{mutableStateOf("ALL")}
    Row(Modifier.fillMaxWidth().padding(horizontal=8.dp).horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
        (listOf("ALL")+gardenCosmetics.map{it.category}.distinct()).forEach{category->GardenPill(if(category=="ALL")"Tất cả" else categoryName(category),filter==category,{filter=category})}
    }
    val grid=rememberLazyGridState()
    BoxWithConstraints(Modifier.weight(1f)) {
        val columns=if(maxWidth>=600.dp)3 else 2
        LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),state=grid,
            contentPadding=PaddingValues(horizontal=24.dp,vertical=16.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
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
    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(onClick=onClick),contentPadding=PaddingValues(12.dp),cornerSize=20.dp) {
        Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Box(Modifier.fillMaxWidth().height(100.dp).background(Pale,RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){art(Modifier.size(44.dp))}
            Text(name,Modifier.heightIn(min=32.dp),color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.SemiBold,maxLines=2,overflow=TextOverflow.Ellipsis)
            Text(value,color=Green,fontSize=12.sp,lineHeight=18.sp)
        }
    }
}

@Composable internal fun CosmeticDetail(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit) {
    val item=gardenCosmetics.firstOrNull{it.id==state.selected}?:run{GardenCaption("Vật phẩm chưa được hỗ trợ.");return}
    val owned=state.snapshot.owned[item.id]?:0
    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(28.dp),cornerSize=28.dp,skin=GardenPanelSkin.HINT){
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)){
            MintStatusPill(categoryName(item.category))
            CosmeticArtwork(item,Modifier.size(86.dp))
            Text(item.name,color=Deep,fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
            GardenCaption(if(item.tier=="Reward")"Quà sưu tầm" else "${item.price} xu")
        }
    }
    MintMetricStrip(listOf("Đã sở hữu" to owned.toString(),"Đang đặt" to state.snapshot.placements.count{it.sku==item.id}.toString()))
    GardenExpandableSection("Vị trí & cách dùng"){
        GardenCaption("${categoryName(item.category)} · ${item.footprint} · ${item.tier}")
        GardenCaption("Chỉ trang trí; không đổi tốc độ cây. Mua để cất vào túi.")
    }
    if(item.tier=="Reward")GardenNavigationCard("Mở khóa qua sưu tầm",{onOpen("ENCYCLOPEDIA","")})
    else if(!item.permanent||owned==0){
        GardenPaintedButton("Mua · ${item.price} xu",{onAct(LivingGardenAction.Buy(item.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy&&state.snapshot.coins>=item.price)
        val days=ceil((item.price-state.snapshot.coins).coerceAtLeast(0)/state.snapshot.earningRate.coerceAtLeast(1.0)).toInt()
        GardenCaption("Ước tính $days ngày · ${if(state.snapshot.estimatedRate)"tham khảo 40 xu/ngày" else "theo thu nhập 7 ngày"}")
    }
    if(owned>0&&item.slot=="THEME")GardenPaintedButton("Chọn chủ đề",{onOpen("APPEARANCE",item.id)},Modifier.fillMaxWidth(),primary=false)
}

@Composable internal fun Encyclopedia(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    val records=state.snapshot.species
    val discovered=records.count{it.discoveredAt>0}
    GardenScreenIntro("Mỗi loài, một câu chuyện.")
    GardenPanel {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            GardenHeading("Bộ sưu tầm",Modifier.weight(1f));Text("$discovered/${catalog.size} · ${discovered*100/catalog.size}%",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)
        }
        PlotProgress(discovered.toFloat()/catalog.size)
        GardenCaption("${catalog.size} loài trong danh mục · Giữ lịch sử khi dọn hoặc bán cây")
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns=if(maxWidth<340.dp || LocalDensity.current.fontScale>1.25f)2 else 3
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
            catalog.chunked(columns).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
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
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
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
    GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT,contentPadding=PaddingValues(18.dp),cornerSize=28.dp) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
            MintStatusPill("${s.tier} · ${r.status}")
            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.fillMaxWidth().height(220.dp))
            else Box(Modifier.fillMaxWidth().height(220.dp),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Spa,null,Modifier.size(72.dp),tint=Green)}
            Text(s.name,color=Deep,fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold)
            GardenCaption(s.description)
        }
    }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth()){GardenHeading("Các giai đoạn",Modifier.weight(1f));GardenCaption("${(r.highestStage+1).coerceAtLeast(0)}/${s.formCount}")}
        val columns=3
        s.profile.stageThresholds.indices.toList().chunked(columns).forEach{row->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                row.forEach{index->
                    Column(Modifier.weight(1f).background(if(index<=r.highestStage)Color(0xFFEDF5E9) else Color(0xFFF0F4EF),RoundedCornerShape(9.dp))
                        .padding(vertical=4.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
                        if(index<=r.highestStage)GardenPlantPortrait(s.id,"NONE",Modifier.size(64.dp))
                        else Box(Modifier.size(64.dp),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Lock,null,Modifier.size(14.dp),tint=Muted.copy(alpha=.55f))}
                        Text((index+1).toString(),color=Muted,fontSize=9.sp,lineHeight=12.sp)
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }
        }
    }
    GardenPanel {
        listOf("Đã trồng" to r.planted.toString(),"Trong túi" to r.ownedSeeds.toString(),"Đang sống" to r.living.toString(),"Trồng đầu tiên" to dateLabel(r.firstPlanted),"Trưởng thành đầu" to dateLabel(r.firstMatured),"Cây cao nhất đang sống" to if(r.tallestStage>=0)"${r.tallestStage+1}/${s.formCount}" else "Chưa ghi nhận").forEachIndexed{index,(label,value)->
            if(index>0)HorizontalDivider(color=Color(0xFFE4E7DC))
            Row(Modifier.fillMaxWidth().padding(vertical=8.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Text(label,Modifier.weight(1f),color=Muted,fontSize=12.sp);Text(value,color=Deep,fontSize=12.sp)}
        }
    }
    GardenExpandableSection("Chăm sóc loài"){GardenCaption("Bình tưới: 8 / 12 / 18 / 24 giờ · Phân bón dùng khi cây đang lớn và đã tưới; không cộng dồn.")}
    state.garden.trees.filter{it.speciesId==s.id}.forEach{tree->GardenNavigationCard(tree.name,{onTree(tree.id)})}
    GardenPaintedButton("Tìm hạt",onShop,Modifier.fillMaxWidth())

}

@Composable internal fun AppearanceContent(state:LivingGardenUiState,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    onAct:(LivingGardenAction)->Unit,onMemories:()->Unit) {
    val initial=state.snapshot.appearance
    var nickname by rememberSaveable{mutableStateOf(initial.nickname)}
    var theme by rememberSaveable{mutableStateOf(initial.theme)}
    var density by rememberSaveable{mutableStateOf(initial.density)}
    var battery by rememberSaveable{mutableStateOf(initial.batterySaver)}
    GardenCaption("Từ ${dateLabel(initial.createdAt)}")
    GardenTextField(nickname,{nickname=it.take(40)},"Tên vườn")
    MintMetricStrip(listOf("Loài cây" to state.garden.trees.map{it.speciesId}.distinct().size.toString(),"Trưởng thành" to state.garden.trees.count{it.status.state==TreeGrowthState.MATURE}.toString(),"Trang trí" to state.snapshot.placements.size.toString()))
    GardenChoice("Chủ đề",theme,(listOf("MEADOW" to "Đồng cỏ")+gardenCosmetics.filter{it.category=="THEME"}.map{it.id to it.name}),{theme=it}){it=="MEADOW"||(state.snapshot.owned[it]?:0)>0}
    GardenChoice("Hạt bay",density,listOf("OFF" to "Tắt","LOW" to "Ít","NORMAL" to "Vừa"),{density=it})
    GardenPanel {
        SettingToggle("Tiết kiệm pin",battery,{battery=it},Icons.Rounded.BatterySaver)
        HorizontalDivider(color=Color(0xFFE4E7DC))
        SettingToggle("Giảm chuyển động",reducedMotion,onReducedMotion,Icons.Rounded.MotionPhotosOff)
    }
    GardenCaption("Giảm chuyển động hoặc tiết kiệm pin tắt hạt bay. Âm thanh tắt.")
    GardenPaintedButton("Lưu diện mạo",{onAct(LivingGardenAction.Appearance(initial.copy(nickname=nickname,theme=theme,density=density,batterySaver=battery,muted=true)))},Modifier.fillMaxWidth(),enabled=!state.busy&&nickname.isNotBlank())
    GardenNavigationCard("Kỷ niệm",onMemories,art=GardenArt.JOURNAL)
}

@Composable private fun GardenChoice(label:String,value:String,options:List<Pair<String,String>>,onChange:(String)->Unit,enabled:(String)->Boolean={true}) {
    var expanded by remember{mutableStateOf(false)}
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
        GardenCaption(label)
        Box {
            GardenPaintedButton((options.firstOrNull{it.first==value}?.second?:value)+"  ⌄",{expanded=true},Modifier.fillMaxWidth(),primary=false)
            DropdownMenu(expanded,{expanded=false}){options.forEach{(id,name)->DropdownMenuItem(text={Text(name)},onClick={onChange(id);expanded=false},enabled=enabled(id))}}
        }
    }
}
@Composable private fun SettingToggle(label:String,checked:Boolean,onChecked:(Boolean)->Unit,icon:androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable{onChecked(!checked)},verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(16.dp)) {
        Icon(icon,null,Modifier.size(16.dp),tint=Green)
        Text(label,Modifier.weight(1f),color=Deep,fontSize=13.sp,lineHeight=19.sp)
        Switch(checked,onChecked,Modifier.graphicsLayer(scaleX=.75f,scaleY=.75f),
            colors=SwitchDefaults.colors(checkedTrackColor=Green,uncheckedTrackColor=Color(0xFFDCE9DF),uncheckedBorderColor=Color(0xFFB7CFC0)))
    }
}
@Composable private fun ExpansionContent(state:LivingGardenUiState,onAct:(LivingGardenAction)->Unit) {
    val section=state.snapshot.sections.firstOrNull{it.id==state.selected.toIntOrNull()} ?: return
    Column(Modifier.fillMaxWidth().padding(vertical=24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Rounded.GridView,null,Modifier.size(44.dp),tint=Green)
        Text("Khu vườn ${section.id}",color=Deep,fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold)
        GardenCaption("Thêm 6 ô · Giữ nguyên cây hiện có")
    }
    section.plots.chunked(3).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){row.forEach{id->
        GardenBotanicalFrame(Modifier.weight(1f),contentPadding=PaddingValues(18.dp),cornerSize=18.dp){Text(id.toString(),Modifier.fillMaxWidth(),textAlign=TextAlign.Center,color=Deep,fontSize=14.sp)}
    }}}
    MintMetricStrip(listOf("Giá mở khu" to "${section.price} xu","Ví hiện tại" to "${state.snapshot.coins} xu"))
    GardenPaintedButton("Mở khu · ${section.price} xu",{onAct(LivingGardenAction.Expand(section.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy&&!section.unlocked&&state.snapshot.coins>=section.price)
    if(state.snapshot.coins<section.price)GardenCaption("Cần thêm ${section.price-state.snapshot.coins} xu")
}

@Preview(name="Compact appearance · owner refresh",widthDp=360,heightDp=620,device="spec:width=360dp,height=620dp,dpi=160")
@Composable fun CompactAppearanceOwnerPreview() {
    MamXanhPreviewTheme {ProvideTextStyle(TextStyle(fontSize=13.sp,lineHeight=19.sp)) {
        Column(Modifier.fillMaxWidth().padding(10.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            AppearanceContent(LivingGardenUiState(loading=false),false,{}, {}, {})
        }
    }}
}

private fun webComparisonGardenState()=LivingGardenUiState(loading=false,route="STUDIO",selected="accent_lantern",
 snapshot=LivingGardenSnapshot(coins=620,owned=mapOf("accent_lantern" to 1),species=listOf(SpeciesRecord("cherry",discoveredAt=1,highestStage=3,planted=1,firstPlanted=1793494800000,ownedSeeds=2,living=1,tallestStage=3),SpeciesRecord("pine",discoveredAt=1,highestStage=2,planted=1,living=1),SpeciesRecord("succulent",discoveredAt=1,highestStage=5,planted=1,firstMatured=1)),sections=(1..4).map{GardenSection(it,((it-1)*6+1..it*6).toList(),it==1,5400)}),
 garden=Garden(trees=listOf(Tree(1,"cherry","Đào hồng xuân",45,"2026-11-01",status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f)),Tree(2,"pine","Thông nhỏ",24,"2026-11-04")),plots=(1..24).map{GardenPlot(it,it<=5,if(it<=2)it.toLong()else null)}))
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun StudioWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState(),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun CosmeticWebDesignPreview(){MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){CosmeticDetail(webComparisonGardenState(),{_,_->},{})}}}}


@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun DecorShopWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="SHOP",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun DecorInventoryWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="INVENTORY",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun EncyclopediaWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="ENCYCLOPEDIA",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun SpeciesWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="SPECIES",selected="cherry"),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun AppearanceWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="APPEARANCE",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun ExpansionWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="EXPANSION",selected="2"),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}
