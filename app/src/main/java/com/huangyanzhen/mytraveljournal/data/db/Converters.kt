package com.huangyanzhen.mytraveljournal.data.db

import androidx.room.TypeConverter
import com.huangyanzhen.mytraveljournal.data.model.BlockContent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromBlockContent(content: BlockContent): String {
        return json.encodeToString(content)
    }

    @TypeConverter
    fun toBlockContent(data: String): BlockContent {
        return json.decodeFromString(data)
    }
}