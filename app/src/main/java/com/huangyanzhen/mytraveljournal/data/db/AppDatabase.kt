package com.huangyanzhen.mytraveljournal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.huangyanzhen.mytraveljournal.data.dao.BlockDao
import com.huangyanzhen.mytraveljournal.data.dao.JournalDao
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity

@Database(entities = [JournalEntity::class, BlockEntity::class],
    version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun blockDao(): BlockDao
}