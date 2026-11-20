package com.mamxanh.app.feature.stats.presentation.ui.stats

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
import com.mamxanh.app.core.components.Deep
import com.mamxanh.app.core.components.MamXanhPreviewTheme
import com.mamxanh.app.core.components.components.GardenArt
import com.mamxanh.app.core.components.components.GardenArtwork

/** Labels may wrap; ticks and bars always share the same 64dp plot and baseline. */
@Composable
internal fun WeekSproutChart(counts: List<Int>, days: List<String>, modifier: Modifier = Modifier) {
    val values=counts.takeLast(7).map{it.coerceAtLeast(0)}
    val maxValue=maxOf(4,values.maxOrNull()?:0).toDouble()
    Row(modifier.semantics(mergeDescendants=true){contentDescription=values.mapIndexed{index,value->AppText.text(TextKey.Copy01ViecEc6e1e,days.getOrElse(index){""},value)}.joinToString(", ")}.fillMaxWidth().height(AppDimensions.Dp150),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12),verticalAlignment=Alignment.Bottom) {
        values.forEachIndexed{index,value->
            Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)) {
                Text(value.toString(),color=com.mamxanh.app.core.components.Muted,fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp14,textAlign=TextAlign.Center)
                Box(Modifier.widthIn(max=AppDimensions.Dp24).fillMaxWidth().height(maxOf(AppDimensions.Dp4,AppDimensions.Dp100*(value/maxValue).toFloat()))
                    .background(if(index==values.lastIndex)com.mamxanh.app.core.components.Green else AppColors.SwatchFFDCECB8,RoundedCornerShape(topStart=AppDimensions.Dp7,topEnd=AppDimensions.Dp7,bottomStart=AppDimensions.Dp3,bottomEnd=AppDimensions.Dp3)))
                Text(days.getOrElse(index){""},color=com.mamxanh.app.core.components.Muted,fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp14,textAlign=TextAlign.Center)
            }
        }
    }
}

@Preview(name = "Chart · mixed values, large text", widthDp = 320, heightDp = 220, fontScale = 1.3f,
    device = "spec:width=320dp,height=220dp,dpi=160")
@Composable
fun WeekSproutChartLargeTextPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(listOf(0, 1, 10, 12, 3, 1, 2), listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(AppDimensions.Dp8))
    }
}

@Preview(name = "Chart · no completions", widthDp = 320, heightDp = 210,
    device = "spec:width=320dp,height=210dp,dpi=160")
@Composable
fun WeekSproutChartEmptyPreview() {
    MamXanhPreviewTheme {
        WeekSproutChart(List(7) { 0 }, listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN"), Modifier.padding(AppDimensions.Dp8))
    }
}
