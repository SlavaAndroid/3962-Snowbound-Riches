package jp.co.tai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import jp.co.tai.screens.levels.LevelsScreenUi
import jp.co.tai.screens.settings.utils.MusicHandler
import jp.co.tai.screens.snowbound.SnowboundScreenUi
import jp.co.tai.screens.start.StartScreenUi
import jp.co.tai.storage.SnowboundStorage

@Composable
fun SnowboundGraph(storage: SnowboundStorage) {

    val navController = rememberNavController()

    MusicHandler(storage)

    NavHost(
        navController = navController,
        startDestination = StartScreen
    ) {
        composable<StartScreen> { StartScreenUi(navController, storage) }
        composable<LevelsScreen> { LevelsScreenUi(navController, storage) }
        composable<SnowboundScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<SnowboundScreen>()
            SnowboundScreenUi(
                navController = navController,
                level = args.level,
                storage = storage
            )
        }
    }
}