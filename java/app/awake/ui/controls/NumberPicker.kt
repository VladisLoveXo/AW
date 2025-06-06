package app.awake.ui.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


@Composable
fun NumberPicker(
    value: Int,
    title: String = "",
    min: Int = 1,
    max: Int = 10,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
)
{
    fun setValue(value: Int) {
        var newValue = value

        if (value < min) {
            newValue = min
        }
        else if (value > max) {
            newValue = max
        }

        onValueChange(newValue)
    }

    val disabledAlpha = Theme.Colors.disabled.colorValue.alpha

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (!title.isEmpty()) {
            CochinSubtitleText(
                text = title,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = (value > min),
                modifier = Modifier
                    .width(42.dp)
                    .height(42.dp),
                onClick =
                {
                    setValue(value - 1)
                    // TODO: haptic
                })
            {
                Image(
                    painter = painterResource(id = R.drawable.arrowshape_backward),
                    contentDescription = "arrowshape.backward",
                    modifier = Modifier
                        .alpha(alpha = if (value <= min) disabledAlpha else 1f)
                )
            }

            CochinText(text = "$value")

            IconButton(
                enabled = (value < max),
                modifier = Modifier
                    .width(42.dp)
                    .height(42.dp),
                onClick =
                {
                    setValue(value + 1)
                    // TODO: haptic
                })
            {
                Image(
                    painter = painterResource(id = R.drawable.arrowshape_forward),
                    contentDescription = "arrowshape.backward",
                    modifier = Modifier
                        .alpha(alpha = if (value >= max) disabledAlpha else 1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview12() {
    AwakeTheme {
        NumberPicker(value = 4, title = "select the number") {}
    }
}