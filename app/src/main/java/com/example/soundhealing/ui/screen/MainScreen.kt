package com.example.soundhealing.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.ui.component.SoundCard
import com.example.soundhealing.ui.component.TimerPicker
import com.example.soundhealing.ui.component.VolumeSlider
import com.example.soundhealing.viewmodel.SoundHealingViewModel

enum class SoundTab {
    SOLFEGGIO, NATURE, BRAINWAVE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: SoundHealingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(SoundTab.SOLFEGGIO) }
    var selectedSound by remember { mutableStateOf<SoundType?>(null) }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopAll() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "サウンドヒーリング",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                SoundTab.values().forEach { tab ->
                    Tab(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = when (tab) {
                                    SoundTab.SOLFEGGIO -> "ソルフェッジョ"
                                    SoundTab.NATURE -> "自然音"
                                    SoundTab.BRAINWAVE -> "脳波"
                                }
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                SoundTab.SOLFEGGIO -> {
                    val frequencies = SolfeggioFrequency.ALL
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(frequencies) { frequency ->
                            val isActive = uiState.activeSounds.any {
                                it.type is SoundType.Solfeggio &&
                                    (it.type as SoundType.Solfeggio).frequency == frequency
                            }
                            SoundCard(
                                soundType = SoundType.Solfeggio(frequency),
                                isSelected = isActive,
                                onClick = {
                                    if (isActive) {
                                        viewModel.stopSound(SoundType.Solfeggio(frequency))
                                    } else {
                                        viewModel.playSound(SoundType.Solfeggio(frequency))
                                    }
                                    selectedSound = SoundType.Solfeggio(frequency)
                                }
                            )
                        }
                    }
                }

                SoundTab.NATURE -> {
                    val sounds = NatureSound.ALL
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sounds) { sound ->
                            val isActive = uiState.activeSounds.any {
                                it.type is SoundType.Nature &&
                                    (it.type as SoundType.Nature).sound == sound
                            }
                            SoundCard(
                                soundType = SoundType.Nature(sound),
                                isSelected = isActive,
                                onClick = {
                                    if (isActive) {
                                        viewModel.stopSound(SoundType.Nature(sound))
                                    } else {
                                        viewModel.playSound(SoundType.Nature(sound))
                                    }
                                    selectedSound = SoundType.Nature(sound)
                                }
                            )
                        }
                    }
                }

                SoundTab.BRAINWAVE -> {
                    val types = BrainwaveType.values()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(types) { type ->
                            val isActive = uiState.activeSounds.any {
                                it.type is SoundType.Brainwave &&
                                    (it.type as SoundType.Brainwave).type == type
                            }
                            SoundCard(
                                soundType = SoundType.Brainwave(type),
                                isSelected = isActive,
                                onClick = {
                                    if (isActive) {
                                        viewModel.stopSound(SoundType.Brainwave(type))
                                    } else {
                                        viewModel.playSound(SoundType.Brainwave(type))
                                    }
                                    selectedSound = SoundType.Brainwave(type)
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.activeSounds.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "音量", style = MaterialTheme.typography.titleSmall)
                        Text(text = "${(uiState.volume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    }
                    VolumeSlider(
                        value = uiState.volume,
                        onValueChange = { viewModel.setVolume(it) }
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "タイマー", style = MaterialTheme.typography.titleSmall)
                TimerPicker(
                    selectedSeconds = uiState.timerSeconds,
                    onSelectedChange = { seconds ->
                        if (seconds > 0) viewModel.startTimer(seconds) else viewModel.cancelTimer()
                    }
                )
                if (uiState.timerRunning) {
                    Text(
                        text = "残り ${uiState.timerSeconds}秒",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (uiState.activeSounds.isNotEmpty()) {
                Button(
                    onClick = { viewModel.stopAll() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("すべて停止")
                }
            }
        }
    }
}
