package app.awake.ui.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import app.awake.ui.texts.CochinText
import app.awake.ui.theme.AwakeTheme


@Composable
fun SwitchWithLabel(
    text: String,
    isChecked: Boolean,
    switchColor: Color = Color.Black,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean)->Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
            //.width(ConfigurationController.getInstance().screenWidth().dp - 84.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CochinText(
            text = text,
        )

        Switch(
            checked = isChecked,
            colors = SwitchDefaults.colors(
                checkedTrackColor = switchColor,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.LightGray,
                uncheckedThumbColor = Color.White,
            ),
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview42() {
    AwakeTheme {
        SwitchWithLabel(
            text = "test",
            isChecked = false
        ) {}
    }
}