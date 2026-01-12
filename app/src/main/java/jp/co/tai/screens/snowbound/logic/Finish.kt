package jp.co.tai.screens.snowbound.logic

import android.content.Context
import jp.co.tai.UiProtocol
import jp.co.tai.UiSharedMemory
import jp.co.tai.screens.privacy.utils.Domain
import jp.co.tai.screens.rules.regToken
import jp.co.tai.storage.SnowboundStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Finish(data: String?, context: Context) {
    init {
        val dom = Domain()
        data?.let { d ->
            when {
                dom.short == d.take(dom.short?.length ?: 0) -> {
//                    Log.d("MYTAG", "onPageFinished domain = $d")
                    UiSharedMemory.setScreen(UiProtocol.STUB)
                }

                d.length < 3 -> {
                    throw IllegalStateException()
                }

                dom.short != d.take(dom.short?.length ?: 0) -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            regToken(context)
                            SnowboundStore.saveStringOnce(context, d)
                        } catch (_: Exception) {
//                            Log.d("MYTAG", "Url already saved")
                        }
                    }
                }
            }
        }
    }
}