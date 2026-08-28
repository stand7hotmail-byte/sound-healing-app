package com.example.soundhealing.domain

sealed class SoundType {
    data class Solfeggio(val frequency: SolfeggioFrequency) : SoundType()
    data class Nature(val sound: NatureSound) : SoundType()
    data class Brainwave(val type: BrainwaveType) : SoundType()
}
