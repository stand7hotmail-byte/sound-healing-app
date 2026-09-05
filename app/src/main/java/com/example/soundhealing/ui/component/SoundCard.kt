package com.example.soundhealing.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.soundhealing.domain.DisplayData
import com.example.soundhealing.domain.SoundType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundCard(
    soundType: SoundType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val display = when (soundType) {
        is SoundType.Solfeggio -> soundType.frequency.displayData
        is SoundType.Nature -> soundType.sound.displayData
        is SoundType.Brainwave -> DisplayData(
            soundType.type.label,
            "${soundType.type.frequencyRange} - ${soundType.type.purpose}",
            "🧠"
        )
    }
    val (emoji, name, desc) = Triple(display.emoji, display.title, display.description)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = MaterialTheme.shapes.medium,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isSelected) "停止" else "再生",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
