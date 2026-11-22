package com.mamxanh.app.feature.tree.data.di

import com.mamxanh.app.feature.tree.data.repository.TreeRepositoryImpl
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import org.koin.dsl.module

val treeDataModule =
    module {
        single<TreeRepository> { TreeRepositoryImpl(get()) }
    }
