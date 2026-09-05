package com.example.soundhealing.domain

data class RandomSession(
    val frequency: SolfeggioFrequency,
    val fadeInSeconds: Int = (1..5).random(),
    val fadeOutSeconds: Int = (1..5).random(),
    val startDelaySeconds: Int = (0..30).random(),
    val durationSeconds: Int = (30..120).random()
) {
    val soundType = SoundType.Solfeggio(frequency)
    
    companion object {
        fun generateAll(): List<RandomSession> {
            return SolfeggioFrequency.ALL.map { freq ->
                RandomSession(
                    frequency = freq,
                    fadeInSeconds = (1..5).random(),
                    fadeOutSeconds = (1..5).random(),
                    startDelaySeconds = (0..30).random(),
                    durationSeconds = (30..120).random()
                )
            }
        }
    }
}
