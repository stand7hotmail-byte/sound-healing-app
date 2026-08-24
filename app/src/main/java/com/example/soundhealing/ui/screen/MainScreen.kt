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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
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
    viewModel: SoundHealingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(SoundTab.SOLFEGGIO) }
    var selectedSound by remember { mutableStateOf<SoundType?>(null) }

    // Stop all sounds when leaving the screen
    DisposableEffect(Unit) {
        onDispose { viewModel.stopAll() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "サウンドヒーリング",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (uiState.isPlaying) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔊 再生中: ${uiState.activeSounds.size} 音源",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FilledTonalIconButton(
                                onClick = { viewModel.stopAll() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "すべて停止",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "音量",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    VolumeSlider(
                        value = uiState.volume,
                        onValueChange = { viewModel.setVolume(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "タイマー",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TimerPicker(
                        selectedSeconds = uiState.timerSeconds,
                        onSelectedChange = { viewModel.setTimer(it) }
                    )
                    if (uiState.timerSeconds > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (uiState.timerRunning) {
                                TextButton(onClick = { viewModel.cancelTimer() }) {
                                    Text("タイマー取消")
                                }
                            } else {
                                FilledTextButton(onClick = { viewModel.startTimer() }) {
                                    Text("タイマー開始")
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Row
            Tabs(
                tabs = listOf(
                    SoundTab.SOLFEGGIO to "ソルフェジオ周波数",
                    SoundTab.NATURE to "自然音",
                    SoundTab.BRAINWAVE to "脳波同調音"
                ),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content
            when (selectedTab) {
                SoundTab.SOLFEGGIO -> SolfeggioTab(
                    onSoundSelected = { soundType ->
                        selectedSound = soundType
                        if (uiState.isPlaying && selectedSound == soundType) {
                            viewModel.stopSound(soundType)
                            selectedSound = null
                        } else {
                            viewModel.playSound(soundType)
                        }
                    }
                )
                SoundTab.NATURE -> NatureTab(
                    onSoundSelected = { soundType ->
                        selectedSound = soundType
                        if (uiState.isPlaying && selectedSound == soundType) {
                            viewModel.stopSound(soundType)
                            selectedSound = null
                        } else {
                            viewModel.playSound(soundType)
                        }
                    }
                )
                SoundTab.BRAINWAVE -> BrainwaveTab(
                    onSoundSelected = { soundType ->
                        selectedSound = soundType
                        if (uiState.isPlaying && selectedSound == soundType) {
                            viewModel.stopSound(soundType)
                            selectedSound = null
                        } else {
                            viewModel.playSound(soundType)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Tabs(
    tabs: List<Pair<SoundTab, String>>,
    selectedTab: SoundTab,
    onTabSelected: (SoundTab) -> Unit
) {
    TabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        tabs.forEach { (tab, label) ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

@Composable
fun SolfeggioTab(onSoundSelected: (SoundType) -> Unit) {
    val isSelected = { freq: SolfeggioFrequency -> false }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SolfeggioFrequency.ALL) { frequency ->
            SoundCard(
                soundType = SoundType.Solfeggio(frequency),
                isSelected = isSelected(frequency),
                onClick = { onSoundSelected(SoundType.Solfeggio(frequency)) }
            )
        }
    }
}

@Composable
fun NatureTab(onSoundSelected: (SoundType) -> Unit) {
    val isSelected = { sound: NatureSound -> false }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(NatureSound.ALL) { sound ->
            SoundCard(
                soundType = SoundType.Nature(sound),
                isSelected = isSelected(sound),
                onClick = { onSoundSelected(SoundType.Nature(sound)) }
            )
        }
    }
}

@Composable
fun BrainwaveTab(onSoundSelected: (SoundType) -> Unit) {
    val isSelected = { type: BrainwaveType -> false }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(BrainwaveType.values()) { type ->
            SoundCard(
                soundType = SoundType.Brainwave(type),
                isSelected = isSelected(type),
                onClick = { onSoundSelected(SoundType.Brainwave(type)) }
            )
        }
    }
}
