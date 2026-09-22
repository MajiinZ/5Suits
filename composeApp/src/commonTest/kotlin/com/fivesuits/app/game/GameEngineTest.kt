package com.fivesuits.app.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameEngineTest {
    @Test
    fun deckHasTwoCopiesOfEverySuitRankAndSixDistinctJokers() {
        val deck = DeckFactory.createDeck()
        assertEquals(116, deck.size)
        assertEquals(116, deck.map { it.id }.distinct().size)
        assertEquals(6, deck.count { it.isJoker })
        for (suit in Suit.entries) for (rank in 3..13) {
            assertEquals(2, deck.count { it.suit == suit && it.rank == rank })
        }
    }

    @Test
    fun dealAndDrawAreImmutableAndEnforceTurnOrder() {
        val engine = GameEngine(Random(3))
        val initial = engine.startGame(listOf(PlayerConfig("You"), PlayerConfig("Bot", true)))
        assertEquals(0, initial.currentPlayerIndex)
        assertEquals(3, initial.wildRank)
        assertEquals(listOf(3, 3), initial.players.map { it.hand.size })
        assertFailsWith<IllegalArgumentException> { engine.discard(initial, initial.currentPlayer.hand.first().id) }
        val drawn = engine.draw(initial, DrawSource.STOCK)
        assertEquals(3, initial.currentPlayer.hand.size)
        assertEquals(4, drawn.currentPlayer.hand.size)
        assertFailsWith<IllegalArgumentException> { engine.draw(drawn, DrawSource.STOCK) }
        assertFailsWith<IllegalArgumentException> { engine.discard(drawn, -1) }
        val discarded = engine.discard(drawn, drawn.currentPlayer.hand.last().id)
        assertEquals(1, discarded.currentPlayerIndex)
        assertEquals(TurnPhase.DRAW, discarded.phase)
        assertConserved(initial)
        assertConserved(drawn)
        assertConserved(discarded)
    }

    @Test
    fun goingOutGivesEveryOtherPlayerExactlyOneFinalTurn() {
        val engine = GameEngine(Random(3))
        val outHand = listOf(Card(1, Suit.CLUBS, 7), Card(2, Suit.HEARTS, 7), Card(3, Suit.DIAMONDS, 7), Card(4, Suit.STARS, 9))
        val secondHand = listOf(Card(5, Suit.CLUBS, 4), Card(6, Suit.HEARTS, 6), Card(7, Suit.SPADES, 9))
        val thirdHand = listOf(Card(8, Suit.DIAMONDS, 4), Card(9, Suit.DIAMONDS, 8), Card(10, Suit.HEARTS, 13))
        val beforeOut = GameState(
            round = 1, dealerIndex = 2, currentPlayerIndex = 0,
            players = listOf(PlayerState("A", false, outHand), PlayerState("B", false, secondHand), PlayerState("C", false, thirdHand)),
            stock = listOf(Card(11, Suit.STARS, 11), Card(12, Suit.CLUBS, 12)),
            discardPile = emptyList(), phase = TurnPhase.DISCARD,
        )
        var state = engine.discard(beforeOut, 4)
        assertEquals(0, state.outPlayerIndex)
        assertEquals(setOf(1, 2), state.finalTurnsRemaining)
        assertEquals(1, state.currentPlayerIndex)
        state = engine.draw(state, DrawSource.STOCK)
        state = engine.discard(state, 12)
        assertEquals(setOf(2), state.finalTurnsRemaining)
        assertEquals(TurnPhase.DRAW, state.phase)
        state = engine.draw(state, DrawSource.STOCK)
        state = engine.discard(state, 11)
        assertEquals(TurnPhase.ROUND_COMPLETE, state.phase)
        assertTrue(state.finalTurnsRemaining.isEmpty())
        assertEquals(listOf(0, 19, 25), state.players.map { it.scores.single() })
        assertFailsWith<IllegalArgumentException> { engine.draw(state, DrawSource.STOCK) }

        val next = engine.nextRound(state)
        assertEquals(2, next.round)
        assertEquals(4, next.wildRank)
        assertEquals(0, next.dealerIndex)
        assertEquals(1, next.currentPlayerIndex)
        assertNull(next.outPlayerIndex)
        assertEquals(listOf(0, 19, 25), next.players.map { it.totalScore })
        assertConserved(next)
    }

    @Test
    fun eleventhRoundEndsTheGameAndCannotDealAgain() {
        val engine = GameEngine(Random(5))
        val state = GameState(
            round = 11, dealerIndex = 0, currentPlayerIndex = 1,
            players = listOf(
                PlayerState("A", false, listOf(Card(1, Suit.CLUBS, 7), Card(2, Suit.DIAMONDS, 7), Card(3, Suit.HEARTS, 7)), List(10) { 1 }),
                PlayerState("B", false, listOf(Card(4, Suit.CLUBS, 4), Card(5, Suit.HEARTS, 5)), List(10) { 2 }),
            ),
            stock = emptyList(), discardPile = emptyList(), phase = TurnPhase.DISCARD,
            outPlayerIndex = 0, finalTurnsRemaining = setOf(1),
        )
        val finished = engine.discard(state, 5)
        assertEquals(TurnPhase.GAME_COMPLETE, finished.phase)
        assertEquals(listOf(10, 24), finished.players.map { it.totalScore })
        assertEquals(listOf(11, 11), finished.players.map { it.scores.size })
        assertFailsWith<IllegalArgumentException> { engine.nextRound(finished) }
    }

    @Test
    fun recyclingPreservesTopDiscardAndEveryCard() {
        val engine = GameEngine(Random(42))
        val initial = engine.startGame(listOf(PlayerConfig("A"), PlayerConfig("B")))
        val exhausted = initial.copy(stock = emptyList(), discardPile = initial.stock + initial.discardPile)
        val drawn = engine.draw(exhausted, DrawSource.STOCK)
        assertEquals(exhausted.topDiscard, drawn.topDiscard)
        assertEquals(1, drawn.discardPile.size)
        assertNotEquals(exhausted.topDiscard, drawn.currentPlayer.hand.last())
        assertConserved(drawn)
    }

    @Test
    fun botTakesDiscardThatCompletesItsHandAndGoesOut() {
        val engine = GameEngine(Random(7))
        val state = GameState(
            round = 1, dealerIndex = 1, currentPlayerIndex = 0,
            players = listOf(
                PlayerState("Bot", true, listOf(Card(1, Suit.CLUBS, 7), Card(2, Suit.HEARTS, 7), Card(3, Suit.SPADES, 11))),
                PlayerState("You", false, listOf(Card(4, Suit.STARS, 5), Card(5, Suit.DIAMONDS, 8), Card(6, Suit.CLUBS, 12))),
            ),
            stock = listOf(Card(7, Suit.STARS, 9)), discardPile = listOf(Card(8, Suit.DIAMONDS, 7)),
        )
        val result = engine.playBotTurn(state)
        assertEquals(0, result.outPlayerIndex)
        assertEquals(3, result.topDiscard!!.id)
        assertEquals(state.stock, result.stock)
        assertEquals(setOf(1), result.finalTurnsRemaining)
    }

    @Test
    fun seededBotGameConservesCardsAcrossRounds() {
        val engine = GameEngine(Random(91))
        var state = engine.startGame(listOf(PlayerConfig("A"), PlayerConfig("B", true), PlayerConfig("C", true)))
        state = state.copy(players = state.players.map { it.copy(isBot = true) })
        repeat(100) {
            assertConserved(state)
            assertEquals(state.handSize, state.currentPlayer.hand.size)
            state = when (state.phase) {
                TurnPhase.DRAW, TurnPhase.DISCARD -> engine.playBotTurn(state)
                TurnPhase.ROUND_COMPLETE -> engine.nextRound(state)
                TurnPhase.GAME_COMPLETE -> return
            }
        }
        assertConserved(state)
    }

    @Test
    fun completeElevenRoundMatchPreservesScoresAndTurnInvariants() {
        val engine = GameEngine(Random(119))
        var state = engine.startGame(listOf(PlayerConfig("A"), PlayerConfig("B", true), PlayerConfig("C", true)))
        state = state.copy(players = state.players.map { it.copy(isBot = true) })
        var turns = 0
        var completedRounds = 0
        while (state.phase != TurnPhase.GAME_COMPLETE && turns < 4000) {
            assertConserved(state)
            assertTrue(state.players.all { it.hand.size == state.handSize })
            assertEquals(state.round + 2, state.wildRank)
            state = if (state.phase == TurnPhase.ROUND_COMPLETE) {
                completedRounds++
                assertTrue(state.players.all { it.scores.size == completedRounds })
                engine.nextRound(state)
            } else {
                turns++
                engine.playBotTurn(state)
            }
        }
        assertEquals(TurnPhase.GAME_COMPLETE, state.phase, "The seeded match should finish within the turn budget.")
        assertEquals(11, state.round)
        assertEquals(10, completedRounds)
        assertTrue(state.players.all { it.scores.size == 11 })
        assertTrue(state.players.all { it.totalScore == it.scores.sum() })
        assertTrue(state.players.all { player -> player.scores.all { it >= 0 } })
        assertTrue(state.finalTurnsRemaining.isEmpty())
        assertConserved(state)
    }

    private fun assertConserved(state: GameState) {
        val cards = state.players.flatMap { it.hand } + state.stock + state.discardPile
        assertEquals(116, cards.size)
        assertEquals(116, cards.map { it.id }.distinct().size)
    }
}
