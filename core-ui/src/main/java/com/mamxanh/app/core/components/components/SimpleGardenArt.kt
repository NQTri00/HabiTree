package com.mamxanh.app.core.components.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity

private val LeafGreen = AppColors.LeafGreen
private val DarkLeaf = AppColors.DarkLeaf
private val Honey = AppColors.Honey
private val InkBrown = AppColors.InkBrown
private val Paper = AppColors.Paper

private fun DrawScope.leaf(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Color = LeafGreen,
    angle: Float = 0f,
) {
    rotate(angle, Offset(x, y)) {
        val p =
            Path().apply {
                moveTo(x, y)
                cubicTo(x - width * .6f, y - height * .25f, x - width * .75f, y - height, x, y - height)
                cubicTo(x + width * .75f, y - height, x + width * .6f, y - height * .25f, x, y)
            }
        drawPath(p, Brush.verticalGradient(listOf(AppColors.SwatchFFBED45B, color), y - height, y))
    }
}

@Composable
fun SimpleGardenArt(
    art: GardenArt,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val factor = minOf(size.width, size.height) / 100f
        translate((size.width - 100 * factor) / 2, (size.height - 100 * factor) / 2) {
            scale(factor, factor, Offset.Zero) {
                when (art) {
                    GardenArt.CHECKED_TASK -> {
                        drawRoundRect(Paper, Offset(14f, 25f), Size(66f, 62f), CornerRadius(6f))
                        drawRoundRect(DarkLeaf, Offset(14f, 25f), Size(66f, 62f), CornerRadius(6f), style = Stroke(4f))
                        drawLine(DarkLeaf, Offset(24f, 51f), Offset(43f, 69f), 7f, StrokeCap.Round)
                        drawLine(DarkLeaf, Offset(43f, 69f), Offset(88f, 15f), 7f, StrokeCap.Round)
                    }
                    GardenArt.SPROUT -> {
                        drawLine(DarkLeaf, Offset(50f, 88f), Offset(49f, 47f), 5f, StrokeCap.Round)
                        leaf(48f, 57f, 39f, 45f, angle = -42f)
                        leaf(51f, 55f, 42f, 52f, angle = 43f)
                    }
                    GardenArt.COIN -> {
                        drawCircle(AppColors.SwatchFFD99928, 40f, Offset(50f, 53f))
                        drawCircle(
                            Brush.radialGradient(listOf(AppColors.SwatchFFFFEC8E, AppColors.SwatchFFF7BE35), Offset(41f, 40f), 54f),
                            36f,
                            Offset(50f, 48f),
                        )
                        drawCircle(AppColors.SwatchFFFFF0A0, 29f, Offset(50f, 48f), style = Stroke(2f))
                        drawOval(AppColors.SwatchFFFFE286, Offset(34f, 22f), Size(15f, 25f))
                    }
                    GardenArt.DROP -> {
                        val drop =
                            Path().apply {
                                moveTo(50f, 7f)
                                cubicTo(43f, 28f, 20f, 47f, 20f, 65f)
                                cubicTo(20f, 102f, 80f, 102f, 80f, 65f)
                                cubicTo(80f, 47f, 57f, 28f, 50f, 7f)
                                close()
                            }
                        drawPath(
                            drop,
                            Brush.horizontalGradient(listOf(AppColors.SwatchFF55B8D3, AppColors.SwatchFF9AE5EC, AppColors.SwatchFF40A2CA)),
                        )
                        drawPath(drop, AppColors.SwatchFF398CA9, style = Stroke(2f))
                        drawOval(AppColors.White.copy(alpha = AppOpacity.VALUE_65), Offset(29f, 48f), Size(12f, 25f))
                    }
                    GardenArt.NUT -> {
                        rotate(32f, Offset(43f, 53f)) {
                            drawOval(
                                Brush.horizontalGradient(listOf(AppColors.SwatchFFAF642B, AppColors.SwatchFFE6A44D)),
                                Offset(13f, 25f),
                                Size(47f, 58f),
                            )
                        }
                        rotate(42f, Offset(67f, 62f)) {
                            drawOval(
                                Brush.horizontalGradient(listOf(AppColors.SwatchFFEAA74F, AppColors.SwatchFFFFD07A)),
                                Offset(47f, 42f),
                                Size(33f, 44f),
                            )
                        }
                    }
                    GardenArt.GLASS -> {
                        val glass =
                            Path().apply {
                                moveTo(22f, 14f)
                                lineTo(78f, 14f)
                                lineTo(69f, 87f)
                                quadraticTo(50f, 96f, 31f, 87f)
                                close()
                            }
                        drawPath(glass, AppColors.SwatchFFD9F0E9)
                        val water =
                            Path().apply {
                                moveTo(28f, 45f)
                                quadraticTo(50f, 52f, 72f, 45f)
                                lineTo(67f, 83f)
                                quadraticTo(50f, 90f, 33f, 83f)
                                close()
                            }
                        drawPath(water, Brush.verticalGradient(listOf(AppColors.SwatchFF82CED4, AppColors.SwatchFF409BA9), 40f, 90f))
                        drawPath(glass, AppColors.SwatchFF5796A0, style = Stroke(2.5f))
                        drawOval(AppColors.SwatchFFEEF9EF, Offset(22f, 7f), Size(56f, 15f))
                        drawOval(AppColors.SwatchFF5796A0, Offset(22f, 7f), Size(56f, 15f), style = Stroke(2f))
                        drawLine(AppColors.White.copy(alpha = AppOpacity.VALUE_6), Offset(35f, 34f), Offset(39f, 78f), 4f, StrokeCap.Round)
                    }
                    GardenArt.BOOK, GardenArt.JOURNAL, GardenArt.CATEGORY -> {
                        if (art == GardenArt.CATEGORY) {
                            repeat(3) { i ->
                                drawRoundRect(
                                    if (i % 2 == 0)DarkLeaf else Honey,
                                    Offset(12f, 30f + i * 19),
                                    Size(76f, 18f),
                                    CornerRadius(4f),
                                )
                                drawRoundRect(Paper, Offset(21f, 32f + i * 19), Size(60f, 11f), CornerRadius(2f))
                            }
                        } else {
                            val book =
                                Path().apply {
                                    moveTo(10f, 22f)
                                    quadraticTo(30f, 10f, 50f, 23f)
                                    quadraticTo(70f, 10f, 90f, 22f)
                                    lineTo(85f, 84f)
                                    quadraticTo(65f, 75f, 50f, 87f)
                                    quadraticTo(30f, 75f, 15f, 84f)
                                    close()
                                }
                            drawPath(book, AppColors.SwatchFFC67E4F, style = Stroke(7f))
                            drawPath(book, Paper)
                            drawLine(Honey, Offset(50f, 25f), Offset(50f, 82f), 2f)
                            repeat(4) { i ->
                                drawLine(AppColors.SwatchFFCEAE7B, Offset(21f, 32f + i * 10), Offset(41f, 35f + i * 10), 1.5f)
                                drawLine(AppColors.SwatchFFCEAE7B, Offset(59f, 35f + i * 10), Offset(79f, 32f + i * 10), 1.5f)
                            }
                        }
                    }
                    GardenArt.CHECKLIST -> {
                        rotate(8f, Offset(50f, 50f)) {
                            drawRoundRect(AppColors.SwatchFFC89D5A, Offset(21f, 12f), Size(62f, 80f), CornerRadius(8f))
                            drawRoundRect(Paper, Offset(17f, 7f), Size(62f, 80f), CornerRadius(8f))
                            repeat(3) { i ->
                                val y = 27f + i * 20
                                drawLine(DarkLeaf, Offset(27f, y), Offset(31f, y + 4), 3f, StrokeCap.Round)
                                drawLine(DarkLeaf, Offset(31f, y + 4), Offset(38f, y - 5), 3f, StrokeCap.Round)
                                drawLine(InkBrown, Offset(46f, y), Offset(67f, y), 2f, StrokeCap.Round)
                            }
                        }
                    }
                    GardenArt.CALENDAR -> {
                        drawRoundRect(Paper, Offset(13f, 22f), Size(74f, 67f), CornerRadius(8f))
                        drawRoundRect(DarkLeaf, Offset(13f, 22f), Size(74f, 67f), CornerRadius(8f), style = Stroke(4f))
                        drawLine(DarkLeaf, Offset(15f, 40f), Offset(85f, 40f), 4f)
                        drawLine(DarkLeaf, Offset(30f, 13f), Offset(30f, 30f), 6f, StrokeCap.Round)
                        drawLine(DarkLeaf, Offset(70f, 13f), Offset(70f, 30f), 6f, StrokeCap.Round)
                        repeat(
                            2,
                        ) { r ->
                            repeat(
                                3,
                            ) { c -> drawRoundRect(LeafGreen, Offset(27f + c * 19, 51f + r * 18), Size(8f, 8f), CornerRadius(1f)) }
                        }
                    }
                    GardenArt.PENCIL -> {
                        rotate(40f, Offset(50f, 50f)) {
                            drawRoundRect(AppColors.SwatchFF96B943, Offset(38f, 12f), Size(24f, 62f), CornerRadius(4f))
                            drawRect(AppColors.SwatchFFD8E884, Offset(41f, 14f), Size(6f, 57f))
                            drawPath(
                                Path().apply {
                                    moveTo(38f, 74f)
                                    lineTo(50f, 94f)
                                    lineTo(62f, 74f)
                                    close()
                                },
                                AppColors.SwatchFFE0B17B,
                            )
                            drawPath(
                                Path().apply {
                                    moveTo(46f, 87f)
                                    lineTo(50f, 94f)
                                    lineTo(54f, 87f)
                                    close()
                                },
                                InkBrown,
                            )
                        }
                    }
                    GardenArt.FERTILIZER, GardenArt.SEED -> {
                        if (art == GardenArt.SEED) {
                            drawOval(AppColors.SwatchFF966342, Offset(8f, 73f), Size(84f, 18f))
                            rotate(30f, Offset(50f, 67f)) {
                                drawOval(
                                    Brush.horizontalGradient(listOf(AppColors.SwatchFFEBA24E, AppColors.SwatchFFFFCE7D)),
                                    Offset(29f, 42f),
                                    Size(42f, 42f),
                                )
                            }
                            drawLine(DarkLeaf, Offset(51f, 49f), Offset(51f, 25f), 3f)
                            leaf(50f, 34f, 24f, 28f, angle = -43f)
                            leaf(52f, 33f, 25f, 29f, angle = 40f)
                        } else {
                            val sack =
                                Path().apply {
                                    moveTo(33f, 25f)
                                    quadraticTo(14f, 49f, 15f, 78f)
                                    quadraticTo(50f, 99f, 85f, 78f)
                                    quadraticTo(86f, 49f, 67f, 25f)
                                    lineTo(75f, 10f)
                                    quadraticTo(50f, 17f, 25f, 10f)
                                    close()
                                }
                            drawPath(
                                sack,
                                Brush.horizontalGradient(
                                    listOf(AppColors.SwatchFFE8B365, AppColors.SwatchFFFFE9B7, AppColors.SwatchFFE9B96E),
                                ),
                            )
                            drawPath(sack, AppColors.SwatchFFD69D58, style = Stroke(2f))
                            drawLine(InkBrown, Offset(34f, 28f), Offset(68f, 28f), 4f, StrokeCap.Round)
                            drawLine(DarkLeaf, Offset(50f, 77f), Offset(50f, 55f), 3f)
                            leaf(49f, 65f, 20f, 26f, angle = -45f)
                            leaf(51f, 65f, 20f, 26f, angle = 45f)
                        }
                    }
                    GardenArt.PUMP -> {
                        drawRoundRect(AppColors.SwatchFFF5B34F, Offset(28f, 39f), Size(48f, 52f), CornerRadius(14f))
                        drawRoundRect(AppColors.SwatchFFFFD37F, Offset(34f, 45f), Size(14f, 34f), CornerRadius(7f))
                        drawRect(Honey, Offset(45f, 22f), Size(12f, 22f))
                        drawRoundRect(AppColors.SwatchFFED9B36, Offset(32f, 14f), Size(43f, 10f), CornerRadius(5f))
                    }
                    GardenArt.TREE, GardenArt.NATURAL -> {
                        if (art == GardenArt.NATURAL) {
                            drawOval(DarkLeaf, Offset(8f, 69f), Size(84f, 21f))
                            drawOval(AppColors.SwatchFF9C9C78, Offset(27f, 43f), Size(49f, 36f))
                        } else {
                            drawPath(
                                Path().apply {
                                    moveTo(41f, 91f)
                                    lineTo(46f, 49f)
                                    lineTo(56f, 49f)
                                    lineTo(61f, 91f)
                                    lineTo(50f, 85f)
                                    close()
                                },
                                InkBrown,
                            )
                            drawCircle(LeafGreen, 27f, Offset(50f, 34f))
                            drawCircle(AppColors.SwatchFF90AC45, 23f, Offset(27f, 53f))
                            drawCircle(AppColors.SwatchFF7F9E37, 23f, Offset(73f, 53f))
                            drawCircle(AppColors.SwatchFF9BB84F, 24f, Offset(50f, 54f))
                        }
                    }
                    GardenArt.SHOP -> drawGardenShop()
                    GardenArt.FLOWERS -> drawGardenFlowers()
                    GardenArt.LANTERN -> drawGardenLantern()
                    GardenArt.RIBBON -> drawGardenRibbon()
                    GardenArt.BOOTS -> drawGardenBoots()
                    GardenArt.BROOM -> drawGardenBroom()
                    GardenArt.WATERING_CAN -> Unit
                }
            }
        }
    }
}

@Preview(name = "Task illustration", showBackground = true)
@Composable
private fun SimpleGardenArtPreview() {
    SimpleGardenArt(GardenArt.CHECKED_TASK, Modifier.size(AppDimensions.Dp80))
}

@Preview(name = "Watering illustration", showBackground = true)
@Composable
private fun SimpleGardenArtWateringPreview() {
    SimpleGardenArt(GardenArt.DROP, Modifier.size(AppDimensions.Dp80))
}
