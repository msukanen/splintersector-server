package net.msukanen.splintersector_server

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "splintersector-server",
    ) {
        loginScreen()
    }
}