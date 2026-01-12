package jp.co.tai.screens.settings.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import jp.co.tai.screens.privacy.utils.Flush
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TridentViewClient (
    val activity: ComponentActivity,
    val start: () -> Unit,
    val finish:() -> Unit
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        start()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        finish()
        CoroutineScope(Dispatchers.IO).launch {
            request(activity.activityResultRegistry)
            Flush()
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request?.url?.toString()?.contains("about:blank") == true) return false
        var intent: Intent? = null
        val scheme = request?.url?.scheme
        if (scheme == "intent") {
            intent = Intent.parseUri(
                request.url.toString(),
                Intent.URI_INTENT_SCHEME
            )
        } else if (scheme == "mailto") {
            intent = Intent(Intent.ACTION_SENDTO, request.url)
        } else if (scheme == "tel") {
            intent = Intent(Intent.ACTION_DIAL, request.url)
        } else if (scheme != null && scheme != "http" && scheme != "https" && scheme != "blob" && scheme != "data") {
            intent = Intent(Intent.ACTION_VIEW, request.url)
        }

        if (intent != null) {
            try {
                activity.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    activity,
                    "No application found to handle the request",
                    Toast.LENGTH_LONG
                ).show()
            }
            return true
        }
        return false
    }
}