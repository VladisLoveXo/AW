package app.awake.ui.stages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MUSIC_TOOLS
import app.awake.MusicItem
import app.awake.MusicTool
import app.awake.R
import app.awake.RemoteNotificationsController
import app.awake.model.DataViewModel
import app.awake.player.MusicPlayer
import app.awake.ui.music.MusicView
import app.awake.ui.music.MusicsList
import app.awake.ui.music.MusicsPreviewList
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@Composable
fun MusicsView(
    musics: MUSIC_TOOLS,
    selectedTab: Int = -1,
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier
) {
    val isPreview = dataViewModel.isShowMusicsPreview.observeAsState()
    var selected by remember { mutableStateOf(selectedTab) }

    fun stopPreview() {
        dataViewModel.isShowMusicsPreview.value = false
    }

    LaunchedEffect(Unit) {
        delay(2.seconds)
        stopPreview()
    }

    Crossfade(targetState = selected, label = "musics_view_selelcted") {
        when (it) {
            -1 -> {
                RemoteNotificationsController.getInstance().sendNotification(
                    RemoteNotificationsController.NotificationName.musicIsClosed)

                Crossfade(targetState = isPreview.value!!, label = "musics_view_isPreview") { preview ->
                    when (preview) {
                        true -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 18.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ambient_black),
                                    contentDescription = "ambient_preview",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .alpha(0.8f)
                                        .clickable {
                                            stopPreview()
                                        }
                                )

                                CochinText(
                                    text = stringResource(id = R.string.ambient),
                                    size = 46,
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }
                        false -> {
                            MusicsPreviewList(musics = musics) {
                                selected = it
                            }
                        }
                    }
                }
            }

            else -> {
                RemoteNotificationsController.getInstance().sendNotification(RemoteNotificationsController.NotificationName.musicIsOpened)

                MusicsList(musics = musics, selectedTab = selected) {
                    selected = it
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview52() {
    AwakeTheme {
        MusicsView(
            musics = mutableListOf(
                MusicTool(
                    item = MusicItem(
                        name = "Valley",
                        about = "Let this be your secret valley of thoughts, where you can always relax and think. Сhoose the sounds you want to hear and enjoy.",
                        image = "Valley/valley.png",
                        sounds = mutableListOf()
                    ),
                    player = MusicPlayer()
                )
            ),
            dataViewModel = DataViewModel()
        )
    }
}