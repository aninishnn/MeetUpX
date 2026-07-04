package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.MeetUpXApp
import com.example.ui.viewmodel.MeetUpXViewModel

// ინიციალიზდება ViewModel და იტვირთება Compose UI
class MainActivity : ComponentActivity() {
  private val viewModel: MeetUpXViewModel by viewModels()

  // ViewModel-ის შექმნა, რომელიც მართავს აპლიკაციის მდგომარეობას
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      // ViewModel-დან მომხმარებლის მიერ არჩეული Theme Preference-ის მიღება
      val themePref by viewModel.themePreference.collectAsState()
      val darkTheme = when (themePref) {
          "light" -> false
          "dark" -> true
          else -> androidx.compose.foundation.isSystemInDarkTheme()
      }
      MyApplicationTheme(darkTheme = darkTheme) {
        // ძირითადი კონტეინერი, სადაც იტვირთება მთელი აპლიკაცია
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
           // აპლიკაციის მთავარი Compose ეკრანი
          MeetUpXApp(viewModel = viewModel)
        }
      }
    }
  }
}
