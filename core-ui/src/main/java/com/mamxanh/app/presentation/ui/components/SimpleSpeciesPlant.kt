package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

/** Recognizable legacy specimens in the simple mics illustration style. */
@Composable
fun SimpleSpeciesPlant(species: String, modifier: Modifier = Modifier, potted: Boolean = false) {
    Canvas(modifier) {
        val ratio=minOf(size.width,size.height)/100f
        translate((size.width-100*ratio)/2,(size.height-100*ratio)/2) {
            scale(ratio,ratio,Offset.Zero) {
                val green=Color(0xFF5E974D)
                val dark=Color(0xFF3F7545)
                val light=Color(0xFFA8C96C)
                fun stem(x:Float,y:Float) {
                    drawLine(dark,Offset(50f,86f),Offset(x,y),3f,androidx.compose.ui.graphics.StrokeCap.Round)
                }
                fun ovalLeaf(x:Float,y:Float,w:Float,h:Float,angle:Float=0f) {
                    rotate(angle,Offset(x+w/2,y+h/2)) {
                        drawOval(Brush.horizontalGradient(listOf(dark,green,light)),Offset(x,y),Size(w,h))
                    }
                }
                when(species) {
                    "monstera" -> {
                        listOf(Offset(28f,40f),Offset(67f,28f),Offset(70f,61f)).forEach { c ->
                            stem(c.x,c.y)
                            rotate(if(c.x<50)-25f else 25f,c) {
                                drawOval(Brush.horizontalGradient(listOf(dark,green,light)),Offset(c.x-19,c.y-22),Size(38f,43f))
                                repeat(3){i->
                                    drawLine(Color(0xFF85B373),Offset(c.x-17,c.y-12+i*11),Offset(c.x-4,c.y-5+i*9),4f,androidx.compose.ui.graphics.StrokeCap.Round)
                                    drawLine(Color(0xFF85B373),Offset(c.x+17,c.y-12+i*11),Offset(c.x+4,c.y-5+i*9),4f,androidx.compose.ui.graphics.StrokeCap.Round)
                                }
                                drawLine(dark,Offset(c.x,c.y-18),Offset(c.x,c.y+17),1.5f)
                            }
                        }
                    }
                    "ficus" -> {
                        drawLine(Color(0xFF9A7048),Offset(50f,87f),Offset(50f,20f),5f)
                        listOf(Offset(30f,61f),Offset(55f,45f),Offset(29f,29f),Offset(51f,10f)).forEachIndexed { i,c ->
                            stem(c.x+10,c.y+12)
                            ovalLeaf(c.x,c.y,25f,37f,if(i%2==0)-35f else 35f)
                        }
                    }
                    "snake", "aloe" -> {
                        repeat(if(species=="snake")5 else 7){i->
                            val angle=(i-(if(species=="snake")2 else 3)) * if(species=="snake")12f else 17f
                            rotate(angle,Offset(50f,86f)) {
                                val height=if(species=="snake") 72f-(i%2)*13 else 62f-(i%2)*8
                                val blade=Path().apply {
                                    moveTo(46f,86f);quadraticTo(36f,60f,50f,86f-height)
                                    quadraticTo(63f,61f,54f,86f);close()
                                }
                                drawPath(blade,if(species=="snake") Color(0xFFC8C56B) else dark)
                                drawPath(blade,Brush.horizontalGradient(listOf(dark,green,light)),style=Stroke(if(species=="snake")6f else 4f))
                                drawLine(light,Offset(50f,80f),Offset(50f,86f-height+12),1f)
                            }
                        }
                    }
                    "cactus" -> {
                        drawRoundRect(Brush.horizontalGradient(listOf(dark,green,light)),Offset(37f,24f),Size(28f,63f),CornerRadius(14f))
                        drawRoundRect(green,Offset(20f,44f),Size(19f,28f),CornerRadius(10f))
                        drawRoundRect(green,Offset(64f,36f),Size(18f,29f),CornerRadius(9f))
                        drawLine(light,Offset(47f,31f),Offset(47f,78f),2f)
                        drawLine(light,Offset(56f,32f),Offset(56f,78f),1.5f)
                        repeat(5){i->
                            val a=i*72.0*Math.PI/180
                            drawCircle(Color(0xFFF39BAA),7f,Offset(50f+kotlin.math.cos(a).toFloat()*7,22f+kotlin.math.sin(a).toFloat()*7))
                        }
                        drawCircle(Color(0xFFFFDA70),4f,Offset(50f,22f))
                    }
                    "lavender" -> {
                        repeat(7){i->
                            val x=22f+i*9
                            val y=16f+(i%3)*10
                            stem(x,y)
                            ovalLeaf(x-5,60f,10f,20f,if(i%2==0)-35f else 35f)
                            repeat(5){p->
                                drawOval(if(p%2==0)Color(0xFF9B7BBE) else Color(0xFFC0A1D6),
                                    Offset(x-5+(p%2)*2,y+p*5),Size(9f,8f))
                            }
                        }
                    }
                    "pilea" -> {
                        listOf(Offset(24f,56f),Offset(25f,27f),Offset(51f,17f),Offset(74f,32f),Offset(76f,61f),Offset(49f,50f)).forEachIndexed { i,c->
                            stem(c.x,c.y)
                            drawCircle(if(i%2==0)green else light,13f,c)
                            drawLine(dark,c,Offset(c.x,c.y+8),1f)
                        }
                    }
                    else -> {
                        stem(50f,25f)
                        ovalLeaf(25f,39f,24f,34f,-40f)
                        ovalLeaf(51f,22f,24f,34f,40f)
                    }
                }
                if(potted) {
                    val pot=Path().apply{moveTo(27f,79f);lineTo(73f,79f);lineTo(67f,98f);lineTo(33f,98f);close()}
                    drawPath(pot,Brush.horizontalGradient(listOf(Color(0xFFCDA675),Color(0xFFFFE6B6),Color(0xFFD4AE7D))))
                    drawRoundRect(Color(0xFFEED0A0),Offset(24f,77f),Size(52f,8f),CornerRadius(4f))
                } else {
                    drawOval(Color(0xFF87A647),Offset(13f,82f),Size(74f,17f))
                    drawOval(Color(0xFFB4C95F),Offset(22f,80f),Size(56f,10f))
                    drawOval(Color(0xFF969B84),Offset(12f,87f),Size(20f,11f))
                    drawOval(Color(0xFFADB09A),Offset(70f,88f),Size(18f,10f))
                }
            }
        }
    }
}
