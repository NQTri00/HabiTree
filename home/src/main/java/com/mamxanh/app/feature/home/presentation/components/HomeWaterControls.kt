package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePumpStation(
    wateringCanMl: Int,
    onPumpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canRefill = wateringCanMl < 1000
    Column(
        modifier = modifier.width(105.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WaterPumpIllustration(
            modifier = Modifier
                .size(80.dp, 90.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    enabled = canRefill,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onPumpClick
                )
                .semantics {
                    contentDescription = if (canRefill) "Bơm thêm 100 ml nước" else "Bình tưới đã đầy"
                }
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            color = Color(0xFFFFF8EC),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF94623C)),
            shadowElevation = 2.dp
        ) {
            Text(
                if (canRefill) "Bơm +100 ml" else "Bình đã đầy",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A3A1E),
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
fun HomeWaterCareControls(
    wateringCanMl: Int,
    canWater: Boolean,
    helperText: String,
    onWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier.size(72.dp).semantics {
                contentDescription = "Bình tưới: $wateringCanMl trên 1000 ml"
            },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { (wateringCanMl / 1000f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF1687B6),
                trackColor = Color.White.copy(alpha = 0.75f),
                strokeWidth = 4.dp
            )
            Surface(
                onClick = onWater,
                enabled = canWater,
                color = Color.White.copy(alpha = if (canWater) 0.98f else 0.7f),
                shape = CircleShape,
                shadowElevation = if (canWater) 4.dp else 1.dp,
                border = BorderStroke(2.dp, Color(0xFFB4DEF2)),
                modifier = Modifier.size(58.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    WateringCanIllustration(modifier = Modifier.size(44.dp))
                }
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color(0xFFB4DEF2))
        ) {
            Text(
                "$wateringCanMl / 1000 ml",
                color = Color(0xFF28495B),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Text(
            helperText,
            color = if (canWater) Color(0xFF155E7D) else Color(0xFF4C6655),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}
