package app.awake.ui.trees

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ViewTree
import app.awake.data.TreeData
import app.awake.data.TreeWithStages
import app.awake.data.isGrownUp
import app.awake.model.DataViewModel
import app.awake.player.SoundPlayer
import app.awake.ui.buttons.BucketButton
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@Composable
fun TreeView(
    tree: ViewTree? = null,
    selectedTree: TreeData? = null,
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier,
    onSelectTree: (TreeWithStages?)->Unit
) {
    val context = LocalContext.current

    var viewTree by remember { mutableStateOf<ViewTree?>(null) } // todo: remove it, because iOS bug with fast switching trees not exist here

    var fastOpen by remember { mutableStateOf(true) }
    var isShowCurrentTree by remember { mutableStateOf(false) }
    val previousTreeAlpha: Float by animateFloatAsState(
        targetValue = if (isShowCurrentTree) 0f else 1f,
        animationSpec = tween(3000)
    )
    val currentTreeAlpha: Float by animateFloatAsState(
        targetValue = if (isShowCurrentTree) 1f else 0f,
        animationSpec = tween(if (fastOpen) 0 else 3000)
    )

    var isShowTask by remember { mutableStateOf(false) }
    val taskAlpha: Float by animateFloatAsState(
        targetValue = if (isShowTask) 1f else 0f,
        animationSpec = tween(2000)
    )

    var isShowControls by remember { mutableStateOf(false) }
    val controlsAlpha: Float by animateFloatAsState(
        targetValue = if (isShowControls) 1f else 0f,
        animationSpec = tween(if (fastOpen) 0 else 1000)
    )

    var isWatered by remember { mutableStateOf(false) }
    val treeScale: Float by animateFloatAsState(
        targetValue = if (isWatered) 1.1f else 1f,
        animationSpec = tween(500)
    )
    /* todo: val treeYOffset: Float by animateFloatAsState(
        targetValue = if (isWatered) -((height) / 20) else 0.dp
    )*/

    val bucketAlpha: Float by animateFloatAsState(
        targetValue = if (viewTree != null && viewTree!!.showBucket) 1f else 0f,
        animationSpec = tween(1000)
    )

    var isShowCongratulations by remember { mutableStateOf(false) }

    var player by remember { mutableStateOf(SoundPlayer()) }

    var composableSize by remember { mutableStateOf(IntSize.Zero) }
    //val height: Dp by animateDpAsState(targetValue = composableSize.height.dp * 0.75f)

    fun hideTask() {
        isShowTask = false
    }

    suspend fun showTask() {
        delay(1.seconds)
        isShowTask = true

        delay(10.seconds)
        hideTask()
    }

    fun createPlayer() {
        if (player == null) {
            player = SoundPlayer()
        }
    }

    fun playTransitionSound() {
        createPlayer()

        player.playBowl(context)
    }

    fun playWatering() {
        if (dataViewModel.getTreesSettings().isPlayWateringSound) {
            createPlayer()

            if (player.prepare(R.raw.watering, context)) {
                player.player?.setVolume(0.7f, 0.7f)
                player.player?.start()
            }
        }
    }

    suspend fun showTransition() {
        if (viewTree != null) {
            isShowControls = false

            if (!viewTree!!.previousTree.isEmpty()) {
                delay(1.seconds)

                playTransitionSound()

                viewTree!!.previousTree = "" // todo: clear

                fastOpen = false
                isShowCurrentTree = true

                delay(2.seconds)

                isShowControls = true

                showTask()
            } else {
                isShowControls = true
                isShowCurrentTree = true

                showTask()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (tree != null) {
            viewTree = tree

            if (viewTree != null && selectedTree != null) {
                if (!viewTree!!.previousTree.isEmpty() && isGrownUp(selectedTree!!)) {
                    isShowCongratulations = true
                    viewTree!!.previousTree = ""
                }
            }

            showTransition()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                composableSize = it.size
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.tree_background),
                contentDescription = "tree_background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(height) // todo: not work
                    .alpha(alpha = if (tree != null) 0.8f else 0.4f)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (!isShowCongratulations && viewTree != null) {
                if (!viewTree!!.previousTree.isEmpty() && viewTree!!.previousTree != "none") {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(previousTreeAlpha),
                    ) {
                        var bitmapState = BitmapFactory.decodeStream(
                            context.assets.open("tree_packs/${viewTree!!.previousTree}")
                        )

                        if (bitmapState != null) {
                            Image(
                                bitmap = bitmapState.asImageBitmap(),
                                contentDescription = "tree",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(treeScale)
                            )
                        }
                    }
                }

                if (!viewTree!!.currentTree.isEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(currentTreeAlpha)
                    ) {
                        var bitmapState = BitmapFactory.decodeStream(
                            context.assets.open("tree_packs/${viewTree!!.currentTree}")
                        )

                        if (bitmapState != null) {
                            Image(
                                bitmap = bitmapState.asImageBitmap(),
                                contentDescription = "tree",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(treeScale)
                            )
                        }
                    }
                }
            }

            // isShowTask
            if (viewTree != null) {
                CochinText(
                    text = viewTree!!.task,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .alpha(taskAlpha)
                        .padding(48.dp)
                        .clickable {
                            hideTask() // todo: fast
                        }
                )
            }
        }

        if (isShowCongratulations) {
            if (selectedTree != null) {
                val pack = dataViewModel.findPackById(selectedTree.selectedPackId)

                if (pack != null) {
                    TreeCongratulationsView(treePack = pack)
                }
            }
        } else if (isShowControls && viewTree != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(controlsAlpha)
            ) {
                if (viewTree!!.showProgress) {
                    Crossfade(targetState = viewTree!!.watered, label = "tree_progress_view") { watered ->
                        TreeProgressView(
                            days = viewTree!!.stageDaysProgress,
                            watered = watered,
                            modifier = Modifier
                                .padding(start = 36.dp, end = 36.dp, bottom = 18.dp)
                        )
                    }
                }

                CochinText(
                    text = viewTree!!.treeName,
                    size = 26,
                    weight = FontWeight.Bold,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                CochinSubtitleText(
                    text = viewTree!!.currentStageName,
                    align = TextAlign.Center
                )

                BucketButton(
                    modifier = Modifier
                        .alpha(bucketAlpha)
                ) {
                    if (viewTree!!.currentStageId != null) {
                        dataViewModel.waterTree(id = viewTree!!.treeId, stageId = viewTree!!.currentStageId!!)

                        val find = dataViewModel.findTreeById(viewTree!!.treeId)

                        if (find != null) {
                            val pack = dataViewModel.findPackById(find.tree.selectedPackId)

                            if (pack != null) {
                                playWatering()

                                val updatedViewTree = ViewTree(
                                    treeId = find.tree.id
                                )
                                updatedViewTree.init(
                                    context = context,
                                    tree = find,
                                    treePack = pack,
                                    dataViewModel = dataViewModel
                                )

                                viewTree = updatedViewTree

                                CoroutineScope(Dispatchers.Default).launch {
                                    isWatered = true

                                    delay(0.4.seconds)

                                    isWatered = false
                                }
                            }
                        }
                    }
                }
            }
        } else if (tree == null) {
            TreeCreationView(
                treeIndex = dataViewModel.getNextTreeIndex(),
                treePacks = dataViewModel.treePacks,
                defaultPackId = dataViewModel.defaultTreePackId(),
                // todo: modifier = Modifier.glow
            ) { tree ->
                dataViewModel.addTree(tree)

                onSelectTree(tree)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview31() {
    AwakeTheme {
        TreeView(dataViewModel = DataViewModel()) {}
    }
}