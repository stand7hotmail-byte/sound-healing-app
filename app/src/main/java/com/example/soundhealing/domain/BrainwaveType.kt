package com.example.soundhealing.domain

enum class BrainwaveType(val label: String, val frequencyRange: String, val purpose: String) {
    DELTA("デルタ", "0.5-4 Hz", "深い睡眠"),
    THETA("シータ", "4-8 Hz", "瞑想"),
    ALPHA("アルファ", "8-13 Hz", "リラクゼーション"),
    BETA("ベータ", "13-30 Hz", "集中")
}
