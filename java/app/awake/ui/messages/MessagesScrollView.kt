package app.awake.ui.messages

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.data.MessageData
import app.awake.model.DataViewModel
import app.awake.ui.theme.AwakeTheme

@Composable
fun MessagesScrollView(
    messages: List<MessageData>,
    dataViewModel: DataViewModel?,
    modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())
            .padding(top = 18.dp, bottom = 18.dp)
    ) {

        ActiveMessagePreview(
            messages = listOf(),
            dataViewModel = dataViewModel,
            modifier = Modifier
                .padding(start = 18.dp)
        )

        for (message in messages) {
            ActiveMessagePreview(
                id = message.id,
                messages = messages,
                dataViewModel = dataViewModel,
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview19() {
    AwakeTheme {
        MessagesScrollView(
            messages = listOf(),
            dataViewModel = null,
        )
    }
}