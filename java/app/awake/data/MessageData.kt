package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.UUID

@Entity
data class MessageData(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "isSelected") val isSelected: Boolean = true,
)

typealias MESSAGES = MutableList<MessageData>

@Dao
interface MessageDao {
    @Query("SELECT * FROM MessageData")
    fun getAll(): MESSAGES

    //@Insert
    //fun insert(message: MessageData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg messages: MessageData)

    @Delete
    fun delete(message: MessageData)
}