package com.mamxanh.app.feature.stats.domain.model

sealed interface TimelineRelated {
    val entityId: String

    data class Tree(
        override val entityId: String,
    ) : TimelineRelated

    data class TaskInsights(
        override val entityId: String,
    ) : TimelineRelated

    companion object {
        fun fromStored(
            type: String,
            entityId: String,
        ): TimelineRelated? {
            if ((entityId.toLongOrNull() ?: 0L) <= 0L) return null
            return if (type.startsWith("TREE")) Tree(entityId) else TaskInsights(entityId)
        }
    }
}
