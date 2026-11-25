package com.mamxanh.app.feature.inventory.presentation.di

import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inventoryPresentationModule =
    module {
        viewModelOf(::InventoryViewModel)
    }
