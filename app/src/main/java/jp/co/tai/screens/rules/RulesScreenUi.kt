package jp.co.tai.screens.rules

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import jp.co.tai.R
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.ui.theme.Typography

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun RulesScreenUi(back: () -> Unit) {

    val isInPreview = LocalInspectionMode.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topPadding = remember { (screenHeightDp * 0.08f).dp }
    val bottomPadding = remember { (screenHeightDp * 0.1f).dp }
    val verticalPadding = remember { (screenHeightDp * 0.12f).dp }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.8f))
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        Background(bgRes = R.drawable.game_bg)

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 50.dp)
                .padding(horizontal = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.how_to_play).uppercase(),
                style = Typography.labelLarge,
                fontSize = 45.sp,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Background(bgRes = R.drawable.privacy_bg)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = verticalPadding)
                        .padding(top = topPadding, bottom = bottomPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
Goal
Reach the target score before you run out of moves.

Gameplay
• The game field is a grid filled with snowbound elements.
• Tap on two or more identical adjacent elements to collect them.
• Elements must touch horizontally or vertically.
• Single elements cannot be collected.

Scoring
• Each collected element gives you points.
• The more elements you collect at once, the higher your score.

Moves
• Each successful match uses 1 move.
• If you run out of moves before reaching the goal — the game is over.

Winning & Losing
• You win if your score reaches the goal.
• You lose if there are no moves left or no possible matches.

Restart
• You can restart the level at any time using the restart button.
    """.trimIndent(),
                        textAlign = TextAlign.Justify,
                        style = Typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        SquareButton(
            btnRes = R.drawable.close_btn,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 50.dp)
        ) { back() }
    }
}