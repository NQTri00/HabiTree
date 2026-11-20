package com.mamxanh.app.feature.home.data.di

import android.content.Context
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelectionRepository
import org.koin.dsl.module

private class AndroidHomeTreeSelectionRepository(context: Context) : HomeTreeSelectionRepository {
    private val preferences = context.applicationContext.getSharedPreferences("bio_ui", Context.MODE_PRIVATE)

    override fun read(): Long = preferences.getLong("home_tree_id", 0L)

    override fun write(treeId: Long) {
        preferences.edit().putLong("home_tree_id", treeId).apply()
    }
}

val homeTreeSelectionDataModule = module {
    single<HomeTreeSelectionRepository> { AndroidHomeTreeSelectionRepository(get()) }
}
