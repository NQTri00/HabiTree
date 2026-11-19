package com.mamxanh.app.di

import com.mamxanh.app.core.config.HabiTreeConfigProvider
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.feature.inventory.data.di.inventoryDataModule
import com.mamxanh.app.feature.stats.data.di.statsDataModule
import com.mamxanh.app.feature.settings.data.di.settingsDataModule
import com.mamxanh.app.feature.settings.presentation.di.settingsPresentationModule
import com.mamxanh.app.feature.stats.presentation.di.statsPresentationModule
import com.mamxanh.app.feature.tree.presentation.di.treePresentationModule
import com.mamxanh.app.feature.shop.presentation.di.shopPresentationModule
import com.mamxanh.app.feature.garden.presentation.di.gardenPresentationModule
import com.mamxanh.app.feature.tasks.data.focusSessionDataModule
import com.mamxanh.app.feature.tasks.presentation.di.tasksPresentationModule
import com.mamxanh.app.feature.home.presentation.di.homePresentationModule
import com.mamxanh.app.feature.home.data.homeTreeSelectionDataModule
import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.di.sharedModule
import com.mamxanh.shared.network.NetworkConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(configProvider: HabiTreeConfigProvider = HabiTreeConfigProvider()) = module {
    includes(sharedModule(NetworkConfig(configProvider.config.apiBaseUrl)))
    single { configProvider }
    includes(*featureNavigation.map { it.navigationModule }.toTypedArray())
    includes(
        homeTreeSelectionDataModule,
        focusSessionDataModule,
        inventoryDataModule,
        statsDataModule,
        settingsDataModule,
    )
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
