package jp.co.tai.screens.snowbound

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.tai.R
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.start.MainButton
import jp.co.tai.screens.start.peckPress
import jp.co.tai.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun BonusScreenUi(
    onBonusConfirmed: (Int) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val fallOffsetY = remember { Animatable(-1000f) }
    val shakeOffsetX = remember { Animatable(0f) }
    val leftOffsetX = remember { Animatable(0f) }
    val rightOffsetX = remember { Animatable(0f) }
    val bonusScale = remember { Animatable(0f) }

    var chestsClickable by remember { mutableStateOf(false) }
    var selectedChestIndex by remember { mutableStateOf<Int?>(null) }
    var bonusValue by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        fallOffsetY.snapTo(-400f)
        fallOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )

        repeat(2) {
            shakeOffsetX.animateTo(-20f, tween(60))
            shakeOffsetX.animateTo(20f, tween(60))
        }
        shakeOffsetX.animateTo(0f, tween(80))

        val spread = with(density) { 40.dp.toPx() }
        launch {
            leftOffsetX.animateTo(
                targetValue = -spread,
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rightOffsetX.animateTo(
                targetValue = spread,
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            )
        }

        chestsClickable = true
    }

    fun onChestClick(index: Int) {
        if (!chestsClickable) return
        if (selectedChestIndex != null) return

        chestsClickable = false
        selectedChestIndex = index

        val possible = listOf(50, 150, 200, 300)
        val bonus = possible.random()
        bonusValue = bonus

        scope.launch {
            bonusScale.snapTo(0f)
            bonusScale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            bonusScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(120, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
            .background(Color.Black.copy(alpha = 0.85f))
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.bonus),
                style = Typography.bodyLarge,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (0..2).forEach { index ->
                        val xOffset = when (index) {
                            0 -> leftOffsetX.value
                            1 -> 0f
                            2 -> rightOffsetX.value
                            else -> 0f
                        } + shakeOffsetX.value

                        val isSelected = selectedChestIndex == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .offset {
                                    IntOffset(
                                        x = xOffset.toInt(),
                                        y = fallOffsetY.value.toInt()
                                    )
                                }
                                .peckPress(isChickenReady = chestsClickable &&
                                        selectedChestIndex == null) {
                                    onChestClick(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected && bonusValue != null) {
                                Text(
                                    text = "+${bonusValue}",
                                    style = Typography.bodyLarge,
                                    fontSize = 42.sp,
                                    modifier = Modifier.graphicsLayer(
                                        scaleX = bonusScale.value,
                                        scaleY = bonusScale.value
                                    )
                                )
                            } else {
                                val alpha =
                                    if (selectedChestIndex != null && !isSelected) 0.7f else 1f

                                Image(
                                    painter = painterResource(id = R.drawable.letter),
                                    contentDescription = "Letter",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(alpha = alpha),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .aspectRatio(3.5f)
            ) {
                if (selectedChestIndex != null && bonusValue != null) {
                    MainButton(
                        buttonText = stringResource(id = R.string.ok),
                        textStyle = Typography.bodyMedium,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        onBonusConfirmed(bonusValue!!)
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.select_chest),
                        style = Typography.labelMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}