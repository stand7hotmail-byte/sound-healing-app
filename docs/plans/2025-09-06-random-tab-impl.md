# ランダムタブ実装計画

**Created**: 2025-09-06
**Status**: IN PROGRESS

## 問題
- `RandomTab()` 実装が未完了（UI内容なし）
- `generateSessions()` 呼び出しなし

## 修正内容

### 1. RandomTabUI実装
```kotlin
@Composable
fun RandomTab() {
    val context = LocalContext.current
    val randomVM = remember { RandomSessionViewModel(context.applicationContext as android.app.Application) }
    val state by randomVM.state.collectAsState()
    
    // セッション生成
    LaunchedEffect(Unit) {
        randomVM.generateSessions()
    }
    
    // UI実装
    // - 6カード表示
    // - 選択状態表示
    // - 再生/停止ボタン
    // - 音量スライダー
}
```

### 2. 必要なインポート
- `LaunchedEffect`
- `LazyVerticalGrid`
- `GridCells`
- `items`

## 検証基準
- [ ] 6カード表示
- [ ] 選択可能
- [ ] 再生ボタンで音が出る
- [ ] 複数同時再生
