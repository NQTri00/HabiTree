package com.mamxanh.app.feature.tasks.presentation.ui.rest

data class RestScreenState(
    val tomorrow: String,
    val scheduledDays: List<String>,
    val tickets: Int,
    val canBuy: Boolean,
)
