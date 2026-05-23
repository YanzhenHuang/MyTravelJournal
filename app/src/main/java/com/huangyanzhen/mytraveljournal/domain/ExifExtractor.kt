package com.huangyanzhen.mytraveljournal.domain

import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * EXIF提取引擎。
 */
class ExifExtractor {
    /**
     * 给定一个 `File` 对象，提取文件的EXIF信息，并返回一个 `MediaMetadata` 对象。
     * @param file 给定的文件对象。
     */
    fun extract(file: File): MediaMetadata {
        val exif = ExifInterface(file.absolutePath)

        // 提取经纬度，如无则null
        val latLong = exif.latLong
        val lat = latLong?.get(0)
        val lng = latLong?.get(1)

        // 时间串
        var timestamp: Long? = null
        val dateString = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
        if (dateString != null) {
            try {
                val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                timestamp = sdf.parse(dateString)?.time
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return MediaMetadata(latitude = lat, longitude = lng, timestamp = timestamp)
    }
}