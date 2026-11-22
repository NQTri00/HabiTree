package com.mamxanh.app.feature.home.data.di

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTaskSummary
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.domain.model.HomeTreeCare
import com.mamxanh.app.feature.home.domain.repository.OnboardingRepository
import com.mamxanh.app.feature.home.domain.usecase.HomeDashboardRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction
import org.koin.dsl.module
import com.mamxanh.shared.feature.home.domain.HomeDashboardRepository as SharedHomeDashboardRepository

private class AndroidHomeDashboardRepository(
    private val source: SharedHomeDashboardRepository,
) : HomeDashboardRepository {
    override fun load(): HomeDashboard {
        val snapshot = source.load()
        return HomeDashboard(
            trees =
                snapshot.trees.map { tree ->
                    val status = tree.status
                    HomeTree(
                        tree.id,
                        tree.speciesId,
                        tree.name,
                        tree.growth,
                        tree.decoration,
                        tree.wateredToday,
                        HomeTreeCare(
                            alive = status.alive,
                            waterActive = status.waterActive,
                            canWater = status.canWater,
                            canFertilize = status.canFertilize,
                            percentage = status.percentage,
                            stageProgress = status.stageProgress,
                            stageIndex = status.stageIndex,
                            stageCount = status.stageCount,
                            stageLabel = status.stageLabel,
                            stateLabel = status.stateLabel,
                            waterLabel = status.waterLabel,
                            fertilizerLabel = status.fertilizerLabel,
                        ),
                    )
                },
            completedTasks = snapshot.completedTasks,
            totalTasks = snapshot.totalTasks,
            seedCount = snapshot.seedCount,
            coins = snapshot.coins,
            equippedCan = snapshot.equippedCan,
            dailyRewardDate = snapshot.dailyRewardDate,
            dailyRewardCoins = snapshot.dailyRewardCoins,
            earnedToday = snapshot.phase.earnedToday,
            streak = snapshot.phase.streak,
            nextTasks =
                snapshot.nextTasks.map { task ->
                    HomeTaskSummary(
                        task.id,
                        task.title,
                        task.category,
                        task.reward,
                        timer = task.config.verification == "TIMER",
                        canComplete = task.available && !task.doneToday && !task.finished && task.lifecycle == "ACTIVE",
                    )
                },
            nextGoal = snapshot.nextGoal,
        )
    }
}

val homeDashboardDataModule =
    module {
        single<OnboardingRepository> {
            val storage = get<GameStorage>()
            OnboardingRepository { request ->
                val result =
                    storage.engage(
                        EngagementAction.Onboard(
                            request.seed,
                            request.category,
                            request.templateIds,
                            request.operationKey,
                            request.skip,
                            request.reminders,
                        ),
                    )
                if (result.success) {
                    RepositoryResult.Success(result.message)
                } else {
                    RepositoryResult.Error(result.message)
                }
            }
        }
        single<HomeDashboardRepository> { AndroidHomeDashboardRepository(get<SharedHomeDashboardRepository>()) }
    }
