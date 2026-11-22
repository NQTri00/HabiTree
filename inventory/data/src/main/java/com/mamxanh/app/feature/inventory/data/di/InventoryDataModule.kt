package com.mamxanh.app.feature.inventory.data.di

import com.mamxanh.app.feature.inventory.data.repository.InventoryRepositoryImpl
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import org.koin.dsl.module

val inventoryDataModule =
    module {
        single<InventoryRepository> { InventoryRepositoryImpl(get()) }
    }
