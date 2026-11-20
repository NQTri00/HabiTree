package com.mamxanh.app.feature.home.domain.repository

import com.mamxanh.app.core.domain.RepositoryResult

data class OnboardingRequest(
    val seed: String,
    val category: String,
    val templateIds: List<String>,
    val operationKey: String,
    val skip: Boolean,
    val reminders: Boolean,
)

fun interface OnboardingRepository {
    fun finish(request: OnboardingRequest): RepositoryResult<String>
}
