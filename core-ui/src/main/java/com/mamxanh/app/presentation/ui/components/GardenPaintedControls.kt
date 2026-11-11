package com.mamxanh.app.presentation.ui.components

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
    val shape = RoundedCornerShape(50)
    Box(modifier.heightIn(min = 48.dp).alpha(if (enabled) 1f else .5f)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.mintDepth(shape, if (primary) MintDepth.primary else MintDepth.secondary,
            if (!enabled) 0.dp else 3.dp).clip(shape), contentAlignment = Alignment.Center) {
            GardenRichText(text, color = if (primary) Color.White else Deep, fontSize = 12.sp, lineHeight = 15.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp))
        }
    }
}

@Composable
fun GardenLeafPlaque(text: String, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = 56.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w=size.width;val h=size.height
            val ribbon=Path().apply {
                moveTo(0f,h*.20f)
                quadraticTo(w*.50f,-h*.10f,w,h*.20f)
                lineTo(w,h*.90f)
                quadraticTo(w*.50f,h*1.04f,0f,h*.90f)
                close()
            }
            drawPath(ribbon,Brush.verticalGradient(listOf(Color(0xFFFFF4DC),Color(0xFFFFE6BC))))
            drawPath(ribbon,Color(0xFFD4AC77),style=Stroke(1.5.dp.toPx()))
        }
        GardenRichText(text, Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            color = Deep, fontSize = 23.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun GardenWoodSign(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 28.sp) {
    Box(modifier.heightIn(min = 52.dp), contentAlignment = Alignment.Center) {
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
            drawPath(board, Brush.verticalGradient(listOf(Color(0xFFC99050), Color(0xFF996034), Color(0xFFB77C42))))
            drawPath(board, Color(0xFFE2AE6C), style = Stroke(3.dp.toPx()))
            drawLine(Color(0xFF85542D).copy(alpha = .25f),
                androidx.compose.ui.geometry.Offset(w*.10f,h*.30f), androidx.compose.ui.geometry.Offset(w*.90f,h*.27f), 1.dp.toPx())
        }
        GardenRichText(text, color = Color(0xFFFFF2D1), fontWeight = FontWeight.Bold, fontSize = fontSize, lineHeight = fontSize * 1.15f,
            textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 22.dp, vertical = 13.dp))
    }
}

@Preview(widthDp = 220)
@Composable
fun GardenPaintedButtonPreview() { GardenPaintedButton("Gieo cây", {}) }
@Preview(widthDp = 160)
@Composable
fun GardenSecondaryButtonPreview() { GardenPaintedButton("Để sau", {}, primary = false) }
@Preview(widthDp = 300)
@Composable
fun GardenWoodSignPreview() { GardenWoodSign("Vườn ươm", Modifier.fillMaxWidth()) }
@Preview(widthDp = 300)
@Composable
fun GardenLeafPlaquePreview() { GardenLeafPlaque("Gieo một việc nhỏ") }

@Preview(name = "Ribbon · long name, large text", widthDp = 288, fontScale = 1.3f,
    device = "spec:width=288dp,height=180dp,dpi=160")
@Composable
fun GardenLeafPlaqueLongNamePreview() {
    com.mamxanh.app.presentation.ui.MamXanhPreviewTheme {
        GardenLeafPlaque("Cây đào bình yên bên cửa sổ của gia đình", Modifier.fillMaxWidth())
    }
}
