package com.fivesuits.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fivesuits.app.data.domain.GameRepository
import com.fivesuits.app.navigation.Screen
import com.fivesuits.app.game.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class HandOrder { RANK, SUIT }

data class GameUiState(
    val screen: Screen = Screen.HOME,
    val game: GameState? = null,
    val selectedCardId: Int? = null,
    val analysis: HandAnalysis? = null,
    val busy: Boolean = false,
    val handRevealed: Boolean = false,
    val handOrder: HandOrder = HandOrder.RANK,
    val darkTheme: Boolean = false,
    val message: String? = null,
)

/** Single state owner. Expensive meld searches run away from the UI thread. */
class GameViewModel(
    private val repository: GameRepository,
    private val engine: GameEngine = GameEngine(),
) : ViewModel() {
    var state by mutableStateOf(GameUiState())
        private set

    init {
        val saved = runCatching { repository.load() }
        state = state.copy(
            game = saved.getOrNull(),
            darkTheme = runCatching { repository.darkTheme() }.getOrDefault(false),
            message = saved.exceptionOrNull()?.let { "Your saved game could not be opened. You can start a new table." },
        )
    }

    fun navigate(screen: Screen) {
        if (state.busy) return
        state = state.copy(screen = screen, selectedCardId = null, handRevealed = false)
        if (screen == Screen.TABLE) refreshAnalysis()
    }

    fun startGame(names: List<String>, bots: Boolean) = transition {
        engine.startGame(names.mapIndexed { index, name -> PlayerConfig(name.trim().ifEmpty { "Player ${index + 1}" }, bots && index > 0) })
    }

    fun revealHand() { state = state.copy(handRevealed = true) }
    fun selectCard(id: Int) {
        if (state.busy) return
        state = state.copy(selectedCardId = if (state.selectedCardId == id) null else id)
    }
    fun toggleOrder() { state = state.copy(handOrder = if (state.handOrder == HandOrder.RANK) HandOrder.SUIT else HandOrder.RANK) }
    fun dismissMessage() { state = state.copy(message = null) }
    fun setDarkTheme(enabled: Boolean) {
        state = state.copy(darkTheme = enabled)
        runCatching { repository.setDarkTheme(enabled) }.onFailure { state = state.copy(message = "Theme changed, but it could not be saved.") }
    }

    fun draw(source: DrawSource) = transition { engine.draw(requireNotNull(state.game), source) }
    fun discard() {
        val id = state.selectedCardId ?: return
        transition { engine.discard(requireNotNull(state.game), id) }
    }
    fun nextRound() = transition { engine.nextRound(requireNotNull(state.game)) }

    suspend fun playBotIfNeeded() {
        val before = state.game ?: return
        if (state.screen != Screen.TABLE || state.busy || !before.currentPlayer.isBot || before.phase != TurnPhase.DRAW) return
        delay(800)
        if (state.game != before || state.screen != Screen.TABLE || state.busy) return
        transition { engine.playBotTurn(before) }
    }

    private fun transition(action: () -> GameState) {
        if (state.busy) return
        val previous = state.game
        state = state.copy(busy = true, message = null)
        viewModelScope.launch {
            try {
                    val game = withContext(Dispatchers.Default) { action() }
                    val switched = previous?.currentPlayerIndex != game.currentPlayerIndex || previous?.round != game.round
                    val analysis = withContext(Dispatchers.Default) { engine.analyzeHand(game) }
                    val save = runCatching { repository.save(game) }
                    state = state.copy(
                        game = game, screen = Screen.TABLE, selectedCardId = null, analysis = analysis,
                        busy = false, handRevealed = if (switched) false else state.handRevealed,
                        message = save.exceptionOrNull()?.let { "Your game is running, but saving failed. Keep the app open to continue." },
                    )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                state = state.copy(busy = false, message = error.message ?: "That move could not be completed.")
            }
        }
    }

    private fun refreshAnalysis() {
        val game = state.game ?: return
        viewModelScope.launch {
            val analysis = withContext(Dispatchers.Default) { engine.analyzeHand(game) }
            if (state.game == game) state = state.copy(analysis = analysis)
        }
    }
}
