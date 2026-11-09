package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/** Small decorative olive sprig from the simple journal references. */
@Composable
fun GardenSprig(modifier: Modifier = Modifier, mirrored: Boolean = false) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        fun x(value: Float) = w * if (mirrored) 1f - value else value
        val stem = Path().apply {
            moveTo(x(.62f), h * .94f)
            cubicTo(x(.30f), h * .66f, x(.48f), h * .30f, x(.34f), h * .08f)
        }
        drawPath(stem, Color(0xFF8A9C3D), style = Stroke(w * .055f, cap = StrokeCap.Round))
        listOf(
            Triple(.27f, .20f, -35f), Triple(.60f, .34f, 38f),
            Triple(.25f, .48f, -38f), Triple(.62f, .61f, 40f),
            Triple(.35f, .75f, -40f)
        ).forEachIndexed { index, (leafX, leafY, angle) ->
            val center = Offset(x(leafX), h * leafY)
            rotate(if (mirrored) -angle else angle, center) {
                drawOval(if (index % 2 == 0) Color(0xFF7C9D30) else Color(0xFFA3B747),
                    topLeft = center - Offset(w * .16f, h * .11f), size = Size(w * .32f, h * .22f))
            }
        }
    }
}
