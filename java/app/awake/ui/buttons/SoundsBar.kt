package app.awake.ui.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.SOUNDS
import app.awake.Sound
import app.awake.player.SoundPlayer
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.theme.AwakeTheme

@Composable
fun SoundsBar(
    sounds: SOUNDS,
    selected: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onSoundChange: (String) -> Unit
) {
    val context = LocalContext.current

    val player by remember { mutableStateOf(SoundPlayer()) }

    fun playSound(id: Int) {
        player.player?.stop()

        if (player.prepare(resId = id, context = context)) {
            player.player?.start()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    )
    {
        CochinSubtitleText(text = stringResource(id = R.string.settings_notitifcations_sound))

        Row {
            for (sound in sounds) {
                RoundedButton(
                    image = sound.image,
                    enabled = enabled,
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp)
                        .alpha(if (sound.name == selected) 1f else 0.3f)
                ) {
                    playSound(sound.resourceId)

                    onSoundChange(sound.name)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview13() {
    AwakeTheme {
        SoundsBar(sounds = mutableListOf<Sound>(
            Sound(name = "", resourceId = R.raw.one, image = R.drawable.pianokeys)
        ), selected = "") {}
    }
}