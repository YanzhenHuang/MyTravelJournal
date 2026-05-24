package com.huangyanzhen.mytraveljournal

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.huangyanzhen.mytraveljournal.data.dao.BlockDao
import com.huangyanzhen.mytraveljournal.data.dao.JournalDao
import com.huangyanzhen.mytraveljournal.data.db.AppDatabase
import com.huangyanzhen.mytraveljournal.data.model.BlockContent
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import com.huangyanzhen.mytraveljournal.data.model.JournalEntity
import com.huangyanzhen.mytraveljournal.data.model.TextSegment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var journalDao: JournalDao
    private lateinit var blockDao: BlockDao

    @Before
    fun createDb() {
        // 获取测试上下文，并创建一个纯内存数据库
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        journalDao = db.journalDao()
        blockDao = db.blockDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadRichTextBlock() = runTest {
        val journalId = "journal-uuid-001"
        val journal = JournalEntity(
            id = journalId,
            title = "测试标题",
            startDate = 1704067200000,
            endDate = 1704499200000
        )
        journalDao.insertJournal(journal)

        // Create the first text block
        val textContent1 = BlockContent.TextBlock(
            segments = listOf(
                TextSegment(text = "今天去了"),
                TextSegment(text = "故宫", isBold = true),
                TextSegment(text = "，非常震撼")
            )
        )
        val block1 = BlockEntity(
            id = "block-uuid-001",
            journalId = journalId,
            content = textContent1
        )

        // Create the second text block
        val textContent2 = BlockContent.TextBlock(
            segments = listOf(
                TextSegment(text = "故宫有很多"),
                TextSegment(text = "名胜古迹", isItalic = true), TextSegment(text = "，非常awesome")
            )
        )

        val block2 = BlockEntity(
            id = "block-uuid-002",
            journalId = journalId,
            content = textContent2,
            previousBlockId = "block-uuid-001",
            nextBlockId = "block-uuid-001"
        )

        // Create the third text block
        val textContent3 = BlockContent.TextBlock(
            segments = listOf(
                TextSegment(text = "有天坛、天安门，以及"),
                TextSegment(text = "很多景点", isBold = true, isItalic = true)
            )
        )
        val block3 = BlockEntity(
            id = "block-uuid-003",
            journalId = journalId,
            content = textContent3,
            previousBlockId = "block-uuid-002",
            nextBlockId = "block-uuid-001"
        )
        block2.nextBlockId = "block-uuid-003"

        blockDao.insertBlocks(listOf(block1, block2, block3))

        // 先不管链接关系，获取到所有属于这个日记的块
        val blocksFromDb = blockDao.getBlocksForJournal(journalId).first()
        val lastBlock = blockDao.getLastBlockFromJournal(journalId).first()
        val firstBlock = blocksFromDb.first { it.id == lastBlock?.nextBlockId }

        // 验证1：块长度
        assertEquals(2, blocksFromDb.size)

        // 验证2：块顺序
        val sortedList = mutableListOf<String>()
        var currentBlock = firstBlock

        if (blocksFromDb.size <= 1) {
            sortedList += firstBlock.id
        } else {
            while (currentBlock.nextBlockId != firstBlock.id) {
                sortedList += currentBlock.id
                currentBlock = blocksFromDb.first { it.id == currentBlock.nextBlockId }
            }
        }

        assertEquals("block-uuid-001", sortedList[0])
        assertEquals("block-uuid-002", sortedList[1])
        assertEquals("block-uuid-003", sortedList[2])
    }

    @Test
    fun insertAndReadMapBlock() = runTest {
        val journalId = "journal-uuid-001"
        val journal = JournalEntity(
            id = journalId,
            title = "测试标题",
            startDate = 1704067200000,
            endDate = 1704499200000
        )

        journalDao.insertJournal(journal)
        val lat = 39.909
        val lng = 116.397

        val mapContent = BlockContent.MapBlock(
            latitude = 39.909,
            longitude = 116.397,
            label = "故宫"
        )

        val block1 = BlockEntity(
            id = "block-uuid-001",
            journalId = journalId,
//            blockOrder = 0,
            content = mapContent
        )

//        val block2 = BlockEntity()

        blockDao.insertBlocks(listOf(block1))
        val blocksFromDb = blockDao.getBlocksForJournal(journalId).first()

        assertEquals(1, blocksFromDb.size)
        val readBlock = blocksFromDb[0]

        assertTrue(
            "反序列化后的 content 应该是 MapBlock 类型",
            readBlock.content is BlockContent.MapBlock
        )

        val readMapContent = readBlock.content as BlockContent.MapBlock

        assertEquals(lat, readMapContent.latitude, 0.001)
        assertEquals(lng, readMapContent.longitude, 0.001)
    }


}