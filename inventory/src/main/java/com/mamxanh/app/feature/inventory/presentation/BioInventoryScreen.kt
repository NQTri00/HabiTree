package com.mamxanh.app.feature.inventory.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun BioInventoryScreen(
    tab: String,
    itemCount: Int,
    onTab: (String) -> Unit,
    onShop: () -> Unit,
    onBuy: () -> Unit,
    item: @Composable (Int, Modifier) -> Unit,
) {
    BioTabs(
        listOf(
            "seeds" to AppText.text(TextKey.CopyHatGiongF02257),
            "care" to AppText.text(TextKey.CopyDungCu913b1a),
            "accessories" to AppText.text(TextKey.CopyPhuKien0852d7),
        ),
        tab,
        onTab,
    )
    if (itemCount > 0) {
        BioGrid(itemCount, content = item)
        BioButton(
            if (tab == "accessories") AppText.text(TextKey.CopyMuaPhuKien7055c4) else AppText.text(TextKey.CopyGheTiemCb74e9),
            onBuy,
            Modifier.fillMaxWidth(),
            "shop",
            true,
        )
    } else {
        BioEmpty(AppText.text(TextKey.CopyTuiDangTrong37bf7b),AppText.text(TextKey.CopyBanCoTheMuaVaDungNgayTrongTiem784a6a),"bag",AppText.text(TextKey.CopyGheTiemCb74e9),onShop)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioInventoryScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioInventoryScreen("accessories", 0, {}, {}, {}, { _, _ -> })
    }
}
