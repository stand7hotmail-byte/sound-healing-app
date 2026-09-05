package com.example.soundhealing.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.soundhealing.domain.RandomSession
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.service.AudioPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RandomSessionState(
    val sessions: List<RandomSession> = emptyList(),
    val selectedIndices: Set<Int> = setOf(),
    val isPlaying: Boolean = false,
    val volume: Float = 0.5f
)

class RandomSessionViewModel(application: Application) : AndroidViewModel(application) {
    companion object { const val TAG = "RandomSessionVM" }
    
    private val _state = MutableStateFlow(RandomSessionState())
    val state: StateFlow<RandomSessionState> = _state.asStateFlow()
    
    fun generateSessions() {
        val sessions = RandomSession.generateAll()
        _state.value = _state.value.copy(sessions = sessions)
        Log.d(TAG, "Generated ${sessions.size} sessions")
    }
    
    fun toggleSelection(index: Int) {
        val selected = _state.value.selectedIndices.toMutableSet()
        if (selected.contains(index)) {
            selected.remove(index)
        } else {
            selected.add(index)
        }
        _state.value = _state.value.copy(selectedIndices = selected)
    }
    
    fun startPlaying() {
        val selected = _state.value.selectedIndices
        if (selected.isEmpty()) return
        
        val sessions = _state.value.sessions.filterIndexed { index, _ -> index in selected }
        Log.d(TAG, "Start playing ${sessions.size} sessions")
        
        sessions.forEach { session ->
            AudioPlaybackService.startWithDelay(
                getApplication(),
                session,
                session.startDelaySeconds * 1000L
            )
        }
        
        _state.value = _state.value.copy(isPlaying = true)
    }
    
    fun stopPlaying() {
        AudioPlaybackService.stop(getApplication())
        _state.value = _state.value.copy(isPlaying = false, selectedIndices = emptySet())
    }
    
    fun setVolume(v: Float) {
        AudioPlaybackService.updateVolume(getApplication(), v)
        _state.value = _state.value.copy(volume = v)
    }
}
