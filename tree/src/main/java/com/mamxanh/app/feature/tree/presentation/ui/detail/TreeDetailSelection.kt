package com.mamxanh.app.feature.tree.presentation.ui.detail

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class TreeDetailSelection {
    var tab by mutableStateOf(DefaultTab)

    fun open(tab: String?) {
        this.tab = tab ?: DefaultTab
    }

    private companion object {
        const val DefaultTab = "care"
    }
}
