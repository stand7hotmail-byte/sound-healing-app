# Sound Healing App Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Androidヒーリング音アプリ — ソルフェジオ周波数、自然音、脳波同調音を生演奏し、タイマー・ボリューム・ミキシング対応の静養アプリ。

**Architecture:** Jetpack Compose (Material 3) UI + KotlinでAudioTrackによる正弦波発振。ソルフェジオ周波数は直接生成、自然音はホワイトノイズ+フィルター合成、脳波同調音はバイノーラルビート（左右別周波数）実装。MVIアーキテクチャ（ViewModel + StateFlow）。

**Tech Stack:** Kotlin 1.9+, Jetpack Compose Material3, AudioTrack, ViewModel/StateFlow, Gradle 8+, minSdk 24

## Global Constraints

- 日本語UI（strings.xmlは全部日本語）
- minSdk 24（Android 7.0+）
- Compose Material 3（ダークテーマ基調、ヒーリング感）
- 正弦波はAudioTrackでリアルタイム生成（asset音源不要）
- バイノーラルビートは左右別チャンネル（ヘッドホン推奨注意書き付き）
- タイマー設定（5/15/30/60分）
- 複数音の同時再生（ミキサー機能）

---

## Task 1: プロジェクト構造作成

**Files:**
- Create: `/c/Users/stand/Documents/hermes_project/sound-healing-app/build.gradle`
- Create: `/c/Users/stand/Documents/hermes_project/sound-healing-app/settings.gradle`
- Create: `/c/Users/stand/Documents/hermes_project/sound-healing-app/gradle.properties`
- Create: `/c/Users/stand/Documents/hermes_project/sound-healing-app/app/build.gradle`
- Create: `/c/Users/stand/Documents/hermes_project/sound-healing-app/app/src/main/AndroidManifest.xml`

**Interfaces:**
- Produces: 空のGradleプロジェクト構造（後続タスクでKotlinファイルを追加）

- [ ] **Step 1: ルート設定ファイルを作成**

```
build.gradle, settings.gradle, gradle.properties を作成
```

- [ ] **Step 2: app/build.gradle を作成**

```
com.android.application 8.1.0, org.jetbrains.kotlin.android 1.9.0
Compose BOM 2023.08.00, Material3, core-ktx, lifecycle-runtime-ktx
```

- [ ] **Step 3: AndroidManifest.xml を作成**

```
namespace: com.example.soundhealing
minSdk 24, targetSdk 34
INTERNET permission（将来的使用）
```

---

## Task 2: データモデル・ドメイン層

**Files:**
- Create: `app/src/main/java/com/example/soundhealing/domain/SoundType.kt`
- Create: `app/src/main/java/com/example/soundhealing/domain/SolfeggioFrequency.kt`
- Create: `app/src/main/java/com/example/soundhealing/domain/NatureSound.kt`
- Create: `app/src/main/java/com/example/soundhealing/domain/BrainwaveType.kt`

**Interfaces:**
- Consumes: なし（新規ドメイン）
- Produces: SoundType enum, SolfeggioFrequency data class (name, freqHz, description, icon), NatureSound data class, BrainwaveType enum (name, freqRangeHz, description)

- [ ] **Step 1: SolfeggioFrequency.kt を作成**

```kotlin
data class SolfeggioFrequency(
    val id: String,
    val name: String,      // 例: "528Hz"
    val frequency: Double, // Hz
    val description: String, // 日本語説明
    val emoji: String      // アイコン絵文字
)
```

- [ ] **Step 2: NatureSound.kt を作成**

```kotlin
data class NatureSound(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String
)
```

- [ ] **Step 3: BrainwaveType.kt を作成**

```kotlin
enum class BrainwaveType(
    val name: String,
    val frequencyRangeHz: Pair<Double, Double>,
    val description: String,
    val emoji: String
) {
    DELTA("デルタ波", 0.5..4.0, "深い睡眠・回復", "🌙"),
    THETA("シータ波", 4.0..8.0, "瞑想・リラックス", "🧘"),
    ALPHA("アルファ波", 8.0..13.0, "安らぎ・集中", "🌿"),
    BETA("ベータ波", 13.0..30.0, "集中・覚醒", "⚡")
}
```

- [ ] **Step 4: SoundType.kt を作成**

```kotlin
sealed class SoundType {
    data class Solfeggio(val freq: SolfeggioFrequency) : SoundType()
    data class Nature(val sound: NatureSound) : SoundType()
    data class Brainwave(val type: BrainwaveType) : SoundType()
}
```

---

## Task 3: オーディオエンジン（核心）

**Files:**
- Create: `app/src/main/java/com/example/soundhealing/audio/AudioEngine.kt`

**Interfaces:**
- Consumes: SolfeggioFrequency, NatureSound, BrainwaveType
- Produces: play(type, volume), stop(), setVolume(), isPlaying()

- [ ] **Step 1: AudioEngine.kt を作成**

```kotlin
class AudioEngine {
    fun playSolfeggio(frequency: Double, sampleRate: Int = 44100): AudioTrack
    fun playNatureSound(type: NatureSound, sampleRate: Int = 44100): AudioTrack
    fun playBrainwave(type: BrainwaveType, sampleRate: Int = 44100): AudioTrack
    fun stopAll()
    fun setMasterVolume(volume: Float)
}
```

実装詳細：
- 正弦波: `Math.sin(2 * PI * frequency * t)` でサンプル生成
- ホワイトノイズ（雨音）: `Random.nextFloat() * 2 - 1` + ローパスフィルター
- オーシャン（波）: ホワイトノイズ + ティンペスト濾過 + エンベロープ変調
- 森林（風）: バンドパスフィルター済みノイズ
- バイノーラルビート: 左チャンネル=基準周波数、右チャンネル=基準+α波差

---

## Task 4: ViewModel（MVI状態管理）

**Files:**
- Create: `app/src/main/java/com/example/soundhealing/ui/SoundHealingViewModel.kt`

**Interfaces:**
- Consumes: AudioEngine
- Produces: StateFlow<UiState>, events: playSound(), stopSound(), setVolume(), setTimer()

- [ ] **Step 1: UiState data class + ViewModel を作成**

```kotlin
data class UiState(
    val isPlaying: Boolean = false,
    val activeSounds: List<ActiveSound> = emptyList(),
    val volume: Float = 0.7f,
    val timerSeconds: Int = 0,
    val timerRunning: Boolean = false
)

data class ActiveSound(
    val soundType: SoundType,
    val volume: Float
)

class SoundHealingViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun playSound(type: SoundType, volume: Float = 0.7f)
    fun stopSound(type: SoundType)
    fun stopAll()
    fun setVolume(volume: Float)
    fun setTimer(seconds: Int)
    fun startTimer()
    fun cancelTimer()
}
```

---

## Task 5: UI — メインスクリーン（Compose）

**Files:**
- Create: `app/src/main/java/com/example/soundhealing/ui/theme/Color.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/screen/MainScreen.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/component/SoundCard.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/component/FrequencyRow.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/component/VolumeSlider.kt`
- Create: `app/src/main/java/com/example/soundhealing/ui/component/TimerPicker.kt`
- Create: `app/src/main/java/com/example/soundhealing/MainActivity.kt`

**Interfaces:**
- Consumes: SoundHealingViewModel, SoundType, SolfeggioFrequency, NatureSound, BrainwaveType
- Produces: コンポーザブルUI（タブ切り替え: ソルフェジオ/自然音/脳波）

- [ ] **Step 1: テーマ（Color.kt + Theme.kt）を作成**

```
ダークヒーリングテーマ:
- Primary: #4DB6AC (teal)
- Secondary: #FF8A65 (coral)
- Background: #1A1A2E (deep navy)
- Surface: #16213E
- OnSurface: #E0E0E0
```

- [ ] **Step 2: SoundCard.kt を作成**

```kotlin
@Composable
fun SoundCard(
    soundType: SoundType,
    isSelected: Boolean,
    onClick: () -> Unit
)
```

- [ ] **Step 3: FrequencyRow.kt を作成**

```kotlin
@Composable
fun FrequencyRow(
    frequency: SolfeggioFrequency,
    isActive: Boolean,
    volume: Float,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit,
    onVolumeChange: (Float) -> Unit
)
```

- [ ] **Step 4: VolumeSlider.kt + TimerPicker.kt を作成**

- [ ] **Step 5: MainScreen.kt を作成**

```
BottomNavigation: ソルフェジオ | 自然音 | 脳波
各タブ内でSoundCardグリッド表示
下部に現在の再生状態＋ボリューム+タイマー
```

- [ ] **Step 6: MainActivity.kt を作成**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setContent { SoundHealingTheme { MainScreen() } }
    }
}
```

---

## Task 6: テスト

**Files:**
- Create: `app/src/test/java/com/example/soundhealing/audio/AudioEngineTest.kt`
- Create: `app/src/test/java/com/example/soundhealing/domain/SolfeggioFrequencyTest.kt`

- [ ] **Step 1: SolfeggioFrequencyTest.kt を作成**

```kotlin
@Test
fun `全ソルフェジオ周波数が定義されている`() {
    val frequencies = SolfeggioFrequencyRepository.all
    assert(frequencies.size == 10)
}

@Test
fun `528Hzの説明が含まれている`() {
    val freq = SolfeggioFrequencyRepository.getById("528")
    assertNotNull(freq)
    assertTrue(freq!!.description.isNotEmpty())
}
```

- [ ] **Step 2: AudioEngineTest.kt を作成**

```kotlin
@Test
fun `正弦波生成が正しい周波数を行う`() {
    // AudioTrack経由でサイン波生成テスト
}

@Test
fun `stopAllで全AudioTrackが解放される`() {
    val engine = AudioEngine()
    val t1 = engine.playSolfeggio(528.0)
    val t2 = engine.playSolfeggio(432.0)
    engine.stopAll()
    // t1, t2がreleaseされていることを確認
}
```

---

## Task 7: 最終統合・ビルド

- [ ] **Step 1: 全ファイルの確認**
- [ ] **Step 2: 依存関係の整合性確認**
- [ ] **Step 3: gradle.build 最終チェック**
- [ ] **Step 4: README.md 作成**

---

## ファイル一覧（最終）

```
sound-healing-app/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/soundhealing/
│           ├── MainActivity.kt
│           ├── domain/
│           │   ├── SoundType.kt
│           │   ├── SolfeggioFrequency.kt
│           │   ├── NatureSound.kt
│           │   └── BrainwaveType.kt
│           ├── audio/
│           │   └── AudioEngine.kt
│           ├── ui/
│           │   ├── theme/
│           │   │   ├── Color.kt
│           │   │   └── Theme.kt
│           │   ├── screen/
│           │   │   └── MainScreen.kt
│           │   └── component/
│           │       ├── SoundCard.kt
│           │       ├── FrequencyRow.kt
│           │       ├── VolumeSlider.kt
│           │       └── TimerPicker.kt
│           └── viewmodel/
│               └── SoundHealingViewModel.kt
└── docs/superpowers/plans/
    └── 2025-08-24-sound-healing-app.md
```
