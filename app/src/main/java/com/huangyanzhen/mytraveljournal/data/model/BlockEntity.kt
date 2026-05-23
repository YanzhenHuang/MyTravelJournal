package com.huangyanzhen.mytraveljournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huangyanzhen.mytraveljournal.data.sync.SyncStatus
import java.util.UUID

@Entity(tableName = "blocks")
data class BlockEntity (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val journalId: String,
    val blockOrder: Int,
    val content: BlockContent,
    val timestamp: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val isDeleted: Boolean = false
)