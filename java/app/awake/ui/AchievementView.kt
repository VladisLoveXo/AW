package app.awake.ui

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.player.SoundPlayer
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@Composable
fun AchievementView(
    number: Int,
    modifier: Modifier = Modifier,
    completion: (()->Unit)
) {
    val context = LocalContext.current

    var showBack by remember { mutableStateOf(false) }
    val backAlpha: Float by animateFloatAsState(
        targetValue = if (showBack) 0.8f else 0f,
        animationSpec = tween(2000, easing = EaseOut)
    )
    val backScale: Float by animateFloatAsState(
        targetValue = if (showBack) 1.2f else 1f,
        animationSpec = tween(2000, easing = EaseOut)
    )

    var showNumber by remember { mutableStateOf(false) }
    val numberAlpha: Float by animateFloatAsState(
        targetValue = if (showNumber) 1f else 0f,
        animationSpec = tween(3000, easing = EaseIn)
    )

    var showGratitude by remember { mutableStateOf(false) }
    val gratitudeAlpha: Float by animateFloatAsState(
        targetValue = if (showGratitude) 1f else 0f,
        tween(2000, easing = EaseIn)
    )

    var player by remember { mutableStateOf(SoundPlayer()) }

    LaunchedEffect(Unit) {
        if (player.prepare(R.raw.achievement, context)) {
            player.player!!.start()
        }

        showBack = true
        showNumber = true

        delay(2.seconds)

        showGratitude = true

        delay(4.seconds)

        showBack = false
        showNumber = false
        showGratitude = false

        delay(1.seconds)

        completion()
    }

    Box {
        Image(
            painter = painterResource(id = R.drawable.achievement_back),
            contentDescription = "achievement_back_image",
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .fillMaxSize()
                .alpha(backAlpha)
                .scale(backScale)
        )

        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // todo: texts glow

            CochinText(
                text = stringResource(id = R.string.entry_achievement_start) + " $number " + stringResource(id = R.string.entry_achievement_end),
                size = 22,
                modifier = Modifier
                    .alpha(numberAlpha)
            )

            CochinText(
                text = stringResource(id = R.string.thank_you),
                size = 36,
                modifier = Modifier
                    .alpha(gratitudeAlpha)
                    .padding(top = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview45() {
    AwakeTheme {
        AchievementView(100) {}
    }
}