package com.mamxanh.app.feature.tasks.domain.repository

data class RestSchedule(
    val tomorrow: String,
    val days: Map<String, String>,
    val tickets: Int,
    val canBuyTicket: Boolean,
)
