package com.mamxanh.app.feature.stats.data.di

import com.mamxanh.app.feature.stats.data.repository.OfflineJournalRepositoryImpl
import com.mamxanh.app.feature.stats.data.repository.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.domain.usecase.JournalRepository
import com.mamxanh.app.feature.stats.domain.usecase.StatisticsRepository
import org.koin.dsl.module

val statsDataModule = module {
    single<JournalRepository> { OfflineJournalRepositoryImpl(get()) }
    single<StatisticsRepository> { OfflineStatisticsRepository(get()) }
}
