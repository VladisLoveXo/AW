package app.awake.ui.trees

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.theme.AwakeTheme
import kotlin.math.max
import kotlin.math.min


@Composable
fun TreeProgressView(
    days: Int,
    factor: Float = 0.7f,
    watered: Int,
    modifier: Modifier = Modifier
) {
    fun position(): Float {
        val minimum = (days.toFloat() * factor).toInt()

        var res: Float = 0f

        if (minimum == 0 && watered == 0) {
            res = 1f
        } else if (minimum == 0 && watered > 0) {
            res = 2f
        } else {
            if (watered >= minimum) {
                val difference = days - minimum
                res = 1 + ((watered - minimum).toFloat() / difference.toFloat())
            } else {
                res = watered.toFloat() / minimum.toFloat()
            }
        }

        return min(2f, res)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
        ) {
            Row {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )

                Divider(
                    color = Color(0.12f, 0.67f, 0f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.5.dp)
                        .fillMaxHeight()
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.5.dp)
                        .fillMaxHeight()
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.5.dp)
                        .fillMaxHeight()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth(max(0.1f, (position() * 0.5f)))
                .offset(x = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrowtriangle_up_fill),
                contentDescription = "tree_progress_arrow",
                modifier = Modifier
                    .padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview40() {
    AwakeTheme {
        TreeProgressView(
            days = 5,
            watered = 3
        )
    }
}