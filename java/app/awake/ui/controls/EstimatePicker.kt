package app.awake.ui.controls

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.EstimateType
import app.awake.R
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


@Composable
fun EstimatePicker(
    estimatedDays: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int)->Unit,
) {
    var type by remember { mutableStateOf(EstimateType.weeks) }
    var value by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        if ((estimatedDays % 30) == 0) {
            type = EstimateType.months
        }

        when (type) {
            EstimateType.weeks -> value = estimatedDays / 7
            EstimateType.months -> value = estimatedDays / 30
        }
    }

    fun calculateEstimatedDays() {
        var result = 7

        when (type) {
            EstimateType.weeks -> result = value * 7
            EstimateType.months -> result = value * 30
        }

        onValueChange(result)
    }

    Column(
        modifier = modifier
            .width(176.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CochinSubtitleText(
            text = stringResource(id = R.string.estimated_time) + ":"
        )

        Crossfade(targetState = type, label = "estimate_picker_type") {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .width(86.dp)
                        .border(
                            border = BorderStroke(
                                width = Theme.Sizes.lineWidth.dpSizeValue.width,
                                color = Color.Black.copy(alpha = if (it == EstimateType.weeks) 1f else 0f),
                            ),
                            shape = RoundedCornerShape(size = 4.dp)
                        ),
                    onClick = {
                        type = EstimateType.weeks
                        calculateEstimatedDays()
                    }
                ) {
                    CochinText(
                        text = stringResource(id = R.string.weeks),
                    )
                }

                IconButton(
                    modifier = Modifier
                        .width(86.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color.Black.copy(alpha = if (it == EstimateType.months) 1f else 0f),
                            ),
                            shape = RoundedCornerShape(size = 4.dp),
                        ),
                    onClick = {
                        type = EstimateType.months
                        calculateEstimatedDays()
                    }
                ) {
                    CochinText(
                        text = stringResource(id = R.string.months),
                    )
                }
            }
        }

        NumberPicker(value = value) { newValue ->
            value = newValue
            calculateEstimatedDays()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview37() {
    AwakeTheme {
        EstimatePicker(estimatedDays = 7) {}
    }
}