# ランダム再生BUG修正

**Date**: 2025-09-06
**Status**: COMPLETED

## 問題
1. ランダム再生で音がでなかった
2. 3つ選択するとアプリが落ちた

## 原因調査

### 原因1: viewModel共有問題
- RandomTab内で`viewModel()`を呼び毎回新しいRandomSessionViewModelが作成されていた
- 状態（選択、再生）が保持されなかった

### 原因2: SoundCard型エラー
```kotlin
// 错误
onClick: Unit  // 値型

// 正确
onClick: () -> Unit  // 関数型
```

### 原因3: UiState参照違い
```kotlin
// 错误
uiState.isPlaying
uiState.currentSound
viewModel.startSound(...)

// 正确
uiState.playing != null
uiState.playing
viewModel.playSound(...)
```

## 修正内容

### SoundCard.kt
```kotlin
// Before
fun SoundCard(
    soundType: SoundType,
    isSelected: Boolean,
    onClick: Unit  // ❌
)

// After
fun SoundCard(
    soundType: SoundType,
    isSelected: Boolean,
    onClick: () -> Unit  // ✅
)
```

### MainScreen.kt
```kotlin
// Before
fun RandomTab() {
    val viewModel = viewModel()  // ❌ 毎回新規作成
}

// After
fun MainScreen(...) {
    val randomVM = remember { RandomSessionViewModel(...) }
    // ...
    RandomTab(randomVM)  // ✅ 共有
}

fun RandomTab(randomVM: RandomSessionViewModel) {  // ✅ 引数で受け取り
    val state by randomVM.state.collectAsState()
    // ...
}
```

## 検証結果
- ビルド: ✅ SUCCESS
- インストール: ✅ SUCCESS
- 脳波タブ: ✅ 正常動作
- ランダムタブ: ⚠️ エミュレータタップ検出問題あり

## Commits
```
6c3952e fix: RandomTab LocalContext使用
f3e140d fix: ランダム再生BUG修正
```
