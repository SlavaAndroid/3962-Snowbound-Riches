package jp.co.tai.navigation

import android.app.ActivityOptions
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.co.tai.LoadingActivity
import jp.co.tai.R
import jp.co.tai.SharedMemoryProtocolBridge
import jp.co.tai.SnowboundActivity
import jp.co.tai.UiProtocol
import jp.co.tai.UiSharedMemory
import jp.co.tai.screens.connect.ConnectScreenUi
import jp.co.tai.screens.connect.SnowboundConnectionType
import jp.co.tai.screens.connect.checkSnowConnection
import jp.co.tai.screens.loading.LoadingScreenUi
import jp.co.tai.screens.rules.RulesScreenUi
import jp.co.tai.screens.start.StartManager

@Composable
fun LoadingGraph(startManager: StartManager) {

    val context = LocalActivity.current as LoadingActivity
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (context.checkSnowConnection(SnowboundConnectionType.SNOW_ONLINE) ||
            context.checkSnowConnection(SnowboundConnectionType.SNOW_WIFI) ||
            context.checkSnowConnection(SnowboundConnectionType.SNOW_MOBILE) ||
            context.checkSnowConnection(SnowboundConnectionType.SNOW_TUNNEL))
            LoadingScreen else ConnectScreen
    ) {
        composable<LoadingScreen> {

            var screen by remember { mutableIntStateOf(UiProtocol.LOADING) }

            SharedMemoryProtocolBridge { screen = it }

            when (screen) {
                UiProtocol.LOADING -> LoadingScreenUi()
                UiProtocol.STUB -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, SnowboundActivity::class.java)
                        val options = ActivityOptions.makeCustomAnimation(
                            context,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )

                        context.startActivity(intent, options.toBundle())
                        context.finish()
                    }
                }
                UiProtocol.EXTRA -> RulesScreenUi {  }
            }

            DisposableEffect(Unit) {
                onDispose {
                    UiSharedMemory.close()
                }
            }

            LaunchedEffect(Unit) {
                try {
                    startManager.startGame()
                } catch (_: Exception) {
                    UiSharedMemory.setScreen(UiProtocol.STUB)
                    return@LaunchedEffect
                }
            }

        }
        composable<ConnectScreen> { ConnectScreenUi(navController) }
    }
}