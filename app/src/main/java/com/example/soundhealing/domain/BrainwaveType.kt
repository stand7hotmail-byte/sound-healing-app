package com.example.soundhealing.domain

enum class BrainwaveType(
    val name: String,
    val frequencyRangeHz: Pair<Double, Double>,
    val description: String,
    val emoji: String
) {
    DELTA(
        name = "デルタ波 (Δ)",
        frequencyRangeHz = Pair(0.5, 4.0),
        description = "深い瞑想と無夢の睡眠をもたらす波。身体の回復と修復を促進し、最も深いリラクゼーション状態を誘導します。",
        emoji = "😴"
    ),
    THETA(
        name = "シータ波 (Θ)",
        frequencyRangeHz = Pair(4.0, 8.0),
        description = "浅い瞑想と睡眠の境界で発生する波。直観力が高まり、創造性と深いリラックスが同時に得られます。",
        emoji = "🧘"
    ),
    ALPHA(
        name = "アルファ波 (α)",
        frequencyRangeHz = Pair(8.0, 13.0),
        description = "リラックスした覚醒状態の波。ストレス軽減と心の平穏をもたらし、集中力と幸福感を高めます。",
        emoji = "☮️"
    ),
    BETA(
        name = "ベータ波 (β)",
        frequencyRangeHz = Pair(13.0, 30.0),
        description = "活発な思考と集中を司る波。問題解決能力を高め、外的な刺激への対応力を強化します。",
        emoji = "🧠"
    )
}
