package com.mamxanh.app.feature.stats.presentation.di

import com.mamxanh.app.feature.stats.domain.LoadGardenStatistics
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val statsPresentationModule = module {
    factory { LoadGardenStatistics(get()) }
    viewModelOf(::StatsViewModel)
}
