package app.awake.ui.popups

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.awake.R
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.texts.CochinSubtitleText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun TextFieldView(
    text: String,
    placeholder: Int,
    modifier: Modifier = Modifier,
    action: (String)->Unit
) {
    val focusRequester = remember { FocusRequester() }

    var result by remember { mutableStateOf(text) }

    var error by remember { mutableStateOf(false) }

    val textScale: Float by animateFloatAsState(
        targetValue = if (error) 1.2f else 1f
    )
    val borderColor: Color by animateColorAsState(
        targetValue = if (error) Color.Red.copy(alpha = 0.4f) else Color.Black
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    PopupView {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = result,
                maxLines = 4,
                textStyle = TextStyle(
                    fontFamily = Theme.Fonts.cochin.font,
                    fontSize = 20.sp,
                    letterSpacing = 0.2.sp,
                    lineHeight = 24.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .width(192.dp)
                    .height(146.dp)
                    .padding(top = 18.dp)
                    .focusRequester(focusRequester)
                    .border(
                        border = BorderStroke(
                            width = Theme.Sizes.lineWidth.dpSizeValue.width,
                            color = borderColor
                        ),
                        shape = RoundedCornerShape(2)
                    )
                    .background(color = Color.White),
                onValueChange = { string ->
                    result = string
                }, placeholder = {
                    CochinSubtitleText(
                        text = stringResource(id = placeholder),
                        modifier = Modifier
                            .scale(textScale)
                    )
                }
            )

            Spacer(modifier = Modifier)

            RoundedButton(
                image = R.drawable.checkmark,
                modifier = Modifier
                    .padding(top = 36.dp, bottom = 18.dp)
            ) {
                if (result.isEmpty()) {
                    CoroutineScope(Dispatchers.Default).launch {
                        error = true
                        delay(0.2.seconds)
                        error = false
                    }
                } else {
                    action(result)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview22() {
    AwakeTheme {
        TextFieldView(
            text = "",
            placeholder = R.string.text_placeholder
        ) {}
    }
}