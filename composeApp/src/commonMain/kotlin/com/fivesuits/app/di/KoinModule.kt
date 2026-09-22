package com.fivesuits.app.di

import com.fivesuits.app.data.GameRepositoryImpl
import com.fivesuits.app.data.LocalStore
import com.fivesuits.app.data.domain.GameRepository
import com.fivesuits.app.game.GameEngine
import com.fivesuits.app.presentation.GameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Same constructor-injection style as the reference's sharedModule. */
val sharedModule = module {
    single { LocalStore() }
    single<GameRepository> { GameRepositoryImpl(get()) }
    single { GameEngine() }
    viewModelOf(::GameViewModel)
}
