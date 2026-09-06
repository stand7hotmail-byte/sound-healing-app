# ランダム再生音なしBUG修正計画

**Created**: 2025-09-06
**Status**: COMPLETED

## 問題
- ランダムタブでカードを3つ選択して「再生」を押しても音がでない

## 原因
1. `startDelaySeconds` が `0..30`（最大30秒遅延）
2. ログ不足で動作確認できなかった

## 修正内容
1. `startDelaySeconds` 範囲を `0..5` に縮小
2. `startPlaying()` に詳細ログ追加

## 検証結果
- ビルド: ✅ SUCCESS
- インストール: ✅ SUCCESS
- ログ確認: `RandomSessionVM` ログ出力確認

## 次回テスト
- 実機でランダムタブ選択→再生ボタン押下
- ログで `Start playing X sessions` 確認
- 音が出ること確認
