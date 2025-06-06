package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import app.awake.Mode

@Entity
data class SettingsData(
    @PrimaryKey val index: Int = 0,

    @ColumnInfo(name = "mode") var mode: Int = Mode.standard.id,
    @ColumnInfo(name = "startOfTheDay") var startOfTheDay: Int = 10,
    @ColumnInfo(name = "endOfTheDay") var endOfTheDay: Int = 23,
    @ColumnInfo(name = "days") var days: MutableList<Int> = mutableListOf(0, 1, 2, 3, 4, 5, 6),
    @ColumnInfo(name = "countOfNotifications") var countOfNotifications: Int = 4,
    @ColumnInfo(name = "sound") var sound: String = "one.mp3",
)

typealias SETTINGS = MutableList<SettingsData>

@Dao
interface SettingsDao {
    @Query("SELECT * FROM SettingsData")
    fun getAll(): SETTINGS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg settings: SettingsData)
}
