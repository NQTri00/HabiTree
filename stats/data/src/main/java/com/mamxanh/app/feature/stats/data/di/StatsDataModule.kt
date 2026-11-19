package com.mamxanh.app.feature.stats.data.di

import com.mamxanh.app.feature.stats.data.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import org.koin.dsl.module

val statsDataModule = module {
    single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
}
