package app.awake

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import app.awake.model.DataViewModel
import app.awake.ui.AchievementView
import app.awake.ui.MainStages.GENERAL
import app.awake.ui.MainStages.START_TEXT
import app.awake.ui.MainView

import app.awake.ui.Splash
import app.awake.ui.popups.InfoView
import app.awake.ui.popups.QuestionView
import app.awake.ui.popups.TextFieldView
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// todo: check all warnings
// todo: all todo's

//@HiltAndroidApp
//@AndroidEntryPoint // todo
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AwakeTheme {
                // A surface container using the 'background' color from the theme
                Surface( 
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // MaterialTheme.colorScheme.background
                )
                {
                    ContentView()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // disable changing portrait mode
    }
}

enum class Popup {
    none,
    textField,
    info,
    question,
}

@Composable
fun ContentView(
    dataViewModel: DataViewModel = hiltViewModel<DataViewModel>(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    dataViewModel.initialize(context)

    var popup by remember { mutableStateOf(Popup.none) }
    var popupData by remember { mutableStateOf<Any?>(null) }

    fun hidePopup() {
        popup = Popup.none
        popupData = null
    }

    MainActivityReceiver.getInstance().dataReceived = { data ->
        popupData = data

        if (data is TextFieldInfo) {
            popup = Popup.textField
        } else if (data is String) {
            popup = Popup.info
        } else if (data is QuestionInfo) {
            popup = Popup.question
        }
    }

    val isSplashMain by dataViewModel.isSplashMain.observeAsState()

    val isShowSplash by dataViewModel.isShowSplash.observeAsState()
    val splashAlpha: Float by animateFloatAsState(
        targetValue = if (isShowSplash!!) 1f else 0f,
        animationSpec = tween(1000)
    )

    var isAchievementViewShowed by remember { mutableStateOf(false) }

    val enterCounter = dataViewModel.enterCount
    val firstEnter = (enterCounter == 0)
    val showAchievementView = ((enterCounter + 1) % 100) == 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (!isSplashMain!!) {
            MainView(
                startDestination = if (firstEnter) START_TEXT else GENERAL,
                dataViewModel = dataViewModel
            )
        }

        Crossfade(targetState = isAchievementViewShowed, label = "isAchievementViewShowed") { showed ->
            if (showAchievementView && !showed) {
                AchievementView(number = enterCounter + 1) {
                    isAchievementViewShowed = true
                }
            }

            if (!showAchievementView || showed) {
                var playSound: Boolean = true
                runBlocking {
                    playSound = context.dataStore.data.first()[app.awake.PLAY_SOUND] ?: true
                }

                Splash(
                    duration = if (firstEnter) 5000 else 2000,
                    playSound = playSound,
                    dataViewModel = dataViewModel,
                    modifier = Modifier
                        .alpha(alpha = splashAlpha)
                )
            }
        }

        Crossfade(targetState = popup, label = "popup_crossfade") {
            if (it != Popup.none) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.4f))
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                hidePopup()
                            }
                    ) {
                        when (popup) {
                            Popup.textField -> {
                                val info = popupData as TextFieldInfo

                                TextFieldView(
                                    text = info.text,
                                    placeholder = R.string.text_placeholder,
                                    action = { result ->
                                        info.action.invoke(result)
                                        hidePopup()
                                    }
                                )
                            }

                            Popup.info -> {
                                val info = popupData as String
                                
                                InfoView(info) {
                                    hidePopup()
                                }
                            }

                            Popup.question -> {
                                val question = popupData as QuestionInfo

                                QuestionView(question = question.question) { res ->
                                    question.action(res)
                                    hidePopup()
                                }
                            }

                            else -> {}
                        }

                        if (popup == Popup.textField) {
                            Spacer(modifier = Modifier)
                            Spacer(modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    AwakeTheme {
        ContentView()
    }
}