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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun GardenScreen(state: GardenUiState, onIntent: (GardenIntent) -> Unit, modifier: Modifier = Modifier) {
    val garden = state.garden
    var selectedSeed by remember { mutableStateOf<Species?>(null) }
    val seeds = seedCatalog.filter { (garden.inventory[it.id] ?: 0) > 0 }
    Box(modifier.fillMaxSize()) {
        GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.GARDEN)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GardenOverviewCard(garden.trees.size, garden.trees.count { !it.wateredToday }, seeds.sumOf { garden.inventory[it.id] ?: 0 })
            Spacer(Modifier.height(20.dp))
            if (state.isLoading && garden.trees.isEmpty()) {
                CircularProgressIndicator(Modifier.padding(48.dp), color = Color(0xFFFFDF89))
            } else if (garden.trees.isEmpty()) {
                GardenBotanicalFrame(Modifier.padding(vertical = 32.dp).fillMaxWidth(), contentPadding = PaddingValues(30.dp), cornerSize = 36.dp, skin = GardenPanelSkin.DIALOG) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GardenArtwork(GardenArt.SEED, Modifier.size(140.dp, 100.dp))
                        Text("Mầm xanh đầu tiên đang chờ", color = Deep, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("Chọn một hạt trong túi để bắt đầu khu vườn.", color = Deep, textAlign = TextAlign.Center)
                    }
                }
            } else {
                garden.trees.chunked(2).forEachIndexed { index, trees ->
                    Row(Modifier.fillMaxWidth().padding(top = if (index == 0) 0.dp else 6.dp), verticalAlignment = Alignment.Top) {
                        trees.forEachIndexed { column, tree ->
                            GardenTreeIsland(
                                tree, onClick = { onIntent(GardenIntent.OpenTree(tree.id)) },
                                modifier = Modifier.weight(1f).padding(top = if (column == 1) 22.dp else 0.dp)
                            )
                        }
                        if (trees.size == 1) {
                            Box(Modifier.weight(1f).height(145.dp), contentAlignment = Alignment.Center) {
                                GardenBotanicalFrame(contentPadding = PaddingValues(16.dp), cornerSize = 22.dp, skin = GardenPanelSkin.HINT) {
                                    Text("Chạm cây\nđể chăm sóc", color = Deep, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
            SeedBasket(seeds, garden.inventory, onSelect = { selectedSeed = it },
                onOpenShop = { onIntent(GardenIntent.OpenShop) })
            Spacer(Modifier.height(8.dp))
        }
    }
    selectedSeed?.let { seed ->
        PlantingDialog(seed, garden.inventory[seed.id] ?: 0,
            onDismiss = { selectedSeed = null },
            onPlant = { name -> onIntent(GardenIntent.Plant(seed.id, name)); selectedSeed = null })
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(PlantingDialogBounds) { PlantingParchment(seed, count, onDismiss, onPlant) }
    }
}

@Composable
private fun PlantingParchment(seed: Species, count: Int, onDismiss: () -> Unit, onPlant: (String) -> Unit) {
    var name by remember(seed.id) { mutableStateOf("") }
    Box {
        GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 26.dp, vertical = 30.dp), cornerSize = 42.dp, skin = GardenPanelSkin.PLANTING_DIALOG) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GardenLeafPlaque("Gieo mầm bình yên", Modifier.fillMaxWidth())
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
