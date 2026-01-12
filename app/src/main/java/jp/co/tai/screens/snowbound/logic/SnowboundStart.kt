package jp.co.tai.screens.snowbound.logic

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import jp.co.tai.screens.leaders.getIdOrLeaders
import jp.co.tai.screens.profile.probe

object SnowboundStart {
    private val hTh = HandlerThread("riches").apply { start() }
    private val hTHL = Handler(hTh.looper)

    fun snowTime(context: Context, uid: (String?) -> Unit) {

        val probe = probe(context) //adb

        if ("abba"[probe] == 'b') {
//            Log.d("MYTAG", "Wrong adb from cpp")
            uid(null)
            return
        }

        hTHL.post {
            val id = getIdOrLeaders(false, context) //gadid
//            Log.d("MYTAG", "get gadid from cpp = $id")
            uid(id)
        }
    }
}