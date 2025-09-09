package com.arthurabreu.wifiqrcodegeneratorapp

import android.app.Application
import com.arthurabreu.wifiqrcodegeneratorapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WifiQrApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WifiQrApp)
            modules(appModule)
        }
    }
}
