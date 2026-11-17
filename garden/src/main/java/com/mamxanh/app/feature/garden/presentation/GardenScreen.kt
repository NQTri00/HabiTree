package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.foundation.horizontalScroll
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.core.ui.R

@Composable
fun GardenScreen(state: GardenUiState, onIntent: (GardenIntent) -> Unit, modifier: Modifier = Modifier,
    treeTransitionModifier: @Composable (Long) -> Modifier = { Modifier },
    onOpenInventory: (Int?) -> Unit = {},
    plantingSeedId: String? = null, plantingPlotId: Int? = null,
    onDismissPlanting: () -> Unit = {}, onLiving: (String,String)->Unit = {_,_->}) {
    val garden = state.garden
    val listState = rememberLazyGridState()
    var selectedPlotId by rememberSaveable(plantingSeedId, plantingPlotId) { mutableStateOf(plantingPlotId) }
    var unlockingPlotId by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedSeed = seedCatalog.firstOrNull { it.id == plantingSeedId }
    val plots = garden.plots.ifEmpty {
        // Preview/legacy callers still get explicit empty slots without fake saved trees.
        val count = maxOf(5, garden.trees.size)
        (1..count).map { com.mamxanh.shared.model.GardenPlot(it, true, garden.trees.getOrNull(it - 1)?.id) }
    }
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var waterAllKey by rememberSaveable { mutableStateOf<String?>(null) }
    val visiblePlots = plots.filter { plot ->
        val status = garden.trees.firstOrNull { it.id == plot.treeId }?.status
        when(filter) {
            1 -> status?.canWater == true
            2 -> status?.state == com.mamxanh.shared.model.TreeGrowthState.GROWING
            3 -> status?.state == com.mamxanh.shared.model.TreeGrowthState.MATURE
            4 -> status?.alive == false
            else -> true
        }
    }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(garden.coins, actions = {
                IconButton({onOpenInventory(null)}){Icon(androidx.compose.material.icons.Icons.Rounded.Inventory2,AppText.text(TextKey.CopyTuiDo6a8e55),tint=Green)}
            })
            GardenScreenIntro(AppText.text(TextKey.CopyCayTrongVuon6565bb),AppText.text(TextKey.CopyGocNhoCuaBanBa1417),AppText.text(TextKey.Copy0Cay1OMo528d81,garden.trees.size,plots.count{it.isUnlocked}),Modifier.padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12))
            Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp8),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                GardenPaintedButton(AppText.text(TextKey.CopyTuoiCaVuon20a14a),{waterAllKey=java.util.UUID.randomUUID().toString()},Modifier.weight(1f),enabled=garden.waterAll.eligible>0&&!state.isLoading)
                GardenPaintedButton(AppText.text(TextKey.CopyGocVuonF4c049),{onLiving("STUDIO","")},Modifier.weight(1f),primary=false)
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp24),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp2)) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4)) {
                    listOf(AppText.text(TextKey.CopyTatCa49c73a),AppText.text(TextKey.CopyCanTuoiA0957f),AppText.text(TextKey.CopyDangLon40daab),AppText.text(TextKey.CopyTruongThanh8c14d7),AppText.text(TextKey.CopyKhoHeoBf546a)).forEachIndexed { index,label ->
                        FilterChip(selected = filter == index, onClick = {filter=index},
                            label = {Text(label,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp14)},
                            shape=RoundedCornerShape(AppDimensions.Dp14),
                            colors=FilterChipDefaults.filterChipColors(selectedContainerColor=Green,selectedLabelColor=AppColors.White,labelColor=Deep))
                    }
                }
            }
            LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward,listState.canScrollForward),
                state=listState,contentPadding=PaddingValues(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp16),
                horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)) {
                if(visiblePlots.isEmpty()) item { Text(AppText.text(TextKey.CopyChuaCoCayPhuHop75e67d),color=Muted) }
                items(visiblePlots, key = { it.id }) { plot ->
                    val tree = garden.trees.firstOrNull { it.id == plot.treeId }
                    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled=!state.isLoading){
                        if(tree!=null)onIntent(GardenIntent.OpenTree(tree.id)) else if(plot.isUnlocked)onOpenInventory(plot.id) else unlockingPlotId=plot.id
                    },contentPadding=PaddingValues(AppDimensions.Dp12),cornerSize=AppDimensions.Dp20) {
                        Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp9)) {
                            Text(plot.id.toString().padStart(2,'0'),color=Muted,fontSize=AppTypography.Sp10)
                            Box(Modifier.fillMaxWidth().height(AppDimensions.Dp108).background(Pale,RoundedCornerShape(AppDimensions.Dp14)),contentAlignment=Alignment.Center) {
                                if(tree!=null)GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.fillMaxSize(),growth=tree.growth,withered=!tree.status.alive)
                                else Text(if(plot.isUnlocked)"+" else "◇",color=Green,fontSize=AppTypography.Sp30)
                            }
                            Text(tree?.name?:if(plot.isUnlocked)AppText.text(TextKey.CopyGieoCay31adde) else AppText.text(TextKey.CopyChuaMoB744b3),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp18,fontWeight=FontWeight.SemiBold)
                            if(tree!=null){
                                MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth())
                                Text("${tree.status.percentage}% · ${tree.status.stateLabel}",color=Muted,fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp14)
                                Text(tree.status.waterLabel,color=Muted,fontSize=AppTypography.Sp11)
                                if(tree.status.fertilizerLabel.isNotBlank())Text(tree.status.fertilizerLabel,color=Green,fontSize=AppTypography.Sp10)
                            }else Text(if(plot.isUnlocked)AppText.text(TextKey.CopyMotKhoiDauMoi280995) else AppText.text(TextKey.Copy0Xu251e68,com.mamxanh.shared.model.Balance.plot(plots.count{it.isUnlocked})),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                        }
                    }
                }
                item(span={GridItemSpan(maxLineSpan)}){GardenNavigationCard(AppText.text(TextKey.CopyBachThaoF2ed64),{onLiving("ENCYCLOPEDIA","")})}
            }
        }
    }
    waterAllKey?.let { key ->
        MintDialog(AppText.text(TextKey.CopyTuoiCaVuon20a14a),{waterAllKey=null},AppText.text(TextKey.CopyTuoiCay0990f6),
            {onIntent(GardenIntent.WaterAll(key));waterAllKey=null},
            confirmEnabled=!state.isLoading && garden.waterAll.eligible>0) {
            MintStatusPill(AppText.text(TextKey.CopyMienPhiDfc4f7))
            MintMetricStrip(listOf(AppText.text(TextKey.CopyCanTuoiA0957f) to garden.waterAll.eligible.toString(),
                AppText.text(TextKey.CopyDaTuoiDe2042) to garden.waterAll.active.toString(),AppText.text(TextKey.CopyKhoHeoBf546a) to garden.waterAll.dead.toString()))
            Text(AppText.text(TextKey.CopyChiTuoiCayConSongDangCanNuocThoiGianTuoiK958a7a),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
        }
    }
    unlockingPlotId?.let { id ->
        val price=com.mamxanh.shared.model.Balance.plot(garden.plots.count{it.isUnlocked})
        MintDialog(AppText.text(TextKey.CopyMoOTrong11b979),{unlockingPlotId=null},AppText.text(TextKey.CopyMoO6d274c),
            {onIntent(GardenIntent.UnlockPlot(id));unlockingPlotId=null},
            confirmEnabled=garden.coins>=price && !state.isLoading) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                GardenArtwork(GardenArt.SPROUT,Modifier.size(AppDimensions.Dp56))
                Text(AppText.text(TextKey.CopyO03d2411,id.toString().padStart(2,'0')),color=Deep,fontSize=AppTypography.Sp17,fontWeight=FontWeight.Bold)
            }
            MintMetricStrip(listOf(AppText.text(TextKey.CopyGiaMoO708ec3) to AppText.text(TextKey.Copy0Xu251e68,price),AppText.text(TextKey.CopyViCuaBanD87c21) to AppText.text(TextKey.Copy0Xu251e68,garden.coins)))
            if(garden.coins<price)Text(AppText.text(TextKey.CopyCanThem0XuD7d41b,price-garden.coins),color=Muted,fontSize=AppTypography.Sp12)
        }
    }
    selectedSeed?.let { seed ->
        val available = plots.any { it.isUnlocked && it.treeId == null &&
            (selectedPlotId == null || it.id == selectedPlotId) }
        if (available) PlantingDialog(seed, garden.inventory[seed.id] ?: 0,
            targetPlotId = selectedPlotId ?: plots.firstOrNull { it.isUnlocked && it.treeId == null }?.id,
            availablePlotIds = plots.filter { it.isUnlocked && it.treeId == null }.map { it.id },
            onSelectPlot = { selectedPlotId = it },
            onDismiss = onDismissPlanting,
            onPlant = { name ->
                onIntent(GardenIntent.Plant(seed.id, name, selectedPlotId)); onDismissPlanting()
            })
        else MintDialog(AppText.text(TextKey.CopyChuaCoOTrong6abd67),onDismissPlanting,AppText.text(TextKey.CopyXemOKhoa2d0677),{
            onDismissPlanting();unlockingPlotId=plots.firstOrNull{!it.isUnlocked}?.id
        }) {
            Text(AppText.text(TextKey.CopyHatVanOTrongTuiMoThemOTrongDeGieoCay54d8b7),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
        }
    }
    state.message?.let { message ->
        MintFeedback(message, { onIntent(GardenIntent.DismissMessage) })
    }
}

@Composable
private fun GardenOverviewCard(treeCount: Int, treesNeedingWater: Int, seedCount: Int) {
    Column(Modifier.fillMaxWidth().padding(top = AppDimensions.Dp4, bottom = AppDimensions.Dp4), horizontalAlignment = Alignment.CenterHorizontally) {
        GardenWoodSign(AppText.text(TextKey.CopyVuonUomCd0b43), Modifier.widthIn(max = AppDimensions.Dp285).fillMaxWidth(.85f))
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = AppDimensions.Dp20, vertical = AppDimensions.Dp6), cornerSize = AppDimensions.Dp20) {
            Text(AppText.text(TextKey.Copy0CayDangLonC1217a,treeCount), color = Deep, fontSize = AppTypography.Sp17, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.padding(top = AppDimensions.Dp3), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
            GardenMetric("💧", AppText.text(TextKey.Copy0CanTuoi425a0d,treesNeedingWater))
            GardenMetric("🌰", AppText.text(TextKey.Copy0HatB54ddd,seedCount))
        }
    }
}

@Composable
private fun GardenMetric(emoji: String, label: String, modifier: Modifier = Modifier) {
    GardenBotanicalFrame(modifier, contentPadding = PaddingValues(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp6), cornerSize = AppDimensions.Dp18) {
        Text("$emoji $label", color = Deep, fontSize = AppTypography.Sp14, fontWeight = FontWeight.Bold)
    }
}

/** A planted scene with a live name plaque; no card or flowerpot around the tree. */
@Composable
private fun GardenTreeIsland(tree: Tree, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClickLabel = AppText.text(TextKey.CopyChamSoc0E5e78b,tree.name), onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        PaintedGardenPlant(tree.speciesId, Modifier.fillMaxWidth().height(AppDimensions.Dp96))
        GardenBotanicalFrame(
            Modifier.fillMaxWidth(.84f).offset(y = AppDimensions.DpMinus8),
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp7), cornerSize = AppDimensions.Dp25
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp2)) {
                Text(tree.name, color = Deep, fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp17, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                LinearProgressIndicator(progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                    Modifier.fillMaxWidth().height(AppDimensions.Dp6).clip(CircleShape), color = Green, trackColor = AppColors.SwatchFFD7BD7C, drawStopIndicator = {})
                Text("${tree.growth.coerceIn(0,120)} / 120", color = Deep, fontSize = AppTypography.Sp12, lineHeight = AppTypography.Sp14, fontWeight = FontWeight.Bold)
                Text(if (tree.wateredToday) AppText.text(TextKey.CopyDaTuoiD97f10) else AppText.text(TextKey.CopyChoTuoi79f9ea),
                    color = if (tree.wateredToday) Green else AppColors.SwatchFF1F6481, fontSize = AppTypography.Sp13, lineHeight = AppTypography.Sp15, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private val PlantingDialogBounds = Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth(.92f).heightIn(max = AppDimensions.Dp700)

@Composable
private fun PlantingDialog(seed: Species,count: Int,targetPlotId: Int?,availablePlotIds: List<Int>,
    onSelectPlot:(Int)->Unit,onDismiss:()->Unit,onPlant:(String)->Unit) {
    var name by rememberSaveable(seed.id){mutableStateOf("")}
    var plotMenu by remember { mutableStateOf(false) }
    val requester=remember{BringIntoViewRequester()}
    val density=androidx.compose.ui.platform.LocalDensity.current
    var focused by remember{mutableStateOf(false)}
    val ime=WindowInsets.ime.getBottom(density)
    LaunchedEffect(focused,ime){if(focused && ime>0){withFrameNanos{};requester.bringIntoView()}}
    MintDialog(AppText.text(TextKey.CopyGieoCay31adde),onDismiss,AppText.text(TextKey.CopyGieoHat6d82a9),{onPlant(name)},confirmEnabled=count>0) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            PaintedGardenPlant(seed.id,Modifier.size(AppDimensions.Dp64))
            Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
                Text(seed.name,color=Deep,fontSize=AppTypography.Sp16,fontWeight=FontWeight.Bold)
                MintStatusPill(AppText.text(TextKey.CopyTrongTui0HatD56e0e,count))
            }
        }
        GardenTextField(name,{name=it},AppText.text(TextKey.CopyTenCayTuyChon3f3d38),Modifier.fillMaxWidth()
            .bringIntoViewRequester(requester).onFocusChanged{focused=it.hasFocus},placeholder=AppText.text(TextKey.CopyMamBinhYenDf7062))
        Text(AppText.text(TextKey.CopyBoTrongDeDungTenLoaiCayA7e5fc),color=Muted,fontSize=AppTypography.Sp11)
        Box(Modifier.fillMaxWidth()) {
            GardenFieldButton(AppText.text(TextKey.CopyOTrong0174f9e,targetPlotId?.toString()?.padStart(2,'0') ?: "—"),{plotMenu=true},Modifier.fillMaxWidth())
            DropdownMenu(expanded=plotMenu,onDismissRequest={plotMenu=false}) {
                availablePlotIds.forEach{id->DropdownMenuItem(text={Text(AppText.text(TextKey.CopyO03d2411,id.toString().padStart(2,'0')))},
                    onClick={onSelectPlot(id);plotMenu=false})}
            }
        }
        Text(AppText.text(TextKey.CopyDung1HatTrongTuiDeBatDauTrongBde1a7),color=Muted,fontSize=AppTypography.Sp12)
    }
}

// Compatibility for isolated reference previews; runtime and previews use the same dialog.
@Composable
private fun PlantingParchment(seed: Species,count:Int,onDismiss:()->Unit,onPlant:(String)->Unit,
    targetPlotId:Int?=4,availablePlotIds:List<Int> = listOf(4),onSelectPlot:(Int)->Unit={}) {
    PlantingDialog(seed,count,targetPlotId,availablePlotIds,onSelectPlot,onDismiss,onPlant)
}

private fun sampleGarden() = Garden(
    trees = listOf(Tree(1,"cherry",AppText.text(TextKey.CopyDaoHong76f74f),68,"2026-01-01"), Tree(2,"pine",AppText.text(TextKey.CopyThongNho84a4db),24,"2026-01-02", wateredToday=true), Tree(3,"succulent",AppText.text(TextKey.CopySenDaB2d005),12,"2026-01-03")),
    inventory = mapOf("cherry" to 1, "pine" to 1)
)

@Preview(name = "Nursery · normal", widthDp = 360, heightDp = 800)
@Preview(name = "Nursery · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun GardenScreenPopulatedPreview() { MamXanhPreviewTheme { GardenScreen(GardenUiState(sampleGarden(), false), {}) } }

@Preview(name = "Nursery · extended plot audit", widthDp = 360, heightDp = 990,
    device = "spec:width=360dp,height=990dp,dpi=160")
@Composable
fun GardenExtendedPlotsPreview() {
    val fixture = sampleGarden().copy(plots = listOf(
        com.mamxanh.shared.model.GardenPlot(1, true, 1L),
        com.mamxanh.shared.model.GardenPlot(2, true, 2L),
        com.mamxanh.shared.model.GardenPlot(3, true),
        com.mamxanh.shared.model.GardenPlot(4, true, 3L),
        com.mamxanh.shared.model.GardenPlot(5, true),
        com.mamxanh.shared.model.GardenPlot(6, false)
    ), coins = 120)
    MamXanhPreviewTheme { GardenScreen(GardenUiState(fixture, false), {}) }
}

@Preview(name = "Nursery · empty", widthDp = 360, heightDp = 800)
@Composable
fun GardenScreenEmptyPreview() { MamXanhPreviewTheme { GardenScreen(GardenUiState(Garden(), false), {}) } }

@Preview(name = "Planting parchment", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun PlantingDialogPreview(background: (@Composable () -> Unit)? = null) {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (background == null) MintBackdrop(Modifier.fillMaxSize()) {}
            else {
                background()
                Box(Modifier.matchParentSize().background(AppColors.Black.copy(alpha = AppOpacity.Value_32)))
            }
            Box(PlantingDialogBounds) { PlantingParchment(seedCatalog.first { it.id == "cherry" }, 1, {}, {}) }
        }
    }
}

@Preview(name = "Planting · large text, no stock", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun PlantingDialogLargeTextPreview() { MamXanhPreviewTheme { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { MintBackdrop(Modifier.fillMaxSize()) {}; Box(PlantingDialogBounds) { PlantingParchment(seedCatalog.first { it.id == "cherry" }, 0, {}, {}) } } } }

@Preview(name = "Planting · short window, large text", widthDp = 640, heightDp = 180,
    fontScale = 1.3f, device = "spec:width=640dp,height=180dp,dpi=160")
@Preview(name = "Planting · landscape IME height", widthDp = 640, heightDp = 97,
    device = "spec:width=640dp,height=97dp,dpi=160")
@Composable
fun PlantingShortWindowPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth(.92f)) {
                PlantingParchment(seedCatalog.first { it.id == "cherry" }, 1, {}, {})
            }
        }
    }
}

@Preview(name = "Nursery overview", widthDp = 360)
@Composable
fun GardenOverviewCardPreview() { MamXanhPreviewTheme { GardenOverviewCard(3,2,2) } }

@Preview(name = "Planted island", widthDp = 180)
@Composable
fun GardenTreeCardPreview() { MamXanhPreviewTheme { GardenTreeIsland(sampleGarden().trees.first(), {}) } }
