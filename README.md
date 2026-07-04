# MeetUpX

MeetUpX is a modern Android social meetup application that allows users to discover, join, save, and create local events in Georgia.

---

## Features

### Authentication
- User registration and login via Firebase Authentication
- Secure logout functionality

### Event System
- Discover events based on category and city filters
- Join events and manage tickets
- Save/bookmark events for later
- Create and publish custom events
- Delete or manage created events

### My Events
- Joined events
- Saved events
- Created events

### Notifications
- Event-related notifications
- Mark as read / clear all

### Profile System
- Edit user profile (name, bio, location)
- View activity stats (joined, saved, created)
- Profile section

### UI/UX
- Jetpack Compose modern UI
- Light/Dark/System theme support
- Smooth animations and transitions

---

## Architecture

- MVVM (Model-View-ViewModel)
- Repository Pattern
- Reactive StateFlow / Flow system

---

## Database

### Local
- Room Database for offline caching:
  - Events
  - Joined events
  - Saved events

### Cloud
- Firebase Authentication
- Firebase Realtime Database (sync)

---

## Tech Stack

- Kotlin
- Jetpack Compose
- Room Database
- Firebase Auth
- Firebase Realtime Database
- Coroutines & Flow

---

## App Purpose

MeetUpX helps users in Georgia connect through real-life events such as:
- Music meetups
- Food experiences
- Sports activities
- Travel & social gatherings

---

## Extra Features

- Swipe-based event interaction
- Animated UI components
- Theme customization
- Smart filtering system

---

![Start Page](images/StartPage.png)
![Sign Up](images/SignUp.png)
![Log In](images/LogIn.png)
![APP](images/app.mov)


