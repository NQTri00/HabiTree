package com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.catalog.domain.repository.CollectedTree
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollection

data class CatalogState(
    val collection: GardenCollection? = null,
    val loading: Boolean = true,
    val treeSpeciesId: String? = null,
    val trees: List<CollectedTree> = emptyList(),
) : UiState {
    fun species(id: String) = collection?.species?.firstOrNull { it.id == id }
}
