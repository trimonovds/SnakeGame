package com.trimonovds.snakegame.shared

sealed class GameState {
    object GameOver: GameState()
    data class Playing(var cells: List<List<GameCell>>): GameState()
}