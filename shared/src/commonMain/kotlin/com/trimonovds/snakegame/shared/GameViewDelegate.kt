package com.trimonovds.snakegame.shared

interface GameViewDelegate {
    enum class GameViewButton {
        TOP, BOTTOM, LEFT, RIGHT
    }
    fun onDidTapButton(button: GameViewButton)
    fun onDidTapRestart()
}