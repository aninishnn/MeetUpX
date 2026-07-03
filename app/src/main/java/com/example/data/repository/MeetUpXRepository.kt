package com.example.data.repository

import com.example.data.db.MeetUpXDatabase
import com.example.data.model.EventEntity
import com.example.data.model.JoinedEventEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.SavedEventEntity
import com.example.data.model.UserProfileEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Log
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class MeetUpXRepository(
    private val db: MeetUpXDatabase,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase
){
    private val eventDao = db.eventDao()
    private val joinedEventDao = db.joinedEventDao()
    private val savedEventDao = db.savedEventDao()
    private val notificationDao = db.notificationDao()
    private val userProfileDao = db.userProfileDao()

    private val _currentUserFlow = MutableStateFlow<UserProfileEntity?>(null)
    val currentUserFlow: StateFlow<UserProfileEntity?> = _currentUserFlow.asStateFlow()

    private val currentUserIdFlow = currentUserFlow
        .map { it?.id ?: firebaseAuth.currentUser?.uid.orEmpty() }
        .distinctUntilChanged()

    suspend fun loadCurrentUser() {
        val user = firebaseAuth.currentUser ?: return
        runCatching {
            val profile = loadProfile(user.uid, user.email.orEmpty())
            _currentUserFlow.value = profile
            userProfileDao.saveProfile(profile)
//            runCatching { syncRemoteEvents() }
//            runCatching { syncUserCollections(user.uid) }
        }
    }

    suspend fun checkAndPrepopulateEvents() {
        val currentEvents = eventDao.getAllEventsFlow().first()
        if (currentEvents.isEmpty()) {
            val defaultEvents = EventRepository().getRealisticEvents().map { event ->
                EventEntity(
                    id = event.id,
                    title = event.title,
                    description = event.description,
                    category = event.category,
                    date = event.date,
                    time = event.time,
                    location = event.location,
                    imageName = event.imageName,
                    creatorId = event.creatorId,
                    isCustom = event.isCustom
                )
            }
            eventDao.insertEvents(defaultEvents)

            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_welcome",
                    title = "Welcome to MeetUpX! рџљЂ",
                    message = "Discover events using Tinder-style swipe cards. Swipe Right to Join, Left to Skip, and Up to Save!",
                    isRead = false
                )
            )
        }

        runCatching { syncRemoteEvents() }
    }

    suspend fun register(
        name: String,
        email: String,
        university: String,
        password: String
    ): Result<UserProfileEntity> {

        return runCatching {

            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email.trim(), password)
                .await()

            val user = authResult.user ?: error("User is null")

            val profile = UserProfileEntity(
                id = user.uid,
                name = name.trim(),
                email = email.trim(),
                bio = "",
                university = university.trim(),
                profilePictureUrl = ""
            )

            firebaseDb.getReference("users")
                .child(user.uid)
                .setValue(profile)
                .await()

            userProfileDao.saveProfile(profile)
            _currentUserFlow.value = profile

            profile
        }
    }

    suspend fun signIn(email: String, password: String): Result<UserProfileEntity> {
        return runCatching {
            val trimmedEmail = email.trim()

            val authResult = firebaseAuth
                .signInWithEmailAndPassword(trimmedEmail, password)
                .await()

            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Firebase user is null")

            val uid = firebaseUser.uid

            val snapshot = firebaseDb
                .getReference("users")
                .child(uid)
                .get()
                .await()

            val profile = snapshot.getValue(UserProfileEntity::class.java)
                ?: UserProfileEntity(
                    id = uid,
                    name = firebaseUser.email?.substringBefore("@") ?: "User",
                    email = firebaseUser.email ?: trimmedEmail,
                    bio = "",
                    university = "",
                    profilePictureUrl = ""
                )

            userProfileDao.saveProfile(profile)

            profile
        }
    }

    fun logout() {
        runCatching { firebaseAuth.signOut() }
        _currentUserFlow.value = null
    }

    suspend fun saveProfile(profile: UserProfileEntity) {
        val uid = firebaseAuth.currentUser?.uid ?: error("User not authenticated")
        val profileToSave = profile.copy(id = uid)
        userProfileDao.saveProfile(profileToSave)
        _currentUserFlow.value = profileToSave

        runCatching {
            firebaseDb
                ?.getReference("users")
                ?.child(uid)
                ?.setValue(profileToSave)
                ?.await()
        }
    }

    fun getAllEventsFlow(): Flow<List<EventEntity>> = eventDao.getAllEventsFlow()

    suspend fun getEventById(id: String): EventEntity? = eventDao.getEventById(id)

    suspend fun createEvent(
        title: String,
        description: String,
        category: String,
        date: String,
        time: String,
        location: String,
        imageName: String
    ) {
        val newEvent = EventEntity(
            id = "event_custom_" + UUID.randomUUID().toString().take(6),
            title = title,
            description = description,
            category = category,
            date = date,
            time = time,
            location = location,
            imageName = imageName,
            creatorId = firebaseAuth.currentUser?.uid ?: error("User not authenticated"),
            isCustom = true
        )
        eventDao.insertEvent(newEvent)
        runCatching {
            firebaseDb.getReference("events").child(newEvent.id).setValue(newEvent).await()
        }

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Event Published! рџ“ў",
                message = "Your event '$title' has been published successfully. People can swipe to discover it now!",
                isRead = false
            )
        )
    }

    suspend fun deleteEvent(event: EventEntity) {
        eventDao.deleteEvent(event)
        runCatching {
            firebaseDb.getReference("events").child(event.id).removeValue().await()
        }
    }

    fun getJoinedEventsFlow(): Flow<List<JoinedEventEntity>> =
        currentUserIdFlow.flatMapLatest { userId -> joinedEventDao.getJoinedEventsFlow(userId) }

    suspend fun joinEvent(eventId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: error("User not authenticated")
        val qrCodeRef = "MUX-QR-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        val joined = JoinedEventEntity(
            id = "${userId}_${eventId}",
            userId = userId,
            eventId = eventId,
            qrCodeReference = qrCodeRef
        )
        joinedEventDao.joinEvent(joined)
        runCatching {
            firebaseDb
                .getReference("user_events")
                .child(userId)
                .child("joined")
                .child(eventId)
                .setValue(joined)
                .await()
        }

        eventDao.getEventById(eventId)?.let {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Joined: ${it.title} рџЋ‰",
                    message = "You swiped right and joined! Your digital QR ticket was successfully created.",
                    isRead = false
                )
            )
        }
    }

    suspend fun leaveEvent(eventId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: error("User not authenticated")
        joinedEventDao.leaveEvent(userId, eventId)
        runCatching {
            firebaseDb
                .getReference("user_events")
                .child(userId)
                .child("joined")
                .child(eventId)
                .removeValue()
                .await()
        }
    }

    fun isJoinedFlow(eventId: String): Flow<Boolean> =
        currentUserIdFlow.flatMapLatest { userId -> joinedEventDao.isJoinedFlow(userId, eventId) }

    fun getSavedEventsFlow(): Flow<List<SavedEventEntity>> =
        currentUserIdFlow.flatMapLatest { userId -> savedEventDao.getSavedEventsFlow(userId) }

    suspend fun saveEvent(eventId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: error("User not authenticated")
        val saved = SavedEventEntity(
            id = "${userId}_${eventId}",
            userId = userId,
            eventId = eventId
        )
        savedEventDao.saveEvent(saved)
        runCatching {
            firebaseDb
                .getReference("user_events")
                .child(userId)
                .child("saved")
                .child(eventId)
                .setValue(saved)
                .await()
        }

        eventDao.getEventById(eventId)?.let {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Saved: ${it.title} в­ђ",
                    message = "Event successfully saved to your bookmarks. View it anytime in 'My Events'!",
                    isRead = false
                )
            )
        }
    }

    suspend fun unsaveEvent(eventId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: error("User not authenticated")
        savedEventDao.unsaveEvent(userId, eventId)
        runCatching {
            firebaseDb
                .getReference("user_events")
                .child(userId)
                .child("saved")
                .child(eventId)
                .removeValue()
                .await()
        }
    }

    fun isSavedFlow(eventId: String): Flow<Boolean> =
        currentUserIdFlow.flatMapLatest { userId -> savedEventDao.isSavedFlow(userId, eventId) }

    fun getNotificationsFlow(): Flow<List<NotificationEntity>> = notificationDao.getAllNotificationsFlow()

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun clearNotifications() {
        notificationDao.clearAll()
    }

    private suspend fun loadProfile(uid: String, email: String): UserProfileEntity {
        val localProfile = userProfileDao.getProfile(uid)

        val snapshot = try {
            firebaseDb.getReference("users").child(uid).get().await()
        } catch (e: Exception) {
            null
        }

        return if (snapshot != null && snapshot.exists()) {
            profileFromSnapshot(snapshot, uid, email)
        } else {
            localProfile ?: UserProfileEntity(
                id = uid,
                name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                bio = "Co-founder of community events",
                university = "Tbilisi, Georgia",
                profilePictureUrl = ""
            )
        }
    }

    private suspend fun syncRemoteEvents() {
        val snapshot = firebaseDb.getReference("events").get().await() ?: return
        val events = snapshot.children.mapNotNull { eventFromSnapshot(it) }
        if (events.isNotEmpty()) {
            eventDao.insertEvents(events)
        }
    }

    private suspend fun syncUserCollections(uid: String) {
        val snapshot = firebaseDb.getReference("user_events").child(uid).get().await() ?: return
        snapshot.child("joined").children.mapNotNull { joinedFromSnapshot(it, uid) }.forEach {
            joinedEventDao.joinEvent(it)
        }
        snapshot.child("saved").children.mapNotNull { savedFromSnapshot(it, uid) }.forEach {
            savedEventDao.saveEvent(it)
        }
    }

    private fun profileFromSnapshot(snapshot: DataSnapshot, uid: String, fallbackEmail: String): UserProfileEntity =
        UserProfileEntity(
            id = uid,
            name = snapshot.child("name").getValue(String::class.java)
                ?: fallbackEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = snapshot.child("email").getValue(String::class.java) ?: fallbackEmail,
            bio = snapshot.child("bio").getValue(String::class.java)
                ?: "Co-founder of community events. Enthusiastic event seeker!",
            university = snapshot.child("university").getValue(String::class.java) ?: "Tbilisi, Georgia",
            profilePictureUrl = snapshot.child("profilePictureUrl").getValue(String::class.java) ?: ""
        )

    private fun eventFromSnapshot(snapshot: DataSnapshot): EventEntity? {
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
        return EventEntity(
            id = id,
            title = snapshot.child("title").getValue(String::class.java) ?: return null,
            description = snapshot.child("description").getValue(String::class.java).orEmpty(),
            category = snapshot.child("category").getValue(String::class.java).orEmpty(),
            date = snapshot.child("date").getValue(String::class.java).orEmpty(),
            time = snapshot.child("time").getValue(String::class.java).orEmpty(),
            location = snapshot.child("location").getValue(String::class.java).orEmpty(),
            imageName = snapshot.child("imageName").getValue(String::class.java) ?: "img_onboarding",
            creatorId = snapshot.child("creatorId").getValue(String::class.java).orEmpty(),
            isCustom = snapshot.child("isCustom").getValue(Boolean::class.java) ?: false
        )
    }

    private fun joinedFromSnapshot(snapshot: DataSnapshot, uid: String): JoinedEventEntity? {
        val eventId = snapshot.child("eventId").getValue(String::class.java) ?: snapshot.key ?: return null
        return JoinedEventEntity(
            id = snapshot.child("id").getValue(String::class.java) ?: "${uid}_${eventId}",
            userId = snapshot.child("userId").getValue(String::class.java) ?: uid,
            eventId = eventId,
            qrCodeReference = snapshot.child("qrCodeReference").getValue(String::class.java) ?: "MUX-QR-PENDING",
            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
        )
    }

    private fun savedFromSnapshot(snapshot: DataSnapshot, uid: String): SavedEventEntity? {
        val eventId = snapshot.child("eventId").getValue(String::class.java) ?: snapshot.key ?: return null
        return SavedEventEntity(
            id = snapshot.child("id").getValue(String::class.java) ?: "${uid}_${eventId}",
            userId = snapshot.child("userId").getValue(String::class.java) ?: uid,
            eventId = eventId,
            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
        )
    }
}
