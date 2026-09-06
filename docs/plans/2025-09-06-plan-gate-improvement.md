# Plan Gate 改善計画

**Created**: 2025-09-06
**Status**: COMPLETED

## 問題
- `refactor`, `計画`, `リファクタ` などは検出される
- `計画して`, `plan please`, `make a plan` などは検出されない

## 修正内容

### 追加パターン
```python
# 日本語
r'計画して',
r'プラン書いて',
r'計画を立てて',

# 英語
r'\bmake\s+a?\s*plan\b',
r'\bplan\s+please\b',
r'\bcreate\s+a?\s*plan\b',
r'\bhow\s+to\s*plan\b',
r'\bplan\s+for\b',
```

## テスト結果
```
OK: "refactor MainScreen.kt" -> True
OK: "計画して" -> True
OK: "plan please" -> True
OK: "make a plan" -> True
OK: "create a plan" -> True
OK: "プラン書いて" -> True
OK: "計画を立てて" -> True
OK: "how to plan" -> True
OK: "plan for refactor" -> True
OK: "fix bug" -> False
OK: "write unit test" -> False

Result: 11/11 passed
```

## 変更ファイル
- `__init__.py`: v1.1.1 → v1.1.2, 検出パターン追加
- `plugin.yaml`: version更新

## Iron Laws（更新）
```
NO CODE WITHOUT SKILL CHECK FIRST
NO FIX WITHOUT ROOT CAUSE INVESTIGATION FIRST
NO COMPLETION WITHOUT VERIFICATION EVIDENCE FIRST
NO MULTI-STEP EXECUTION WITHOUT A WRITTEN PLAN FIRST
```
