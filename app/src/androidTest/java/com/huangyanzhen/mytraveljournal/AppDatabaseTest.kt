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

        val textContent = BlockContent.TextBlock(
            segments = listOf(
                TextSegment(text = "今天去了"),
                TextSegment(text = "故宫", isBold = true),
                TextSegment(text = "，非常震撼")
            )
        )
        val block = BlockEntity(
            id = "block-uuid-001",
            journalId = journalId,
            blockOrder = 0,
            content = textContent
        )
        blockDao.insertBlocks(listOf(block))

        // 4. 执行读操作：通过 Flow 获取最新的数据列表，first() 表示取第一帧数据
        val blocksFromDb = blockDao.getBlocksForJournal(journalId).first()

        // 5. 断言验证 (Assert)
        assertEquals(1, blocksFromDb.size)
        val readBlock = blocksFromDb[0]

        // 验证 TypeConverter 是否完美还原了多态对象
        assertTrue(
            "反序列化后的 content 应该是 TextBlock 类型",
            readBlock.content is BlockContent.TextBlock
        )

        val readTextContent = readBlock.content as BlockContent.TextBlock
        assertEquals(3, readTextContent.segments.size)
        assertEquals("故宫", readTextContent.segments[1].text)
        assertTrue("故宫这个词应该是加粗的", readTextContent.segments[1].isBold)

        println("✅ 测试通过！AST JSON 序列化与反序列化完美运行！")
    }


}