package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.components.GardenLandmark

@Composable
fun GardenTrailBar(selected: AppDestination, onNavigate: (AppDestination) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal=8.dp,vertical=4.dp),
        horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        AppDestination.entries.forEach { destination ->
            Column(Modifier.weight(1f).clickable { onNavigate(destination) },
                horizontalAlignment=Alignment.CenterHorizontally) {
                GardenLandmark(destination.name,Modifier.fillMaxWidth().height(42.dp))
                Box(Modifier.fillMaxWidth().height(28.dp),contentAlignment=Alignment.Center) {
                    Canvas(Modifier.matchParentSize()) {
                        val active=selected==destination
                        drawRoundRect(if(active) Color(0xFF648653) else Color(0xFFAC8056),
                            size=size,cornerRadius=androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()))
                        for(i in 0..2) {
                            val y=size.height*(.15f+i*.31f)
                            drawLine(Color(0xFFF4D6A1).copy(alpha=.22f),Offset(size.width*.07f,y),
                                Offset(size.width*.93f,y+size.height*.06f),1.dp.toPx(),StrokeCap.Round)
                        }
                        drawCircle(Color(0xFF60472F),1.5.dp.toPx(),Offset(4.dp.toPx(),size.height*.5f))
                        drawCircle(Color(0xFF60472F),1.5.dp.toPx(),Offset(size.width-4.dp.toPx(),size.height*.5f))
                    }
                    Text(when(destination) {
                        AppDestination.HOME -> "Sân vườn"
                        AppDestination.TASKS -> "Việc nhỏ"
                        AppDestination.GARDEN -> "Vườn ươm"
                        AppDestination.SHOP -> "Tiệm hạt"
                        AppDestination.STATS -> "Nhật ký"
                    },color=Color(0xFFFFF0CD),fontSize=10.sp,fontWeight=FontWeight.Bold,maxLines=1)
                }
            }
        }
    }
}

@Preview(name="Garden trail navigation",widthDp=360,showBackground=true)
@Composable
fun GardenTrailBarPreview() { MamXanhPreviewTheme { GardenTrailBar(AppDestination.HOME,{}) } }
