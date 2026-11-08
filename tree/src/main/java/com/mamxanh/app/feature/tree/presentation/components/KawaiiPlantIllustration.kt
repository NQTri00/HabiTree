package com.mamxanh.app.feature.tree.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.mamxanh.feature.tree.R
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Painted sprites remain separate: rooted soil, seed, growing stem, bark skeleton,
 * and five independently hinged leaf/blossom clusters. All phases share one root.
 */
@Composable
fun KawaiiPlantIllustration(
    growth: Int,
    speciesId: String = "sprout",
    decoration: String = "NONE",
    modifier: Modifier = Modifier,
    growthOverride: Float? = null
) {
    val bark = ImageBitmap.imageResource(R.drawable.tree_painted_trunk)
    val soil = ImageBitmap.imageResource(R.drawable.tree_painted_soil)
    val leaves = ImageBitmap.imageResource(R.drawable.tree_painted_leaves)
    val blossoms = ImageBitmap.imageResource(R.drawable.tree_painted_blossoms)
    val seed = ImageBitmap.imageResource(R.drawable.tree_painted_seed)
    val animatedGrowth by animateFloatAsState(growth.coerceIn(0, 120).toFloat(), tween(1800), label = "painted-tree-growth")
    val progress = ((growthOverride ?: if (LocalInspectionMode.current) growth.toFloat() else animatedGrowth) / 120f).coerceIn(0f, 1f)
    val breeze = rememberInfiniteTransition(label = "painted-tree-breeze")
    val phase by breeze.animateFloat(0f, 6.283185f, infiniteRepeatable(tween(7200, easing = LinearEasing)), label = "branch-breeze")
    val still = LocalInspectionMode.current

    Canvas(modifier.aspectRatio(.95f)) {
        val w = size.width
        val h = size.height
        fun ease(start: Float, end: Float): Float {
            val t = ((progress - start) / (end - start)).coerceIn(0f, 1f)
            return t * t * (3f - 2f * t)
        }
        val root = Offset(w * .5f, h * .85f)
        val wind = if (still) 0f else sin(phase)
        sprite(soil, w * .035f, h * .77f, w * .93f, h * .21f)
        val seedVisibility = 1f - ease(.12f, .34f)
        if (seedVisibility > 0f) {
            rotate(-ease(.08f, .30f) * 28f, root) {
                sprite(seed, root.x - w * .065f, root.y - h * .08f, w * .13f, h * .09f, seedVisibility)
            }
        }

        // The sprout and the wooden rig overlap during emergence; no stage jump.
        val shoot = ease(.025f, .30f)
        val wood = ease(.19f, .48f)
        val tip = root + Offset(w * .035f * shoot + wind * w * .008f, -h * .29f * shoot)
        if (shoot > 0f && wood < 1f) {
            val stem = Path().apply {
                moveTo(root.x, root.y)
                cubicTo(root.x - w * .045f, root.y - h * .10f * shoot,
                    tip.x + w * .03f, tip.y + h * .06f * shoot, tip.x, tip.y)
            }
            drawPath(stem, Color(0xFF658B30), alpha = 1f - wood, style = Stroke(w * .014f, cap = StrokeCap.Round))
            for (side in listOf(-1f, 1f)) {
                val leaf = Path().apply {
                    moveTo(tip.x, tip.y + h * .025f)
                    cubicTo(tip.x + side * w * .14f * shoot, tip.y + h * .025f,
                        tip.x + side * w * .14f * shoot, tip.y - h * .075f * shoot, tip.x, tip.y + h * .025f)
                }
                drawPath(leaf, Color(0xFF91B53B), alpha = 1f - wood)
            }
        }

        if (wood > 0f) {
            val treeSize = .22f + .78f * ease(.18f, .93f)
            translate(root.x, root.y) {
                scale(treeSize, treeSize, Offset.Zero) {
                    rotate(wind * .65f, Offset.Zero) {
                        sprite(bark, -w * .37f, -h * .65f, w * .74f, h * .66f, wood)
                        val anchors = listOf(
                            Offset(-w * .23f, -h * .29f),
                            Offset(w * .23f, -h * .30f),
                            Offset(-w * .15f, -h * .41f),
                            Offset(w * .16f, -h * .40f),
                            Offset(0f, -h * .46f)
                        )
                        anchors.forEachIndexed { index, anchor ->
                            val unfold = ease(.24f + index * .025f, .57f + index * .025f)
                            val bloom = if (speciesId == "cherry" || decoration == "FLOWERS") ease(.30f, .58f) else 0f
                            val sway = if (still) 0f else sin(phase + index * .83f) * (1.3f + index * .12f)
                            translate(anchor.x, anchor.y) {
                                rotate(sway, Offset.Zero) {
                                    scale(unfold, unfold, Offset.Zero) {
                                        sprite(leaves, -w * .22f, -h * .235f, w * .44f, h * .255f, wood)
                                        sprite(blossoms, -w * .22f, -h * .235f, w * .44f, h * .255f, wood * bloom)
                                    }
                                }
                            }
                        }
                        if (decoration == "LANTERNS") {
                            val anchor = Offset(w * .25f, -h * .27f)
                            drawLine(Color(0xFF72502B), anchor, anchor + Offset(0f, h * .12f), w * .004f)
                            drawOval(Color(0xFFFFBD44), anchor + Offset(-w * .035f, h * .10f), Size(w * .07f, h * .075f))
                        }
                        if (decoration == "RIBBON") {
                            val y = -h * .10f
                            val bow = Path().apply {
                                moveTo(0f, y)
                                lineTo(-w * .065f, y - h * .03f); lineTo(-w * .065f, y + h * .03f)
                                lineTo(w * .065f, y - h * .03f); lineTo(w * .065f, y + h * .03f); close()
                            }
                            drawPath(bow, Color(0xFFE593A7))
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.sprite(image: ImageBitmap, x: Float, y: Float, width: Float, height: Float, opacity: Float = 1f) {
    if (width <= 0f || height <= 0f || opacity <= 0f) return
    drawImage(
        image = image,
        dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
        dstSize = IntSize(width.roundToInt().coerceAtLeast(1), height.roundToInt().coerceAtLeast(1)),
        alpha = opacity.coerceIn(0f, 1f),
        filterQuality = FilterQuality.Medium
    )
}

@Preview(name = "Painted layered rig · seed", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeSeedRigPreview() { KawaiiPlantIllustration(0, growthOverride = 0f) }

@Preview(name = "Painted layered rig · shoot", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeShootRigPreview() { KawaiiPlantIllustration(24, "cherry", growthOverride = 24f) }

@Preview(name = "Painted layered rig · young tree", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeYoungRigPreview() { KawaiiPlantIllustration(72, "cherry", growthOverride = 72f) }

@Preview(name = "Painted layered rig · flowering tree", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeMatureRigPreview() { KawaiiPlantIllustration(120, "cherry", growthOverride = 120f) }
