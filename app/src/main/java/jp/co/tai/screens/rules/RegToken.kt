package jp.co.tai.screens.rules

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.Locale

public fun regToken(context: Context): Unit {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val fcmToken = withContext(Dispatchers.IO) {
                FirebaseMessaging.getInstance().token.await()
            }
            val locale = Locale.getDefault().toLanguageTag()
            val url = "https://snowboundriches.xyz/bpm74l2bb4/"
            val fullUrl = "$url?" +
                    "2t8phbvu7w=${Firebase.analytics.appInstanceId.await()}" +
                    "&mag8xfxtk6=${
                        URLEncoder.encode(fcmToken, "UTF-8")
                    }"

            val client = Volley.newRequestQueue(context)
            val request = object : StringRequest(
                Request.Method.GET,
                fullUrl,
                { _ -> },
                { _ -> }
            ) {
                override fun getHeaders(): Map<String, String> {
                    return mapOf("Accept-Language" to locale)
                }
            }
            client.add(request)
        } catch (exc: Exception) {}
    }
}