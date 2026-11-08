package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep

/**
 * Top bar for HomeScreen featuring:
 * - Left: Orange circular nutrient badge ("Dinh dưỡng")
 * - Right: Daily tasks completion pill & gold coin pill
 */
@Composable
fun HomeTopBar(
    nutrientPoints: Int,
    completedTasks: Int,
    totalTasks: Int,
    coins: Int,
    onTasksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Circular Orange Badge: "Dinh dưỡng" / Level Points
        NutrientCircleBadge(points = nutrientPoints)

        // Right: Tasks Shortcut & Gold Coins
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onTasksClick,
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(50),
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDCECE2))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📋", fontSize = 14.sp)
                    Text(
                        "$completedTasks/$totalTasks",
                        fontWeight = FontWeight.Bold,
                        color = Deep,
                        fontSize = 13.sp
                    )
                }
            }

            Surface(
                color = Color(0xFFFFE6A0),
                shape = RoundedCornerShape(50),
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5B548))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🪙", fontSize = 14.sp)
                    Text(
                        "$coins",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5C3B0E),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Orange circular nutrient badge on top-left (Concept Image 1).
 */
@Composable
fun NutrientCircleBadge(points: Int, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFFFA01A),
        shape = CircleShape,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(3.5.dp, Color.White),
        modifier = modifier.size(68.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Dinh dưỡng",
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "$points",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
