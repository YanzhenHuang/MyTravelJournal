package com.huangyanzhen.mytraveljournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huangyanzhen.mytraveljournal.data.sync.SyncStatus
import java.util.UUID

@Entity(tableName = "journals")
data class JournalEntity (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    /** 日记文字标题 */
    val title: String,
    /** 日记封面图片路径 */
    val coverImagePath: String ?= null,

    /** 日记记录开始时间 */
    val startDate: Long,
    /** 日记记录结束时间 */
    val endDate: Long,

    /** 日记创建时间 */
    val createdAt: Long = System.currentTimeMillis(),
    /** 上一次更新时间 */
    val updatedAt: Long = System.currentTimeMillis(),

    /** 同步状态 */
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    /** “已删除”标志位 */
    val isDeleted: Boolean = false
)