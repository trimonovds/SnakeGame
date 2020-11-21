package com.trimonovds.snakegame.shared

import kotlinx.coroutines.*

class GameEngine(private val settings: GameSettings) {

    private var snakeState: SnakeState = SnakeState(arrayOf(Point(0, 0)), Direction.RIGHT)
    private var userDirection: Direction? = null

    // Array of field rows (size = gameSettings.height, row.size = gameSettings.width)
    private fun cells(state: SnakeState): Array<Array<GameCell>> {
        var gameCells = emptyArray<Array<GameCell>>()
        val snakePointsSet = state.points.toSet()
        for (rowIndex in 0 until settings.fieldSize.height) {
            val row = Array<GameCell>(settings.fieldSize.width) { columnIndex ->
                val point = Point(columnIndex, rowIndex)
                if (snakePointsSet.contains(point)) GameCell.SNAKE else GameCell.EMPTY
            }
            gameCells += row
        }
        return gameCells
    }

    suspend fun run() {
        println("Let the game begin!!!")
        var finished = false
        while (!finished) {
            delay(1000L)
            println("Snake: ${snakeState.points.joinToString(", ")}")
            val gameCells = cells(snakeState)
            debugPrint(gameCells)

            snakeState = snakeState.nextState(userDirection)
            finished = !snakeState.isValid(settings.fieldSize)
        }
        println("You lose")
    }
}

private fun debugPrint(gameCells: Array<Array<GameCell>>) {
    println("=== Game field ===")
    for (rowElement in gameCells.withIndex()) {
        println("Row: ${rowElement.index}: " + rowElement.value.joinToString(", "))
    }
}

private fun SnakeState.nextState(userDirection: Direction?): SnakeState {
    val nextStateDirection: Direction = nextStateDirection(userDirection)
    val newFirstPoint = points.first().nextByDirection(nextStateDirection)
    val newPoints = points.toMutableList()
    newPoints.add(0, newFirstPoint)
    newPoints.removeLast()
    return this.copy(points = newPoints.toTypedArray())
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