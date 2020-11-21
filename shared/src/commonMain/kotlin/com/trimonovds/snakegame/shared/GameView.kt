package com.trimonovds.snakegame.shared

interface GameView {
    var delegate: GameViewDelegate?
    fun render(state: GameState)
}