package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mamxanh.app.presentation.ui.*

/**
 * Draws the scenic pastoral farm backdrop matching Concept 1:
 * - Watercolor sky with soft morning sun
 * - Traditional windmill with lattice sails on rolling hill
 * - Charming rose-pink barn with white trim and cross-braced windows
 * - White rustic pasture fence
 * - Lush rolling green hills dotted with wildflowers
 */
@Composable
fun FarmPastoralBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Sky & Sun glow
        drawRect(SkyPastel)
        drawCircle(
            color = Color(0xFFFFF6D6).copy(alpha = 0.85f),
            radius = w * 0.22f,
            center = Offset(w * 0.52f, h * 0.08f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = w * 0.12f,
            center = Offset(w * 0.52f, h * 0.08f)
        )

        // Soft distant clouds
        val cloudColor = Color.White.copy(alpha = 0.55f)
        drawOval(cloudColor, Offset(w * 0.05f, h * 0.03f), Size(w * 0.35f, h * 0.06f))
        drawOval(cloudColor, Offset(w * 0.60f, h * 0.04f), Size(w * 0.42f, h * 0.07f))

        // 2. Far hills
        drawPath(Path().apply {
            moveTo(0f, h * 0.22f)
            cubicTo(w * 0.25f, h * 0.14f, w * 0.55f, h * 0.22f, w * 0.85f, h * 0.15f)
            lineTo(w, h * 0.17f)
            lineTo(w, h * 0.45f)
            lineTo(0f, h * 0.45f)
            close()
        }, Color(0xFFA5CCE0).copy(alpha = 0.6f))

        // 3. Middle rolling green hills
        drawPath(Path().apply {
            moveTo(0f, h * 0.28f)
            cubicTo(w * 0.20f, h * 0.18f, w * 0.50f, h * 0.28f, w * 0.78f, h * 0.20f)
            lineTo(w, h * 0.22f)
            lineTo(w, h * 0.60f)
            lineTo(0f, h * 0.60f)
            close()
        }, Color(0xFF9ECEB5).copy(alpha = 0.75f))

        // 4. Windmill
        val wmX = w * 0.12f
        val wmY = h * 0.12f
        val wmBaseW = w * 0.10f
        val wmH = h * 0.11f
        val tower = Path().apply {
            moveTo(wmX - wmBaseW * 0.35f, wmY + wmH)
            lineTo(wmX - wmBaseW * 0.22f, wmY + wmH * 0.25f)
            lineTo(wmX + wmBaseW * 0.22f, wmY + wmH * 0.25f)
            lineTo(wmX + wmBaseW * 0.35f, wmY + wmH)
            close()
        }
        drawPath(tower, Color(0xFFB5BBC7))
        drawPath(tower, Color(0xFF7E879B), style = Stroke(w * 0.003f))

        drawArc(
            color = Color(0xFF8B92A4),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(wmX - wmBaseW * 0.25f, wmY + wmH * 0.08f),
            size = Size(wmBaseW * 0.5f, wmH * 0.30f)
        )
        drawCircle(Color(0xFF5A6273), w * 0.012f, Offset(wmX, wmY + wmH * 0.22f))

        val bladeLen = w * 0.11f
        val bladeAngles = listOf(25f, 115f, 205f, 295f)
        bladeAngles.forEach { deg ->
            val rad = Math.toRadians(deg.toDouble())
            val bx = wmX + (Math.cos(rad) * bladeLen).toFloat()
            val by = (wmY + wmH * 0.22f) + (Math.sin(rad) * bladeLen).toFloat()
            drawLine(Color(0xFF6B7280), Offset(wmX, wmY + wmH * 0.22f), Offset(bx, by), w * 0.005f)
            for (step in 1..4) {
                val t = step / 4.5f
                val rx = wmX + (Math.cos(rad) * bladeLen * t).toFloat()
                val ry = (wmY + wmH * 0.22f) + (Math.sin(rad) * bladeLen * t).toFloat()
                val perpRad = rad + Math.PI / 2
                val rungLen = w * 0.018f * t
                val p1 = Offset(rx - (Math.cos(perpRad) * rungLen).toFloat(), ry - (Math.sin(perpRad) * rungLen).toFloat())
                val p2 = Offset(rx + (Math.cos(perpRad) * rungLen).toFloat(), ry + (Math.sin(perpRad) * rungLen).toFloat())
                drawLine(Color(0xFF8D95A5), p1, p2, w * 0.003f)
            }
        }

        // 5. Pink Barn
        val barnX = w * 0.31f
        val barnY = h * 0.17f
        val barnW = w * 0.34f
        val barnH = h * 0.14f

        drawRect(
            color = BarnRed,
            topLeft = Offset(barnX - barnW * 0.45f, barnY + barnH * 0.32f),
            size = Size(barnW * 0.90f, barnH * 0.68f)
        )
        val barnRoof = Path().apply {
            moveTo(barnX - barnW * 0.50f, barnY + barnH * 0.34f)
            lineTo(barnX - barnW * 0.44f, barnY + barnH * 0.12f)
            lineTo(barnX, barnY - barnH * 0.08f)
            lineTo(barnX + barnW * 0.44f, barnY + barnH * 0.12f)
            lineTo(barnX + barnW * 0.50f, barnY + barnH * 0.34f)
            close()
        }
        drawPath(barnRoof, BarnRedDark)
        drawPath(barnRoof, BarnWhite, style = Stroke(w * 0.007f))

        val winW = barnW * 0.18f
        val winH = barnH * 0.20f
        listOf(-0.16f, 0.16f).forEach { offX ->
            val wx = barnX + barnW * offX - winW * 0.5f
            val wy = barnY + barnH * 0.08f
            drawRect(Color(0xFF863748), Offset(wx, wy), Size(winW, winH))
            drawRect(BarnWhite, Offset(wx, wy), Size(winW, winH), style = Stroke(w * 0.004f))
            drawLine(BarnWhite, Offset(wx, wy), Offset(wx + winW, wy + winH), w * 0.003f)
            drawLine(BarnWhite, Offset(wx + winW, wy), Offset(wx, wy + winH), w * 0.003f)
        }

        val mWinW = barnW * 0.10f
        val mWinH = barnH * 0.18f
        listOf(-0.25f, 0f, 0.25f).forEach { offX ->
            val wx = barnX + barnW * offX - mWinW * 0.5f
            val wy = barnY + barnH * 0.38f
            drawRect(Color(0xFF863748), Offset(wx, wy), Size(mWinW, mWinH))
            drawRect(BarnWhite, Offset(wx, wy), Size(mWinW, mWinH), style = Stroke(w * 0.004f))
        }

        val doorW = barnW * 0.28f
        val doorH = barnH * 0.36f
        val doorY = barnY + barnH * 0.64f
        listOf(-0.17f, 0.17f).forEach { offX ->
            val dx = barnX + barnW * offX - doorW * 0.5f
            drawRect(BarnRedDark, Offset(dx, doorY), Size(doorW, doorH))
            drawRect(BarnWhite, Offset(dx, doorY), Size(doorW, doorH), style = Stroke(w * 0.005f))
            drawLine(BarnWhite, Offset(dx, doorY), Offset(dx + doorW, doorY + doorH), w * 0.004f)
            drawLine(BarnWhite, Offset(dx + doorW, doorY), Offset(dx, doorY + doorH), w * 0.004f)
        }

        // 6. White Pasture Fence
        val fenceY = h * 0.27f
        val fencePoints = listOf(
            Offset(0f, fenceY),
            Offset(w * 0.07f, fenceY - h * 0.02f),
            Offset(w * 0.14f, fenceY - h * 0.04f),
            Offset(w * 0.21f, fenceY - h * 0.03f)
        )
        drawLine(BarnWhite.copy(alpha = 0.85f), fencePoints[0], fencePoints[3], w * 0.006f)
        drawLine(BarnWhite.copy(alpha = 0.85f), Offset(fencePoints[0].x, fencePoints[0].y + h * 0.018f),
            Offset(fencePoints[3].x, fencePoints[3].y + h * 0.018f), w * 0.006f)
        fencePoints.forEach { pt ->
            drawLine(BarnWhite, Offset(pt.x, pt.y - h * 0.015f), Offset(pt.x, pt.y + h * 0.035f), w * 0.008f)
        }

        // 7. Tree groves
        listOf(
            Triple(0.70f, 0.22f, 0.14f),
            Triple(0.85f, 0.20f, 0.16f),
            Triple(0.96f, 0.24f, 0.13f)
        ).forEach { (fx, fy, fr) ->
            drawCircle(Color(0xFF7FA89E).copy(alpha = 0.6f), w * fr, Offset(w * fx, h * fy))
            drawCircle(Color(0xFF91BAAF).copy(alpha = 0.5f), w * (fr * 0.75f), Offset(w * (fx - 0.02f), h * (fy - 0.02f)))
        }

        // 8. Meadow hill
        val meadowPath = Path().apply {
            moveTo(0f, h * 0.32f)
            cubicTo(w * 0.30f, h * 0.25f, w * 0.70f, h * 0.34f, w, h * 0.27f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(meadowPath, MeadowPastel)

        drawPath(Path().apply {
            moveTo(0f, h * 0.68f)
            cubicTo(w * 0.35f, h * 0.62f, w * 0.65f, h * 0.72f, w, h * 0.64f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }, MeadowDark.copy(alpha = 0.25f))

        // 9. Dotted wildflowers
        val flowers = listOf(
            Pair(0.06f, 0.37f), Pair(0.12f, 0.42f), Pair(0.08f, 0.46f),
            Pair(0.18f, 0.52f), Pair(0.24f, 0.45f), Pair(0.38f, 0.38f),
            Pair(0.74f, 0.35f), Pair(0.82f, 0.38f), Pair(0.91f, 0.44f),
            Pair(0.78f, 0.48f), Pair(0.86f, 0.53f), Pair(0.93f, 0.58f),
            Pair(0.09f, 0.62f), Pair(0.16f, 0.68f), Pair(0.35f, 0.71f),
            Pair(0.70f, 0.68f), Pair(0.84f, 0.71f), Pair(0.90f, 0.64f),
            Pair(0.22f, 0.82f), Pair(0.42f, 0.86f), Pair(0.66f, 0.88f),
            Pair(0.14f, 0.90f), Pair(0.80f, 0.86f)
        )
        flowers.forEach { (fx, fy) ->
            val center = Offset(w * fx, h * fy)
            val petalRadius = w * 0.010f
            val petalOff = w * 0.007f
            val flowerColor = Color.White.copy(alpha = 0.92f)
            drawCircle(flowerColor, petalRadius, Offset(center.x - petalOff, center.y))
            drawCircle(flowerColor, petalRadius, Offset(center.x + petalOff, center.y))
            drawCircle(flowerColor, petalRadius, Offset(center.x, center.y - petalOff))
            drawCircle(flowerColor, petalRadius, Offset(center.x, center.y + petalOff))
            drawCircle(Color(0xFFFFD34D), w * 0.006f, center)
        }
    }
}
