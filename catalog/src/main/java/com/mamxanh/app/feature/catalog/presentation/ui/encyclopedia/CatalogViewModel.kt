package com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollectionRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogViewModel(
    private val repository: GardenCollectionRepository,
) : BaseViewModel<CatalogState, CatalogEvent, CatalogAction>(CatalogState()) {
    private var refreshJob: Job? = null

    override fun handleAction(action: CatalogAction) {
        when (action) {
            is CatalogAction.Refresh -> refresh(action.speciesId)
            is CatalogAction.OpenSpecies -> send(CatalogEvent.OpenSpecies(action.id))
            is CatalogAction.OpenTree -> send(CatalogEvent.OpenTree(action.id))
            CatalogAction.OpenSeeds -> send(CatalogEvent.OpenSeeds)
        }
    }

    private fun refresh(speciesId: String?) {
        refreshJob?.cancel()
        refreshJob =
            viewModelScope.launch {
                try {
                    mutableState.value =
                        withContext(Dispatchers.IO) {
                            CatalogState(
                                collection = repository.load(),
                                loading = false,
                                treeSpeciesId = speciesId,
                                trees = speciesId?.let(repository::loadTrees).orEmpty(),
                            )
                        }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(loading = false)
                    emitEvent(CatalogEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }

    private fun send(event: CatalogEvent) {
        viewModelScope.launch { emitEvent(event) }
    }
}
