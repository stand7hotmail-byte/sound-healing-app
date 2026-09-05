from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

vm = """package com.example.soundhealing.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SoundType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

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
}
"""

p = base / 'app/src/main/java/com/example/soundhealing/viewmodel/SoundHealingViewModel.kt'
p.write_text(vm)
b = p.read_bytes()
print(f'ViewModel fixed: bytes={len(b)} eq={b.count(b\"=\")}')
print(f'has value =: {b"value = _uiState" in b}')
print(f'has copy(playing =: {b"copy(playing =" in b}')
