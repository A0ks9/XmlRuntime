package com.voyager.core.view

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper

object ViewFactory {
    private const val DEFAULT_ANDROID_WIDGET_PACKAGE = "android.widget."

    fun createView(context: Context, type: String): View {
        val ctx = context as? ContextThemeWrapper ?: ContextThemeWrapper(context, 0)
        return DefaultViewRegistry.createView(ctx, type.qualifiedPackage())
            ?: CustomViewRegistry.createView(ctx, type.qualifiedPackage())
            ?: ReflectionViewCreator.createView(ctx, type.qualifiedPackage())
            ?: throw IllegalArgumentException("Could not create view for type: ${type.qualifiedPackage()}")
    }

    private fun String.qualifiedPackage() =
        if (contains('.')) this else "$DEFAULT_ANDROID_WIDGET_PACKAGE$this"
} 