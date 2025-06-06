package app.awake.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.Date


class MutableListConverter {
    @TypeConverter
    fun listToJsonString(value: MutableList<Int>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<Int>::class.java).toMutableList()
}

object DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return if (date == null) null else date.getTime()
    }
}
