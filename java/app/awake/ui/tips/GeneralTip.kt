package app.awake.ui.tips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.theme.AwakeTheme


@Composable
fun GeneralTip(
    back: Boolean = true,
    image: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .alpha(0.6f),
    ){
        if (back) {
            Image(
                painter = painterResource(id = R.drawable.chevron_backward_2),
                contentDescription = "tip_image_back"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
        ) {
            CochinSubtitleText(
                text = stringResource(id = R.string.swipe),
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
            )

            Image(
                painter = painterResource(id = image),
                contentDescription = "tip_image"
            )
        }

        if (!back) {
            Image(
                painter = painterResource(id = R.drawable.chevron_forward_2),
                contentDescription = "tip_image_back"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview50() {
    AwakeTheme {
        GeneralTip(image = R.drawable.arrowshape_backward)
    }
}