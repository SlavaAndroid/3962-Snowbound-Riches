package jp.co.tai.screens.leaders

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.tai.R
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.Typography
import kotlin.math.min

@Composable
fun LeadersScreenUi(
    storage: SnowboundStorage,
    back: () -> Unit
) {

    val userNickname by remember { mutableStateOf(storage.getName()) }
    val userScore by remember { mutableIntStateOf(storage.getScore()) }
    val allPlayers = remember {
        mutableStateListOf(
            "Blizzard" to 2625,
            "Mojo" to 2540,
            "Aurora" to 2485,
            "Glacier" to 2410,
            "Icicle" to 2355,
            "Whiteout" to 2290,
            "Snowfall" to 2235,
            "Drift" to 2180,
            "Polar" to 2125,
            "Aval" to 2070,
            "Nimbus" to 2015,
            "Storm" to 1960,
            "Chill" to 1910,
            "Boreal" to 1860,
            "Sleet" to 1815,
            "Arctic" to 1770,
            "Frost" to 1725,
            "IceShard" to 1680
        )
    }

    fun updateLeaderboard(nickname: String, score: Int) {
        val idx = allPlayers.indexOfFirst { it.first == nickname }
        if (idx >= 0) {
            allPlayers[idx] = nickname to score
        } else {
            allPlayers.add(nickname to score)
        }
    }

    LaunchedEffect(Unit) {
        updateLeaderboard(userNickname, userScore)
    }

    val sortedPlayers by remember {
        derivedStateOf { allPlayers.sortedByDescending { it.second }.take(18) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        Background(bgRes = R.drawable.game_bg)

        SquareButton(
            btnRes = R.drawable.back_btn,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 50.dp)
        ) { back() }

        Text(
            text = stringResource(R.string.leaderboard).uppercase(),
            style = Typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 78.dp)
                .padding(horizontal = 50.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val column1 = sortedPlayers.subList(0, min(6, sortedPlayers.size))
                val column2 = sortedPlayers.subList(min(6, sortedPlayers.size), min(12, sortedPlayers.size))
                val column3 = sortedPlayers.subList(min(12, sortedPlayers.size), min(18, sortedPlayers.size))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(column1.size) { index ->
                            val (name, score) = column1[index]
                            LeadersRow(
                                place = index + 1,
                                name = name,
                                coins = score
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(column2.size) { index ->
                            val (name, score) = column2[index]
                            LeadersRow(
                                place = index + 7,
                                name = name,
                                coins = score
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(column3.size) { index ->
                            val (name, score) = column3[index]
                            LeadersRow(
                                place = index + 13,
                                name = name,
                                coins = score
                            )
                        }
                    }
                }
            }
        }
    }
}