package com.huangyanzhen.mytraveljournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huangyanzhen.mytraveljournal.data.sync.SyncStatus
import java.util.UUID

@Entity(tableName = "journals")
data class JournalEntity (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val coverImagePath: String ?= null,
    val startDate: Long,
    val endDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val isDeleted: Boolean = false
)