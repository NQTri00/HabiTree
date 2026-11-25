package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.repository.TreeDetailRecord

data class TreeState(
    val isLoading: Boolean = false,
    val isPanelExpanded: Boolean = false,
    val dispositionId: Long? = null,
    val selling: Boolean = false,
    val quote: TreeCareReceipt? = null,
    val actionKey: String = "",
    val tab: String = "care",
    val detail: TreeDetailRecord? = null,
) : UiState
