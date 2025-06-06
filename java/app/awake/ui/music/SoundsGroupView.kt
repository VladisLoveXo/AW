package app.awake.ui.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.ACTIVE_SOUNDS
import app.awake.SOUND_ITEMS
import app.awake.SoundItem
import app.awake.player.MusicPlayer
import app.awake.ui.theme.AwakeTheme

@Composable
fun SoundsGroupView(
    player: MusicPlayer,
    soundItems: SOUND_ITEMS,
    activeSounds: ACTIVE_SOUNDS,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            items(soundItems) { item ->
                val isSelected = activeSounds.contains(item)

                SoundView(
                    soundItem = item,
                    isSelected = isSelected,
                    modifier = Modifier
                        .padding(all = 6.dp)
                ) {
                    if (!isSelected) {
                        activeSounds.add(item)
                    } else {
                        activeSounds.remove(item)
                    }

                    player.setActiveSounds(activeSounds)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview25() {
    AwakeTheme {
        SoundsGroupView(
            player = MusicPlayer(),
            soundItems = mutableListOf(
                SoundItem(name = "Water", path = "", image = "Valley/water.png", selected = true),
                SoundItem(name = "Forest", path = "", image = "Valley/forest.png", selected = true),
                SoundItem(name = "Pad", path = "", image = "Valley/pad.png", selected = true),
                SoundItem(name = "Guitar", path = "", image = "Valley/guitar.png", selected = true),
                SoundItem(name = "Piano", path = "", image = "Valley/piano.png", selected = true),
                SoundItem(name = "Lamp", path = "", image = "Valley/lamp.png", selected = true),
            ),
            activeSounds = mutableSetOf(),
        )
    }
}