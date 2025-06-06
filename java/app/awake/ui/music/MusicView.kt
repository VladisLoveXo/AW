package app.awake.ui.music

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MusicItem
import app.awake.R
import app.awake.SoundItem
import app.awake.player.MusicPlayer
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

// todo: glow on controls
@Composable
fun MusicView(
    musicItem: MusicItem,
    player: MusicPlayer,
    modifier: Modifier = Modifier
) {
    var isReady by remember { mutableStateOf(player.isPlaying.value!!) }

    if (!isReady) {
        LaunchedEffect(Unit) {
            delay(1.5.seconds)
            isReady = true
        }
    }

    Crossfade(targetState = isReady) { ready ->
        when (ready) {
            true -> MusicViewControls(isPreview = false, musicItem = musicItem, player = player, modifier = modifier)
            false -> MusicViewControls(isPreview = true, musicItem = musicItem, player = player, modifier = modifier) { isReady = true }
        }
    }
}

@Composable
fun MusicViewControls(
    isPreview: Boolean,
    musicItem: MusicItem,
    player: MusicPlayer,
    modifier: Modifier = Modifier,
    setReady: (()->Unit)? = null
) {
    val context = LocalContext.current

    var animation by remember { mutableStateOf(false) }

    val previewAlpha: Float by animateFloatAsState(
        targetValue = if (animation) 1f else 0f,
        animationSpec = tween(if (isPreview) 1500 else 500)
    )
    val controlsAlpha: Float by animateFloatAsState(
        targetValue = if (animation) 1f else 0f,
        animationSpec = tween(1000)
    )

    val isPlaying = player.isPlaying.observeAsState()
    val activeSounds = player.activeSounds.observeAsState()

    val progressAlpha: Float by animateFloatAsState(
        targetValue = if (isPlaying.value!!) 1f else 0f,
        animationSpec = tween(500)
    )

    var playButtonAnimation by remember { mutableStateOf(false) }
    val playButtonScale: Float by animateFloatAsState(
        targetValue = if (playButtonAnimation) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        animation = true
        playButtonAnimation = player.isPlaying.value!!

        player.prepare(musicItem, context = context)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
            .alpha(previewAlpha)
    ) {
        CochinText(
            text = musicItem.name,
            size = 42,
            weight = FontWeight.Bold,
            spacing = 6f,
            modifier = Modifier
                .offset(y = 18.dp)
        )

        var bitmapState = BitmapFactory.decodeStream(
            LocalContext.current.assets.open("musics/${musicItem.image}")
        )

        if (bitmapState != null) {
            Box(contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Image(
                        bitmap = bitmapState.asImageBitmap(),
                        contentDescription = "music_image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setReady?.invoke()
                            }
                    )

                    PlayerProgressView(
                        player = player,
                        modifier = Modifier
                            .offset(y = 24.dp)
                            .alpha(progressAlpha)
                    )
                }

                if (!isPreview) {
                    RoundedButton(
                        image = if (isPlaying.value!!) R.drawable.pause_fill else R.drawable.play_fill,
                        modifier = Modifier
                            .scale(playButtonScale)
                    ) {
                        if (isPlaying.value == false) {
                            player.play()
                        } else {
                            player.pause()
                        }

                        playButtonAnimation = player.isPlaying.value!!
                    }
                }
            }
        }

        if (!isPreview) {
            Column(
                modifier = Modifier
                    .alpha(controlsAlpha)
                    .padding(top = 12.dp)
            ) {
                CochinText(
                    text = musicItem.about,
                    style = FontStyle.Italic,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .padding(all = 18.dp)
                )

                Crossfade(targetState = activeSounds) { sounds ->
                    SoundsGroupView(
                        player = player,
                        soundItems = musicItem.sounds,
                        activeSounds = sounds.value!!,
                        modifier = Modifier
                            .padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview26() {
    AwakeTheme {
        MusicView(
            musicItem = MusicItem(
                name = "Valley",
                about = "let this be your secret valley of thoughts, where you can always relax and think. Choose what you want to listen to and enjoy.",
                image = "Valley/valley.png",
                sounds = mutableListOf(
                    SoundItem(name = "Water", path = "", image = "Valley/water.png", selected = true),
                    SoundItem(name = "Forest", path = "", image = "Valley/forest.png", selected = true),
                    SoundItem(name = "Pad", path = "", image = "Valley/pad.png", selected = true),
                    SoundItem(name = "Guitar", path = "", image = "Valley/guitar.png", selected = true),
                    SoundItem(name = "Piano", path = "", image = "Valley/piano.png", selected = true),
                    SoundItem(name = "Lamp", path = "", image = "Valley/lamp.png", selected = true),
                )
            ),
            player = MusicPlayer()
        )
    }
}