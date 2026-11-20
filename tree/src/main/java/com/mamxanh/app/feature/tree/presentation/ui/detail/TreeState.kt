package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt

data class TreeUiState(
    val isLoading: Boolean = false,val isPanelExpanded: Boolean = false,
    val dispositionId: Long? = null,val selling: Boolean = false,
    val quote: TreeCareReceipt? = null,val actionKey: String = ""
) : UiState
