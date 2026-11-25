package com.mamxanh.app.feature.tasks.domain.repository

interface RestRepository {
    fun load(): RestSchedule

    fun schedule(date: String): TaskOperationResult

    fun protect(
        date: String,
        key: String,
    ): TaskOperationResult

    fun buyTicket(key: String): TaskOperationResult
}
