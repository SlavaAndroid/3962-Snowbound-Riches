package jp.co.tai.screens.start

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import jp.co.tai.R
import jp.co.tai.SnowboundActivity
import jp.co.tai.navigation.LevelsScreen
import jp.co.tai.navigation.StartScreen
import jp.co.tai.screens.leaders.LeadersScreenUi
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.privacy.PrivacyScreenUi
import jp.co.tai.screens.profile.ProfileScreenUi
import jp.co.tai.screens.rules.RulesScreenUi
import jp.co.tai.screens.settings.SettingsScreenUi
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.Typography
import java.io.File

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun StartScreenUi(
    navController: NavController,
    storage: SnowboundStorage
) {

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val activity = context as? SnowboundActivity
    var profile by remember { mutableStateOf(false) }
    var privacy by remember { mutableStateOf(false) }
    var leaders by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    var rules by remember { mutableStateOf(false) }
    val userName by rememberUpdatedState(storage.getName())
    val filePhoto =  File(context.filesDir, "photo_image")
    val profilePhotoUri = remember(storage.getPhoto()) {
        when {
            filePhoto.exists() -> Uri.fromFile(filePhoto)
            storage.getPhoto().isNotEmpty() -> storage.getPhoto().toUri()
            else -> null
        }
    }
    val letterJumpOffsetY = rememberJumpOffsetY()

    BackHandler(enabled = true) {
        if (profile) {
            profile = false
        } else if (privacy) {
            privacy = false
        } else if (leaders) {
            leaders = false
        } else if (rules) {
            rules = false
        } else if (settings) {
            settings = false
        } else {
            activity?.finish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
    ) {
        Background(bgRes = R.drawable.game_bg)

        ProfileButton(
            userPhoto = profilePhotoUri,
            text = userName,
            context = context,
            buttonClickable = { profile = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 50.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.letter),
            contentDescription = "How to play",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.22f)
                .offset(
                    x = 20.dp,
                    y = (-10).dp + letterJumpOffsetY.dp
                )
                .graphicsLayer(
                    scaleX = -1f,
                    transformOrigin = TransformOrigin.Center
                )
                .peckPress{
                    rules = true
                }
        )

        Image(
            painter = painterResource(id = R.drawable.girl),
            contentDescription = "Girl",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.4f)
                .offset(x = (-34).dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 50.dp)
                .fillMaxWidth(0.33f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainButton(
                buttonText = "Play",
                textStyle = Typography.bodyLarge,
                modifier = Modifier
                    .aspectRatio(3.3f)
            ) { navController.navigate(LevelsScreen) }

            MainButton(
                buttonText = "Leaderboard",
                modifier = Modifier
                    .aspectRatio(4.5f)
            ) { leaders = true }

            MainButton(
                buttonText = "Settings",
                modifier = Modifier
                    .aspectRatio(4.5f)
            ) { settings = true }

            MainButton(
                buttonText = "Privacy Policy",
                modifier = Modifier
                    .aspectRatio(4.5f)
            ) { privacy = true }
        }


        if (privacy) {
            PrivacyScreenUi { privacy = false }
        }

        if (profile) {
            ProfileScreenUi(storage) { navController.navigate(StartScreen) {
                popUpTo(StartScreen) { inclusive = true }
            } }
        }

        if (leaders) {
            LeadersScreenUi (storage) { leaders = false }
        }

        if (settings) {
            SettingsScreenUi (storage) { settings = false }
        }

        if (rules){
            RulesScreenUi { rules = false }
        }
    }
}