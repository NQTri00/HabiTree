package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.core.ui.R

@Composable
fun GardenPriceTag(price: Int, modifier: Modifier = Modifier) {
    GardenBotanicalFrame(modifier.heightIn(min = 30.dp).widthIn(min = 88.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
        cornerSize = 12.dp, skin = GardenPanelSkin.PRICE) {
        GardenRichText("$price xu", Modifier.align(Alignment.Center), color = com.mamxanh.app.presentation.ui.Deep,
            fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(name = "Source price label")
@Composable
fun GardenPriceTagPreview() { GardenPriceTag(25) }
