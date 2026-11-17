package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
private fun categoryName(s:String)=when(s){"GROUND"->AppText.text(TextKey.CopyMatDat9d263a);"BORDER"->AppText.text(TextKey.CopyVien5051f2);"ACCENT"->AppText.text(TextKey.CopyDiemNhan0400b9);"AMBIENT"->AppText.text(TextKey.CopyHieuUng40f9c1);"TREE"->AppText.text(TextKey.CopyPhuKien0852d7);"THEME"->AppText.text(TextKey.CopyChuDe59500d);else->s}
private fun dateLabel(at:Long)=if(at<=0)AppText.text(TextKey.CopyChuaGhiNhanF7654d) else java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.getDefault()).format(java.util.Date(at))

@Composable
fun LivingGardenScreen(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit,
    onSelect:(String)->Unit,onPlace:(Int,String?)->Unit,onRemove:(String)->Unit,onUndo:()->Unit,onSave:()->Unit,onCancel:()->Unit,
    onTree:(Long)->Unit,onSeedShop:()->Unit,onMemories:()->Unit,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    active:Boolean,modifier:Modifier=Modifier,onPlant:(Int)->Unit={},onBack:()->Unit={onOpen("","")}) {
    val arranging=false
    val title=when(state.route){"SHOP"->AppText.text(TextKey.CopyTiemTrangTriFbd50e);"INVENTORY"->AppText.text(TextKey.CopyTrangTriCuaBanDa2761);"ARRANGE"->AppText.text(TextKey.CopyGocVuonF4c049);"ENCYCLOPEDIA"->AppText.text(TextKey.CopyBachThaoF2ed64);"SPECIES"->AppText.text(TextKey.CopyHoSoLoaiCfff22);"APPEARANCE"->AppText.text(TextKey.CopyDienMao14bd0c);"DETAIL"->AppText.text(TextKey.CopyTrangTriD48231);"EXPANSION"->AppText.text(TextKey.CopyMoKhuVuon35e0bd);else->state.snapshot.appearance.nickname}
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19,color=Deep)) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp4),verticalAlignment=Alignment.CenterVertically) {
                    IconButton({if(arranging)onCancel() else onBack()}) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack,if(arranging)AppText.text(TextKey.CopyHuyBoTriEa6ca2) else AppText.text(TextKey.CopyQuayLai6d7935),Modifier.size(AppDimensions.Dp18),tint=Green)
                    }
                    Text(title,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp17,fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    if(arranging) {
                        IconButton(onUndo,enabled=state.undoAvailable && !state.busy){Icon(Icons.AutoMirrored.Rounded.Undo,AppText.text(TextKey.CopyHoanTacE72ff6),Modifier.size(AppDimensions.Dp18),tint=if(state.undoAvailable)Green else Muted)}
                        GardenCompactAction(AppText.text(TextKey.CopyLuuE91f26),onSave,primary=true,enabled=!state.busy && !state.loading,icon=Icons.Rounded.Check)
                    } else {
                        MintCoinBadge(state.snapshot.coins)
                        Spacer(Modifier.width(AppDimensions.Dp6))
                    }
                }
                if(state.loading || state.busy)LinearProgressIndicator(Modifier.fillMaxWidth().height(AppDimensions.Dp2))
                if(state.message.isNotBlank())Text(state.message,Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp12,vertical=AppDimensions.Dp3),color=Green,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
                when(state.route) {
                    "STUDIO","ARRANGE"->GardenStudio(state,onOpen,onSelect,onPlace,onRemove,onTree,onPlant,reducedMotion,active)
                    "SHOP","INVENTORY"->CosmeticCatalog(state,onOpen)
                    else->key(state.route,state.selected) {
                        val scroll=rememberScrollState()
                        Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                            .verticalScroll(scroll).padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
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
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)) {
        GardenScreenIntro(state.snapshot.appearance.nickname,AppText.text(TextKey.CopyKhongGianCuaBanD45f67))
        GardenNavigationCard(AppText.text(TextKey.CopyBachThaoF2ed64),{onOpen("ENCYCLOPEDIA","")},art=GardenArt.BOOK)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
            state.snapshot.sections.forEach{s->GardenPill("Khu ${s.id}",section==s.id,{section=s.id},icon=if(s.unlocked)null else Icons.Rounded.Lock)}
        }
        if(current!=null&&!current.unlocked)GardenPanel{
            GardenHeading(AppText.text(TextKey.CopyKhu0DangKhoaBdcfda,section))
            GardenCaption(AppText.text(TextKey.Copy0XuThem6OBd7dbb,current.price))
            GardenPaintedButton(AppText.text(TextKey.CopyXemMoKhu28c09c),{onOpen("EXPANSION",section.toString())},Modifier.fillMaxWidth())
        }else {
            state.garden.plots.filter{it.id in (current?.plots?:emptyList())}.chunked(2).forEach{row->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
                    row.forEach{plot->
                        val tree=state.garden.trees.firstOrNull{it.id==plot.treeId}
                        val placed=state.snapshot.placements.filter{it.plotId==plot.id}
                        GardenBotanicalFrame(Modifier.weight(1f).clickable{if(tree!=null)onTree(tree.id) else if(plot.isUnlocked)onPlant(plot.id)},contentPadding=PaddingValues(AppDimensions.Dp12),cornerSize=AppDimensions.Dp20){
                            Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
                                GardenCaption(plot.id.toString().padStart(2,'0'))
                                Box(Modifier.fillMaxWidth().height(AppDimensions.Dp84).background(Pale,RoundedCornerShape(AppDimensions.Dp14)),contentAlignment=Alignment.Center){
                                    if(tree!=null)GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.fillMaxSize(),withered=!tree.status.alive)
                                    else Icon(if(plot.isUnlocked)Icons.Rounded.Add else Icons.Rounded.Lock,null,tint=Green)
                                }
                                Text(tree?.name?:if(plot.isUnlocked)AppText.text(TextKey.CopyGieoCay31adde) else AppText.text(TextKey.CopyChuaMoB744b3),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp18,fontWeight=FontWeight.SemiBold)
                                if(tree!=null){MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth());GardenCaption("${tree.status.percentage}%");GardenCaption(tree.status.waterLabel)}
                                else GardenCaption(if(plot.isUnlocked)AppText.text(TextKey.CopyMotKhoiDauMoi280995) else AppText.text(TextKey.CopyODangKhoa0b475c))
                                if(placed.isNotEmpty())GardenCaption(AppText.text(TextKey.Copy0TrangTriA468bd,placed.size))
                            }
                        }
                    }
                    if(row.size==1)Spacer(Modifier.weight(1f))
                }
            }
        }
        GardenNavigationCard(AppText.text(TextKey.CopyTiemTrangTriFbd50e),{onOpen("SHOP","")},art=GardenArt.SHOP)
        GardenNavigationCard(AppText.text(TextKey.CopyTrangTriCuaBanDa2761),{onOpen("INVENTORY","")},art=GardenArt.SEED)
        GardenNavigationCard(AppText.text(TextKey.CopyDienMao14bd0c),{onOpen("APPEARANCE","")},art=GardenArt.LANTERN)
    }
}

@Composable private fun PlotProgress(progress:Float,label:String?=null) {
    Box(Modifier.fillMaxWidth().height(if(label==null)AppDimensions.Dp4 else with(LocalDensity.current){AppTypography.Sp12.toDp()}).clip(RoundedCornerShape(AppDimensions.Dp6)).background(AppColors.SwatchFFDCEBE1),contentAlignment=Alignment.Center) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f,1f)).align(Alignment.CenterStart).background(MintDepth.diagonal(listOf(AppColors.SwatchFFA6D869,AppColors.SwatchFF74BA64))))
        if(label!=null)Text(label,color=Deep,fontSize=AppTypography.Sp9,lineHeight=AppTypography.Sp11,fontWeight=FontWeight.Bold)
    }
}
@Composable private fun GardenParticles(low:Boolean) {
    val transition=rememberInfiniteTransition(label="garden-ambience")
    val fraction by transition.animateFloat(0f,1f,infiniteRepeatable(tween(10000,easing=LinearEasing)),label="petals")
    Canvas(Modifier.fillMaxSize()) {repeat(if(low)4 else 8){i->
        drawCircle(AppColors.SwatchFFE6E6AC.copy(alpha=AppOpacity.Value_45),AppDimensions.Dp3.toPx(),Offset(((i*.173f+.12f)%1f)*size.width+sin((fraction+i)*6.28f)*AppDimensions.Dp12.toPx(),((fraction+i*.137f)%1f)*size.height))
    }}
}

@Composable private fun ColumnScope.CosmeticCatalog(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12).background(Pale,RoundedCornerShape(AppDimensions.Dp15)).padding(AppDimensions.Dp4)) {
        listOf("SHOP" to AppText.text(TextKey.CopyTiemTrangTriFbd50e),"INVENTORY" to AppText.text(TextKey.CopyDaSoHuu822625)).forEach{(route,label)->
            Box(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48).background(if(state.route==route)AppColors.White else Pale,RoundedCornerShape(AppDimensions.Dp12)).clickable{onOpen(route,"")},contentAlignment=Alignment.Center){Text(label,color=if(state.route==route)Green else Muted,fontSize=AppTypography.Sp12)}
        }
    }
    var filter by rememberSaveable{mutableStateOf("ALL")}
    Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp8).horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
        (listOf("ALL")+gardenCosmetics.map{it.category}.distinct()).forEach{category->GardenPill(if(category=="ALL")AppText.text(TextKey.CopyTatCa49c73a) else categoryName(category),filter==category,{filter=category})}
    }
    val grid=rememberLazyGridState()
    BoxWithConstraints(Modifier.weight(1f)) {
        val columns=if(maxWidth>=AppDimensions.Dp600)3 else 2
        LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward,grid.canScrollForward),state=grid,
            contentPadding=PaddingValues(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp16),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            val items=gardenCosmetics.filter{(filter=="ALL" || it.category==filter) && (state.route!="INVENTORY" || (state.snapshot.owned[it.id] ?: 0)>0)}
            items(items,key={it.id}){item->
                SmallCatalogTile(item.name,if((state.snapshot.owned[item.id] ?: 0)>0)AppText.text(TextKey.CopySoHuu0A67ee0,state.snapshot.owned[item.id]) else if(item.tier=="Reward")AppText.text(TextKey.CopyQuaSuuTam218de5) else AppText.text(TextKey.Copy0Xu251e68,item.price),
                    {onOpen("DETAIL",item.id)}){CosmeticArtwork(item,it)}
            }
            if(items.isEmpty())item(span={GridItemSpan(maxLineSpan)}) {GardenPanel {GardenHeading(AppText.text(TextKey.CopyChuaCoTrangTriC94f39));GardenCompactAction(AppText.text(TextKey.CopyGheTiemCb74e9),{onOpen("SHOP","")},icon=Icons.Rounded.Storefront)}}
        }
    }
}
@Composable private fun SmallCatalogTile(name:String,value:String,onClick:()->Unit,art:@Composable (Modifier)->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(onClick=onClick),contentPadding=PaddingValues(AppDimensions.Dp12),cornerSize=AppDimensions.Dp20) {
        Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)) {
            Box(Modifier.fillMaxWidth().height(AppDimensions.Dp100).background(Pale,RoundedCornerShape(AppDimensions.Dp14)),contentAlignment=Alignment.Center){art(Modifier.size(AppDimensions.Dp44))}
            Text(name,Modifier.heightIn(min=AppDimensions.Dp32),color=Deep,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp16,fontWeight=FontWeight.SemiBold,maxLines=2,overflow=TextOverflow.Ellipsis)
            Text(value,color=Green,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
        }
    }
}

@Composable internal fun CosmeticDetail(state:LivingGardenUiState,onOpen:(String,String)->Unit,onAct:(LivingGardenAction)->Unit) {
    val item=gardenCosmetics.firstOrNull{it.id==state.selected}?:run{GardenCaption(AppText.text(TextKey.CopyVatPhamChuaDuocHoTro5099e1));return}
    val owned=state.snapshot.owned[item.id]?:0
    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(AppDimensions.Dp28),cornerSize=AppDimensions.Dp28,skin=GardenPanelSkin.HINT){
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
            MintStatusPill(categoryName(item.category))
            CosmeticArtwork(item,Modifier.size(AppDimensions.Dp86))
            Text(item.name,color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
            GardenCaption(if(item.tier=="Reward")AppText.text(TextKey.CopyQuaSuuTam218de5) else AppText.text(TextKey.Copy0Xu251e68,item.price))
        }
    }
    MintMetricStrip(listOf(AppText.text(TextKey.CopyDaSoHuu822625) to owned.toString(),AppText.text(TextKey.CopyDangDatAe223b) to state.snapshot.placements.count{it.sku==item.id}.toString()))
    GardenExpandableSection(AppText.text(TextKey.CopyViTriCachDung82d89c)){
        GardenCaption("${categoryName(item.category)} · ${item.footprint} · ${item.tier}")
        GardenCaption(AppText.text(TextKey.CopyChiTrangTriKhongDoiTocDoCayMuaDeCatVaoTu58883c))
    }
    if(item.tier=="Reward")GardenNavigationCard(AppText.text(TextKey.CopyMoKhoaQuaSuuTam12f128),{onOpen("ENCYCLOPEDIA","")})
    else if(!item.permanent||owned==0){
        GardenPaintedButton(AppText.text(TextKey.CopyMua0Xu39faa9,item.price),{onAct(LivingGardenAction.Buy(item.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy&&state.snapshot.coins>=item.price)
        val days=ceil((item.price-state.snapshot.coins).coerceAtLeast(0)/state.snapshot.earningRate.coerceAtLeast(1.0)).toInt()
        GardenCaption(AppText.text(TextKey.CopyUocTinh0Ngay1B69012,days,if(state.snapshot.estimatedRate)AppText.text(TextKey.CopyThamKhao40XuNgayC8596f) else AppText.text(TextKey.CopyTheoThuNhap7NgayF72e2b)))
    }
    if(owned>0&&item.slot=="THEME")GardenPaintedButton(AppText.text(TextKey.CopyChonChuDeC1f89a),{onOpen("APPEARANCE",item.id)},Modifier.fillMaxWidth(),primary=false)
}

@Composable internal fun Encyclopedia(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    val records=state.snapshot.species
    val discovered=records.count{it.discoveredAt>0}
    GardenScreenIntro(AppText.text(TextKey.CopyMoiLoaiMotCauChuyen3baf0d))
    GardenPanel {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            GardenHeading(AppText.text(TextKey.CopyBoSuuTam539bd6),Modifier.weight(1f));Text("$discovered/${catalog.size} · ${discovered*100/catalog.size}%",color=Green,fontSize=AppTypography.Sp11,fontWeight=FontWeight.Bold)
        }
        PlotProgress(discovered.toFloat()/catalog.size)
        GardenCaption(AppText.text(TextKey.Copy0LoaiTrongDanhMucGiuLichSuKhiDonHoacBanCAc2aaf,catalog.size))
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns=if(maxWidth<AppDimensions.Dp340 || LocalDensity.current.fontScale>1.25f)2 else 3
        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            catalog.chunked(columns).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                row.forEach{s->
                    val r=records.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
                    Box(Modifier.weight(1f)) {
                        SmallCatalogTile(s.name,r.status,{onOpen("SPECIES",s.id)}) {
                            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",it)
                            else Icon(Icons.Rounded.Spa,null,it.padding(AppDimensions.Dp5),tint=Green.copy(alpha=AppOpacity.Value_35))
                        }
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }}
        }
    }
    GardenPanel {
        GardenHeading(AppText.text(TextKey.CopyDauMocSuuTam1b494b))
        state.snapshot.goals.forEachIndexed{index,goal->
            if(index>0)HorizontalDivider(color=Green.copy(alpha=AppOpacity.Value_08))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                val reward=gardenCosmetics.firstOrNull{it.id==goal.reward}
                if(reward!=null)CosmeticArtwork(reward,Modifier.size(AppDimensions.Dp22))
                else Icon(Icons.Rounded.WorkspacePremium,null,Modifier.size(AppDimensions.Dp22),tint=Green)
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp3)) {
                    Text(goal.title,color=Deep,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp14,fontWeight=FontWeight.SemiBold)
                    GardenCaption(reward?.name ?: AppText.text(TextKey.CopyHuyHieuBoSuuTam521407))
                    PlotProgress(goal.progress.toFloat()/goal.target)
                }
                if(goal.unlockedAt>0)Icon(Icons.Rounded.CheckCircle,null,Modifier.size(AppDimensions.Dp15),tint=Green)
                else Text("${goal.progress.coerceAtMost(goal.target)}/${goal.target}",color=Muted,fontSize=AppTypography.Sp10)
            }
        }
    }
}

@Composable internal fun SpeciesDetail(state:LivingGardenUiState,onTree:(Long)->Unit,onShop:()->Unit) {
    val s=catalog.firstOrNull{it.id==state.selected} ?: run{GardenCaption(AppText.text(TextKey.CopyLoaiChuaDuocHoTro9107c4));return}
    val r=state.snapshot.species.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
    GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT,contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp28) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
            MintStatusPill("${s.tier} · ${r.status}")
            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.fillMaxWidth().height(AppDimensions.Dp220))
            else Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Spa,null,Modifier.size(AppDimensions.Dp72),tint=Green)}
            Text(s.name,color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
            GardenCaption(s.description)
        }
    }
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
        Row(Modifier.fillMaxWidth()){GardenHeading(AppText.text(TextKey.CopyCacGiaiDoanD4547f),Modifier.weight(1f));GardenCaption("${(r.highestStage+1).coerceAtLeast(0)}/${s.formCount}")}
        val columns=3
        s.profile.stageThresholds.indices.toList().chunked(columns).forEach{row->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
                row.forEach{index->
                    Column(Modifier.weight(1f).background(if(index<=r.highestStage)AppColors.SwatchFFEDF5E9 else AppColors.SwatchFFF0F4EF,RoundedCornerShape(AppDimensions.Dp9))
                        .padding(vertical=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp2)) {
                        if(index<=r.highestStage)GardenPlantPortrait(s.id,"NONE",Modifier.size(AppDimensions.Dp64))
                        else Box(Modifier.size(AppDimensions.Dp64),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Lock,null,Modifier.size(AppDimensions.Dp14),tint=Muted.copy(alpha=AppOpacity.Value_55))}
                        Text((index+1).toString(),color=Muted,fontSize=AppTypography.Sp9,lineHeight=AppTypography.Sp12)
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }
        }
    }
    GardenPanel {
        listOf(AppText.text(TextKey.CopyDaTrong42fa2b) to r.planted.toString(),AppText.text(TextKey.CopyTrongTui905deb) to r.ownedSeeds.toString(),AppText.text(TextKey.CopyDangSongE3573d) to r.living.toString(),AppText.text(TextKey.CopyTrongDauTien2e1aed) to dateLabel(r.firstPlanted),AppText.text(TextKey.CopyTruongThanhDauDd677a) to dateLabel(r.firstMatured),AppText.text(TextKey.CopyCayCaoNhatDangSong7d778f) to if(r.tallestStage>=0)"${r.tallestStage+1}/${s.formCount}" else AppText.text(TextKey.CopyChuaGhiNhanF7654d)).forEachIndexed{index,(label,value)->
            if(index>0)HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
            Row(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp8),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Text(label,Modifier.weight(1f),color=Muted,fontSize=AppTypography.Sp12);Text(value,color=Deep,fontSize=AppTypography.Sp12)}
        }
    }
    GardenExpandableSection(AppText.text(TextKey.CopyChamSocLoaiF520bc)){GardenCaption(AppText.text(TextKey.CopyBinhTuoi8121824GioPhanBonDungKhiCayDangDe84c6))}
    state.garden.trees.filter{it.speciesId==s.id}.forEach{tree->GardenNavigationCard(tree.name,{onTree(tree.id)})}
    GardenPaintedButton(AppText.text(TextKey.CopyTimHatA56320),onShop,Modifier.fillMaxWidth())

}

@Composable internal fun AppearanceContent(state:LivingGardenUiState,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    onAct:(LivingGardenAction)->Unit,onMemories:()->Unit) {
    val initial=state.snapshot.appearance
    var nickname by rememberSaveable{mutableStateOf(initial.nickname)}
    var theme by rememberSaveable{mutableStateOf(initial.theme)}
    var density by rememberSaveable{mutableStateOf(initial.density)}
    var battery by rememberSaveable{mutableStateOf(initial.batterySaver)}
    GardenCaption(AppText.text(TextKey.CopyTu07eb90b,dateLabel(initial.createdAt)))
    GardenTextField(nickname,{nickname=it.take(40)},AppText.text(TextKey.CopyTenVuon1b0761))
    MintMetricStrip(listOf(AppText.text(TextKey.CopyLoaiCayDd9342) to state.garden.trees.map{it.speciesId}.distinct().size.toString(),AppText.text(TextKey.CopyTruongThanh8c14d7) to state.garden.trees.count{it.status.state==TreeGrowthState.MATURE}.toString(),AppText.text(TextKey.CopyTrangTriD48231) to state.snapshot.placements.size.toString()))
    GardenChoice(AppText.text(TextKey.CopyChuDe59500d),theme,(listOf("MEADOW" to AppText.text(TextKey.CopyDongCo09da3d))+gardenCosmetics.filter{it.category=="THEME"}.map{it.id to it.name}),{theme=it}){it=="MEADOW"||(state.snapshot.owned[it]?:0)>0}
    GardenChoice(AppText.text(TextKey.CopyHatBayA6343b),density,listOf("OFF" to AppText.text(TextKey.CopyTat328606),"LOW" to AppText.text(TextKey.CopyItA3681b),"NORMAL" to AppText.text(TextKey.CopyVua8af1c0)),{density=it})
    GardenPanel {
        SettingToggle(AppText.text(TextKey.CopyTietKiemPinB1018c),battery,{battery=it},Icons.Rounded.BatterySaver)
        HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
        SettingToggle(AppText.text(TextKey.CopyGiamChuyenDongFf3295),reducedMotion,onReducedMotion,Icons.Rounded.MotionPhotosOff)
    }
    GardenCaption(AppText.text(TextKey.CopyGiamChuyenDongHoacTietKiemPinTatHatBayAmT91e597))
    GardenPaintedButton(AppText.text(TextKey.CopyLuuDienMao105663),{onAct(LivingGardenAction.Appearance(initial.copy(nickname=nickname,theme=theme,density=density,batterySaver=battery,muted=true)))},Modifier.fillMaxWidth(),enabled=!state.busy&&nickname.isNotBlank())
    GardenNavigationCard(AppText.text(TextKey.CopyKyNiemE3e113),onMemories,art=GardenArt.JOURNAL)
}

@Composable private fun GardenChoice(label:String,value:String,options:List<Pair<String,String>>,onChange:(String)->Unit,enabled:(String)->Boolean={true}) {
    var expanded by remember{mutableStateOf(false)}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
        GardenCaption(label)
        Box {
            GardenPaintedButton((options.firstOrNull{it.first==value}?.second?:value)+"  ⌄",{expanded=true},Modifier.fillMaxWidth(),primary=false)
            DropdownMenu(expanded,{expanded=false}){options.forEach{(id,name)->DropdownMenuItem(text={Text(name)},onClick={onChange(id);expanded=false},enabled=enabled(id))}}
        }
    }
}
@Composable private fun SettingToggle(label:String,checked:Boolean,onChecked:(Boolean)->Unit,icon:androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).clickable{onChecked(!checked)},verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
        Icon(icon,null,Modifier.size(AppDimensions.Dp16),tint=Green)
        Text(label,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
        Switch(checked,onChecked,Modifier.graphicsLayer(scaleX=.75f,scaleY=.75f),
            colors=SwitchDefaults.colors(checkedTrackColor=Green,uncheckedTrackColor=AppColors.SwatchFFDCE9DF,uncheckedBorderColor=AppColors.SwatchFFB7CFC0))
    }
}
@Composable private fun ExpansionContent(state:LivingGardenUiState,onAct:(LivingGardenAction)->Unit) {
    val section=state.snapshot.sections.firstOrNull{it.id==state.selected.toIntOrNull()} ?: return
    Column(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp24),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)) {
        Icon(Icons.Rounded.GridView,null,Modifier.size(AppDimensions.Dp44),tint=Green)
        Text(AppText.text(TextKey.CopyKhuVuon0C58827,section.id),color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
        GardenCaption(AppText.text(TextKey.CopyThem6OGiuNguyenCayHienCoF944b2))
    }
    section.plots.chunked(3).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){row.forEach{id->
        GardenBotanicalFrame(Modifier.weight(1f),contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp18){Text(id.toString(),Modifier.fillMaxWidth(),textAlign=TextAlign.Center,color=Deep,fontSize=AppTypography.Sp14)}
    }}}
    MintMetricStrip(listOf(AppText.text(TextKey.CopyGiaMoKhuD7a20a) to AppText.text(TextKey.Copy0Xu251e68,section.price),AppText.text(TextKey.CopyViHienTai127da5) to AppText.text(TextKey.Copy0Xu251e68,state.snapshot.coins)))
    GardenPaintedButton(AppText.text(TextKey.CopyMoKhu0XuD27450,section.price),{onAct(LivingGardenAction.Expand(section.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy&&!section.unlocked&&state.snapshot.coins>=section.price)
    if(state.snapshot.coins<section.price)GardenCaption(AppText.text(TextKey.CopyCanThem0Xu8837ee,section.price-state.snapshot.coins))
}

@Preview(name="Compact appearance · owner refresh",widthDp=360,heightDp=620,device="spec:width=360dp,height=620dp,dpi=160")
@Composable fun CompactAppearanceOwnerPreview() {
    MamXanhPreviewTheme {ProvideTextStyle(TextStyle(fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)) {
        Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp10),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            AppearanceContent(LivingGardenUiState(loading=false),false,{}, {}, {})
        }
    }}
}

private fun webComparisonGardenState()=LivingGardenUiState(loading=false,route="STUDIO",selected="accent_lantern",
 snapshot=LivingGardenSnapshot(coins=620,owned=mapOf("accent_lantern" to 1),species=listOf(SpeciesRecord("cherry",discoveredAt=1,highestStage=3,planted=1,firstPlanted=1793494800000,ownedSeeds=2,living=1,tallestStage=3),SpeciesRecord("pine",discoveredAt=1,highestStage=2,planted=1,living=1),SpeciesRecord("succulent",discoveredAt=1,highestStage=5,planted=1,firstMatured=1)),sections=(1..4).map{GardenSection(it,((it-1)*6+1..it*6).toList(),it==1,5400)}),
 garden=Garden(trees=listOf(Tree(1,"cherry",AppText.text(TextKey.CopyDaoHongXuanC2fd99),45,"2026-11-01",status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f)),Tree(2,"pine",AppText.text(TextKey.CopyThongNho84a4db),24,"2026-11-04")),plots=(1..24).map{GardenPlot(it,it<=5,if(it<=2)it.toLong()else null)}))
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun StudioWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState(),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun CosmeticWebDesignPreview(){MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){CosmeticDetail(webComparisonGardenState(),{_,_->},{})}}}}


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
