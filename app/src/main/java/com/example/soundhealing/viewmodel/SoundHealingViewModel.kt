package com.example.soundhealing.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActiveSound(
    val type: SoundType,
    val audioTrack: Any? = null
)

data class UiState(
    val isPlaying: Boolean = false,
    val activeSounds: List<ActiveSound> = emptyList(),
    val volume: Float = 0.5f,
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false
)

class SoundHealingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val audioEngine = AudioEngine()
    private var timerJob: Job? = null

    init {
        _uiState.value = UiState(volume = 0.5f)
    }

    fun playSound(type: SoundType) {
        when (type) {
            is SoundType.Solfeggio -> {
                audioEngine.playSolfeggio(type.frequency.frequency)
            }
            is SoundType.Nature -> {
                audioEngine.playNatureSound(type.sound)
            }
            is SoundType.Brainwave -> {
                audioEngine.playBrainwave(type.type)
            }
        }
        val newActive = _uiState.value.activeSounds + ActiveSound(type)
        _uiState.value = _uiState.value.copy(
            isPlaying = true,
            activeSounds = newActive
        )
    }

    fun stopSound(type: SoundType) {
        // For simplicity, we stop all when one is selected
        // In a full app, you'd track individual AudioTracks
        stopAll()
    }

    fun stopAll() {
        audioEngine.stopAll()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            activeSounds = emptyList(),
            timerRunning = false
        )
    }

    fun setVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
        _uiState.value = _uiState.value.copy(volume = volume)
    }

    fun setTimer(seconds: Int) {
        _uiState.value = _uiState.value.copy(timerSeconds = seconds)
    }

    fun startTimer() {
        val seconds = _uiState.value.timerSeconds
        if (seconds <= 0) return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(seconds * 1000L)
            stopAll()
        }
        _uiState.value = _uiState.value.copy(timerRunning = true)
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(timerRunning = false)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
