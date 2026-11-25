package com.mamxanh.app.feature.tree.presentation.ui.accessory

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot

data class AccessoryState(
    val sku: String = "",
    val selected: String = "",
    val snapshot: AccessorySnapshot = AccessorySnapshot(),
    val busy: Boolean = false,
    val loading: Boolean = true,
) : UiState {
    val eligibleTargets: List<String> get() = snapshot.trees.filter { it.living }.map { it.id }
}
