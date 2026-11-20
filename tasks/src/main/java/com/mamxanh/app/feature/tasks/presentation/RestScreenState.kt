package com.mamxanh.app.feature.tasks.presentation

data class RestScreenState(
    val tomorrow: String,
    val scheduledDays: List<String>,
    val tickets: Int,
    val canBuy: Boolean,
)
