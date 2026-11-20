package com.mamxanh.app.feature.home.presentation.ui.home

data class HomeScreenState(
    val date: String,
    val completed: Int,
    val required: Int,
    val earned: Int,
    val streak: Int,
)
