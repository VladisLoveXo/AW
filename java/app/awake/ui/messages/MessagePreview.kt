package app.awake.ui.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MainActivityReceiver
import app.awake.R
import app.awake.TextFieldInfo
import app.awake.data.MessageData
import app.awake.model.DataViewModel
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import java.util.UUID

@Composable
fun MessagePreview(
    message: MessageData? = null,
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        onClick = {
            if (message != null) {
                var messages = dataViewModel.messages.value!!

                messages.forEach { messageData ->
                    if (messageData.id == message.id) {
                        val newMessageData = MessageData(
                            id = messageData.id,
                            index = messageData.index,
                            text = messageData.text,
                            isSelected = !messageData.isSelected
                        )

                        dataViewModel.addMessage(newMessageData)
                    }
                }
            } else {
                MainActivityReceiver.getInstance().pushData(
                    data = TextFieldInfo(
                        text = "",
                        placeholder = R.string.message_placeholder
                    ) { result ->
                        dataViewModel.addMessage(MessageData(
                            id = UUID.randomUUID(),
                            index = dataViewModel.getNextMessageIndex(),
                            text = result
                        ))
                    }
                )
            }
        })
    {
        if (message != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = if (message.isSelected) R.drawable.checkmark_circle_fill else R.drawable.checkmark_circle),
                    contentDescription = "message_checkmark",
                    modifier = Modifier
                        .alpha(if (message.isSelected) 1f else 0.6f)
                        .padding(18.dp)
                )

                CochinText(text = message.text)

                Row {
                    IconButton(onClick = {
                        MainActivityReceiver.getInstance().pushData(
                            data = TextFieldInfo(
                                text = message.text,
                                placeholder = R.string.message_placeholder
                            ) { result ->
                                var messages = dataViewModel.messages.value!!

                                messages.forEach { messageData ->
                                    if (messageData.id == message.id) {
                                        val newMessageData = MessageData(
                                            id = messageData.id,
                                            index = messageData.index,
                                            text = result
                                        )
                                        dataViewModel.addMessage(newMessageData)
                                    }
                                }
                            }
                        )
                    }) {
                        Image(painter = painterResource(id = R.drawable.pencil), contentDescription = "edit_message")
                    }

                    IconButton(onClick = {
                        var messages = dataViewModel.messages.value!!

                        messages.forEach { messageData ->
                            if (messageData.id == message.id) {
                                dataViewModel.deleteMessage(messageData)
                            }
                        }
                    }) {
                        Image(painter = painterResource(id = R.drawable.xmark_bin), contentDescription = "delete_message")
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(painter = painterResource(id = R.drawable.plus), contentDescription = "message_plus")

                CochinText(
                    text = stringResource(id = R.string.messages_add_new),
                    modifier = Modifier
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview47() {
    AwakeTheme {
        MessagePreview(
            dataViewModel = DataViewModel()
        )
    }
}