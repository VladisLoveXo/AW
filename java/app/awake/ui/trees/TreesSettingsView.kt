package app.awake.ui.trees

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.R
import app.awake.RemoteNotificationsController
import app.awake.Sound
import app.awake.model.DataViewModel
import app.awake.ui.buttons.SoundsBar
import app.awake.ui.controls.SwitchWithLabel
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreesSettingsView(
    dataViewModel: DataViewModel,
    modifier: Modifier = Modifier
) {
    val settings = dataViewModel.getTreesSettings()

    var isSendReminders by remember { mutableStateOf(settings.isSendReminders) }
    var selectedSound by remember { mutableStateOf(settings.reminderSound) }
    var isPlayTreeBackground by remember { mutableStateOf(settings.isPlayTreeBackground) }
    var isPlayWateringSound by remember { mutableStateOf(settings.isPlayWateringSound) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(top = 18.dp)
    ) {
        SwitchWithLabel(
            text = stringResource(id = R.string.trees_setting_reminders),
            isChecked = isSendReminders,
            modifier = Modifier
                .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 24.dp),
            onCheckedChange = { checked ->
            settings.isSendReminders = checked
            dataViewModel.setTreesSettings(settings)

            isSendReminders = checked

            RemoteNotificationsController.getInstance().notifyAboutTreesSettingsChanged()
        })

        SoundsBar(
            sounds = mutableListOf(
                Sound(name = "bowl.wav", resourceId = R.raw.bowl, image = R.drawable.eye_fill),
                Sound(name = "trees_reminder.mp3", resourceId = R.raw.trees_reminder, image = R.drawable.tree_fill),
            ), selected = selectedSound,
            enabled = isSendReminders,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .alpha(if (isSendReminders) 1.0f else 0.4f)
        ) {
            selected ->

            settings.reminderSound = selected
            dataViewModel.setTreesSettings(settings)

            selectedSound = selected

            RemoteNotificationsController.getInstance().notifyAboutTreesSettingsChanged()
        }

        Divider(
            modifier = Modifier
                .padding(start = 48.dp, end = 48.dp)
        )

        SwitchWithLabel(
            text = stringResource(id = R.string.trees_setting_playBackground),
            isChecked = isPlayTreeBackground,
            modifier = Modifier
                .padding(28.dp),
            onCheckedChange = { checked ->
                settings.isPlayTreeBackground = checked
                dataViewModel.setTreesSettings(settings)

                isPlayTreeBackground = checked
            })

        Divider(
            modifier = Modifier
                .padding(start = 48.dp, end = 48.dp)
        )

        SwitchWithLabel(
            text = stringResource(id = R.string.trees_setting_watering),
            isChecked = isPlayWateringSound,
            modifier = Modifier
                .padding(28.dp),
            onCheckedChange = { checked ->
                settings.isPlayWateringSound = checked
                dataViewModel.setTreesSettings(settings)

                isPlayWateringSound = checked
            })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview41() {
    AwakeTheme {
        TreesSettingsView(dataViewModel = DataViewModel())
    }
}