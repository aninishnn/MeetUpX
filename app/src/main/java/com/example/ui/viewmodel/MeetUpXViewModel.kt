package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MeetUpXDatabase
import com.example.data.model.*
import com.example.data.repository.MeetUpXRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

// ViewModel აკავშირებს UI-ს Repository-სთან.
// აქ იმართება ეკრანების მდგომარეობა, მომხმარებლის მოქმედებები
// და მონაცემების მიღება/განახლება.
class MeetUpXViewModel(application: Application) : AndroidViewModel(application) {
    // Room Database-ის ინიციალიზაცია
    private val db = MeetUpXDatabase.getDatabase(application)
    // Repository უზრუნველყოფს Room-სა და Firebase-თან მუშაობას
    private val repository = MeetUpXRepository(
        db,
        FirebaseAuth.getInstance(),
        FirebaseDatabase.getInstance()
    )

    // ინახავს ამჟამად გახსნილ ეკრანს
    private val _currentScreen = MutableStateFlow<String>("splash")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // მომხმარებლის ავტორიზაციის მიმდინარე მდგომარეობა
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // ინახავს იმ ღონისძიების ID-ს,
    // რომლის დეტალებიც უნდა გამოჩნდეს.
    private val _selectedEventId = MutableStateFlow<String?>(null)
    val selectedEventId: StateFlow<String?> = _selectedEventId.asStateFlow()

    // არჩეული კატეგორიის ფილტრი
    private val _selectedCategory = MutableStateFlow<String>("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // არჩეული ქალაქის ფილტრი
    private val _selectedCity = MutableStateFlow<String>("All")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    // Local Theme Preference: "system", "light", "dark"
    private val sharedPrefs = application.getSharedPreferences("meetupx_theme_prefs", android.content.Context.MODE_PRIVATE)
    private val _themePreference = MutableStateFlow(sharedPrefs.getString("theme_pref", "system") ?: "system")
    val themePreference: StateFlow<String> = _themePreference.asStateFlow()

    private val _myEventsSubTab = MutableStateFlow<String>("Joined")
    val myEventsSubTab: StateFlow<String> = _myEventsSubTab.asStateFlow()

    // Repository-დან მიღებული Flow-ები
    val currentUser = repository.currentUserFlow
    val allEvents = repository.getAllEventsFlow()
    val joinedEvents = repository.getJoinedEventsFlow()
    val savedEvents = repository.getSavedEventsFlow()
    val notifications = repository.getNotificationsFlow()

    // აერთიანებს ყველა Flow-ს და აბრუნებს
    // მხოლოდ იმ ღონისძიებებს,
    // რომლებიც ჯერ არც Joined-ია,
    // არც Saved და აკმაყოფილებს ფილტრებს.
    val discoverEvents: StateFlow<List<EventEntity>> = combine(
        allEvents,
        joinedEvents,
        savedEvents,
        _selectedCategory,
        _selectedCity
    ) { events, joined, saved, category, city ->
        val joinedIds = joined.map { it.eventId }.toSet()
        val savedIds = saved.map { it.eventId }.toSet()
        
        events.filter { event ->
            event.id !in joinedIds &&
            event.id !in savedIds &&
            (category == "All" || event.category == category) &&
            (city == "All" || event.location.contains(city, ignoreCase = true))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // აბრუნებს Joined Events-ის სრულ ინფორმაციას
    val joinedEventsDetails: StateFlow<List<EventEntity>> = combine(
        allEvents,
        joinedEvents
    ) { events, joined ->
        val joinedIds = joined.map { it.eventId }.toSet()
        events.filter { it.id in joinedIds }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // აბრუნებს Saved Events-ის სრულ ინფორმაციას
    val savedEventsDetails: StateFlow<List<EventEntity>> = combine(
        allEvents,
        savedEvents
    ) { events, saved ->
        val savedIds = saved.map { it.eventId }.toSet()
        events.filter { it.id in savedIds }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // აბრუნებს მხოლოდ მომხმარებლის მიერ შექმნილ ღონისძიებებს.
    val myCreatedEventsDetails: StateFlow<List<EventEntity>> = allEvents.map { events ->
        events.filter { it.isCustom }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeTab = MutableStateFlow<String>("home")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _popupMessage = MutableStateFlow<PopupState?>(null)
    val popupMessage: StateFlow<PopupState?> = _popupMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulateEvents()
            repository.loadCurrentUser()
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectEvent(eventId: String) {
        _selectedEventId.value = eventId
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
    }

    fun setThemePreference(pref: String) {
        _themePreference.value = pref
        sharedPrefs.edit().putString("theme_pref", pref).apply()
    }

    fun setMyEventsSubTab(subTab: String) {
        _myEventsSubTab.value = subTab
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (email.isBlank() || password.isBlank()) {
                    _authState.value = AuthState.Error("Please enter your email and password.")
                    return@launch
                }

                val result = repository.signIn(email, password)

                _authState.value = if (result.isSuccess) {
                    AuthState.Success
                } else {
                    AuthState.Error(
                        result.exceptionOrNull()?.localizedMessage ?: "Authentication failed."
                    )
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unexpected error")
            }
        }
    }

    fun register(name: String, email: String, university: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (name.isBlank() || email.isBlank() || university.isBlank() || password.isBlank()) {
                    _authState.value = AuthState.Error("Please fill in all fields.")
                    return@launch
                }

                if (password.length < 6) {
                    _authState.value = AuthState.Error("Password must be at least 6 characters.")
                    return@launch
                }

                val result = repository.register(name, email, university, password)

                _authState.value = if (result.isSuccess) {
                    AuthState.Success
                } else {
                    AuthState.Error(
                        result.exceptionOrNull()?.localizedMessage ?: "Registration failed."
                    )
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unexpected error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState.Idle
        }
    }

    // აბრუნებს მხოლოდ მომხმარებლის მიერ შექმნილ ღონისძიებებს
    fun handleSwipeRight(eventId: String) {
        viewModelScope.launch {
            repository.joinEvent(eventId)
            showPopup("Joined Event!", "Check your QR ticket in 'My Events'!", "success")
        }
    }
    
    // Left Swipe - ღონისძიების გამოტოვება
    fun handleSwipeLeft(eventId: String) {
        viewModelScope.launch {
            // Simulated skipped action - simply show a brief visual acknowledgement
            showPopup("Skipped", "Card skipped. Keep sliding!", "info")
        }
    }

    // Up Swipe - ღონისძიების შენახვა
    fun handleSwipeUp(eventId: String) {
        viewModelScope.launch {
            repository.saveEvent(eventId)
            showPopup("Saved Event!", "Added to your bookmarked events deck.", "save")
        }
    }

    // ახალი ღონისძიების შექმნა    
    fun createEvent(title: String, description: String, category: String, date: String, time: String, location: String, imageName: String) {
        viewModelScope.launch {
            repository.createEvent(title, description, category, date, time, location, imageName)
            showPopup("Published Event!", "Your social meetup is now live in discover deck.", "success")
            _myEventsSubTab.value = "Created"
            setActiveTab("my_events")
        }
    }

    fun deleteCreatedEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            showPopup("Deleted Event", "Created event was removed successfully.", "info")
        }
    }

    fun cancelJoinedEvent(eventId: String) {
        viewModelScope.launch {
            repository.leaveEvent(eventId)
            showPopup("Cancelled Booking", "Successfully left event.", "info")
        }
    }

    fun unsaveEvent(eventId: String) {
        viewModelScope.launch {
            repository.unsaveEvent(eventId)
            showPopup("Unsaved Event", "Removed from bookmarks.", "info")
        }
    }

    // მომხმარებლის პროფილის განახლება
    fun updateProfile(name: String, bio: String, university: String) {
        viewModelScope.launch {
            val current = currentUser.value ?: return@launch
            val updated = current.copy(name = name, bio = bio, university = university)
            repository.saveProfile(updated)
            showPopup("Profile Updated", "Your details have been saved.", "success")
        }
    }

    // NOTIFICATION MANAGEMENT
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    private fun showPopup(title: String, message: String, type: String) {
        _popupMessage.value = PopupState(title, message, type)
    }

    fun dismissPopup() {
        _popupMessage.value = null
    }

    // აბრუნებს Join-ისას გენერირებულ QR Reference-ს
    fun getQrCodeReferenceForEvent(eventId: String): StateFlow<String> {
        return joinedEvents.map { list ->
            list.find { it.eventId == eventId }?.qrCodeReference ?: "MUX-QR-PENDING"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "MUX-QR-PENDING")
    }

    fun isJoinedFlow(eventId: String): Flow<Boolean> = repository.isJoinedFlow(eventId)
    fun isSavedFlow(eventId: String): Flow<Boolean> = repository.isSavedFlow(eventId)
}

data class PopupState(
    val title: String,
    val message: String,
    val type: String // "success", "info", "save"
)

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

