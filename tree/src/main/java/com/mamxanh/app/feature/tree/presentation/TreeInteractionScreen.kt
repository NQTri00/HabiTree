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

@Composable
fun TreeInteractionScreen(
    tree: Tree, state: TreeUiState, fertilizerCount: Int, wateringCanMl: Int,
    onIntent: (TreeIntent) -> Unit, modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    val displayedGrowth = tree.growth.coerceIn(0, 120)
    Box(modifier.fillMaxSize()) {
        GardenForestScene(Modifier.fillMaxSize(), setting = ForestSetting.TREE)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) {
                GardenWoodSign("‹ Vườn ươm",
                    Modifier.align(Alignment.Start).width((170.dp * LocalDensity.current.fontScale).coerceAtMost(280.dp))
                        .clickable(role = Role.Button, onClickLabel = "Quay lại khu vườn", onClick = onBack),
                    fontSize = 16.sp)
            }
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green)
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp), cornerSize = 30.dp, skin = GardenPanelSkin.NAME) {
                Text(tree.name, color = Deep, fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 30.sp, textAlign = TextAlign.Center)
            }
            Box(Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
                GardenPlantPortrait(tree.speciesId, tree.decoration, sharedTreeModifier.fillMaxSize())
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
            DecorationPicker(tree.decoration, { onIntent(TreeIntent.Decorate(tree.id, it)) })
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp), cornerSize = 22.dp) {
                Text("Một chút chăm sóc, thêm một mầm vui", color = Deep, fontSize = 13.sp, lineHeight = 16.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
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
