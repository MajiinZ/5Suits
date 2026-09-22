package com.fivesuits.app.data.domain

import com.fivesuits.app.game.GameState

/** Repository boundary follows the same interface/implementation pattern as MzBotanicals. */
interface GameRepository {
    fun load(): GameState?
    fun save(game: GameState)
    fun darkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
