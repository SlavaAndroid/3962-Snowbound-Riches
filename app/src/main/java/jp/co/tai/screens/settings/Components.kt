package jp.co.tai.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.co.tai.ui.theme.BlueGradient
import jp.co.tai.ui.theme.OrangeGradient

fun levelToProgress(level: Int): Float = when (level.coerceIn(0, 4)) {
    4 -> 1.0f
    3 -> 0.75f
    2 -> 0.5f
    1 -> 0.25f
    else -> 0.0f
}

@Composable
fun VolumeBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(6f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(18.dp),
                brush = OrangeGradient
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .padding(3.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BlueGradient)
        )
    }
}