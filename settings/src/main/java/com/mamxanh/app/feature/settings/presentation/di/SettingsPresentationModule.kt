package com.mamxanh.app.feature.settings.presentation.di

import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import org.koin.dsl.module

val settingsPresentationModule =
    module {
        factory { SaveReminderSettingsUseCase(get()) }
    }
