package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Woven basket illustration behind individually selectable live seed packets. */
@Composable
fun GardenSeedBasketSurface(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w=size.width;val h=size.height
        val body=Path().apply {
            moveTo(w*.02f,h*.28f)
            cubicTo(w*.10f,-h*.03f,w*.90f,-h*.03f,w*.98f,h*.28f)
            cubicTo(w*1.02f,h*.58f,w*.91f,h*.97f,w*.80f,h*.98f)
            lineTo(w*.20f,h*.98f)
            cubicTo(w*.09f,h*.97f,-w*.02f,h*.58f,w*.02f,h*.28f)
            close()
        }
        drawPath(body,Brush.verticalGradient(listOf(Color(0xFFE8B573),Color(0xFFC78B47),Color(0xFFAC7039))))
        repeat(7){i->
            val y=h*(.31f+i*.10f)
            drawLine(Color(0xFFE7B778),Offset(w*.10f,y),Offset(w*.90f,y),3.dp.toPx())
        }
        repeat(10){i->
            val x=w*(.10f+i*.09f)
            drawLine(Color(0xFFB67D40).copy(alpha=.6f),Offset(x,h*.22f),Offset(w*.5f+(x-w*.5f)*.75f,h*.95f),2.dp.toPx())
        }
        drawOval(Color(0xFFBF8445),Offset(w*.015f,h*.09f),Size(w*.97f,h*.67f),style=Stroke(11.dp.toPx()))
        drawOval(Color(0xFFF0C78C),Offset(w*.015f,h*.07f),Size(w*.97f,h*.67f),style=Stroke(5.dp.toPx()))
    }
}
