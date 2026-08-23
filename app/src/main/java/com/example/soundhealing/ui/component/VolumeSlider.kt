package com.example.soundhealing.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..1f,
        steps = 10,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = SliderDefaults.colors(
            activeTrackColor = androidx.compose.ui.graphics.Color(0xFF4DB6AC),
            inactiveTrackColor = androidx.compose.ui.graphics.Color(0xFF3D4F60),
            thumbColor = androidx.compose.ui.graphics.Color(0xFF4DB6AC)
        )
    )
}
