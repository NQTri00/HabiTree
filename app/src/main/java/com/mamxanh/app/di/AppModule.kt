package com.mamxanh.app.di

import com.mamxanh.app.core.config.HabiTreeConfigProvider
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.feature.catalog.data.repository.catalogDataModule
import com.mamxanh.app.feature.catalog.presentation.di.catalogPresentationModule
import com.mamxanh.app.feature.garden.data.di.gardenDataModule
import com.mamxanh.app.feature.garden.presentation.di.gardenPresentationModule
import com.mamxanh.app.feature.home.data.di.homeDashboardDataModule
import com.mamxanh.app.feature.home.data.di.homeTreeSelectionDataModule
import com.mamxanh.app.feature.home.presentation.di.homePresentationModule
import com.mamxanh.app.feature.inventory.data.di.inventoryDataModule
import com.mamxanh.app.feature.inventory.presentation.di.inventoryPresentationModule
import com.mamxanh.app.feature.settings.data.di.settingsDataModule
import com.mamxanh.app.feature.settings.domain.model.TaskReminderSettings
import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.settings.presentation.di.settingsPresentationModule
import com.mamxanh.app.feature.shop.data.di.shopDataModule
import com.mamxanh.app.feature.shop.presentation.di.shopPresentationModule
import com.mamxanh.app.feature.stats.data.di.statsDataModule
import com.mamxanh.app.feature.stats.presentation.di.statsPresentationModule
import com.mamxanh.app.feature.tasks.data.di.taskDataModule
import com.mamxanh.app.feature.tasks.data.repository.focusSessionDataModule
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.app.feature.tasks.domain.repository.TaskReminderWriter
import com.mamxanh.app.feature.tasks.presentation.di.tasksPresentationModule
import com.mamxanh.app.feature.tree.data.di.treeDataModule
import com.mamxanh.app.feature.tree.presentation.di.treePresentationModule
import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.di.sharedModule
import com.mamxanh.shared.network.NetworkConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(configProvider: HabiTreeConfigProvider = HabiTreeConfigProvider()) =
    module {
        includes(sharedModule(NetworkConfig(configProvider.config.apiBaseUrl)))
        single { configProvider }
        includes(*featureNavigation.map { it.navigationModule }.toTypedArray())
        includes(
            homeDashboardDataModule,
            homeTreeSelectionDataModule,
            focusSessionDataModule,
            taskDataModule,
            inventoryDataModule,
            inventoryPresentationModule,
            shopDataModule,
            treeDataModule,
            catalogDataModule,
            catalogPresentationModule,
            gardenDataModule,
            statsDataModule,
            settingsDataModule,
        )
        single<TaskReminderWriter> {
            val save = get<SaveReminderSettingsUseCase>()
            TaskReminderWriter { taskId, mode, minute, followUp ->
                when (val result = save(TaskReminderSettings(taskId, mode, minute, followUp))) {
                    is RepositoryResult.Success -> TaskOperationResult(true, result.value)
                    is RepositoryResult.Error -> TaskOperationResult(false, result.message)
                }
            }
        }
        single { HabiTreeRouteRegistry(getAll<HabiTreeFeatureDestination>()) }
        includes(
            homePresentationModule,
            tasksPresentationModule,
            gardenPresentationModule,
            shopPresentationModule,
            treePresentationModule,
            statsPresentationModule,
            settingsPresentationModule,
        )
        single { GardenDatabase(androidContext()) } onClose { it?.close() }
        single<GameDatabase> { get<GardenDatabase>() }
    }
