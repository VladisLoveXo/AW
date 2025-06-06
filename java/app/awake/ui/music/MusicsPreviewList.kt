package app.awake.ui.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MUSIC_TOOLS
import app.awake.R
import app.awake.ui.theme.AwakeTheme


@Composable
fun MusicsPreviewList(
    musics: MUSIC_TOOLS,
    modifier: Modifier = Modifier,
    onSelect: (Int)->Unit
) {
    var localUriHandler = LocalUriHandler.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.crane_black),
            contentDescription = "musics_crane",
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
        )

        musics.forEachIndexed { index, music ->
            MusicPreview(
                item = music.item,
                extra =  if (music.player.isPlaying.value!!) stringResource(id = R.string.playing_now) else "",
                modifier = Modifier
                    .padding(bottom = 18.dp)
            ) {
                onSelect(index)
            }

            if (index != musics.count() - 1) {
                Divider(
                    modifier = Modifier
                        .padding(start = 28.dp, end = 28.dp)
                        .alpha(0.6f)
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.bandcamp),
            contentDescription = "bandcamp_link",
            modifier = Modifier
                .width(160.dp)
                .padding(bottom = 18.dp)
                .clickable {
                    localUriHandler.openUri("https://ourhut.bandcamp.com")
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview53() {
    AwakeTheme {
        MusicsPreviewList(musics = mutableListOf()) {}
    }
}