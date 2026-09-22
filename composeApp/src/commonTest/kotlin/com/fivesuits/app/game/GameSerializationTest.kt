package com.fivesuits.app.game

import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameSerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun drawCheckpointRoundTripsAndCanFinishTheSameMove() {
        val engine = GameEngine(Random(37))
        val original = engine.draw(
            engine.startGame(listOf(PlayerConfig("You"), PlayerConfig("Robin", true))),
            DrawSource.DISCARD,
        )
        val restored = json.decodeFromString<GameState>(json.encodeToString(original))
        assertEquals(original, restored)
        assertEquals(TurnPhase.DISCARD, restored.phase)
        assertEquals(4, restored.currentPlayer.hand.size)
        val selectedCard = original.currentPlayer.hand.last().id
        assertEquals(engine.discard(original, selectedCard), engine.discard(restored, selectedCard))
    }

    @Test
    fun finalLapCheckpointRetainsWhoStillHasATurn() {
        val engine = GameEngine(Random(119))
        var game = engine.startGame(listOf(PlayerConfig("A"), PlayerConfig("B", true), PlayerConfig("C", true)))
        game = game.copy(players = game.players.map { it.copy(isBot = true) })
        repeat(400) {
            if (game.outPlayerIndex != null) return@repeat
            game = engine.playBotTurn(game)
        }
        assertNotNull(game.outPlayerIndex)
        val restored = json.decodeFromString<GameState>(json.encodeToString(game))
        assertEquals(game, restored)
        assertEquals(2, restored.finalTurnsRemaining.size)
        assertEquals(0, engine.analyzeHand(restored, restored.outPlayerIndex!!).score)
        assertEquals(game.players.flatMap { it.hand }.map { it.id }, restored.players.flatMap { it.hand }.map { it.id })
    }
}
