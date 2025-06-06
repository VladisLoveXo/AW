package app.awake.ui.music

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import app.awake.MUSIC_TOOLS
import app.awake.R
import app.awake.ui.controls.IndexIndicator
import app.awake.ui.stages.GeneralTab
import app.awake.ui.theme.AwakeTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicsList(
    musics: MUSIC_TOOLS,
    selectedTab: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int)->Unit
) {
    var pagerState = rememberPagerState(initialPage = selectedTab) { musics.count() }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // todo (need?): onSelect.invoke(page)
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        IndexIndicator(count = musics.count(), selected = selectedTab)

        Box(contentAlignment = Alignment.BottomCenter) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                val music = musics[page]
                MusicView(musicItem = music.item, player = music.player) // todo: isReady
            }

            IconButton(onClick = { // todo: padding from bottom
                onSelect(-1)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.arrowshape_backward_fill),
                    contentDescription = "musics_list_back"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview54() {
    AwakeTheme {
        MusicsList(musics = mutableListOf(), selectedTab = 0) {}
    }
}