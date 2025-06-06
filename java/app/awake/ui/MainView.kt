package app.awake.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.awake.ConfigurationController
import app.awake.model.DataViewModel
import app.awake.ui.MainStages.GENERAL
import app.awake.ui.MainStages.SETTINGS
import app.awake.ui.MainStages.START_TEXT
import app.awake.ui.stages.GeneralView
import app.awake.ui.stages.SettingsView
import app.awake.ui.stages.StartTextView
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


object MainStages {
    const val GENERAL = "general"
    const val SETTINGS = "settings"
    const val START_TEXT = "start_text"
}

@Composable
fun MainViewNavHost(
    dataViewModel: DataViewModel? = null,
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            GENERAL,
            enterTransition = { fadeIn(initialAlpha = 0f, animationSpec = tween(1000)) }
        ) {
            GeneralView(dataViewModel = dataViewModel)
        }

        composable(
            SETTINGS,
            enterTransition = { fadeIn(initialAlpha = 0f, animationSpec = tween(1000)) },
        ) {
            SettingsView(
                showCheckmark = true,
                dataViewModel = dataViewModel
            ) {
                navController.navigate(GENERAL)
            }
        }

        composable(START_TEXT) {
            StartTextView() {
                navController.navigate(SETTINGS)
                dataViewModel?.isShowSplash?.value = true
            }
        }
    }
}

@Composable
fun MainView(
    startDestination: String,
    dataViewModel: DataViewModel? = null,
    modifier: Modifier = Modifier
) {
    val paddingFromTop: Dp by animateDpAsState(
        targetValue = if (dataViewModel!!.isShowSplash.value!!)
            (if (ConfigurationController.getInstance().isSmallScreen()) 40.dp else Theme.Sizes.paddingFromTop.dpSizeValue.height)
        else ( if (ConfigurationController.getInstance().isSmallScreen()) 0.dp else 20.dp),
        animationSpec = tween(1000)
    )

    LaunchedEffect(Unit)
    {
        if (startDestination == START_TEXT) {
            dataViewModel?.isShowSplash?.value = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = paddingFromTop),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    )
    {
        MainViewNavHost(
            dataViewModel = dataViewModel,
            startDestination = startDestination
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    AwakeTheme {
        MainView(startDestination = GENERAL)
    }
}