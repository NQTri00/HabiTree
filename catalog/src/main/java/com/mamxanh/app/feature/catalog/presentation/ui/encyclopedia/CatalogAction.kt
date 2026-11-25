package com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia

import com.mamxanh.app.core.presentation.UiAction

sealed interface CatalogAction : UiAction {
    data class Refresh(
        val speciesId: String?,
    ) : CatalogAction

    data class OpenSpecies(
        val id: String,
    ) : CatalogAction

    data class OpenTree(
        val id: String,
    ) : CatalogAction

    data object OpenSeeds : CatalogAction
}
