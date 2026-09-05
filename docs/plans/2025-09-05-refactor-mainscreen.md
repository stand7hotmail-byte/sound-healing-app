# リファクタリング計画: MainScreen.kt 整理

**Created**: 2025-09-05
**Status**: COMPLETED
**REQUIRED SUB-SKILL**: Use superpowers:subagent-driven-development

## 現状分析
- MainScreen.kt: 344行, eq=161
- 3つのタブ(SOLFEGGIO/NATURE/BRAINWAVE)が重複コード
- 各タブにLazyVerticalGrid + 波形 + 音量 + 停止ボタンが複製

## タスク

### Step 1: 共通化設計 ✅
- [x] `SoundTabContent` 汎用コンポーザブル設計
- [x] 型パラメータ除去（Kotlin制限対応）
- [x] `SoundHealingViewModel.UiState` 参照修正

### Step 2: 実装 ✅
- [x] MainScreen.kt 書き直し（Pythonスクリプト経由）
- [x] 型参照修正
- [x] インポート整理

### Step 3: ビルド検証 ✅
- [x] `gradlew assembleDebug` 成功
- [x] eq count 検証（剥がれなし確認: eq=107）

### Step 4: エミュレータテスト ✅
- [x] APKインストール
- [x] 4タブ表示確認
- [x] ソルフェジオタブ動作確認

### Step 5: コミット ✅
- [x] git add -A
- [x] commit + push

## 検証結果
- [x] `eq=107`（剥がれなし）
- [x] `lines=266`（-23%削減）
- [x] `SoundTabContent` 関数存在
- [x] `SoundHealingViewModel.UiState` 参照正しい
- [x] ビルド成功
- [x] エミュレータ動作確認

## 使用スキル
- `refactor-must-load-skills` ✅
- `sound-healing-app-build` ✅
- `fix-eq-stripping` ✅
- `superpowers-plan-execute` ✅
- `superpowers-verification` ✅

## 改善効果
| 項目 | 変更前 | 変更後 | 改善率 |
|------|--------|--------|--------|
| 行数 | 344 | 266 | -23% |
| eqカウント | 161 | 107 | -34% |
| 重複コード | 3つのタブ | 1つの汎用関数 | 除去 |
