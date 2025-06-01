package com.voyager.core.view

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper

object ReflectionViewCreator {

    fun createView(context: ContextThemeWrapper, type: String): View? {
        return try {
            val clazz = Class.forName(type).kotlin
            val ktor =
                clazz.constructors.firstOrNull { it.parameters.size == 1 && it.parameters[0].type.classifier == Context::class }
                    ?: throw NoSuchMethodException(
                        "No constructor with a single Context parameter found for $type. Custom views must have a public constructor(Context) or constructor(Context, AttributeSet)."
                    )
            val viewConstructor: (ContextThemeWrapper) -> View = { ctx ->
                try {
                    ktor.call(ctx) as View
                } catch (e: Exception) {
                    throw IllegalArgumentException(
                        "Failed to invoke constructor for $type.", e
                    )
                }
            }

            DefaultViewRegistry.viewCreators[type] = viewConstructor
            viewConstructor(context)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Error creating view via reflection for type: $type. Details: ${e.message}", e
            )
        }
    }
} 