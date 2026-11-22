package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.mamxanh.app.core.components.design.bioLocale
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollectionRepository
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.usecase.availablePlantingPlots
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tasks.presentation.ui.focus.focusSessionTitle
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.*
import java.text.SimpleDateFormat
import java.util.*

private fun array(items: Iterable<Any?>) = JSONArray().apply { items.forEach { put(it) } }

private fun mapJson(map: Map<String, *>) = JSONObject().apply { map.forEach { (k, v) -> put(k, v) } }

private fun date(at: Long) =
    if (at <= 0)AppText.text(TextKey.CopyChuaGhiNhanF7654d)else SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(at))

internal fun nativeWorld(
    storage: GameStorage,
    reference: BioWorld,
    service: BackupService,
    inventory: InventoryRepository,
    garden: GardenRepository,
    collectionRepository: GardenCollectionRepository,
    taskRepository: TaskRepository,
    loadStatistics: LoadGardenStatistics,
): BioWorld {
    val data = JSONObject(reference.data.toString())
    val g = garden.loadGarden()
    val tasks = taskRepository.observeSnapshot()
    val e = storage.engagementSnapshot()
    val collection = collectionRepository.load()
    val statistics = loadStatistics()
    val phase = taskRepository.snapshot()
    val now = System.currentTimeMillis()
    val today = date(now)
    val trees =
        g.trees.map { t ->
            val decoration = collection.decorationsByTree[t.id].orEmpty()
            json(
                "id" to t.id,
                "species" to t.speciesId,
                "name" to AppText.plantName(t.speciesId, t.name),
                "plot" to (g.plots.firstOrNull { it.treeId == t.id }?.id ?: 0),
                "state" to
                    t.status.state.name
                        .lowercase(),
                "stage" to t.status.stageIndex + 1,
                "progress" to t.status.percentage,
                "watered" to t.status.waterActive,
                "planted" to t.planted,
                "decor" to decoration,
                "canFertilize" to t.status.canFertilize,
                "fertilized" to t.status.fertilizerId,
            )
        }
    val taskJson =
        tasks.map { t ->
            json(
                "id" to t.id,
                "title" to t.title,
                "kind" to t.kind,
                "category" to t.category,
                "verification" to t.config.verification,
                "minutes" to t.config.minutes,
                "frequency" to t.frequency,
                "description" to t.config.description,
                "days" to t.config.days,
                "due" to t.due,
                "start" to clockLabel(t.config.startMinute),
                "end" to clockLabel(t.config.endMinute),
                "reminder" to clockLabel(t.config.reminderMinute),
                "done" to t.doneToday,
                "finished" to t.finished,
                "lifecycle" to t.lifecycle,
                "scheduledToday" to t.scheduledToday,
                "available" to t.available,
                "availability" to t.availability,
                "streak" to t.streak,
                "lastDone" to t.lastDone,
                "history" to array(t.history.map { json("date" to it.date, "nominal" to it.nominal, "granted" to it.granted) }),
            )
        }
    val focus = e.focus.firstOrNull { it.state in listOf("RUNNING", "PAUSED", "BREAK") }
    val reminders =
        JSONObject().apply {
            e.reminders.forEach {
                put(
                    it.taskId.toString(),
                    json(
                        "mode" to it.mode,
                        "minute" to clockLabel(it.minute),
                        "followUp" to it.followUp,
                    ),
                )
            }
        }
    val state =
        json(
            "coins" to g.coins,
            "earned" to phase.earnedToday,
            "claimed" to taskRepository.isDailyBonusClaimed(),
            "trees" to array(trees),
            "tasks" to array(taskJson),
            "inventory" to mapJson(g.inventory),
            "equipped" to g.equippedCan,
            "owned" to mapJson(collection.ownedAccessories),
            "unlocked" to g.plots.count { it.isUnlocked },
            "openPlots" to array(g.plots.filter { it.isUnlocked }.map { it.id }),
            "settings" to
                json(
                    "paused" to e.settings.paused,
                    "treeReminders" to e.settings.treeReminders,
                    "breakPrompts" to e.settings.breakPrompts,
                    "reducedMotion" to e.settings.reducedMotion,
                    "quietStart" to clockLabel(e.settings.quietStart),
                    "quietEnd" to clockLabel(e.settings.quietEnd),
                ),
            "reminders" to reminders,
            "templates" to array(e.templates),
            "tickets" to e.protectionTickets,
            "rest" to array(e.days.filter { it.state == "REST" }.map { it.date }),
            "protected" to array(e.days.filter { it.state == "PROTECTED" }.map { it.date }),
            "bytes" to service.sizeBytes(),
            "safety" to service.hasSafety(),
            "onboarded" to e.onboarded,
            "focusHistory" to
                array(
                    e.focus.filter { it.state == "COMPLETE" }.map {
                        json(
                            "title" to focusSessionTitle(it.taskId, it.title),
                            "date" to it.date,
                        )
                    },
                ),
            "memories" to
                array(
                    e.memories.map {
                        json(
                            "id" to it.id,
                            "title" to AppText.history(it.title),
                            "type" to it.type,
                            "entity" to it.entity,
                            "at" to it.at,
                            "date" to
                                java.text.DateFormat
                                    .getDateTimeInstance(
                                        java.text.DateFormat.SHORT,
                                        java.text.DateFormat.SHORT,
                                        bioLocale(),
                                    ).format(Date(it.at)),
                            "note" to it.note,
                        )
                    },
                ),
            "notes" to JSONObject(),
        )
    if (focus !=
        null
    ) {
        state.put(
            "focus",
            json(
                "id" to focus.id,
                "title" to focusSessionTitle(focus.taskId, focus.title),
                "state" to focus.state,
                "remaining" to focus.remaining,
                "break" to (focus.state == "BREAK"),
            ),
        )
    }
    data.put("state", state)
    data.put("date", today)
    data.put("tomorrow", e.tomorrow)
    data.put(
        "phase",
        json(
            "required" to phase.required,
            "completed" to phase.completed,
            "streak" to phase.streak,
            "canClaim" to taskRepository.canClaimDailyBonus(),
        ),
    )
    data.put(
        "statistics",
        json(
            "total" to statistics.total,
            "today" to statistics.today,
            "week" to statistics.week,
            "streak" to statistics.streak,
            "treeCount" to statistics.treeCount,
            "recentDailyCompletions" to array(statistics.recentDailyCompletions),
        ),
    )
    data.put(
        "records",
        array(
            collection.species.map { r ->
                json(
                    "id" to r.id,
                    "seeds" to r.ownedSeeds,
                    "planted" to r.planted,
                    "discovered" to r.discovered,
                    "highest" to r.highestStage,
                    "mature" to (r.firstMatured > 0),
                    "bloom" to r.flourished,
                    "status" to r.status,
                    "firstPlanted" to date(r.firstPlanted),
                    "firstMatured" to date(r.firstMatured),
                )
            },
        ),
    )
    data.put(
        "goals",
        array(
            collection.milestones.map { g0 ->
                json(
                    "id" to g0.id,
                    "title" to g0.title,
                    "n" to g0.progress,
                    "target" to g0.target,
                    "reward" to g0.reward,
                    "unlocked" to g0.unlocked,
                )
            },
        ),
    )
    data.put(
        "achievements",
        array(
            e.achievements.map { a ->
                json(
                    "id" to a.id,
                    "title" to a.title,
                    "criterion" to a.criterion,
                    "progress" to a.progress,
                    "target" to a.target,
                    "unlockedAt" to a.unlockedAt,
                )
            },
        ),
    )
    data.put("days", array(e.days.map { json("date" to it.date, "state" to it.state) }))
    data.put(
        "insights",
        array(
            e.insights.map { i ->
                json(
                    "taskId" to i.taskId,
                    "longest" to i.longest,
                    "rate" to i.rate,
                    "focusMinutes" to i.focusMinutes,
                    "commonHour" to i.commonHour,
                    "suggestion" to i.suggestion,
                    "days" to array(i.days.map { json("date" to it.date, "state" to it.state) }),
                )
            },
        ),
    )
    data.put(
        "review",
        e.review?.let { r ->
            json(
                "start" to r.start,
                "scheduled" to r.scheduled,
                "completed" to r.completed,
                "previousRate" to r.previousRate,
                "focusMinutes" to r.focusMinutes,
                "earned" to r.earned,
                "spent" to r.spent,
                "careDays" to r.careDays,
                "milestones" to r.milestones,
                "consistent" to r.consistent,
                "improve" to r.improve,
                "reflection" to r.reflection,
            )
        }
            ?: JSONObject(),
    )
    return BioWorld(
        data,
        native = true,
        inventoryQuantities = inventory.quantities(),
        gardenCollection = collection,
        plantingPlots = g.availablePlantingPlots(),
    )
}

/** Native database/OS adapter. The presentation used here is also the preview target. */
