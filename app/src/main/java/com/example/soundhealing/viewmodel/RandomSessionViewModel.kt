package com.example.soundhealing.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.RandomSession
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
    companion object {
        const val TAG = "RandomSessionVM"
    }
    
    private val _state = MutableStateFlow(RandomSessionState())
    val state: StateFlow<RandomSessionState> = _state.asStateFlow()
    
    // 各セッションのAudioEngineリスト
    private val engines = mutableListOf<AudioEngine>()
    
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
        
        // 未生成のEngineを作成
        while (engines.size < sessions.size) {
            engines.add(AudioEngine())
        }
        
        // 各セッションを別Engineで再生
        sessions.forEachIndexed { i, session ->
            engines[i].start(session)
            Log.d(TAG, "Started engine $i for ${session.frequency.name}")
        }
        
        _state.value = _state.value.copy(isPlaying = true)
    }
    
    fun stopPlaying() {
        engines.forEach { it.stop() }
        Log.d(TAG, "Stopped all engines")
        _state.value = _state.value.copy(isPlaying = false)
    }
    
    fun setVolume(volume: Float) {
        _state.value = _state.value.copy(volume = volume)
        engines.forEach { it.setVolume(volume) }
        Log.d(TAG, "Set volume to $volume")
    }
    
    override fun onCleared() {
        super.onCleared()
        stopPlaying()
        engines.clear()
        Log.d(TAG, "ViewModel cleared")
    }
}
