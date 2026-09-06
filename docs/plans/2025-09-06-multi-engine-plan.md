# 複数同時再生実装計画

**Created**: 2025-09-06
**Status**: COMPLETED

## 実装内容

### 1. AudioEngine修正
- `startSimple()` から `stop()` 呼び出しを削除
- 複数Engine同時動作を許可

### 2. RandomSessionViewModel更新
```kotlin
private val engines = mutableListOf<AudioEngine>()

fun startPlaying() {
    // 各セッションに別Engineを割り当て
    sessions.forEachIndexed { i, session ->
        engines[i].start(session)
    }
}

fun stopPlaying() {
    engines.forEach { it.stop() }
}
```

### 3. RandomTab更新
- ViewModel使用
- 6カード表示
- 選択/再生/停止/音量制御

## 検証基準
- [ ] 6周波数カード表示
- [ ] 複数選択可能
- [ ] 同時再生動作確認
- [ ] 全停止動作確認

## 次回テスト（実機推奨）
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
# ランダムタブ → 複数カード選択 → 再生
```
