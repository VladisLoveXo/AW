package app.awake.ui.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.MainActivityReceiver
import app.awake.R
import app.awake.TextFieldInfo
import app.awake.data.MessageData
import app.awake.model.DataViewModel
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme
import app.awake.ui.theme.Theme
import java.util.UUID

@Composable
fun ActiveMessagePreview(
    id: UUID? = null,
    messages: List<MessageData>,
    dataViewModel: DataViewModel?,
    modifier: Modifier = Modifier) {

    fun message(): MessageData? {
        for (message in messages) {
            if (message.id == id) {
                return message
            }
        }

        return null
    }

    Box(modifier = modifier) {
        IconButton(
            modifier = Modifier
                .width(if (message() != null) 224.dp else 48.dp)
                .height(136.dp)
                //.shadow(elevation = 4.dp, spotColor = Color.White)
                .border(
                    border = BorderStroke(
                        width = Theme.Sizes.lineWidth.dpSizeValue.width,
                        color = Color.Black.copy(alpha = if (message() != null) 1f else 0f)
                    ),
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .background(
                    color = Color.White.copy(
                        alpha = if (message() != null) 0.6f else 0f
                    )
                ),
            onClick = {
                MainActivityReceiver.getInstance().pushData(
                    data = TextFieldInfo(
                        text = if (message() != null) message()!!.text else "",
                        placeholder = R.string.message_placeholder
                    ) { result ->
                        if (dataViewModel != null) {
                            if (id == null) {
                                dataViewModel.addMessage(MessageData(
                                    id = UUID.randomUUID(),
                                    index = dataViewModel.getNextMessageIndex(),
                                    text = result
                                ))
                            } else {
                                messages.forEach { messageData ->
                                    if (messageData.id == id) {
                                        val newMessageData = MessageData(
                                            id = messageData.id,
                                            index = messageData.index,
                                            text = result
                                        )
                                        dataViewModel.addMessage(newMessageData)
                                    }
                                }
                            }
                        }
                    }
                )
            },
        ) {
            val message = message()

            if (message == null) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "add_message"
                )
            } else {
                CochinText(
                    text = message.text,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .padding(all = 18.dp)
                )
            }
        }

        if (id != null) {
            Row(
                modifier = modifier
                    .width(212.dp)
                    .offset(x = 16.dp, y = -24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    if (dataViewModel != null) {
                        messages.forEach { message ->
                            if (message.id == id) {
                                dataViewModel.deleteMessage(message)
                            }
                        }
                    }
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.xmark_circle_fill),
                        contentDescription = "message_preview_xmark")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview20() {
    AwakeTheme {
        ActiveMessagePreview(
            messages = listOf(),
            dataViewModel = null,
        )
    }
}