package app.awake.ui.music

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MusicItem
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun MusicPreview(
    item: MusicItem,
    showAbout: Boolean = true,
    extra: String = "",
    extraImage: Int? = null,
    modifier: Modifier = Modifier,
    action: (()->Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                action?.invoke()
            }
    ) {
        CochinText(
            text = item.name,
            size = 42,
            weight = FontWeight.Bold,
            spacing = 6f,
            modifier = Modifier
                .offset(y = 18.dp)
        )

        var bitmapState = BitmapFactory.decodeStream(
            LocalContext.current.assets.open("musics/${item.image}")
        )

        if (bitmapState != null) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmapState.asImageBitmap(),
                    contentDescription = "music_image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if (!extra.isEmpty()) {
                    CochinText(
                        text = extra,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f))
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color.Black,
                                ambientColor = Color.Black
                            )
                    )
                } else if (extraImage != null) {
                    Image(
                        painter = painterResource(id = extraImage),
                        contentDescription = "music_preview_extra_image",
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }
        }

        if (showAbout) {
            CochinText(
                text = item.about,
                style = FontStyle.Italic,
                align = TextAlign.Center,
                modifier = Modifier
                    .padding(all = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview51() {
    AwakeTheme {
        MusicPreview(
            item = MusicItem(
                name = "Valley",
                about = "Let this be your secret valley of thoughts, where you can always relax and think. Сhoose the sounds you want to hear and enjoy.",
                image = "Valley/valley.png",
                sounds = mutableListOf()
            ),
            extra = "Playing now..."
        )
    }
}