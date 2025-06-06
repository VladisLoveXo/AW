package app.awake.ui.buttons

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.theme.AwakeTheme


@Composable
fun BucketButton(
    modifier: Modifier = Modifier,
    action: ()->Unit,
) {
    var isAnimation by remember { mutableStateOf(false) }
    val rotationDegrees: Float by animateFloatAsState(
        targetValue = if (isAnimation) -5f else 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        isAnimation = true
    }

    IconButton(
        modifier = modifier
            .width(128.dp)
            .height(128.dp)
            .padding(18.dp),
        onClick = action
    ) {
        Image(painter = painterResource(
            id = R.drawable.bucket),
            contentDescription = "bucket_button",
            modifier = Modifier
                .rotate(rotationDegrees)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview39() {
    AwakeTheme {
        BucketButton() {}
    }
}