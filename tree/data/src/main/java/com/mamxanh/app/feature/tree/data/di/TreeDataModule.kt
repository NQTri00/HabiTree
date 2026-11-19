package com.mamxanh.app.feature.tree.data.di

import com.mamxanh.app.feature.tree.data.TreeRepositoryImpl
import com.mamxanh.app.feature.tree.domain.TreeRepository
import org.koin.dsl.module

val treeDataModule = module {
    single<TreeRepository> { TreeRepositoryImpl(get()) }
}
