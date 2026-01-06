package jp.co.tai.screens.settings.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.tai.storage.SnowboundStorage

@Composable
fun MusicHandler(storage: SnowboundStorage) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MusicManager.initialize(context)
        SoundEffectPlayer.initialize(context)

        MusicManager.setVolumeLevel(storage.getMusic())
        SoundEffectPlayer.setVolumeLevel(storage.getSound())

        if (storage.getMusic() > 0) MusicManager.play()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> MusicManager.pause()
                Lifecycle.Event.ON_DESTROY -> {
                    MusicManager.release()
                    SoundEffectPlayer.release()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}