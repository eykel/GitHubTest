package com.s2start.githubtest.app

import android.app.Application
import com.s2start.githubtest.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application (){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(apiModule, netModule, viewModelModule, repositoryModule, databaseModule, viewModule))
        }
    }
}