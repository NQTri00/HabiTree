package com.mamxanh.app.feature.tree.presentation.ui.detail.component
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

private fun sectionPreviewTree() =
    Tree(
        1,
        "cherry",
        AppText.text(TextKey.CopyDaoHongXuanC2fd99),
        45,
        "2026-11-01",
        "FLOWERS",
        status = TreeStatus(stageIndex = 3, stageCount = 9, stageProgress = .46f, wateredUntil = 100, sampledAt = 1, cue = CareCue.HEALTHY),
    )

@Composable
private fun SectionDocument(selected: Int) {
    MamXanhPreviewTheme {
        Column(
            Modifier.fillMaxWidth().background(Cream).padding(AppDimensions.Dp24),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
        ) {
            TreeDetailTabs(selected, {})
            TreeDetailSectionContent(sectionPreviewTree(), selected, mapOf("fertilizer" to 2, "fertilizer_rich" to 1), false, {})
        }
    }
}

@Preview(name = "Care · complete content", widthDp = 360)
@Composable
fun TreeCareFullContentPreview() {
    SectionDocument(0)
}

@Preview(name = "Decoration · complete content", widthDp = 360)
@Composable
fun TreeDecorationFullContentPreview() {
    SectionDocument(1)
}

@Preview(name = "Information · complete content", widthDp = 360)
@Composable
fun TreeInformationFullContentPreview() {
    SectionDocument(2)
}

@Preview(name = "Care · narrow, large text, complete content", widthDp = 320, fontScale = 1.3f)
@Composable
fun TreeCareNarrowContentPreview() {
    SectionDocument(0)
}
