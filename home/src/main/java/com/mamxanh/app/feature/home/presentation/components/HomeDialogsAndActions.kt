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
fun MascotCompanionDialog(onDismiss: () -> Unit) {
    if (androidx.compose.ui.platform.LocalInspectionMode.current) {
        MascotCompanionContent(onDismiss)
    } else {
        androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
            MascotCompanionContent(onDismiss)
        }
    }
}

@Composable
fun MascotCompanionContent(onDismiss: () -> Unit) {
    com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Bé Mầm Nhỏ Nhắn", color = Deep, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge)
            com.mamxanh.app.presentation.ui.components.GardenLandmark(
                "HOME", Modifier.align(Alignment.CenterHorizontally).size(100.dp)
            )
            Text(
                "Chào bạn! Hãy hoàn thành các thói quen tốt hôm nay để nhận thêm nước và phân bón chăm sóc mầm cây nhé! Cây rất vui khi được bạn chăm sóc mỗi ngày.",
                color = Deep, style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Cảm ơn bạn nhỏ!")
            }
        }
    }
}

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
    if (androidx.compose.ui.platform.LocalInspectionMode.current) {
        HomeTasksSheetContent(completedTasks,totalTasks,treeCount,onDismiss,onOpenTasks,onOpenGarden)
    } else {
        ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFFFFCF1)) {
            HomeTasksSheetContent(completedTasks,totalTasks,treeCount,onDismiss,onOpenTasks,onOpenGarden)
        }
    }
}

@Composable
fun HomeTasksSheetContent(
    completedTasks: Int,
    totalTasks: Int,
    treeCount: Int,
    onDismiss: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenGarden: () -> Unit
) {
    com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame(Modifier.padding(12.dp)) {
        Column(Modifier.fillMaxWidth(),verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Thói quen tích cực", fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge, color = Deep)
            Text("$completedTasks/$totalTasks nhiệm vụ hoàn thành hôm nay",color = Muted)
            LinearProgressIndicator(
                progress = { if(totalTasks <= 0) 0f else (completedTasks.toFloat()/totalTasks).coerceIn(0f,1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                color = Green, trackColor = Color(0xFFE2E8DE)
            )
            Button(onClick = { onDismiss(); onOpenTasks() },modifier=Modifier.fillMaxWidth()) {
                Text("Xem tất cả việc nhỏ")
            }
            OutlinedButton(onClick = { onDismiss(); onOpenGarden() },modifier=Modifier.fillMaxWidth()) {
                Text("Ghé thăm khu vườn ($treeCount cây)")
            }
        }
    }
}
