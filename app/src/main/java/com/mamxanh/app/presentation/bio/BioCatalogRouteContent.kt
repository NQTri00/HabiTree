package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.catalog.presentation.model.SpeciesTreeDisplay
import com.mamxanh.app.feature.catalog.presentation.model.speciesDisplay
import com.mamxanh.app.feature.catalog.presentation.navigation.EncyclopediaRouteContent
import com.mamxanh.app.feature.catalog.presentation.navigation.SpeciesRouteContent
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CollectionGoalDisplay
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.toEncyclopediaGoals
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.catalog
import java.text.SimpleDateFormat
import java.util.*

internal fun encyclopediaRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveCatalog? = null,
): EncyclopediaRouteContent {
    val species = if (live == null) emptyList() else catalog
    val collection = live?.state?.collection
    return EncyclopediaRouteContent(
        discovered =
            if (live == null) {
                world.records.count { it.b("discovered") }
            } else {
                collection?.species?.count { it.discovered } ?: 0
            },
        speciesCount = if (live == null) world.species.size else species.size,
        goals =
            if (live != null) {
                collection?.milestones?.toEncyclopediaGoals().orEmpty()
            } else {
                world.data.a("goals").objects().filter { it.s("id") !in listOf("expansion", "section") }.map {
                    CollectionGoalDisplay(it.s("title"), it.i("n"), it.i("target", 1), it.s("reward"))
                }
            },
        item = { index, modifier ->
            if (live == null) {
                BioProduct(world, ui, world.species[index], modifier, encyclopedia = true)
            } else {
                val item = species[index]
                val entry = live.state.species(item.id)
                BioProductCard(
                    BioProductDisplay(item.id, item.name, item.price, seed = true, tier = item.tier),
                    modifier,
                    quantity = 0,
                    equippedCan = "",
                    catalogStatus = entry?.status ?: AppText.text(TextKey.CopyChuaKhamPhaC0b0cb),
                    known = entry != null && (entry.discovered || entry.planted > 0 || entry.ownedSeeds > 0),
                ) { live.openSpecies(item.id) }
            }
        },
    )
}

internal fun speciesRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    live: LiveCatalog? = null,
): SpeciesRouteContent {
    val reference = if (live == null) world.species(argument) else org.json.JSONObject()
    val species = if (live == null) null else catalog.let { entries -> entries.firstOrNull { it.id == argument } ?: entries.first() }
    val id = species?.id ?: reference.s("id")
    val record = if (live == null) world.record(id) else org.json.JSONObject()
    val collected = live?.state?.species(id)
    val trees =
        if (live == null) {
            world.trees.filter { it.s("species") == id }.map {
                SpeciesTreeDisplay(it.s("id"), it.s("name"), living = it.s("state") != "dead", stage = it.i("stage"))
            }
        } else {
            live.state.trees.takeIf { live.state.treeSpeciesId == id }.orEmpty().map {
                SpeciesTreeDisplay(it.id.toString(), it.name, living = it.living, stage = it.stage)
            }
        }
    val noRecord = AppText.text(TextKey.CopyChuaGhiNhanF7654d)

    fun recordDate(timestamp: Long?): String =
        timestamp
            ?.takeIf { it > 0 }
            ?.let { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it)) } ?: noRecord
    return SpeciesRouteContent(
        state =
            speciesDisplay(
                id = id,
                name = species?.name ?: reference.s("name"),
                description = species?.description ?: reference.s("description"),
                tier = bioTier(species?.tier ?: reference.s("tier")),
                status = record.s("status", AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)),
                discovered = record.b("discovered"),
                highest = record.i("highest"),
                stages = species?.formCount ?: reference.i("stages"),
                planted = record.i("planted"),
                owned = if (live == null) world.count(id) else collected?.ownedSeeds ?: 0,
                firstPlanted = if (live == null) record.s("firstPlanted", noRecord) else recordDate(collected?.firstPlanted),
                firstMatured = if (live == null) record.s("firstMatured", noRecord) else recordDate(collected?.firstMatured),
                trees = trees,
                collection = collected,
            ),
        onTree = { if (live == null) ui.go("tree", it) else live.openTree(it) },
        onShop = {
            if (live == null) {
                ui.shopSelection.shopTab = "seeds"
                ui.go("shop")
            } else {
                live.openSeeds()
            }
        },
    )
}
