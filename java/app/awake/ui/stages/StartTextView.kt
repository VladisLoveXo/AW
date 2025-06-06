package app.awake.ui.stages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.Localization
import app.awake.R
import app.awake.ReadJSONFromAssets
import app.awake.TextInfo
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.texts.TextView
import app.awake.ui.theme.AwakeTheme
import com.google.gson.Gson


@Composable
fun StartTextView(
    showCheckmark: Boolean = true,
    modifier: Modifier = Modifier,
    completion: (()->Unit)?) {

    fun startText(context: Context): TextInfo {
        val filename = Localization.getLocalizedFilename(context, "startText") + ".json"

        val jsonString = ReadJSONFromAssets(context, filename)
        return Gson().fromJson(jsonString, TextInfo::class.java)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    )
    {
        TextView(
            textInfo = startText(LocalContext.current),
            modifier = Modifier
                .padding(
                    top = 10.dp, start = 10.dp, end = 10.dp,
                    bottom = if (showCheckmark) 16.dp else 0.dp)
        )

        if (showCheckmark) {
            completion?.let {
                RoundedButton(
                    image = R.drawable.checkmark,
                    modifier = Modifier
                        .padding(bottom = 32.dp),
                    action = it
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    AwakeTheme {
        StartTextView() {}
    }
}