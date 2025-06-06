package app.awake.ui.popups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun QuestionView(
    question: String,
    modifier: Modifier = Modifier,
    action: (Boolean)->Unit
) {
    PopupView {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            CochinText(
                text = question,
                modifier = Modifier
                    .padding(18.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
            ) {
                RoundedButton(
                    image = R.drawable.checkmark,
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp)
                ) {
                    action(true)
                }

                RoundedButton(
                    image = R.drawable.xmark,
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp)
                ) {
                    action(false)
                }
            }

            /*Text(question)
                .cochin()
                .padding()

            Spacer()

            HStack {
                RoundedButton(image: Image(systemName: "checkmark")) {
                    action?(true)
                }
                .padding([.leading, .trailing])

                RoundedButton(image: Image(systemName: "xmark")) {
                    action?(false)
                }
                .padding([.leading, .trailing])
            }
            .padding(.bottom)*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview44() {
    AwakeTheme {
        QuestionView(
            question = "test"
        ) {}
    }
}