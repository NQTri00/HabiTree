package com.mamxanh.app.feature.shop.presentation.di

import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val shopPresentationModule = module {
    viewModelOf(::ShopViewModel)
}
