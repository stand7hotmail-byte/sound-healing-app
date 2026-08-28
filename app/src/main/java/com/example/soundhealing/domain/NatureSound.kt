package com.example.soundhealing.domain

enum class NatureSound(
    val name: String,
    val description: String,
    val emoji: String
) {
    RAIN("雨音", "穏やかな雨の音で心を落ち着かせます", "🌧️"),
    OCEAN("波の音", " oceanの波の音で深いリラクゼーションを", "🌊"),
    WIND("風の音", "森を縫う風のそよぎ", "💨"),
    BIRDS("鳥の声", "朝の森で聴こえる鳥たちのさえずり", "🐦"),
    CRICKET("せみの声", "夏の夜のせみの合唱", "🦗"),
    FIRE("焚き火", "炭火がぽつぽつと響く静寂", "🔥")
}
