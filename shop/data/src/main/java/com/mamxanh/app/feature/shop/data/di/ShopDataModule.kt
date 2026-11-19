package com.mamxanh.app.feature.shop.data.di

import com.mamxanh.app.feature.shop.data.OfflineShopRepository
import com.mamxanh.app.feature.shop.domain.ShopRepository
import org.koin.dsl.module

val shopDataModule = module {
    single<ShopRepository> { OfflineShopRepository(get()) }
}
