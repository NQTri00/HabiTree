package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import com.mamxanh.app.presentation.ui.components.GardenArt
import com.mamxanh.app.presentation.ui.components.GardenArtwork

/** Labels may wrap; ticks and bars always share the same 64dp plot and baseline. */
@Composable
internal fun WeekSproutChart(counts: List<Int>, days: List<String>, modifier: Modifier = Modifier) {
    val values = counts.map { it.coerceAtLeast(0) }
    val axisMax = 3
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer(cacheSize = 16)
    val countStyle = LocalTextStyle.current.copy(fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
    val axisStyle = LocalTextStyle.current.copy(fontSize = 10.sp, lineHeight = 13.sp)
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val ticks = (0..3).map { (it * axisMax / 3).toString() }
        val axisWidth = maxOf(20.dp * density.fontScale, with(density) {
            ticks.maxOf { measurer.measure(it, style = axisStyle).size.width }.toDp() + 6.dp
        })
        val columnWidth = ((maxWidth - axisWidth - 7.dp * (values.size - 1).coerceAtLeast(0)) /
            values.size.coerceAtLeast(1)).coerceAtLeast(1.dp)
        val labelConstraints = Constraints(maxWidth = with(density) { columnWidth.roundToPx() }.coerceAtLeast(1))
        val axisLabelHeight = with(density) { measurer.measure("0", style = axisStyle).size.height.toDp() }
        val countLabelHeight = with(density) {
            (values.maxOfOrNull {
                measurer.measure(if (it > axisMax) "$it↑" else it.toString(), style = countStyle, constraints = labelConstraints).size.height
            } ?: measurer.measure("0", style = countStyle).size.height).toDp()
        }
        val plotHeight = 64.dp
        val topSpace = countLabelHeight + 6.dp + 2.dp
        val chartHeight = topSpace + plotHeight
        Column {
            Row(Modifier.fillMaxWidth().height(chartHeight)) {
                Box(Modifier.width(axisWidth).fillMaxHeight()) {
                    ticks.forEachIndexed { tick, label ->
                        Text(label,
                            Modifier.fillMaxWidth().offset(y = topSpace + plotHeight * (1f - tick / 3f) - axisLabelHeight / 2),
                            color = Deep, style = axisStyle, textAlign = TextAlign.Start, maxLines = 1)
                    }
                }
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    Canvas(Modifier.matchParentSize()) {
                        val top = topSpace.toPx()
                        val height = plotHeight.toPx()
                        (0..3).forEach { tick ->
                            val y = top + height * (1f - tick / 3f)
                            drawLine(Color(0xFF9EB6AA).copy(alpha = if (tick == 0) .85f else .25f),
                                Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                        }
                        drawLine(Color(0xFF82988D), Offset(0f, top), Offset(0f, top + height), 1.dp.toPx())
                    }
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.Bottom) {
                        values.forEachIndexed { index, count ->
                            Column(Modifier.weight(1f).fillMaxHeight().semantics(mergeDescendants = true) {
                                contentDescription = "${days.getOrElse(index) { "" }}: $count việc hoàn thành"
                            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                Text(if (count > axisMax) "$count↑" else count.toString(), Modifier.fillMaxWidth(), color = Deep,
                                    style = countStyle, textAlign = TextAlign.Center)

                                Box(Modifier.width(18.dp).height(if (count == 0) 0.dp else plotHeight * (count.toFloat() / axisMax).coerceAtMost(1f))
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFF8BD32C), Color(0xFFB5E65A), Color(0xFF94D83B)))))
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(start = axisWidth, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                days.forEach { day ->
                    Text(day, Modifier.weight(1f), color = Deep, fontSize = 11.sp, lineHeight = 14.sp,
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(name = "Chart · mixed values, large text", widthDp = 320, heightDp = 220, fontScale = 1.3f,
    device = "spec:width=320dp,height=220dp,dpi=160")
@Composable
fun WeekSproutChartLargeTextPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(listOf(0, 1, 10, 12, 3, 1, 2), listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(8.dp))
    }
}

@Preview(name = "Chart · no completions", widthDp = 320, heightDp = 210,
    device = "spec:width=320dp,height=210dp,dpi=160")
@Composable
fun WeekSproutChartEmptyPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(List(7) { 0 }, listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(8.dp))
    }
}
