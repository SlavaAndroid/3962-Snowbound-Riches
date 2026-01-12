package jp.co.tai.screens.start

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import jp.co.tai.screens.leaders.CustomWebView
import jp.co.tai.screens.levels.TridentChromeClient
import jp.co.tai.screens.privacy.utils.Looper
import jp.co.tai.storage.SnowboundStore.getStringFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartManager(
    private val activity: ComponentActivity
) {
    private var chrome: TridentChromeClient? = null

    fun setChrome(chromeClient: TridentChromeClient){
        this.chrome = chromeClient
    }

    fun startGame() {
        CoroutineScope(Dispatchers.IO).launch {
            getStringFlow(activity).collect { savedUrl ->
                if (savedUrl == null) {
//                    Log.d("MYTAG", "Run build link")
                    Looper(activity).loop()
                } else {
//                    Log.d("MYTAG", "Saved url = $savedUrl")
                    withContext(Dispatchers.Main){
                        val cwv = CustomWebView(activity, chrome!!)
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        cwv.requestFocus()
                        cwv.loadUrl(savedUrl)
                    }
                    return@collect
                }
            }
        }
    }
}