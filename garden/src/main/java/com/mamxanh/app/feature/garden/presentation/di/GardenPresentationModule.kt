package com.mamxanh.app.feature.garden.presentation.di

import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.garden.presentation.LivingGardenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gardenPresentationModule = module {
    viewModelOf(::GardenViewModel)
    viewModelOf(::LivingGardenViewModel)
}
