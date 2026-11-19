package com.mamxanh.app.feature.settings.data.di

import com.mamxanh.app.feature.settings.data.OfflineReminderSettingsRepositoryImpl
import com.mamxanh.app.feature.settings.domain.ReminderSettingsRepository
import org.koin.dsl.module

val settingsDataModule = module {
    single<ReminderSettingsRepository> { OfflineReminderSettingsRepositoryImpl(get()) }
}
