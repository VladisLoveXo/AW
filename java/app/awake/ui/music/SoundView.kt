package app.awake.ui.music

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.SoundItem
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun SoundView(
    soundItem: SoundItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    action: (Boolean)->Unit,
) {
    var bitmapState = BitmapFactory.decodeStream(
        LocalContext.current.assets.open("musics/${soundItem.image}")
    )

    val width = 80.dp
    val height = 100.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(height)
            .clickable {
                action(!isSelected)
            },
    ) {
        if (bitmapState != null) {
            Image(
                bitmap = bitmapState.asImageBitmap(),
                contentDescription = "sound_image",
                modifier = Modifier
                    .alpha(if (isSelected) 1f else 0.2f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .width(width)
                .height(height)
        ) {
            CochinText(
                text = soundItem.name,
                size = 18,
                align = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview24() {
    AwakeTheme {
        SoundView(
            SoundItem(
            name = "Water",
            path = "",
            image = "Valley/water.png",
            selected = true
        ), isSelected = true) {}
    }
}