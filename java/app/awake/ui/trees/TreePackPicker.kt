package app.awake.ui.trees

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.TREE_PACKS
import app.awake.TreePack
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreePackPicker(
    treePacks: TREE_PACKS,
    selectedPackId: String,
    modifier: Modifier = Modifier,
    onSelectPack: (String)->Unit
) {
    Row(modifier = modifier) {
        for (pack in treePacks) {
            TreePackTab(
                packId = pack.id,
                preview = pack.preview,
                isSelected = (pack.id == selectedPackId),
                onSelect = onSelectPack,
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview33() {
    AwakeTheme {
        TreePackPicker(
            treePacks = mutableListOf(
                TreePack(name = "One", id = "one", preview = "One/preview.png", trees = mutableListOf()),
                TreePack(name = "Two", id = "two", preview = "Two/preview.png", trees = mutableListOf()),
                TreePack(name = "Three", id = "three", preview = "Three/preview.png", trees = mutableListOf()),
        ),
            selectedPackId = ""
        ) {}
    }
}