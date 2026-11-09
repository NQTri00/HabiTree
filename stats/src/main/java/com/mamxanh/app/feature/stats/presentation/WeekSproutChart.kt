package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.components.GardenArt
import com.mamxanh.app.presentation.ui.components.GardenArtwork

/** Ticks and bars share the same baseline and scale, including at large text sizes. */
@Composable
internal fun WeekSproutChart(counts: List<Int>, days: List<String>, modifier: Modifier = Modifier) {
    val values = counts.map { it.coerceAtLeast(0) }
    val axisMax = ((((values.maxOrNull() ?: 0).coerceAtLeast(3).toLong() + 2) / 3) * 3)
    val fontScale = LocalDensity.current.fontScale
    val plotHeight = 112.dp
    val countLabelHeight = 16.dp * fontScale
    val topSpace = countLabelHeight + 18.dp + 4.dp
    val chartHeight = topSpace + plotHeight
    val axisWidth = maxOf(20, axisMax.toString().length * 7 + 6).dp * fontScale
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().height(chartHeight)) {
            Box(Modifier.width(axisWidth).fillMaxHeight()) {
                (0..3).forEach { tick ->
                    Text((tick * axisMax / 3).toString(),
                        Modifier.fillMaxWidth().offset(y = topSpace + plotHeight * (1f - tick / 3f) - countLabelHeight / 2),
                        color = Deep, fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Start, maxLines = 1)
                }
            }
            Box(Modifier.weight(1f).fillMaxHeight()) {
                Canvas(Modifier.matchParentSize()) {
                    val top = topSpace.toPx()
                    val height = plotHeight.toPx()
                    (0..3).forEach { tick ->
                        val y = top + height * (1f - tick / 3f)
                        drawLine(Color(0xFFB58C54).copy(alpha = if (tick == 0) .85f else .25f),
                            Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    }
                    drawLine(Color(0xFF8B6A39), Offset(0f, top), Offset(0f, top + height), 1.dp.toPx())
                }
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.Bottom) {
                    values.forEachIndexed { index, count ->
                        Column(Modifier.weight(1f).fillMaxHeight().semantics(mergeDescendants = true) {
                            contentDescription = "${days.getOrElse(index) { "" }}: $count việc hoàn thành"
                        }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            Text(count.toString(), color = Deep, fontWeight = FontWeight.Bold,
                                fontSize = if (count > 99) 11.sp else 13.sp, lineHeight = 16.sp, maxLines = 1)
                            GardenArtwork(GardenArt.SPROUT, Modifier.width(26.dp).height(18.dp))
                            Box(Modifier.width(27.dp).height(if (count == 0) 2.dp else plotHeight * (count.toFloat() / axisMax))
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF789C22), Color(0xFFA6BD45), Color(0xFF6B9022)))))
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(start = axisWidth, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            days.forEach { day ->
                Text(day, Modifier.weight(1f), color = Deep, fontSize = 11.sp, lineHeight = 14.sp,
                    textAlign = TextAlign.Center, maxLines = 1)
            }
        }
    }
}

@Preview(name = "Chart · mixed values, large text", widthDp = 320, heightDp = 220, fontScale = 1.3f)
@Composable
fun WeekSproutChartLargeTextPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(listOf(0, 1, 10, 12, 3, 1, 2), listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(8.dp))
    }
}

@Preview(name = "Chart · no completions", widthDp = 320, heightDp = 210)
@Composable
fun WeekSproutChartEmptyPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(List(7) { 0 }, listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(8.dp))
    }
}
