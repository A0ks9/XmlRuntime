package com.example

import android.app.Application
import com.example.di.AppModule
import com.voyager.di.appModule
import com.voyager.resources.ResourcesBridge
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused") // To suppress lint warning about unused Application class
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin here
        startKoin {
            androidContext(this@MyApplication) // Pass application context
            modules(
                appModule(ResourcesBridge(), true),
                AppModule
            )             // Load your AppModule
        }
    }
}