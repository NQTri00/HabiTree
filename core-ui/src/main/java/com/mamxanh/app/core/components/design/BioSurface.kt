package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.core.ui.R

fun bioShape(radius: Int = AppRadii.VALUE_22) =
    RoundedCornerShape(topStart = radius.dp, topEnd = (radius / 2).dp, bottomEnd = radius.dp, bottomStart = radius.dp)

fun bioHeroBrush(p: BioPalette) =
    object : ShaderBrush() {
        override fun createShader(size: androidx.compose.ui.geometry.Size): Shader {
            val dx = .5735764f
            val dy = .8191521f
            val half = (size.width * dx + size.height * dy) / 2f
            val center =
                androidx.compose.ui.geometry
                    .Offset(size.width / 2f, size.height / 2f)
            val delta =
                androidx.compose.ui.geometry
                    .Offset(dx * half, dy * half)
            return LinearGradientShader(
                center - delta,
                center + delta,
                listOf(AppColors.White.copy(alpha = AppOpacity.VALUE_6), p.soft, lerp(p.soft, p.accent, AppThemeEffects.HERO_BLEND)),
                listOf(0f, .46f, 1f),
            )
        }
    }

fun Modifier.bioLift(
    shape: Shape,
    tint: Color,
) = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = android.graphics.Path()
    when (outline) {
        is Outline.Rounded -> {
            val r = outline.roundRect
            path.addRoundRect(
                android.graphics.RectF(r.left, r.top, r.right, r.bottom),
                floatArrayOf(
                    r.topLeftCornerRadius.x,
                    r.topLeftCornerRadius.y,
                    r.topRightCornerRadius.x,
                    r.topRightCornerRadius.y,
                    r.bottomRightCornerRadius.x,
                    r.bottomRightCornerRadius.y,
                    r.bottomLeftCornerRadius.x,
                    r.bottomLeftCornerRadius.y,
                ),
                android.graphics.Path.Direction.CW,
            )
        }
        is Outline.Rectangle ->
            path.addRect(
                outline.rect.left,
                outline.rect.top,
                outline.rect.right,
                outline.rect.bottom,
                android.graphics.Path.Direction.CW,
            )
        is Outline.Generic -> path.set(outline.path.asAndroidPath())
    }
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        paint.color = tint.copy(alpha = AppOpacity.VALUE_035).toArgb()
        canvas.nativeCanvas.save()
        canvas.nativeCanvas.translate(0f, AppDimensions.Dp3.toPx())
        canvas.nativeCanvas.drawPath(path, paint)
        canvas.nativeCanvas.restore()
        paint.color = AppColors.White.toArgb()
        paint.setShadowLayer(AppDimensions.Dp12.toPx(), 0f, AppDimensions.Dp8.toPx(), tint.copy(alpha = AppOpacity.VALUE_08).toArgb())
        canvas.nativeCanvas.drawPath(path, paint)
    }
}

@Composable fun BioCard(
    modifier: Modifier = Modifier,
    tint: Color = bp.surface,
    padding: Int = AppSpacing.VALUE_18,
    radius: Int = AppRadii.VALUE_22,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = bioShape(radius)
    Column(
        modifier
            .bioLift(shape, bp.deep)
            .background(tint, shape)
            .border(
                AppDimensions.Dp1,
                borderColor ?: AppColors.White.copy(alpha = AppOpacity.VALUE_8),
                shape,
            ).then(
                if (onClick !=
                    null
                ) {
                    Modifier.clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick)
                } else {
                    Modifier
                },
            ).padding(
                (
                    padding +
                        1
                ).dp,
            ),
        content = content,
    )
}

@Preview(name = "Lifted cards", widthDp = 335, showBackground = true)
@Composable
private fun BioSurfacePreview() {
    Column(Modifier.padding(AppDimensions.Dp12), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        BioCard(Modifier.fillMaxWidth()) { Bt(stringResource(R.string.copy_vuon_9e4c10)) }
        BioCard(Modifier.fillMaxWidth(), tint = bp.soft) { Bt(stringResource(R.string.copy_da_tuoi_de2042)) }
    }
}
