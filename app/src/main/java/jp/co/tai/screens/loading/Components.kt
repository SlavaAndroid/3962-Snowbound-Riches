package jp.co.tai.screens.loading

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.tai.screens.loading.model.SnowflakeModel
import jp.co.tai.ui.theme.BlueGradient
import jp.co.tai.ui.theme.Orange
import jp.co.tai.ui.theme.Typography
import jp.co.tai.ui.theme.YellowGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun rememberFallingSnowflakes(
    boxWidthPx: Float,
    boxHeightPx: Float,
    snowflakeSizePx: Float
): List<SnowflakeModel> {
    val snowflakes = remember { mutableStateListOf<SnowflakeModel>() }
    var snowflakeId by remember { mutableIntStateOf(0) }

    LaunchedEffect(boxWidthPx, boxHeightPx) {
        if (boxWidthPx == 0f || boxHeightPx == 0f) return@LaunchedEffect
        while (true) {
            delay(Random.nextLong(300, 700))
            val x = Random.nextFloat() * (boxWidthPx - snowflakeSizePx)
            val animY = Animatable(-snowflakeSizePx)
            val myId = snowflakeId++
            val rotation = Random.nextFloat() * 360f - 180f
            val model = SnowflakeModel(myId, x, animY, rotation)
            snowflakes += model

            launch {
                animY.animateTo(
                    targetValue = boxHeightPx + snowflakeSizePx,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = LinearEasing
                    )
                )
                model.isVisible = false
                delay(100)
                snowflakes.removeAll { it.id == myId }
            }
        }
    }
    return snowflakes.filter { it.isVisible }
}

@Composable
fun RoadProgress(
    modifier: Modifier = Modifier,
    initialPercent: Int,
    onProgress: (Int) -> Unit,
) {
    val startFraction = (initialPercent / 100f).coerceIn(0f, 0.99f)
    val anim = remember { Animatable(startFraction) }
    val percentage = (anim.value * 100).roundToInt()

    LaunchedEffect(Unit) {
        snapshotFlow { (anim.value * 100).roundToInt() }
            .distinctUntilChanged()
            .collect { onProgress(it) }
    }

    LaunchedEffect(initialPercent) {
        val cur = anim.value

        suspend fun go(target: Float, totalMs: Int, from: Float, to: Float) {
            if (cur < target) {
                val remain = (((target - cur) / (to - from)) * totalMs)
                    .toInt()
                    .coerceAtLeast(0)
                anim.animateTo(
                    targetValue = target,
                    animationSpec = tween(durationMillis = remain, easing = LinearOutSlowInEasing)
                )
            }
        }

        go(0.5f, 10_000, from = 0f,   to = 0.5f)
        go(0.75f,20_000, from = 0.5f, to = 0.75f)
        go(0.99f,180_000,from = 0.75f,to = 0.99f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Loading...$percentage%",
            style = Typography.bodyMedium,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(6.5f)
                .clip(RoundedCornerShape(12.dp))
                .background(BlueGradient)
                .border(2.dp, YellowGradient, RoundedCornerShape(12.dp))
        ) {
            SegmentedProgressBarCanvas(
                progress = anim.value.coerceIn(0f, 0.99f),
                segments = 24,
                gap = 3.dp,
                corner = 3.dp,
                filledColor = Orange,
                emptyColor = Orange.copy(alpha = 0.18f),
                padding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SegmentedProgressBarCanvas(
    modifier: Modifier = Modifier,
    progress: Float,
    segments: Int = 24,
    gap: Dp = 4.dp,
    corner: Dp = 10.dp,
    filledColor: Color,
    emptyColor: Color = filledColor.copy(alpha = 0.2f),
    padding: PaddingValues = PaddingValues(0.dp),
) {
    val clamped = progress.coerceIn(0f, 1f)
    val v = (clamped * segments).coerceIn(0f, segments.toFloat() - 1e-4f)
    val full = v.toInt()
    val partial = (v - full).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (segments <= 0) return@Canvas

        val gapPx = gap.toPx()
        val radius = corner.toPx().coerceAtLeast(0f)

        val totalGap = gapPx * (segments - 1)
        val segW = ((size.width - totalGap) / segments).coerceAtLeast(0f)
        val segH = size.height

        var x = 0f
        repeat(segments) { i ->
            drawRoundRect(
                color = emptyColor,
                topLeft = Offset(x, 0f),
                size = Size(segW, segH),
                cornerRadius = CornerRadius(radius, radius)
            )

            val fillFrac = when {
                i < full -> 1f
                i == full -> partial
                else -> 0f
            }

            if (fillFrac > 0f) {
                drawRoundRect(
                    color = filledColor,
                    topLeft = Offset(x, 0f),
                    size = Size(segW * fillFrac, segH),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }

            x += segW + gapPx
        }
    }
}

@Composable
fun Background(
    modifier: Modifier = Modifier,
    bgRes: Int,
    bgScale: ContentScale = ContentScale.FillBounds
) {
    Image(
        painter = painterResource(id = bgRes),
        contentDescription = "Background",
        modifier = modifier.fillMaxSize(),
        contentScale = bgScale
    )
}