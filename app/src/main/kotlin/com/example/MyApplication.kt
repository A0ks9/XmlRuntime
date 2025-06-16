package com.example

import android.app.Application
import com.example.di.AppModule
import com.example.logging.AndroidLogger
import com.voyager.core.appModule
import com.voyager.core.model.VoyagerConfig
import com.voyager.core.resources.ResourcesBridge
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.generated.AttributeRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

/**
 * Main Application class that initializes the core components of the app.
 * This class is responsible for:
 * 1. Setting up the logging system
 * 2. Initializing Koin for dependency injection
 * 3. Registering custom attributes for XML parsing
 */
@Suppress("unused") // To suppress lint warning about unused Application class
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the Android logger for the Voyager library
        LoggerFactory.setLogger(AndroidLogger())

        // Create resources provider
        val resourcesProvider = ResourcesBridge()

        // Initialize Koin dependency injection framework
        startKoin {
            // Pass the application context to Koin
            androidContext(this@MyApplication)
            // Load both the Voyager core module and our app-specific module
            modules(
                appModule(resourcesProvider, true, true, R.style.Theme_Voyager), AppModule
            )
        }

        // Wait for Koin to initialize and get the VoyagerConfig
        GlobalContext.get().get<VoyagerConfig>()

        // Register all custom views and its attributes that can be in XML layouts
        AttributeRegistry.registerAll()
    }
}