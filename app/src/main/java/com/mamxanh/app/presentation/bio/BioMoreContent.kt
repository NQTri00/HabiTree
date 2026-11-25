package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.core.ui.R

@Composable internal fun ColumnScope.BioMore(ui: BioController) {
    BioIntro(stringResource(R.string.copy_them_mot_chut_006e8b), stringResource(R.string.copy_theo_nhip_cua_ban_5a2ada))
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        listOf(
            Triple("focus", "clock", stringResource(R.string.copy_tap_trung_964002)),
            Triple("templates", "plus", stringResource(R.string.copy_viec_mau_535dc1)),
            Triple("review", "calendar", stringResource(R.string.copy_nhin_lai_tuan_ff3f0c)),
            Triple("achievements", "award", stringResource(R.string.copy_dau_moc_dffd66)),
            Triple("timeline", "book", stringResource(R.string.copy_ky_niem_e3e113)),
            Triple("insights", "chart", stringResource(R.string.copy_nhip_thoi_quen_1b8324)),
            Triple("encyclopedia", "book", stringResource(R.string.copy_bach_thao_f2ed64)),
            Triple("settings", "settings", stringResource(R.string.copy_tuy_chon_nhac_viec_a44d85)),
            Triple("data", "download", stringResource(R.string.copy_du_lieu_ung_dung_9843b7)),
        ).forEach { (r, i, t) ->
            BioLink(t, i, {
                ui.go(r)
            })
        }
    }
}

@Preview(name = "More actions", widthDp = 335, heightDp = 720)
@Composable
private fun BioMoreComponentPreview() {
    BioReferenceSurface("more")
}
