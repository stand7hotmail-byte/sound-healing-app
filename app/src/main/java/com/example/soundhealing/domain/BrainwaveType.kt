package com.example.soundhealing.domain

enum class BrainwaveType(
    val frequencyLowHz: Double,
    val frequencyHighHz: Double,
    val description: String,
    val emoji: String
) {
    DELTA(0.5, 4.0, "æ·±ã„ç çœ ã€ç„¡æ„è­˜ã®é ˜åŸŸã€‚èº«ä½“ä¿®å¾©ã¨æˆé•·ã‚’ä¿ƒèŒ¬", "ğŸ’¤"),
    THETA(4.0, 8.0, "æxb7±ã„ãƒªãƒã‚¯ã‚¼ãƒ¼ã‚·ãƒ§ãƒ³ã€æx9e»æx83³çx8axb6æx80x81ãx80x82åx88x9béx80x9fæx80xa7ãx81xa8çx9b´æx84x9fãx82x92éxabx98ãx81x99ãx82x8b", "ğŸŸˆ"),
    ALPHA(8.0, 13.0, "ãx83xaaãx82­ãx82¹ãx83x88ãx81x97ãx81x9fèx90x8béx86x92çx8axb6æx80x81ãx80x82ãx82xb9ãx83x88xe3x83xacãx82x99èxbb»èxbbx89ãx81xa8éx9bx86ãx81x97ãx82x8bãx83xaaãx83x93ãx83xabåx90x91xe4xb8x8a", "ğŸŒ¿"),
    BETA(13.0, 30.0, "èx90x8béx86x92xe3x81x97xe3x81x9fæx80x9dèx80x83xe7x8axb6xe6x80x81xe3x80x82åxadxxe5xadxa6xe3x81x98xe3x81xa8xe5x95x8fxe9x80x9fxe8xa7xa3xe3x82x92xe6x8inaexx97xe3x81x99", "ğŸš ")
}
