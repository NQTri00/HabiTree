@file:JvmName("SimplePlantComponents")

package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mamxanh.core.ui.R
import kotlin.math.roundToInt

/** Static specimens inspired by mics; unidentified species remain neutral seed sacks. */
@Composable
fun PaintedGardenPlant(speciesId: String, modifier: Modifier = Modifier, potted: Boolean = false) {
    val resource = when (speciesId) {
        "cherry" -> R.drawable.simple_cherry
        "pine" -> R.drawable.simple_pine
        "succulent" -> R.drawable.simple_succulent
        "mandarin" -> R.drawable.simple_mandarin
        else -> null
    }
    if (resource == null) {
        if (speciesId == "tree") GardenArtwork(GardenArt.TREE, modifier)
        else SimpleSpeciesPlant(speciesId, modifier, potted)
    } else if (!potted || speciesId == "mandarin") {
        GardenSourceImage(resource, modifier)
    } else {
        val bitmap = ImageBitmap.imageResource(resource)
        val bounds = remember(resource, bitmap) { sourceArtworkBounds(resource,bitmap) }
        Canvas(modifier) {
            val sourceWidth=bounds[2]-bounds[0]
            val sourceHeight=((bounds[3]-bounds[1]) * when(speciesId) { "succulent" -> .62f; "pine" -> .68f; else -> .70f }).roundToInt()
            val ratio=minOf(size.width/sourceWidth,size.height*.80f/sourceHeight)
            val target=IntSize((sourceWidth*ratio).roundToInt().coerceAtLeast(1),(sourceHeight*ratio).roundToInt().coerceAtLeast(1))
            drawImage(bitmap,srcOffset=IntOffset(bounds[0],bounds[1]),srcSize=IntSize(sourceWidth,sourceHeight),
                dstOffset=IntOffset(((size.width-target.width)/2).roundToInt(),(size.height*.80f-target.height).roundToInt()),dstSize=target)
            val w=size.width;val h=size.height
            val pot=Path().apply {moveTo(w*.31f,h*.78f);lineTo(w*.69f,h*.78f);lineTo(w*.64f,h*.97f);quadraticTo(w*.50f,h*1.02f,w*.36f,h*.97f);close()}
            drawPath(pot,Brush.horizontalGradient(listOf(Color(0xFFD2AD7F),Color(0xFFFFE8BD),Color(0xFFD5AF7C))))
            drawOval(Color(0xFFEFD1A1),Offset(w*.29f,h*.74f),Size(w*.42f,h*.09f))
            drawOval(Color(0xFFAA8259),Offset(w*.33f,h*.76f),Size(w*.34f,h*.045f))
        }
    }
}

@Preview(name="Simple reference specimens",widthDp=360,heightDp=130)
@Composable
fun SimplePottedPlantsPreview() {
    Row { listOf("succulent","pine","cherry","mandarin").forEach {
        PaintedGardenPlant(it,Modifier.weight(1f).height(120.dp),potted=true)
    } }
}
