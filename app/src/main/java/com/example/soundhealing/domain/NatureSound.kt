package com.example.soundhealing.domain

data class NatureSound(
    val id: Int,
    val name: String,
    val description: String,
    val emoji: String
) {
    companion object {
        val ALL = listOf(
            NatureSound(
                id = 1,
                name = "雨音",
                description = "穏やかな雨の音。自然と一体化した静かな癒しを提供し、深いリラックス状態へ導きます。",
                emoji = "🌧️"
            ),
            NatureSound(
                id = 2,
                name = "海浪",
                description = "波が打ち寄せる音色。永遠のリズムが心を落ち着かせ、不安を洗い流すように癒します。",
                emoji = "🌊"
            ),
            NatureSound(
                id = 3,
                name = "森林",
                description = "緑豊かな森のざわめき。鳥の声と木の葉の揺れる音が、自然の安心感に包み込みます。",
                emoji = "🌲"
            ),
            NatureSound(
                id = 4,
                name = "風",
                description = "そよ風が運ぶ涼しげな音色。草原を渡る風が心を軽くし、自由な感覚を与えてくれます。",
                emoji = "🍃"
            ),
            NatureSound(
                id = 5,
                name = "溪流",
                description = "小川が岩をすり抜けるせせらぎ。清らかな水の音が雑念を払い、心をクリアにします。",
                emoji = "💧"
            )
        )
    }
}
