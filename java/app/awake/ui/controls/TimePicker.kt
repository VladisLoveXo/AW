package app.awake.ui.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


@Composable
fun TimePicker(
    title: String,
    initialTime: Int,
    modifier: Modifier = Modifier,
    onTimeChange: (Int)->Unit
) {
    var time: Int by rememberSaveable { mutableStateOf(initialTime) }

    fun setTime(_time: Int) {
        if (_time < 0) {
            time = 0
        }
        else if (_time > 23) {
            time = 23
        }
        else {
            time = _time
        }

        onTimeChange(time)
    }

    val isAM = {
        if (time < 12) true else false
    }

    val shownTime = {
        if (isAM.invoke()) time else time - 12
    }

    val disabledAlpha = Theme.Colors.disabled.colorValue.alpha

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    )
    {
        CochinSubtitleText(text = title)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        )
        {
            IconButton(
                enabled = (time > 0),
                modifier = Modifier
                    .width(42.dp)
                    .height(42.dp),
                onClick =
                {
                    setTime(time - 1)

                    // TODO: haptic
                })
            {
                Image(
                    painter = painterResource(id = R.drawable.arrowshape_backward),
                    contentDescription = "arrowshape.backward",
                    modifier = Modifier
                        .alpha(alpha = if (time <= 0) disabledAlpha else 1f)
                )
            }

            val tm = shownTime.invoke()
            CochinText(
                text = "$tm",
            )

            TextButton(
                modifier = Modifier
                    .width(72.dp)
                    .height(36.dp)
                    .padding(start = 12.dp),
                border = BorderStroke(
                    width = Theme.Sizes.lineWidth.dpSizeValue.width,
                    color = Color.Black),
                shape = RoundedCornerShape(6.dp),
                onClick = {
                if (isAM.invoke()) {
                    setTime(time + 12)
                } else {
                    setTime(time - 12)
                }

                // TODO: haptic
            }) {
                CochinText(
                    text = if (isAM.invoke()) "AM" else "PM",
                    modifier = Modifier
                        .offset(y = -2.dp)
                )
            }

            IconButton(
                enabled = (time < 23),
                modifier = Modifier
                    .width(42.dp)
                    .height(42.dp),
                onClick =
                {
                    setTime(time + 1)

                    // TODO: haptic
                })
            {
                Image(
                    painter = painterResource(id = R.drawable.arrowshape_forward),
                    contentDescription = "arrowshape.forward",
                    modifier = Modifier
                        .alpha(alpha = if (time >= 23) disabledAlpha else 1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview8() {
    AwakeTheme {
        TimePicker(title = "Select time", initialTime = 8) {}
    }
}