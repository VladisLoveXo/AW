package app.awake.ui.modes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.Mode
import app.awake.ui.theme.AwakeTheme

@Composable
fun ModeTabs(
    selectedMode: Int,
    modifier: Modifier = Modifier,
    onModeChange: (Int) -> Unit
)
{
    Row(modifier = modifier) {
        for (mode in Mode.values()) {
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
fun GreetingPreview11() {
    AwakeTheme {
        ModeTabs(selectedMode = Mode.standard.id) {}
    }
}