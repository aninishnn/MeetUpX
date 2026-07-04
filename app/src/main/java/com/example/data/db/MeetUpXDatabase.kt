package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

// Room მონაცემთა ბაზის კონფიგურაცია
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
    // DAO-ებზე წვდომა
    abstract fun eventDao(): EventDao
    abstract fun joinedEventDao(): JoinedEventDao
    abstract fun savedEventDao(): SavedEventDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        // მონაცემთა ბაზის ერთადერთი (Singleton) ინსტანცია
        @Volatile
        private var INSTANCE: MeetUpXDatabase? = null

        // ქმნის ან აბრუნებს უკვე არსებულ მონაცემთა ბაზას
        fun getDatabase(context: Context): MeetUpXDatabase {
            return INSTANCE ?: synchronized(this) {
                // Room მონაცემთა ბაზის შექმნა
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeetUpXDatabase::class.java,
                    "meetupx_database"
                )
                // ვერსიის ცვლილებისას ძველი ბაზის წაშლა და ახლის შექმნა
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
