package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

private fun webComparisonGardenState() =
    LivingGardenUiState(
        loading = false,
        route = "STUDIO",
        selected = "accent_lantern",
        snapshot =
            LivingGardenSnapshot(
                coins = 620,
                owned = mapOf("accent_lantern" to 1),
                species =
                    listOf(
                        SpeciesRecord(
                            "cherry",
                            discoveredAt = 1,
                            highestStage = 3,
                            planted = 1,
                            firstPlanted = 1793494800000,
                            ownedSeeds = 2,
                            living = 1,
                            tallestStage = 3,
                        ),
                        SpeciesRecord("pine", discoveredAt = 1, highestStage = 2, planted = 1, living = 1),
                        SpeciesRecord("succulent", discoveredAt = 1, highestStage = 5, planted = 1, firstMatured = 1),
                    ),
                sections =
                    (1..4).map { GardenSection(it, ((it - 1) * 6 + 1..it * 6).toList(), it == 1, 5400) },
            ),
        garden =
            Garden(
                trees =
                    listOf(
                        Tree(
                            1,
                            "cherry",
                            AppText.text(TextKey.CopyDaoHongXuanC2fd99),
                            45,
                            "2026-11-01",
                            status = TreeStatus(stageIndex = 3, stageCount = 9, stageProgress = .46f),
                        ),
                        Tree(2, "pine", AppText.text(TextKey.CopyThongNho84a4db), 24, "2026-11-04"),
                    ),
                plots =
                    (1..24).map { GardenPlot(it, it <= 5, if (it <= 2)it.toLong()else null) },
            ),
    )

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun StudioWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState(), { _, _ -> }, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun CosmeticWebDesignPreview() {
    MamXanhPreviewTheme {
        MintBackdrop {
            Column(Modifier.padding(AppDimensions.Dp24), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
                CosmeticDetail(webComparisonGardenState(), { _, _ -> }, {})
            }
        }
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun DecorShopWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "SHOP", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun DecorInventoryWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "INVENTORY", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun EncyclopediaWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "ENCYCLOPEDIA", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun SpeciesWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "SPECIES", selected = "cherry"), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun AppearanceWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "APPEARANCE", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun ExpansionWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "EXPANSION", selected = "2"), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}
