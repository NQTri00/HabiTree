package com.mamxanh.app

import android.app.Application
import com.mamxanh.app.core.config.HabiTreeConfigProvider
import com.mamxanh.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MamXanhApplication : Application() {
    override fun attachBaseContext(base:android.content.Context){
        super.attachBaseContext(com.mamxanh.app.presentation.ui.localization.AppLanguageController.wrap(base))
    }
    override fun onCreate() {
        super.onCreate()
        com.mamxanh.app.presentation.ui.localization.AppLanguageController.initialize(this)
        startKoin {
            androidContext(this@MamXanhApplication)
            modules(appModule(HabiTreeConfigProvider(BuildConfig.API_BASE_URL)))
        }
    }
}
