package com.mamxanh.app.feature.garden.presentation.di

import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenViewModel
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingViewModel
import com.mamxanh.app.feature.garden.presentation.ui.livinggarden.LivingGardenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gardenPresentationModule =
    module {
        viewModelOf(::GardenViewModel)
        viewModelOf(::PlantingViewModel)
        viewModelOf(::LivingGardenViewModel)
    }
