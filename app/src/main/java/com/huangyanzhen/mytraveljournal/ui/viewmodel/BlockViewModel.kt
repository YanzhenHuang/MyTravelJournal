package com.huangyanzhen.mytraveljournal.ui.viewmodel

import com.huangyanzhen.mytraveljournal.data.db.AppRepository
import com.huangyanzhen.mytraveljournal.data.model.BlockContent
import com.huangyanzhen.mytraveljournal.data.model.BlockEntity
import com.huangyanzhen.mytraveljournal.data.model.TextSegment
import com.huangyanzhen.mytraveljournal.domain.ExifExtractor
import com.huangyanzhen.mytraveljournal.domain.LocalMediaManager
import kotlinx.coroutines.flow.combine

data class BlockUIState(
    val blocks: List<BlockEntity> = emptyList(),
    val firstBlock: BlockEntity? = null,
    val lastBlock: BlockEntity? = null,
    override val isLoading: Boolean = true
) : BaseUIState(isLoading = true)

class BlockViewModel(
    repository: AppRepository,
    private val mediaManager: LocalMediaManager,
    private val exifExtractor: ExifExtractor,
    private val journalId: String
) : BaseViewModel<BlockUIState>(repository, BlockUIState()) {

    init {
        loadBlocks()
    }

    /**
     * 内部方法，读取所有blocks，并更新UI状态。
     */
    private fun loadBlocks() = createAsyncTask {
        repository.observeBlocks(journalId)
            .combine(uiStateRW) { blocks, curState ->
                curState.copy(
                    blocks = blocks,
                    isLoading = false,
                    firstBlock = findFirstBlock(blocks),
                    lastBlock = findLastBlock(blocks, curState.firstBlock)
                )
            }
            .collect { newState -> uiStateRW.value = newState }
    }

    /**
     * 获取第一个block
     * @param blocks: 所有block列表
     * @return: 第一个block，如果没有则返回null
     */
    private fun findFirstBlock(blocks: List<BlockEntity>): BlockEntity? {
        if (blocks.isEmpty()) return null

        val nonFirstBlockIds = blocks.mapNotNull { it.previousBlockId }.toSet()
        return blocks.find { !nonFirstBlockIds.contains(it.id) }
    }

    /**
     * 获取最后一个block
     * @param blocks: 所有block列表
     * @param firstBlock: 第一个block
     * @return: 最后一个block，如果没有则返回null
     */
    private fun findLastBlock(blocks: List<BlockEntity>, firstBlock: BlockEntity?): BlockEntity? {
        if (blocks.isEmpty() || firstBlock == null) return null

        return blocks.find { it.nextBlockId == null }
    }


    /**
     * 刷新，重新获取数据库中的所有blocks
     */
    fun refreshBlocks() {
        loadBlocks()
    }

    /**
     * 在指定block之后插入新的block
     * @param previousBlockId: 前一个内容块的ID
     * @param blockContent: 新的block的内容
     */
    fun insertBlockAfter(
        previousBlockId: String?,
        blockContent: BlockContent
    ) = createAsyncTask {
        val blocks = uiStateR.value.blocks

        // 获取前一个block
        val previousBlock = if (previousBlockId != null) {
            blocks.find { it.id == previousBlockId }
        } else {
            null
        }

        // 确定新block的连接关系
        val nextBlockId = if (previousBlock != null) {
            previousBlock.nextBlockId
        } else {
            // 如果插入到开头，则原来的第一个block将成为新block的下一个
            uiStateR.value.firstBlock?.id
        }

        // 获取下一个block
        val nextBlock = if (nextBlockId != null) {
            blocks.find { it.id == nextBlockId }
        } else {
            null
        }

        // 创建新block
        val newBlock = BlockEntity(
            journalId = journalId,
            content = blockContent,
            previousBlockId = previousBlockId,
            nextBlockId = nextBlockId
        )

        // 插入新block
        repository.insertOneBlock(newBlock)

        // 如果有前一个block，更新其nextBlockId为当前block
        if (previousBlockId != null) {
            repository.updateOneBlock(
                previousBlock!!.copy(nextBlockId = newBlock.id)
            )
        }

        // 如果原来有下一个block，更新其previousBlockId为当前block
        if (nextBlockId != null) {
            repository.updateOneBlock(
                nextBlock!!.copy(previousBlockId = newBlock.id)
            )
        }
    }

//    /**
//     * 添加文本block
//     * @param segments: 文本片段列表
//     */
//    fun addTextBlock(
//        segments: List<TextSegment>
//    ) = createAsyncTask {
//        val textBlock = BlockContent.TextBlock(segments = segments)
//        val block = BlockEntity(
//            journalId = journalId,
//            content = textBlock,
//            previousBlockId = null,
//            nextBlockId = null
//        )
//        repository.insertOneBlock(block)
//    }
//
//    /**
//     * 添加图片block
//     * @param imagePath: 图片路径
//     * @param caption: 图片说明
//     */
//    fun addImageBlock(
//        imagePath: String,
//        caption: String = ""
//    ) = createAsyncTask {
//        // 使用ExifExtractor提取图片的GPS信息
//        val file = mediaManager.getFileFromRelativePath(imagePath)
//        val metadata = exifExtractor.extract(file)
//
//        val imageBlock = BlockContent.ImageBlock(
//            localRelativePath = imagePath,
//            caption = caption,
//            width = 0,
//            height = 0
//        )
//        val block = BlockEntity(
//            journalId = journalId,
//            content = imageBlock,
//            previousBlockId = null,
//            nextBlockId = null
//        )
//        repository.insertOneBlock(block)
//
//        // 如果图片包含GPS信息，可以考虑创建一个地图block
//        if (metadata.latitude != null && metadata.longitude != null) {
//            val mapBlock = BlockContent.MapBlock(
//                latitude = metadata.latitude,
//                longitude = metadata.longitude,
//                label = "Location from photo"
//            )
//            val mapBlockEntity = BlockEntity(
//                journalId = journalId,
//                content = mapBlock,
//                previousBlockId = null,
//                nextBlockId = null
//            )
//            repository.insertOneBlock(mapBlockEntity)
//        }
//    }
//
//    /**
//     * 添加地图block
//     * @param latitude: 纬度
//     * @param longitude: 经度
//     * @param label: 位置标签
//     */
//    fun addMapBlock(
//        latitude: Double,
//        longitude: Double,
//        label: String
//    ) = createAsyncTask {
//        val mapBlock = BlockContent.MapBlock(
//            latitude = latitude,
//            longitude = longitude,
//            label = label
//        )
//        val block = BlockEntity(
//            journalId = journalId,
//            content = mapBlock,
//            previousBlockId = null,
//            nextBlockId = null
//        )
//        repository.insertOneBlock(block)
//    }
}