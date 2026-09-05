package com.example.soundhealing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.soundhealing.service.AudioPlaybackService
import com.example.soundhealing.ui.screen.MainScreen
import com.example.soundhealing.ui.theme.SoundHealingTheme
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start foreground service with a default sound for testing
        val testType = SoundType.Solfeggio(SolfeggioFrequency.ALL.first())
        AudioPlaybackService.start(this, testType)
        
        enableEdgeToEdge()
        setContent {
            SoundHealingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
