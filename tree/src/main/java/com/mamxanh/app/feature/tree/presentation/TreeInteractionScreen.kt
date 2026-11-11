package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.feature.tree.presentation.components.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeInteractionScreen(
    tree: Tree, state: TreeUiState, fertilizerCount: Int, wateringCanMl: Int,
    onIntent: (TreeIntent) -> Unit, modifier: Modifier = Modifier, sharedTreeModifier: Modifier = Modifier,
    coins: Int = 0, onPrevious: (() -> Unit)? = null, onNext: (() -> Unit)? = null,
    onOpenGarden: () -> Unit = { onIntent(TreeIntent.Back) }
) {
    val scroll = rememberScrollState()
    var tab by rememberSaveable(tree.id) { mutableIntStateOf(0) }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(coins)
            val heroHeight = mintTreeHeroHeight()
            Column(Modifier.weight(1f).mintScrollEdges(scroll.canScrollBackward, scroll.canScrollForward)
                .verticalScroll(scroll)
                .padding(horizontal = 12.dp).padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .clickable(onClick = onOpenGarden),
                    contentPadding = PaddingValues(8.dp), cornerSize = 18.dp) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GardenArtwork(GardenArt.SPROUT, Modifier.size(24.dp))
                        Text("Vườn ươm", Modifier.weight(1f), fontWeight = FontWeight.Bold,
                            fontSize = 13.sp, lineHeight = 16.sp, color = Deep)
                        Text("›", fontSize = 22.sp, color = Deep)
                    }
                }
                Box(Modifier.fillMaxWidth().heightIn(min = heroHeight), contentAlignment = Alignment.Center) {
                    GardenPlantPortrait(tree.speciesId, tree.decoration,
                        sharedTreeModifier.fillMaxWidth().padding(horizontal = 40.dp).height(heroHeight),
                        tree.growth, fitVisibleArtwork = true, artworkAlignment = Alignment.Center)
                    Column(Modifier.align(Alignment.CenterEnd).width(48.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        MintCareIcon(GardenArt.WATERING_CAN,
                            if (tree.wateredToday) "Đã tưới hôm nay" else "Tưới · 50 ml",
                            !tree.wateredToday && wateringCanMl >= 50,
                            { onIntent(TreeIntent.Water(tree.id)) }, checked = tree.wateredToday)
                        MintCareIcon(GardenArt.FERTILIZER, "Bón · $fertilizerCount phân bón",
                            fertilizerCount > 0 && tree.growth < 120, { onIntent(TreeIntent.Fertilize(tree.id)) })
                        MintCareIcon(GardenArt.PUMP, "Bơm nước", true,
                            { onIntent(TreeIntent.OpenWaterSupply) })
                        MintCareIcon(GardenArt.DROP, "Nước $wateringCanMl ml", true,
                            { onIntent(TreeIntent.OpenWaterSupply) }, caption = "$wateringCanMl\nml")
                    }
                }
                GardenBotanicalFrame(Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp), cornerSize = 18.dp) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onPrevious?.invoke() }, enabled = onPrevious != null) {
                            Text("‹", fontSize = 24.sp, color = if (onPrevious != null) Deep else Muted)
                        }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(tree.name, color = Deep, fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, lineHeight = 17.sp, textAlign = TextAlign.Center)
                            MintProgress(tree.growth / 120f, Modifier.fillMaxWidth())
                            Text("${tree.growth.coerceIn(0,120)} / 120", color = Deep,
                                fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { onNext?.invoke() }, enabled = onNext != null) {
                            Text("›", fontSize = 24.sp, color = if (onNext != null) Deep else Muted)
                        }
                    }
                }
                MintSegments(listOf("Chăm sóc", "Trang trí", "Thông tin"), tab,
                    { tab = it; onIntent(TreeIntent.SetPanelExpanded(true)) })
            }
        }
        if(state.isPanelExpanded) ModalBottomSheet(
            onDismissRequest = { onIntent(TreeIntent.SetPanelExpanded(false)) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFFF1FAF5), contentColor = Deep
        ) {
            Column(Modifier.fillMaxWidth().fillMaxHeight(.75f).navigationBarsPadding().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MintSegments(listOf("Chăm sóc", "Trang trí", "Thông tin"), tab, { tab = it })
                ExpandedCarePanel(tree, fertilizerCount, wateringCanMl, onIntent, Modifier.weight(1f), tab)
                GardenPaintedButton("Đóng", { onIntent(TreeIntent.SetPanelExpanded(false)) }, Modifier.fillMaxWidth(), primary = false)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ExpandedCarePanel(tree: Tree, fertilizerCount: Int, wateringCanMl: Int,
    onIntent: (TreeIntent) -> Unit, modifier: Modifier = Modifier, tab: Int = 0) {
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        when(tab) {
            0 -> {
                TreeCareActions(tree.wateredToday, fertilizerCount, wateringCanMl,
                    { onIntent(TreeIntent.Water(tree.id)) }, { onIntent(TreeIntent.Fertilize(tree.id)) },
                    { onIntent(TreeIntent.OpenWaterSupply) }, { onIntent(TreeIntent.OpenFertilizerShop) })
                GardenBotanicalFrame(Modifier.fillMaxWidth()) { Text("💧 Tưới nước · Dùng 50 ml\nCòn $wateringCanMl ml", color = Deep) }
                GardenBotanicalFrame(Modifier.fillMaxWidth()) { Text("🌱 Bón phân · Dùng 1 túi\nCòn $fertilizerCount túi", color = Deep) }
            }
            1 -> DecorationPicker(tree.decoration, { onIntent(TreeIntent.Decorate(tree.id, it)) })
            else -> GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GardenPlantPortrait(tree.speciesId, tree.decoration, Modifier.fillMaxWidth().height(120.dp), tree.growth)
                    Text("Thông tin cây", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Deep)
                    Text("Loài cây: " + (com.mamxanh.app.feature.tree.domain.model.treeSpeciesCatalog.firstOrNull { it.id == tree.speciesId }?.name ?: tree.speciesId), color = Deep)
                    Text("Ngày trồng: ${tree.planted}", color = Deep)
                    Text("Độ lớn: ${tree.growth.coerceIn(0,120)} / 120", color = Deep)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

private fun previewTree(species: String = "cherry", watered: Boolean = false) = Tree(
    1L, species, "Đào hồng xuân", 120, "2026-11-01", "NONE", watered)

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun TreeInteractionScreenPreview() { MamXanhPreviewTheme { TreeInteractionScreen(previewTree(), TreeUiState(), 2, 500, {}, coins = 120) } }
@Preview(widthDp = 320, heightDp = 640, fontScale = 1.3f, device = "spec:width=320dp,height=640dp,dpi=160")
@Composable
fun TreeInteractionPinePreview() { MamXanhPreviewTheme { TreeInteractionScreen(previewTree("pine", true), TreeUiState(), 0, 0, {}) } }
@Preview(widthDp = 360, heightDp = 240, fontScale = 1.3f, device = "spec:width=360dp,height=240dp,dpi=160")
@Composable
fun ExpandedCarePanelShortPreview() { MamXanhPreviewTheme { ExpandedCarePanel(previewTree(), 0, 0, {}, Modifier.fillMaxSize()) } }
@Preview(widthDp = 360, heightDp = 650, device = "spec:width=360dp,height=650dp,dpi=160")
@Composable
fun ExpandedCarePanelExtendedPreview() { ExpandedCarePanelShortPreview() }
@Composable
fun TreePortraitPreview() { MamXanhPreviewTheme { TreePortrait(previewTree()) } }
@Composable
fun TreeCareActionsPreview() { MamXanhPreviewTheme { TreeCareActions(false, 2, 500, {}, {}) } }
@Composable
fun DecorationPickerPreview() { MamXanhPreviewTheme { DecorationPicker("FLOWERS", {}) } }
