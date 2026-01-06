package jp.co.tai.screens.loading

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import jp.co.tai.R
import jp.co.tai.ui.theme.Typography

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LoadingScreenUi(
    initialPercent: Int = 0,
    onProgress: (Int) -> Unit = {}
) {

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topPadding = remember { (screenHeightDp * 0.06f).dp }
    val density = LocalDensity.current
    val snowflakeSizeDp = remember { 50.dp }
    val snowflakeSizePx = remember { with(density) { snowflakeSizeDp.toPx() } }
    var boxWidthPx by remember { mutableFloatStateOf(0f) }
    var boxHeightPx by remember { mutableFloatStateOf(0f) }
    val snowflakes = rememberFallingSnowflakes(boxWidthPx, boxHeightPx, snowflakeSizePx)

    BackHandler(enabled = true) {}


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Background(bgRes = R.drawable.loading_bg)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    boxWidthPx = it.size.width.toFloat()
                    boxHeightPx = it.size.height.toFloat()
                }
        ){
            snowflakes.forEach { coin ->
                Image(
                    painter = painterResource(id = R.drawable.snowflake),
                    contentDescription = "Snowflake",
                    modifier = Modifier
                        .offset { IntOffset(coin.x.toInt(), coin.animY.value.toInt()) }
                        .size(snowflakeSizeDp)
                        .graphicsLayer(rotationZ = coin.rotation)
                        .zIndex(2f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = topPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = Typography.labelLarge,
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(id = R.drawable.letter),
                contentDescription = "Chicken",
                modifier = Modifier.fillMaxWidth(0.8f),
                contentScale = ContentScale.Fit
            )

            RoadProgress(
                initialPercent = initialPercent,
                onProgress = onProgress
            )
        }
    }
}