package com.example.soundhealing.ui.screen

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
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
    )
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
            val isActive = typeChecker(soundType) && uiState.playing == soundType
            SoundCard(
                soundType = soundType,
                isSelected = isActive,
                onClick = {
                    if (isActive) {
                        viewModel.stopSound(soundType)
                    } else {
                        viewModel.playSound(soundType)
                    }
                }
            )
        }
    }

    if (uiState.playing != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val name = when (val s = uiState.playing) {
                is SoundType.Solfeggio -> s.frequency.name
                is SoundType.Nature -> s.sound.name
                is SoundType.Brainwave -> s.type.label
                else -> ""
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
            if (uiState.playing != null) {
                WaveformView(soundType = uiState.playing)
            }
            Spacer(modifier = Modifier.height(8.dp))
            VolumeSlider(
                value = uiState.volume,
                onValueChange = viewModel::setVolume
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = viewModel::stopAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("停止")
            }
        }
    }
}

@Composable
fun RandomTab() {
    val context = LocalContext.current
    val randomVM = remember { RandomSessionViewModel(context.applicationContext as android.app.Application) }
    val state by randomVM.state.collectAsState()

    LaunchedEffect(Unit) {
        randomVM.generateSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                items(state.sessions) { session ->
                    val index = state.sessions.indexOf(session)
                    val isSelected = state.selectedIndices.contains(index)
                    SoundCard(
                        soundType = session.soundType,
                        isSelected = isSelected,
                        onClick = { randomVM.toggleSelection(index) }
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
                    Text(text = "音量", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    VolumeSlider(
                        value = state.volume,
                        onValueChange = { randomVM.setVolume(it) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { randomVM.startPlaying() },
                        modifier = Modifier.weight(1f),
                        enabled = state.selectedIndices.isNotEmpty() && !state.isPlaying
                    ) {
                        Text("再生")
                    }
                    Button(
                        onClick = { randomVM.stopPlaying() },
                        modifier = Modifier.weight(1f),
                        enabled = state.isPlaying,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("停止")
                    }
                }
            }
        }
    }
}
