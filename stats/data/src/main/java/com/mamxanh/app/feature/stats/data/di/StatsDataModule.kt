package com.mamxanh.app.feature.stats.data.di

import com.mamxanh.app.feature.stats.data.OfflineJournalRepositoryImpl
import com.mamxanh.app.feature.stats.data.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.domain.JournalRepository
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import org.koin.dsl.module

val statsDataModule = module {
    single<JournalRepository> { OfflineJournalRepositoryImpl(get()) }
    single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
}
