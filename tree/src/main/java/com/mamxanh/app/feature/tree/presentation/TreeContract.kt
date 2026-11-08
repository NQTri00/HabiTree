package com.mamxanh.app.feature.tree.presentation

data class TreeUiState(val isLoading: Boolean = false)

sealed interface TreeIntent {
    data class Water(val id: Long) : TreeIntent
    data class Fertilize(val id: Long) : TreeIntent
    data class Decorate(val id: Long, val decoration: String) : TreeIntent
}
sealed interface TreeEffect {
    data class Feedback(val message: String) : TreeEffect
    data object TreeChanged : TreeEffect
}
