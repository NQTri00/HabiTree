package com.mamxanh.app.feature.inventory.presentation.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

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
            "seeds" to stringResource(R.string.copy_hat_giong_f02257),
            "care" to stringResource(R.string.copy_dung_cu_913b1a),
            "accessories" to stringResource(R.string.copy_phu_kien_0852d7),
        ),
        tab,
        onTab,
    )
    if (itemCount > 0) {
        BioGrid(itemCount, content = item)
        BioButton(
            if (tab == "accessories") stringResource(R.string.copy_mua_phu_kien_7055c4) else stringResource(R.string.copy_ghe_tiem_cb74e9),
            onBuy,
            Modifier.fillMaxWidth(),
            "shop",
            true,
        )
    } else {
        BioEmpty(stringResource(R.string.copy_tui_dang_trong_37bf7b),stringResource(R.string.copy_ban_co_the_mua_va_dung_ngay_trong_tiem_784a6a),"bag",stringResource(R.string.copy_ghe_tiem_cb74e9),onShop)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioInventoryScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioInventoryScreen("accessories", 0, {}, {}, {}, { _, _ -> })
    }
}
