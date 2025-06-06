package app.awake

import android.content.Context
import android.content.res.Configuration
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class ConfigurationController() {
    private var configuration: Configuration? = null
    public fun initConfiguration(context: Context) {
        this.configuration = context.resources.configuration
    }

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: ConfigurationController? = null

        fun getInstance(): ConfigurationController {
            return instance ?: synchronized(this) {
                ConfigurationController().also { ConfigurationController.instance = it }
            }
        }
    }

    fun isSmallScreen(): Boolean {
        configuration?.let {
            return (it.screenWidthDp <= 411 && it.screenHeightDp < 700) // todo: ?
        }

        return false
    }

    fun isMiddleScreen(): Boolean {
        configuration?.let {
            return (it.screenHeightDp < 800) // todo: ?
        }

        return false
    }

    fun screenWidth(): Int {
        configuration?.let {
            return it.screenWidthDp
        }

        return 0
    }

    fun screenHeight(): Int {
        configuration?.let {
            return it.screenHeightDp
        }

        return 0
    }
}

@Singleton
class MainActivityReceiver() {
    var dataReceived: ((Any?)->Unit)? = null
    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: MainActivityReceiver? = null

        fun getInstance(): MainActivityReceiver {
            return instance ?: synchronized(this) {
                MainActivityReceiver().also { MainActivityReceiver.instance = it }
            }
        }
    }

    fun pushData(data: Any?) {
        dataReceived?.invoke(data)
    }
}

@Singleton
class RemoteNotificationsController() {
    companion object {
        @Volatile
        private var instance: RemoteNotificationsController? = null

        fun getInstance(): RemoteNotificationsController {
            return RemoteNotificationsController.instance ?: synchronized(this) {
                RemoteNotificationsController().also { RemoteNotificationsController.instance = it }
            }
        }
    }

    class NotificationsListener(
        val listenNotificationName: NotificationName,
        var onNotificationReceived: (()->Unit)?
    )

    private var listeners = mutableListOf<NotificationsListener>()

    fun addListener(listener: NotificationsListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    enum class NotificationName {
        settingsChanged,
        messagesChanged,
        treesSettingsChanged,
        treeIsOpened,
        treeIsClosed,
        musicIsOpened,
        musicIsClosed,
    }

    fun sendNotification(name: NotificationName) {
        for (listener in listeners) {
            if (listener.listenNotificationName == name) {
                listener.onNotificationReceived?.let { it() }
            }
        }
    }


    // helpers
    fun notifyAboutSettingsChanged() {
        sendNotification(NotificationName.settingsChanged)
    }

    fun notifyAboutMessagesChanged() {
        sendNotification(NotificationName.messagesChanged)
    }

    fun notifyAboutTreesSettingsChanged() {
        sendNotification(NotificationName.treesSettingsChanged)
    }
}

fun getHoursInterval(start: Int, end: Int): Int {
    return if ((end - start) <= 0)
            (24 + (end - start)) else (end - start)
}

fun getDistance(c1: Calendar, c2: Calendar): Long {
    val d1: Date = c1.time
    val d2: Date = c2.time

    return d1.time - d2.time
}

fun getDistanceInDays(c1: Calendar, c2: Calendar): Int {
    val distance = getDistance(c1, c2)
    return TimeUnit.DAYS.convert(distance, TimeUnit.MILLISECONDS).toInt()
}

fun getDatesInterval(c1: Calendar, c2: Calendar): Int {
    val start = c1.get(Calendar.HOUR_OF_DAY)
    val end = c2.get(Calendar.HOUR_OF_DAY)

    return getHoursInterval(start, end)
}
