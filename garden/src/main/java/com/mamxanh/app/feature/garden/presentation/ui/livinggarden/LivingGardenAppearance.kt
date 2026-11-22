package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable internal fun AppearanceContent(
    state: LivingGardenUiState,
    reducedMotion: Boolean,
    onReducedMotion: (Boolean) -> Unit,
    onAct: (LivingGardenAction) -> Unit,
    onMemories: () -> Unit,
) {
    val initial = state.snapshot.appearance
    var nickname by rememberSaveable { mutableStateOf(initial.nickname) }
    var theme by rememberSaveable { mutableStateOf(initial.theme) }
    var density by rememberSaveable { mutableStateOf(initial.density) }
    var battery by rememberSaveable { mutableStateOf(initial.batterySaver) }
    GardenCaption(AppText.text(TextKey.CopyTu07eb90b, dateLabel(initial.createdAt)))
    GardenTextField(nickname, { nickname = it.take(40) }, AppText.text(TextKey.CopyTenVuon1b0761))
    MintMetricStrip(
        listOf(
            AppText.text(TextKey.CopyLoaiCayDd9342) to
                state.garden.trees
                    .map { it.speciesId }
                    .distinct()
                    .size
                    .toString(),
            AppText.text(TextKey.CopyTruongThanh8c14d7) to
                state.garden.trees
                    .count { it.status.state == TreeGrowthState.MATURE }
                    .toString(),
            AppText.text(TextKey.CopyTrangTriD48231) to
                state.snapshot.placements.size
                    .toString(),
        ),
    )
    GardenChoice(
        AppText.text(TextKey.CopyChuDe59500d),
        theme,
        (
            listOf("MEADOW" to AppText.text(TextKey.CopyDongCo09da3d)) +
                gardenCosmetics.filter { it.category == "THEME" }.map { it.id to it.name }
        ),
        { theme = it },
    ) { it == "MEADOW" || (state.snapshot.owned[it] ?: 0) > 0 }
    GardenChoice(
        AppText.text(TextKey.CopyHatBayA6343b),
        density,
        listOf(
            "OFF" to AppText.text(TextKey.CopyTat328606),
            "LOW" to AppText.text(TextKey.CopyItA3681b),
            "NORMAL" to AppText.text(TextKey.CopyVua8af1c0),
        ),
        { density = it },
    )
    GardenPanel {
        SettingToggle(AppText.text(TextKey.CopyTietKiemPinB1018c), battery, { battery = it }, Icons.Rounded.BatterySaver)
        HorizontalDivider(color = AppColors.SwatchFFE4E7DC)
        SettingToggle(AppText.text(TextKey.CopyGiamChuyenDongFf3295), reducedMotion, onReducedMotion, Icons.Rounded.MotionPhotosOff)
    }
    GardenCaption(AppText.text(TextKey.CopyGiamChuyenDongHoacTietKiemPinTatHatBayAmT91e597))
    GardenPaintedButton(
        AppText.text(TextKey.CopyLuuDienMao105663),
        {
            onAct(
                LivingGardenAction.Appearance(
                    initial.copy(nickname = nickname, theme = theme, density = density, batterySaver = battery, muted = true),
                ),
            )
        },
        Modifier.fillMaxWidth(),
        enabled =
            !state.busy && nickname.isNotBlank(),
    )
    GardenNavigationCard(AppText.text(TextKey.CopyKyNiemE3e113), onMemories, art = GardenArt.JOURNAL)
}

@Composable private fun GardenChoice(
    label: String,
    value: String,
    options: List<Pair<String, String>>,
    onChange: (String) -> Unit,
    enabled: (String) -> Boolean = {
        true
    },
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
        GardenCaption(label)
        Box {
            GardenPaintedButton(
                (
                    options
                        .firstOrNull {
                            it.first == value
                        }?.second ?: value
                ) + "  ⌄",
                { expanded = true },
                Modifier.fillMaxWidth(),
                primary = false,
            )
            DropdownMenu(expanded, { expanded = false }) {
                options.forEach { (id, name) ->
                    DropdownMenuItem(text = { Text(name) }, onClick = {
                        onChange(id)
                        expanded =
                            false
                    }, enabled = enabled(id))
                }
            }
        }
    }
}

@Composable private fun SettingToggle(
    label: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp48).clickable {
            onChecked(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
    ) {
        Icon(icon, null, Modifier.size(AppDimensions.Dp16), tint = Green)
        Text(label, Modifier.weight(1f), color = Deep, fontSize = AppTypography.Sp13, lineHeight = AppTypography.Sp19)
        Switch(
            checked,
            onChecked,
            Modifier.graphicsLayer(scaleX = .75f, scaleY = .75f),
            colors =
                SwitchDefaults.colors(
                    checkedTrackColor = Green,
                    uncheckedTrackColor = AppColors.SwatchFFDCE9DF,
                    uncheckedBorderColor = AppColors.SwatchFFB7CFC0,
                ),
        )
    }
}

@Composable internal fun ExpansionContent(
    state: LivingGardenUiState,
    onAct: (LivingGardenAction) -> Unit,
) {
    val section = state.snapshot.sections.firstOrNull { it.id == state.selected.toIntOrNull() } ?: return
    Column(
        Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10),
    ) {
        Icon(Icons.Rounded.GridView, null, Modifier.size(AppDimensions.Dp44), tint = Green)
        Text(
            AppText.text(TextKey.CopyKhuVuon0C58827, section.id),
            color = Deep,
            fontSize = AppTypography.Sp26,
            lineHeight = AppTypography.Sp34,
            fontWeight = FontWeight.Bold,
        )
        GardenCaption(AppText.text(TextKey.CopyThem6OGiuNguyenCayHienCoF944b2))
    }
    section.plots.chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
            row.forEach { id ->
                GardenBotanicalFrame(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(AppDimensions.Dp18),
                    cornerSize = AppDimensions.Dp18,
                ) {
                    Text(id.toString(), Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Deep, fontSize = AppTypography.Sp14)
                }
            }
        }
    }
    MintMetricStrip(
        listOf(
            AppText.text(TextKey.CopyGiaMoKhuD7a20a) to AppText.text(TextKey.Copy0Xu251e68, section.price),
            AppText.text(TextKey.CopyViHienTai127da5) to AppText.text(TextKey.Copy0Xu251e68, state.snapshot.coins),
        ),
    )
    GardenPaintedButton(
        AppText.text(TextKey.CopyMoKhu0XuD27450, section.price),
        {
            onAct(LivingGardenAction.Expand(section.id, newKey()))
        },
        Modifier.fillMaxWidth(),
        enabled =
            !state.busy && !section.unlocked && state.snapshot.coins >= section.price,
    )
    if (state.snapshot.coins < section.price)GardenCaption(AppText.text(TextKey.CopyCanThem0Xu8837ee, section.price - state.snapshot.coins))
}

@Preview(name = "Compact appearance · owner refresh", widthDp = 360, heightDp = 620, device = "spec:width=360dp,height=620dp,dpi=160")
@Composable
fun CompactAppearanceOwnerPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(TextStyle(fontSize = AppTypography.Sp13, lineHeight = AppTypography.Sp19)) {
            Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp10), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                AppearanceContent(LivingGardenUiState(loading = false), false, {}, {}, {})
            }
        }
    }
}
