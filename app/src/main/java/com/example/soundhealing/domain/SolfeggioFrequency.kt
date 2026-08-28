package com.example.soundhealing.domain

data class SolfeggioFrequency(
    val id: Int,
    val name: String,
    val frequency: Double,
    val description: String,
    val emoji: String
) {
    companion object {
        val ALL = listOf(
            SolfeggioFrequency(1, "迷いの除去", 396.0, "罪やGuiltを取り除き、自由な自分を取り戻す周波数", "🎵"),
            SolfeggioFrequency(2, "力の回復", 417.0, "変化を促し、過去のトラウマやネガティブなエネルギーを取り除く", "🎶"),
            SolfeggioFrequency(3, "不思議な奇跡", 528.0, "DNA修復と変容、奇跡の周波数と呼ばれる", "✨"),
            SolfeggioFrequency(4, "愛と調和", 639.0, "人間関係の調和と愛深层次を促進する", "💕"),
            SolfeggioFrequency(5, "直感と洞察", 741.0, "直感的な力を高め、-self expressionを促進する", "🔮"),
            SolfeggioFrequency(6, "内在の平安", 852.0, "霊的な秩序を取り戻し、内在の力を呼び覚ます", "🕊️")
        )
    }
}
