package app.awake.ui.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.BUTTONS_INFO
import app.awake.ButtonInfo
import app.awake.R
import app.awake.ui.theme.AwakeTheme

@Composable
fun ButtonsBar(
    buttonsInfo: BUTTONS_INFO,
    modifier: Modifier = Modifier,
    backAction: (()->Unit)? = null)
{
    var pressedId by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        buttonsInfo.forEachIndexed { index, info ->
            if (info.selected) {
                pressedId = index + 1
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        buttonsInfo.forEachIndexed { index, info ->
            BarButton(
                id = index + 1,
                pressedId = pressedId,
                info = info,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                )
            {
                pressedId = index + 1
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview15() {
    AwakeTheme {
        ButtonsBar(buttonsInfo = mutableListOf(
            ButtonInfo(image = R.drawable.bar_eye, action = {}),
            ButtonInfo(image = R.drawable.bar_book, action = {}),
            ButtonInfo(image = R.drawable.bar_gear, action = {})
        ))
    }
}