package com.mamxanh.app.feature.home.presentation.di

import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule =
    module {
        viewModelOf(::HomeViewModel)
        viewModelOf(::OnboardingViewModel)
        factory { HomeTreeSelection(get()) }
    }
