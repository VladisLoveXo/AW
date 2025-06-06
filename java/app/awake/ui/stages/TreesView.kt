package app.awake.ui.stages

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.awake.ButtonInfo
import app.awake.NotificationsController
import app.awake.R
import app.awake.RemoteNotificationsController
import app.awake.ViewTree
import app.awake.data.TreeWithStages
import app.awake.data.isWithered
import app.awake.model.DataViewModel
import app.awake.player.SoundPlayer
import app.awake.ui.buttons.ButtonsBar
import app.awake.ui.extensions.animatePlacement
import app.awake.ui.stages.TreesStages.LIST
import app.awake.ui.stages.TreesStages.SETTINGS
import app.awake.ui.stages.TreesStages.TREE
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import app.awake.ui.trees.TreeView
import app.awake.ui.trees.TreesListView
import app.awake.ui.trees.TreesSettingsView
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


object TreesStages {
    const val LIST = "list"
    const val TREE = "tree"
    const val SETTINGS = "settings"
}

@Composable
fun TreesView(
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var selectedTree by remember { mutableStateOf<TreeWithStages?>(null) }

    var isPreview = dataViewModel.isShowTreesPreview.observeAsState()
    val imageSize: Dp by animateDpAsState(
        targetValue = if (isPreview.value!!) 250.dp else 120.dp,
        animationSpec = tween(1000, easing = EaseOut)
    )
    val imageAlpha: Float by animateFloatAsState(
        targetValue = if (isPreview.value!!) 0.2f else 1f,
        animationSpec = tween(1000, easing = EaseOut)
    )
    val titleAlpha: Float by animateFloatAsState(
        targetValue = if (isPreview.value!!) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOut)
    )

    var player by remember { mutableStateOf(SoundPlayer()) }


    fun setupNotifications() {
        if (!dataViewModel.getTreesSettings().isSendReminders || !dataViewModel.isHaveGrowingTrees()) {
            NotificationsController.getInstance().removeTreesNotifications(context, dataViewModel)
        } else {
            NotificationsController.getInstance().setupTreesNotifications(context, dataViewModel)
        }
    }


    LaunchedEffect(Unit) {
        delay(2.seconds)
        dataViewModel.isShowTreesPreview.value = false

        setupNotifications()

        // notifications
        val settingsListener = RemoteNotificationsController.NotificationsListener(
            RemoteNotificationsController.NotificationName.treesSettingsChanged
        ) {
            setupNotifications()
        }

        RemoteNotificationsController.getInstance().addListener(settingsListener)
    }

    var startDestination = LIST
    var navController = rememberNavController()
    var currentDestination by remember { mutableStateOf(startDestination) }

    val topAlpha: Float by animateFloatAsState(
        targetValue = if (currentDestination != TREE) 1f else 0f,
        animationSpec = tween(1000)
    )
    val paddingFromTop: Dp by animateDpAsState(
        targetValue = if (currentDestination != TREE) 136.dp else 0.dp,
        animationSpec = tween(1000)
    )

    fun navigate(destination: String) {
        currentDestination = destination
        navController.navigate(destination)
    }

    if (currentDestination == TREE) {
        RemoteNotificationsController.getInstance().sendNotification(RemoteNotificationsController.NotificationName.treeIsOpened)

        if (dataViewModel.getTreesSettings().isPlayTreeBackground
            && (selectedTree == null || (selectedTree != null && !isWithered(selectedTree!!.tree)))) {

            // todo: smooth fade and appear
            if (player.prepare(R.raw.forest, LocalContext.current)) {
                player.player?.seekTo(22 * 1000)
                player.player?.start()
            }
        }
    } else {
        RemoteNotificationsController.getInstance().sendNotification(RemoteNotificationsController.NotificationName.treeIsClosed)

        player.player?.stop()
    }

    Box(modifier = modifier) {
        Column(
            verticalArrangement = if (isPreview.value!!) Arrangement.Center else Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isPreview.value!!) 0.dp else Theme.Sizes.paddingFromTabBar.dpSizeValue.height)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 36.dp)
                    .alpha(topAlpha)
                    .animatePlacement()
                    .clickable {
                        dataViewModel.isShowTreesPreview.value = false
                    }
            ) {
                Image(painter = painterResource(
                    id = R.drawable.tree),
                    contentDescription = "tree_image",
                    modifier = Modifier
                        .width(imageSize)
                        .height(imageSize)
                        .alpha(imageAlpha)
                )

                CochinText(
                    text = stringResource(id = R.string.trees),
                    size = 46,
                    modifier = Modifier
                        .alpha(titleAlpha)
                    // todo: .glow
                )
            }
        }

        Crossfade(targetState = isPreview.value!!, label = "trees_view") {
            when (it) {
                false -> {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = paddingFromTop,
                                bottom = Theme.Sizes.paddingFromTabBar.dpSizeValue.height
                            )
                    ) {
                        composable(LIST) {
                            TreesListView(dataViewModel = dataViewModel) { selected ->
                                selectedTree = selected

                                navigate(TREE)
                            }
                        }

                        composable(TREE) {
                            if (selectedTree != null) {
                                val selectedPack = dataViewModel.findPackById(selectedTree!!.tree.selectedPackId)

                                if (selectedPack != null) {
                                    val viewTree = ViewTree(selectedTree!!.tree.id)
                                    viewTree.init(
                                        context = LocalContext.current,
                                        tree = selectedTree!!,
                                        treePack = selectedPack,
                                        keepProgress = true,
                                        dataViewModel = dataViewModel
                                    )

                                    TreeView(
                                        tree = viewTree,
                                        selectedTree = selectedTree!!.tree,
                                        dataViewModel = dataViewModel,
                                    ) { selected ->
                                        selectedTree = selected
                                    }
                                }
                            } else {
                                TreeView(dataViewModel = dataViewModel) { selected ->
                                    selectedTree = selected
                                }
                            }
                        }

                        composable(SETTINGS) {
                            TreesSettingsView(dataViewModel = dataViewModel)
                        }
                    }

                    val barBottomPadding = 12.dp

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (currentDestination == TREE) {
                            ButtonsBar(
                                modifier = Modifier
                                    .padding(bottom = barBottomPadding),
                                buttonsInfo = mutableListOf(
                                    ButtonInfo(
                                        image = if (selectedTree != null) R.drawable.arrowshape_backward else R.drawable.xmark,
                                        selected = true
                                    ) {
                                        navigate(LIST)
                                    }
                                )
                            )
                        } else {
                            ButtonsBar(
                                modifier = Modifier
                                    .padding(top = if (currentDestination == TREE) 18.dp else 0.dp)
                                    .padding(bottom = barBottomPadding),
                                buttonsInfo = mutableListOf(
                                    ButtonInfo(
                                        image = R.drawable.bar_list_triangle,
                                        selected = true
                                    ) {
                                        navigate(LIST)
                                    },
                                    ButtonInfo(image = R.drawable.bar_gear) {
                                        navigate(SETTINGS)
                                    }
                                )
                            )
                        }
                    }
                }

                true -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview28() {
    AwakeTheme {
        TreesView(dataViewModel = DataViewModel())
    }
}