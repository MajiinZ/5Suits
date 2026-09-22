package com.fivesuits.app.game

import kotlinx.serialization.Serializable

@Serializable
enum class Suit(val symbol: String, val label: String) {
    CLUBS("♣", "Clubs"),
    DIAMONDS("♦", "Diamonds"),
    HEARTS("♥", "Hearts"),
    SPADES("♠", "Spades"),
    STARS("★", "Stars"),
}

/** IDs distinguish the two copies of each card and all six jokers. */
@Serializable
data class Card(val id: Int, val suit: Suit?, val rank: Int) {
    val isJoker: Boolean get() = suit == null
    val rankLabel: String get() = when {
        isJoker -> "J"
        rank == 11 -> "J"
        rank == 12 -> "Q"
        rank == 13 -> "K"
        else -> rank.toString()
    }

    fun isWild(wildRank: Int): Boolean = isJoker || rank == wildRank

    fun points(wildRank: Int): Int = when {
        isJoker -> 50
        rank == wildRank -> 20
        else -> rank
    }
}

@Serializable
data class PlayerConfig(val name: String, val isBot: Boolean = false)

@Serializable
data class PlayerState(
    val name: String,
    val isBot: Boolean,
    val hand: List<Card> = emptyList(),
    val scores: List<Int> = emptyList(),
) {
    val totalScore: Int get() = scores.sum()
}

@Serializable
enum class TurnPhase { DRAW, DISCARD, ROUND_COMPLETE, GAME_COMPLETE }

enum class DrawSource { STOCK, DISCARD }

@Serializable
data class GameState(
    val round: Int,
    val dealerIndex: Int,
    val currentPlayerIndex: Int,
    val players: List<PlayerState>,
    val stock: List<Card>,
    val discardPile: List<Card>,
    val phase: TurnPhase = TurnPhase.DRAW,
    val outPlayerIndex: Int? = null,
    val finalTurnsRemaining: Set<Int> = emptySet(),
    val lastAction: String = "",
) {
    val handSize: Int get() = round + 2
    val wildRank: Int get() = handSize
    val currentPlayer: PlayerState get() = players[currentPlayerIndex]
    val topDiscard: Card? get() = discardPile.lastOrNull()
    val isFinalLap: Boolean get() = outPlayerIndex != null
}

enum class MeldType { BOOK, RUN }

data class Meld(val cards: List<Card>, val type: MeldType)

data class HandAnalysis(
    val melds: List<Meld>,
    val deadwood: List<Card>,
    val score: Int,
) {
    val canGoOut: Boolean get() = deadwood.isEmpty()
}

object DeckFactory {
    fun createDeck(): List<Card> = buildList {
        repeat(2) {
            for (suit in Suit.entries) {
                for (rank in 3..13) add(Card(size, suit, rank))
            }
        }
        repeat(6) { add(Card(size, null, 0)) }
    }
}
