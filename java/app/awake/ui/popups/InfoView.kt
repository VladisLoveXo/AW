package app.awake.ui.popups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun InfoView(info: String, modifier: Modifier = Modifier, action: ()->Unit) {
    PopupView {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            CochinText(
                text = info,
                modifier = Modifier
                    .padding(all = 18.dp)
            )

            RoundedButton(
                image = R.drawable.checkmark,
                modifier = Modifier
                    .padding(bottom = 18.dp)
            ) {
                action()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview23() {
    AwakeTheme {
        InfoView("Info") {}
    }
}