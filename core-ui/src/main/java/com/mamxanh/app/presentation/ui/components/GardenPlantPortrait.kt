package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Static specimen presentation. Growth remains live data; animation is deferred. */
@Composable
fun GardenPlantPortrait(speciesId: String, decoration: String, modifier: Modifier = Modifier, growth: Int = 120) {
    BoxWithConstraints(modifier) {
        if (growth <= 0) {
            GardenArtwork(GardenArt.SEED, Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth(.65f).height(maxHeight * .45f))
        } else {
            PaintedGardenPlant(speciesId, Modifier.fillMaxSize())
        }
        when (decoration) {
            "LANTERNS" -> GardenArtwork(GardenArt.LANTERN,
                Modifier.offset(maxWidth * .72f, maxHeight * .36f).width(maxWidth * .15f).height(maxHeight * .24f))
            "RIBBON" -> GardenArtwork(GardenArt.RIBBON,
                Modifier.offset(maxWidth * .42f, maxHeight * .62f).width(maxWidth * .20f).height(maxHeight * .14f))
            "FLOWERS" -> GardenArtwork(GardenArt.FLOWERS,
                Modifier.offset(maxWidth * .12f, maxHeight * .55f).width(maxWidth * .30f).height(maxHeight * .20f))
        }
    }
}

@Preview(name = "Static cherry specimen", widthDp = 320, heightDp = 300)
@Composable
fun GardenPlantPortraitPreview() { GardenPlantPortrait("cherry", "LANTERNS", Modifier.size(320.dp, 300.dp)) }

@Preview(name = "Newly planted specimen", widthDp = 170, heightDp = 150,
    device = "spec:width=170dp,height=150dp,dpi=160")
@Composable
fun GardenNewPlantPortraitPreview() { GardenPlantPortrait("cherry", "NONE", Modifier.fillMaxSize(), growth = 0) }
