package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenBrowseState
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingSelectionState
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingSelectionState
import com.mamxanh.app.feature.settings.presentation.SettingsRequestState
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopSelectionState
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskDraftState
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskScreenSelection
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeDetailSelection

internal class BioPreviewState {
    val treeDetail = TreeDetailSelection()
    val shopSelection = ShopSelectionState()
    val plantingSelection = PlantingSelectionState()
    val gardenBrowse = GardenBrowseState()
    val onboarding = OnboardingSelectionState()
    val taskDraft = TaskDraftState()
    val taskSelection = TaskScreenSelection()
    val settingsRequests = SettingsRequestState()
}
