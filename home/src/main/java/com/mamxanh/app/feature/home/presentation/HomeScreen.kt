package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun HomeScreen(
    state: HomeUiState, onIntent: (HomeIntent) -> Unit, fertilizerCount: Int = 0,
    modifier: Modifier = Modifier, sharedTreeModifier: Modifier = Modifier,
    taskTitle: String = "Mỗi việc nhỏ, một bước lớn lên",
    canClaimBonus: Boolean = false, onClaimBonus: () -> Unit = { onIntent(HomeIntent.OpenTasks) }
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    val heroHeight = mintTreeHeroHeight()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(d.coins)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp).padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (tree == null) {
                    Spacer(Modifier.height(20.dp))
                    MintEmptySprout(Modifier.size(150.dp))
                    Text("Chưa có cây trong vườn", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Deep)
                    Text("Hãy gieo mầm đầu tiên để bắt đầu\nhành trình chăm sóc cây cùng Mầm Xanh!",
                        color = Deep, textAlign = TextAlign.Center)
                    GardenPaintedButton("🌱 Gieo mầm đầu tiên", { onIntent(HomeIntent.OpenGarden) }, Modifier.fillMaxWidth())
                } else {
                    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(role = Role.Button) {
                        onIntent(HomeIntent.OpenTree(tree.id))
                    }, contentPadding = PaddingValues(8.dp), cornerSize = 24.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GardenArtwork(GardenArt.SPROUT, Modifier.size(24.dp))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(tree.name, color = Deep, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 17.sp)
                                MintProgress(tree.growth / 120f, Modifier.fillMaxWidth())
                                Text("${d.completedTasks} / ${d.totalTasks} việc hôm nay", fontSize = 11.sp, lineHeight = 14.sp, color = Deep)
                            }
                            Text("›", color = Deep, fontSize = 24.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth().heightIn(min = heroHeight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        GardenPlantPortrait(tree.speciesId, tree.decoration,
                            sharedTreeModifier.weight(1f).height(heroHeight)
                                .clickable(role = Role.Button) { onIntent(HomeIntent.OpenTree(tree.id)) }
                                .semantics { contentDescription = "Chăm sóc " + tree.name },
                            growth = tree.growth, fitVisibleArtwork = true)
                        Column(Modifier.width(52.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            MintCareIcon(GardenArt.WATERING_CAN,
                                if (tree.wateredToday) "Đã tưới hôm nay" else "Tưới",
                                !tree.wateredToday && d.wateringCanMl >= 50,
                                { onIntent(HomeIntent.WaterTree(tree.id)) }, checked = tree.wateredToday)
                            MintCareIcon(GardenArt.FERTILIZER, "Bón · $fertilizerCount phân bón",
                                fertilizerCount > 0, { onIntent(HomeIntent.FertilizeTree(tree.id)) })
                            MintCareIcon(GardenArt.PUMP, "Bơm nước", true, { onIntent(HomeIntent.RefillWater) })
                            MintCareIcon(GardenArt.DROP, "Nước ${d.wateringCanMl} ml", true,
                                { onIntent(HomeIntent.RefillWater) }, caption = "${d.wateringCanMl}\nml")
                        }
                    }
                    HomeLink(GardenArt.CHECKLIST, "Việc nhỏ", taskTitle, { onIntent(HomeIntent.OpenTasks) })
                    val claimed = d.dailyRewardDate.isNotBlank()
                    HomeLink(if (claimed) GardenArt.CHECKED_TASK else GardenArt.COIN,
                        if (claimed) "Đã nhận thưởng hôm nay" else "Thưởng hôm nay",
                        if (claimed) "" else "+${d.dailyRewardCoins} xu",
                        if (claimed) ({}) else onClaimBonus, enabled = !claimed && canClaimBonus)
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
private fun HomeLink(art: GardenArt, title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(8.dp), cornerSize = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GardenArtwork(art, Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                if (subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = 11.sp, lineHeight = 14.sp)
            }
            if (enabled) Text("›", color = Green, fontSize = 22.sp)
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
