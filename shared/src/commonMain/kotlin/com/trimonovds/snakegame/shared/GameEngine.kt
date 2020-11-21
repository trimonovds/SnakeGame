package com.trimonovds.snakegame.shared

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class GameEngine(private val settings: GameSettings) {

    fun run(changeDirectionTaps: MutableStateFlow<Direction?>): Flow<GameState> = flow {
        emit(GameState.Playing(emptyList()))
        var finished = false
        val snakePoints = (0..4).map { Point(it, 0) }.reversed()
        val food = generateFood(snakePoints, settings.fieldSize)
        var state = SnakeState(snakePoints, Direction.RIGHT, food)
        while (!finished) {
            emit(GameState.Playing(mapStateToCells(state, settings.fieldSize)))
            delay(500L)
            val direction: Direction? = changeDirectionTaps.value
            state = state.nextState(direction, settings.fieldSize)
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
                if (snakePointsSet.contains(point)) {
                    GameCell.SNAKE
                } else {
                    if (state.food == point) GameCell.FOOD else GameCell.EMPTY
                }
            }
            cells.add(row)
        }
        return cells
    }
}

private fun generateFood(snakePoints: List<Point>, fieldSize: Size): Point {
    var points = mutableListOf<Point>()
    for (rowIndex in 0 until fieldSize.height) {
        for (columnIndex in 0 until fieldSize.width) {
            points.add(Point(columnIndex, rowIndex))
        }
    }
    val notSnakePoints = points.toSet().subtract(snakePoints.toSet()).toList()
    val randomPointIndex = Random.nextInt(notSnakePoints.size)
    return notSnakePoints[randomPointIndex]
}

private fun SnakeState.nextState(userDirection: Direction?, fieldSize: Size): SnakeState {
    val nextStateDirection: Direction = nextStateDirection(userDirection)
    val newFirstPoint = points.first().nextByDirection(nextStateDirection)
    val isFoodEaten = newFirstPoint == this.food

    val newPoints = points.toMutableList()
    newPoints.add(0, newFirstPoint)
    if (!isFoodEaten) {
        newPoints.removeLast()
    }

    val newOrSameFood: Point = if (isFoodEaten) {
        generateFood(newPoints, fieldSize)
    } else {
        this.food
    }

    return this.copy(points = newPoints, direction = nextStateDirection, food = newOrSameFood)
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