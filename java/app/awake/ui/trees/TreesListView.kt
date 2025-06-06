package app.awake.ui.trees

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.data.TreeWithStages
import app.awake.model.DataViewModel
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreesListView(
    dataViewModel: DataViewModel? = null,
    modifier: Modifier = Modifier,
    onSelectTree: (TreeWithStages?)->Unit
) {
    if (dataViewModel != null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (dataViewModel.trees.value!!.isEmpty()) {
                CochinText(
                    text = stringResource(id = R.string.trees_info),
                    modifier = Modifier
                        .padding(all = 18.dp)
                        .padding(start = 18.dp, end = 18.dp)
                )
            }

            TreePreview(onSelectTree = onSelectTree)

            // growing
            if (!dataViewModel.getGrowingTrees().isEmpty()) {
                CochinText(
                    text = stringResource(id = R.string.tree_growing),
                    weight = FontWeight.Bold,
                    style = FontStyle.Italic,
                    align = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .padding(start = 18.dp)
                )

                for (tree in dataViewModel.getGrowingTrees()) {
                    TreePreview(tree = tree, dataViewModel = dataViewModel) { tree ->
                        onSelectTree(tree)
                    }
                }
            }

            // grown
            if (!dataViewModel.getGrownTrees().isEmpty()) {
                CochinText(
                    text = stringResource(id = R.string.tree_grownUp),
                    weight = FontWeight.Bold,
                    style = FontStyle.Italic,
                    align = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .padding(start = 18.dp)
                )

                for (tree in dataViewModel.getGrownTrees()) {
                    TreePreview(tree = tree, dataViewModel = dataViewModel) { tree ->
                        onSelectTree(tree)
                    }
                }
            }

            // withered
            if (!dataViewModel.getWitheredTrees().isEmpty()) {
                CochinText(
                    text = stringResource(id = R.string.tree_withered),
                    weight = FontWeight.Bold,
                    style = FontStyle.Italic,
                    align = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .padding(start = 18.dp)
                )

                for (tree in dataViewModel.getWitheredTrees()) {
                    TreePreview(tree = tree, dataViewModel = dataViewModel) { tree ->
                        onSelectTree(tree)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview29() {
    AwakeTheme {
        TreesListView() {}
    }
}