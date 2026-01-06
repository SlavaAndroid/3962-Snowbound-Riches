package jp.co.tai.screens.snowbound

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.tai.R
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.MainButton
import jp.co.tai.ui.theme.Typography

@Composable
fun GameOverScreenUi(
    winLevel: Boolean,
    score: Int,
    totalScore: Int,
    level: Int,
    restartOrNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        if (winLevel){
            Image(
                painter = painterResource(id = R.drawable.super_text),
                contentDescription = "Super",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 80.dp)
                    .rotate(-45f)
                    .fillMaxWidth(0.33f),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (winLevel) stringResource(id = R.string.you_win) else
                    stringResource(id = R.string.you_lose),
                style = Typography.bodyLarge
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1.8f)
            ){
                Background(bgRes = R.drawable.privacy_bg)

                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Level: $level",
                        style = Typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Score: $score",
                        style = Typography.bodyMedium,
                    )

                    Text(
                        text = "Total score: $totalScore",
                        style = Typography.bodyMedium,
                    )
                }
            }

            MainButton(
                buttonText = if (winLevel) stringResource(id = R.string.next_level) else stringResource(
                    id = R.string.try_again
                ),
                textStyle = Typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(4f)
            ) { restartOrNext() }
        }
    }
}