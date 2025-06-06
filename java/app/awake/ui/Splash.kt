package app.awake.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.datastore.preferences.core.edit
import app.awake.MainActivityReceiver
import app.awake.R
import app.awake.dataStore
import app.awake.model.DataViewModel
import app.awake.player.SoundPlayer
import app.awake.ui.extensions.animatePlacement
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

@Composable
fun Splash(
    duration: Int = 2000,
    playSound: Boolean,
    dataViewModel: DataViewModel?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var isShow by remember { mutableStateOf(false) }
    val imageAlpha: Float by animateFloatAsState(
        targetValue = if (isShow) 1f else 0f,
        animationSpec = tween(durationMillis = duration)
    )

    val isSplashMain by dataViewModel!!.isSplashMain.observeAsState()
    val controlsAlpha: Float by animateFloatAsState(
        targetValue = if (isSplashMain!!) 1f else 0f,
    )
    val imageSize: Dp by animateDpAsState(
        targetValue = if (isSplashMain!!) 300.dp else 80.dp,
        animationSpec = tween(500)
    )
    val rotateAngle: Float by animateFloatAsState(
        targetValue = if (isSplashMain!!) 0f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    var isPlaySound by remember { mutableStateOf(playSound) }
    val soundIcon: Int by animateIntAsState(
        targetValue = if (isPlaySound) R.drawable.speaker_wave_3 else R.drawable.speaker_slash
    )

    var player by remember { mutableStateOf(SoundPlayer()) }

    LaunchedEffect(Unit) {
        delay(0.2.seconds)

        if (playSound) {
            player.playBowl(context)
        }

        isShow = true

        delay((duration / 1000).seconds)

        dataViewModel!!.isSplashMain.value = false
    }

    Box(modifier = if (!isSplashMain!!) modifier else modifier
        .fillMaxSize()
        .background(color = Color.White)
    ) {
        Box(
            modifier = if (isSplashMain!!) modifier.fillMaxSize() else modifier.fillMaxWidth(),
            contentAlignment = if (isSplashMain!!) Alignment.Center else Alignment.TopCenter
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .animatePlacement()
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.eye),
                    contentDescription = "eye",
                    alpha = imageAlpha,
                    modifier = modifier
                        .width(imageSize)
                        .height(imageSize)
                        .animatePlacement()
                        .rotate(degrees = rotateAngle)
                        .clickable {
                            dataViewModel!!.isSplashMain.value =
                                !dataViewModel!!.isSplashMain.value!!
                        }
                )

                if (isSplashMain!!) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier
                            .alpha(controlsAlpha)
                            .offset(y = -44.dp)
                    )
                    {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 12.dp),
                            onClick = {

                            isPlaySound = !isPlaySound

                            if (!isPlaySound) {
                                player.player?.stop()
                            }

                            runBlocking {
                                context.dataStore.edit { settings ->
                                    settings[app.awake.PLAY_SOUND] = isPlaySound
                                }
                            }
                        }) {
                            Image(
                                painter = painterResource(id = soundIcon),
                                contentDescription = "speaker_wave_3",
                            )
                        }

                        TextButton(
                            modifier = Modifier
                                .padding(end = 12.dp),
                            onClick = {
                            try {
                                val intent = Intent(
                                    Settings.ACTION_APP_LOCALE_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )

                                context.startActivity(intent)
                            } finally {
                                Log.d("", "ERROR: Can't open locale settings.")
                            }
                        }) {
                            val configuration = LocalConfiguration.current
                            var locale = ConfigurationCompat.getLocales(configuration).get(0).toString()

                            if (locale.contains("_")) {
                                locale = locale.substring(locale.indexOf("_") + 1, locale.length)
                            }

                            CochinText(
                                text = locale,
                                size = 20,
                                color = Color.Gray.copy(0.6f)
                            )
                        }

                        CochinText(
                            text = "(beta)",
                            color = Color.Gray.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        if (isSplashMain!!) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            )
            {
                TextButton(onClick = {
                    MainActivityReceiver.getInstance().pushData(
                        context.resources.getString(R.string.support_info)
                    )
                }) {
                    CochinText(
                        text = stringResource(id = R.string.contact_us),
                        size = 14,
                        color = Color.Gray.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview2() {
    AwakeTheme {
        Splash(
            dataViewModel = null,
            playSound = false
        )
    }
}