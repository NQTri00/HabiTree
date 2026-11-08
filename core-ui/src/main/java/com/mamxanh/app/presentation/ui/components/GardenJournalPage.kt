package com.mamxanh.app.presentation.ui.components

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
        Box(Modifier.widthIn(max = 560.dp).fillMaxSize().padding(top = 24.dp, bottom = 10.dp)
            .background(Color(0xFFA86E36), RoundedCornerShape(30.dp))
            .padding(start = 12.dp, top = 5.dp, end = 4.dp, bottom = 10.dp)
            .background(Color(0xFFE8CBA2), RoundedCornerShape(25.dp)).padding(4.dp)
            .background(Brush.horizontalGradient(listOf(Color(0xFFF9E6C7), Color(0xFFFFF5E5), Color(0xFFFAE9CF))), RoundedCornerShape(22.dp))
            .border(1.5.dp, Color(0xFFE1BA86), RoundedCornerShape(22.dp))) {
            Canvas(Modifier.matchParentSize()) {
                repeat(4) { index ->
                    val y = size.height * (.13f + index * .23f)
                    drawLine(Color(0xFFAC763C), Offset(-12.dp.toPx(), y + 2.dp.toPx()),
                        Offset(8.dp.toPx(), y + 2.dp.toPx()), 10.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(Color(0xFFE4B474), Offset(-12.dp.toPx(), y - 1.dp.toPx()),
                        Offset(8.dp.toPx(), y - 1.dp.toPx()), 5.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
                }
            }
            Box(Modifier.fillMaxSize().padding(start = 22.dp, top = 36.dp, end = 16.dp, bottom = 20.dp), content = content)
        }
    }
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
fun GardenJournalPagePreview() { GardenJournalPage(Modifier.fillMaxSize()) {} }
