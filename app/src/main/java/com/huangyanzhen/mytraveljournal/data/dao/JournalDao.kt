package com.huangyanzhen.mytraveljournal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journals WHERE isDeleted = 0 ORDER BY startDate DESC")
    fun getAllActiveJournals(): Flow<List<JournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Query("UPDATE journals SET isDeleted = 1, syncStatus = 'Pending', updatedAt = :timestamp WHERE id = :journalId")
    suspend fun softDeleteJournal(journalId: String, timestamp: Long = System.currentTimeMillis())
}