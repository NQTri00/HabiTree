package com.mamxanh.app.feature.shop.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.GardenScene
import com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop

/**
 * Draws the soft pink tile wall backdrop (Concept Image 2).
 */
@Composable
fun PinkTileBackdrop(modifier: Modifier = Modifier) {
    Box(modifier) {
        GardenSceneBackdrop(GardenScene.NURSERY, Modifier.fillMaxSize())
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(PinkTileBg.copy(alpha = 0.30f))

            val tileSize = w * 0.18f
            val cols = (w / tileSize).toInt() + 1
            val rows = (h / tileSize).toInt() + 1

            for (c in 0..cols) {
                val x = c * tileSize
                drawLine(PinkTileLine.copy(alpha = 0.58f), Offset(x, 0f), Offset(x, h), w * 0.003f)
            }
            for (r in 0..rows) {
                val y = r * tileSize
                drawLine(PinkTileLine.copy(alpha = 0.58f), Offset(0f, y), Offset(w, y), w * 0.003f)
            }
        }
    }
}

/**
 * Draws a single Kawaii Potted Plant with cute expressive face (Concept Image 2).
 */
@Composable
fun KawaiiPottedPlant(
    varietyIndex: Int,
    modifier: Modifier = Modifier,
    hasAlertBubble: Boolean = false,
    potExpression: Int = 0
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val groundY = h * 0.90f

        drawKawaiiFoliage(varietyIndex, cx, groundY - h * 0.28f, w, h)
        drawCuteCeramicPot(cx, groundY - h * 0.32f, w * 0.44f, h * 0.32f, varietyIndex, potExpression)

        if (hasAlertBubble) {
            drawAlertBubble(cx + w * 0.25f, h * 0.18f, w * 0.15f)
        }
    }
}

private fun DrawScope.drawAlertBubble(cx: Float, cy: Float, size: Float) {
    val bubbleColor = Color.White
    val outlineColor = Color(0xFF6B4A3A)

    drawCircle(bubbleColor, size * 0.5f, Offset(cx, cy))
    drawCircle(outlineColor, size * 0.5f, Offset(cx, cy), style = Stroke(size * 0.08f))

    val tail = Path().apply {
        moveTo(cx - size * 0.15f, cy + size * 0.40f)
        lineTo(cx, cy + size * 0.70f)
        lineTo(cx + size * 0.10f, cy + size * 0.42f)
        close()
    }
    drawPath(tail, bubbleColor)
    drawPath(tail, outlineColor, style = Stroke(size * 0.08f))

    val exColor = Color(0xFFFF5252)
    drawLine(exColor, Offset(cx, cy - size * 0.25f), Offset(cx, cy + size * 0.05f), size * 0.14f, cap = StrokeCap.Round)
    drawCircle(exColor, size * 0.08f, Offset(cx, cy + size * 0.22f))
}

/**
 * Draws the wooden display shelves on the left with tiered plants (Concept Image 2).
 */
@Composable
fun WoodenPlantShelf(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val postW = w * 0.07f
        drawRoundRect(ShelfWood, Offset(0f, 0f), Size(postW, h), CornerRadius(postW * 0.3f))
        drawRoundRect(ShelfWoodDark, Offset(0f, 0f), Size(postW, h), CornerRadius(postW * 0.3f), style = Stroke(w * 0.012f))

        drawRoundRect(ShelfWood, Offset(w - postW, 0f), Size(postW, h), CornerRadius(postW * 0.3f))
        drawRoundRect(ShelfWoodDark, Offset(w - postW, 0f), Size(postW, h), CornerRadius(postW * 0.3f), style = Stroke(w * 0.012f))

        val shelfH = h * 0.045f
        val shelfTiers = listOf(0.38f, 0.72f)

        shelfTiers.forEach { tierY ->
            val y = h * tierY
            drawRoundRect(
                color = ShelfWood,
                topLeft = Offset(0f, y),
                size = Size(w, shelfH),
                cornerRadius = CornerRadius(w * 0.02f)
            )
            drawRoundRect(
                color = ShelfWoodDark,
                topLeft = Offset(0f, y),
                size = Size(w, shelfH),
                cornerRadius = CornerRadius(w * 0.02f),
                style = Stroke(w * 0.012f)
            )
            drawCircle(Color(0xFF754522), w * 0.018f, Offset(postW * 0.5f, y + shelfH * 0.5f))
            drawCircle(Color(0xFF754522), w * 0.018f, Offset(w - postW * 0.5f, y + shelfH * 0.5f))
        }
    }
}

/**
 * Draws the cute striped shop awning with wooden sign "My Tiny Garden" (Concept Image 2).
 */
@Composable
fun ShopAwning(
    title: String = "My Tiny Garden",
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val numStripes = 7
        val stripeW = w / numStripes
        val awningH = h * 0.65f
        val awningY = h * 0.35f

        for (i in 0 until numStripes) {
            val color = if (i % 2 == 0) AwningRed else AwningCream
            val sx = i * stripeW
            val stripePath = Path().apply {
                moveTo(sx, awningY)
                lineTo(sx + stripeW, awningY)
                lineTo(sx + stripeW, awningY + awningH * 0.80f)
                cubicTo(
                    sx + stripeW * 0.75f, awningY + awningH,
                    sx + stripeW * 0.25f, awningY + awningH,
                    sx, awningY + awningH * 0.80f
                )
                close()
            }
            drawPath(stripePath, color)
            drawPath(stripePath, Color(0xFF8A4B40), style = Stroke(w * 0.008f))
        }

        val vineColor = Color(0xFF5A9E52)
        val leaves = listOf(
            Offset(w * 0.05f, awningY + awningH * 0.90f),
            Offset(w * 0.08f, awningY + awningH * 0.75f),
            Offset(w * 0.92f, awningY + awningH * 0.90f),
            Offset(w * 0.95f, awningY + awningH * 0.75f)
        )
        leaves.forEach { pt ->
            drawCircle(vineColor, w * 0.025f, pt)
        }
    }
}

@Preview(name = "Shop tile wall", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PinkTileBackdropPreview() {
    PinkTileBackdrop(Modifier)
}

@Preview(name = "Kawaii nursery plant", showBackground = true)
@Composable
fun KawaiiPottedPlantPreview() {
    MamXanhPreviewTheme {
        KawaiiPottedPlant(8, Modifier.size(140.dp, 170.dp), hasAlertBubble = true, potExpression = 1)
    }
}

@Preview(name = "Wooden nursery shelf", showBackground = true)
@Composable
fun WoodenPlantShelfPreview() {
    MamXanhPreviewTheme { WoodenPlantShelf(Modifier.size(140.dp, 280.dp)) }
}

@Preview(name = "Nursery awning", showBackground = true)
@Composable
fun ShopAwningPreview() {
    MamXanhPreviewTheme { ShopAwning(modifier = Modifier.size(240.dp, 96.dp)) }
}
