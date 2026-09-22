package com.fivesuits.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fivesuits.app.game.Suit
import com.fivesuits.app.ui.theme.WhiteLabel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun suitColor(suit: Suit?): Color = when (suit) {
    Suit.CLUBS -> WhiteLabel.clubs
    Suit.DIAMONDS -> WhiteLabel.diamonds
    Suit.HEARTS -> WhiteLabel.hearts
    Suit.SPADES -> WhiteLabel.spades
    Suit.STARS -> WhiteLabel.stars
    null -> WhiteLabel.primary
}

/** Canvas paths avoid platform-specific font glyphs, so every suit looks the same on iOS. */
@Composable
fun SuitMark(suit: Suit?, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Canvas(modifier.size(size)) {
        withTransform({ scale(this@Canvas.size.width / 100f, this@Canvas.size.height / 100f, pivot = Offset.Zero) }) {
            drawSuit(suit, suitColor(suit))
        }
    }
}

private fun DrawScope.drawSuit(suit: Suit?, color: Color) {
    when (suit) {
        Suit.HEARTS -> drawPath(Path().apply {
            moveTo(50f, 89f)
            cubicTo(42f, 78f, 8f, 56f, 8f, 33f)
            cubicTo(8f, 9f, 38f, 5f, 50f, 26f)
            cubicTo(62f, 5f, 92f, 9f, 92f, 33f)
            cubicTo(92f, 56f, 58f, 78f, 50f, 89f)
            close()
        }, color)
        Suit.DIAMONDS -> drawPath(Path().apply {
            moveTo(50f, 5f)
            lineTo(90f, 50f)
            lineTo(50f, 95f)
            lineTo(10f, 50f)
            close()
        }, color)
        Suit.CLUBS -> {
            drawCircle(color, radius = 23f, center = Offset(50f, 28f))
            drawCircle(color, radius = 23f, center = Offset(27f, 57f))
            drawCircle(color, radius = 23f, center = Offset(73f, 57f))
            drawPath(Path().apply {
                moveTo(45f, 50f)
                cubicTo(46f, 68f, 45f, 82f, 32f, 94f)
                lineTo(68f, 94f)
                cubicTo(55f, 82f, 54f, 68f, 55f, 50f)
                close()
            }, color)
        }
        Suit.SPADES -> {
            drawPath(Path().apply {
                moveTo(50f, 5f)
                cubicTo(40f, 18f, 8f, 42f, 8f, 61f)
                cubicTo(8f, 85f, 40f, 88f, 50f, 66f)
                cubicTo(60f, 88f, 92f, 85f, 92f, 61f)
                cubicTo(92f, 42f, 60f, 18f, 50f, 5f)
                close()
            }, color)
            drawPath(Path().apply {
                moveTo(46f, 54f)
                cubicTo(46f, 71f, 44f, 84f, 32f, 95f)
                lineTo(68f, 95f)
                cubicTo(56f, 84f, 54f, 71f, 54f, 54f)
                close()
            }, color)
        }
        Suit.STARS -> drawPath(starPath(50f, 50f, 47f, 21f), color)
        null -> {
            drawCircle(color.copy(alpha = 0.12f), radius = 46f, center = Offset(50f, 50f))
            drawCircle(color, radius = 42f, center = Offset(50f, 50f), style = Stroke(2.5f))
            drawPath(starPath(50f, 50f, 31f, 13f), color)
            drawCircle(WhiteLabel.accent, radius = 5f, center = Offset(83f, 16f))
        }
    }
}

private fun starPath(cx: Float, cy: Float, outerRadius: Float, innerRadius: Float) = Path().apply {
    for (point in 0 until 10) {
        val angle = -PI / 2 + point * PI / 5
        val radius = if (point % 2 == 0) outerRadius else innerRadius
        val x = cx + (cos(angle) * radius).toFloat()
        val y = cy + (sin(angle) * radius).toFloat()
        if (point == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier.size(44.dp)
            .background(WhiteLabel.primary, RoundedCornerShape(14.dp))
            .semantics { contentDescription = "5 Suits logo" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(31.dp)) {
            withTransform({ scale(size.width / 100f, size.height / 100f, pivot = Offset.Zero) }) {
                withTransform({ rotate(-17f, Offset(50f, 50f)) }) {
                    drawRoundRect(Color.White.copy(alpha = 0.4f), Offset(17f, 13f), Size(52f, 71f), CornerRadius(7f))
                }
                drawRoundRect(Color(0xFFFFFCED), Offset(31f, 15f), Size(52f, 71f), CornerRadius(7f))
                drawPath(starPath(57f, 49f, 19f, 8.5f), WhiteLabel.primary)
            }
        }
    }
}
