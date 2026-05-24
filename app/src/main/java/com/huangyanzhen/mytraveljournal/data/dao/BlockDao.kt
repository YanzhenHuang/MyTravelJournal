package com.huangyanzhen.mytraveljournal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Query(
        """
        SELECT * FROM blocks WHERE journalId = :journalId AND nextBlockId = (
        SELECT id FROM blocks WHERE previousBlockId = NULL
        )
    """
    )
    fun getLastBlockFromJournal(journalId: String): BlockEntity?

    @Query("SELECT * FROM blocks WHERE journalId = :journalId AND isDeleted = 0")
    fun getBlocksForJournal(journalId: String): Flow<List<BlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<BlockEntity>)
}