package app.awake.ui.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.awake.DAYS
import app.awake.Day
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme

@Composable
fun DaysBar(
    selected: DAYS,
    modifier: Modifier = Modifier,
    onValueChange: (DAYS) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (day in Day.values()) {
            RoundedButton(
                text = stringResource(id = day.character),
                size = DpSize(width = 36.dp, height = 36.dp),
                color = if (selected.contains(day)) Color.Black else Theme.Colors.disabled.colorValue,
                modifier = modifier
            ) {
                var newSet = selected.toMutableSet()

                if (newSet.contains(day)) {
                    newSet.remove(day)
                } else {
                    newSet.add(day)
                }

                onValueChange(newSet)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview9() {
    AwakeTheme {
        DaysBar(selected = mutableSetOf()) { newVal -> }
    }
}