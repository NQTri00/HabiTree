package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.home.presentation.OnboardingAction
import com.mamxanh.app.feature.home.presentation.OnboardingDisplay
import com.mamxanh.app.feature.home.presentation.navigation.OnboardingRouteContent
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

internal fun onboardingRouteContent(world: BioWorld, ui: BioController, gutter: Int): OnboardingRouteContent {
    val templates = world.catalog.a("templates").objects()
    val category = ui.selectedCategory
    val chosen = ui.selectedTemplates.toSet()
    val byCategory = templates.sortedBy { it.s("category") != category }

    fun finish(skip: Boolean) {
        if (world.native && world.state.b("onboarded")) {
            ui.go("home", root = true)
        } else if (skip) {
            ui.send("onboard", mapOf("skip" to "true"))
        } else {
            ui.send(
                "onboard",
                mapOf(
                    "seed" to ui.selectedSeed,
                    "category" to ui.selectedCategory,
                    "ids" to ui.selectedTemplates.joinToString(","),
                    "remind" to (ui.fields["onboard-remind"] ?: "false"),
                ),
            )
        }
    }

    return OnboardingRouteContent(
        state = OnboardingDisplay(
            step = ui.onboardingStep,
            seed = ui.selectedSeed,
            category = category,
            selectedCount = chosen.size,
            selectedTitles = templates.filter { it.s("id") in chosen }.map { it.s("title") },
            seedNames = (listOf("succulent", "monstera", "snake") + ui.selectedSeed).associateWith { world.species(it).s("name") },
            templateCount = byCategory.size,
            remind = ui.fields["onboard-remind"] == "true",
        ),
        gutter = gutter,
        onAction = { action ->
            when (action) {
                OnboardingAction.Back -> ui.onboardingStep = (ui.onboardingStep - 1).coerceAtLeast(0)
                OnboardingAction.Next -> if (ui.onboardingStep < 4) ui.onboardingStep++ else finish(skip = false)
                OnboardingAction.Skip -> finish(skip = true)
                is OnboardingAction.Choose -> if (action.step == 1) ui.selectedSeed = action.id else ui.selectedCategory = action.id
                is OnboardingAction.Remind -> ui.fields["onboard-remind"] = action.enabled.toString()
            }
        },
        templateRow = { index ->
            val template = byCategory[index]
            val id = template.s("id")
            BioTemplateRow(template, id in chosen, onboarding = true) {
                if (!ui.selectedTemplates.remove(id)) {
                    if (ui.selectedTemplates.size < 5) ui.selectedTemplates.add(id)
                    else ui.message = AppText.text(TextKey.CopyBanDaChon5ViecBoChonMotViecDeDoi95f1ae)
                }
            }
        },
    )
}
