package com.mamxanh.app.feature.tree.presentation.di

import com.mamxanh.app.feature.tree.presentation.TreeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val treePresentationModule = module {
    viewModelOf(::TreeViewModel)
}
