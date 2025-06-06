package app.awake.ui.texts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.DailyText
import app.awake.ui.theme.AwakeTheme

@Composable
fun DailyTextView(dailyText: DailyText, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CochinText(
            text = dailyText.text,
            style = FontStyle.Italic,
            modifier = Modifier
                .padding(start = 18.dp, end = 18.dp)
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (dailyText.ps != null) {
                CochinText(
                    text = dailyText.ps!!,
                    weight = FontWeight.Bold,
                    align = TextAlign.Right,
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp, top = 18.dp)
                )
            }
        }

        Divider(modifier = Modifier.padding(top = 18.dp, start = 48.dp, end = 48.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview17() {
    AwakeTheme {
        DailyTextView(dailyText = DailyText(text = "text", ps = "ps"))
    }
}