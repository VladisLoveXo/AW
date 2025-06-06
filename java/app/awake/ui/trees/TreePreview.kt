package app.awake.ui.trees

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MainActivityReceiver
import app.awake.QuestionInfo
import app.awake.R
import app.awake.ViewTree
import app.awake.data.TreeWithStages
import app.awake.model.DataViewModel
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreePreview(
    dataViewModel: DataViewModel? = null,
    tree: TreeWithStages? = null,
    modifier: Modifier = Modifier,
    onSelectTree: (TreeWithStages?)->Unit
) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(120.dp)
            .clickable {
                onSelectTree(tree)
            }
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.tree_preview),
            contentDescription = "tree_preview",
            alpha = 0.4f,
            contentScale = ContentScale.FillWidth
        )

        if (tree != null) {
            val selectedPack = dataViewModel!!.findPackById(tree.tree.selectedPackId)
            if (selectedPack != null) {
                val viewTree = ViewTree(treeId = tree.tree.id)
                viewTree.init(
                    context = LocalContext.current,
                    tree = tree,
                    treePack = selectedPack,
                    dataViewModel = dataViewModel
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        //.horizontalScroll(rememberScrollState())
                        .padding(start = 18.dp, end = 18.dp)
                ) {
                    var bitmapState = BitmapFactory.decodeStream(
                        LocalContext.current.assets.open("tree_packs/${viewTree.treePreview}")
                    )

                    if (bitmapState != null) {
                        Image(
                            bitmap = bitmapState.asImageBitmap(),
                            contentDescription = "tree_preview",
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CochinText(
                            text = viewTree.treeName,
                            weight = FontWeight.Bold,
                            align = TextAlign.Center
                        )

                        CochinSubtitleText(
                            text = viewTree.currentStageName,
                            align = TextAlign.Center
                        )
                    }

                    IconButton(onClick = {
                        MainActivityReceiver.getInstance().pushData(
                            data = QuestionInfo(question = context.getString(R.string.tree_delete)) { res ->
                                if (res) {
                                    dataViewModel.deleteTree(tree)
                                }
                            }
                        )
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.xmark_bin),
                            contentDescription = "tree_delete_button"
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "plant_tree",
                    modifier = Modifier
                        .padding(all = 12.dp)
                )

                CochinText(text = stringResource(id = R.string.plant_tree))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview30() {
    AwakeTheme {
        TreePreview() {}
    }
}