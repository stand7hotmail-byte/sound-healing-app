package com.example.soundhealing.ui.screen

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
import com.example.soundhealing.domain.RandomSession
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.ui.component.SoundCard
import com.example.soundhealing.ui.component.VolumeSlider
import com.example.soundhealing.ui.component.WaveformView
import com.example.soundhealing.viewmodel.RandomSessionViewModel
import com.example.soundhealing.viewmodel.SoundHealingViewModel

enum class SoundTab {
    SOLFEGGIO, NATURE, BRAINWAVE, RANDOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: SoundHealingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(SoundTab.SOLFEGGIO) }

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
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
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
                                    SoundTab.SOLFEGGIO -> "ソルフェジオ"
                                    SoundTab.NATURE -> "自然音"
                                    SoundTab.BRAINWAVE -> "脳波"
                                    SoundTab.RANDOM -> "ランダム生成"
                                }
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                SoundTab.SOLFEGGIO -> SoundTabContent(
                    items = SolfeggioFrequency.ALL.map { SoundType.Solfeggio(it) },
                    viewModel = viewModel,
                    uiState = uiState,
                    typeChecker = { it is SoundType.Solfeggio }
                )
                SoundTab.NATURE -> SoundTabContent(
                    items = NatureSound.ALL.map { SoundType.Nature(it) },
                    viewModel = viewModel,
                    uiState = uiState,
                    typeChecker = { it is SoundType.Nature }
                )
                SoundTab.BRAINWAVE -> SoundTabContent(
                    items = BrainwaveType.entries.map { SoundType.Brainwave(it) },
                    viewModel = viewModel,
                    uiState = uiState,
                    typeChecker = { it is SoundType.Brainwave }
                )
                SoundTab.RANDOM -> RandomTab()
            }
        }
    }
}

@Composable
fun SoundTabContent(
    items: List<SoundType>,
    viewModel: SoundHealingViewModel,
    uiState: SoundHealingViewModel.UiState,
    typeChecker: (SoundType) -> Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { soundType ->
            val isActive = uiState.playing == soundType
            SoundCard(
                soundType = soundType,
                isSelected = isActive,
                onClick = {
                    if (isActive) viewModel.stopSound(soundType)
                    else viewModel.playSound(soundType)
                }
            )
        }
    }

    if (uiState.playing != null && typeChecker(uiState.playing)) {
        WaveformView(
            soundType = uiState.playing,
            modifier = Modifier.fillMaxWidth(),
            amplitude = 0.8f
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "音量", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${(uiState.volume * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            VolumeSlider(
                value = uiState.volume,
                onValueChange = { viewModel.setVolume(it) }
            )
        }
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

@Composable
fun RandomTab() {
    val viewModel: RandomSessionViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.generateSessions()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ランダム生成: 各周波数のフェードイン/アウト時間と再生タイミングをランダムに生成します",
            style = MaterialTheme.typography.bodyMedium
        )

        if (state.sessions.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.sessions.size) { index ->
                    val session = state.sessions[index]
                    val isSelected = state.selectedIndices.contains(index)
                    SoundCard(
                        soundType = session.soundType,
                        isSelected = isSelected,
                        onClick = { viewModel.toggleSelection(index) }
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "選択中: ${state.selectedIndices.size} 個の周波数",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "音量", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${(state.volume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                VolumeSlider(
                    value = state.volume,
                    onValueChange = { viewModel.setVolume(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.startPlaying() },
                        modifier = Modifier.weight(1f),
                        enabled = state.selectedIndices.isNotEmpty() && !state.isPlaying
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("再生開始")
                    }
                    Button(
                        onClick = { viewModel.stopPlaying() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = state.isPlaying
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("停止")
                    }
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
