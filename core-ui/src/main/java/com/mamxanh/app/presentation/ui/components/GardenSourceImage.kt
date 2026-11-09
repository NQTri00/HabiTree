package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

private val artworkBounds = ConcurrentHashMap<Int, IntArray>()

/** Ignore transparent export margins and isolated antialiasing pixels. */
internal fun sourceArtworkBounds(resource: Int, bitmap: ImageBitmap): IntArray =
    artworkBounds.getOrPut(resource) {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(pixels)
        val rows = IntArray(bitmap.height)
        val columns = IntArray(bitmap.width)
        for (y in rows.indices) for (x in columns.indices) {
            if ((pixels[y * bitmap.width + x] ushr 24) > 96) {
                rows[y]++
                columns[x]++
            }
        }
        val left = columns.indexOfFirst { it > bitmap.height * .02f }.coerceAtLeast(0)
        val right = columns.indexOfLast { it > bitmap.height * .02f }.let { if (it < left) bitmap.width else it + 1 }
        val top = rows.indexOfFirst { it > bitmap.width * .02f }.coerceAtLeast(0)
        val bottom = rows.indexOfLast { it > bitmap.width * .02f }.let { if (it < top) bitmap.height else it + 1 }
        intArrayOf(left, top, right, bottom)
    }

internal data class SourceArtwork(val bitmap: ImageBitmap, val bounds: IntArray)

/** Decode and inspect sprites on a worker; composition must never read their pixels. */
@Composable
internal fun rememberSourceArtwork(resource: Int): SourceArtwork? {
    val resources = LocalContext.current.resources
    val configuration = LocalConfiguration.current
    if (LocalInspectionMode.current) {
        return remember(resource, resources, configuration) {
            val bitmap = ImageBitmap.imageResource(resources, resource)
            SourceArtwork(bitmap, sourceArtworkBounds(resource, bitmap))
        }
    }
    return produceState<SourceArtwork?>(null, resource, resources, configuration) {
        value = null
        value = withContext(Dispatchers.IO) {
            val bitmap = ImageBitmap.imageResource(resources, resource)
            SourceArtwork(bitmap, sourceArtworkBounds(resource, bitmap))
        }
    }.value
}

/** A decorative source sprite fitted by its visible artwork rather than export canvas. */
@Composable
fun GardenSourceImage(resource: Int, modifier: Modifier = Modifier) {
    val artwork = rememberSourceArtwork(resource)
    Canvas(modifier) {
        val bitmap = artwork?.bitmap ?: return@Canvas
        val bounds = artwork.bounds
        val width = bounds[2] - bounds[0]
        val height = bounds[3] - bounds[1]
        val scale = minOf(size.width / width, size.height / height)
        val target = IntSize((width * scale).roundToInt().coerceAtLeast(1), (height * scale).roundToInt().coerceAtLeast(1))
        drawImage(bitmap, srcOffset = IntOffset(bounds[0], bounds[1]), srcSize = IntSize(width, height),
            dstOffset = IntOffset(((size.width - target.width) / 2).roundToInt(), ((size.height - target.height) / 2).roundToInt()),
            dstSize = target)
    }
}
