package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.foundation.horizontalScroll
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    treeTransitionModifier: @Composable (Long) -> Modifier = { Modifier }) {
    val garden = state.garden
    var selectedSeedId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedPlotId by rememberSaveable { mutableStateOf<Int?>(null) }
    var choosingSeed by rememberSaveable { mutableStateOf(false) }
    var unlockingPlotId by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedSeed = seedCatalog.firstOrNull { it.id == selectedSeedId }
    val seeds = seedCatalog.filter { (garden.inventory[it.id] ?: 0) > 0 }
    val plots = garden.plots.ifEmpty {
        // Preview/legacy callers still get explicit empty slots without fake saved trees.
        val count = maxOf(5, garden.trees.size)
        (1..count).map { com.mamxanh.shared.model.GardenPlot(it, true, garden.trees.getOrNull(it - 1)?.id) }
    }
    var previousAssignments by remember { mutableStateOf<Map<Int, Long?>?>(null) }
    var revealedTreeId by remember { mutableStateOf<Long?>(null) }
    val plantingGlow = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(plots, state.isLoading) {
        if (!state.isLoading) {
            val assignments = plots.associate { it.id to it.treeId }
            previousAssignments?.let { previous ->
                assignments.entries.firstOrNull { (id, treeId) ->
                    treeId != null && previous.containsKey(id) && previous[id] == null
                }?.value?.let { revealedTreeId = it }
            }
            previousAssignments = assignments
        }
    }
    LaunchedEffect(revealedTreeId) {
        if (revealedTreeId != null) {
            plantingGlow.snapTo(1f)
            plantingGlow.animateTo(0f, androidx.compose.animation.core.tween(1400))
            revealedTreeId = null
        }
    }
    Box(modifier.fillMaxSize()) {
        GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.GARDEN)
        Column(Modifier.fillMaxSize().padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            GardenWoodSign("Vườn ươm", Modifier.widthIn(max = 285.dp).fillMaxWidth(.85f))
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)) {
                Text("${garden.trees.size} cây · ${plots.count { it.isUnlocked }} ô mở", color = Deep,
                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                plots.forEach { plot ->
                    val tree = garden.trees.firstOrNull { it.id == plot.treeId }
                    Row(Modifier.fillMaxWidth().heightIn(min = 150.dp).drawBehind {
                        // The clearing and its path belong to the scrolling plot, not the fixed forest.
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width * .34f, 0f)
                            cubicTo(size.width * .75f, size.height * .28f, size.width * .12f,
                                size.height * .72f, size.width * .62f, size.height)
                        }
                        drawPath(path, Color(0xFFCFAB70),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.width * .16f))
                        drawOval(Color(0xFF95A64A), Offset(size.width * .01f, size.height * .40f),
                            Size(size.width * .96f, size.height * .60f))
                        drawOval(Color(0xFFC3A16C), Offset(size.width * .04f, size.height * .43f),
                            Size(size.width * .90f, size.height * .51f))
                        drawOval(Color(0xFFD8BB88), Offset(size.width * .09f, size.height * .46f),
                            Size(size.width * .80f, size.height * .42f))
                        if (tree != null && tree.id == revealedTreeId) {
                            drawOval(Color(0xFFFFD85A).copy(alpha = plantingGlow.value),
                                Offset(size.width * .04f, size.height * .43f),
                                Size(size.width * .90f, size.height * .51f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(5.dp.toPx()))
                        }
                    },
                        verticalAlignment = Alignment.CenterVertically) {
                        if (tree != null) {
                            GardenPlantPortrait(tree.speciesId, tree.decoration,
                                Modifier.weight(1f).height(150.dp).then(treeTransitionModifier(tree.id))
                                    .clickable(onClickLabel = "Chăm sóc ${tree.name}") { onIntent(GardenIntent.OpenTree(tree.id)) }, growth = tree.growth)
                        } else {
                            Box(Modifier.weight(1f).height(120.dp), contentAlignment = Alignment.Center) {
                                Text(if (plot.isUnlocked) "+" else "🔒", color = Green,
                                    fontSize = 42.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        GardenBotanicalFrame(Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(plot.id.toString().padStart(2, '0'), color = Deep,
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                if (tree != null) {
                                    Text(tree.name, color = Deep, textAlign = TextAlign.Center,
                                        modifier = Modifier.clickable { onIntent(GardenIntent.OpenTree(tree.id)) })
                                    LinearProgressIndicator(progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(6.dp), color = Green,
                                        trackColor = Color(0xFFD6B78D), drawStopIndicator = {})
                                    Text("${tree.growth} / 120", color = Deep, fontWeight = FontWeight.Bold)
                                } else if (plot.isUnlocked) {
                                    GardenPaintedButton("Gieo cây", {
                                        selectedPlotId = plot.id
                                        choosingSeed = true
                                    }, enabled = !state.isLoading)
                                } else {
                                    Text("Đang khóa", color = Deep)
                                    GardenPaintedButton("Mở ô · ${com.mamxanh.shared.model.GardenPlotPolicy.UNLOCK_PRICE} xu",
                                        { unlockingPlotId = plot.id }, enabled = !state.isLoading)
                                }
                            }
                        }
                    }
                }
                SeedBasket(seeds, garden.inventory, onSelect = {
                    selectedPlotId = null
                    selectedSeedId = it.id
                }, onOpenShop = { onIntent(GardenIntent.OpenShop) })
            }
        }
    }
    if (choosingSeed) {
        AlertDialog(onDismissRequest = { choosingSeed = false },
            title = { Text(if (seeds.isEmpty()) "Bạn chưa có hạt" else "Chọn hạt để gieo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    seeds.forEach { seed ->
                        GardenPaintedButton("${seed.name} · ${garden.inventory[seed.id] ?: 0}", {
                            selectedSeedId = seed.id
                            choosingSeed = false
                        })
                    }
                }
            },
            confirmButton = {
                if (seeds.isEmpty()) TextButton(onClick = {
                    choosingSeed = false; onIntent(GardenIntent.OpenShop)
                }) { Text("Tiệm hạt") }
            },
            dismissButton = { TextButton(onClick = { choosingSeed = false }) { Text("Để sau") } })
    }
    unlockingPlotId?.let { id ->
        val price = com.mamxanh.shared.model.GardenPlotPolicy.UNLOCK_PRICE
        Dialog(onDismissRequest = { unlockingPlotId = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
                GardenBotanicalFrame(Modifier.widthIn(max = 440.dp).fillMaxWidth(.92f).heightIn(max = 520.dp),
                    contentPadding = PaddingValues(22.dp), cornerSize = 32.dp, skin = GardenPanelSkin.DIALOG) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            GardenArtwork(GardenArt.FLOWERS, Modifier.size(32.dp))
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
            onDismiss = { selectedSeedId = null },
            onPlant = { name ->
                onIntent(GardenIntent.Plant(seed.id, name, selectedPlotId)); selectedSeedId = null
            })
        else AlertDialog(onDismissRequest = { selectedSeedId = null },
            title = { Text("Chưa có ô trống") },
            text = { Text("Hạt vẫn ở trong túi. Mở thêm ô trồng để gieo cây.") },
            confirmButton = { TextButton(onClick = {
                selectedSeedId = null
                unlockingPlotId = plots.firstOrNull { !it.isUnlocked }?.id
            }) { Text("Xem ô bị khóa") } },
            dismissButton = { TextButton(onClick = { selectedSeedId = null }) { Text("Để sau") } })
    }
    state.message?.let { message ->
        AlertDialog(onDismissRequest = { onIntent(GardenIntent.DismissMessage) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { onIntent(GardenIntent.DismissMessage) }) { Text("Đã hiểu") } })
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

@Composable
private fun SeedBasket(seeds: List<Species>, inventory: Map<String, Int>, onSelect: (Species) -> Unit,
    onOpenShop: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp), cornerSize = 20.dp) {
            Text("🌱 Hạt trong túi", color = Deep, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().heightIn(min = 150.dp), contentAlignment = Alignment.BottomCenter) {
            GardenSeedBasketSurface(Modifier.matchParentSize())
            if (seeds.isEmpty()) {
                GardenBotanicalFrame(Modifier.padding(22.dp), contentPadding = PaddingValues(14.dp), cornerSize = 22.dp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Túi hạt đang trống", color = Deep, fontSize = 13.sp, textAlign = TextAlign.Center)
                        GardenPaintedButton("Ghé Tiệm →", onOpenShop, primary = false)
                    }
                }
            } else Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    seeds.forEach { seed ->
                        GardenBotanicalFrame(Modifier.width(78.dp).heightIn(min = 108.dp)
                            .clickable(onClickLabel = "Gieo ${seed.name}, còn ${inventory[seed.id] ?: 0} hạt", onClick = { onSelect(seed) }),
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 8.dp), cornerSize = 14.dp) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                PaintedGardenPlant(seed.id, Modifier.size(46.dp, 40.dp))
                                Text(when(seed.id) { "cherry" -> "Đào"; "pine" -> "Thông"; "succulent" -> "Sen đá"; "mandarin" -> "Quýt"; else -> seed.name },
                                    color = Deep, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text("×${inventory[seed.id] ?: 0}", color = Deep, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                GardenPaintedButton("Gieo hạt", { onSelect(seeds.first()) }, Modifier.width(112.dp))
            }
        }
    }
}

private val PlantingDialogBounds = Modifier.widthIn(max = 440.dp).fillMaxWidth(.92f).heightIn(max = 700.dp)

@Composable
private fun PlantingDialog(seed: Species, count: Int, onDismiss: () -> Unit, onPlant: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Box(PlantingDialogBounds) { PlantingParchment(seed, count, onDismiss, onPlant) }
        }
    }
}

@Composable
private fun PlantingParchment(seed: Species, count: Int, onDismiss: () -> Unit, onPlant: (String) -> Unit) {
    var name by rememberSaveable(seed.id) { mutableStateOf("") }
    Box {
        GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 26.dp, vertical = 30.dp), cornerSize = 42.dp, skin = GardenPanelSkin.PLANTING_DIALOG) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    GardenLeafPlaque("Gieo mầm bình yên", Modifier.weight(1f))
                    TextButton(onClick = onDismiss, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
                        Text("×", fontSize = 24.sp)
                    }
                }
                Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GardenArtwork(GardenArt.SEED, Modifier.size(150.dp, 100.dp))
                    GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp), cornerSize = 18.dp) {
                        Text("${seed.name} · Còn $count", color = Deep, fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                    Text("✏️ Tên cây · tùy chọn", color = Deep, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    GardenTextField(name, { name = it }, "Tên cây tùy chọn", Modifier.fillMaxWidth(), placeholder = "Mầm bình yên")
                    Text("Bỏ trống để dùng tên loài cây.", color = Deep, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                    GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        cornerSize = 18.dp, skin = GardenPanelSkin.HINT) {
                        Text("Gieo cây sẽ dùng 1 hạt trong túi.", color = Deep, fontSize = 15.sp,
                            fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GardenPaintedButton("Để sau", onDismiss, Modifier.weight(1f), primary = false)
                    GardenPaintedButton("Gieo cây", { onPlant(name) }, Modifier.weight(1.2f), enabled = count > 0)
                }
            }
        }
        GardenBookHandles(Modifier.matchParentSize())
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

@Preview(name = "Nursery · empty", widthDp = 360, heightDp = 800)
@Composable
fun GardenScreenEmptyPreview() { MamXanhPreviewTheme { GardenScreen(GardenUiState(Garden(), false), {}) } }

@Preview(name = "Planting parchment", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun PlantingDialogPreview(background: (@Composable () -> Unit)? = null) {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (background == null) GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.GARDEN)
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
fun PlantingDialogLargeTextPreview() { MamXanhPreviewTheme { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.GARDEN); Box(PlantingDialogBounds) { PlantingParchment(seedCatalog.first { it.id == "cherry" }, 0, {}, {}) } } } }

@Preview(name = "Nursery overview", widthDp = 360)
@Composable
fun GardenOverviewCardPreview() { MamXanhPreviewTheme { GardenOverviewCard(3,2,2) } }

@Preview(name = "Planted island", widthDp = 180)
@Composable
fun GardenTreeCardPreview() { MamXanhPreviewTheme { GardenTreeIsland(sampleGarden().trees.first(), {}) } }

@Preview(name = "Seed basket · long labels, large text", widthDp = 360, heightDp = 220, fontScale = 1.3f)
@Composable
fun SeedBasketLargeTextPreview() {
    MamXanhPreviewTheme {
        SeedBasket(seedCatalog.filter { it.id in setOf("monstera", "succulent") },
            mapOf("monstera" to 12, "succulent" to 24), {})
    }
}
