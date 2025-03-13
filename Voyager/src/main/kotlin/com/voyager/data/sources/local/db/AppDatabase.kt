package com.voyager.data.sources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voyager.data.models.ViewNode

@Database(entities = [ViewNode::class], version = 1, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun ViewNodeDao(): ViewNodeDao
}