package com.mamxanh.app.feature.shop.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.shop.domain.model.ShopSeed
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green

/**
 * A cute plant card in the "Shop Tiny Plants" grid (Concept Image 2).
 */
@Composable
fun TinyPlantCard(
    plant: ShopSeed,
    varietyIndex: Int,
    ownedCount: Int,
    canAfford: Boolean,
    onBuy: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    GardenBotanicalFrame(modifier.fillMaxWidth().clickable(onClick = onInspect)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KawaiiPottedPlant(
                varietyIndex = varietyIndex,
                potExpression = varietyIndex % 3,
                modifier = Modifier.size(68.dp, 76.dp)
            )

            Text(
                plant.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Deep,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Golden Price Tag Pill: "⭐ 25" (Concept Image 2)
            Surface(
                onClick = onBuy,
                enabled = canAfford,
                color = if (canAfford) Color(0xFFFFAE34) else Color(0xFFD6CBC0),
                shape = RoundedCornerShape(50),
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⭐", fontSize = 11.sp)
                    Text(
                        "${plant.price}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            if (ownedCount > 0) {
                Text(
                    "Đã có: $ownedCount",
                    fontSize = 10.sp,
                    color = Green,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(name = "Nursery seed card", showBackground = true)
@Composable
fun TinyPlantCardPreview() {
    com.mamxanh.app.presentation.ui.MamXanhPreviewTheme {
        TinyPlantCard(
            plant = ShopSeed("cherry", "Đào hồng xuân", "🌸", "Cánh hoa đào rực rỡ ngày xuân", 25),
            varietyIndex = 8,
            ownedCount = 2,
            canAfford = true,
            onBuy = {},
            onInspect = {},
            modifier = Modifier.width(160.dp)
        )
    }
}
