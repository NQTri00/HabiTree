package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.shared.data.GameStorage
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun liveBioData(reference: BioWorld): JSONObject =
    JSONObject().apply {
        put("palettes", reference.data.opt("palettes"))
        put("date", SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    }

internal fun loadingNativeWorld(reference: BioWorld): BioWorld = BioWorld(liveBioData(reference), loading = true, native = true)

internal fun nativeWorld(
    storage: GameStorage,
    reference: BioWorld,
    garden: GardenRepository,
): BioWorld {
    val g = garden.loadGarden()
    val e = storage.engagementSnapshot()
    return BioWorld(
        liveBioData(reference),
        native = true,
        liveTrees = g.trees.map { BioShellTree(it.id.toString(), it.speciesId) },
        liveCoins = g.coins,
        liveOnboarded = e.onboarded,
    )
}

internal data class BioShellTree(
    val id: String,
    val species: String,
)

/** Native database/OS adapter. The presentation used here is also the preview target. */
