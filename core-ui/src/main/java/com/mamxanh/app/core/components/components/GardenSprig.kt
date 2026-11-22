package com.mamxanh.app.core.components.components

import com.mamxanh.app.core.components.design.AppDimensions
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppColors

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
        drawPath(stem, AppColors.SwatchFF8A9C3D, style = Stroke(w * .055f, cap = StrokeCap.Round))
        listOf(
            Triple(.27f, .20f, -35f), Triple(.60f, .34f, 38f),
            Triple(.25f, .48f, -38f), Triple(.62f, .61f, 40f),
            Triple(.35f, .75f, -40f)
        ).forEachIndexed { index, (leafX, leafY, angle) ->
            val center = Offset(x(leafX), h * leafY)
            rotate(if (mirrored) -angle else angle, center) {
                drawOval(if (index % 2 == 0) AppColors.SwatchFF7C9D30 else AppColors.SwatchFFA3B747,
                    topLeft = center - Offset(w * .16f, h * .11f), size = Size(w * .32f, h * .22f))
            }
        }
    }
}

@Preview(name = "Garden sprig", showBackground = true)
@Composable
private fun GardenSprigPreview() {
    GardenSprig(Modifier.size(AppDimensions.Dp80, AppDimensions.Dp120))
}

@Preview(name = "Mirrored sprig", showBackground = true)
@Composable
private fun GardenSprigMirroredPreview() {
    GardenSprig(Modifier.size(AppDimensions.Dp80, AppDimensions.Dp120), mirrored = true)
}
