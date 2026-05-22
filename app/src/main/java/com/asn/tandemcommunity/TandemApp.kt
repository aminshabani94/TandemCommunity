package com.asn.tandemcommunity

import android.app.Application
import com.asn.tandemcommunity.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TandemApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TandemApp)
            modules(appModule)
        }
    }
}