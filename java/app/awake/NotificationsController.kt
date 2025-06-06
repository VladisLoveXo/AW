package app.awake

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.awake.data.NotificationData
import app.awake.model.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min


@Singleton
class NotificationsController {
    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: NotificationsController? = null

        const val MESSAGES_ONE_CHANNEL_ID = "messages_one"
        const val MESSAGES_TWO_CHANNEL_ID = "messages_two"
        const val MESSAGES_THREE_CHANNEL_ID = "messages_three"
        const val MESSAGES_FOUR_CHANNEL_ID = "messages_four"

        const val TREES_CHANNEL_ID = "trees"

        fun getInstance(): NotificationsController {
            return instance ?: synchronized(this) {
                NotificationsController().also { NotificationsController.instance = it }
            }
        }
    }


    fun getChannelIdFromSoundName(soundName: String): String {
        var res = MESSAGES_ONE_CHANNEL_ID

        when (soundName) {
            "one.mp3" -> res = MESSAGES_ONE_CHANNEL_ID
            "two.mp3" -> res = MESSAGES_TWO_CHANNEL_ID
            "three.mp3" -> res = MESSAGES_THREE_CHANNEL_ID
            "four.mp3" -> res = MESSAGES_FOUR_CHANNEL_ID
        }

        return res
    }



    fun createNotificationChannels(context: Context) {
        createMessagesChannels(context)
        createTreesChannel(context)
    }

    // todo: check texts below
    private fun createMessagesChannels(context: Context) {
        val one = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/one")
        val two = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/two")
        val three = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/three")
        val four = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/four")


        createNotificationChannel(context, MESSAGES_ONE_CHANNEL_ID, "Messages notifications 1", "Notification with messages for yourself", one)
        createNotificationChannel(context, MESSAGES_TWO_CHANNEL_ID, "Messages notifications 2", "Notification with messages for yourself", two)
        createNotificationChannel(context, MESSAGES_THREE_CHANNEL_ID, "Messages notifications 3", "Notification with messages for yourself", three)
        createNotificationChannel(context, MESSAGES_FOUR_CHANNEL_ID, "Messages notifications 4", "Notification with messages for yourself", four)
    }

    private fun createTreesChannel(context: Context) {
        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/bowl")

        createNotificationChannel(context, TREES_CHANNEL_ID, "Trees reminders", "Reminders about trees check", soundUri)
    }

    private fun createNotificationChannel(context: Context, channel_id: String, name: String, description: String, soundUri: Uri? = null) {
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channel_id, name, importance).apply {
            this.description = description
        }

        if (soundUri != null) {
            val soundAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            channel.setSound(soundUri, soundAttributes)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    fun setupNotifications(context: Context, dataViewModel: DataViewModel, changeAll: Boolean = false)
    {
        if (changeAll) {
            val notifications = dataViewModel.getAllNotifications()

            for (notification in notifications) {
                deleteNotification(context, notification, dataViewModel)
            }

            dataViewModel.deleteAllNotifications()
        }

        updateNotifications(context, dataViewModel)

        val settings = dataViewModel.getSettings()
        val messages = dataViewModel.messages.value!!

        val notifications = dataViewModel.getAllNotifications()

        var days: Int = 10
        var maxMinutes: Int = 59

        val mode = Mode.fromInt(settings.mode)
        when (mode) {
            Mode.standard -> {
                days = 10
                maxMinutes = 59
            }

            Mode.intensive -> {
                days = 60 / settings.countOfNotifications
                maxMinutes = 29
            }
        }

        if (notifications.count() <= ((days / 2) * settings.countOfNotifications)) {
            Log.d("", "---------- Setup notifications ----------\n")

            val nowCalendar = Calendar.getInstance()

            // find notifications which need to remove

            var delete = mutableListOf<Int>()

            for (notification in notifications) {
                val notificationCalendar = Calendar.getInstance()
                notificationCalendar.time = notification.date

                if (!isNow(notificationCalendar, nowCalendar) && !treeNotificationsIds.contains(notification.id)) {
                    delete.add(notification.id)

                    Log.d("", "notification will be deleted: ${notificationCalendar.time}")
                } else {
                    Log.d("", "notification will be saved: ${notificationCalendar.time}")
                }
            }

            deleteNotificationsById(context, dataViewModel, delete)


            if (settings.days.count() == 0) {
                Log.d("", "empty\n")
                return
            }

            // setup new notifications

            val datesInterval = getHoursInterval(settings.startOfTheDay, settings.endOfTheDay) * 60
            val notificationsInterval = datesInterval / settings.countOfNotifications

            var day = 0
            var daysSetted = 0

            while (daysSetted < days) {
                var notificationCalendar = Calendar.getInstance()

                notificationCalendar.set(Calendar.MINUTE, 0)
                notificationCalendar.set(Calendar.SECOND, 0)
                notificationCalendar.set(Calendar.HOUR_OF_DAY, settings.startOfTheDay)

                notificationCalendar.add(Calendar.DAY_OF_MONTH, day)

                if (settings.days.contains(Day.fromCalendar(notificationCalendar).index)) {
                    val minutes = (0..maxMinutes).random()
                    notificationCalendar.add(Calendar.MINUTE, minutes)

                    (0..< settings.countOfNotifications).forEach {
                        if (notificationCalendar > nowCalendar && !isNow(notificationCalendar, nowCalendar)) {
                            val messageIndex = (0 .. max(0, messages.count() - 1)).random()
                            val message = if (messages.count() > 0) messages[messageIndex].text else context.getString(R.string.default_message)

                            Log.d("", "notification will be added: ${notificationCalendar.time}")

                            var notificationData = NotificationData(
                                dataViewModel.getNextNotificationId(),
                                notificationCalendar.time,
                                "Awake",
                                message,
                                getChannelIdFromSoundName(settings.sound)
                            )

                            addNotification(context, dataViewModel, notificationData)
                        }

                        notificationCalendar.add(Calendar.MINUTE, notificationsInterval)
                    }

                    daysSetted += 1
                }

                // add warning notifications

                if (daysSetted == days) {
                    notificationCalendar = Calendar.getInstance()
                    notificationCalendar.set(Calendar.HOUR_OF_DAY, settings.startOfTheDay)

                    notificationCalendar.add(Calendar.DAY_OF_MONTH, day + 1)

                    Log.d("", "warning will be added: ${notificationCalendar.time}")

                    var notificationData = NotificationData(
                        dataViewModel.getNextNotificationId(),
                        notificationCalendar.time,
                        "Awake",
                        context.getString(R.string.messages_disabled),
                        getChannelIdFromSoundName(settings.sound)
                    )

                    addNotification(context, dataViewModel, notificationData)
                }

                day += 1
            }
        } else {
            // print scheduled notifications

            for (notification in notifications) {
                Log.d("", "scheduled notification: ${notification.date}")
            }
        }
    }


    private val treeNotificationsIds = mutableListOf<Int>(
        1000,
        1001,
        1002,
        1003,
        1004,
        1005,
        1006
    )

    fun setupTreesNotifications(context: Context, dataViewModel: DataViewModel) {
        var settings = dataViewModel.getSettings()

        removeTreesNotifications(context, dataViewModel, log = false)

        val notifications = dataViewModel.getAllNotifications()

        Log.d("", "---------- Setup trees notifications ----------\n")

        val nowCalendar = Calendar.getInstance()

        val countOfNotifications = min(64 - notifications.count(), treeNotificationsIds.count())

        (0 ..< countOfNotifications).forEach { index ->
            var notificationCalendar = Calendar.getInstance()

            notificationCalendar.set(Calendar.MINUTE, 0)
            notificationCalendar.set(Calendar.SECOND, 0)
            notificationCalendar.set(Calendar.HOUR_OF_DAY, settings.endOfTheDay)

            notificationCalendar.add(Calendar.DAY_OF_MONTH, index)

            if (notificationCalendar.time > Date()) {
                Log.d("", "notification will be added: ${notificationCalendar.time}")

                var notificationData = NotificationData(
                    treeNotificationsIds[index],
                    notificationCalendar.time,
                    context.getString(R.string.trees),
                    context.getString(R.string.trees_check),
                    TREES_CHANNEL_ID
                )

                addNotification(context, dataViewModel, notificationData)
            }
        }
    }

    fun removeTreesNotifications(context: Context, dataViewModel: DataViewModel, log: Boolean = true) {
        var notifications = dataViewModel.getAllNotifications()

        for (notification in notifications) {
            if (treeNotificationsIds.contains(notification.id)) {
                deleteNotification(context, notification, dataViewModel)
            }
        }

        if (log) {
            Log.d("", "---------- All trees notifications removed ----------\n")
        }
    }



    private fun isNow(calendar: Calendar, nowCalendar: Calendar): Boolean {
        return (
                calendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)
                        && calendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH)
                        && calendar.get(Calendar.DAY_OF_MONTH) == nowCalendar.get(Calendar.DAY_OF_MONTH)
                        && calendar.get(Calendar.HOUR_OF_DAY) == nowCalendar.get(Calendar.HOUR_OF_DAY)
                )
    }

    private fun addNotification(context: Context, dataViewModel: DataViewModel, notification: NotificationData) {
        setAlarm(context, notification)
        dataViewModel.addNotification(notification)
    }

    private fun deleteNotification(context: Context, notification: NotificationData, dataViewModel: DataViewModel) {
        removeAlarm(context, notification.id)
        dataViewModel.deleteNotification(notification)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context, p1: Intent?) {
        val id = p1?.getIntExtra("id", 0)
        val channel_id = p1?.getStringExtra("channel_id")

        val text = p1?.getStringExtra("text")

        // tapResultIntent gets executed when user taps the notification
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity( context,0,tapResultIntent,FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val notification = context.let {
            NotificationCompat.Builder(it, channel_id!!)
                .setContentTitle("Awake")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build()
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager = context.let { NotificationManagerCompat.from(it) }
        notificationManager!!.notify(id!!, notification)
    }
}

// todo: check this class in the future
@AndroidEntryPoint
class RebootBroadcastReceiver : BroadcastReceiver(){
    @Inject
    lateinit var dataViewModel: DataViewModel

    override fun onReceive(context: Context?, p1: Intent?) {
        val time = Date()
        CoroutineScope(Main).launch {
            if (context != null) {
                dataViewModel.initialize(context)

                updateNotifications(context, dataViewModel)

                val notifications = dataViewModel.getAllNotifications()

                notifications.forEach { notification ->
                    setAlarm(context, notification)
                }
            }
        }
    }
}

private fun setAlarm(context: Context, notification: NotificationData) {
    // creating alarmManager instance
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // adding intent and pending intent to go to AlarmReceiver Class in future
    val intent = Intent(context, AlarmReceiver::class.java)

    intent.putExtra("id", notification.id)
    intent.putExtra("channel_id", notification.channel_id)

    intent.putExtra("text", notification.text)

    val pendingIntent = PendingIntent.getBroadcast(context, notification.id, intent, PendingIntent.FLAG_IMMUTABLE)

    // when using setAlarmClock() it displays a notification until alarm rings and when pressed it takes us to mainActivity
    val mainActivityIntent = Intent(context, MainActivity::class.java)
    val basicPendingIntent = PendingIntent.getActivity(context, notification.id, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)

    // creating clockInfo instance
    val clockInfo = AlarmManager.AlarmClockInfo(notification.date.time, basicPendingIntent)

    // check permission todo: intent if not granted?
    alarmManager.canScheduleExactAlarms()

    // setting the alarm
    alarmManager.setAlarmClock(clockInfo, pendingIntent)
}

private fun removeAlarm(context: Context, id: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)
}

private fun deleteNotificationsById(context: Context, dataViewModel: DataViewModel, ids: MutableList<Int>) {
    for (id in ids) {
        removeAlarm(context, id)
    }

    dataViewModel.deleteNotificationsById(ids)
}

private fun updateNotifications(context: Context, dataViewModel: DataViewModel) {
    var delete = mutableListOf<Int>()

    val notifications = dataViewModel.getAllNotifications()

    notifications.forEach { notification ->
        if (notification.date < Date()) {
            delete.add(notification.id)
        }
    }

    deleteNotificationsById(context, dataViewModel, delete)
}


