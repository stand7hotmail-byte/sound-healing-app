# リファクタリング計画: 定数分離 + Domain層強化

**Created**: 2025-09-05
**Status**: IN PROGRESS
**REQUIRED SUB-SKILL**: Use superpowers:subagent-driven-development

## タスク1: AudioPlaybackService 定数分離

### 現状
- AudioPlaybackService.kt: 214行, eq=62
- 10個の定数が Service 内に定義

### 変更内容
1. `Constants.kt` 新規作成（定数専用クラス）
2. AudioPlaybackService から定数定義を移動
3. インポート修正

### 分離対象定数
```kotlin
const val TAG = "AudioPlayback"
const val CHANNEL_ID = "sound_healing_playback"
const val NOTIFICATION_ID = 1001
const val SERVICE_TYPE = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
const val ACTION_PLAY = "com.example.soundhealing.action.PLAY"
const val ACTION_STOP = "com.example.soundhealing.action.STOP"
const val ACTION_UPDATE_VOLUME = "com.example.soundhealing.action.UPDATE_VOLUME"
const val EXTRA_KIND = "extra_kind"
const val EXTRA_ID = "extra_id"
const val EXTRA_VOLUME = "extra_volume"
```

## タスク2: Domain層 displayData 追加

### 現状
- SolfeggioFrequency: name, description, emoji あり
- FrequencyRow: 別ファイルで同機能実装

### 変更内容
1. `SolfeggioFrequency.displayData` プロパティ追加
2. `NatureSound.displayData` プロパティ追加
3. `BrainwaveType.displayData` プロパティ追加
4. `FrequencyRow.kt` 統合検討（必要なら削除）

## 検証基準
- [ ] AudioPlaybackService: 214→~150行（-30%）
- [ ] Constants.kt: 新規作成
- [ ] domain/*.kt: displayData 追加
- [ ] ビルド成功
- [ ] eq count 検証
