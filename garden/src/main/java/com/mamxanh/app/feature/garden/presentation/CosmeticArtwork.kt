package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.GardenCosmetic

/** Existing resource art plus resolution-independent native ground/border/stone shapes. */
@Composable internal fun CosmeticArtwork(item:GardenCosmetic,modifier:Modifier=Modifier) {
    when {
        item.art=="RIBBON"->GardenArtwork(GardenArt.RIBBON,modifier)
        item.art=="FLOWERS"->GardenArtwork(GardenArt.FLOWERS,modifier)
        item.art=="LANTERNS"->GardenArtwork(GardenArt.LANTERN,modifier)
        item.id=="reward_sign" || item.id=="reward_plaque"->Canvas(modifier) {
            drawLine(Color(0xFF806544),Offset(size.width*.5f,size.height*.45f),Offset(size.width*.5f,size.height*.90f),5.dp.toPx())
            drawRoundRect(Color(0xFFBDA87D),Offset(size.width*.12f,size.height*.20f),Size(size.width*.76f,size.height*.40f),androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()))
            drawLine(Color(0xFF687D56),Offset(size.width*.28f,size.height*.40f),Offset(size.width*.72f,size.height*.40f),2.dp.toPx())
        }
        item.category=="GROUND"->Canvas(modifier) {
            drawOval(Brush.linearGradient(listOf(Color(0xFF91BF70),Color(0xFF4C8956))),Offset(size.width*.08f,size.height*.30f),Size(size.width*.84f,size.height*.52f))
            repeat(7){i->val x=size.width*(.20f+i*.10f);drawLine(Color(0xFFB8D98D),Offset(x,size.height*.65f),Offset(x-size.width*.04f,size.height*.38f),2.dp.toPx())}
        }
        item.category=="BORDER"->Canvas(modifier) {
            val brown=Color(0xFF947451)
            repeat(4){i->val x=size.width*(.16f+i*.20f);drawLine(brown,Offset(x,size.height*.25f),Offset(x,size.height*.80f),6.dp.toPx())}
            drawLine(brown,Offset(size.width*.12f,size.height*.45f),Offset(size.width*.85f,size.height*.45f),5.dp.toPx())
        }
        item.id=="accent_stone"->Canvas(modifier) {
            drawOval(Color(0xFF697F78),Offset(size.width*.12f,size.height*.48f),Size(size.width*.78f,size.height*.32f))
            drawOval(Color(0xFFACBAB0),Offset(size.width*.20f,size.height*.25f),Size(size.width*.60f,size.height*.42f))
        }
        else->Box(modifier,contentAlignment=Alignment.Center){Text(item.icon,fontSize=28.sp)}
    }
}
