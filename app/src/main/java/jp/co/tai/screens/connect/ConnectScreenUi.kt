package jp.co.tai.screens.connect

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import jp.co.tai.R
import jp.co.tai.navigation.ConnectScreen
import jp.co.tai.navigation.LoadingScreen
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.MainButton
import jp.co.tai.ui.theme.Typography

@Composable
fun ConnectScreenUi(navController: NavController) {

    var showButton by remember { mutableStateOf(true) }
    var showConnecting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Background(bgRes = R.drawable.loading_bg)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.lost_network),
                        style = Typography.bodyLarge,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Image(
                        painter = painterResource(id = R.drawable.lost_network),
                        contentDescription = "Connect icon",
                        modifier = Modifier.fillMaxWidth(0.7f),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(id = R.string.check),
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    when {
                        showConnecting -> {
                            AnimatedVisibility(
                                visible = showConnecting,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "Connecting...",
                                    style = Typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        showButton -> {
                            MainButton(
                                buttonText = stringResource(id = R.string.try_again),
                                enabled = showButton,
                                modifier = Modifier
                                    .fillMaxWidth(0.53f)
                                    .aspectRatio(3.5f)
                            ) {
                                showButton = false
                                showConnecting = true

                                if (context.checkSnowConnection(SnowboundConnectionType.SNOW_ONLINE) ||
                                    context.checkSnowConnection(SnowboundConnectionType.SNOW_WIFI) ||
                                    context.checkSnowConnection(SnowboundConnectionType.SNOW_MOBILE) ||
                                    context.checkSnowConnection(SnowboundConnectionType.SNOW_TUNNEL)) {
                                    navController.navigate(LoadingScreen) {
                                        popUpTo(ConnectScreen) { inclusive = true }
                                    }
                                } else {
                                    showButton = true
                                    showConnecting = false
                                }
                            }
                        }

                        else -> {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ServiceCast")
fun Context.checkSnowConnection(type: SnowboundConnectionType): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val nc = cm.getNetworkCapabilities(network) ?: return false
    return when (type) {
        SnowboundConnectionType.SNOW_ONLINE    -> nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        SnowboundConnectionType.SNOW_WIFI  -> nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        SnowboundConnectionType.SNOW_MOBILE   -> nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        SnowboundConnectionType.SNOW_TUNNEL -> nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
}