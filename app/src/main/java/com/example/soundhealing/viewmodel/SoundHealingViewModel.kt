package com.example.soundhealing.viewmodel

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
            isPlaying = newActive.isNotEmpty(),
            activeSounds = newActive
        )
    }

    fun stopSound(type: SoundType) {
        val newActive = _uiState.value.activeSounds.filter { it.type != type }
        _uiState.value = _uiState.value.copy(
            isPlaying = newActive.isNotEmpty(),
            activeSounds = newActive
        )
    }

    fun stopAll() {
        audioEngine.stopAll()
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            activeSounds = emptyList()
        )
    }

    fun setVolume(volume: Float) {
        audioEngine.setVolume(volume)
        _uiState.value = _uiState.value.copy(volume = volume)
    }

    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(timerRunning = true)
            for (i in seconds downTo 1) {
                delay(1000L)
                _uiState.value = _uiState.value.copy(timerSeconds = i)
            }
            stopAll()
            _uiState.value = _uiState.value.copy(timerRunning = false, timerSeconds = 0)
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(timerRunning = false, timerSeconds = 0)
    }

    override fun onCleared() {
        super.onCleared()
        stopAll()
    }
}
