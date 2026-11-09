package com.mamxanh.shared.di

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.garden.data.OfflineGardenRepository
import com.mamxanh.shared.feature.garden.domain.GardenRepository
import com.mamxanh.shared.feature.home.data.OfflineHomeDashboardRepository
import com.mamxanh.shared.feature.home.domain.HomeDashboardRepository
import com.mamxanh.shared.feature.shop.data.OfflineShopRepository
import com.mamxanh.shared.feature.shop.domain.ShopRepository
import com.mamxanh.shared.feature.stats.data.OfflineStatisticsRepository
import com.mamxanh.shared.feature.stats.domain.StatisticsRepository
import com.mamxanh.shared.feature.tasks.data.OfflineTaskRepository
import com.mamxanh.shared.feature.tasks.domain.TaskRepository
import com.mamxanh.shared.feature.tree.data.OfflineTreeRepository
import com.mamxanh.shared.feature.tree.domain.TreeRepository
import com.mamxanh.shared.network.NetworkConfig
import com.mamxanh.shared.network.RemoteService
import com.mamxanh.shared.network.createHttpClient
import org.koin.dsl.module
import org.koin.dsl.onClose

fun sharedModule(networkConfig: NetworkConfig = NetworkConfig()) = module {
    single { GameStorage(get()) }
    single<GardenRepository> { OfflineGardenRepository(get()) }
    single<HomeDashboardRepository> { OfflineHomeDashboardRepository(get()) }
    single<ShopRepository> { OfflineShopRepository(get()) }
    single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
    single<TaskRepository> { OfflineTaskRepository(get()) }
    single<TreeRepository> { OfflineTreeRepository(get()) }
    single { networkConfig }
    single { createHttpClient() } onClose { it?.close() }
    single { RemoteService(get(), get()) }
}
