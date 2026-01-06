package jp.co.tai.screens.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.tai.R
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.settings.utils.MusicManager
import jp.co.tai.screens.settings.utils.SoundEffectPlayer
import jp.co.tai.screens.start.MainButton
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.Typography

@Composable
fun SettingsScreenUi(
    storage: SnowboundStorage,
    back: () -> Unit
) {

    val originalMusicLevel = remember { storage.getMusic() }
    val originalSoundLevel = remember { storage.getSound() }
    var saved by remember { mutableStateOf(false) }
    var musicLevel by remember { mutableIntStateOf(storage.getMusic()) }
    var soundLevel by remember { mutableIntStateOf(storage.getSound()) }
    val musicProgress = remember { Animatable(levelToProgress(musicLevel)) }
    val soundProgress = remember { Animatable(levelToProgress(soundLevel)) }

    LaunchedEffect(musicLevel) { musicProgress.animateTo(levelToProgress(musicLevel)) }
    LaunchedEffect(soundLevel) { soundProgress.animateTo(levelToProgress(soundLevel)) }

    DisposableEffect(Unit) {
        onDispose {
            if (!saved) {
                storage.setMusic(originalMusicLevel)
                storage.setSound(originalSoundLevel)

                MusicManager.setVolumeLevel(originalMusicLevel)
                SoundEffectPlayer.setVolumeLevel(originalSoundLevel)
                if (originalMusicLevel > 0) MusicManager.play() else MusicManager.pause()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        Background(bgRes = R.drawable.game_bg)

        SquareButton(
            btnRes = R.drawable.back_btn,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 50.dp)
        ) { back() }

        Text(
            text = stringResource(R.string.settings).uppercase(),
            style = Typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 68.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .aspectRatio(1.7f)
            ) {
                Background(bgRes = R.drawable.privacy_bg)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.music),
                        style = Typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SquareButton(
                            btnRes = R.drawable.minus_btn,
                            cooldownMillis = 0L,
                            modifier = Modifier.weight(0.15f)
                        ) {
                            if (musicLevel > 0) {
                                musicLevel--
                                MusicManager.setVolumeLevel(musicLevel)
                                if (musicLevel > 0) MusicManager.play() else MusicManager.pause()
                            }
                        }

                        VolumeBar(
                            progress = musicProgress.value,
                            modifier = Modifier.weight(0.7f)
                        )

                        SquareButton(
                            btnRes = R.drawable.plus_btn,
                            cooldownMillis = 0L,
                            modifier = Modifier.weight(0.15f)
                        ) {
                            if (musicLevel < 4) {
                                musicLevel++
                                MusicManager.setVolumeLevel(musicLevel)
                                if (musicLevel > 0) MusicManager.play() else MusicManager.pause()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.sound),
                        style = Typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SquareButton(
                            btnRes = R.drawable.minus_btn,
                            cooldownMillis = 0L,
                            modifier = Modifier.weight(0.15f)
                        ) {
                            if (soundLevel > 0) {
                                soundLevel--
                                SoundEffectPlayer.setVolumeLevel(soundLevel)
                            }
                        }

                        VolumeBar(
                            progress = soundProgress.value,
                            modifier = Modifier.weight(0.7f)
                        )

                        SquareButton(
                            btnRes = R.drawable.plus_btn,
                            cooldownMillis = 0L,
                            modifier = Modifier.weight(0.15f)
                        ) {
                            if (soundLevel < 4) {
                                soundLevel++
                                SoundEffectPlayer.setVolumeLevel(soundLevel)
                            }
                        }
                    }
                }
            }

            MainButton(
                buttonText = stringResource(id = R.string.save),
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(4f)
            ){
                storage.setMusic(musicLevel)
                storage.setSound(soundLevel)

                saved = true
                back()
            }
        }
    }
}