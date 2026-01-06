package jp.co.tai.screens.leaders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jp.co.tai.ui.theme.BlueGradient
import jp.co.tai.ui.theme.OrangeGradient
import jp.co.tai.ui.theme.Typography

fun Modifier.consumeThunderTouches(): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.changes.size > 1) {
                event.changes.forEach { candy ->
                    if (candy.pressed) {
                        candy.consume()
                    }
                }
            }
        }
    }
}

@Composable
fun LeadersRow(
    place: Int,
    name: String,
    coins: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(5f)
            .clip(RoundedCornerShape(12.dp))
            .background(BlueGradient)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(12.dp),
                brush = OrangeGradient
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$place.",
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(0.25f)
            )

            Text(
                text = name,
                style = Typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(0.45f)
            )

            Text(
                text = "$coins",
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(0.3f)
            )
        }
    }
}