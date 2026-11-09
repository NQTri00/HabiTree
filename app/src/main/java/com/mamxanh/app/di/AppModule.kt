package com.mamxanh.app.di

import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.garden.data.OfflineGardenRepository
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.home.data.OfflineHomeDashboardRepository
import com.mamxanh.app.feature.home.domain.HomeDashboardRepository
import com.mamxanh.app.feature.home.presentation.HomeViewModel
import com.mamxanh.app.feature.shop.data.OfflineShopRepository
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import com.mamxanh.app.feature.stats.data.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import com.mamxanh.app.feature.tasks.data.OfflineTaskRepository
import com.mamxanh.app.feature.tasks.domain.TaskRepository
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tree.data.OfflineTreeRepository
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.app.feature.tree.presentation.TreeViewModel
import com.mamxanh.app.presentation.ui.AppViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { GardenDatabase(androidContext()) }
    single { GameStorage(get()) }
    single<GardenRepository> { OfflineGardenRepository(get()) }
    single<HomeDashboardRepository> { OfflineHomeDashboardRepository(get()) }
    single<ShopRepository> { OfflineShopRepository(get()) }
    single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
    single<TaskRepository> { OfflineTaskRepository(get()) }
    single<TreeRepository> { OfflineTreeRepository(get()) }

    viewModelOf(::GardenViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ShopViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::TasksViewModel)
    viewModelOf(::TreeViewModel)
    viewModelOf(::AppViewModel)
}
