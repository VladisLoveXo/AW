package app.awake.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class MainData(
    @PrimaryKey val index: Int = 0,

    @ColumnInfo(name = "dailyTextIndex") var dailyTextIndex: Int,
    @ColumnInfo(name = "lastDay") var lastDay: Int,
)

@Dao
interface MainDataDao {
    @Query("SELECT * FROM MainData")
    fun getAll(): MutableList<MainData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg data: MainData)
}