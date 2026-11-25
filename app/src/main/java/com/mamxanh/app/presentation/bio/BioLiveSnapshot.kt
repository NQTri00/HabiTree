package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.garden.domain.model.GardenTreeGrowth
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.shared.data.GameStorage
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun liveBioData(reference: BioWorld): JSONObject =
    JSONObject().apply {
        for (key in listOf("catalog", "products", "icons", "palettes")) put(key, reference.data.opt(key))
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
        reminderScheduleInput = BioReminderScheduleInput(storage.tasks(), e.settings, e.reminders),
        liveTrees = g.trees.map { BioShellTree(it.id.toString(), it.speciesId, it.status.state, it.status.waterActive) },
        liveCoins = g.coins,
        liveOnboarded = e.onboarded,
    )
}

internal data class BioShellTree(
    val id: String,
    val species: String,
    val growth: GardenTreeGrowth,
    val watered: Boolean,
)

/** Native database/OS adapter. The presentation used here is also the preview target. */
