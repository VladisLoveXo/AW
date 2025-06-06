package app.awake.ui.stages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.DAYS
import app.awake.Day
import app.awake.Localization
import app.awake.R
import app.awake.RemoteNotificationsController
import app.awake.Sound
import app.awake.model.DataViewModel
import app.awake.ui.buttons.DaysBar
import app.awake.ui.buttons.RoundedButton
import app.awake.ui.buttons.SoundsBar
import app.awake.ui.controls.NumberPicker
import app.awake.ui.controls.TimePicker
import app.awake.ui.controls.TimePickerRu
import app.awake.ui.modes.ModeTabs
import app.awake.ui.theme.AwakeTheme

@Composable
fun SettingsView(
    dataViewModel: DataViewModel?,
    showCheckmark: Boolean = false,
    modifier: Modifier = Modifier,
    onOk: (()->Unit)? = null
) {
    val settings = dataViewModel!!.getSettings()

    var startOfTheDay by remember { mutableStateOf(settings.startOfTheDay) }
    var endOfTheDay by remember { mutableStateOf(settings.endOfTheDay) }
    var intDays by remember { mutableStateOf(settings.days) }
    var mode by remember { mutableStateOf(settings.mode) }
    var countOfNotifications by remember { mutableStateOf(settings.countOfNotifications) }
    var selectedSound by remember { mutableStateOf(settings.sound) }

    fun updateCountOfNotifications() {
        var newCountOfNotifications = countOfNotifications

        if (newCountOfNotifications < 1)
        {
            newCountOfNotifications = 1
        }
        else if (newCountOfNotifications > dataViewModel.maxNotifications())
        {
            newCountOfNotifications = dataViewModel.maxNotifications()
        }

        settings.countOfNotifications = newCountOfNotifications
        dataViewModel.setSettings(settings)

        countOfNotifications = newCountOfNotifications
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    )
    {
        // todo: check localization and show needed picker
        Row(
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            when (Localization.current(LocalContext.current)) {
                Localization.eng -> {
                    TimePicker(
                        title = stringResource(id = R.string.settings_start_day),
                        initialTime = startOfTheDay,
                        modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp)
                    ) { value ->
                        settings.startOfTheDay = value
                        dataViewModel.setSettings(settings)

                        startOfTheDay = value

                        updateCountOfNotifications()
                        RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
                    }

                    TimePicker(
                        title = stringResource(id = R.string.settings_end_day),
                        initialTime = endOfTheDay,
                        modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp)
                    ) { value ->
                        settings.endOfTheDay = value
                        dataViewModel.setSettings(settings)

                        endOfTheDay = value

                        updateCountOfNotifications()
                        RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
                    }
                }

                Localization.rus -> {
                    TimePickerRu(
                        title = stringResource(id = R.string.settings_start_day),
                        initialTime = startOfTheDay,
                        modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp)
                    ) { value ->
                        settings.startOfTheDay = value
                        dataViewModel.setSettings(settings)

                        startOfTheDay = value

                        updateCountOfNotifications()
                        RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
                    }

                    TimePickerRu(
                        title = stringResource(id = R.string.settings_end_day),
                        initialTime = endOfTheDay,
                        modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp)
                    ) { value ->
                        settings.endOfTheDay = value
                        dataViewModel.setSettings(settings)

                        endOfTheDay = value

                        updateCountOfNotifications()
                        RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
                    }
                }
            }
        }

        var days: DAYS = mutableSetOf()
        for (day in intDays)
        {
            days.add(Day.fromInt(day))
        }

        DaysBar(selected = days) { newIntDays ->
            val newDays = mutableSetOf<Int>()
            for (day in newIntDays) {
                newDays.add(day.index)
            }

            settings.days = newDays.toMutableList()
            dataViewModel.setSettings(settings)

            intDays = newDays.toMutableList()
            RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
        }

        Crossfade(targetState = mode, label = "mode") {
            ModeTabs(
                selectedMode = it,
                modifier = Modifier
                    .padding(
                        top = 38.dp,
                        bottom = 20.dp
                    )
            )
            { newMode ->
                settings.mode = newMode
                dataViewModel.setSettings(settings)

                mode = newMode

                updateCountOfNotifications()
                RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
            }
        }

        NumberPicker(
            value = countOfNotifications,
            title = stringResource(id = R.string.settings_count_of_notifications),
            max = dataViewModel.maxNotifications(),
            modifier = Modifier
                .padding(bottom = 10.dp)
        ) { value ->
            settings.countOfNotifications = value
            dataViewModel.setSettings(settings)

            countOfNotifications = value

            RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
        }

        SoundsBar(sounds = mutableListOf(
            Sound(name = "one.mp3", resourceId = R.raw.one, image = R.drawable.pianokeys),
            Sound(name = "two.mp3", resourceId = R.raw.two, image = R.drawable.pianokeys),
            Sound(name = "three.mp3", resourceId = R.raw.three, image = R.drawable.pianokeys_inverse),
            Sound(name = "four.mp3", resourceId = R.raw.four, image = R.drawable.water_waves)
        ), selected = selectedSound)
        { newSound ->
            settings.sound = newSound
            dataViewModel.setSettings(settings)

            selectedSound = newSound

            RemoteNotificationsController.getInstance().notifyAboutSettingsChanged()
        }

        if (showCheckmark) {
            onOk?.let {
                RoundedButton(
                    image = R.drawable.checkmark,
                    modifier = Modifier
                        .padding(top = 32.dp, bottom = 24.dp),
                    action = it
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview7() {
    AwakeTheme {
        SettingsView(dataViewModel = null, showCheckmark = true) {}
    }
}