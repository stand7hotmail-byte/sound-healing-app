# Sound Healing App リファクタリング計画

## 現状分析

### コード duplications
- MainScreen.kt: 5つのLazyVerticalGrid（tabs重複）
- AudioPlaybackService: `=`剥がれ可能性
- 各タブの音量/停止ボタン重複

### ファイルサイズ
| ファイル | 行数 | 問題 |
|---------|------|------|
| MainScreen.kt | 344 | 重複多すぎ |
| AudioPlaybackService.kt | 214 | 巨大 |
| AudioEngine.kt | 144 |  manejable |
| WaveformView.kt | 121 | 新しい |

## リファクタリング項目

### 1. MainScreen.kt 整理
- 重複tabロジックを`SoundTabContent`コンポーザブルに抽出
- `LazyVerticalGrid`パターン共通化
- インポート整理

### 2. AudioPlaybackService 分割
- 定数クラス化（Constants.kt）
- 通知ビルダー共通化
- セッション処理分離

### 3. Domain層強化
- `SoundType.displayData`補完
- `SolfeggioFrequency`にメタデータ追加

### 4. ViewModel統合
- `SoundHealingViewModel` + `RandomSessionViewModel` 連携確認

## 実行順序
1. MainScreen.kt 改善（最大効果）
2. AudioPlaybackService 定数整理
3. Domain層補完
4. ビルド検証

## 検証基準
- [ ] `gradlew assembleDebug` 成功
- [ ] eq count 検証（剥がれなし）
- [ ] エミュレータ動作確認
