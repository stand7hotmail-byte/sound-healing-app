package com.example.soundhealing.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPicker(
    selectedSeconds: Int,
    onSelectedChange: (Int) -> Unit
) {
    val options = listOf(
        5 * 60 to "5\ub2ec",
        15 * 60 to "15\ub2ec",
        30 * 60 to "30\ub2ec",
        60 * 60 to "60\ub2ec"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (seconds, label) ->
            FilterChip(
                selected = selectedSeconds == seconds,
                onClick = { onSelectedChange(seconds) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
