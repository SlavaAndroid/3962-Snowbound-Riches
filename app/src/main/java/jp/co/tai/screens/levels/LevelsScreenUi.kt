package jp.co.tai.screens.levels

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import jp.co.tai.R
import jp.co.tai.navigation.SnowboundScreen
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.screens.start.peckPress
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.GrayGradient
import jp.co.tai.ui.theme.OrangeGradient
import jp.co.tai.ui.theme.Typography

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LevelsScreenUi(
    navController: NavController,
    storage: SnowboundStorage
) {

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topPadding = remember { (screenHeightDp * 0.28f).dp }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
    ) {
        Background(bgRes = R.drawable.game_bg)

        Text(
            text = stringResource(R.string.levels).uppercase(),
            style = Typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 50.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(21 / 7) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (rowIndex == 0) topPadding else 0.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    for (colIndex in 0 until 7) {
                        val level = rowIndex * 7 + colIndex + 1
                        val isLevelPassed = storage.isLevelPassed(level - 1)
                        val isUnlocked = level == 1 || isLevelPassed

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .peckPress(
                                    isChickenReady = isUnlocked,
                                    onPeck = { navController.navigate(SnowboundScreen(level)) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Background(
                                bgRes = if (isUnlocked)
                                    R.drawable.level_open_btn
                                else
                                    R.drawable.level_closed_btn
                            )

                            Text(
                                text = "$level",
                                style = Typography.titleMedium.copy(
                                    brush = if (isUnlocked) OrangeGradient else GrayGradient
                                )
                            )
                        }
                    }
                }
            }
        }

        SquareButton(
            btnRes = R.drawable.back_btn,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 50.dp)
        ) { navController.popBackStack() }
    }
}