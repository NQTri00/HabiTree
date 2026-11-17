package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.shared.model.gardenCosmetics
import androidx.compose.material3.Icon
import androidx.compose.material.icons.outlined.*
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
    val vector=when(item.category) {
        "GROUND"->androidx.compose.material.icons.Icons.Outlined.Spa
        "BORDER"->androidx.compose.material.icons.Icons.Outlined.GridView
        "ACCENT"->androidx.compose.material.icons.Icons.Outlined.LightMode
        "THEME"->androidx.compose.material.icons.Icons.Outlined.NightsStay
        else->androidx.compose.material.icons.Icons.Outlined.AutoAwesome
    }
    Icon(vector,null,modifier,tint=com.mamxanh.app.presentation.ui.Deep)

}

@Preview(name="Small decoration artwork",widthDp=160,heightDp=40,device="spec:width=160dp,height=40dp,dpi=160")
@Composable fun CompactCosmeticIconsPreview() {
    Row(Modifier.padding(6.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        listOf("border_wood","reward_path","reward_sign","theme_night").forEach{id->
            CosmeticArtwork(gardenCosmetics.first{it.id==id},Modifier.size(24.dp))
        }
    }
}
