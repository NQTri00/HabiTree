package com.mamxanh.app.feature.stats.presentation.di

import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import com.mamxanh.app.feature.stats.domain.usecase.SaveJournalNoteUseCase
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val statsPresentationModule =
    module {
        factory { LoadGardenStatistics(get()) }
        factory { SaveJournalNoteUseCase(get()) }
        viewModelOf(::StatsViewModel)
    }
