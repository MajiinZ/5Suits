package com.fivesuits.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        title = "5 Suits",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1200.dp, height = 860.dp),
    ) {
        App()
    }
}
