package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.core.ui.R

enum class ForestSetting { HOME, GARDEN, TREE }

/** Source-derived scenery only. All specimens, signs and controls are separate live layers. */
@Composable
fun GardenForestScene(modifier: Modifier = Modifier, showStream: Boolean = true, setting: ForestSetting = if (showStream) ForestSetting.HOME else ForestSetting.TREE) {
    val resource = when (setting) {
        ForestSetting.HOME -> R.drawable.simple_home
        ForestSetting.GARDEN -> R.drawable.simple_forest
        ForestSetting.TREE -> R.drawable.simple_forest
    }
    Image(painterResource(resource), null, modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
}

@Preview(name = "Reference forest environment", widthDp = 360, heightDp = 800)
@Composable
fun GardenForestScenePreview() { GardenForestScene(Modifier.fillMaxSize()) }
