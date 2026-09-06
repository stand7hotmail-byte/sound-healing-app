package com.example.soundhealing.viewmodel

import android.app.Application
import android.widget.Toast
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.service.AudioPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoundHealingViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "ViewModel"
    }

    data class UiState(
        val playing: SoundType? = null,
        val volume: Float = 0.5f,
        val timerSeconds: Int = 0,
        val timerRunning: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var audioEngine: AudioEngine? = null

    fun playSound(soundType: SoundType) {
        val freq = when (soundType) {
            is SoundType.Solfeggio -> soundType.frequency.frequency
            else -> 440.0
        }
        Log.d(TAG, "playSound freq=$freq")
        AudioPlaybackService.start(getApplication(), freq)
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
        _uiState.value = _uiState.value.copy(playing = null, timerRunning = false)
    }

    fun setVolume(volume: Float) {
        Log.d(TAG, "setVolume $volume")
        AudioPlaybackService.updateVolume(getApplication(), volume)
        _uiState.value = _uiState.value.copy(volume = volume)
    }

    fun startTimer(seconds: Int) {
        stopAll()
        _uiState.value = _uiState.value.copy(timerSeconds = seconds, timerRunning = true)
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                kotlinx.coroutines.delay(1000L)
                _uiState.value = _uiState.value.copy(timerSeconds = i)
            }
            stopAll()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(timerRunning = false, timerSeconds = 0)
    }

    fun testTone(frequency: Double) {
        Log.d(TAG, "testTone: $frequency Hz")
        Toast.makeText(getApplication(), "testTone $frequency Hz", Toast.LENGTH_SHORT).show()
        audioEngine = AudioEngine()
        audioEngine?.startTone(frequency)
    }

    fun stopTestTone() {
        Log.d(TAG, "stopTestTone")
        Toast.makeText(getApplication(), "stopTestTone", Toast.LENGTH_SHORT).show()
        audioEngine?.stop()
        audioEngine = null
    }
}
