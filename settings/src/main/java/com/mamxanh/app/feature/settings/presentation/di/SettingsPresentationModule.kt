package com.mamxanh.app.feature.settings.presentation.di

import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupViewModel
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsPresentationModule =
    module {
        factory { SaveReminderSettingsUseCase(get()) }
        viewModelOf(::SettingsViewModel)
        viewModelOf(::BackupViewModel)
    }
