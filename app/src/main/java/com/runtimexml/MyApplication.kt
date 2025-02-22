package com.runtimexml

import android.app.Application
import com.runtimexml.di.appModule // Import your AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused") // To suppress lint warning about unused Application class
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin here
        startKoin {
            androidContext(this@MyApplication) // Pass application context
            modules(appModule)             // Load your AppModule
        }
    }
}