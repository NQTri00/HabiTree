package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview

enum class GardenScene {
    BEDS,
    JOURNAL,
    NURSERY,
    GROWTH_JOURNAL,
    TREE_CLEARING
}

@Composable
fun GardenSceneBackdrop(scene: GardenScene, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sky = if (scene == GardenScene.NURSERY) Color(0xFFFFEFF1) else Color(0xFFD8EEF0)
        val meadow = when (scene) {
            GardenScene.JOURNAL -> Color(0xFFECEBD9)
            GardenScene.GROWTH_JOURNAL -> Color(0xFFE1EDD8)
            GardenScene.NURSERY -> Color(0xFFF9DDE5)
            GardenScene.TREE_CLEARING -> Color(0xFFE4EED9)
            GardenScene.BEDS -> Color(0xFFE5F0DB)
        }
        drawRect(Brush.verticalGradient(listOf(sky, Color(0xFFF8F5E9), meadow), endY = h))

        val sunCenter = Offset(w * 0.82f, h * 0.12f)
        drawCircle(Color(0xFFFFE7A0).copy(alpha = .70f), w * .14f, sunCenter)
        drawCircle(Color(0xFFFFF6D8).copy(alpha = .78f), w * .09f, sunCenter)

        drawPath(
            path = Path().apply {
                moveTo(0f, h * .48f)
                cubicTo(w * .20f, h * .38f, w * .38f, h * .52f, w * .60f, h * .43f)
                cubicTo(w * .79f, h * .35f, w * .91f, h * .48f, w, h * .40f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            },
            color = Color(0xFFB8D6B7).copy(alpha = .56f)
        )
        drawPath(
            path = Path().apply {
                moveTo(0f, h * .68f)
                cubicTo(w * .18f, h * .58f, w * .38f, h * .72f, w * .63f, h * .61f)
                cubicTo(w * .81f, h * .54f, w * .93f, h * .68f, w, h * .58f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            },
            color = if (scene == GardenScene.TREE_CLEARING) Color(0xFF91B77E) else Color(0xFF9FCBA5)
        )

        when (scene) {
            GardenScene.BEDS -> {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, h * .80f)
                        cubicTo(w * .22f, h * .73f, w * .42f, h * .84f, w * .58f, h * .78f)
                        cubicTo(w * .76f, h * .72f, w * .89f, h * .84f, w, h * .76f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    },
                    color = Color(0xFF78A66F).copy(alpha = .32f)
                )
                drawBedRows(w, h)
            }
            GardenScene.JOURNAL -> {
                repeat(7) { i ->
                    val x = w * (0.08f + i * .13f)
                    val y = h * (0.79f + (i % 2) * .06f)
                    drawLeaf(x, y, w * .018f, Color(0xFF8FAE78).copy(alpha = .42f), i % 2 == 0)
                }
                drawOval(Color.White.copy(alpha = .20f), Offset(0f, h * .23f), Size(w, h * .14f))
            }
            GardenScene.NURSERY -> {
                drawRect(Color(0xFFE9A9B8).copy(alpha = .42f), Offset(0f, h * .66f), Size(w, h * .035f))
                for (i in 0..5) {
                    val x = w * (.08f + i * .17f)
                    drawLine(Color(0xFFD88F9F).copy(alpha = .35f), Offset(x, h * .66f), Offset(x, h * .88f), w * .006f)
                }
                drawOval(Color(0xFFB5D7A6).copy(alpha = .35f), Offset(w * .05f, h * .82f), Size(w * .90f, h * .20f))
            }
            GardenScene.GROWTH_JOURNAL -> {
                val center = Offset(w * .84f, h * .78f)
                listOf(.19f, .13f, .07f).forEachIndexed { index, radius ->
                    drawCircle(
                        color = Color(0xFF729C68).copy(alpha = .13f - index * .025f),
                        radius = w * radius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * .006f)
                    )
                }
                drawLeaf(w * .15f, h * .87f, w * .025f, Color(0xFF83AA70).copy(alpha = .5f), true)
            }
            GardenScene.TREE_CLEARING -> {
                drawTreeSilhouette(w * .08f, h * .54f, w * .14f, h * .29f, Color(0xFF638B62).copy(alpha = .56f))
                drawTreeSilhouette(w * .92f, h * .50f, w * .17f, h * .34f, Color(0xFF6E9867).copy(alpha = .60f))
                drawOval(Color(0xFF946B45).copy(alpha = .30f), Offset(w * .13f, h * .77f), Size(w * .74f, h * .15f))
                drawOval(Color(0xFFC59B67).copy(alpha = .34f), Offset(w * .19f, h * .79f), Size(w * .62f, h * .11f))
                listOf(.12f, .25f, .76f, .89f).forEachIndexed { index, x ->
                    drawLeaf(w * x, h * (.80f + (index % 2) * .05f), w * .018f, Color(0xFF5F9B58).copy(alpha = .68f), index % 2 == 0)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBedRows(w: Float, h: Float) {
    repeat(3) { index ->
        val y = h * (.84f + index * .045f)
        drawOval(
            color = Color(0xFFAA8259).copy(alpha = .18f - index * .035f),
            topLeft = Offset(w * (.06f + index * .035f), y),
            size = Size(w * (.88f - index * .07f), h * .045f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    x: Float,
    y: Float,
    radius: Float,
    color: Color,
    tiltRight: Boolean
) {
    val direction = if (tiltRight) 1f else -1f
    val leaf = Path().apply {
        moveTo(x, y)
        cubicTo(x + radius * direction, y - radius * 1.5f, x + radius * 2.8f * direction, y - radius * .8f, x + radius * 2.7f * direction, y + radius * .2f)
        cubicTo(x + radius * 1.6f * direction, y + radius * 1.4f, x + radius * .4f * direction, y + radius * .8f, x, y)
        close()
    }
    drawPath(leaf, color)
    drawLine(color.copy(alpha = color.alpha * .65f), Offset(x, y), Offset(x + radius * 2.3f * direction, y + radius * .05f), radius * .11f)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTreeSilhouette(
    centerX: Float,
    baseY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    drawRect(color.copy(alpha = .75f), Offset(centerX - width * .07f, baseY - height * .40f), Size(width * .14f, height * .45f))
    drawCircle(color, width * .50f, Offset(centerX, baseY - height * .66f))
    drawCircle(color, width * .37f, Offset(centerX - width * .25f, baseY - height * .50f))
    drawCircle(color, width * .36f, Offset(centerX + width * .26f, baseY - height * .50f))
}

@Preview(name = "Garden beds scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenBedsBackdropPreview() {
    GardenSceneBackdrop(GardenScene.BEDS, Modifier.fillMaxSize())
}

@Preview(name = "Field journal scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenJournalBackdropPreview() {
    GardenSceneBackdrop(GardenScene.JOURNAL, Modifier.fillMaxSize())
}

@Preview(name = "Nursery scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenNurseryBackdropPreview() {
    GardenSceneBackdrop(GardenScene.NURSERY, Modifier.fillMaxSize())
}

@Preview(name = "Growth journal scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenGrowthJournalBackdropPreview() {
    GardenSceneBackdrop(GardenScene.GROWTH_JOURNAL, Modifier.fillMaxSize())
}

@Preview(name = "Tree clearing scene", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TreeClearingBackdropPreview() {
    GardenSceneBackdrop(GardenScene.TREE_CLEARING, Modifier.fillMaxSize())
}
