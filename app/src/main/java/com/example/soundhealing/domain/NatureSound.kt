package com.example.soundhealing.domain

enum class NatureSound(
    val description: String,
    val emoji: String
) {
    RAIN("静かな雨の音で心をもたらします", "🌧️"),
    OCEAN("海の波の音で深いリラクゼーションを", "🌊"),
    WIND("森を織う風のそよぎ", "💨"),
    BIRDS("朝の森で聴こえる鳥たちのさえずり", "🐦"),
    CRICKET("夏の夜のせみの合唱", "🪲"),
    FIRE("篝火がぽつぽつと響く静寂", "🔥")
}
