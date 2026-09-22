package com.fivesuits.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fivesuits.app.game.Card
import com.fivesuits.app.ui.theme.WhiteLabel

@Composable
fun PlayingCard(
    card: Card,
    wildRank: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val isWild = card.isWild(wildRank)
    val description = when {
        card.isJoker -> "Joker, wild card"
        else -> "${card.rankLabel} of ${card.suit!!.label}${if (isWild) ", wild card" else ""}"
    }
    val cardModifier = modifier
        .size(if (compact) 56.dp else 76.dp, if (compact) 84.dp else 110.dp)
        .clip(WhiteLabel.cardShape)
        .then(if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
        .semantics(mergeDescendants = true) {
            contentDescription = description
            this.selected = selected
        }

    Surface(
        modifier = cardModifier,
        shape = WhiteLabel.cardShape,
        color = if (selected) Color(0xFFF2FAF3) else WhiteLabel.surface,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) WhiteLabel.primary else if (isWild) WhiteLabel.accent.copy(alpha = 0.6f) else Color(0xFFDDE2D9),
        ),
        shadowElevation = if (selected) 5.dp else 1.dp,
    ) {
        Box(Modifier.clearAndSetSemantics {}) {
            CardCorner(card, compact, Modifier.align(Alignment.TopStart).padding(start = 6.dp, top = 4.dp))
            SuitMark(card.suit, Modifier.align(Alignment.Center), size = if (compact) 26.dp else 34.dp)
            if (isWild) {
                Text(
                    "WILD",
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp)
                        .background(WhiteLabel.accentSoft, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    color = Color(0xFF775116),
                    fontSize = if (compact) 7.sp else 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    lineHeight = 10.sp,
                )
            } else {
                CardCorner(card, compact, Modifier.align(Alignment.BottomEnd).padding(end = 6.dp, bottom = 4.dp).rotate(180f))
            }
        }
    }
}

@Composable
private fun CardCorner(card: Card, compact: Boolean, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (card.isJoker) "J" else card.rankLabel,
            color = suitColor(card.suit),
            fontSize = if (compact) 13.sp else 17.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = if (compact) 14.sp else 18.sp,
        )
        SuitMark(card.suit, size = if (compact) 9.dp else 11.dp)
    }
}

@Composable
fun CardBack(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val backModifier = modifier.size(76.dp, 110.dp)
        .clip(WhiteLabel.cardShape)
        .then(if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
        .semantics(mergeDescendants = true) { contentDescription = "Face-down draw pile" }
        .background(WhiteLabel.surface)
        .padding(4.dp)
        .clip(RoundedCornerShape(7.dp))
        .background(Brush.linearGradient(listOf(WhiteLabel.primary, WhiteLabel.tableDeep)))
        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(7.dp))

    Box(backModifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val step = 12.dp.toPx()
            var x = -size.height
            while (x < size.width + size.height) {
                drawLine(Color.White.copy(alpha = 0.07f), Offset(x, 0f), Offset(x + size.height, size.height), 1.dp.toPx())
                drawLine(Color.White.copy(alpha = 0.07f), Offset(x, size.height), Offset(x + size.height, 0f), 1.dp.toPx())
                x += step
            }
        }
        Text(
            "5",
            modifier = Modifier.clearAndSetSemantics {}
                .size(38.dp)
                .border(1.dp, WhiteLabel.accent.copy(alpha = 0.7f), RoundedCornerShape(50))
                .padding(top = 2.dp),
            color = Color(0xFFFFF4D7),
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
