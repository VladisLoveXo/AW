package app.awake.ui.music

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.player.MusicPlayer
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay

@Composable
fun PlayerProgressView(player: MusicPlayer, modifier: Modifier = Modifier) {

    var isEditing by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(player.currentTime().toFloat()) }

    LaunchedEffect(Unit) {
        while (true) {
            if (!isEditing) {
                value = player.currentTime().toFloat()
            }

            delay(500)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val current = DateUtils.formatElapsedTime(player.currentTime().toLong())
            CochinText(text = current, size = 16)

            val duration = DateUtils.formatElapsedTime(player.duration().toLong())
            CochinText(text = duration, size = 16)
        }

        Slider(
            value = value,
            valueRange = 0f..player.duration().toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.Black,
                inactiveTrackColor = Color.LightGray
            ),
            onValueChange = { newValue ->
                isEditing = true
                value = newValue
            },
            onValueChangeFinished = {
                player.setTime(value.toInt())
                isEditing = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview27() {
    AwakeTheme {
        PlayerProgressView(player = MusicPlayer())
    }
}