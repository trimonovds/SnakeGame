package com.trimonovds.snakegame.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GamePresenter(cellsInRow: Int = 10): GameViewDelegate {

    private val scope = MainScope()
    private val engine = GameEngine(GameSettings(Size(cellsInRow, cellsInRow)))
    private var view: GameView? = null
    private var job: Job? = null

    fun onAttach(view: GameView) {
        if (this.view != null) {
            throw Exception("Only one view can be attached at the moment")
        }
        this.view = view
        view.delegate = this
        restart(view)
    }

    fun onDetach() {
        job?.cancel()
        view?.delegate = null
        this.view = null
    }

    override fun onDidTapButton(button: GameViewDelegate.GameViewButton) {
        changeDirectionTaps.value = button.getDirection()
    }

    override fun onDidTapRestart() {
        view?.let { restart(it) }
    }

    private fun restart(view: GameView) {
        job?.cancel()
        changeDirectionTaps.value = null
        job = scope.launch(Dispatchers.Main) {
            engine.run(changeDirectionTaps).collect {
                view.render(it)
            }
        }
    }

    private val changeDirectionTaps = MutableStateFlow<Direction?>(null)

}

fun GameViewDelegate.GameViewButton.getDirection(): Direction {
    return when (this) {
        GameViewDelegate.GameViewButton.TOP -> Direction.TOP
        GameViewDelegate.GameViewButton.BOTTOM -> Direction.BOTTOM
        GameViewDelegate.GameViewButton.LEFT -> Direction.LEFT
        GameViewDelegate.GameViewButton.RIGHT -> Direction.RIGHT
    }
}