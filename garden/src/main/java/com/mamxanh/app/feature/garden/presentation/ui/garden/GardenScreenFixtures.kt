package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

internal fun sampleGarden() =
    Garden(
        trees =
            listOf(
                Tree(1, "cherry", AppText.text(TextKey.CopyDaoHong76f74f), 68, "2026-01-01"),
                Tree(2, "pine", AppText.text(TextKey.CopyThongNho84a4db), 24, "2026-01-02", wateredToday = true),
                Tree(3, "succulent", AppText.text(TextKey.CopySenDaB2d005), 12, "2026-01-03"),
            ),
        inventory = mapOf("cherry" to 1, "pine" to 1),
    )

@Preview(name = "Nursery · normal", widthDp = 360, heightDp = 800)
@Preview(name = "Nursery · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun GardenScreenPopulatedPreview() {
    MamXanhPreviewTheme { GardenScreen(GardenState(sampleGarden(), false), {}) }
}

@Preview(
    name = "Nursery · extended plot audit",
    widthDp = 360,
    heightDp = 990,
    device = "spec:width=360dp,height=990dp,dpi=160",
)
@Composable
fun GardenExtendedPlotsPreview() {
    val fixture =
        sampleGarden().copy(
            plots =
                listOf(
                    GardenPlot(1, true, 1L),
                    GardenPlot(2, true, 2L),
                    GardenPlot(3, true),
                    GardenPlot(4, true, 3L),
                    GardenPlot(5, true),
                    GardenPlot(6, false),
                ),
            coins = 120,
        )
    MamXanhPreviewTheme { GardenScreen(GardenState(fixture, false), {}) }
}

@Preview(name = "Nursery · empty", widthDp = 360, heightDp = 800)
@Composable
fun GardenScreenEmptyPreview() {
    MamXanhPreviewTheme { GardenScreen(GardenState(Garden(), false), {}) }
}
