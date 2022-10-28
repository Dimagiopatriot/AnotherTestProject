package com.example.anothertestproject

import android.app.Application
import com.example.anothertestproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(repositoriesModule, apiModule, retrofitModule, useCases, viewModelModule, roomModule)
        }
    }
}