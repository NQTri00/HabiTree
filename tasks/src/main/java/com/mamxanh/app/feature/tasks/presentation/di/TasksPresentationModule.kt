package com.mamxanh.app.feature.tasks.presentation.di

import com.mamxanh.app.feature.tasks.domain.FocusSessionCommands
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tasks.presentation.EngagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tasksPresentationModule = module {
    factory { FocusSessionCommands(get()) }
    viewModelOf(::TasksViewModel)
    viewModelOf(::EngagementViewModel)
}
