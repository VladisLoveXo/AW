package app.awake.ui.messages

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.data.MESSAGES
import app.awake.model.DataViewModel
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme

@Composable
fun ActiveMessagesView(
    messages: MESSAGES,
    dataViewModel: DataViewModel?,
    modifier: Modifier = Modifier,
    openAction: ()->Unit
) {

    var animation by remember { mutableStateOf(false) }
    val imageScale: Float by animateFloatAsState(
        targetValue = if (animation) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    val imageAlpha: Float by animateFloatAsState(
        targetValue = if (animation) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        animation = true
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .offset(y = 46.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.messages_background),
                contentDescription = "messages_background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale = imageScale)
                    .alpha(alpha = imageAlpha)
            )
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            TextButton(onClick = {
                openAction.invoke()
            }) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CochinText(
                        text = stringResource(id = R.string.messages_title),
                        size = 30,
                        weight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 6.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.chevron_forward),
                        contentDescription = "messages_open_forward",
                        modifier = Modifier
                            .padding(start = 18.dp)
                    )
                }
            }

            CochinText(
                text = stringResource(id = R.string.messages_info),
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
            )

            MessagesScrollView(
                messages = messages,
                dataViewModel = dataViewModel,
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview18() {
    AwakeTheme {
        ActiveMessagesView(
            messages = mutableListOf(),
            dataViewModel = null,
        ) {}
    }
}