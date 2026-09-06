package com.example.soundhealing.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.ui.component.SoundCard
import com.example.soundhealing.ui.component.VolumeSlider
import com.example.soundhealing.viewmodel.SoundHealingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: SoundHealingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var clickCount by remember { mutableStateOf(0) }
    var lastClick by remember { mutableStateOf<String?>(null) }

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
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (clickCount > 0) {
                Text(text = "クリック数: $clickCount, 最後: $lastClick")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        clickCount++
                        lastClick = "再生"
                        viewModel.testTone(440.0)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("テスト音再生")
                }
                Button(
                    onClick = {
                        clickCount++
                        lastClick = "停止"
                        viewModel.stopTestTone()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("テスト音停止")
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SolfeggioFrequency.ALL) { freq ->
                    SoundCard(
                        soundType = SoundType.Solfeggio(freq),
                        isSelected = false,
                        onClick = {
                            clickCount++
                            lastClick = "カード: ${freq.name}"
                            viewModel.playSound(SoundType.Solfeggio(freq))
                        }
                    )
                }
            }

            VolumeSlider(
                value = uiState.volume,
                onValueChange = { viewModel.setVolume(it) }
            )
        }
    }
}
