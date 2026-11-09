package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/** Small pressed leaf inspired by the Journal reference, outside the live metric text. */
@Composable
internal fun PressedGinkgo(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val base = Offset(size.width * .62f, size.height * .42f)
        drawPath(Path().apply {
            moveTo(base.x, base.y)
            quadraticTo(size.width * .87f, size.height * .70f, size.width * .50f, size.height * .96f)
        }, Color(0xFFE3A439), style = Stroke(size.width * .065f))
        repeat(7) { index ->
            rotate(-72f + index * 24f, base) {
                drawOval(if (index % 2 == 0) Color(0xFFFFC34F) else Color(0xFFF5B23D),
                    Offset(base.x - size.width * .20f, base.y - size.height * .40f),
                    Size(size.width * .40f, size.height * .43f))
            }
        }
    }
}
