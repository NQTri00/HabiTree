package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenBrowseState
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingSelectionState
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingSelectionState
import com.mamxanh.app.feature.settings.presentation.SettingsRequestState
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopPendingAction
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
    var busy by mutableStateOf(false)
    var purchase by mutableStateOf<ShopPendingAction?>(null)
    var expanded = false
    var scrollBottom = false
    var sheetBottom = false
    var insightDays by mutableStateOf("7")
    var onboardingReminders by mutableStateOf(false)

    fun setTab(
        key: String,
        value: String,
    ) {
        when (key) {
            TaskScreenSelection.FILTER_KEY -> taskSelection.filter = value
            GardenBrowseState.SAVED_FILTER_KEY -> gardenBrowse.filter = value
            "tree" -> treeDetail.tab = value
            "shop" -> shopSelection.shopTab = value
            "inventory" -> shopSelection.inventoryTab = value
            "insightDays" -> insightDays = value
        }
    }

    fun restoreFields(values: Map<String, String>) {
        val remaining = settingsRequests.restoreFields(taskSelection.restoreFields(taskDraft.restore(values, false)))
        onboardingReminders = remaining["onboard-remind"] == "true"
    }

    fun clearFields() {
        taskDraft.clear()
        taskSelection.clearFields()
        settingsRequests.clear()
        onboardingReminders = false
    }
}
