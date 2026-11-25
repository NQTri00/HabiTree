package com.mamxanh.app.feature.tree.data.di

import com.mamxanh.app.feature.tree.data.repository.AccessoryRepositoryImpl
import com.mamxanh.app.feature.tree.data.repository.TreeDetailRepositoryImpl
import com.mamxanh.app.feature.tree.data.repository.TreeRepositoryImpl
import com.mamxanh.app.feature.tree.domain.repository.AccessoryRepository
import com.mamxanh.app.feature.tree.domain.repository.TreeDetailRepository
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import org.koin.dsl.module

val treeDataModule =
    module {
        single<TreeDetailRepository> { TreeDetailRepositoryImpl(get()) }
        single<AccessoryRepository> { AccessoryRepositoryImpl(get()) }
        single<TreeRepository> { TreeRepositoryImpl(get()) }
    }
