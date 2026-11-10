package com.mamxanh.app.presentation.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep

/** A shallow reference-style arch; long or enlarged text wraps instead of shrinking. */
@Composable
fun GardenArchedHeading(label: String, modifier: Modifier = Modifier, fontSize: TextUnit = 20.sp) {
    val density = LocalDensity.current
    val textSize = with(density) { fontSize.toPx() }
    val headingHeight = with(density) { fontSize.toDp() } + 40.dp
    val paint = remember(textSize) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("serif", Typeface.BOLD)
            this.textSize = textSize
            color = Deep.toArgb()
            textAlign = Paint.Align.LEFT
        }
    }
    BoxWithConstraints(modifier) {
        val room = with(density) { (maxWidth - 24.dp).toPx() }
        if (paint.measureText(label) > room) {
            GardenRichText(label, Modifier.fillMaxWidth().heightIn(min = headingHeight).semantics { heading() },
                color = Deep, fontSize = fontSize, lineHeight = fontSize * 1.2f,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        } else {
            Canvas(Modifier.fillMaxWidth().heightIn(min = headingHeight).semantics {
                text = AnnotatedString(label)
                heading()
            }) {
                val inset = 8.dp.toPx()
                val baseline = -paint.fontMetrics.ascent + 6.dp.toPx()
                val bend = 6.dp.toPx()
                val arc = android.graphics.Path().apply {
                    moveTo(inset, baseline + bend)
                    quadTo(size.width / 2, baseline - bend, size.width - inset, baseline + bend)
                }
                val length = android.graphics.PathMeasure(arc, false).length
                drawIntoCanvas {
                    it.nativeCanvas.drawTextOnPath(label, arc, (length - paint.measureText(label)) / 2, 0f, paint)
                }
                val underline = Path().apply {
                    moveTo(inset, baseline + bend + 10.dp.toPx())
                    quadraticTo(size.width / 2, baseline - bend + 10.dp.toPx(),
                        size.width - inset, baseline + bend + 10.dp.toPx())
                }
                drawPath(underline, Color(0xFFC89B61).copy(alpha = .45f), style = Stroke(1.dp.toPx()))
            }
        }
    }
}
