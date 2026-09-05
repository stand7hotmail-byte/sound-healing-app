package com.example.soundhealing.ui.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.soundhealing.domain.SoundType

@Composable
fun WaveformView(
    soundType: SoundType,
    modifier: Modifier = Modifier,
    amplitude: Float = 0.8f
) {
    val color = when (soundType) {
        is SoundType.Solfeggio -> Color(0xFF0D9488)
        is SoundType.Nature -> Color(0xFF22C55E)
        is SoundType.Brainwave -> Color(0xFF8B5CF6)
    }
    val secondaryColor = color.copy(alpha = 0.3f)

    val transition = rememberInfiniteTransition(label = "waveform")
    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveform_phase"
    )
    val thickness = transition.animateFloat(
        initialValue = 2f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveform_thickness"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFF1E293B))
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val waveHeight = height * 0.35f * amplitude
        val points = 200
        val p = phase.value
        val t = thickness.value

        // Draw center line
        drawLine(
            color = secondaryColor,
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1f
        )

        // Draw main waveform
        var prevX = 0f
        var prevY = 0f
        for (i in 0 until points) {
            val x = (i / points.toFloat()) * width
            val normalizedX = (i / points.toFloat()) * 6f * Math.PI + p
            val y = centerY + waveHeight * Math.sin(normalizedX).toFloat()

            if (i == 0) {
                prevX = x
                prevY = y
            } else {
                drawLine(
                    color = color,
                    start = Offset(prevX, prevY),
                    end = Offset(x, y),
                    strokeWidth = t
                )
                prevX = x
                prevY = y
            }
        }

        // Draw secondary harmonic wave
        prevX = 0f
        prevY = 0f
        for (i in 0 until points) {
            val x = (i / points.toFloat()) * width
            val normalizedX = (i / points.toFloat()) * 6f * Math.PI + p * 0.7f
            val y = centerY + waveHeight * 0.4f * Math.cos(normalizedX).toFloat()

            if (i == 0) {
                prevX = x
                prevY = y
            } else {
                drawLine(
                    color = secondaryColor,
                    start = Offset(prevX, prevY),
                    end = Offset(x, y),
                    strokeWidth = 1.5f
                )
                prevX = x
                prevY = y
            }
        }
    }
}
