package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.core.presentation.UiEvent
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LivingGardenViewModel(
    private val storage: GameStorage,
    private val saved: SavedStateHandle,
) : BaseViewModel<LivingGardenUiState, UiEvent, LivingGardenUiAction>(
        initialState =
            LivingGardenUiState(
                route = (saved.get<String>("living_route") ?: "").let { if (it == "ARRANGE")"STUDIO" else it },
                selected = saved["living_selected"] ?: "",
                draft = saved.get<ArrayList<String>>("layout_draft")?.mapNotNull(::decodePlacement),
                draftVersion =
                    saved["layout_version"] ?: 0L,
            ),
    ) {
    private val history = mutableListOf<List<GardenPlacement>>()

    init {
        viewModelScope.launch {
            storage.changes.collect { if (mutableState.value.route.isNotBlank() || mutableState.value.loading)refresh() }
        }
    }

    fun discover(ids: List<String>) {
        viewModelScope.launch { withContext(Dispatchers.IO) { storage.designGarden(LivingGardenAction.Discover(ids)) } }
    }

    fun refreshVisible() {
        if (mutableState.value.route.isNotBlank() && !mutableState.value.busy)viewModelScope.launch { refresh() }
    }

    private suspend fun refresh() {
        val pair = withContext(Dispatchers.IO) { storage.garden() to storage.livingGarden() }
        mutableState.value = mutableState.value.copy(garden = pair.first, snapshot = pair.second, loading = false)
    }

    fun open(
        route: String,
        selected: String = "",
    ) {
        val current = mutableState.value
        val stack = saved.get<ArrayList<String>>("living_backstack") ?: arrayListOf()
        if (route.isBlank()) {
            stack.clear()
        } else if (current.route.isNotBlank() && (current.route != route || current.selected != selected)) {
            stack.add(current.route + "|" + current.selected)
        }
        saved["living_backstack"] = stack
        show(route, selected)
    }

    fun back() {
        val stack = saved.get<ArrayList<String>>("living_backstack") ?: arrayListOf()
        val previous = if (stack.isEmpty())null else stack.removeAt(stack.lastIndex).split("|", limit = 2)
        saved["living_backstack"] = stack
        show(previous?.get(0) ?: "", previous?.getOrNull(1) ?: "")
    }

    private fun show(
        requestedRoute: String,
        selected: String = "",
    ) {
        val route = if (requestedRoute == "ARRANGE")"STUDIO" else requestedRoute
        saved["living_route"] = route
        saved["living_selected"] = selected
        mutableState.value = mutableState.value.copy(route = route, selected = selected, message = "")
        if (route.isNotBlank()) {
            mutableState.value = mutableState.value.copy(loading = true)
            viewModelScope.launch {
                refresh()
                if (mutableState.value.route == "ARRANGE" && mutableState.value.draft == null) {
                    history.clear()
                    mutableState.value =
                        mutableState.value.copy(
                            draft = mutableState.value.snapshot.placements,
                            draftVersion = mutableState.value.snapshot.version,
                            undoAvailable = false,
                        )
                    persist()
                }
            }
        }
    }

    fun resetAfterRestore() {
        history.clear()
        saved.remove<ArrayList<String>>("layout_draft")
        saved.remove<Long>("layout_version")
        mutableState.value = mutableState.value.copy(draft = null, draftVersion = 0, undoAvailable = false, selected = "")
        open("")
    }

    fun cancel() {
        history.clear()
        saved.remove<ArrayList<String>>("layout_draft")
        mutableState.value =
            mutableState.value.copy(draft = null, undoAvailable = false)
        back()
    }

    private fun persist() {
        saved["layout_draft"] =
            mutableState.value.draft
                ?.map {
                    listOf(it.id, it.sku, it.plotId, it.treeId).joinToString("|")
                }?.let { ArrayList(it) }
        saved["layout_version"] = mutableState.value.draftVersion
    }

    private fun change(next: List<GardenPlacement>) {
        history.add(mutableState.value.draft ?: return)
        mutableState.value = mutableState.value.copy(draft = next, undoAvailable = true, message = "")
        persist()
    }

    fun undo() {
        if (history.isNotEmpty()) {
            mutableState.value =
                mutableState.value.copy(draft = history.removeAt(history.lastIndex), undoAvailable = history.isNotEmpty())
            persist()
        }
    }

    fun select(sku: String) {
        saved["living_selected"] = sku
        mutableState.value = mutableState.value.copy(selected = sku, message = "")
    }

    fun place(
        plot: Int,
        moving: String? = null,
    ) {
        val state = mutableState.value
        val draft = state.draft ?: return
        val existing = draft.firstOrNull { it.id == moving }
        val sku = existing?.sku ?: state.selected
        val item = gardenCosmetics.firstOrNull { it.id == sku } ?: return
        val target = state.garden.plots.firstOrNull { it.id == plot }
        val tree = state.garden.trees.firstOrNull { it.id == target?.treeId }
        val base = draft.filterNot { it.id == moving }
        val error =
            when {
                item.slot == "THEME" -> AppText.text(TextKey.CopyChonChuDeTaiDienMaoBff149)
                item.slot != "AMBIENT" && target?.isUnlocked != true -> AppText.text(TextKey.CopyODangKhoa0b475c)
                item.slot == "TREE" && tree?.status?.alive != true -> AppText.text(TextKey.CopyChonOCoCayConSongC0f228)
                base.count { it.sku == sku } >= (state.snapshot.owned[sku] ?: 0) -> AppText.text(TextKey.CopyVatPhamDaDuocDatHet57767b)
                base.any {
                    it.plotId == (
                        if (item.slot ==
                            "AMBIENT"
                        ) {
                            0
                        } else {
                            plot
                        }
                    ) &&
                        gardenCosmetics.firstOrNull { c -> c.id == it.sku }?.slot == item.slot
                } -> AppText.text(TextKey.CopyONayDaCoVatPhamCungLoaiA47c7d)
                else -> null
            }
        if (error != null) {
            mutableState.value = state.copy(message = error)
            return
        }
        change(
            base +
                GardenPlacement(
                    existing?.id ?: java.util.UUID
                        .randomUUID()
                        .toString(),
                    sku,
                    if (item.slot == "AMBIENT")0 else plot,
                    if (item.slot == "TREE")tree!!.id else 0,
                ),
        )
    }

    fun remove(id: String) {
        change((mutableState.value.draft ?: return).filterNot { it.id == id })
    }

    fun save() {
        val s = mutableState.value
        act(
            LivingGardenAction.Layout(
                s.draftVersion,
                s.draft ?: return,
                java.util.UUID
                    .randomUUID()
                    .toString(),
            ),
        )
    }

    fun act(action: LivingGardenAction) = dispatch(LivingGardenUiAction(action))

    override fun handleAction(action: LivingGardenUiAction) {
        perform(action.mutation)
    }

    private fun perform(action: LivingGardenAction) {
        if (mutableState.value.busy || mutableState.value.loading && action !is LivingGardenAction.Discover)return
        mutableState.value = mutableState.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { storage.designGarden(action) }
                refresh()
                if (result.success && action is LivingGardenAction.Layout) {
                    cancel()
                }
                mutableState.value = mutableState.value.copy(message = result.message)
            } finally {
                mutableState.value = mutableState.value.copy(busy = false)
            }
        }
    }
}

private fun decodePlacement(value: String): GardenPlacement? {
    val parts = value.split('|')
    if (parts.size != 4) return null
    val plot = parts[2].toIntOrNull() ?: return null
    val rotation = parts[3].toLongOrNull() ?: return null
    return GardenPlacement(parts[0], parts[1], plot, rotation)
}
