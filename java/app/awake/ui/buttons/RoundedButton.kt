package app.awake.ui.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


@Composable
fun RoundedButton(
    text: String? = null,
    image: Int? = null,
    size: DpSize = Theme.Sizes.roundedButton.dpSizeValue,
    color: Color = Color.Black,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    action: ()->Unit
) {
    var lineWidth = Theme.Sizes.lineWidth.dpSizeValue

    TextButton(
        enabled = enabled,
        modifier = modifier
            .width(size.width)
            .height(size.height),
        border = BorderStroke(width = lineWidth.width, color = color),
        onClick = action,
    )
    {
        if (text != null) {
            CochinText(
                text = text,
                color = color,
                align = TextAlign.Center,
                modifier = modifier.offset(y = -2.dp)
            )
        } else if (image != null) {
            Image(painter = painterResource(id = image), contentDescription = "rounded_button_image")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview6() {
    AwakeTheme {
        RoundedButton(text = "Go") {}
    }
}