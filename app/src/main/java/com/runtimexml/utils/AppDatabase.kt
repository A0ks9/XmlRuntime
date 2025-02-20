package com.runtimexml.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.runtimexml.utils.interfaces.ViewStateDao

@Database(entities = [ViewState::class], version = 1, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun ViewStateDao(): ViewStateDao
}