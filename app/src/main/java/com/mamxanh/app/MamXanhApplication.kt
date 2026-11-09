package com.mamxanh.app

import android.app.Application
import com.mamxanh.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MamXanhApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MamXanhApplication)
            modules(appModule)
        }
    }
}
