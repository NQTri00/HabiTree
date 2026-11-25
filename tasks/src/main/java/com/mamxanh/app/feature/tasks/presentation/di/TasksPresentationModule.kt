package com.mamxanh.app.feature.tasks.presentation.di

import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplatesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tasksPresentationModule =
    module {
        factory { FocusSessionCommands(get()) }
        viewModelOf(::TasksViewModel)
        viewModelOf(::TaskListViewModel)
        viewModelOf(::RestViewModel)
        viewModelOf(::InsightsViewModel)
        viewModelOf(::TemplatesViewModel)
        viewModel { parameters -> TaskDetailViewModel(get(), parameters.get()) }
        viewModel { parameters -> TaskFormViewModel(get(), get(), parameters.get()) }
        viewModel { parameters -> FocusViewModel(get(), get(), parameters.get()) }
        viewModelOf(::EngagementViewModel)
    }
