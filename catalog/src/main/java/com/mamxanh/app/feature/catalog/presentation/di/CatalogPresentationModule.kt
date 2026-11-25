package com.mamxanh.app.feature.catalog.presentation.di

import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CatalogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val catalogPresentationModule =
    module {
        viewModelOf(::CatalogViewModel)
    }
