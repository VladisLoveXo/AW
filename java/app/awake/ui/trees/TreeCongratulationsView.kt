package app.awake.ui.trees

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.TreePack
import app.awake.model.DataViewModel
import app.awake.player.SoundPlayer
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@Composable
fun TreeCongratulationsView(
    treePack: TreePack,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var index by remember { mutableStateOf(0) }
    var currentTree by remember { mutableStateOf("") }
    var penultimateTree by remember { mutableStateOf(0) }

    val treeScale: Float by animateFloatAsState(
        targetValue = if (penultimateTree == 1) 1.1f else 1f,
        animationSpec = tween(2000, easing = EaseOut)
    )
    val textAlpha: Float by animateFloatAsState(
        targetValue = if (index == treePack.trees.count()) 1f else 0f,
        animationSpec = tween(2000)
    )

    var player by remember { mutableStateOf<SoundPlayer?>(null) }

    fun playSound() {
        if (player == null) {
            player = SoundPlayer()

            if (player!!.prepare(R.raw.grown_up, context)) {
                player!!.player?.start()
            }
        }
    }

    LaunchedEffect(Unit) {
        while (index != treePack.trees.count()) {
            delay(1.seconds)

            playSound()

            if (index < treePack.trees.count()) {
                currentTree = treePack.trees[index].blooming
            }

            if (index == treePack.trees.count() - 2) {
                penultimateTree += 1
            }

            if (penultimateTree != 1) {
                index += 1
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.tree_background),
                contentDescription = "tree_background",
                contentScale = ContentScale.FillBounds
            )
        }

        if (!currentTree.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Crossfade(targetState = currentTree, label = "congratulation_tree") { tree ->
                    var bitmapState = BitmapFactory.decodeStream(
                        context.assets.open("tree_packs/${tree}")
                    )

                    if (bitmapState != null) {
                        Image(
                            bitmap = bitmapState.asImageBitmap(),
                            contentDescription = "congratulation_current_tree",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(treeScale),
                        )
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .alpha(textAlpha),
        ) {
            CochinText(
                text = stringResource(id = R.string.tree_congratulations),
                size = 42,
                modifier = Modifier
                    .padding(top = 48.dp)
            )
            
            CochinSubtitleText(
                text = stringResource(id = R.string.tree_congratulationsText),
                align = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 28.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview43() {
    AwakeTheme {
        val dataViewModel = DataViewModel()
        dataViewModel.initialize(LocalContext.current)

        TreeCongratulationsView(treePack = dataViewModel.treePacks[0])
    }
}