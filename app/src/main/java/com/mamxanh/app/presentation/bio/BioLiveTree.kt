package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeEvent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.app.feature.tree.presentation.navigation.TreeRouteContent
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeEvent
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeState
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindTreeHomeSelection(
    world: BioWorld,
    ui: BioController,
): HomeViewModel {
    val model = koinViewModel<HomeViewModel>(key = "tree-picker-home")
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val pickerOpen = ui.modal == "tree-picker"
    LaunchedEffect(model, world, pickerOpen) {
        if (pickerOpen && !world.loading) model.dispatch(HomeAction.Refresh)
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (event is HomeEvent.TreeSelected) {
                    ui.homeTreeState.selectedId = event.id
                    if (ui.route == "tree" && ui.modal == "tree-picker") ui.modal = ""
                } else if (event is HomeEvent.Feedback && ui.route == "tree" && ui.modal == "tree-picker") {
                    ui.modalMessage = event.message
                }
            }
        }
    }
    return model
}

@Composable
internal fun liveTreeRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    compact: Boolean,
    accessoryCatalog: AccessorySnapshot?,
    selectHomeTree: (String) -> Unit,
): TreeRouteContent {
    val model = koinViewModel<TreeViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world, argument) {
        argument.toLongOrNull()?.let { model.dispatch(TreeAction.Load(it)) }
    }
    val requestedTab = ui.pendingTreeTab
    LaunchedEffect(model, argument, requestedTab) {
        if (requestedTab != null && ui.route == "tree" && ui.param == argument) {
            model.dispatch(TreeAction.SelectTab(requestedTab))
            ui.pendingTreeTab = null
        }
    }
    LaunchedEffect(model, lifecycle, ui, argument) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "tree" || ui.param != argument) return@collect
                when (event) {
                    TreeEvent.Back -> ui.back()
                    is TreeEvent.BrowseTo -> {
                        ui.browseTree(event.fromId.toString(), event.treeId.toString())
                    }
                    TreeEvent.OpenFertilizerShop -> ui.act("shop-care")
                    is TreeEvent.ConfirmDisposition -> {
                        val modal = if (event.sell) "sell" else "remove"
                        if (event.id.toString() == argument && (ui.modal != modal || ui.modalId != argument)) {
                            ui.show(modal, argument)
                        }
                    }
                    is TreeEvent.Disposed -> {
                        ui.modal = ""
                        ui.go("garden", root = true)
                        ui.message = event.message
                    }
                    is TreeEvent.Feedback -> {
                        if (ui.modal == "sell" || ui.modal == "remove") ui.modalMessage = event.message else ui.message = event.message
                    }
                }
            }
        }
    }
    return liveTreeContent(
        world,
        ui,
        argument,
        compact,
        LiveTree(argument.toLongOrNull(), state, model::dispatch),
        accessoryCatalog,
        selectHomeTree,
    )
}

internal class LiveTree(
    private val treeId: Long?,
    val state: TreeState,
    val dispatch: (TreeAction) -> Unit,
) {
    fun water() {
        treeId?.let { dispatch(TreeAction.Water(it)) }
    }

    fun fertilize(tier: String) {
        treeId?.let { dispatch(TreeAction.Fertilize(it, tier)) }
    }

    fun unequip() {
        treeId?.let { dispatch(TreeAction.Unequip(it)) }
    }

    fun askDisposition(sell: Boolean) {
        treeId?.let { dispatch(TreeAction.AskDisposition(it, sell)) }
    }
}
