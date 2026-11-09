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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

private val LeafGreen = Color(0xFF87A93A)
private val DarkLeaf = Color(0xFF517A31)
private val Honey = Color(0xFFD99A4F)
private val InkBrown = Color(0xFF8C6038)
private val Paper = Color(0xFFFFE9B6)

private fun DrawScope.leaf(x: Float, y: Float, width: Float, height: Float, color: Color = LeafGreen, angle: Float = 0f) {
    rotate(angle, Offset(x, y)) {
        val p = Path().apply {
            moveTo(x,y)
            cubicTo(x-width*.6f,y-height*.25f,x-width*.75f,y-height,x,y-height)
            cubicTo(x+width*.75f,y-height,x+width*.6f,y-height*.25f,x,y)
        }
        drawPath(p, Brush.verticalGradient(listOf(Color(0xFFBED45B), color), y-height,y))
    }
}

@Composable
fun SimpleGardenArt(art: GardenArt, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val factor = minOf(size.width,size.height)/100f
        translate((size.width-100*factor)/2,(size.height-100*factor)/2) {
            scale(factor, factor, Offset.Zero) {
                when (art) {
                    GardenArt.CHECKED_TASK -> {
                        drawRoundRect(Paper, Offset(14f,25f), Size(66f,62f), CornerRadius(6f))
                        drawRoundRect(DarkLeaf, Offset(14f,25f), Size(66f,62f), CornerRadius(6f), style=Stroke(4f))
                        drawLine(DarkLeaf, Offset(24f,51f), Offset(43f,69f), 7f, StrokeCap.Round)
                        drawLine(DarkLeaf, Offset(43f,69f), Offset(88f,15f), 7f, StrokeCap.Round)
                    }
                    GardenArt.SPROUT -> {
                        drawLine(DarkLeaf,Offset(50f,88f),Offset(49f,47f),5f,StrokeCap.Round)
                        leaf(48f,57f,39f,45f,angle=-42f)
                        leaf(51f,55f,42f,52f,angle=43f)
                    }
                    GardenArt.COIN -> {
                        drawCircle(Color(0xFFD99928),40f,Offset(50f,53f))
                        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFEC8E),Color(0xFFF7BE35)),Offset(41f,40f),54f),36f,Offset(50f,48f))
                        drawCircle(Color(0xFFFFF0A0),29f,Offset(50f,48f),style=Stroke(2f))
                        drawOval(Color(0xFFFFE286),Offset(34f,22f),Size(15f,25f))
                    }
                    GardenArt.DROP -> {
                        val drop=Path().apply {
                            moveTo(50f,7f)
                            cubicTo(43f,28f,20f,47f,20f,65f)
                            cubicTo(20f,102f,80f,102f,80f,65f)
                            cubicTo(80f,47f,57f,28f,50f,7f)
                            close()
                        }
                        drawPath(drop,Brush.horizontalGradient(listOf(Color(0xFF55B8D3),Color(0xFF9AE5EC),Color(0xFF40A2CA))))
                        drawPath(drop,Color(0xFF398CA9),style=Stroke(2f))
                        drawOval(Color.White.copy(alpha=.65f),Offset(29f,48f),Size(12f,25f))
                    }
                    GardenArt.NUT -> {
                        rotate(32f,Offset(43f,53f)) { drawOval(Brush.horizontalGradient(listOf(Color(0xFFAF642B),Color(0xFFE6A44D))),Offset(13f,25f),Size(47f,58f)) }
                        rotate(42f,Offset(67f,62f)) { drawOval(Brush.horizontalGradient(listOf(Color(0xFFEAA74F),Color(0xFFFFD07A))),Offset(47f,42f),Size(33f,44f)) }
                    }
                    GardenArt.GLASS -> {
                        val glass=Path().apply {moveTo(22f,14f);lineTo(78f,14f);lineTo(69f,87f);quadraticTo(50f,96f,31f,87f);close()}
                        drawPath(glass,Color(0xFFD9F0E9))
                        val water=Path().apply {moveTo(28f,45f);quadraticTo(50f,52f,72f,45f);lineTo(67f,83f);quadraticTo(50f,90f,33f,83f);close()}
                        drawPath(water,Brush.verticalGradient(listOf(Color(0xFF82CED4),Color(0xFF409BA9)),40f,90f))
                        drawPath(glass,Color(0xFF5796A0),style=Stroke(2.5f))
                        drawOval(Color(0xFFEEF9EF),Offset(22f,7f),Size(56f,15f))
                        drawOval(Color(0xFF5796A0),Offset(22f,7f),Size(56f,15f),style=Stroke(2f))
                        drawLine(Color.White.copy(alpha=.6f),Offset(35f,34f),Offset(39f,78f),4f,StrokeCap.Round)
                    }
                    GardenArt.BOOK, GardenArt.JOURNAL, GardenArt.CATEGORY -> {
                        if (art == GardenArt.CATEGORY) {
                            repeat(3) { i ->
                                drawRoundRect(if(i%2==0)DarkLeaf else Honey,Offset(12f,30f+i*19),Size(76f,18f),CornerRadius(4f))
                                drawRoundRect(Paper,Offset(21f,32f+i*19),Size(60f,11f),CornerRadius(2f))
                            }
                        } else {
                            val book=Path().apply {moveTo(10f,22f);quadraticTo(30f,10f,50f,23f);quadraticTo(70f,10f,90f,22f);lineTo(85f,84f);quadraticTo(65f,75f,50f,87f);quadraticTo(30f,75f,15f,84f);close()}
                            drawPath(book,Color(0xFFC67E4F),style=Stroke(7f))
                            drawPath(book,Paper)
                            drawLine(Honey,Offset(50f,25f),Offset(50f,82f),2f)
                            repeat(4) { i ->
                                drawLine(Color(0xFFCEAE7B),Offset(21f,32f+i*10),Offset(41f,35f+i*10),1.5f)
                                drawLine(Color(0xFFCEAE7B),Offset(59f,35f+i*10),Offset(79f,32f+i*10),1.5f)
                            }
                        }
                    }
                    GardenArt.CHECKLIST -> {
                        rotate(8f,Offset(50f,50f)) {
                            drawRoundRect(Color(0xFFC89D5A),Offset(21f,12f),Size(62f,80f),CornerRadius(8f))
                            drawRoundRect(Paper,Offset(17f,7f),Size(62f,80f),CornerRadius(8f))
                            repeat(3){i->
                                val y=27f+i*20
                                drawLine(DarkLeaf,Offset(27f,y),Offset(31f,y+4),3f,StrokeCap.Round)
                                drawLine(DarkLeaf,Offset(31f,y+4),Offset(38f,y-5),3f,StrokeCap.Round)
                                drawLine(InkBrown,Offset(46f,y),Offset(67f,y),2f,StrokeCap.Round)
                            }
                        }
                    }
                    GardenArt.CALENDAR -> {
                        drawRoundRect(Paper,Offset(13f,22f),Size(74f,67f),CornerRadius(8f))
                        drawRoundRect(DarkLeaf,Offset(13f,22f),Size(74f,67f),CornerRadius(8f),style=Stroke(4f))
                        drawLine(DarkLeaf,Offset(15f,40f),Offset(85f,40f),4f)
                        drawLine(DarkLeaf,Offset(30f,13f),Offset(30f,30f),6f,StrokeCap.Round)
                        drawLine(DarkLeaf,Offset(70f,13f),Offset(70f,30f),6f,StrokeCap.Round)
                        repeat(2){r->repeat(3){c->drawRoundRect(LeafGreen,Offset(27f+c*19,51f+r*18),Size(8f,8f),CornerRadius(1f))}}
                    }
                    GardenArt.PENCIL -> {
                        rotate(40f,Offset(50f,50f)) {
                            drawRoundRect(Color(0xFF96B943),Offset(38f,12f),Size(24f,62f),CornerRadius(4f))
                            drawRect(Color(0xFFD8E884),Offset(41f,14f),Size(6f,57f))
                            drawPath(Path().apply {moveTo(38f,74f);lineTo(50f,94f);lineTo(62f,74f);close()},Color(0xFFE0B17B))
                            drawPath(Path().apply {moveTo(46f,87f);lineTo(50f,94f);lineTo(54f,87f);close()},InkBrown)
                        }
                    }
                    GardenArt.FERTILIZER, GardenArt.SEED -> {
                        if(art == GardenArt.SEED){
                            drawOval(Color(0xFF966342),Offset(8f,73f),Size(84f,18f))
                            rotate(30f,Offset(50f,67f)){drawOval(Brush.horizontalGradient(listOf(Color(0xFFEBA24E),Color(0xFFFFCE7D))),Offset(29f,42f),Size(42f,42f))}
                            drawLine(DarkLeaf,Offset(51f,49f),Offset(51f,25f),3f)
                            leaf(50f,34f,24f,28f,angle=-43f);leaf(52f,33f,25f,29f,angle=40f)
                        } else {
                            val sack=Path().apply{moveTo(33f,25f);quadraticTo(14f,49f,15f,78f);quadraticTo(50f,99f,85f,78f);quadraticTo(86f,49f,67f,25f);lineTo(75f,10f);quadraticTo(50f,17f,25f,10f);close()}
                            drawPath(sack,Brush.horizontalGradient(listOf(Color(0xFFE8B365),Color(0xFFFFE9B7),Color(0xFFE9B96E))))
                            drawPath(sack,Color(0xFFD69D58),style=Stroke(2f))
                            drawLine(InkBrown,Offset(34f,28f),Offset(68f,28f),4f,StrokeCap.Round)
                            drawLine(DarkLeaf,Offset(50f,77f),Offset(50f,55f),3f)
                            leaf(49f,65f,20f,26f,angle=-45f);leaf(51f,65f,20f,26f,angle=45f)
                        }
                    }
                    GardenArt.PUMP -> {
                        drawRoundRect(Color(0xFFF5B34F),Offset(28f,39f),Size(48f,52f),CornerRadius(14f))
                        drawRoundRect(Color(0xFFFFD37F),Offset(34f,45f),Size(14f,34f),CornerRadius(7f))
                        drawRect(Honey,Offset(45f,22f),Size(12f,22f))
                        drawRoundRect(Color(0xFFED9B36),Offset(32f,14f),Size(43f,10f),CornerRadius(5f))
                    }
                    GardenArt.TREE, GardenArt.NATURAL -> {
                        if(art==GardenArt.NATURAL){
                            drawOval(DarkLeaf,Offset(8f,69f),Size(84f,21f));drawOval(Color(0xFF9C9C78),Offset(27f,43f),Size(49f,36f))
                        } else {
                            drawPath(Path().apply{moveTo(41f,91f);lineTo(46f,49f);lineTo(56f,49f);lineTo(61f,91f);lineTo(50f,85f);close()},InkBrown)
                            drawCircle(LeafGreen,27f,Offset(50f,34f));drawCircle(Color(0xFF90AC45),23f,Offset(27f,53f));drawCircle(Color(0xFF7F9E37),23f,Offset(73f,53f));drawCircle(Color(0xFF9BB84F),24f,Offset(50f,54f))
                        }
                    }
                    GardenArt.SHOP -> {
                        drawRoundRect(Honey,Offset(20f,42f),Size(60f,46f),CornerRadius(5f))
                        drawRect(Paper,Offset(28f,47f),Size(22f,22f))
                        drawRect(Color(0xFF7A9B54),Offset(55f,55f),Size(15f,32f))
                        val canopy=Path().apply{moveTo(17f,20f);lineTo(83f,20f);lineTo(96f,47f);lineTo(4f,47f);close()}
                        drawPath(canopy,Color(0xFFEE8A73))
                        drawPath(Path().apply{moveTo(32f,20f);lineTo(45f,20f);lineTo(43f,47f);lineTo(26f,47f);close()},Paper)
                        drawPath(Path().apply{moveTo(59f,20f);lineTo(72f,20f);lineTo(78f,47f);lineTo(60f,47f);close()},Paper)
                    }
                    GardenArt.FLOWERS -> {
                        repeat(3){i->
                            val x=30f+i*22;val y=50f-(i%2)*20
                            drawLine(DarkLeaf,Offset(x,88f),Offset(x,y),3f)
                            repeat(5){p->val a=p*72.0*Math.PI/180;drawCircle(Color(0xFFFFA7AD),12f,Offset(x+kotlin.math.cos(a).toFloat()*12,y+kotlin.math.sin(a).toFloat()*12))}
                            drawCircle(Color(0xFFFFD56D),6f,Offset(x,y))
                        }
                    }
                    GardenArt.LANTERN -> {
                        drawLine(InkBrown,Offset(50f,0f),Offset(50f,24f),4f)
                        drawRoundRect(Honey,Offset(23f,23f),Size(54f,62f),CornerRadius(17f))
                        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFFFEE8A),Color(0xFFFBC44D))),Offset(31f,32f),Size(38f,43f),CornerRadius(12f))
                        drawRoundRect(InkBrown,Offset(20f,23f),Size(60f,7f),CornerRadius(4f))
                        drawRoundRect(InkBrown,Offset(27f,80f),Size(46f,7f),CornerRadius(4f))
                    }
                    GardenArt.RIBBON -> {
                        rotate(-24f,Offset(50f,46f)){drawOval(Color(0xFFF58E9D),Offset(7f,23f),Size(43f,34f))}
                        rotate(24f,Offset(50f,46f)){drawOval(Color(0xFFF58E9D),Offset(50f,23f),Size(43f,34f))}
                        drawPath(Path().apply{moveTo(42f,50f);lineTo(27f,89f);lineTo(45f,82f);lineTo(50f,57f);lineTo(55f,82f);lineTo(73f,89f);lineTo(58f,50f);close()},Color(0xFFEB8293))
                        drawRoundRect(Color(0xFFFFB3BB),Offset(40f,33f),Size(20f,24f),CornerRadius(6f))
                    }
                    GardenArt.BOOTS -> {
                        repeat(2){i->translate(i*32f, -i*8f){
                            val boot=Path().apply{moveTo(25f,24f);lineTo(55f,24f);lineTo(54f,59f);quadraticTo(71f,70f,66f,84f);lineTo(17f,84f);quadraticTo(9f,66f,27f,59f);close()}
                            drawPath(boot,Brush.verticalGradient(listOf(Color(0xFFDEAA65),Color(0xFFC4823E))))
                            drawLine(InkBrown,Offset(18f,82f),Offset(65f,82f),6f,StrokeCap.Round)
                            repeat(3){r->drawLine(Paper,Offset(31f,37f+r*9),Offset(47f,37f+r*9),3f,StrokeCap.Round)}
                        }}
                    }
                    GardenArt.BROOM -> {
                        rotate(28f, Offset(50f, 50f)) {
                            drawLine(InkBrown, Offset(50f, 10f), Offset(50f, 62f), 9f, StrokeCap.Round)
                            drawLine(Honey, Offset(48f, 12f), Offset(48f, 58f), 3f, StrokeCap.Round)
                            val bristles = Path().apply {
                                moveTo(37f, 57f); lineTo(63f, 57f)
                                lineTo(79f, 91f); quadraticTo(50f, 99f, 21f, 91f); close()
                            }
                            drawPath(bristles, Brush.verticalGradient(listOf(Color(0xFFE6BC72), Honey), 57f, 96f))
                            drawPath(bristles, InkBrown, style = Stroke(2f))
                            repeat(7) { index ->
                                drawLine(Color(0xFFB17B3F), Offset(39f + index * 3.7f, 63f),
                                    Offset(28f + index * 7.3f, 90f), 1.3f)
                            }
                            drawLine(DarkLeaf, Offset(35f, 61f), Offset(65f, 61f), 5f, StrokeCap.Round)
                        }
                    }
                    GardenArt.WATERING_CAN -> Unit
                }
            }
        }
    }
}
