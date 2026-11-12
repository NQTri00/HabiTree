package com.mamxanh.app.di

import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.garden.presentation.LivingGardenViewModel
import com.mamxanh.app.feature.home.presentation.HomeViewModel
import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tasks.presentation.EngagementViewModel
import com.mamxanh.app.feature.tree.presentation.TreeViewModel
import com.mamxanh.app.presentation.ui.AppViewModel
import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.di.sharedModule
import com.mamxanh.shared.network.NetworkConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(networkConfig: NetworkConfig = NetworkConfig()) = module {
    includes(sharedModule(networkConfig))
    single { GardenDatabase(androidContext()) } onClose { it?.close() }
    single<GameDatabase> { get<GardenDatabase>() }

    viewModelOf(::GardenViewModel)
    viewModelOf(::LivingGardenViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ShopViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::TasksViewModel)
    viewModelOf(::EngagementViewModel)
    viewModelOf(::TreeViewModel)
    viewModelOf(::AppViewModel)
}
