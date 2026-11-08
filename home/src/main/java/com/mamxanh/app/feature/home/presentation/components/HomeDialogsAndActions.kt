package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Muted

/**
 * Bottom action bar with circular kawaii buttons (Concept Image 1):
 * - Nước (Water)
 * - Phân bón (Fertilizer)
 * - Bạn nhỏ (Mascot Companion)
 */
@Composable
fun HomeBottomActionBar(
    isWateredToday: Boolean,
    waterAvailable: Boolean,
    fertilizerCount: Int,
    onWaterClick: () -> Unit,
    onFertilizeClick: () -> Unit,
    onCompanionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularActionButton(
            iconText = "💧",
            labelText = "Nước",
            badge = if (isWateredToday) "✓" else if (!waterAvailable) "!" else null,
            enabled = !isWateredToday && waterAvailable,
            onClick = onWaterClick
        )

        CircularActionButton(
            iconText = "🌾",
            labelText = "Phân bón",
            badge = if (fertilizerCount > 0) "$fertilizerCount" else null,
            onClick = onFertilizeClick
        )

        CircularActionButton(
            iconText = "🐹",
            labelText = "Bạn nhỏ",
            onClick = onCompanionClick
        )
    }
}

/**
 * Circular game action button with label and optional notification badge.
 */
@Composable
fun CircularActionButton(
    iconText: String,
    labelText: String,
    badge: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            Surface(
                onClick = onClick,
                enabled = enabled,
                color = Color.White,
                shape = CircleShape,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(3.5.dp, Color(0xFFE5F1E8)),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconText, fontSize = 28.sp)
                }
            }

            if (badge != null) {
                Surface(
                    color = Color(0xFFFFA01A),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            badge,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        Text(
            labelText,
            color = Color(0xFF435848),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

/**
 * Toast popup banner showing feedback when plant is watered.
 */
@Composable
fun WaterSplashFeedback(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF29B6F6))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("💦", fontSize = 24.sp)
                Column {
                    Text("Đã tưới nước mát lành!", fontWeight = FontWeight.Bold, color = Deep)
                    Text("+8 điểm lớn lên mỗi ngày", fontSize = 12.sp, color = Green)
                }
            }
        }
        LaunchedEffect(visible) {
            kotlinx.coroutines.delay(1600)
            onDismiss()
        }
    }
}

/**
 * Dialog interacting with the companion mascot.
 */
@Composable
fun MascotCompanionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🐹", fontSize = 28.sp)
                Text("Bé Mầm Nhỏ Nhắn", fontWeight = FontWeight.Bold, color = Deep)
            }
        },
        text = {
            Text(
                "Chào bạn! Hãy hoàn thành các thói quen tốt hôm nay để nhận thêm nước và phân bón chăm sóc mầm cây nhé! Cây rất vui khi được bạn chăm sóc mỗi ngày. 🌱",
                color = Deep,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text("Cảm ơn bạn nhỏ!")
            }
        }
    )
}

/**
 * Bottom task drawer modal for quick daily check-ins.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTasksBottomSheet(
    completedTasks: Int,
    totalTasks: Int,
    treeCount: Int,
    onDismiss: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenGarden: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF7F9F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Thói Quen Tích Cực", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Deep)
                    Text("$completedTasks/$totalTasks nhiệm vụ hoàn thành hôm nay", color = Muted, fontSize = 13.sp)
                }
                Button(
                    onClick = {
                        onDismiss()
                        onOpenTasks()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text("Xem tất cả  →")
                }
            }

            LinearProgressIndicator(
                progress = { if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF55C75A),
                trackColor = Color(0xFFE2E8DE)
            )

            OutlinedButton(
                onClick = {
                    onDismiss()
                    onOpenGarden()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🌿 Ghé thăm khu vườn ($treeCount cây)")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
