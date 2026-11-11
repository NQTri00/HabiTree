package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*

@Composable
fun MintStatusPill(label: String, modifier: Modifier = Modifier) {
    Surface(modifier,shape=RoundedCornerShape(50),color=Color(0xFFE4F2EA),contentColor=Green) {
        Text(label,Modifier.padding(horizontal=6.dp,vertical=3.dp),fontSize=10.sp,lineHeight=13.sp,fontWeight=FontWeight.Medium)
    }
}

@Composable
fun MintTreeStatus(stage: String,state: String,water: String) {
    FlowRow(horizontalArrangement=Arrangement.spacedBy(5.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
        MintStatusPill(stage); MintStatusPill(state); MintStatusPill(water)
    }
}

@Composable
fun MintMetricStrip(metrics: List<Pair<String,String>>, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
        metrics.forEach { (label,value) ->
            Surface(Modifier.weight(1f).fillMaxHeight(),shape=RoundedCornerShape(10.dp),color=Color(0xFFF0F7F2)) {
                Column(Modifier.padding(horizontal=6.dp,vertical=4.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(value,color=Deep,fontSize=12.sp,lineHeight=15.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                    Text(label,color=Muted,fontSize=10.sp,lineHeight=13.sp,textAlign=TextAlign.Center)
                }
            }
        }
    }
}

/** Fixed artwork and label slots keep the entire grid aligned at every font scale. */
@Composable
fun MintCatalogCard(name: String,value: String,onClick: (() -> Unit)?=null,artwork: @Composable (Modifier)->Unit) {
    val nameHeight=with(LocalDensity.current){30.sp.toDp()}
    GardenBotanicalFrame(Modifier.fillMaxWidth().then(if(onClick!=null)
        Modifier.clickable(role=Role.Button,onClickLabel="Chọn $name",onClick=onClick) else Modifier),
        contentPadding=PaddingValues(5.dp),cornerSize=13.dp) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
            artwork(Modifier.fillMaxWidth().height(28.dp))
            Box(Modifier.fillMaxWidth().height(nameHeight),contentAlignment=Alignment.BottomCenter) {
                Text(name,color=Deep,fontSize=12.sp,lineHeight=15.sp,fontWeight=FontWeight.Bold,
                    textAlign=TextAlign.Center,maxLines=2,overflow=TextOverflow.Ellipsis)
            }
            Text(value,color=Green,fontSize=12.sp,lineHeight=15.sp,fontWeight=FontWeight.Bold,maxLines=1,
                overflow=TextOverflow.Ellipsis,textAlign=TextAlign.Center)
        }
    }
}
