package app.awake.ui.modes

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.ModeInfo
import app.awake.R
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme

@Composable
fun ModeTab(
    mode: Int,
    info: ModeInfo,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onModeChange: (Int) -> Unit
) {
    val tabSize = Theme.Sizes.modeTab.dpSizeValue

    var animation by remember { mutableStateOf(false) }
    val imageScale: Float by animateFloatAsState(
        targetValue = if (animation) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        if (isSelected) {
            animation = !animation
        }
    }

    Box(
        modifier = modifier
            .width(tabSize.width)
            .height(tabSize.height)
            .background(color = Color.White)
            .border(
                border = BorderStroke(
                    width = Theme.Sizes.lineWidth.dpSizeValue.width,
                    color = if (isSelected) Color.Black else Theme.Colors.disabled.colorValue
                ),
                shape = RoundedCornerShape(size = 16.dp)
            )
            .clickable {
                onModeChange(mode)
            }
    ) {
        Image(
            painter = painterResource(id = info.image),
            contentDescription = "mode_image",
            modifier = modifier
                .scale(scale = imageScale)
                .alpha(if (isSelected) 0.8f else 0.3f)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(all = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        )
        {
            CochinText( // todo: shadow
                text = stringResource(id = info.name),
                weight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(all = 4.dp)
            )

            Divider(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

            CochinText(
                text = stringResource(id = info.info),
                size = 18,
                weight = FontWeight.Light,
                lineHeight = 20f,
                modifier = Modifier
                    .padding(top = 4.dp)
            )

            /*Text(info.name)
                .font(.custom("Cochin", size: 20))
            .fontWeight(.heavy)
            .offset(y: 6)
            .glow(color: .white, radius: 2)*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview10() {
    AwakeTheme {
        ModeTab(
            mode = 0,
            info = ModeInfo(name = R.string.mode_standard, info = R.string.standardMode_info, image= R.drawable.standard_mode),
            isSelected = true,
        ) { _ -> }
    }
}