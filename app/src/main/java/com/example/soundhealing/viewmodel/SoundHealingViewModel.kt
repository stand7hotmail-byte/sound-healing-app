package com.example.soundhealing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SoundType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val activeSounds: List<ActiveSound> = emptyList(),
    val volume: Float = 0.5f,
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false
)

data class ActiveSound(
    val type: SoundType,
    val volume: Float
)

class SoundHealingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val audioEngines = mutableMapOf<SoundType, AudioEngine>()

    fun playSound(soundType: SoundType) {
        val engine = audioEngines.getOrPut(soundType) {
            AudioEngine().also { it.start(soundType) }
        }
        engine.setVolume(_uiState.value.volume)
        updateState()
    }

    fun stopSound(soundType: SoundType) {
        audioEngines[soundType]?.stop()
        audioEngines.remove(soundType)
        updateState()
    }

    fun stopAll() {
        audioEngines.values.forEach { it.stop() }
        audioEngines.clear()
        cancelTimer()
        updateState()
    }

    fun setVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(volume = volume)
        audioEngines.forEach { _, engine -> engine.setVolume(volume) }
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
        // nothing to cancel
    }

    private fun updateState() {
        _uiState.value = _uiState.value.copy(
            activeSounds = audioEngines.keys.map { type ->
                ActiveSound(type, _uiState.value.volume)
            }
        )
    }
}
