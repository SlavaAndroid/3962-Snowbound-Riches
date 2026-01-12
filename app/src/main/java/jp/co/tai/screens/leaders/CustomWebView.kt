package jp.co.tai.screens.leaders

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import jp.co.tai.screens.levels.TridentChromeClient
import jp.co.tai.screens.settings.utils.TridentViewClient

@SuppressLint("ViewConstructor")
class CustomWebView(
    activity: ComponentActivity,
    private var chromeClient: TridentChromeClient
) : WebView(activity) {
    private val contentRoot: FrameLayout = FrameLayout(activity)
    private var contentCallback: ValueCallback<Array<Uri>>? = null
    private val viewClient = TridentViewClient(activity, start = {
        contentCallback?.onReceiveValue(null)
        contentCallback = null
    }, finish = {
        contentRoot.isVisible = true
    })
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (canGoBack()) {
                goBack()
            }
        }
    }

    init {
        val content: ViewGroup = activity.findViewById(android.R.id.content)
        content.addView(contentRoot)
        contentRoot.addView(
            this,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        contentRoot.isVisible = false
        activity.onBackPressedDispatcher.addCallback(activity, backPressedCallback)
        setupView(this)
    }

    override fun destroy() {
        chromeClient.onDestroy()
        backPressedCallback.remove()
        super.destroy()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    fun setupView(view: WebView) {
        view.webViewClient = viewClient
        view.webChromeClient = chromeClient

        view.isFocusable = true
        view.isFocusableInTouchMode = true

        view.settings.javaScriptEnabled = true
        view.settings.javaScriptCanOpenWindowsAutomatically = true
        view.settings.builtInZoomControls = true
        view.settings.displayZoomControls = false
        view.settings.setSupportMultipleWindows(false)

        view.settings.mediaPlaybackRequiresUserGesture = true

        view.settings.domStorageEnabled = true

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)

        view.settings.loadWithOverviewMode = true
        view.settings.useWideViewPort = true

        view.isVerticalScrollBarEnabled = false
        view.isHorizontalScrollBarEnabled = false

        view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        view.settings.allowContentAccess = true
        view.settings.allowFileAccess = true

        view.settings.cacheMode = WebSettings.LOAD_DEFAULT
        view.settings.loadsImagesAutomatically = true

        view.settings.saveFormData = false
        view.importantForAutofill = IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS

        view.setLayerType(
            LAYER_TYPE_HARDWARE,
            null
        )
        UserAgent(view).refactor()
    }
}