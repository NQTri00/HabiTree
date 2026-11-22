package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.shop.domain.model.ShopWallet

data class ShopState(
    val wallet: ShopWallet = ShopWallet(0, emptyMap()),
    val isLoading: Boolean = true,
    val averageEarnings: Int = 40,
    val filter: ShopFilter = ShopFilter.SEEDS,
    val isPurchasing: Boolean = false,
    val outcome: PurchaseOutcome? = null,
) : UiState
