package jp.co.tai.screens.snowbound

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.tai.R
import jp.co.tai.ui.theme.OrangeGradient
import jp.co.tai.ui.theme.Typography

@Composable
fun ScaleInOverlay(
    visible: Boolean,
    enterScale: Float = 0.6f,
    durationMillis: Int = 350,
    dimBehind: Boolean = true,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else enterScale,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "OverlayScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "OverlayAlpha"
    )

    if (visible || alpha > 0.01f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (dimBehind) Color.Black.copy(alpha = 0.45f * alpha) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
            ) {
                content()
            }
        }
    }
}

@Composable
fun ScoreBox(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(30.dp),
                brush = OrangeGradient
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            style = Typography.labelMedium
        )
    }
}

@Composable
fun GirlBox(
    modifier: Modifier = Modifier,
    girlWidth: Dp,
    girlHeight: Dp,
    girlOffsetX: Dp,
) {
    Box(
        modifier = modifier
            .size(girlWidth, girlHeight)
            .offset(x = girlOffsetX, y = 6.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.girl),
            contentDescription = "Girl",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = -1f,
                    transformOrigin = TransformOrigin.Center
                ),
            contentScale = ContentScale.FillBounds
        )
    }
}