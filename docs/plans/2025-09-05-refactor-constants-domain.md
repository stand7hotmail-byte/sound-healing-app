# リファクタリング計画: 定数分離 + Domain層強化

**Created**: 2025-09-05
**Status**: COMPLETED

## タスク1: AudioPlaybackService 定数分離 ✅

### 変更内容
1. `Constants.kt` 新規作成（定数専用object）
2. AudioPlaybackService から定数定義を移動
3. インポート修正

### 結果
| ファイル | 変更前 | 変更後 | 削減 |
|---------|--------|--------|------|
| AudioPlaybackService.kt | 214行 / eq=62 | 149行 / eq=33 | **-30% / -47%** |
| Constants.kt | 新規 | 35行 / eq=11 | - |

## タスク2: Domain層 displayData 追加 ✅

### 変更内容
1. `DisplayData.kt` 新規作成
2. `SolfeggioFrequency.displayData` プロパティ追加
3. `NatureSound.displayData` プロパティ追加
4. `BrainwaveType` enum再設計（label/frequencyRange/purpose）
5. `SoundCard.kt` displayData活用

### 結果
| ファイル | 変更前 | 変更後 |
|---------|--------|--------|
| SolfeggioFrequency.kt | 20行 / eq=1 | 22行 / eq=2 |
| NatureSound.kt | 13行 / eq=0 | 19行 / eq=2 |
| BrainwaveType.kt | 13行 / eq=0 | 8行 / eq=0 |
| DisplayData.kt | 新規 | 7行 / eq=0 |
| SoundCard.kt | 62行 / eq=19 | 81行 / eq=27 |

## 検証結果
- [x] AudioPlaybackService: 214→149行（-30%）
- [x] Constants.kt: 新規作成
- [x] domain/*.kt: displayData 追加
- [x] ビルド成功
- [x] eq count 検証（剥がれなし）
- [x] エミュレータ動作確認

## 使用スキル
- `refactor-must-load-skills` ✅
- `sound-healing-app-build` ✅
- `fix-eq-stripping` ✅
- `superpowers-plan-execute` ✅
- `superpowers-verification` ✅
