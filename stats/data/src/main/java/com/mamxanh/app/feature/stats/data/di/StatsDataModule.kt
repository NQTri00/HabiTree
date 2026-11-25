package com.mamxanh.app.feature.stats.data.di

import com.mamxanh.app.feature.stats.data.repository.OfflineJournalRepositoryImpl
import com.mamxanh.app.feature.stats.data.repository.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.domain.usecase.AchievementProgress
import com.mamxanh.app.feature.stats.domain.usecase.AchievementRepository
import com.mamxanh.app.feature.stats.domain.usecase.JournalMemory
import com.mamxanh.app.feature.stats.domain.usecase.JournalRepository
import com.mamxanh.app.feature.stats.domain.usecase.StatisticsRepository
import com.mamxanh.app.feature.stats.domain.usecase.TimelineRepository
import com.mamxanh.app.feature.stats.domain.usecase.WeeklyJournalReview
import com.mamxanh.app.feature.stats.domain.usecase.WeeklyReviewRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import org.koin.dsl.module

val statsDataModule =
    module {
        single<AchievementRepository> {
            val storage = get<GameStorage>()
            AchievementRepository {
                storage.engagementSnapshot().achievements.map {
                    AchievementProgress(it.title, it.criterion, it.progress, it.target, it.unlockedAt > 0)
                }
            }
        }
        single<TimelineRepository> {
            val storage = get<GameStorage>()
            TimelineRepository {
                storage.engagementSnapshot().memories.map {
                    JournalMemory(
                        it.id,
                        it.at,
                        AppText.history(it.title),
                        TimelineRelated.fromStored(it.type, it.entity.toString()),
                        it.note,
                    )
                }
            }
        }
        single<WeeklyReviewRepository> {
            val storage = get<GameStorage>()
            val statistics = get<StatisticsRepository>()
            WeeklyReviewRepository {
                val snapshot = storage.engagementSnapshot()
                snapshot.review?.let {
                    WeeklyJournalReview(
                        it.start,
                        it.reflection,
                        it.completed,
                        it.scheduled,
                        it.focusMinutes.toInt(),
                        it.previousRate,
                        it.earned,
                        it.spent,
                        it.careDays,
                        it.milestones,
                        it.consistent,
                        it.improve,
                        snapshot.protectionTickets,
                        statistics.load().recentDailyCompletions,
                    )
                }
            }
        }
        single<JournalRepository> { OfflineJournalRepositoryImpl(get()) }
        single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
    }
