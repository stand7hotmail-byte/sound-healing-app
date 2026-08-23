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
                id = 1,
                name = "174Hz - 疼痛軽減",
                frequency = 174.0,
                description = "身体的痛みの軽減と身体の緊張を和らげる効果があります。基礎周波数として体を癒す力を持つとされています。",
                emoji = "🎵"
            ),
            SolfeggioFrequency(
                id = 2,
                name = "285Hz - 組織修復",
                frequency = 285.0,
                description = "細胞レベルでの修復を促進し、傷んだ組織を回復させる働きがあるとされています。エネルギー場を整えます。",
                emoji = "💚"
            ),
            SolfeggioFrequency(
                id = 3,
                name = "396Hz - 解放と浄化",
                frequency = 396.0,
                description = "潜在意識深处的な恐怖や罪悪感を解放し、感情の浄化を促す周波数です。灵魂の重荷を軽やかにします。",
                emoji = "🌊"
            ),
            SolfeggioFrequency(
                id = 4,
                name = "417Hz - 変化を促す",
                frequency = 417.0,
                description = "ネガティブなエネルギーパターンを_break_し、新しい変化と成長への道を開く力を秘めています。",
                emoji = "🌀"
            ),
            SolfeggioFrequency(
                id = 5,
                name = "528Hz - 愛と奇跡",
                frequency = 528.0,
                description = "「愛の周波数」とも呼ばれ、DNAの修復や細胞の再生を促すとされる神秘的な周波数です。",
                emoji = "✨"
            ),
            SolfeggioFrequency(
                id = 6,
                name = "639Hz - 調和と関係",
                frequency = 639.0,
                description = "人間関係の調和を高め、理解と寛容心を深める効果があります。心と心のつながりを強化します。",
                emoji = "💫"
            ),
            SolfeggioFrequency(
                id = 7,
                name = "741Hz - 浄化と解毒",
                frequency = 741.0,
                description = "身体と心の毒を浄化し、思考パターンを整理整頓します。細胞レベルでのデトックスを促進します。",
                emoji = "🌿"
            ),
            SolfeggioFrequency(
                id = 8,
                name = "852Hz - 直観と霊性",
                frequency = 852.0,
                description = "直観力を高め、精神的な觉醒を促す周波数です。第六感を目覚めさせ、内的な真実を見極めます。",
                emoji = "🔮"
            ),
            SolfeggioFrequency(
                id = 9,
                name = "963Hz - 神性への接続",
                frequency = 963.0,
                description = "ピラミッドや聖なる空間で使われるとされる高周波数。宇宙意識とのつながりを深め、完全な調和へ導きます。",
                emoji = "🕉️"
            )
        )
    }
}
