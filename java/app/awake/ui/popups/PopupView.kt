package app.awake.ui.popups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.theme.AwakeTheme

@Composable
fun PopupView(modifier: Modifier = Modifier, popupBody: (@Composable () -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(280.dp)
            .height(360.dp)
            .background(color = Color.White)
            .border(border = BorderStroke(width = 2.dp, color = Color.Black))

    ) {
        Image(
            painter = painterResource(id = R.drawable.sun),
            contentDescription = "popup_sun",
            modifier = Modifier
                .width(100.dp)
                .height(112.dp)
                .padding(top = 12.dp)
        )

        popupBody?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview21() {
    AwakeTheme {
        PopupView()
    }
}