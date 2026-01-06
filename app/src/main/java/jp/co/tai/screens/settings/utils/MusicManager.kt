package jp.co.tai.screens.settings.utils

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import jp.co.tai.R

object MusicManager {
    private var exoPlayer: ExoPlayer? = null
    var isMusicEnabled = true
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        val uri = "android.resource://${context.packageName}/${R.raw.game_music}".toUri()
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            prepare()
            volume = 1.0f
        }
        isInitialized = true
    }

    fun play() { if (isMusicEnabled) exoPlayer?.playWhenReady = true }
    fun pause() { exoPlayer?.playWhenReady = false }

    fun setEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) play() else pause()
    }

    fun setVolumeLevel(level: Int) {
        val v = when (level.coerceIn(0,4)) {
            4 -> 1.0f
            3 -> 0.75f
            2 -> 0.5f
            1 -> 0.25f
            else -> 0.0f
        }
        exoPlayer?.volume = v
        setEnabled(v > 0f)
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        isInitialized = false
    }
}