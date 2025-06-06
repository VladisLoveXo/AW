package app.awake.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(MutableListConverter::class, DateConverter::class)
@Database(
    entities =
    [
        MainData::class,
        MessageData::class,
        TreeData::class,
        TreeStage::class,
        SettingsData::class,
        TreesSettingsData::class,
        NotificationData::class,
    ],
    version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mainDataDao(): MainDataDao
    abstract fun messageDao(): MessageDao
    abstract fun settingsDao(): SettingsDao
    abstract fun treeStageDao(): TreeStageDao
    abstract fun treeDao(): TreeDao
    abstract fun treesSettingsDao(): TreesSettingsDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            synchronized(this) {
                return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "awake_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
        }
    }
}