package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiEvent

sealed interface GardenEvent : UiEvent {
    data object DataChanged : GardenEvent

    data object OpenInventory : GardenEvent

    data object OpenCatalog : GardenEvent

    data object ConfirmWaterAll : GardenEvent

    data class PickSeed(
        val plotId: Int? = null,
    ) : GardenEvent

    data class ConfirmUnlock(
        val plotId: Int,
    ) : GardenEvent

    data class MutationFinished(
        val success: Boolean,
        val message: String,
    ) : GardenEvent

    data class OpenTree(
        val id: Long,
    ) : GardenEvent

    data object OpenShop : GardenEvent
}
