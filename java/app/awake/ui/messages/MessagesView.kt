package app.awake.ui.messages

import android.widget.ScrollView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.data.MESSAGES
import app.awake.model.DataViewModel
import app.awake.ui.theme.AwakeTheme

@Composable
fun MessagesView(
    messages: MESSAGES,
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        MessagePreview(
            dataViewModel = dataViewModel,
            modifier = Modifier
                .padding(18.dp)
        )

        Divider(
            modifier = Modifier
                .padding(start = 46.dp, end = 46.dp)
        )

        for (message in messages) {
            MessagePreview(
                message = message,
                dataViewModel = dataViewModel,
                modifier = Modifier
                    .padding(18.dp)
            )

            Divider(
                modifier = Modifier
                    .padding(start = 46.dp, end = 46.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview48() {
    AwakeTheme {
        MessagesView(
            messages = mutableListOf(),
            dataViewModel = DataViewModel()
        )
    }
}