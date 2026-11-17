package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.mamxanh.app.presentation.ui.Deep

@Composable
fun GardenPaintedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, primary: Boolean = true) {
    val shape = RoundedCornerShape(AppDimensions.Dp15)
    Box(modifier.heightIn(min = AppDimensions.Dp48).alpha(if (enabled) AppOpacity.Value1 else AppOpacity.Value_42)
        .mintDepth(shape, if (primary) MintDepth.primary else MintDepth.secondary, if(enabled) AppDimensions.Dp3 else AppDimensions.Dp0)
        .clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .padding(horizontal = AppDimensions.Dp16, vertical = AppDimensions.Dp12), contentAlignment = Alignment.Center) {
        GardenRichText(text, color = if (primary) AppColors.White else Deep, fontSize = AppTypography.Sp14, lineHeight = AppTypography.Sp20,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
fun GardenLeafPlaque(text: String, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = AppDimensions.Dp56), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w=size.width;val h=size.height
            val ribbon=Path().apply {
                moveTo(0f,h*.20f)
                quadraticTo(w*.50f,-h*.10f,w,h*.20f)
                lineTo(w,h*.90f)
                quadraticTo(w*.50f,h*1.04f,0f,h*.90f)
                close()
            }
            drawPath(ribbon,Brush.verticalGradient(listOf(AppColors.SwatchFFFFF4DC,AppColors.SwatchFFFFE6BC)))
            drawPath(ribbon,AppColors.SwatchFFD4AC77,style=Stroke(AppDimensions.Dp1_5.toPx()))
        }
        GardenRichText(text, Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp12),
            color = Deep, fontSize = AppTypography.Sp23, lineHeight = AppTypography.Sp27, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun GardenWoodSign(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = AppTypography.Sp28) {
    Box(modifier.heightIn(min = AppDimensions.Dp52), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width; val h = size.height
            val board = Path().apply {
                moveTo(w * .08f, h * .12f)
                cubicTo(w * .27f, -h * .02f, w * .73f, -h * .02f, w * .92f, h * .12f)
                cubicTo(w * 1.02f, h * .27f, w * .98f, h * .78f, w * .90f, h * .90f)
                quadraticTo(w * .50f, h * .75f, w * .10f, h * .90f)
                cubicTo(w * .02f, h * .78f, -w * .02f, h * .27f, w * .08f, h * .12f)
                close()
            }
            drawPath(board, Brush.verticalGradient(listOf(AppColors.SwatchFFC99050, AppColors.SwatchFF996034, AppColors.SwatchFFB77C42)))
            drawPath(board, AppColors.SwatchFFE2AE6C, style = Stroke(AppDimensions.Dp3.toPx()))
            drawLine(AppColors.SwatchFF85542D.copy(alpha = AppOpacity.Value_25),
                androidx.compose.ui.geometry.Offset(w*.10f,h*.30f), androidx.compose.ui.geometry.Offset(w*.90f,h*.27f), AppDimensions.Dp1.toPx())
        }
        GardenRichText(text, color = AppColors.SwatchFFFFF2D1, fontWeight = FontWeight.Bold, fontSize = fontSize, lineHeight = fontSize * 1.15f,
            textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = AppDimensions.Dp22, vertical = AppDimensions.Dp13))
    }
}

@Preview(widthDp = 220)
@Composable
fun GardenPaintedButtonPreview() { GardenPaintedButton(AppText.text(TextKey.CopyGieoCay31adde), {}) }
@Preview(widthDp = 160)
@Composable
fun GardenSecondaryButtonPreview() { GardenPaintedButton(AppText.text(TextKey.CopyDeSau9be0a7), {}, primary = false) }
@Preview(widthDp = 300)
@Composable
fun GardenWoodSignPreview() { GardenWoodSign(AppText.text(TextKey.CopyVuonUomCd0b43), Modifier.fillMaxWidth()) }
@Preview(widthDp = 300)
@Composable
fun GardenLeafPlaquePreview() { GardenLeafPlaque(AppText.text(TextKey.CopyGieoMotViecNho227fcc)) }

@Preview(name = "Ribbon · long name, large text", widthDp = 288, fontScale = 1.3f,
    device = "spec:width=288dp,height=180dp,dpi=160")
@Composable
fun GardenLeafPlaqueLongNamePreview() {
    com.mamxanh.app.presentation.ui.MamXanhPreviewTheme {
        GardenLeafPlaque(AppText.text(TextKey.CopyCayDaoBinhYenBenCuaSoCuaGiaDinh3c7da8), Modifier.fillMaxWidth())
    }
}
