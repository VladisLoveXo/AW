package app.awake.ui.texts

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import app.awake.ui.theme.Theme

@Composable
fun CochinText(
    text: String,
    size: Int = 20,
    color: Color = Color.Black,
    style: FontStyle = FontStyle.Normal,
    weight: FontWeight = FontWeight.Medium,
    align: TextAlign = TextAlign.Start,
    textStyle: TextStyle = TextStyle.Default,
    spacing: Float = 0.2f,
    lineHeight: Float = 24f,
    modifier: Modifier = Modifier)
{
    Text(
        text = text,
        color = color,
        fontSize = size.sp,
        fontFamily = Theme.Fonts.cochin.font,
        fontStyle = style,
        fontWeight = weight,
        textAlign = align,
        style = textStyle,
        letterSpacing = spacing.sp,
        lineHeight = lineHeight.sp,
        modifier = modifier,
    )
}

@Composable
fun CochinSubtitleText(
    text: String,
    align: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier
) {
    CochinText(
        text = text,
        size = 18,
        weight = FontWeight.Light,
        color = Color.Black.copy(alpha = 0.6f),
        align = align,
        modifier = modifier
    )
}