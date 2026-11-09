package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
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
    onIntent: (TreeIntent) -> Unit, modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier
) {
    val displayedGrowth = tree.growth.coerceIn(0, 120)
    val sceneScroll = rememberScrollState()
    LaunchedEffect(state.isPanelExpanded) {
        if (state.isPanelExpanded) sceneScroll.scrollTo(0)
    }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val sceneHeight = maxOf(maxHeight, 560.dp * LocalDensity.current.fontScale.coerceAtLeast(1f))
        GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.TREE)
        Box(Modifier.fillMaxSize().verticalScroll(sceneScroll)) {
        Column(
            Modifier.fillMaxWidth().height(sceneHeight).padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GardenWoodSign("‹ Vườn ươm",
                Modifier.align(Alignment.Start).width((170.dp * LocalDensity.current.fontScale).coerceAtMost(280.dp))
                    .clickable(role = Role.Button, onClickLabel = "Quay lại khu vườn",
                        onClick = { onIntent(TreeIntent.Back) }),
                fontSize = 16.sp)
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GardenLeafPlaque(tree.name, Modifier.fillMaxWidth().padding(horizontal = 24.dp))
                GardenArtwork(GardenArt.FLOWERS, Modifier.align(Alignment.CenterStart).size(40.dp, 48.dp))
                GardenArtwork(GardenArt.SPROUT, Modifier.align(Alignment.CenterEnd).size(30.dp))
            }
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                GardenPlantPortrait(tree.speciesId, tree.decoration, sharedTreeModifier.fillMaxSize())
                GardenArtwork(GardenArt.LANTERN, Modifier.align(Alignment.TopEnd).padding(end = 3.dp).size(38.dp, 62.dp))
            }
            GardenBotanicalFrame(Modifier.fillMaxWidth(.9f), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp), cornerSize = 24.dp, skin = GardenPanelSkin.PROGRESS) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(progress = { (displayedGrowth / 120f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp), color = Green, trackColor = Color(0xFFD1B470), drawStopIndicator = {})
                    Text("${displayedGrowth} / 120", color = Deep, fontSize = 20.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            TreeCareActions(tree.wateredToday, fertilizerCount, wateringCanMl,
                { onIntent(TreeIntent.Water(tree.id)) }, { onIntent(TreeIntent.Fertilize(tree.id)) })
            GardenPaintedButton("Trang trí & thông tin ↑",
                { onIntent(TreeIntent.SetPanelExpanded(true)) }, Modifier.fillMaxWidth(), primary = false)
        }
        }
        if (state.isPanelExpanded) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(.75f),
                onDismissRequest = { onIntent(TreeIntent.SetPanelExpanded(false)) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFFFFEBC9),
                contentColor = Deep
            ) {
                Column(
                    Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🌱 Chăm sóc cây", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TreeCareActions(tree.wateredToday, fertilizerCount, wateringCanMl,
                        { onIntent(TreeIntent.Water(tree.id)) }, { onIntent(TreeIntent.Fertilize(tree.id)) })
                    Column(
                        Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Trang trí", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        DecorationPicker(tree.decoration, { onIntent(TreeIntent.Decorate(tree.id, it)) })
                        GardenBotanicalFrame(Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp), cornerSize = 20.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Thông tin cây", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Loài cây: " + (com.mamxanh.app.feature.tree.domain.model.treeSpeciesCatalog
                                    .firstOrNull { it.id == tree.speciesId }?.name ?: tree.speciesId), color = Deep)
                                Text("Ngày trồng: ${tree.planted}", color = Deep)
                                Text("Độ lớn: $displayedGrowth / 120", color = Deep)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

private fun previewTree(species: String = "cherry", watered: Boolean = false) = Tree(
    1L, species, if (species == "pine") "Thông xanh biếc" else "Đào hồng xuân",
    68, "2026-11-01", "FLOWERS", watered
)

@Preview(name = "Tree care · cherry", widthDp = 360, heightDp = 800)
@Preview(name = "Tree care · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun TreeInteractionScreenPreview() { MamXanhPreviewTheme { TreeInteractionScreen(previewTree(), TreeUiState(isLoading = false), 2, 500, {}) } }

@Preview(name = "Tree care · pine watered", widthDp = 360, heightDp = 800)
@Composable
fun TreeInteractionPinePreview() { MamXanhPreviewTheme { TreeInteractionScreen(previewTree("pine", true), TreeUiState(isLoading = false), 0, 240, {}) } }

@Preview(name = "Tree portrait", widthDp = 360, heightDp = 360)
@Composable
fun TreePortraitPreview() { MamXanhPreviewTheme { TreePortrait(previewTree()) } }

@Preview(name = "Tree care actions", widthDp = 340)
@Composable
fun TreeCareActionsPreview() { MamXanhPreviewTheme { TreeCareActions(false, 2, 500, {}, {}) } }

@Preview(name = "Tree decoration picker", widthDp = 340)
@Composable
fun DecorationPickerPreview() { MamXanhPreviewTheme { DecorationPicker("FLOWERS", {}) } }
