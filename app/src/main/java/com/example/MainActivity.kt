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

class MainActivity : ComponentActivity() {
  private val viewModel: MeetUpXViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themePref by viewModel.themePreference.collectAsState()
      val darkTheme = when (themePref) {
          "light" -> false
          "dark" -> true
          else -> androidx.compose.foundation.isSystemInDarkTheme()
      }
      MyApplicationTheme(darkTheme = darkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          MeetUpXApp(viewModel = viewModel)
        }
      }
    }
  }
}
