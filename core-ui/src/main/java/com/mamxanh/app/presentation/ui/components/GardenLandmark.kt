package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Small painted destinations used on the garden map and its trail signs. */
@Composable
fun GardenLandmark(kind: String, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val bark = Color(0xFF795437)
        fun line(x: Float, y: Float, xx: Float, yy: Float, color: Color, thickness: Float) =
            drawLine(color, Offset(w*x,h*y), Offset(w*xx,h*yy), w*thickness, StrokeCap.Round)
        drawOval(Color(0xFF416A45).copy(alpha=.17f), Offset(w*.06f,h*.81f), Size(w*.88f,h*.15f))
        if (kind == "SHOP") {
            drawRoundRect(Color(0xFFCB9B67), Offset(w*.19f,h*.40f), Size(w*.64f,h*.44f), androidx.compose.ui.geometry.CornerRadius(w*.025f))
            for (i in 0..5) line(.22f+i*.10f,.43f,.22f+i*.10f,.83f,Color(0xFFAC7D50),.012f)
            line(.18f,.27f,.18f,.86f,bark,.035f); line(.83f,.27f,.83f,.86f,bark,.035f)
            val roof = Path().apply { moveTo(w*.08f,h*.39f); lineTo(w*.22f,h*.19f); lineTo(w*.80f,h*.19f); lineTo(w*.95f,h*.39f); close() }
            drawPath(roof,Color(0xFFF6D6B9))
            for(i in 0..5) {
                val x=.11f+i*.137f
                drawRoundRect(if(i%2==0) Color(0xFFD9858D) else Color(0xFFFFE6C8),Offset(w*x,h*.33f),Size(w*.137f,h*.12f),androidx.compose.ui.geometry.CornerRadius(w*.035f))
            }
            drawRect(Color(0xFF594934),Offset(w*.28f,h*.47f),Size(w*.45f,h*.17f))
            for(i in 0..2) {
                val x=w*(.34f+i*.16f)
                drawCircle(Color(0xFF7B9F64),w*.06f,Offset(x,h*.51f))
                drawRoundRect(Color(0xFFE9BC9A),Offset(x-w*.035f,h*.56f),Size(w*.07f,h*.07f),androidx.compose.ui.geometry.CornerRadius(w*.01f))
            }
            line(.16f,.68f,.87f,.68f,Color(0xFFECC18A),.055f)
        } else if(kind == "GARDEN") {
            for(i in 0..2) {
                val x=.21f+i*.27f
                drawOval(Color(0xFFAB7954),Offset(w*(x-.10f),h*.66f),Size(w*.23f,h*.13f))
                line(x,.70f,x,.37f,Color(0xFF538754),.025f)
                drawOval(Color(0xFF8DB96D),Offset(w*(x-.14f),h*.39f),Size(w*.15f,h*.13f))
                drawOval(Color(0xFF659D5D),Offset(w*x,h*.31f),Size(w*.17f,h*.13f))
            }
            line(.12f,.81f,.91f,.81f,Color(0xFFDDC49A),.025f)
        } else if(kind == "HOME") {
            line(.50f,.85f,.50f,.37f,bark,.055f)
            listOf(Offset(.35f,.40f),Offset(.61f,.39f),Offset(.49f,.24f)).forEach {
                drawCircle(Color(0xFF709B60),w*.19f,Offset(w*it.x,h*it.y))
                drawCircle(Color(0xFFA7C481),w*.11f,Offset(w*(it.x-.03f),h*(it.y-.03f)))
            }
        } else {
            line(.26f,.29f,.26f,.90f,bark,.035f); line(.76f,.29f,.76f,.90f,bark,.035f)
            drawRoundRect(Color(0xFFB68B5D),Offset(w*.13f,h*.20f),Size(w*.76f,h*.52f),androidx.compose.ui.geometry.CornerRadius(w*.035f))
            drawRoundRect(Color(0xFFFFECCA),Offset(w*.20f,h*.27f),Size(w*.62f,h*.37f),androidx.compose.ui.geometry.CornerRadius(w*.015f))
            if(kind=="STATS") for(i in 0..3) {
                drawRoundRect(Color(0xFF6F9864),Offset(w*(.27f+i*.13f),h*(.52f-i*.055f)),Size(w*.075f,h*(.08f+i*.055f)),androidx.compose.ui.geometry.CornerRadius(w*.02f))
            } else for(i in 0..2) {
                line(.29f,.36f+i*.10f,.34f,.39f+i*.10f,Color(0xFF638D5A),.015f)
                line(.34f,.39f+i*.10f,.39f,.33f+i*.10f,Color(0xFF638D5A),.015f)
                line(.46f,.36f+i*.10f,.71f,.36f+i*.10f,Color(0xFFB49771),.012f)
            }
            line(.09f,.20f,.52f,.07f,Color(0xFF6B8B54),.05f)
            line(.52f,.07f,.94f,.20f,Color(0xFF6B8B54),.05f)
        }
    }
}

@Preview(name="Painted garden places", widthDp=360,heightDp=160,showBackground=true)
@Composable
fun GardenLandmarkPreview() {
    Row(Modifier.fillMaxSize()) {
        listOf("HOME","SHOP","TASKS","GARDEN","STATS").forEach {
            GardenLandmark(it,Modifier.weight(1f).height(140.dp))
        }
    }
}
