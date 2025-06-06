package app.awake.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun CombinedButton(
    image: Painter,
    string: String,
    modifier: Modifier = Modifier,
    onClick: ()->Unit
) {
    TextButton(
        modifier = modifier
            .padding(all = 18.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = image,
                contentDescription = "combined_button_image",
            )

            CochinText(
                text = string,
                size = 16,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview38() {
    AwakeTheme {
        CombinedButton(image = painterResource(id = R.drawable.checkmark), string = "test") {}
    }
}