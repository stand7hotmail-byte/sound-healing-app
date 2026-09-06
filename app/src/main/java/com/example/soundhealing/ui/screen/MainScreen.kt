package com.example.soundhealing.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.ui.component.SoundCard
import com.example.soundhealing.ui.component.VolumeSlider
import com.example.soundhealing.ui.component.WaveformView
import com.example.soundhealing.viewmodel.RandomSessionViewModel
import com.example.soundhealing.viewmodel.SoundHealingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext

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
                }
            )
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    SoundTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == selectedTab,
                            onClick = { selectedTab = tab },
                            text = {
                                val label = when (tab) {
                                    SoundTab.SOLFEGGIO -> "ソルフェジオ"
                                    SoundTab.NATURE -> "自然音"
                                    SoundTab.BRAINWAVE -> "脳波"
                                    SoundTab.RANDOM -> "ランダム生成"
                                }
                                Text(label)
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
}
