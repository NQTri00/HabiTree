package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

enum class GardenScene { BEDS, JOURNAL, NURSERY, GROWTH_JOURNAL, TREE_CLEARING }

@Composable
fun GardenSceneBackdrop(scene: GardenScene, modifier: Modifier = Modifier) {
    GardenForestScene(modifier, setting = when (scene) {
        GardenScene.BEDS -> ForestSetting.GARDEN
        GardenScene.TREE_CLEARING -> ForestSetting.TREE
        else -> ForestSetting.HOME
    })
}

@Preview(name = "Garden beds scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenBedsBackdropPreview() { GardenSceneBackdrop(GardenScene.BEDS, Modifier.fillMaxSize()) }

@Preview(name = "Field journal scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenJournalBackdropPreview() { GardenSceneBackdrop(GardenScene.JOURNAL, Modifier.fillMaxSize()) }

@Preview(name = "Nursery scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenNurseryBackdropPreview() { GardenSceneBackdrop(GardenScene.NURSERY, Modifier.fillMaxSize()) }

@Preview(name = "Growth journal scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenGrowthJournalBackdropPreview() { GardenSceneBackdrop(GardenScene.GROWTH_JOURNAL, Modifier.fillMaxSize()) }

@Preview(name = "Tree clearing scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TreeClearingBackdropPreview() { GardenSceneBackdrop(GardenScene.TREE_CLEARING, Modifier.fillMaxSize()) }
