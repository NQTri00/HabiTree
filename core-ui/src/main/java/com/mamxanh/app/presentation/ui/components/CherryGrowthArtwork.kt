package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.mamxanh.core.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private val cherryGrowthFrames = intArrayOf(
    R.drawable.cherry_growth_00, R.drawable.cherry_growth_01,
    R.drawable.cherry_growth_02, R.drawable.cherry_growth_03,
    R.drawable.cherry_growth_04, R.drawable.cherry_growth_05,
    R.drawable.cherry_growth_06, R.drawable.cherry_growth_07,
    R.drawable.cherry_growth_08, R.drawable.cherry_growth_09,
    R.drawable.cherry_growth_10, R.drawable.cherry_growth_11
)

/** A persisted growth value selects a supplied frame; entering a screen never replays growth. */
@Composable
internal fun CherryGrowthArtwork(growth: Int, modifier: Modifier = Modifier) {
    val resource = cherryGrowthFrames[growth.coerceIn(0, 120) * cherryGrowthFrames.lastIndex / 120]
    val resources = LocalContext.current.resources
    val configuration = LocalConfiguration.current
    val bitmap = if (LocalInspectionMode.current) {
        remember(resource, resources, configuration) { ImageBitmap.imageResource(resources, resource) }
    } else {
        // Keep the previous frame until its replacement is ready, avoiding a blank specimen
        // during a real growth update. Only this displayed frame is retained by composition.
        produceState<ImageBitmap?>(null, resource, resources, configuration) {
            value = withContext(Dispatchers.IO) { ImageBitmap.imageResource(resources, resource) }
        }.value
    }
    Canvas(modifier) {
        val frame = bitmap ?: return@Canvas
        val scale = minOf(size.width / frame.width, size.height / frame.height)
        val target = IntSize(
            (frame.width * scale).roundToInt().coerceAtLeast(1),
            (frame.height * scale).roundToInt().coerceAtLeast(1)
        )
        // Full, equal-sized source canvases are essential: per-frame alpha cropping
        // would enlarge a seed and move the soil as branches appear.
        drawImage(frame,
            dstOffset = IntOffset(((size.width - target.width) / 2).roundToInt(),
                (size.height - target.height).roundToInt()),
            dstSize = target)
    }
}
