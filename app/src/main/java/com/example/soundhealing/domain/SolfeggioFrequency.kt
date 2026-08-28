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
            SolfeggioFrequency(
                1,
                "174Hz 疼痛軽減",
                174.0,
                "身体的痛みの軽減と身体の緊張を和らげる効果があります。基礎周波数として体を癒す力を持つとされています。",
                "🎵"
            ),
            SolfeggioFrequency(
                2,
                "285Hz 組織修復",
                285.0,
                "細胞レベルでの修復を促進し、傷んだ組織を回復させる働きがあるとされています。エネルギー場を整えます。",
                "💚"
            ),
            SolfeggioFrequency(
                3,
                "396Hz 解放と浄化",
                396.0,
                "潜在意識深处的な恐怖や罪悪感を解放し、感情の浄化を促す周波数です。",
                "🌊"
            ),
            SolfeggioFrequency(
                4,
                "417Hz 変化を促す",
                417.0,
                "ネガティブなエネルギーパターンを_break_し、新しい変化と成長への道を開く力を秘めています。",
                "🌀"
            ),
            SolfeggioFrequency(
                5,
                "528Hz 愛と奇跡",
                528.0,
                "「愛の周波数」とも呼ばれ、DNAの修復や細胞の再生を促すとされる神秘的な周波数です。",
                "✨"
            ),
            SolfeggioFrequency(
                6,
                "639Hz 調和と関係",
                639.0,
                "人間関係の調和を高め、理解と寛容心を深める効果があります。心と心のつながりを強化します。",
                "💫"
            ),
            SolfeggioFrequency(
                7,
                "741Hz 直感と瞑想",
                741.0,
                "霊的な進化を促進し、問題解決能力を高めるとされています。瞑想時の深い集中を助けます。",
                "🧘"
            ),
            SolfeggioFrequency(
                8,
                "852Hz 霊的覚醒",
                852.0,
                "直観力を高め、精神的な覺醒を促す効果があります。自我を越えた存在に接続する力を秘めています。",
                "🔮"
            )
        )
    }
}
