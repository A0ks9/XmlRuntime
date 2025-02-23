package com.dynamic.data.sources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dynamic.data.models.ViewState

@Database(entities = [ViewState::class], version = 1, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun ViewStateDao(): ViewStateDao
}