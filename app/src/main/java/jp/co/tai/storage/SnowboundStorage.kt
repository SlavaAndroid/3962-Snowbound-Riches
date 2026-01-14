package jp.co.tai.storage

import android.content.Context
import androidx.core.content.edit
import java.io.File

class SnowboundStorage (context: Context) {
    private val prefs = context.getSharedPreferences("snowbound_prefs", Context.MODE_PRIVATE)

    fun getScore(): Int = prefs.getInt("score", 0)
    fun setScore(value: Int) = prefs.edit { putInt("score", value) }

    fun getName(): String = prefs.getString("name", "Username") ?: "Username"
    fun setName(value: String) = prefs.edit { putString("name", value) }

    fun getPhoto(): String = prefs.getString("photo", "") ?: ""
    fun setPhoto(value: String) = prefs.edit { putString("photo", value) }

    fun isLevelPassed(level: Int) = prefs.getBoolean("level_$level", false)
    fun setLevelPassed(level: Int, isPassed: Boolean) =
        prefs.edit { putBoolean("level_$level", isPassed) }

    fun getMusic(): Int = prefs.getInt("music", 4)
    fun setMusic(value: Int) = prefs.edit { putInt("music", value.coerceIn(0, 4)) }

    fun getSound(): Int = prefs.getInt("sound", 4)
    fun setSound(value: Int) = prefs.edit { putInt("sound", value.coerceIn(0, 4)) }

    fun isPushRequestShown(): Boolean =
        prefs.getBoolean("push_request_shown", false)

    fun setPushRequestShown() {
        prefs.edit { putBoolean("push_request_shown", true) }
    }

    fun clearUserProfile() {
        prefs.edit {
            remove("name")
            remove("photo")
        }
    }

    fun saveCameraPhoto(context: Context, tempPath: String): String? {
        return try {
            val file = File(context.filesDir, "photo_image")
            if (file.exists()) {
                file.delete()
                file.createNewFile()
            }
            val tempFile = File(tempPath)
            tempFile.inputStream().use { inputStream ->
                file.outputStream().use { outputStream ->
                    outputStream.write(inputStream.readBytes())
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}