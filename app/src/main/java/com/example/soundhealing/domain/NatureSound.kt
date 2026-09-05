package com.example.soundhealing.domain

data class NatureSound(
    val name: String,
    val description: String,
    val emoji: String
) {
    val displayData get() = DisplayData(name, description, emoji)
    
    companion object {
        val ALL = listOf(
            NatureSound("雨音", "静かな雨の音で心をもたらす", "🌧️"),
            NatureSound("海浪", "海の波の音で深いリラクゼーションを", "🌊"),
            NatureSound("森林", "森を織う風のそよぎ", "🌲"),
            NatureSound("風", "朝の森で聴こえる鳥たちのさえずり", "🍃"),
            NatureSound("溪流", "透明的流水声，净化心灵", "💧")
        )
    }
}
