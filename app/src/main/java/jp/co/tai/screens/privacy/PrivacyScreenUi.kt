package jp.co.tai.screens.privacy

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import jp.co.tai.R
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.ui.theme.BlueDark
import jp.co.tai.ui.theme.Typography

@SuppressLint("SetJavaScriptEnabled", "ConfigurationScreenWidthHeight")
@Composable
fun PrivacyScreenUi(back: () -> Unit) {

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topPadding = remember { (screenHeightDp * 0.035f).dp }
    val bottomPadding = remember { (screenHeightDp * 0.042f).dp }
    val verticalPadding = remember { (screenHeightDp * 0.05f).dp }
    var loadWeb by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        Background(bgRes = R.drawable.game_bg)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 4.dp)
                .padding(horizontal = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp)
            ) {
                Background(bgRes = R.drawable.privacy_bg)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = verticalPadding)
                        .padding(top = topPadding, bottom = bottomPadding)
                ) {
                    AndroidView(
                        factory = { context ->
                            FrameLayout(context).apply {
                                val webView = WebView(context).apply {
                                    setInitialScale(100)
                                    settings.setSupportZoom(true)
                                    settings.builtInZoomControls = true
                                    settings.displayZoomControls = false
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            loadWeb = false
                                        }

                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            return false
                                        }
                                    }
                                    loadUrl(
                                        "https://snowboundriches.xyz/"
                                    )
                                }
                                addView(
                                    webView, FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    if (loadWeb) {
                        LinearProgressIndicator(
                            color = BlueDark,
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.privacy).uppercase(),
                style = Typography.labelLarge,
                fontSize = 45.sp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        SquareButton(
            btnRes = R.drawable.close_btn,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 50.dp)
        ) { back() }
    }
}