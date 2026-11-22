package com.mamxanh.app.feature.tasks.presentation.di

import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tasksPresentationModule =
    module {
        factory { FocusSessionCommands(get()) }
        viewModelOf(::TasksViewModel)
        viewModelOf(::EngagementViewModel)
    }
