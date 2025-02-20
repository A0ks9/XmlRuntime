package com.runtimexml.utils

import android.content.Context
import androidx.room.Room

internal object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "view_states"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
