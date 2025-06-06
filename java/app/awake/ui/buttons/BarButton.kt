package app.awake.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import app.awake.ButtonInfo
import app.awake.R
import app.awake.ui.theme.AwakeTheme

@Composable
fun BarButton(
    id: Int,
    pressedId: Int,
    info: ButtonInfo,
    modifier: Modifier = Modifier,
    backAction: (()->Unit)? = null,
    action: () -> Unit)
{
    when (info.type) {
        ButtonInfo.Type.classic -> IconButton(modifier = modifier, onClick = {
            action.invoke()
            info.action.invoke()

            // todo: haptic
        }) {
            Image(
                painter = painterResource(id = info.image),
                contentDescription = "bar_button",
                modifier = Modifier
                    .alpha(alpha = if (pressedId == id) 1f else 0.4f)
            )
        }

        ButtonInfo.Type.roundedAndMoving -> Box {}
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview16() {
    AwakeTheme {
        BarButton(id = 0, pressedId = 1, info = ButtonInfo(image = R.drawable.bar_gear, action = {})) {}
    }
}