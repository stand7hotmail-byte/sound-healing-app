package com.example.soundhealing.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.service.AudioPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val playing: SoundType? = null,
    val volume: Float = 0.5f,
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false
)

class SoundHealingViewModel(application: Application) : AndroidViewModel(application) {
    companion object { const val TAG = "ViewModel" }
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun playSound(soundType: SoundType) {
        Log.d(TAG, "playSound type=$soundType")
        AudioPlaybackService.start(getApplication(), soundType)
        _uiState.value = _uiState.value.copy(playing = soundType)
    }

    fun stopSound(soundType: SoundType) {
        Log.d(TAG, "stopSound type=$soundType")
        AudioPlaybackService.stop(getApplication())
        if (_uiState.value.playing == soundType) {
            _uiState.value = _uiState.value.copy(playing = null)
        }
    }

    fun stopAll() {
        Log.d(TAG, "stopAll")
        AudioPlaybackService.stop(getApplication())
        cancelTimer()
        _uiState.value = _uiState.value.copy(playing = null, timerRunning = false)
    }

    fun setVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(volume = volume)
        AudioPlaybackService.updateVolume(getApplication(), volume)
    }

    fun startTimer(seconds: Int) {
        cancelTimer()
        _uiState.value = _uiState.value.copy(timerSeconds = seconds, timerRunning = true)
        viewModelScope.launch {
            for (i in seconds downTo 0) {
                _uiState.value = _uiState.value.copy(timerSeconds = i)
                if (i == 0) {
                    stopAll()
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun cancelTimer() {
        _uiState.value = _uiState.value.copy(timerSeconds = 0, timerRunning = false)
    }
}
