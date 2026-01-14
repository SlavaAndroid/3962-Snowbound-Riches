package jp.co.tai.screens.profile

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import jp.co.tai.screens.snowbound.logic.SnowboundStart
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URLEncoder
import java.util.Locale
import kotlin.coroutines.resume

object Slicer {
    suspend fun sliceData(raw: String, id: String, context: Context): String {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val time = packageInfo.firstInstallTime.toString()
        val encoded = URLEncoder.encode(raw, "UTF-8")
        val device =
            Build.BRAND.replaceFirstChar { it.titlecase(Locale.getDefault()) } + " " + Build.MODEL
        val encodeDevice = URLEncoder.encode(device, "UTF-8")
        hearDeerBells(context)?.let {
            return "h2zxe6ec6dcu0czy=${it}&znwek07pu9npr=$encoded&zh0rspsph9hnzhio=$id&7a5vk2vn2gm8gl=$time&k4dtzpvuia50pq=$encodeDevice"
        }
        return ""
    }

    private suspend fun hearDeerBells(context: Context) = suspendCancellableCoroutine { c ->
        SnowboundStart.snowTime(context) {
            if (c.isActive)
                c.resume(it)
        }
    }
}