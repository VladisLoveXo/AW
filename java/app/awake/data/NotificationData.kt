package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date

@Entity
data class NotificationData(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "channel_id") var channel_id: String,
)

typealias NOTIFICATIONS = MutableList<NotificationData>

@Dao
interface NotificationDao {
    @Query("SELECT * FROM NotificationData")
    fun getAll(): NOTIFICATIONS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg notifications: NotificationData)

    @Delete
    fun delete(notification: NotificationData)
}
