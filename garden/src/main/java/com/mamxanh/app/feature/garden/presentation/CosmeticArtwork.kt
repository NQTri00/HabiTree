package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.shared.model.gardenCosmetics
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.GardenCosmetic

/** Existing resource art plus resolution-independent native ground/border/stone shapes. */
@Composable internal fun CosmeticArtwork(item:GardenCosmetic,modifier:Modifier=Modifier) {
    when {
        item.art=="RIBBON"->GardenArtwork(GardenArt.RIBBON,modifier)
        item.art=="FLOWERS"->GardenArtwork(GardenArt.FLOWERS,modifier)
        item.art=="LANTERNS"->GardenArtwork(GardenArt.LANTERN,modifier)
        item.id=="reward_sign" || item.id=="reward_plaque"->Canvas(modifier) {
            drawLine(Color(0xFF806544),Offset(size.width*.5f,size.height*.45f),Offset(size.width*.5f,size.height*.90f),size.width*.08f)
            drawRoundRect(Color(0xFFBDA87D),Offset(size.width*.12f,size.height*.20f),Size(size.width*.76f,size.height*.40f),androidx.compose.ui.geometry.CornerRadius(size.width*.08f))
            drawLine(Color(0xFF687D56),Offset(size.width*.28f,size.height*.40f),Offset(size.width*.72f,size.height*.40f),size.width*.04f)
        }
        item.category=="GROUND"->Canvas(modifier) {
            drawOval(Brush.linearGradient(listOf(Color(0xFF91BF70),Color(0xFF4C8956))),Offset(size.width*.08f,size.height*.30f),Size(size.width*.84f,size.height*.52f))
            repeat(7){i->val x=size.width*(.20f+i*.10f);drawLine(Color(0xFFB8D98D),Offset(x,size.height*.65f),Offset(x-size.width*.04f,size.height*.38f),size.width*.03f)}
        }
        item.id=="reward_path"->Canvas(modifier) {
            repeat(4){i->
                val x=size.width*(if(i%2==0).18f else .48f)
                drawOval(if(i%2==0)Color(0xFF85998A) else Color(0xFFACB9A7),Offset(x,size.height*(.12f+i*.18f)),Size(size.width*.34f,size.height*.16f))
            }
        }
        item.category=="BORDER"->Canvas(modifier) {
            val brown=Color(0xFF947451)
            repeat(4){i->val x=size.width*(.16f+i*.20f);drawLine(brown,Offset(x,size.height*.25f),Offset(x,size.height*.80f),size.width*.11f)}
            drawLine(brown,Offset(size.width*.12f,size.height*.45f),Offset(size.width*.85f,size.height*.45f),size.width*.08f)
        }
        item.id=="accent_stone"->Canvas(modifier) {
            drawOval(Color(0xFF697F78),Offset(size.width*.12f,size.height*.48f),Size(size.width*.78f,size.height*.32f))
            drawOval(Color(0xFFACBAB0),Offset(size.width*.20f,size.height*.25f),Size(size.width*.60f,size.height*.42f))
        }
        item.id=="reward_petals"->GardenArtwork(GardenArt.FLOWERS,modifier)
        item.id=="ambient_lights"->Icon(Icons.Rounded.AutoAwesome,null,modifier,tint=Color(0xFFB49A48))
        item.id=="theme_stone"->Icon(Icons.Rounded.GridView,null,modifier,tint=Color(0xFF819287))
        item.id=="theme_night"->Icon(Icons.Rounded.NightsStay,null,modifier,tint=Color(0xFF608B77))
        else->Icon(Icons.Rounded.Spa,null,modifier,tint=Color(0xFF608B77))
    }
}

@Preview(name="Small decoration artwork",widthDp=160,heightDp=40,device="spec:width=160dp,height=40dp,dpi=160")
@Composable fun CompactCosmeticIconsPreview() {
    Row(Modifier.padding(6.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        listOf("border_wood","reward_path","reward_sign","theme_night").forEach{id->
            CosmeticArtwork(gardenCosmetics.first{it.id==id},Modifier.size(24.dp))
        }
    }
}
