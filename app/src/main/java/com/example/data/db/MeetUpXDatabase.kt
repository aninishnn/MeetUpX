package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        EventEntity::class,
        JoinedEventEntity::class,
        SavedEventEntity::class,
        NotificationEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MeetUpXDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun joinedEventDao(): JoinedEventDao
    abstract fun savedEventDao(): SavedEventDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: MeetUpXDatabase? = null

        fun getDatabase(context: Context): MeetUpXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeetUpXDatabase::class.java,
                    "meetupx_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
