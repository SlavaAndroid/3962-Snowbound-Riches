package jp.co.tai.screens.start

import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import jp.co.tai.R
import jp.co.tai.screens.loading.Background
import jp.co.tai.ui.theme.BlueGradient
import jp.co.tai.ui.theme.OrangeGradient
import jp.co.tai.ui.theme.Typography

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    textStyle: TextStyle = Typography.bodyMedium,
    cooldownMillis: Long = 1000L,
    enabled: Boolean = true,
    buttonClickable: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .peckPress(
                onPeck = buttonClickable,
                cooldownMillis = cooldownMillis,
                isChickenReady = enabled
            ),
        contentAlignment = Alignment.Center
    ) {
        Background(bgRes = R.drawable.main_btn)
        Text(
            text = buttonText,
            style = textStyle
        )
    }
}

@Composable
fun SquareButton(
    modifier: Modifier = Modifier,
    btnRes: Int,
    btnMaxWidth: Float = 0.07f,
    cooldownMillis: Long = 1000L,
    btnEnabled: Boolean = true,
    btnClickable: () -> Unit
) {
    Image(
        painter = painterResource(id = btnRes),
        contentDescription = "Button",
        modifier = modifier
            .fillMaxWidth(btnMaxWidth)
            .aspectRatio(1f)
            .peckPress(
                onPeck = btnClickable,
                cooldownMillis = cooldownMillis,
                isChickenReady = btnEnabled
            )
    )
}

@Composable
fun Modifier.peckPress(
    cooldownMillis: Long = 1000L,
    isChickenReady: Boolean = true,
    onPeck: () -> Unit
): Modifier {
    var lastPeckTime by remember { mutableLongStateOf(0L) }
    var isPecking by remember { mutableStateOf(false) }

    val peckScale by animateFloatAsState(
        targetValue = if (isPecking && isChickenReady) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PeckPress"
    )

    return this
        .graphicsLayer {
            scaleX = peckScale
            scaleY = peckScale
        }
        .pointerInput(isChickenReady) {
            detectTapGestures(
                onPress = {
                    if (!isChickenReady) return@detectTapGestures

                    val now = System.currentTimeMillis()
                    if (now - lastPeckTime >= cooldownMillis) {
                        lastPeckTime = now
                        isPecking = true
                        try {
                            tryAwaitRelease()
                            onPeck()
                        } finally {
                            isPecking = false
                        }
                    }
                }
            )
        }
}

@Composable
fun ProfileButton(
    modifier: Modifier = Modifier,
    userPhoto: Any?,
    text: String,
    context: Context,
    buttonClickable: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.17f)
            .aspectRatio(1f)
            .peckPress(onPeck = buttonClickable)
    ) {
        UserProfileBox(
            userPhoto = userPhoto,
            context = context
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BlueGradient)
                .border(
                    width = 2.dp,
                    brush = OrangeGradient,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = Typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun UserProfileBox(
    modifier: Modifier = Modifier,
    userPhoto: Any?,
    context: Context
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (userPhoto != null) {
            val request = remember(userPhoto) {
                ImageRequest.Builder(context)
                    .data(userPhoto)
                    .crossfade(true)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = "User Image",
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        brush = OrangeGradient,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun rememberJumpOffsetY(
    jumpHeight: Float = 20f,
    durationMillisJ: Int = 5000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "jump_transition")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = durationMillisJ

                0f at 0
                -jumpHeight at 300
                0f at 600
                0f at durationMillisJ
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "jump_offset"
    )

    return offsetY
}