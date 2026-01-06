package jp.co.tai.screens.snowbound

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import jp.co.tai.R
import jp.co.tai.navigation.LevelsScreen
import jp.co.tai.navigation.SnowboundScreen
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.settings.utils.Sfx
import jp.co.tai.screens.settings.utils.SoundEffectPlayer
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.screens.start.peckPress
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.snowboundElements
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@SuppressLint(
    "UseOfNonLambdaOffsetOverload", "ConfigurationScreenWidthHeight",
    "MutableCollectionMutableState"
)
@Composable
fun SnowboundScreenUi(
    navController: NavController,
    level: Int,
    storage: SnowboundStorage
) {

    val context = LocalContext.current
    val density = LocalDensity.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val girlWidth = remember { (screenHeightDp * 0.7f).dp }
    val girlHeight = remember { (screenHeightDp * 0.81f).dp }
    val girlOffsetX = remember { (screenHeightDp * 0.18f).dp }
    val elementsPaddingVert = remember { (screenHeightDp * 0.055f).dp }
    val elementsPaddingHor = remember { (screenHeightDp * 0.06f).dp }
    val fieldEndPadding = remember { (screenHeightDp * 0.34f).dp }
    val snowboundElList = remember { snowboundElements }
    var bonusRes by remember { mutableStateOf<Int?>(null) }
    val rows = 4
    val cols = 6
    val storageScore by remember { mutableIntStateOf(storage.getScore()) }
    var score by remember { mutableIntStateOf(0) }
    var newScore by remember { mutableIntStateOf(0) }
    val count = remember {
        when (level) {
            in 1..12 -> 6
            in 13..18 -> 7
            in 19..21 -> 8
            else -> 8
        }
    }
    val choosingElements = remember { snowboundElList.shuffled().take(count) }
    val coroutineScope = rememberCoroutineScope()
    var isRestarting by remember { mutableStateOf(false) }
    val offsets = remember { List(rows * cols) { Animatable(0f) } }
    val sizes = remember { List(rows * cols) { Animatable(1f) } }
    val rotationAngle = remember { Animatable(0f) }
    var matchingIndices by remember { mutableStateOf(setOf<Int>()) }
    var showLightBg by remember { mutableStateOf(false) }
    var showLevelCompleted by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }
    val nextLevelTarget by remember { mutableIntStateOf(level * 150) }
    var moves by remember { mutableIntStateOf(level * 4) }
    var randomizedElements by remember {
        mutableStateOf(
            MutableList<Int?>(rows * cols) { choosingElements.random() }
        )
    }
    var cellHeightPx by remember { mutableFloatStateOf(0f) }
    var isProcessingClick by remember { mutableStateOf(false) }
    var showBonusScreen by remember { mutableStateOf(false) }
    var lastClusterSize by remember { mutableIntStateOf(0) }
    val isEffect by remember { mutableIntStateOf(storage.getSound()) }

    fun onElementClick(index: Int) {
        if (isProcessingClick || showGameOver || showLevelCompleted) return
        if (randomizedElements[index] == null) return

        coroutineScope.launch {
            isProcessingClick = true

            val cluster = findMatchGroup(
                rows = rows,
                cols = cols,
                elements = randomizedElements,
                startIndex = index
            )

            if (cluster.isEmpty()) {
                isProcessingClick = false
                return@launch
            }

            matchingIndices = cluster
            showLightBg = true
            if (isEffect > 0) SoundEffectPlayer.play(context, Sfx.COLLECT)

            val gainedScore = 15 * cluster.size
            score += gainedScore
            lastClusterSize = cluster.size
            bonusRes = bonusResForCluster(lastClusterSize)

            cluster.forEach { idx ->
                launch {
                    sizes[idx].animateTo(
                        targetValue = 1.15f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    )
                }
            }

            launch {
                rotationAngle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(400, easing = LinearEasing)
                )
                rotationAngle.snapTo(0f)
            }

            delay(350)

            cluster.forEach { idx ->
                launch {
                    sizes[idx].animateTo(
                        targetValue = 0f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    )
                }
            }

            delay(130)

            val dropSteps = calculateDropStepsAfterRemoval(
                rows = rows,
                cols = cols,
                elements = randomizedElements,
                removed = cluster
            )

            showLightBg = false
            matchingIndices = emptySet()
            bonusRes = null

            animateFallOfExisting(
                dropSteps = dropSteps,
                randomizedElements = randomizedElements,
                cellHeightPx = cellHeightPx,
                offsets = offsets,
                coroutineScope = coroutineScope
            )

            for (i in 0 until rows * cols) {
                offsets[i].snapTo(0f)
            }

            randomizedElements = randomizedElements.toMutableList()
                .also { list -> cluster.forEach { idx -> list[idx] = null } }

            cluster.forEach { idx ->
                sizes[idx].snapTo(1f)
            }

            val (newList, newIndices) = applyGravityAndGetNewIndices(
                rows = rows,
                cols = cols,
                elements = randomizedElements,
                choosingElements = choosingElements
            )
            randomizedElements = newList

            animateFallOfNew(
                newIndices = newIndices,
                cellHeightPx = cellHeightPx,
                offsets = offsets,
                coroutineScope = coroutineScope
            )

            moves = (moves - 1).coerceAtLeast(0)

            if (lastClusterSize >= 4 && score < nextLevelTarget) {
                if (isEffect > 0) SoundEffectPlayer.play(context, Sfx.BONUS)
                showBonusScreen = true
            } else {
                if (score >= nextLevelTarget) {
                    storage.setLevelPassed(level, true)
                    newScore = score + storageScore
                    storage.setScore(newScore)
                    showLevelCompleted = true
                } else if (moves == 0 && score < nextLevelTarget) {
                    showGameOver = true
                } else if (!hasAnyAdjacentMatch(
                        rows = rows,
                        cols = cols,
                        elements = randomizedElements
                    )
                ) {
                    showGameOver = true
                }
            }

            isProcessingClick = false
        }
    }

    suspend fun restartBoard() {
        if (cellHeightPx <= 0f) return

        isProcessingClick = true
        isRestarting = true
        showGameOver = false
        showLevelCompleted = false
        showLightBg = false
        matchingIndices = emptySet()

        val total = rows * cols
        val collapseJobs = mutableListOf<Job>()
        for (i in 0 until total) {
            collapseJobs += coroutineScope.launch {
                sizes[i].animateTo(
                    targetValue = 0f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                )
            }
        }
        collapseJobs.joinAll()

        randomizedElements = MutableList(total) { choosingElements.random() }

        val startOffset = -cellHeightPx * rows
        for (i in 0 until total) {
            offsets[i].snapTo(startOffset)
            sizes[i].snapTo(1f)
        }

        val fallJobs = mutableListOf<Job>()
        for (i in 0 until total) {
            fallJobs += coroutineScope.launch {
                offsets[i].animateTo(
                    targetValue = 0f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        }
        fallJobs.joinAll()

        score = 0
        newScore = 0
        moves = level * 5

        isRestarting = false
        isProcessingClick = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
    ) {
        Background(bgRes = R.drawable.game_bg)

        SquareButton(
            btnRes = R.drawable.restart_btn,
            cooldownMillis = 0L,
            btnEnabled = !isProcessingClick && !isRestarting,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 50.dp)
        ) {
            coroutineScope.launch {
                restartBoard()
            }
        }

        GirlBox(
            modifier = Modifier.align(Alignment.BottomEnd),
            girlWidth = girlWidth,
            girlHeight = girlHeight,
            girlOffsetX = girlOffsetX
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, start = 44.dp, end = fieldEndPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .zIndex(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ScoreBox(text = "Level $level", modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(4.dp))

                ScoreBox(text = "Score: $score", modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(4.dp))

                ScoreBox(text = "Goal: $nextLevelTarget", modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(4.dp))

                ScoreBox(text = "Moves: $moves", modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                Background(
                    bgRes = R.drawable.game_field,
                    modifier = Modifier.scale(1.07f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = elementsPaddingVert, horizontal = elementsPaddingHor)
                ) {
                    for (row in 0 until rows) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (col in 0 until cols) {
                                    val index = row * cols + col

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .offset(y = with(density) { offsets[index].value.toDp() })
                                            .graphicsLayer(
                                                scaleX = sizes[index].value,
                                                scaleY = sizes[index].value
                                            )
                                            .onGloballyPositioned { coords ->
                                                if (cellHeightPx == 0f) {
                                                    cellHeightPx = coords.size.height.toFloat()
                                                }
                                            }
                                            .peckPress(
                                                cooldownMillis = 0L,
                                                isChickenReady = !isProcessingClick && !showGameOver && !showLevelCompleted,
                                                onPeck = { onElementClick(index) }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (showLightBg && index in matchingIndices) {
                                            Image(
                                                painter = painterResource(id = R.drawable.glow),
                                                contentDescription = "Glow",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .scale(1.5f)
                                                    .graphicsLayer(rotationZ = rotationAngle.value),
                                                contentScale = ContentScale.Fit
                                            )
                                        }

                                        randomizedElements[index]?.let { elementRes ->
                                            Image(
                                                painter = painterResource(id = elementRes),
                                                contentDescription = "Snowbound element",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Fit
                                            )
                                        }

                                        if (showLightBg && index in matchingIndices){
                                            bonusRes?.let { res ->
                                                Image(
                                                    painter = painterResource(id = res),
                                                    contentDescription = "Bonus X",
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .fillMaxWidth(0.9f),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showBonusScreen) {
            BonusScreenUi { bonus ->
                score += bonus

                if (score >= nextLevelTarget) {
                    storage.setLevelPassed(level, true)
                    newScore = score + storageScore
                    storage.setScore(newScore)
                    showLevelCompleted = true
                } else if (moves == 0 && score < nextLevelTarget) {
                    showGameOver = true
                } else if (!hasAnyAdjacentMatch(
                        rows = rows,
                        cols = cols,
                        elements = randomizedElements
                    )
                ) {
                    showGameOver = true
                }

                showBonusScreen = false
            }
        }

        ScaleInOverlay(visible = showLevelCompleted) {
            GameOverScreenUi(
                winLevel = true,
                score = score,
                totalScore = newScore,
                level = level,
                restartOrNext = {
                    if (level >= 21) {
                        navController.navigate(LevelsScreen) {
                            popUpTo(LevelsScreen) { inclusive = true }
                        }
                    } else {
                        navController.navigate(SnowboundScreen(level + 1)) {
                            popUpTo(SnowboundScreen(level)) { inclusive = true }
                        }
                    }
                }
            )
        }

        ScaleInOverlay(visible = showGameOver) {
            GameOverScreenUi(
                winLevel = false,
                score = score,
                totalScore = storageScore,
                level = level,
                restartOrNext = {
                    navController.navigate(SnowboundScreen(level)) {
                        popUpTo(SnowboundScreen(level)) { inclusive = true }
                    }
                }
            )
        }
    }
}