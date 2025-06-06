package app.awake.ui.stages

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.awake.ButtonInfo
import app.awake.ConfigurationController
import app.awake.MainActivityReceiver
import app.awake.NotificationsController
import app.awake.R
import app.awake.RemoteNotificationsController
import app.awake.dataStore
import app.awake.model.DataViewModel
import app.awake.player.MusicPlayer
import app.awake.ui.buttons.ButtonsBar
import app.awake.ui.controls.IndexIndicator
import app.awake.ui.messages.ActiveMessagesView
import app.awake.ui.messages.MessagesView
import app.awake.ui.stages.GeneralStages.GENERAL
import app.awake.ui.stages.GeneralStages.SETTINGS
import app.awake.ui.stages.GeneralStages.TEXT
import app.awake.ui.texts.DailyTextView
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import app.awake.ui.tips.GeneralTip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

object GeneralStages {
    const val GENERAL = "general"
    const val SETTINGS = "settings"
    const val TEXT = "text"
}

enum class GeneralSubStage {
    none,
    messages,
}

enum class GeneralTab {
    trees {
        override val index: Int = 0
    },
    general {
        override val index: Int = 1
    },
    musics {
        override val index: Int = 2
    };

    abstract val index: Int
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GeneralView(
    dataViewModel: DataViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = ConfigurationController.getInstance()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            MainActivityReceiver.getInstance().pushData(
                context.resources.getString(R.string.notifications_disabled)
            )
        }
    }

    var player by remember { mutableStateOf(MusicPlayer()) }

    val messages by dataViewModel!!.messages.observeAsState()
    val activeMessages by dataViewModel!!.activeMessages.observeAsState()

    var navController = rememberNavController()
    var subStage by remember { mutableStateOf(GeneralSubStage.none) }
    var pagerState = rememberPagerState(initialPage = GeneralTab.general.index) { GeneralTab.values().count() }

    var isShowTip by remember { mutableStateOf(false) }
    val tipAlpha: Float by animateFloatAsState(
        targetValue = if (isShowTip) 1f else 0f,
        animationSpec = tween(500)
    )


    var isScrollDisabled by remember { mutableStateOf(false) }
    var isShowButtonsBar by remember { mutableStateOf(true) }

    val buttonsBarAlpha: Float by animateFloatAsState(
        targetValue = if (isShowButtonsBar) 1f else 0f
    )
    val bottomPadding: Dp by animateDpAsState(
        targetValue = if (isShowButtonsBar) Theme.Sizes.paddingFromTabBar.dpSizeValue.height else 0.dp
    )


    fun setupNotifications(changeAll: Boolean = false) {
        if (dataViewModel != null) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // permission granted
            } else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            NotificationsController.getInstance().setupNotifications(
                context,
                dataViewModel,
                changeAll,
            )
        }
    }

    fun showTip() {
        CoroutineScope(Dispatchers.Main).launch {
            isShowTip = true

            delay(2.seconds)

            isShowTip = false
        }
    }

    LaunchedEffect(Unit) {
        showTip()

        var enterCounter = dataViewModel!!.enterCount

        if (enterCounter == 0) {
            dataViewModel.addDefaultMessages()
        }

        enterCounter += 1
        runBlocking {
            context.dataStore.edit { settings ->
                settings[app.awake.ENTER_COUNTER] = enterCounter
            }
        }

        setupNotifications()

        // notifications
        val settingsListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.settingsChanged
        ) {
            setupNotifications(changeAll = true)
        }

        val messagesListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.messagesChanged
        ) {
            setupNotifications(changeAll = true)
        }

        RemoteNotificationsController.getInstance().addListener(settingsListener)
        RemoteNotificationsController.getInstance().addListener(messagesListener)

        val treeIsOpenedListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.treeIsOpened
        ) {
            isScrollDisabled = true
        }

        val treeIsClosedListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.treeIsClosed
        ) {
            isScrollDisabled = false
        }

        val musicIsOpenedListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.musicIsOpened
        ) {
            isScrollDisabled = true
        }

        val musicIsClosedListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.musicIsClosed
        ) {
            isScrollDisabled = false
        }

        RemoteNotificationsController.getInstance().addListener(treeIsOpenedListener)
        RemoteNotificationsController.getInstance().addListener(treeIsClosedListener)
        RemoteNotificationsController.getInstance().addListener(musicIsOpenedListener)
        RemoteNotificationsController.getInstance().addListener(musicIsClosedListener)

        // pager changes
        snapshotFlow { pagerState.currentPage }.collect { page ->
            isShowTip = false

            when (page) {
                GeneralTab.trees.index, GeneralTab.musics.index  -> {
                    dataViewModel!!.isShowSplash.value = false
                }

                else -> {
                    dataViewModel!!.isShowSplash.value = true
                }
            }
        }
    }

    fun showGeneral() {
        navController.navigate(GENERAL)
        subStage = GeneralSubStage.none

        dataViewModel!!.isShowSplash.value = true

        isShowButtonsBar = true
        isScrollDisabled = false
    }

    Box {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isScrollDisabled) 0f else 1f)
        ) {
            // indicator
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                IndexIndicator(
                    count = GeneralTab.values().count(),
                    selected = pagerState.currentPage,
                    images = mutableListOf(
                        R.drawable.tree_circle_fill,
                        R.drawable.eye_circle_fill,
                        R.drawable.headphones_circle_fill
                    ),
                    modifier = Modifier
                        .alpha(0.8f)
                        .padding(start = 18.dp, end = 18.dp)
                        .clickable { // todo: not work
                            showTip()
                        }
                )
            }
        }

        // tips
        // todo: pagerState.currentPage == GeneralTab.general.index
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .alpha(tipAlpha)
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = Theme.Sizes.paddingFromTop.dpSizeValue.height
                )
        ) {
            GeneralTip(image = R.drawable.tree_circle_fill)

            GeneralTip(back = false, image = R.drawable.headphones_circle_fill)
        }

        // main
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            userScrollEnabled = !isScrollDisabled
        ) { page ->
            when (page) {
                GeneralTab.trees.index -> {
                    TreesView(dataViewModel = dataViewModel!!)
                }

                GeneralTab.general.index -> {
                    Box {
                        NavHost(
                            navController = navController,
                            startDestination = GENERAL,
                            modifier = Modifier
                                .padding(bottom = bottomPadding)
                        ) {
                            composable(GENERAL) {
                                Crossfade(
                                    targetState = subStage,
                                    label = "general_substage"
                                ) { stage ->
                                    when (stage) {
                                        GeneralSubStage.none -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Spacer(modifier = Modifier)

                                                DailyTextView(
                                                    dailyText = dataViewModel!!.dailyText()
                                                )

                                                Crossfade(
                                                    targetState = activeMessages,
                                                    label = "active_messages"
                                                ) { targetMessages ->
                                                    targetMessages?.let {
                                                        ActiveMessagesView(
                                                            messages = it,
                                                            dataViewModel = dataViewModel,
                                                            modifier = Modifier
                                                        ) {
                                                            subStage = GeneralSubStage.messages
                                                            isScrollDisabled = true
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        GeneralSubStage.messages -> {
                                            Crossfade(
                                                targetState = messages,
                                                label = "messages"
                                            ) { targetMessages ->
                                                targetMessages?.let { it1 ->
                                                    MessagesView(
                                                        messages = it1,
                                                        dataViewModel = dataViewModel!!
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            composable(SETTINGS) {
                                SettingsView(dataViewModel = dataViewModel)
                            }

                            composable(TEXT) {
                                StartTextView(showCheckmark = false) {}
                            }
                        }

                        if (isShowButtonsBar) {
                            Column(
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 12.dp, bottom = 12.dp)
                                    .alpha(buttonsBarAlpha)
                            ) {
                                if (subStage != GeneralSubStage.none) {
                                    ButtonsBar(
                                        buttonsInfo = mutableListOf(
                                            ButtonInfo(
                                                image = R.drawable.arrowshape_backward_fill,
                                                selected = true
                                            ) {
                                                showGeneral()
                                            }
                                        )
                                    )
                                } else {
                                    ButtonsBar( // todo: fix repeatable open views when click on the bar buttons
                                        buttonsInfo = mutableListOf(
                                            ButtonInfo(
                                                image = R.drawable.bar_eye,
                                                selected = true,
                                                action = {
                                                    showGeneral()
                                                }),
                                            ButtonInfo(image = R.drawable.bar_book, action = {
                                                navController.navigate(TEXT)

                                                dataViewModel!!.isShowSplash.value = false

                                                isShowButtonsBar = true
                                                isScrollDisabled = true
                                            }),
                                            ButtonInfo(image = R.drawable.bar_gear, action = {
                                                navController.navigate(SETTINGS)

                                                dataViewModel!!.isShowSplash.value =
                                                    if (configuration.isMiddleScreen() || configuration.isSmallScreen()) false else true

                                                isShowButtonsBar = true
                                                isScrollDisabled = true
                                            })
                                        )
                                    )
                                }
                            }
                        }
                    } // box
                } // general tab

                GeneralTab.musics.index -> {
                    MusicsView(musics = dataViewModel!!.musics, dataViewModel = dataViewModel!!)
                }
            } // when
        } // pager
    } // box
}

@Preview(showBackground = true, apiLevel = 34, device = "id:pixel_7")
@Composable
fun GreetingPreview14() {
    AwakeTheme {
        GeneralView()
    }
}