package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun HomeScreen(
    state: HomeUiState, onIntent: (HomeIntent) -> Unit, fertilizerCount: Int = 0,
    modifier: Modifier = Modifier, sharedTreeModifier: Modifier = Modifier,
    previewApproach: Float = 0f, previewViewportHeight: Dp? = null,
    taskTitle: String = "Mỗi việc nhỏ, một bước lớn lên",
    canClaimBonus: Boolean = false, onClaimBonus: () -> Unit = { onIntent(HomeIntent.OpenTasks) }
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintHeader("Mầm Xanh", d.coins)
            BoxWithConstraints(Modifier.weight(1f)) {
                val heroHeight = (maxHeight - 410.dp).coerceIn(110.dp, 300.dp)
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp).padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (tree == null) {
                        Spacer(Modifier.height(20.dp))
                        GardenArtwork(GardenArt.SEED, Modifier.size(150.dp))
                        Text("Chưa có cây trong vườn", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Deep)
                        Text("Hãy gieo mầm đầu tiên để bắt đầu\nhành trình chăm sóc cây cùng Mầm Xanh!",
                            color = Deep, textAlign = TextAlign.Center)
                        GardenPaintedButton("🌱 Gieo mầm đầu tiên", { onIntent(HomeIntent.OpenGarden) }, Modifier.fillMaxWidth())
                    } else {
                        GardenBotanicalFrame(Modifier.fillMaxWidth(.9f).clickable { onIntent(HomeIntent.OpenTree(tree.id)) },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp), cornerSize = 40.dp) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                GardenArtwork(GardenArt.SPROUT, Modifier.size(32.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(tree.name, color = Deep, fontWeight = FontWeight.Bold)
                                    MintProgress(tree.growth / 120f, Modifier.fillMaxWidth())
                                    Text("${d.completedTasks} / ${d.totalTasks} việc hôm nay", fontSize = 13.sp, color = Deep)
                                }
                                Text("›", color = Deep, fontSize = 28.sp)
                            }
                        }
                        GardenPlantPortrait(tree.speciesId, tree.decoration,
                            sharedTreeModifier.fillMaxWidth().height(heroHeight)
                                .clickable { onIntent(HomeIntent.OpenTree(tree.id)) }, growth = tree.growth)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HomeCareTile(GardenArt.WATERING_CAN, if(tree.wateredToday) "✓ Đã tưới" else "Tưới",
                                !tree.wateredToday && d.wateringCanMl >= 50, { onIntent(HomeIntent.WaterTree(tree.id)) }, Modifier.weight(1f))
                            HomeCareTile(GardenArt.FERTILIZER, "Bón", fertilizerCount > 0,
                                { onIntent(HomeIntent.FertilizeTree(tree.id)) }, Modifier.weight(1f))
                            HomeCareTile(GardenArt.PUMP, "Bơm", true, { onIntent(HomeIntent.RefillWater) }, Modifier.weight(1f))
                            HomeCareTile(GardenArt.DROP, "Nước\n${d.wateringCanMl} ml", true,
                                { onIntent(HomeIntent.RefillWater) }, Modifier.weight(1f))
                        }
                        HomeLink(GardenArt.CHECKLIST, "Việc nhỏ", taskTitle, { onIntent(HomeIntent.OpenTasks) })
                        val claimed = d.dailyRewardDate.isNotBlank()
                        HomeLink(if(claimed) GardenArt.CHECKED_TASK else GardenArt.COIN,
                            if(claimed) "Đã nhận thưởng hôm nay" else "Thưởng hôm nay",
                            if(claimed) "" else "+${d.dailyRewardCoins} xu",
                            if(claimed) ({}) else onClaimBonus, enabled = !claimed && canClaimBonus)
                    }
                }
            }
        }
        if (state.isDailyClearVisible) {
            AlertDialog(onDismissRequest = { onIntent(HomeIntent.DismissDailyClear) },
                title = { Text("✓ Đã xong hôm nay", fontWeight = FontWeight.Bold) },
                text = { Text("Đã nhận +${d.dailyRewardCoins} xu") },
                confirmButton = { TextButton(onClick = { onIntent(HomeIntent.DismissDailyClear) }) { Text("OK") } })
        }
    }
}

@Composable
private fun HomeCareTile(art: GardenArt, label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    GardenBotanicalFrame(modifier.clickable(enabled = enabled, onClick = onClick),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp), cornerSize = 22.dp) {
        Column(Modifier.fillMaxWidth().heightIn(min = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            GardenArtwork(art, Modifier.size(36.dp))
            Text(label, color = if(enabled) Deep else Muted, fontSize = 12.sp, lineHeight = 15.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HomeLink(art: GardenArt, title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick),
        contentPadding = PaddingValues(12.dp), cornerSize = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GardenArtwork(art, Modifier.size(36.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold)
                if(subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = 13.sp)
            }
            if(enabled) Text("›", color = Green, fontSize = 28.sp)
        }
    }
}

private fun previewHomeState(treeName: String = "Đào hồng xuân") = HomeUiState(HomeDashboard(
    trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
    completedTasks = 2, totalTasks = 4, coins = 120, wateringCanMl = 500), false)

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) } }
@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(HomeDashboard(coins = 60), false), {}) } }
@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState("Cây đào bình yên bên cửa sổ của gia đình"), {}, 2) } }
@Composable
fun HomeCompactLongNamePreview() { HomeLongNamePreview() }
@Composable
fun HomeShopApproachPreview() { HomeScreenPreview() }
