package app.awake.ui.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme


@Composable
fun IndexIndicator(
    count: Int,
    selected: Int,
    images: MutableList<Int> = mutableListOf(),
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        (0..<count).forEach { index ->
            if (index < images.count()) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = "index_indicator_image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .width(18.dp)
                        .height(18.dp)
                        .alpha(if (index == selected) 1f else 0.4f)
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .width(12.dp)
                        .height(12.dp),
                    onDraw = {
                    drawCircle(color = if (index == selected) Color.Black else Theme.Colors.disabled.colorValue)
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview49() {
    AwakeTheme {
        IndexIndicator(count = 3, selected = 0)
    }
}