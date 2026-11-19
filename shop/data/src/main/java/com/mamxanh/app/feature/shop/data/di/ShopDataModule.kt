package com.mamxanh.app.feature.shop.data.di

import com.mamxanh.app.feature.shop.data.ShopRepositoryImpl
import com.mamxanh.app.feature.shop.domain.ShopRepository
import org.koin.dsl.module

val shopDataModule = module {
    single<ShopRepository> { ShopRepositoryImpl(get()) }
}
