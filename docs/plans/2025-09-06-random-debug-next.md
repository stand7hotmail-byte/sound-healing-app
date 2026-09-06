# ランダム再生デバッグ計画

**Created**: 2025-09-06
**Status**: COMPLETED

## 検証結果

### テスト1: シンプル版（AudioEngine直接）✅
```kotlin
engine.startSimple(SoundType.Solfeggio(SolfeggioFrequency.ALL[0]))
```
**結果**: 音が聞こえる

### テスト2: AudioPlaybackService経由 ❌
```kotlin
AudioPlaybackService.start(context, SoundType.Solfeggio(...))
```
**結果**: 未確認（エミュレータ入力問題）

## 分析

### deserialize() 確認 ✅
```kotlin
"OLFEGGIO" -> SolfeggioFrequency.ALL
    .firstOrNull { it.id == id.toIntOrNull() }
    ?.let { SoundType.Solfeggio(it) }
```
- 論理的には正しい
- id: Int (1-6) → toString() → toIntOrNull()

### 仮説
1. **ForegroundService問題**: NotificationRequired
2. **Intent処理問題**: EXTRA処理
3. **タイミング問題**: Service開始前にアプリ終了

## 結論

**シンプル版（AudioEngine直接）を維持する方が現実的**

理由：
1. AudioEngineは正常に動作
2. AudioPlaybackService経由は複雑（Serviceバインド、Notification等）
3. エミュレータではService開始が不安定

## 推奨アクション

### Option A: シンプル版維持（推奨）
- RandomTabをシンプル版（AudioEngine直接）に固定
- 複数セッション再生は別実装検討

### Option B: AudioPlaybackService修正
- ForegroundService設定確認
- Intent処理デバッグ
- 実機でのテスト必須

## 次回テスト（実機推奨）
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
adb logcat -c
adb shell am start -n com.example.soundhealing/.MainActivity
# ランダムタブ選択 → 再生ボタン
adb logcat -d | grep -iE "AudioEngine|AudioPlayback"
```
