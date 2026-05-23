import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.huangyanzhen.mytraveljournal.domain.ExifExtractor
import com.huangyanzhen.mytraveljournal.domain.LocalMediaManager
import com.huangyanzhen.mytraveljournal.domain.MediaConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class MediaManagerTest {

    private lateinit var context: Context
    private lateinit var mediaManager: LocalMediaManager
    private lateinit var exifExtractor: ExifExtractor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        mediaManager = LocalMediaManager(context)
        exifExtractor = ExifExtractor()
    }

    @Test
    fun testMediaCopyAndExifExtraction() {
        // 1. 制造一张假照片并存入缓存目录
        val fakePhotoFile = File(context.cacheDir, "fake_photo.jpg")

        // 必须写入真实的 JPEG 数据，否则 ExifInterface 会拒绝工作
        // TODO: 测试其它文件类型
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        FileOutputStream(fakePhotoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        // 2. 伪造 EXIF 数据 (模拟澳门的坐标和当前时间)
        val testLat = 22.1987
        val testLng = 113.5438
        val testTimeString = "2026:05:23 12:00:00"

        val exif = ExifInterface(fakePhotoFile.absolutePath)
        exif.setLatLong(testLat, testLng)
        exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, testTimeString)
        exif.saveAttributes()

        // 将 File 包装成系统的 Uri（模拟用户从相册选中返回的结果）
        val fakeUri = Uri.fromFile(fakePhotoFile)

        // ================= 开始测试核心逻辑 =================

        // 3. 测试 LocalMediaManager
        val relativePath = mediaManager.copyImageToLocalOSS(fakeUri)

        assertNotNull("拷贝失败，相对路径为空", relativePath)
        assertTrue("路径格式不对", relativePath!!.startsWith(MediaConstants.LOCAL_OSS_MEDIA_DIR))

        val savedFile = mediaManager.getFileFromRelativePath(relativePath)
        assertTrue("文件没有真正存入私有目录", savedFile.exists())
        assertTrue("文件大小不正常", savedFile.length() > 0)

        // 4. 测试 ExifExtractor
        val metadata = exifExtractor.extract(savedFile)

        // 验证坐标 (浮点数对比需要提供一个极小的误差范围，这里设为 0.0001)
        assertNotNull("经度解析失败", metadata.latitude)
        assertNotNull("纬度解析失败", metadata.longitude)
        assertEquals(testLat, metadata.latitude!!, 0.0001)
        assertEquals(testLng, metadata.longitude!!, 0.0001)

        // 验证时间戳
        val expectedTimestamp = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
            .parse(testTimeString)?.time
        assertEquals("时间解析不匹配", expectedTimestamp, metadata.timestamp)

        println("多媒体测试通过！文件拷贝和 EXIF 提取全部正常！")

        // 5. 打扫战场：删掉测试文件
        fakePhotoFile.delete()
        savedFile.delete()
    }
}