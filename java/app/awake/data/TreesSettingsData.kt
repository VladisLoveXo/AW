package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class TreesSettingsData(
    @PrimaryKey val index: Int = 0,

    @ColumnInfo(name = "isSendReminders") var isSendReminders: Boolean = true,
    @ColumnInfo(name = "reminderSound") var reminderSound: String = "trees_reminder.mp3",
    @ColumnInfo(name = "isPlayTreeBackground") var isPlayTreeBackground: Boolean = true,
    @ColumnInfo(name = "isPlayWateringSound") var isPlayWateringSound: Boolean = true,
)

typealias TREES_SETTINGS = MutableList<TreesSettingsData>

@Dao
interface TreesSettingsDao {
    @Query("SELECT * FROM TreesSettingsData")
    fun getAll(): TREES_SETTINGS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg settings: TreesSettingsData)
}
