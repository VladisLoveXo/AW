package app.awake.ui.trees

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.TreeMode
import app.awake.ui.modes.ModeTab
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreeModes(
    selectedMode: Int,
    modifier: Modifier = Modifier,
    onModeChange: (Int) -> Unit
) {
    Row(modifier = modifier) {
        for (mode in TreeMode.values()) {
            ModeTab(
                mode = mode.id,
                info = mode.info,
                isSelected = if (selectedMode == mode.id) true else false,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp))
            { newMode ->
                onModeChange(newMode)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview35() {
    AwakeTheme {
        TreeModes(selectedMode = 0) {}
    }
}