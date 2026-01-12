package jp.co.tai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import jp.co.tai.navigation.LoadingGraph
import jp.co.tai.screens.levels.TridentChromeClient
import jp.co.tai.screens.start.StartManager

class LoadingActivity : ComponentActivity() {
    private var controller: WindowInsetsControllerCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val startManager = StartManager(this).apply {
            setChrome(TridentChromeClient(this@LoadingActivity))
        }
        controller = WindowInsetsControllerCompat(window, window.decorView)
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        enableEdgeToEdge()
        setContent {
            LoadingGraph(startManager)
        }
    }
}