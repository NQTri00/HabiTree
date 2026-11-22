package com.mamxanh.app.feature.stats.presentation.ui.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions

/** Small pressed leaf inspired by the Journal reference, outside the live metric text. */
@Composable
internal fun PressedGinkgo(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val base = Offset(size.width * .62f, size.height * .42f)
        drawPath(
            Path().apply {
                moveTo(base.x, base.y)
                quadraticTo(size.width * .87f, size.height * .70f, size.width * .50f, size.height * .96f)
            },
            AppColors.SwatchFFE3A439,
            style = Stroke(size.width * .065f),
        )
        repeat(7) { index ->
            rotate(-72f + index * 24f, base) {
                drawOval(
                    if (index % 2 == 0) AppColors.SwatchFFFFC34F else AppColors.SwatchFFF5B23D,
                    Offset(base.x - size.width * .20f, base.y - size.height * .40f),
                    Size(size.width * .40f, size.height * .43f),
                )
            }
        }
    }
}

@Preview(name = "Journal leaf", showBackground = true)
@Composable
private fun PressedGinkgoPreview() {
    PressedGinkgo(Modifier.size(AppDimensions.Dp80))
}
