package com.mamxanh.app.presentation.bio

import com.mamxanh.app.core.components.design.BioGrid
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.app.feature.tree.presentation.navigation.TreeRouteContent
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenFertilizer
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenState
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHero
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.FertilizerTier
import com.mamxanh.shared.model.catalog
import com.mamxanh.shared.model.gardenCosmetics

internal fun liveTreeContent(
    ui: BioController,
    id: String,
    compact: Boolean,
    live: LiveTree,
    accessoryCatalog: AccessorySnapshot?,
    selectHomeTree: (String) -> Unit,
): TreeRouteContent {
    val tree = live.state.detail?.takeIf { it.id.toString() == id }
    val cosmetics = gardenCosmetics
    val speciesCatalog = catalog
    val species = speciesCatalog.firstOrNull { it.id == tree?.speciesId } ?: speciesCatalog.first()
    val state =
        tree?.let {
            TreeScreenState(
                growth = it.growth,
                watered = it.watered,
                fertilizerNoteAllowed = it.canFertilize,
                canFertilize = it.canFertilize,
                busy = ui.busy || live.state.isLoading,
                hasDecoration = it.decoration.isNotBlank(),
                decorationName =
                    cosmetics
                        .firstOrNull { cosmetic -> cosmetic.id == it.decoration }
                        ?.name
                        .orEmpty()
                        .lowercase(),
                speciesName = species.name,
                stage = it.stage,
                stages = species.formCount,
                progress = it.progress,
                planted = it.planted,
                description = species.description,
                fertilizers =
                    FertilizerTier.entries.map { tier ->
                        TreeScreenFertilizer(
                            tier.itemId,
                            tier.label,
                            (tier.rate / 1000.0).toString(),
                            tier.hours,
                            it.quantities[tier.itemId] ?: 0,
                        )
                    },
            )
        }
    return TreeRouteContent(
        state = state,
        tab = live.state.tab,
        onTab = { live.dispatch(TreeAction.SelectTab(it)) },
        onAction = { action ->
            when (action) {
                TreeScreenAction.Encyclopedia -> ui.go("encyclopedia")
                TreeScreenAction.Water -> live.water()
                is TreeScreenAction.Fertilize -> live.fertilize(action.id)
                TreeScreenAction.BuyFertilizer -> live.dispatch(TreeAction.OpenFertilizerShop)
                TreeScreenAction.Unequip -> live.unequip()
                TreeScreenAction.BuyAccessory -> live.dispatch(TreeAction.OpenAccessoryShop)
                TreeScreenAction.Species -> tree?.let { ui.go("species", it.speciesId) }
                TreeScreenAction.Sell -> live.askDisposition(true)
                TreeScreenAction.Remove -> live.askDisposition(false)
            }
        },
        hero = {
            if (tree != null) {
                val stageLabel =
                    when (tree.growth) {
                        "dead" -> TextKey.CopyDaKhoHeoA9c1b9
                        "mature" -> TextKey.CopyTruongThanh8c14d7
                        else -> TextKey.CopyDangLon40daab
                    }
                val caption =
                    when (tree.growth) {
                        "dead" -> TextKey.CopyMamXanhDaDungLai6dabc6
                        "mature" -> TextKey.CopyDaTruongThanh54f165
                        else -> TextKey.CopyDangLonCungBan6913c2
                    }
                TreeHero(
                    TreeHeroState(
                        species = tree.speciesId,
                        name = tree.name,
                        stageLabel = AppText.text(stageLabel),
                        growthCaption = AppText.text(caption),
                        stage = tree.stage,
                        stages = species.formCount,
                        progress = tree.progress,
                        home = false,
                        compact = compact,
                        dead = tree.growth == "dead",
                        watered = tree.watered,
                        busy = ui.busy || live.state.isLoading,
                        selectedOnHome = ui.homeTreeState.selectedId == id,
                        canBrowseTrees = tree.treeIds.size > 1,
                        accessoryIcon = tree.decoration.takeIf { tree.accessoryOwned }?.let(::accessoryIcon),
                    ),
                ) { action ->
                    when (action) {
                        TreeHeroAction.ChooseTree -> ui.show("tree-picker")
                        TreeHeroAction.PinTree -> selectHomeTree(id)
                        TreeHeroAction.OpenTree -> ui.go("tree", id)
                        TreeHeroAction.PreviousTree -> live.dispatch(TreeAction.Browse(tree.id, forward = false))
                        TreeHeroAction.NextTree -> live.dispatch(TreeAction.Browse(tree.id, forward = true))
                        TreeHeroAction.Water -> live.water()
                        TreeHeroAction.Decorate -> live.dispatch(TreeAction.SelectTab("decor"))
                    }
                }
            }
        },
        accessories = {
            val products = cosmetics.filter { it.category == "TREE" }
            BioGrid(products.size) { index, modifier ->
                val item = products[index]
                BioAccessoryCard(
                    item.id,
                    item.name,
                    item.price,
                    accessoryCatalog?.owns(item.id) == true,
                    accessoryCatalog?.equipped(item.id) == true,
                    modifier,
                ) { live.dispatch(TreeAction.OpenAccessory(item.id)) }
            }
        },
    )
}
