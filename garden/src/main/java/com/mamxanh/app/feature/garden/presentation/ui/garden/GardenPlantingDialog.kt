package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.Species
import com.mamxanh.shared.model.catalog
import com.mamxanh.app.core.components.components.GardenRichText as Text

private val PlantingDialogBounds = Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth(.92f).heightIn(max = AppDimensions.Dp700)

@Composable
internal fun PlantingDialog(
    seed: Species,
    count: Int,
    targetPlotId: Int?,
    availablePlotIds: List<Int>,
    onSelectPlot: (Int) -> Unit,
    onDismiss: () -> Unit,
    onPlant: (String) -> Unit,
) {
    var name by rememberSaveable(seed.id) { mutableStateOf("") }
    var plotMenu by remember { mutableStateOf(false) }
    val requester = remember { BringIntoViewRequester() }
    val density = androidx.compose.ui.platform.LocalDensity.current
    var focused by remember { mutableStateOf(false) }
    val ime = WindowInsets.ime.getBottom(density)
    LaunchedEffect(focused, ime) {
        if (focused && ime > 0) {
            withFrameNanos {}
            requester.bringIntoView()
        }
    }
    MintDialog(
        AppText.text(TextKey.CopyGieoCay31adde),
        onDismiss,
        AppText.text(TextKey.CopyGieoHat6d82a9),
        { onPlant(name) },
        confirmEnabled =
            count > 0,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            PaintedGardenPlant(seed.id, Modifier.size(AppDimensions.Dp64))
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp4)) {
                Text(seed.name, color = Deep, fontSize = AppTypography.Sp16, fontWeight = FontWeight.Bold)
                MintStatusPill(AppText.text(TextKey.CopyTrongTui0HatD56e0e, count))
            }
        }
        GardenTextField(
            name,
            { name = it },
            AppText.text(TextKey.CopyTenCayTuyChon3f3d38),
            Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(requester)
                .onFocusChanged { focused = it.hasFocus },
            placeholder = AppText.text(TextKey.CopyMamBinhYenDf7062),
        )
        Text(AppText.text(TextKey.CopyBoTrongDeDungTenLoaiCayA7e5fc), color = Muted, fontSize = AppTypography.Sp11)
        Box(Modifier.fillMaxWidth()) {
            GardenFieldButton(AppText.text(TextKey.CopyOTrong0174f9e, targetPlotId?.toString()?.padStart(2, '0') ?: "—"), {
                plotMenu = true
            }, Modifier.fillMaxWidth())
            DropdownMenu(expanded = plotMenu, onDismissRequest = { plotMenu = false }) {
                availablePlotIds.forEach { id ->
                    DropdownMenuItem(
                        text = { Text(AppText.text(TextKey.CopyO03d2411, id.toString().padStart(2, '0'))) },
                        onClick = {
                            onSelectPlot(id)
                            plotMenu = false
                        },
                    )
                }
            }
        }
        Text(AppText.text(TextKey.CopyDung1HatTrongTuiDeBatDauTrongBde1a7), color = Muted, fontSize = AppTypography.Sp12)
    }
}

// Compatibility for isolated reference previews; runtime and previews use the same dialog.
@Composable
private fun PlantingParchment(
    seed: Species,
    count: Int,
    onDismiss: () -> Unit,
    onPlant: (String) -> Unit,
    targetPlotId: Int? = 4,
    availablePlotIds: List<Int> = listOf(4),
    onSelectPlot: (Int) -> Unit = {},
) {
    PlantingDialog(seed, count, targetPlotId, availablePlotIds, onSelectPlot, onDismiss, onPlant)
}

@Preview(
    name = "Planting parchment",
    widthDp = 360,
    heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160",
)
@Composable
fun PlantingDialogPreview(background: (@Composable () -> Unit)? = null) {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (background == null) {
                MintBackdrop(Modifier.fillMaxSize()) {}
            } else {
                background()
                Box(Modifier.matchParentSize().background(AppColors.Black.copy(alpha = AppOpacity.VALUE_32)))
            }
            Box(PlantingDialogBounds) { PlantingParchment(catalog.first { it.id == "cherry" }, 1, {}, {}) }
        }
    }
}

@Preview(
    name = "Planting · large text, no stock",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160",
)
@Composable
fun PlantingDialogLargeTextPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MintBackdrop(Modifier.fillMaxSize()) {}
            Box(PlantingDialogBounds) {
                PlantingParchment(
                    catalog.first {
                        it.id ==
                            "cherry"
                    },
                    0,
                    {},
                    {},
                )
            }
        }
    }
}

@Preview(
    name = "Planting · short window, large text",
    widthDp = 640,
    heightDp = 180,
    fontScale = 1.3f,
    device = "spec:width=640dp,height=180dp,dpi=160",
)
@Preview(
    name = "Planting · landscape IME height",
    widthDp = 640,
    heightDp = 97,
    device = "spec:width=640dp,height=97dp,dpi=160",
)
@Composable
fun PlantingShortWindowPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth(.92f)) {
                PlantingParchment(catalog.first { it.id == "cherry" }, 1, {}, {})
            }
        }
    }
}
