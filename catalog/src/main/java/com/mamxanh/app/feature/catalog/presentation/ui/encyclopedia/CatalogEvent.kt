package com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia

import com.mamxanh.app.core.presentation.UiEvent

sealed interface CatalogEvent : UiEvent {
    data class OpenSpecies(
        val id: String,
    ) : CatalogEvent

    data class OpenTree(
        val id: String,
    ) : CatalogEvent

    data object OpenSeeds : CatalogEvent

    data class Feedback(
        val message: String,
    ) : CatalogEvent
}
