package app.awake.ui.texts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.TextInfo
import app.awake.ui.theme.AwakeTheme

@Composable
fun TextView(textInfo: TextInfo, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    )
    {
        CochinText(
            text = textInfo.title,
            size = 36,
            weight = FontWeight.Bold
            )

        Divider(modifier = Modifier
            .padding(all = 10.dp)
            .padding(bottom = 10.dp),
            color = Color.Gray.copy(0.2f))

        CochinText(
            text = textInfo.text,
            size = 22
            )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview4() {
    AwakeTheme {
        TextView(TextInfo("Hello", "text"))
    }
}