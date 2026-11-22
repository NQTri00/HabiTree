package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
private fun GardenOverviewCard(
    treeCount: Int,
    treesNeedingWater: Int,
    seedCount: Int,
) {
    Column(
        Modifier.fillMaxWidth().padding(top = AppDimensions.Dp4, bottom = AppDimensions.Dp4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GardenWoodSign(AppText.text(TextKey.CopyVuonUomCd0b43), Modifier.widthIn(max = AppDimensions.Dp285).fillMaxWidth(.85f))
        GardenBotanicalFrame(
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp20, vertical = AppDimensions.Dp6),
            cornerSize = AppDimensions.Dp20,
        ) {
            Text(
                AppText.text(TextKey.Copy0CayDangLonC1217a, treeCount),
                color = Deep,
                fontSize = AppTypography.Sp17,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(Modifier.padding(top = AppDimensions.Dp3), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
            GardenMetric("💧", AppText.text(TextKey.Copy0CanTuoi425a0d, treesNeedingWater))
            GardenMetric("🌰", AppText.text(TextKey.Copy0HatB54ddd, seedCount))
        }
    }
}

@Composable
private fun GardenMetric(
    emoji: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    GardenBotanicalFrame(
        modifier,
        contentPadding = PaddingValues(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp6),
        cornerSize = AppDimensions.Dp18,
    ) {
        Text("$emoji $label", color = Deep, fontSize = AppTypography.Sp14, fontWeight = FontWeight.Bold)
    }
}

/** A planted scene with a live name plaque; no card or flowerpot around the tree. */
@Composable
private fun GardenTreeIsland(
    tree: Tree,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.clickable(onClickLabel = AppText.text(TextKey.CopyChamSoc0E5e78b, tree.name), onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PaintedGardenPlant(tree.speciesId, Modifier.fillMaxWidth().height(AppDimensions.Dp96))
        GardenBotanicalFrame(
            Modifier.fillMaxWidth(.84f).offset(y = AppDimensions.DpMinus8),
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp7),
            cornerSize = AppDimensions.Dp25,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp2)) {
                Text(
                    tree.name,
                    color = Deep,
                    fontSize = AppTypography.Sp15,
                    lineHeight = AppTypography.Sp17,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
                LinearProgressIndicator(
                    progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                    Modifier.fillMaxWidth().height(AppDimensions.Dp6).clip(CircleShape),
                    color = Green,
                    trackColor = AppColors.SwatchFFD7BD7C,
                    drawStopIndicator = {
                    },
                )
                Text(
                    "${tree.growth.coerceIn(0,120)} / 120",
                    color = Deep,
                    fontSize = AppTypography.Sp12,
                    lineHeight = AppTypography.Sp14,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (tree.wateredToday) AppText.text(TextKey.CopyDaTuoiD97f10) else AppText.text(TextKey.CopyChoTuoi79f9ea),
                    color = if (tree.wateredToday) Green else AppColors.SwatchFF1F6481,
                    fontSize = AppTypography.Sp13,
                    lineHeight = AppTypography.Sp15,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(name = "Nursery overview", widthDp = 360)
@Composable
fun GardenOverviewCardPreview() {
    MamXanhPreviewTheme { GardenOverviewCard(3, 2, 2) }
}

@Preview(name = "Planted island", widthDp = 180)
@Composable
fun GardenTreeCardPreview() {
    MamXanhPreviewTheme { GardenTreeIsland(sampleGarden().trees.first(), {}) }
}
