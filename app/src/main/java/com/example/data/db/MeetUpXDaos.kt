package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    // აბრუნებს ყველა ღონისძიების რეალურ დროში განახლებად სიას
    @Query("SELECT * FROM events")
    fun getAllEventsFlow(): Flow<List<EventEntity>>

    // პოულობს კონკრეტულ ღონისძიებას ID-ის მიხედვით
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    // ამატებს ან ანახლებს ერთ ღონისძიებას
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    // ამატებს ან ანახლებს რამდენიმე ღონისძიებას
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)
   
    @Delete
    suspend fun deleteEvent(event: EventEntity)
}

@Dao
interface JoinedEventDao {
    @Query("SELECT * FROM joined_events WHERE userId = :userId")
    fun getJoinedEventsFlow(userId: String): Flow<List<JoinedEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun joinEvent(joinedEvent: JoinedEventEntity)

    @Query("DELETE FROM joined_events WHERE userId = :userId AND eventId = :eventId")
    suspend fun leaveEvent(userId: String, eventId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM joined_events WHERE userId = :userId AND eventId = :eventId)")
    fun isJoinedFlow(userId: String, eventId: String): Flow<Boolean>
}

@Dao
interface SavedEventDao {
    @Query("SELECT * FROM saved_events WHERE userId = :userId")
    fun getSavedEventsFlow(userId: String): Flow<List<SavedEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEvent(savedEvent: SavedEventEntity)

    @Query("DELETE FROM saved_events WHERE userId = :userId AND eventId = :eventId")
    suspend fun unsaveEvent(userId: String, eventId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_events WHERE userId = :userId AND eventId = :eventId)")
    fun isSavedFlow(userId: String, eventId: String): Flow<Boolean>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getProfile(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun getProfileFlow(userId: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfileEntity)
}
