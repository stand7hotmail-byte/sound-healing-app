import base64
from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# RandomSession.kt with Parcelable
rs = '''package com.example.soundhealing.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RandomSession(
    val frequency: SolfeggioFrequency,
    val fadeInSeconds: Int = (1..5).random(),
    val fadeOutSeconds: Int = (1..5).random(),
    val startDelaySeconds: Int = (0..30).random(),
    val durationSeconds: Int = (30..120).random()
) : Parcelable {
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
'''

p = base / 'app/src/main/java/com/example/soundhealing/domain/RandomSession.kt'
p.write_bytes(rs.encode('utf-8'))
b = p.read_bytes()
print(f'RandomSession.kt: eq={b.count(b"=")} bytes={len(b)}')
