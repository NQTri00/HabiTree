package com.mamxanh.app.di

import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.feature.stats.presentation.di.statsPresentationModule
import com.mamxanh.app.feature.tree.presentation.di.treePresentationModule
import com.mamxanh.app.feature.shop.presentation.di.shopPresentationModule
import com.mamxanh.app.feature.garden.presentation.di.gardenPresentationModule
import com.mamxanh.app.feature.tasks.presentation.di.tasksPresentationModule
import com.mamxanh.app.feature.home.presentation.di.homePresentationModule
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
    includes(homePresentationModule, tasksPresentationModule, gardenPresentationModule, shopPresentationModule, treePresentationModule, statsPresentationModule)
    single { GardenDatabase(androidContext()) } onClose { it?.close() }
    single<GameDatabase> { get<GardenDatabase>() }

    viewModelOf(::AppViewModel)
}
