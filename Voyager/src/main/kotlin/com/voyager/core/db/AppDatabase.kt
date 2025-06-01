package com.voyager.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voyager.core.model.ViewNode

@Database(entities = [ViewNode::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun viewNodeDao(): ViewNodeDao
} 