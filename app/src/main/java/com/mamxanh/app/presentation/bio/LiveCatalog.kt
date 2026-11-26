package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CatalogAction
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CatalogEvent
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CatalogState
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CatalogViewModel
import com.mamxanh.shared.model.lifecycleProfiles
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindLiveCatalog(
    world: BioWorld,
    ui: BioController,
): LiveCatalog {
    val model = koinViewModel<CatalogViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val speciesId =
        ui.param.takeIf { ui.route == "species" }?.let { argument ->
            lifecycleProfiles.firstOrNull { it.id == argument }?.id ?: lifecycleProfiles.first().id
        }
    val active = ui.route == "encyclopedia" || ui.route == "species"
    LaunchedEffect(model, world, speciesId, active) {
        if (active && !world.loading) model.dispatch(CatalogAction.Refresh(speciesId))
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "encyclopedia" && ui.route != "species") return@collect
                when (event) {
                    is CatalogEvent.OpenSpecies -> ui.go("species", event.id)
                    is CatalogEvent.OpenTree -> ui.go("tree", event.id)
                    CatalogEvent.OpenSeeds -> {
                        ui.requestShopTab("seeds")
                        ui.go("shop")
                    }
                    is CatalogEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    return LiveCatalog(state, model::dispatch)
}

internal class LiveCatalog(
    val state: CatalogState,
    private val dispatch: (CatalogAction) -> Unit,
) {
    fun openSpecies(id: String) = dispatch(CatalogAction.OpenSpecies(id))

    fun openTree(id: String) = dispatch(CatalogAction.OpenTree(id))

    fun openSeeds() = dispatch(CatalogAction.OpenSeeds)
}
