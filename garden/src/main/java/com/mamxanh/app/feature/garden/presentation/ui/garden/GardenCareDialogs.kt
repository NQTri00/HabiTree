package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
internal fun GardenCareDialogs(
    state: GardenState,
    waterAllKey: String?,
    unlockingPlotId: Int?,
    onDismissWaterAll: () -> Unit,
    onDismissUnlock: () -> Unit,
    onIntent: (GardenIntent) -> Unit,
) {
    val garden = state.garden
    waterAllKey?.let { key ->
        MintDialog(
            AppText.text(TextKey.CopyTuoiCaVuon20a14a),
            onDismissWaterAll,
            AppText.text(TextKey.CopyTuoiCay0990f6),
            {
                onIntent(GardenIntent.WaterAll(key))
                onDismissWaterAll()
            },
            confirmEnabled = !state.isLoading && garden.waterAll.eligible > 0,
        ) {
            MintStatusPill(AppText.text(TextKey.CopyMienPhiDfc4f7))
            MintMetricStrip(
                listOf(
                    AppText.text(TextKey.CopyCanTuoiA0957f) to garden.waterAll.eligible.toString(),
                    AppText.text(TextKey.CopyDaTuoiDe2042) to garden.waterAll.active.toString(),
                    AppText.text(TextKey.CopyKhoHeoBf546a) to garden.waterAll.dead.toString(),
                ),
            )
            Text(
                AppText.text(TextKey.CopyChiTuoiCayConSongDangCanNuocThoiGianTuoiK958a7a),
                color = Muted,
                fontSize = AppTypography.Sp12,
                lineHeight = AppTypography.Sp17,
            )
        }
    }
    unlockingPlotId?.let { id ->
        val price =
            com.mamxanh.shared.model.Balance
                .plot(garden.plots.count { it.isUnlocked })
        MintDialog(
            AppText.text(TextKey.CopyMoOTrong11b979),
            onDismissUnlock,
            AppText.text(TextKey.CopyMoO6d274c),
            {
                onIntent(GardenIntent.UnlockPlot(id))
                onDismissUnlock()
            },
            confirmEnabled = garden.coins >= price && !state.isLoading,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                GardenArtwork(GardenArt.SPROUT, Modifier.size(AppDimensions.Dp56))
                Text(
                    AppText.text(TextKey.CopyO03d2411, id.toString().padStart(2, '0')),
                    color = Deep,
                    fontSize = AppTypography.Sp17,
                    fontWeight = FontWeight.Bold,
                )
            }
            MintMetricStrip(
                listOf(
                    AppText.text(TextKey.CopyGiaMoO708ec3) to AppText.text(TextKey.Copy0Xu251e68, price),
                    AppText.text(TextKey.CopyViCuaBanD87c21) to AppText.text(TextKey.Copy0Xu251e68, garden.coins),
                ),
            )
            if (garden.coins <
                price
            ) {
                Text(AppText.text(TextKey.CopyCanThem0XuD7d41b, price - garden.coins), color = Muted, fontSize = AppTypography.Sp12)
            }
        }
    }
}

@Preview(name = "Water the garden", widthDp = 375, heightDp = 812)
@Composable
private fun GardenCareDialogsPreview() {
    MamXanhPreviewTheme {
        GardenCareDialogs(
            state = GardenState(garden = sampleGarden(), isLoading = false),
            waterAllKey = "water-preview",
            unlockingPlotId = null,
            onDismissWaterAll = {},
            onDismissUnlock = {},
            onIntent = {},
        )
    }
}
