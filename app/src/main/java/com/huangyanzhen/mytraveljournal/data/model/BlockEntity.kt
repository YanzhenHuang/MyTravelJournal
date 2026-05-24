package com.huangyanzhen.mytraveljournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huangyanzhen.mytraveljournal.data.sync.SyncStatus
import java.util.UUID

@Entity(tableName = "blocks")
data class BlockEntity (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    /** 该内容快所属的日记ID */
    val journalId: String,
    /** 该内容快在所属日记内的前一个块ID */
    var previousBlockId: String? = null,
    /** 该内容快在所属日记内的下一个块ID */
    var nextBlockId: String? = null,

    /** Block的具体内容 */
    val content: BlockContent,

    /** 创建时间戳 */
    val timestamp: Long = System.currentTimeMillis(),
    /** 上次更新时间戳 */
    val updatedAt: Long = System.currentTimeMillis(),

    /** 同步状态 */
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    /** “已删除” 标志位 */
    val isDeleted: Boolean = false
)