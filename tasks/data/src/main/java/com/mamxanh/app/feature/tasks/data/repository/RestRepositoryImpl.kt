package com.mamxanh.app.feature.tasks.data.repository

import com.mamxanh.app.feature.tasks.domain.repository.RestRepository
import com.mamxanh.app.feature.tasks.domain.repository.RestSchedule
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction

internal class RestRepositoryImpl(
    private val storage: GameStorage,
) : RestRepository {
    override fun load(): RestSchedule {
        val snapshot = storage.engagementSnapshot()
        return RestSchedule(
            tomorrow = snapshot.tomorrow,
            days = snapshot.days.associate { it.date to it.state },
            tickets = snapshot.protectionTickets,
            canBuyTicket = storage.statistics().coins >= 250,
        )
    }

    override fun schedule(date: String) = execute(EngagementAction.Rest(date))

    override fun protect(
        date: String,
        key: String,
    ) = execute(EngagementAction.Protect(date, key))

    override fun buyTicket(key: String) = execute(EngagementAction.BuyProtection(key))

    private fun execute(action: EngagementAction): TaskOperationResult {
        val result = storage.engage(action)
        return TaskOperationResult(result.success, result.message)
    }
}
