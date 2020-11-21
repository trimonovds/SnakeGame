package com.trimonovds.snakegame.shared

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*

interface GameViewDelegate {
    enum class GameViewButton {
        TOP, BOTTOM, LEFT, RIGHT
    }
    fun onDidTapButton(button: GameViewButton)
    fun onDidTapRestart()
}

interface GameView {
    var delegate: GameViewDelegate?
    fun render(state: GameState)
}

sealed class GameState {
    object GameOver: GameState()
    data class Playing(var cells: List<List<GameCell>>): GameState()
}

class GamePresenter(cellsInRow: Int = 10): GameViewDelegate {

    private val scope = MainScope()
    private val engine = GameEngine(GameSettings(Size(cellsInRow,cellsInRow)))
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

    fun onDettach() {
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

private fun GameViewDelegate.GameViewButton.getDirection(): Direction {
    return when (this) {
        GameViewDelegate.GameViewButton.TOP -> Direction.TOP
        GameViewDelegate.GameViewButton.BOTTOM -> Direction.BOTTOM
        GameViewDelegate.GameViewButton.LEFT -> Direction.LEFT
        GameViewDelegate.GameViewButton.RIGHT -> Direction.RIGHT
    }
}

class GameEngine(private val settings: GameSettings) {

    fun run(changeDirectionTaps: MutableStateFlow<Direction?>): Flow<GameState> = flow {
        emit(GameState.Playing(emptyList()))
        var finished = false
        var state = SnakeState(listOf(Point(4, 0), Point(3, 0), Point(2, 0), Point(1, 0), Point(0, 0)), Direction.RIGHT)
        while (!finished) {
            emit(GameState.Playing(mapStateToCells(state, settings.fieldSize)))
            delay(1000L)
            val direction: Direction? = changeDirectionTaps.value
            state = state.nextState(direction)
            finished = !state.isValid(settings.fieldSize)
        }
        emit(GameState.GameOver)
    }

    // Array of field rows (size = gameSettings.height, row.size = gameSettings.width)
    private fun mapStateToCells(state: SnakeState, fieldSize: Size): List<List<GameCell>> {
        var cells: MutableList<List<GameCell>> = mutableListOf()
        val snakePointsSet = state.points.toSet()
        for (rowIndex in 0 until fieldSize.height) {
            val row = List<GameCell>(fieldSize.width) { columnIndex ->
                val point = Point(columnIndex, rowIndex)
                if (snakePointsSet.contains(point)) GameCell.SNAKE else GameCell.EMPTY
            }
            cells.add(row)
        }
        return cells
    }
}

private fun SnakeState.nextState(userDirection: Direction?): SnakeState {
    val nextStateDirection: Direction = nextStateDirection(userDirection)
    val newFirstPoint = points.first().nextByDirection(nextStateDirection)
    val newPoints = points.toMutableList()
    newPoints.add(0, newFirstPoint)
    newPoints.removeLast()
    return this.copy(points = newPoints, direction = nextStateDirection)
}

private fun SnakeState.nextStateDirection(userDirection: Direction?): Direction {
    return if (userDirection != null) {
        if (userDirection.isOpposite(this.direction) || userDirection == direction) {
            this.direction
        } else {
            userDirection
        }
    } else {
        this.direction
    }
}

private fun Direction.isOpposite(direction: Direction): Boolean {
    return when (direction) {
        Direction.TOP -> this == Direction.BOTTOM
        Direction.BOTTOM -> this == Direction.TOP
        Direction.LEFT -> this == Direction.RIGHT
        Direction.RIGHT -> this == Direction.LEFT
    }
}

private fun SnakeState.isValid(fieldSize: Size): Boolean {
    val firstPoint = this.points.first()
    val isFirstPointInField =
        firstPoint.x >= 0 && firstPoint.x < fieldSize.width && firstPoint.y >= 0 && firstPoint.y < fieldSize.height
    val noIntersectionWithOwnPoints = this.points.toSet().size == this.points.size
    return isFirstPointInField && noIntersectionWithOwnPoints
}

private fun Point.nextByDirection(direction: Direction): Point {
    return when (direction) {
        Direction.TOP -> Point(x, y - 1)
        Direction.LEFT -> Point(x - 1, y)
        Direction.BOTTOM -> Point(x, y + 1)
        Direction.RIGHT -> Point(x + 1, y)
    }
}