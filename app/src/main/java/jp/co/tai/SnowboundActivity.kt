package jp.co.tai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import jp.co.tai.navigation.SnowboundGraph
import jp.co.tai.screens.settings.utils.MusicManager
import jp.co.tai.storage.SnowboundStorage

class SnowboundActivity : ComponentActivity() {
    private val windowController by lazy { WindowInsetsControllerCompat(window, window.decorView) }
    private val storage by lazy { SnowboundStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnowboundGraph(storage)
        }
    }

    override fun onResume() {
        super.onResume()
        windowController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowController.hide(WindowInsetsCompat.Type.systemBars())
        if (storage.getMusic() > 0) MusicManager.play()
    }
}