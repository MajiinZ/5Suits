package com.fivesuits.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Brand configuration, kept in one place until the reference whitelabel project is available.
 * Change these values to reskin the shared Android and iOS experience together.
 */
object WhiteLabel {
    const val appName = "5 Suits"
    const val tagline = "A little strategy. A lot of possibilities."
    const val shortTagline = "Five suits. Your next move."

    val primary = Color(0xFF195C48)
    val onPrimary = Color(0xFFFFFFFF)
    val background = Color(0xFFF6F4EE)
    val surface = Color(0xFFFFFEFA)
    val ink = Color(0xFF182B25)
    val muted = Color(0xFF6D7972)
    val accent = Color(0xFFD59B38)
    val accentSoft = Color(0xFFF5E8CD)
    val border = Color(0xFFDEE3DA)
    val table = Color(0xFF174E40)
    val tableDeep = Color(0xFF103B32)
    val tableHighlight = Color(0xFF26715A)

    val hearts = Color(0xFFC45D51)
    val clubs = Color(0xFF28785B)
    val spades = Color(0xFF4B72A4)
    val diamonds = Color(0xFF8964A7)
    val stars = Color(0xFFB98220)

    val cornerRadius = 24.dp
    val cardRadius = 10.dp
    val panelShape = RoundedCornerShape(cornerRadius)
    val cardShape = RoundedCornerShape(cardRadius)
}
