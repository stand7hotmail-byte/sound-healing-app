# 再構築計画

**Created**: 2025-09-06
**Status**: PLANNING

## 目標
- ソルフェジオタブのみ残す
- その他タブ（自然音・脳波・ランダム生成）を削除
- テスト音再生・停止ボタンを追加
- 音の再生・停止を確実に動作させる

## 現状分析问题
1. エミュレーターで音が鳴ることは確認済み
2. テスト音停止が機能しない問題あり
3. コードに`=`缺失によるコンパイルエラー多数

## 作業ステップ

### Step 1: プロジェクト復元
- `git checkout` でクリーンな状態に戻す
- 最新コミット（6b55751）をベースに構築

### Step 2: 削除する機能
- [ ] RandomTab コンポーザブル削除
- [ ] SoundTab.NATURE ケース削除
- [ ] SoundTab.BRAINWAVE ケース削除
- [ ] SoundTab.RANDOM ケース削除
- [ ] 自動タブ切り替え LaunchedEffect 削除

### Step 3: ソルフェジオタブのみ保持
- [ ] SoundTab.SOLFEGGIO ケース維持
- [ ] SolfeggioFrequency.ALL 使用

### Step 4: テスト音機能追加
- [ ] AudioEngine に startTone(frequency: Double) 追加
- [ ] AudioEngine に stop() 修正（安全な停止）
- [ ] ViewModel に testTone/stopTestTone 追加
- [ ] MainScreen にテスト音ボタン追加

### Step 5: ビルド・テスト
- [ ] gradlew assembleDebug
- [ ] エミュレーターインストール
- [ ] 音再生テスト
- [ ] 音停止テスト

## 検証基準
- [ ] ソルフェジオタブのみ表示
- [ ] 440Hz純音が聞こえる
- [ ] 停止ボタンで音が止まる
- [ ] コンパイルエラーなし
