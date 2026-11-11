package com.mamxanh.app.feature.tree.presentation
import com.mamxanh.shared.model.ActionResult
data class TreeUiState(
    val isLoading: Boolean = false,val isPanelExpanded: Boolean = false,
    val dispositionId: Long? = null,val selling: Boolean = false,
    val quote: ActionResult? = null,val actionKey: String = ""
)
sealed interface TreeIntent {
    data object Back : TreeIntent
    data object OpenFertilizerShop : TreeIntent
    data class SetPanelExpanded(val expanded: Boolean) : TreeIntent
    data class Water(val id: Long) : TreeIntent
    data class Fertilize(val id: Long,val tier: String = "AUTO") : TreeIntent
    data class Decorate(val id: Long,val decoration: String) : TreeIntent
    data class AskDisposition(val id: Long,val sell: Boolean) : TreeIntent
    data object DismissDisposition : TreeIntent
    data object ConfirmDisposition : TreeIntent
}
sealed interface TreeEffect {
    data object Back : TreeEffect
    data object OpenFertilizerShop : TreeEffect
    data class Feedback(val message: String) : TreeEffect
    data object TreeChanged : TreeEffect
}
