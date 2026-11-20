package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection

@Stable
class HomeTreeState {
    var selectedId by mutableStateOf("1")

    fun reconcile(selection: HomeTreeSelection, availableIds: List<String>) {
        selectedId = selection.resolve(selectedId, availableIds)
    }
}
