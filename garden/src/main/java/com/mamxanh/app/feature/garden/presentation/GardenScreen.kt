package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
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
    onDismissPlanting: () -> Unit = {}) {
    val garden = state.garden
    val listState = rememberLazyListState()
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
            MintHeader("Mầm Xanh", garden.coins)
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Vườn ươm", Modifier.weight(1f), color = Deep, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onOpenInventory(null) }) {
                    Icon(androidx.compose.material.icons.Icons.Rounded.Inventory2, "Túi đồ", tint = Green)
                }
                GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("${garden.trees.size} cây · ${plots.count { it.isUnlocked }} ô mở", color = Deep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            LazyColumn(Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item(key = "care-toolbar") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GardenPaintedButton("Tưới tất cả · ${garden.waterAll.eligible} cây",
                            { waterAllKey = java.util.UUID.randomUUID().toString() }, Modifier.fillMaxWidth(),
                            enabled = garden.waterAll.eligible > 0 && !state.isLoading)
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Tất cả","Cần tưới","Đang lớn","Trưởng thành","Khô héo").forEachIndexed { index,label ->
                                FilterChip(selected = filter == index, onClick = {filter=index}, label = {Text(label)},
                                    shape=RoundedCornerShape(20.dp),
                                    colors=FilterChipDefaults.filterChipColors(selectedContainerColor=Green,selectedLabelColor=Color.White,labelColor=Deep))
                            }
                        }
                        if(garden.waterAll.eligible == 0) Text("Không có cây cần tưới lúc này.",color=Muted,fontSize=12.sp)
                    }
                }
                if(visiblePlots.isEmpty()) item { Text("Chưa có cây phù hợp.",color=Muted) }
                items(visiblePlots, key = { it.id }) { plot ->
                    val tree = garden.trees.firstOrNull { it.id == plot.treeId }
                    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled = !state.isLoading) {
                        if(tree != null) onIntent(GardenIntent.OpenTree(tree.id))
                        else if(plot.isUnlocked) onOpenInventory(plot.id)
                        else unlockingPlotId = plot.id
                    }, contentPadding = PaddingValues(10.dp), cornerSize = 22.dp) {
                        Row(Modifier.fillMaxWidth().heightIn(min = 86.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if(tree != null) GardenPlantPortrait(tree.speciesId, tree.decoration,
                                Modifier.size(84.dp).then(treeTransitionModifier(tree.id)), growth = tree.growth, withered = !tree.status.alive)
                            else if(plot.isUnlocked) MintEmptySprout(Modifier.size(74.dp))
                            else Text("🔒", Modifier.width(64.dp), fontSize = 32.sp, textAlign = TextAlign.Center)
                            GardenBotanicalFrame(contentPadding = PaddingValues(6.dp), cornerSize = 50.dp) {
                                Text(plot.id.toString().padStart(2, '0'), fontWeight = FontWeight.Bold, color = Deep)
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(tree?.name ?: if(plot.isUnlocked) "Ô trống" else "Ô trống (Đang khóa)",
                                    fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Deep)
                                if(tree != null) {
                                    MintProgress(tree.status.stageProgress, Modifier.fillMaxWidth())
                                    Text("${tree.status.stageLabel} · ${tree.status.stateLabel}", fontSize = 12.sp, color = Deep)
                                    Text("${tree.status.percentage}% · ${tree.status.waterLabel}", fontSize = 12.sp, color = Muted)
                                    if(tree.status.fertilizerLabel.isNotBlank()) Text(tree.status.fertilizerLabel,fontSize=11.sp,color=Green)
                                } else {
                                    GardenPaintedButton(if(plot.isUnlocked) "Gieo cây" else "Mở ô · ${com.mamxanh.shared.model.Balance.plot(garden.plots.count { it.isUnlocked })} xu",
                                        {
                                            if(plot.isUnlocked) onOpenInventory(plot.id)
                                            else unlockingPlotId = plot.id
                                        }, Modifier.fillMaxWidth(), primary = plot.isUnlocked)
                                }
                            }
                            Text("›", fontSize = 26.sp, color = Deep)
                        }
                    }
                }
                item(key = "inventory-shortcut") {
                    GardenPaintedButton("Túi đồ · Vật phẩm và hạt giống", { onOpenInventory(null) },
                        Modifier.fillMaxWidth(), primary = false)
                }
            }
        }
    }
    waterAllKey?.let { key ->
        AlertDialog(onDismissRequest={waterAllKey=null},title={Text("Tưới tất cả?")},text={
            Text("Tưới miễn phí\nSẽ tưới: ${garden.waterAll.eligible}\nĐã được tưới: ${garden.waterAll.active}\nĐã khô héo: ${garden.waterAll.dead}\nCây trưởng thành được tưới để duy trì sức sống.")
        },confirmButton={TextButton(onClick={onIntent(GardenIntent.WaterAll(key));waterAllKey=null},
            enabled=!state.isLoading && garden.waterAll.eligible>0){Text("Tưới cây")}},
            dismissButton={TextButton(onClick={waterAllKey=null}){Text("Để sau")}})
    }
    unlockingPlotId?.let { id ->
        val price = com.mamxanh.shared.model.Balance.plot(garden.plots.count { it.isUnlocked })
        Dialog(onDismissRequest = { unlockingPlotId = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
                GardenBotanicalFrame(Modifier.widthIn(max = 440.dp).fillMaxWidth(.92f).heightIn(max = 520.dp),
                    contentPadding = PaddingValues(22.dp), cornerSize = 32.dp, skin = GardenPanelSkin.DIALOG) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            GardenArtwork(GardenArt.SPROUT, Modifier.size(32.dp))
                            Text("Mở thêm ô trồng", Modifier.weight(1f), color = Deep,
                                fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            TextButton(onClick = { unlockingPlotId = null }) { Text("×", fontSize = 24.sp) }
                        }
                        Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Ô " + id.toString().padStart(2, '0'), color = Deep, fontWeight = FontWeight.Bold)
                        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("🪙 Số xu hiện có: ${garden.coins}", color = Deep)
                        }
                        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("Giá: $price xu", color = Deep, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        if (garden.coins < price) Text("Chưa đủ xu để mở ô trồng.", color = Deep)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GardenPaintedButton("Để sau", { unlockingPlotId = null }, Modifier.weight(1f), primary = false)
                            GardenPaintedButton("Mở ô", {
                                onIntent(GardenIntent.UnlockPlot(id)); unlockingPlotId = null
                            }, Modifier.weight(1f), enabled = garden.coins >= price && !state.isLoading)
                        }
                    }
                }
            }
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
        else AlertDialog(onDismissRequest = { onDismissPlanting() },
            title = { Text("Chưa có ô trống") },
            text = { Text("Hạt vẫn ở trong túi. Mở thêm ô trồng để gieo cây.") },
            confirmButton = { TextButton(onClick = {
                onDismissPlanting()
                unlockingPlotId = plots.firstOrNull { !it.isUnlocked }?.id
            }) { Text("Xem ô bị khóa") } },
            dismissButton = { TextButton(onClick = { onDismissPlanting() }) { Text("Để sau") } })
    }
    state.message?.let { message ->
        MintFeedback(message, { onIntent(GardenIntent.DismissMessage) })
    }
}

@Composable
private fun GardenOverviewCard(treeCount: Int, treesNeedingWater: Int, seedCount: Int) {
    Column(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        GardenWoodSign("Vườn ươm", Modifier.widthIn(max = 285.dp).fillMaxWidth(.85f))
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp), cornerSize = 20.dp) {
            Text("🌿  $treeCount cây đang lớn  🌿", color = Deep, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GardenMetric("💧", "$treesNeedingWater cần tưới")
            GardenMetric("🌰", "$seedCount hạt")
        }
    }
}

@Composable
private fun GardenMetric(emoji: String, label: String, modifier: Modifier = Modifier) {
    GardenBotanicalFrame(modifier, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp), cornerSize = 18.dp) {
        Text("$emoji $label", color = Deep, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/** A planted scene with a live name plaque; no card or flowerpot around the tree. */
@Composable
private fun GardenTreeIsland(tree: Tree, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClickLabel = "Chăm sóc ${tree.name}", onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        PaintedGardenPlant(tree.speciesId, Modifier.fillMaxWidth().height(96.dp))
        GardenBotanicalFrame(
            Modifier.fillMaxWidth(.84f).offset(y = (-8).dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp), cornerSize = 25.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(tree.name, color = Deep, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                LinearProgressIndicator(progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                    Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = Green, trackColor = Color(0xFFD7BD7C), drawStopIndicator = {})
                Text("${tree.growth.coerceIn(0,120)} / 120", color = Deep, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                Text(if (tree.wateredToday) "✓ Đã tưới" else "💧 Chờ tưới",
                    color = if (tree.wateredToday) Green else Color(0xFF1F6481), fontSize = 13.sp, lineHeight = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private val PlantingDialogBounds = Modifier.widthIn(max = 440.dp).fillMaxWidth(.92f).heightIn(max = 700.dp)

@Composable
private fun PlantingDialog(seed: Species, count: Int, targetPlotId: Int?, availablePlotIds: List<Int>, onSelectPlot: (Int) -> Unit, onDismiss: () -> Unit, onPlant: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Box(PlantingDialogBounds) { PlantingParchment(seed, count, onDismiss, onPlant, targetPlotId, availablePlotIds, onSelectPlot) }
        }
    }
}

@Composable
private fun PlantingParchment(seed: Species, count: Int, onDismiss: () -> Unit, onPlant: (String) -> Unit,
    targetPlotId: Int? = 4, availablePlotIds: List<Int> = listOf(4), onSelectPlot: (Int) -> Unit = {}) {
    var plotMenu by remember { mutableStateOf(false) }
    var name by rememberSaveable(seed.id) { mutableStateOf("") }
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fontScale = density.fontScale
    val nameBringIntoView = remember { BringIntoViewRequester() }
    var nameFocused by remember { mutableStateOf(false) }
    val imeBottom = WindowInsets.ime.getBottom(density)
    LaunchedEffect(nameFocused, imeBottom) {
        if (nameFocused && imeBottom > 0) {
            withFrameNanos { }
            nameBringIntoView.bringIntoView()
        }
    }
    BoxWithConstraints {
        val compact = maxHeight < 360.dp * fontScale
        val veryShort = maxHeight < 160.dp * fontScale
        GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(
            horizontal = if (veryShort) 12.dp else 26.dp,
            vertical = if (veryShort) 0.dp else if (compact) 4.dp else 30.dp),
            cornerSize = 42.dp, skin = GardenPanelSkin.PLANTING_DIALOG) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (veryShort) 0.dp else if (compact) 4.dp else 12.dp)) {
                if (!veryShort) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Gieo cây", Modifier.weight(1f), fontSize = if (compact) 18.sp else 22.sp, fontWeight = FontWeight.Bold, color = Deep)
                    PlantingCloseButton(onDismiss)
                }
                Box(Modifier.weight(1f, fill = false).fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(end = if (veryShort) 56.dp else 0.dp)
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (veryShort) Text("Gieo cây", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Deep)
                    GardenArtwork(GardenArt.SEED, Modifier.size(150.dp, 100.dp))
                    GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp), cornerSize = 18.dp) {
                        Text("${seed.name} · Còn $count", color = Deep, fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                    Text("✏️ Tên cây · tùy chọn", color = Deep, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    GardenTextField(name, { name = it }, "Tên cây tùy chọn", Modifier.fillMaxWidth()
                        .bringIntoViewRequester(nameBringIntoView)
                        .onFocusChanged { nameFocused = it.hasFocus }, placeholder = "Mầm bình yên")
                    Text("Bỏ trống để dùng tên loài cây.", color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Box(Modifier.fillMaxWidth()) {
                        GardenFieldButton("Ô đất mục tiêu · Ô số " + (targetPlotId?.toString()?.padStart(2, '0') ?: "—") + " (trống)",
                            { plotMenu = true }, Modifier.fillMaxWidth())
                        DropdownMenu(expanded = plotMenu, onDismissRequest = { plotMenu = false }) {
                            availablePlotIds.forEach { id ->
                                DropdownMenuItem(text = { Text("Ô số " + id.toString().padStart(2, '0')) },
                                    onClick = { onSelectPlot(id); plotMenu = false })
                            }
                        }
                    }
                    GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        cornerSize = 18.dp, skin = GardenPanelSkin.HINT) {
                        Text("Gieo cây sẽ dùng 1 hạt trong túi.", color = Deep, fontSize = 15.sp,
                            fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                    }
                }
                if (veryShort) PlantingCloseButton(onDismiss, Modifier.align(Alignment.TopEnd))
                }
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GardenPaintedButton("Để sau", onDismiss, Modifier.weight(1f).fillMaxHeight(), primary = false)
                    GardenPaintedButton("Gieo cây", { onPlant(name) }, Modifier.weight(1.2f).fillMaxHeight(), enabled = count > 0)
                }
            }
        }

    }
}

@Composable
private fun PlantingCloseButton(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onDismiss, modifier = modifier.size(48.dp)
        .background(Color(0xFFF5FCF8), CircleShape)
        .drawBehind {
            drawCircle(Color(0xFFC9DDD2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
        }
        .semantics { contentDescription = "Đóng gieo cây" }) {
        Text("×", color = Deep, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

private fun sampleGarden() = Garden(
    trees = listOf(Tree(1,"cherry","Đào hồng",68,"2026-01-01"), Tree(2,"pine","Thông nhỏ",24,"2026-01-02", wateredToday=true), Tree(3,"succulent","Sen đá",12,"2026-01-03")),
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
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
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
            Box(Modifier.widthIn(max = 440.dp).fillMaxWidth(.92f)) {
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
