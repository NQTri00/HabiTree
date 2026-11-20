package com.mamxanh.app.core.components.components

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Plain bound cream journal from the simplified reference; entries remain live UI. */
@Composable
fun GardenJournalPage(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(Modifier.widthIn(max = AppDimensions.Dp560).fillMaxSize().padding(top = AppDimensions.Dp24, bottom = AppDimensions.Dp10)
            .background(AppColors.SwatchFFA86E36, RoundedCornerShape(AppDimensions.Dp30))
            .padding(start = AppDimensions.Dp12, top = AppDimensions.Dp5, end = AppDimensions.Dp4, bottom = AppDimensions.Dp10)
            .background(AppColors.SwatchFFE8CBA2, RoundedCornerShape(AppDimensions.Dp25)).padding(AppDimensions.Dp4)
            .background(Brush.horizontalGradient(listOf(AppColors.SwatchFFF9E6C7, AppColors.SwatchFFFFF5E5, AppColors.SwatchFFFAE9CF)), RoundedCornerShape(AppDimensions.Dp22))
            .border(AppDimensions.Dp1_5, AppColors.SwatchFFE1BA86, RoundedCornerShape(AppDimensions.Dp22))) {
            Canvas(Modifier.matchParentSize()) {
                repeat(4) { index ->
                    val y = size.height * (.13f + index * .23f)
                    drawLine(AppColors.SwatchFFAC763C, Offset(AppDimensions.DpMinus12.toPx(), y + AppDimensions.Dp2.toPx()),
                        Offset(AppDimensions.Dp8.toPx(), y + AppDimensions.Dp2.toPx()), AppDimensions.Dp10.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(AppColors.SwatchFFE4B474, Offset(AppDimensions.DpMinus12.toPx(), y - AppDimensions.Dp1.toPx()),
                        Offset(AppDimensions.Dp8.toPx(), y - AppDimensions.Dp1.toPx()), AppDimensions.Dp5.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
                }
            }
            Box(Modifier.fillMaxSize().padding(start = AppDimensions.Dp22, top = AppDimensions.Dp36, end = AppDimensions.Dp16, bottom = AppDimensions.Dp20), content = content)
        }
    }
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
fun GardenJournalPagePreview() { GardenJournalPage(Modifier.fillMaxSize()) {} }
