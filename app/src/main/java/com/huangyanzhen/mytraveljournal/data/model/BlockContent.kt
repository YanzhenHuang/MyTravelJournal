package com.huangyanzhen.mytraveljournal.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TextSegment(
    val text: String,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val textColor: String? = null,
)

@Serializable
sealed class BlockContent {
    @Serializable
    data class TextBlock(
        val segments: List<TextSegment>
    ) : BlockContent()

    @Serializable
    data class ImageBlock(
        val localRelativePath: String,
        val caption: String = "",
        val width: Int = 0,
        val height: Int = 0
    ): BlockContent()

    @Serializable
    data class MapBlock(
        val latitude: Double,
        val longitude: Double,
        val label: String
    ): BlockContent()
}