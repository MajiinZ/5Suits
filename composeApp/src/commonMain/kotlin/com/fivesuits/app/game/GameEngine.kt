package com.fivesuits.app.game

import kotlin.random.Random

/** Platform-independent game transitions. Lists are replaced, never mutated in a saved state. */
class GameEngine(private val random: Random = Random.Default) {
    fun startGame(configs: List<PlayerConfig>): GameState {
        require(configs.size in 2..4) { "Choose two to four players." }
        require(configs.any { !it.isBot }) { "At least one player must be human." }
        require(configs.all { it.name.isNotBlank() }) { "Every player needs a name." }
        return dealRound(
            round = 1,
            dealerIndex = configs.lastIndex,
            players = configs.map { PlayerState(it.name.trim(), it.isBot) },
        )
    }

    fun analyzeHand(state: GameState, playerIndex: Int = state.currentPlayerIndex): HandAnalysis =
        MeldSolver.analyze(state.players[playerIndex].hand, state.wildRank)

    fun draw(state: GameState, source: DrawSource): GameState {
        require(state.phase == TurnPhase.DRAW) { "Draw only at the start of your turn." }
        var stock = state.stock
        var discardPile = state.discardPile
        val card = when (source) {
            DrawSource.STOCK -> {
                if (stock.isEmpty()) {
                    require(discardPile.size > 1) { "No cards are available to draw." }
                    stock = discardPile.dropLast(1).shuffled(random)
                    discardPile = discardPile.takeLast(1)
                }
                stock.last().also { stock = stock.dropLast(1) }
            }
            DrawSource.DISCARD -> {
                require(discardPile.isNotEmpty()) { "The discard pile is empty." }
                discardPile.last().also { discardPile = discardPile.dropLast(1) }
            }
        }
        val player = state.currentPlayer
        return state.copy(
            players = replacePlayer(state, player.copy(hand = player.hand + card)),
            stock = stock,
            discardPile = discardPile,
            phase = TurnPhase.DISCARD,
            lastAction = "${player.name} drew from ${if (source == DrawSource.STOCK) "the deck" else "the discard pile"}.",
        )
    }

    fun discard(state: GameState, cardId: Int): GameState {
        require(state.phase == TurnPhase.DISCARD) { "Draw a card before discarding." }
        val player = state.currentPlayer
        val card = requireNotNull(player.hand.find { it.id == cardId }) { "Choose a card from your hand." }
        val hand = player.hand.filterNot { it.id == cardId }
        var next = state.copy(
            players = replacePlayer(state, player.copy(hand = hand)),
            discardPile = state.discardPile + card,
            phase = TurnPhase.DRAW,
            lastAction = "${player.name} discarded ${card.description()}.",
        )
        if (state.outPlayerIndex == null && MeldSolver.analyze(hand, state.wildRank).canGoOut) {
            next = next.copy(
                outPlayerIndex = state.currentPlayerIndex,
                finalTurnsRemaining = state.players.indices.filter { it != state.currentPlayerIndex }.toSet(),
                lastAction = "${player.name} went out. Everyone else gets one final turn.",
            )
        } else if (state.outPlayerIndex != null) {
            next = next.copy(finalTurnsRemaining = state.finalTurnsRemaining - state.currentPlayerIndex)
            if (next.finalTurnsRemaining.isEmpty()) return finishRound(next)
        }
        return next.copy(currentPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size)
    }

    /** Completes one bot's turn. Chooses the discard with the lowest exact remaining score. */
    fun playBotTurn(state: GameState): GameState {
        require(state.currentPlayer.isBot) { "This player is controlled by a person." }
        require(state.phase == TurnPhase.DRAW || state.phase == TurnPhase.DISCARD) { "The round is over." }
        val drawn = if (state.phase == TurnPhase.DRAW) {
            val top = state.topDiscard
            val currentScore = analyzeHand(state).score
            val takeDiscard = top != null &&
                bestDiscard(state.currentPlayer.hand + top, state.wildRank).score < currentScore
            draw(state, if (takeDiscard) DrawSource.DISCARD else DrawSource.STOCK)
        } else state
        return discard(drawn, bestDiscard(drawn.currentPlayer.hand, drawn.wildRank).card.id)
    }

    fun nextRound(state: GameState): GameState {
        require(state.phase == TurnPhase.ROUND_COMPLETE && state.round < 11) { "There is no next round yet." }
        return dealRound(state.round + 1, (state.dealerIndex + 1) % state.players.size, state.players)
    }

    private fun dealRound(round: Int, dealerIndex: Int, players: List<PlayerState>): GameState {
        val deck = DeckFactory.createDeck().shuffled(random).toMutableList()
        val firstPlayer = (dealerIndex + 1) % players.size
        val hands = List(players.size) { mutableListOf<Card>() }
        repeat(round + 2) {
            repeat(players.size) { offset ->
                hands[(firstPlayer + offset) % players.size].add(deck.removeAt(deck.lastIndex))
            }
        }
        val topDiscard = deck.removeAt(deck.lastIndex)
        return GameState(
            round = round,
            dealerIndex = dealerIndex,
            currentPlayerIndex = firstPlayer,
            players = players.mapIndexed { index, player -> player.copy(hand = hands[index].toList()) },
            stock = deck.toList(),
            discardPile = listOf(topDiscard),
            lastAction = "Round $round. ${players[firstPlayer].name} draws first.",
        )
    }

    private fun finishRound(state: GameState): GameState = state.copy(
        players = state.players.map { player ->
            player.copy(scores = player.scores + MeldSolver.analyze(player.hand, state.wildRank).score)
        },
        phase = if (state.round == 11) TurnPhase.GAME_COMPLETE else TurnPhase.ROUND_COMPLETE,
        lastAction = if (state.round == 11) "Game complete. The lowest total score wins." else "Round ${state.round} complete. Scores are in.",
    )

    private fun replacePlayer(state: GameState, player: PlayerState): List<PlayerState> =
        state.players.mapIndexed { index, current -> if (index == state.currentPlayerIndex) player else current }

    private data class DiscardChoice(val card: Card, val score: Int)

    private fun bestDiscard(hand: List<Card>, wildRank: Int): DiscardChoice = hand.map { card ->
        DiscardChoice(card, MeldSolver.analyze(hand.filterNot { it.id == card.id }, wildRank).score)
    }.minWith(compareBy<DiscardChoice> { it.score }.thenByDescending { it.card.points(wildRank) }.thenBy { it.card.id })

    private fun Card.description(): String = if (isJoker) "a joker" else "$rankLabel${suit!!.symbol}"
}
