package com.mamxanh.app.presentation.ui.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Softens only the 16dp viewport edges that hide more content. Put before the scroll modifier.
 * The source is an unfiltered display list; only narrow edge strips get blur buffers.
 * Older Android versions and IDE previews use the lightweight fade without RenderEffect.
 */
@Composable
fun Modifier.mintScrollEdges(canScrollBackward: Boolean, canScrollForward: Boolean): Modifier {
    if (!canScrollBackward && !canScrollForward) return this
    val blurSupported = Build.VERSION.SDK_INT >= 31 && !LocalInspectionMode.current
    val source = if (blurSupported) rememberGraphicsLayer() else null
    val top = if (blurSupported && canScrollBackward) rememberGraphicsLayer() else null
    val bottom = if (blurSupported && canScrollForward) rememberGraphicsLayer() else null
    return drawWithCache {
        val edge = minOf(16.dp.toPx(), size.height / 8f)
        val radius = 2.dp.toPx()
        val bandHeight = ceil(edge + radius * 3f).toInt().coerceAtMost(size.height.roundToInt())
        val bandSize = IntSize(size.width.roundToInt(), bandHeight)
        val bottomCaptureY = size.height - bandHeight
        val effect = BlurEffect(radius, radius, TileMode.Clamp)
        top?.renderEffect = effect
        bottom?.renderEffect = effect
        val maskTop = Brush.verticalGradient(listOf(Color.White.copy(alpha = .9f), Color.Transparent),
            startY = 0f, endY = edge)
        val maskBottom = Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = .9f)),
            startY = size.height - edge, endY = size.height)
        val fog = Color(0xFFE5F2E9).copy(alpha = if (blurSupported) .16f else .35f)
        val fadeTop = Brush.verticalGradient(listOf(fog, Color.Transparent), startY = 0f, endY = edge)
        val fadeBottom = Brush.verticalGradient(listOf(Color.Transparent, fog),
            startY = size.height - edge, endY = size.height)
        val edgePaint = Paint()
        onDrawWithContent {
            if (size.width <= 0f || size.height <= 0f) {
                drawContent()
            } else {
                if (source != null) {
                    source.record(this, layoutDirection,
                        IntSize(size.width.roundToInt(), size.height.roundToInt())) {
                        this@onDrawWithContent.drawContent()
                    }
                    drawLayer(source)
                    top?.record(this, layoutDirection, bandSize) {
                        clipRect { drawLayer(source) }
                    }
                    bottom?.record(this, layoutDirection, bandSize) {
                        clipRect { translate(top = -bottomCaptureY) { drawLayer(source) } }
                    }
                } else drawContent()
                if (canScrollBackward) drawSoftEdge(top, 0f, 0f, edge, maskTop, fadeTop, edgePaint)
                if (canScrollForward) drawSoftEdge(bottom, size.height - edge,
                    bottomCaptureY, edge, maskBottom, fadeBottom, edgePaint)
            }
        }
    }
}

private fun DrawScope.drawSoftEdge(
    layer: GraphicsLayer?, edgeY: Float, captureY: Float, edgeHeight: Float,
    mask: Brush, fade: Brush, paint: Paint
) {
    if (layer != null) {
        val canvas = drawContext.canvas
        canvas.saveLayer(Rect(0f, edgeY, size.width, edgeY + edgeHeight), paint)
        try {
            clipRect(0f, edgeY, size.width, edgeY + edgeHeight) {
                translate(top = captureY) { drawLayer(layer) }
                drawRect(mask, topLeft = Offset(0f, edgeY),
                    size = Size(size.width, edgeHeight), blendMode = BlendMode.DstIn)
            }
        } finally {
            canvas.restore()
        }
    }
    drawRect(fade, topLeft = Offset(0f, edgeY), size = Size(size.width, edgeHeight))
}
