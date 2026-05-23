package com.huangyanzhen.mytraveljournal.domain

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * 本地媒体管理器。接收系统相册选中的临时 URI，拷贝到App私有的沙盒目录中，并返回不会失效的相对路径。
 */
class LocalMediaManager(private val context: Context) {

    // OSS 私有文件目录
    private val mediaDir = File(
        context.filesDir,
        MediaConstants.LOCAL_OSS_MEDIA_DIR
    ).apply { if (!exists()) mkdirs() }

    /**
     * 将相册 Uri 复制到私有目录，并返回相对路径。若出错则返回 `null`，不抛出异常。
     * @param sourceUri: 源文件 URI
     */
    fun copyImageToLocalOSS(sourceUri: Uri): String? {
        return try {

            // 动态获取文件拓展名
            val extension = getFileExtension(sourceUri)
            if (extension == null)
                return null

            // 目标文件
            val fileName = "${UUID.randomUUID()}.$extension"
            val destFile = File(mediaDir, fileName)

            // 流拷贝
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output -> input.copyTo(output) }
            }

            // 返回持久化相对路径
//            File(MediaConstants.LOCAL_OSS_MEDIA_DIR, fileName).toString()
            "${MediaConstants.LOCAL_OSS_MEDIA_DIR}/$fileName"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 工具方法：将相对路径还原为可以展示的绝对 File 对象
     * @param relativePath 相对路径
     */
    fun getFileFromRelativePath(relativePath: String): File {
        return File(context.filesDir, relativePath)
    }

    /**
     * 给定一个URI，通过解析MIME Type 来获取文件拓展名。
     */
    private fun getFileExtension(uri: Uri): String? {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri)

        // 系统相册或文件管理器: content://URI
        if (mimeType != null) {
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }

        // 兼容 file://URI
        val path = uri.path
        if (path != null) {
            return File(path).extension
        }

        return null
    }
}