package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Temporary static species image. Stage artwork/animation is deferred until models are ready. */
@Composable
fun GardenPlantPortrait(
    speciesId: String, decoration: String, modifier: Modifier = Modifier, growth: Int = 120,
    fitVisibleArtwork: Boolean = false,
    artworkAlignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.BottomCenter,
    withered: Boolean = false
) {
    BoxWithConstraints(modifier.then(if(withered) Modifier.graphicsLayer(alpha=.45f,rotationZ=-6f) else Modifier)) {
        val bounds = if (fitVisibleArtwork && speciesId == "cherry") {
            androidx.compose.ui.platform.LocalContext.current.resources.getIntArray(com.mamxanh.core.ui.R.array.cherry_growth_bounds)
        } else null
        val artworkHeight = if (bounds != null) minOf(maxHeight, maxWidth * bounds[3].toFloat() / bounds[2]) else maxHeight
        val artworkWidth = if (bounds != null) minOf(maxWidth, maxHeight * bounds[2].toFloat() / bounds[3]) else maxWidth
        BoxWithConstraints(Modifier.align(artworkAlignment).size(artworkWidth, artworkHeight)) {
        if (speciesId == "cherry") {
            CherryGrowthArtwork(120, Modifier.fillMaxSize(), fitVisibleArtwork)
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
}

@Preview(name = "Cherry specimen", widthDp = 320, heightDp = 300,
    device = "spec:width=320dp,height=300dp,dpi=160")
@Composable
fun GardenPlantPortraitPreview() { GardenPlantPortrait("cherry", "LANTERNS", Modifier.size(320.dp, 300.dp)) }

@Preview(name = "Newly planted specimen", widthDp = 170, heightDp = 150,
    device = "spec:width=170dp,height=150dp,dpi=160")
@Composable
fun GardenNewPlantPortraitPreview() { GardenPlantPortrait("cherry", "NONE", Modifier.fillMaxSize(), growth = 0) }

@Preview(name = "Supplied cherry growth · fixed root", widthDp = 360, heightDp = 130,
    device = "spec:width=360dp,height=130dp,dpi=160")
@Composable
fun CherryGrowthStagesPreview() {
    Row(Modifier.fillMaxSize()) {
        listOf(0, 24, 66, 120).forEach { growth ->
            Column(Modifier.weight(1f), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                GardenPlantPortrait("cherry", "NONE", Modifier.fillMaxWidth().height(104.dp), growth)
                GardenRichText("$growth / 120")
            }
        }
    }
}
