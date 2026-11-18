package com.mamxanh.app.feature.tree.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.shared.model.ActionResult

data class TreeUiState(
    val isLoading: Boolean = false,val isPanelExpanded: Boolean = false,
    val dispositionId: Long? = null,val selling: Boolean = false,
    val quote: ActionResult? = null,val actionKey: String = ""
) : UiState
