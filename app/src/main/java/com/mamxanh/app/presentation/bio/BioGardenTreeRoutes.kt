package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.garden.presentation.navigation.GardenRouteContent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenAction
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenBrowseState
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenDisplayPlot
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenDisplayState
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenDisplayTree
import com.mamxanh.app.feature.tree.presentation.navigation.TreeRouteContent
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenFertilizer
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenState
import org.json.JSONObject

internal fun gardenRouteContent(
    world: BioWorld,
    ui: BioController,
): GardenRouteContent {
    val open = world.state.i("unlocked", 5)
    val filter = ui.gardenBrowse.filter

    fun thirsty(tree: JSONObject) = GardenBrowseState.isThirsty(tree.s("state"), tree.b("watered"))
    val trees = world.trees
    val byPlot = trees.groupBy { it.i("plot") }
    val plots =
        buildList {
            for (plot in 1..open) {
                val tree = byPlot[plot]?.firstOrNull()
                if (!ui.gardenBrowse.includes(tree?.s("state"), tree?.b("watered") == true)) continue
                val displayTree =
                    tree?.let {
                        GardenDisplayTree(
                            id = it.s("id"),
                            species = it.s("species"),
                            name = it.s("name"),
                            progress = it.i("progress"),
                            stageLabel = treeState(it),
                            dead = it.s("state") == "dead",
                            watered = it.b("watered"),
                        )
                    }
                add(GardenDisplayPlot(plot, displayTree))
            }
            if (filter == "all" && open < 24) {
                add(GardenDisplayPlot(open + 1, price = 400 + (open - 5).coerceAtLeast(0) * 200))
            }
        }
    return GardenRouteContent(
        state = GardenDisplayState(filter, trees.size, world.emptyPlots().size, world.species.size, trees.any(::thirsty), plots),
        onAction = { action ->
            when (action) {
                GardenAction.Inventory -> ui.go("inventory")
                GardenAction.WaterAll -> ui.show("water-all")
                GardenAction.PickSeed -> ui.show("seed-picker")
                GardenAction.Encyclopedia -> ui.go("encyclopedia")
                is GardenAction.Filter -> ui.gardenBrowse.filter = action.value
                is GardenAction.Plot -> {
                    val plot = action.value
                    val tree = plot.tree
                    when {
                        tree != null -> ui.go("tree", tree.id)
                        plot.price != null -> ui.show("unlock", plot.number.toString())
                        else -> {
                            ui.plantingSelection.select(plot.number.toString())
                            ui.show("seed-picker")
                        }
                    }
                }
            }
        },
    )
}

internal fun treeRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    compact: Boolean,
): TreeRouteContent {
    val tree = world.tree(argument)
    val id = tree?.s("id").orEmpty()
    val speciesId = tree?.s("species").orEmpty()
    val state =
        tree?.let {
            val species = world.species(speciesId)
            TreeScreenState(
                growth = it.s("state"),
                watered = it.b("watered"),
                fertilizerNoteAllowed = it.b("canFertilize", true),
                canFertilize = it.b("canFertilize", it.b("watered")),
                busy = ui.busy,
                hasDecoration = it.s("decor").isNotBlank(),
                decorationName = world.product(it.s("decor")).s("name").lowercase(),
                speciesName = species.s("name"),
                stage = it.i("stage"),
                stages = species.i("stages"),
                progress = it.i("progress"),
                planted = it.s("planted"),
                description = species.s("description"),
                fertilizers =
                    world.products.filter { product -> !product.b("permanent") }.map { product ->
                        val productId = product.s("id")
                        TreeScreenFertilizer(productId, product.s("name"), product.s("rate"), product.i("hours"), world.count(productId))
                    },
            )
        }
    return TreeRouteContent(
        state = state,
        tab = ui.treeDetail.tab,
        onTab = { ui.treeDetail.tab = it },
        onAction = { action ->
            when (action) {
                TreeScreenAction.Encyclopedia -> ui.go("encyclopedia")
                TreeScreenAction.Water -> ui.send("water:$id")
                is TreeScreenAction.Fertilize -> ui.send("fertilize:$id:${action.id}")
                TreeScreenAction.BuyFertilizer -> ui.act("shop-care")
                TreeScreenAction.Unequip -> ui.send("unequip:$id")
                TreeScreenAction.BuyAccessory -> ui.act("bio-accessory-shop:$id")
                TreeScreenAction.Species -> ui.go("species", speciesId)
                TreeScreenAction.Sell -> ui.show("sell", id)
                TreeScreenAction.Remove -> ui.show("remove", id)
            }
        },
        hero = { if (tree != null) BioHero(world, ui, tree, short = compact) },
        accessories = {
            val products = world.accessories
            BioGrid(products.size) { index, modifier -> BioProduct(world, ui, products[index], modifier, target = id) }
        },
    )
}
