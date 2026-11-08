package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.feature.tree.presentation.components.*
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame
import com.mamxanh.app.presentation.ui.components.GardenScene
import com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop

@Composable
fun TreeInteractionScreen(
    tree: Tree,
    state: TreeUiState,
    fertilizerCount: Int,
    wateringCanMl: Int,
    onIntent: (TreeIntent) -> Unit,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.TREE_CLEARING, Modifier.fillMaxSize())
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green)

            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            if (tree.growth >= 120) "Cây trưởng thành" else "Hành trình lớn lên",
                            color = Deep,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "${tree.growth.coerceAtMost(120)} / 120",
                            color = Green,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Green,
                        trackColor = Color(0xFFDDEAD5)
                    )
                }
            }

            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                TreePortrait(
                    tree = tree,
                    modifier = Modifier.fillMaxWidth(),
                    sharedTreeModifier = sharedTreeModifier
                )
            }

            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                TreeCareActions(
                    tree.wateredToday,
                    fertilizerCount,
                    wateringCanMl,
                    { onIntent(TreeIntent.Water(tree.id)) },
                    { onIntent(TreeIntent.Fertilize(tree.id)) }
                )
            }

            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                DecorationPicker(tree.decoration, { onIntent(TreeIntent.Decorate(tree.id, it)) })
            }

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.16f))
            ) {
                Text(
                    "Cảm ơn bạn đã dành chút thời gian chăm sóc cây hôm nay. 🍃",
                    color = Green,
                    modifier = Modifier.fillMaxWidth().padding(14.dp)
                )
            }
        }
    }
}

@Preview(name = "Tree clearing · cherry growing", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TreeInteractionScreenPreview() {
    MamXanhPreviewTheme {
        TreeInteractionScreen(
            tree = Tree(
                id = 1L,
                speciesId = "cherry",
                name = "Đào hồng xuân",
                growth = 68,
                planted = "2026-11-01",
                decoration = "FLOWERS",
                wateredToday = false
            ),
            state = TreeUiState(),
            fertilizerCount = 2,
            wateringCanMl = 500,
            onIntent = {}
        )
    }
}

@Preview(name = "Tree clearing · pine watered", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TreeInteractionPinePreview() {
    MamXanhPreviewTheme {
        TreeInteractionScreen(
            tree = Tree(
                id = 2L,
                speciesId = "pine",
                name = "Thông xanh biếc",
                growth = 112,
                planted = "2026-09-08",
                decoration = "NONE",
                wateredToday = true
            ),
            state = TreeUiState(),
            fertilizerCount = 0,
            wateringCanMl = 240,
            onIntent = {}
        )
    }
}

@Preview(name = "Tree portrait", showBackground = true, widthDp = 360, heightDp = 360)
@Composable
fun TreePortraitPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize()) {
            GardenSceneBackdrop(GardenScene.TREE_CLEARING, Modifier.fillMaxSize())
            TreePortrait(
                Tree(
                    id = 1L,
                    speciesId = "cherry",
                    name = "Đào hồng xuân",
                    growth = 68,
                    planted = "2026-11-01",
                    decoration = "FLOWERS",
                    wateredToday = false
                )
            )
        }
    }
}

@Preview(name = "Tree care actions", showBackground = true)
@Composable
fun TreeCareActionsPreview() {
    MamXanhPreviewTheme {
        GardenBotanicalFrame {
            TreeCareActions(false, 2, 500, {}, {})
        }
    }
}

@Preview(name = "Tree decoration picker", showBackground = true, widthDp = 360)
@Composable
fun DecorationPickerPreview() {
    MamXanhPreviewTheme {
        GardenBotanicalFrame {
            DecorationPicker("FLOWERS", {})
        }
    }
}
