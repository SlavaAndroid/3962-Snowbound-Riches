package jp.co.tai

import android.os.SharedMemory
import java.nio.ByteBuffer

object UiSharedMemory {
    private var sharedMemory: SharedMemory? = null
    private var byteBuffer: ByteBuffer? = null

    @Synchronized
    fun init() {
        if (sharedMemory != null) return

        try {
            sharedMemory = SharedMemory.create("ui_protocol", 4)
            byteBuffer = sharedMemory?.mapReadWrite()
            byteBuffer?.putInt(0, UiProtocol.LOADING)
        } catch (t: Throwable) {
            sharedMemory = null
            byteBuffer = null
            t.printStackTrace()
        }
    }

    fun setScreen(screen: Int) {
        try {
            byteBuffer?.putInt(0, screen)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun getScreen(): Int {
        return try {
            byteBuffer?.getInt(0) ?: UiProtocol.LOADING
        } catch (_: Throwable) {
            UiProtocol.LOADING
        }
    }

    @Synchronized
    fun close() {
        try {
            sharedMemory?.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            sharedMemory = null
            byteBuffer = null
        }
    }
}