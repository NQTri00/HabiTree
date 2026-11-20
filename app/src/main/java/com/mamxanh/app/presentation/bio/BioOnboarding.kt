package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingAction
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingDisplay
import com.mamxanh.app.feature.home.presentation.navigation.OnboardingRouteContent
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

internal fun onboardingRouteContent(world: BioWorld, ui: BioController, gutter: Int): OnboardingRouteContent {
    val templates = world.catalog.a("templates").objects()
    val category = ui.onboarding.category
    val chosen = ui.onboarding.selectedTemplates.toSet()
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
                    "seed" to ui.onboarding.seed,
                    "category" to ui.onboarding.category,
                    "ids" to ui.onboarding.selectedTemplates.joinToString(","),
                    "remind" to (ui.fields["onboard-remind"] ?: "false"),
                ),
            )
        }
    }

    return OnboardingRouteContent(
        state = OnboardingDisplay(
            step = ui.onboarding.step,
            seed = ui.onboarding.seed,
            category = category,
            selectedCount = chosen.size,
            selectedTitles = templates.filter { it.s("id") in chosen }.map { it.s("title") },
            seedNames = (listOf("succulent", "monstera", "snake") + ui.onboarding.seed).associateWith { world.species(it).s("name") },
            templateCount = byCategory.size,
            remind = ui.fields["onboard-remind"] == "true",
        ),
        gutter = gutter,
        onAction = { action ->
            when (action) {
                OnboardingAction.Back -> ui.onboarding.previousStep()
                OnboardingAction.Next -> if (!ui.onboarding.advance()) finish(skip = false)
                OnboardingAction.Skip -> finish(skip = true)
                is OnboardingAction.Choose -> if (action.step == 1) ui.onboarding.seed = action.id else ui.onboarding.category = action.id
                is OnboardingAction.Remind -> ui.fields["onboard-remind"] = action.enabled.toString()
            }
        },
        templateRow = { index ->
            val template = byCategory[index]
            val id = template.s("id")
            BioTemplateRow(template, id in chosen, onboarding = true) {
                if (!ui.onboarding.toggleTemplate(id)) {
                    ui.message = AppText.text(TextKey.CopyBanDaChon5ViecBoChonMotViecDeDoi95f1ae)
                }
            }
        },
    )
}
