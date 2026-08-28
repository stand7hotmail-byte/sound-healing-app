package com.example.soundhealing.domain

enum class BrainwaveType(
    val frequencyRangeHz: Pair<Double, Double>,
    val description: String,
    val emoji: String
) {
    DELTA(0.5..4.0, "深い睡眠、無意識の領域。身体修復と成長を促進", "💤"),
    THETA(4.0..8.0, "深いリラクゼーション、瞑想状態。創造性と直感を高める", "🧘"),
    ALPHA(8.0..13.0, "リラックスした覚醒状態。ストレス軽減と集中力向上", "🌿"),
    BETA(13.0..30.0, "覚醒した思考状態。学習と問題解決を支援", "🧠")
}
