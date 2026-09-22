package com.fivesuits.app.data

import com.fivesuits.app.game.GameState
import com.fivesuits.app.data.domain.GameRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class SavedGame(val version: Int = 1, val game: GameState)

class GameRepositoryImpl(private val store: LocalStore = LocalStore()) : GameRepository {
    private val json = Json { ignoreUnknownKeys = true }
    override fun load(): GameState? = store.read("game.v1")?.let {
        val saved = json.decodeFromString<SavedGame>(it)
        require(saved.version == 1) { "This save was made by a newer app version." }
        saved.game
    }
    override fun save(game: GameState) = store.write("game.v1", json.encodeToString(SavedGame(game = game)))
    override fun darkTheme(): Boolean = store.read("darkTheme") == "true"
    override fun setDarkTheme(enabled: Boolean) = store.write("darkTheme", enabled.toString())
}
