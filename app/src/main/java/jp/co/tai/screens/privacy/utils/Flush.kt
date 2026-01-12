package jp.co.tai.screens.privacy.utils

import android.webkit.CookieManager

class Flush {
    init {
        try {
            CookieManager.getInstance().flush()
        } catch (_: Throwable) {
        }
    }
}