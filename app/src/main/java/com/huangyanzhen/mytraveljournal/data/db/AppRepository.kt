package com.huangyanzhen.mytraveljournal.data.db

import com.huangyanzhen.mytraveljournal.data.model.BlockContent
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {
    private val journalDao = database.journalDao()
    private val blockDao = database.blockDao()

    /**
     * 获取所有Journal
     */
    fun getAllJournals(): Flow<List<JournalEntity>> =
        journalDao.getAllActiveJournals()

    /**
     * 给定某个Journal，获取所有block
     */
    fun observeBlocks(journalId: String): Flow<List<BlockEntity>> =
        blockDao.getBlocksForJournal(journalId)

    /**
     * 新建Journal
     */
    suspend fun createJournal(
        title: String, startDate: Long,
        endDate: Long) {
        val journal = JournalEntity(
            title = title,
            startDate = startDate,
            endDate = endDate,
        )
        journalDao.insertJournal(journal)
    }

    suspend fun addBlockToJournal(block: BlockEntity) {
        blockDao.insertBlocks(listOf(block))
    }

}