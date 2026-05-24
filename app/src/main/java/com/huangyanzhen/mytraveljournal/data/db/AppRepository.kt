package com.huangyanzhen.mytraveljournal.data.db

import com.huangyanzhen.mytraveljournal.data.model.BlockContent
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity
import kotlinx.coroutines.flow.Flow

/**
 * 仓库类，所有的ViewModel都跟它交互。
 */
class AppRepository(private val database: AppDatabase) {
    private val journalDao = database.journalDao()
    private val blockDao = database.blockDao()

    /**
     * 获取所有Journal，返回一个Flow流
     */
    fun getAllJournals(): Flow<List<JournalEntity>> =
        journalDao.getAllActiveJournals()

    /**
     * 给定某个Journal，获取所有block
     * @param journalId 日记ID
     */
    fun observeBlocks(journalId: String): Flow<List<BlockEntity>> =
        blockDao.getBlocksForJournal(journalId)

    /**
     * 新建Journal
     * @param title 文字标题
     * @param startDate 日记记录开始时间
     * @param endDate 日记记录结束时间
     */
    suspend fun createJournal(
        title: String,
        startDate: Long,
        endDate: Long
    ) {
        val journal = JournalEntity(
            title = title,
            startDate = startDate,
            endDate = endDate,
        )
        journalDao.insertJournal(journal)
    }

    /**
     * 向Block数据库中添加实体，其中Block内部定义了其所属的日记的ID。
     * @param block 内容快实体
     */
    suspend fun addBlockToJournal(block: BlockEntity) {
        blockDao.insertBlocks(listOf(block))
    }

    /**
     * 软删除某个日记
     * @param journalId 要软删除的日记的ID
     */
    suspend fun softDeleteJournal(journalId: String) {
        journalDao.softDeleteJournal(journalId)
    }

}