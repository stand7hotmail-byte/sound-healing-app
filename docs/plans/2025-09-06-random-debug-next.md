# ランダム再生デバッグ計画

**Created**: 2025-09-06
**Status**: IN PROGRESS

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
**結果**: ログ未確認（エミュレータ入力問題）

## 仮説
AudioPlaybackService側に問題あり：
1. `deserialize()` の問題
2. `ACTION_PLAY` 処理の問題
3. ForegroundServiceの問題

## 次のステップ

### Step 1: AudioPlaybackService問題特定
- [ ] `deserialize()` メソッド確認
- [ ] `ACTION_PLAY` 処理確認
- [ ] ログ追加（タップ検出无法）

### Step 2: 代替案検討
- [ ] RandomTabでAudioEngine直接呼び出しに維持
- [ ] またはAudioPlaybackService修正

## 備考
- エミュレータのInputDispatcher問題継続
- 実機でのテストが効果的
